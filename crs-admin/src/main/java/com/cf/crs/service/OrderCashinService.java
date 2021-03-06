package com.cf.crs.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cf.crs.common.entity.PagingBase;
import com.cf.crs.common.exception.RenException;
import com.cf.crs.common.utils.BeanMapUtils;
import com.cf.crs.entity.*;
import com.cf.crs.mapper.OrderCashinMapper;
import com.cf.crs.properties.OrderCallbackParam;
import com.cf.crs.properties.OrderConfigProperties;
import com.cf.crs.properties.OrderParam;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import com.cf.util.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * 存款
 * @author frank
 * @date 2021-06-06
 */
@Slf4j
@Service
public class OrderCashinService extends ServiceImpl<OrderCashinMapper, OrderCashinEntity> implements IService<OrderCashinEntity> {


    @Autowired
    OrderConfigProperties orderConfigProperties;

    @Autowired
    HttpServletRequest request;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    AccountBalanceService accountBalanceService;

    @Autowired
    FinancialDetailsService financialDetailsService;

    @Autowired
    HttpServletResponse response;

    @Autowired
    SendRedisMessage sendRedisMessage;


    /**
     * 统计存款总额
     *
     * @param dto
     * @return
     */
    public ResultJson<BigDecimal> queryTotalAmount(OrderCashinDto dto) {
        QueryWrapper<OrderCashinEntity> queryWrapper = getQueryWrapper(dto);
        queryWrapper.select(" sum(real_amount) as real_amount");
        OrderCashinEntity orderCashinEntity = baseMapper.selectOne(queryWrapper);
        return HttpWebResult.getMonoSucResult(orderCashinEntity.getRealAmount());
    }


    public void export(OrderCashinDto dto) {
        try {
            QueryWrapper<OrderCashinEntity> queryWrapper = getQueryWrapper(dto);
            List<OrderCashinEntity> list = baseMapper.selectList(queryWrapper);
            ExcelUtils.exportExcelWithDict(response, null, list, OrderCashinEntity.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 统计存款总额
     *
     * @param dto
     * @return
     */
    public void sendCashinRebate(OrderCashinDto dto) {
        sendRedisMessage.send(DataChange.obToString(dto.getUid()),Const.CASHIN_TAG);
    }

    /**
     * 查询用户的充值列表
     *
     * @param dto
     * @return
     */
    public PagingBase<OrderCashinEntity> queryList(OrderCashinDto dto) {
        Page<OrderCashinEntity> iPage = new Page(dto.getPageNum(), dto.getPageSize());
        QueryWrapper<OrderCashinEntity> queryWrapper = getQueryWrapper(dto);
        queryWrapper.orderByDesc("order_time");
        IPage<OrderCashinEntity> pageList = this.page(iPage, queryWrapper);
        List<OrderCashinEntity> records = pageList.getRecords();
        records.forEach(record->{
            if (StringUtils.isEmpty(record.getOrderSn())) {
                String orderSn = new StringBuilder("T").append(DateUtil.timesToDate(record.getOrderTime(), DateUtil.DEFAULT)).append("G").append(record.getId()).toString();
                record.setOrderSn(orderSn);
            }
        });
        return new PagingBase<OrderCashinEntity>(records, pageList.getTotal());
    }

    private QueryWrapper<OrderCashinEntity> getQueryWrapper(OrderCashinDto dto) {
        return new QueryWrapper<OrderCashinEntity>().eq(dto.getUid() != null, "uid", dto.getUid())
                    .ge(dto.getStartTime() != null, "order_time", dto.getStartTime())
                    .le(dto.getEndTime() != null, "order_time", dto.getEndTime()).
                            eq(dto.getStatus() != null, "status", dto.getStatus());
    }

    /**
     * 查询用户的资金明细列表
     *
     * @param dto
     * @return
     */
    public PagingBase<OrderCashinEntity> list(OrderCashinDto dto) {
        Page<OrderCashinEntity> iPage = new Page(dto.getPageNum(), dto.getPageSize());
        IPage<OrderCashinEntity> pageList = this.page(iPage, new QueryWrapper<OrderCashinEntity>().eq("uid", dto.getUid())
                .ge(dto.getStartTime() != null,"order_time",dto.getStartTime())
                .le(dto.getEndTime() != null,"order_time",dto.getEndTime()).orderByDesc("order_time"));
        List<OrderCashinEntity> records = pageList.getRecords();
        records.forEach(record->{
            if (StringUtils.isEmpty(record.getOrderSn())) {
                String orderSn = new StringBuilder("T").append(DateUtil.timesToDate(record.getOrderTime(), DateUtil.DEFAULT)).append("G").append(record.getId()).toString();
                record.setOrderSn(orderSn);
            }
        });
        return new PagingBase<OrderCashinEntity>(records, pageList.getTotal());
    }

    /**
     * 代收款下单接口
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultJson<String> order(OrderCashinParam orderCashinParam){
        if (orderCashinParam.getAmount() == null) return HttpWebResult.getMonoError("请输入存款金额");

        long now = System.currentTimeMillis();
        //获取订单数据
        OrderCashinEntity orderCashinEntity = getOrderCashinEntity(orderCashinParam, now);
        baseMapper.insert(orderCashinEntity);
        //第三方存款
        JSONObject result = goCashin(now, orderCashinEntity);
        if (result != null && result.getInteger("code") == 1) return HttpWebResult.getMonoSucResult(result.getJSONObject("data").getString("pay_pageurl"));
        throw new RenException(result.getString("msg"));
    }

    /**
     * 第三方存款
     * @param now
     * @param orderCashinEntity
     * @return
     */
    private JSONObject goCashin(long now, OrderCashinEntity orderCashinEntity) {
        //T+时间+G+id 作为存款订单唯一标识
        String orderSn = new StringBuilder("T").append(DateUtil.timesToDate(now,DateUtil.DEFAULT)).append("G").append(orderCashinEntity.getId()).toString();
        orderCashinEntity.setOrderSn(orderSn);
        //组装代收款参数
        OrderParam orderParam = getOrderParam(orderCashinEntity);
        LinkedMultiValueMap linkedMultiValueMap = getLinkedMultiValueMap(orderParam);
        String sign = OrderSignUtil.createSign(orderConfigProperties.getApiToken(), JSON.parseObject(JSON.toJSONString(orderParam), Map.class));
        linkedMultiValueMap.add("sign",sign);
        log.info("order cashin param:{}",JSON.toJSONString(linkedMultiValueMap));
        JSONObject result = restTemplate.postForObject(orderConfigProperties.getOrderUrl(), linkedMultiValueMap, JSONObject.class);
        log.info("order cashin result:{}",JSON.toJSONString(result));
        return result;
    }

    private OrderParam getOrderParam(OrderCashinEntity orderCashinEntity) {
        OrderParam orderParam = new OrderParam();
        orderParam.setMerch_id(orderConfigProperties.getMerchId());
        orderParam.setAmount(orderCashinEntity.getAmount());
        orderParam.setGoods_info(orderCashinEntity.getGoodsInfo());
        orderParam.setIp(orderCashinEntity.getIp());
        orderParam.setOrder_sn(orderCashinEntity.getOrderSn());
        orderParam.setPayer_account(orderCashinEntity.getPayerAccount());
        orderParam.setPayment_id(orderCashinEntity.getPaymentId());
        orderParam.setNotify_url(orderConfigProperties.getOrderCallbackUrl());
        return orderParam;
    }

    private static LinkedMultiValueMap getLinkedMultiValueMap(OrderParam orderParam){
        LinkedMultiValueMap linkedMultiValueMap = new LinkedMultiValueMap();
        linkedMultiValueMap.add("merch_id",orderParam.getMerch_id());
        linkedMultiValueMap.add("payment_id",orderParam.getPayment_id());
        linkedMultiValueMap.add("order_sn",orderParam.getOrder_sn());
        linkedMultiValueMap.add("amount",orderParam.getAmount());
        linkedMultiValueMap.add("payer_account",orderParam.getPayer_account());
        linkedMultiValueMap.add("goods_info",orderParam.getGoods_info());
        linkedMultiValueMap.add("ip",orderParam.getIp());
        linkedMultiValueMap.add("notify_url",orderParam.getNotify_url());
        return linkedMultiValueMap;
    }


    /**
     * 组装存款订单数据
     * @param orderCashinParam
     * @param now
     * @return
     */
    private OrderCashinEntity getOrderCashinEntity(OrderCashinParam orderCashinParam, long now) {
        OrderCashinEntity orderCashinEntity = BeanMapUtils.map(orderCashinParam, OrderCashinEntity.class);
        orderCashinEntity.setOrderTime(now);
        orderCashinEntity.setStatus(0);
        orderCashinEntity.setIp(WebTools.getIpAddr(request));
        return orderCashinEntity;
    }

    /**
     * 回调
     * @param callbackParamm
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String orderCallback(OrderCallbackParam callbackParamm){
        if (callbackParamm.getStatus() == 1){
            //支付成功
            String order_sn = callbackParamm.getOrder_sn();
            //解析出存款记录id
            String id = order_sn.split("G")[1];
            OrderCashinEntity orderCashinEntity = baseMapper.selectById(id);
            if (orderCashinEntity == null) {
                log.info("order cashin id error:{}",id);
                return "success";
            }
            //更新存款记录
            int resutl = updateOrderCashin(callbackParamm, orderCashinEntity);
            if (resutl == 0) return "success";
            //更新用户余额
            updateAccountBalance(orderCashinEntity);
            //新增资金明细记录
            addFinancialDetails(callbackParamm, orderCashinEntity);
            try {
                //发送充值成功到消息队列，处理注册充值推广奖励
                sendRedisMessage.send(DataChange.obToString(orderCashinEntity.getUid()), Const.CASHIN_TAG);
            } catch (Exception e) {
                log.error(e.getMessage(),e);
            }
        }
        return "success";
    }

    /**
     * 新增资金明细记录
     * @param callbackParamm
     * @param orderCashinEntity
     */
    private void addFinancialDetails(OrderCallbackParam callbackParamm, OrderCashinEntity orderCashinEntity) {
        //AccountBalanceEntity accountBalanceEntity = accountBalanceService.getAccountBalanceByUId(orderCashinEntity.getUid());
        FinancialDetailsEntity financialDetailsEntity = FinancialDetailsEntity.builder().amount(new BigDecimal(Float.toString(callbackParamm.getAmount()))).
                orderSn(callbackParamm.getOrder_sn()).type(1).uid(orderCashinEntity.getUid()).orderTime(callbackParamm.getTime() * 1000).build();
        financialDetailsService.save(financialDetailsEntity);
    }

    /**
     * 更新用户余额
     * @param orderCashinEntity
     */
    private void updateAccountBalance(OrderCashinEntity orderCashinEntity) {
        AccountBalanceEntity accountBalanceEntity = new AccountBalanceEntity();
        accountBalanceEntity.setAmount(new BigDecimal(Float.toString(orderCashinEntity.getRealAmount())));
        accountBalanceEntity.setUid(orderCashinEntity.getUid());
        accountBalanceEntity.setUpdateTime(System.currentTimeMillis());
        accountBalanceService.updateBalance(accountBalanceEntity);
    }

    /**
     * 更新存款记录
     * @param orderCashinEntity
     */
    private int updateOrderCashin(OrderCallbackParam callbackParamm, OrderCashinEntity orderCashinEntity) {
        orderCashinEntity.setOrderSn(callbackParamm.getOrder_sn());
        orderCashinEntity.setPtOrderSn(callbackParamm.getPt_order_sn());
        orderCashinEntity.setRealAmount(callbackParamm.getAmount());
        orderCashinEntity.setDealTime(callbackParamm.getTime()*1000);
        orderCashinEntity.setStatus(2);
        return baseMapper.update(orderCashinEntity,new UpdateWrapper<OrderCashinEntity>().ne("status",2).eq("id",orderCashinEntity.getId()));
    }


}
