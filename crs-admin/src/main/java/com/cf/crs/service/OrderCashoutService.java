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
import com.cf.crs.mapper.OrderCashoutMapper;
import com.cf.crs.properties.CollectionCallbackParam;
import com.cf.crs.properties.CollectionParam;
import com.cf.crs.properties.OrderConfigProperties;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import com.cf.util.utils.DateUtil;
import com.cf.util.utils.OrderSignUtil;
import com.cf.util.utils.WebTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 提现
 * @author frank
 * @date 2021-06-06
 */
@Slf4j
@Service
public class OrderCashoutService extends ServiceImpl<OrderCashoutMapper, OrderCashoutEntity> implements IService<OrderCashoutEntity> {


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



    /**
     * 查询用户的资金明细列表
     *
     * @param dto
     * @return
     */
    public PagingBase<OrderCashoutEntity> list(OrderCashoutDto dto) {
        Page<OrderCashoutEntity> iPage = new Page(dto.getPageNum(), dto.getPageSize());
        IPage<OrderCashoutEntity> pageList = this.page(iPage, new QueryWrapper<OrderCashoutEntity>().eq("uid", dto.getUid())
                .ge(dto.getStartTime() != null,"order_time",dto.getStartTime())
                .le(dto.getEndTime() != null,"order_time",dto.getEndTime()).orderByDesc("order_time"));
        List<OrderCashoutEntity> records = pageList.getRecords();
        records.forEach(record->{
            if (StringUtils.isEmpty(record.getOrderSn())) {
                String orderSn = new StringBuilder("T").append(DateUtil.timesToDate(record.getOrderTime(), DateUtil.DEFAULT)).append("G").append(record.getId()).toString();
                record.setOrderSn(orderSn);
            }
        });
        return new PagingBase<OrderCashoutEntity>(records, pageList.getTotal());
    }

    /**
     * 代收款下单接口
     * @return
     */
    public ResultJson<String> order(OrderCashoutParam orderCashoutParam){
        OrderCashoutEntity orderCashoutEntity = getOrderCashinEntity(orderCashoutParam);
        //保存订单数据
        baseMapper.insert(orderCashoutEntity);
        return HttpWebResult.getMonoSucStr();
    }

    /**
     * 提现数据审批
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultJson<String> approve(Long id){
        //更新审批状态
        int update = baseMapper.update(null, new UpdateWrapper<OrderCashoutEntity>().eq("id", id).eq("approve_status", 0).set("approve_status", 1));
        if (update == 0) throw new RenException("此提案不存在或者已审批");
        OrderCashoutEntity orderCashoutEntity = baseMapper.selectById(id);
        //更改余额
        int i = updateAcountBalanceForCashout(orderCashoutEntity);
        if (i == 0) throw new RenException("用户提款金额不足,审批失败");
        //获取组装订单号
        String orderSn = new StringBuilder("T").append(DateUtil.timesToDate(orderCashoutEntity.getOrderTime(),DateUtil.DEFAULT)).append("G").append(orderCashoutEntity.getId()).toString();
        orderCashoutEntity.setOrderSn(orderSn);
        //添加资金明细
        //addFinancialDetails(orderCashoutEntity);
        //第三方提现
        JSONObject result = goCashout(orderCashoutEntity);
        if (result == null) throw new RenException("提现失败");
        if (result.getInteger("code") == 1) return HttpWebResult.getMonoSucStr();
        throw new RenException(result.getString("msg"));
    }

    /**
     * 第三方提现
     * @param orderCashoutEntity
     * @return
     */
    private JSONObject goCashout(OrderCashoutEntity orderCashoutEntity) {
        CollectionParam collectionParam = getCollectionParam(orderCashoutEntity);
        LinkedMultiValueMap linkedMultiValueMap = getLinkedMultiValueMap(collectionParam);
        String sign = OrderSignUtil.createSign(orderConfigProperties.getApiToken(), JSON.parseObject(JSON.toJSONString(collectionParam), Map.class));
        linkedMultiValueMap.add("sign",sign);
        log.info("order cashout param:{}",JSON.toJSONString(linkedMultiValueMap));
        //提现
        JSONObject result = restTemplate.postForObject(orderConfigProperties.getCollectionUrl(), linkedMultiValueMap, JSONObject.class);
        log.info("order cashout result:{}",JSON.toJSONString(result));
        return result;
    }

    /**
     * 新增资金明细
     * @param orderCashoutEntity
     */
    private void addFinancialDetails(OrderCashoutEntity orderCashoutEntity) {
        AccountBalanceEntity accountBalanceEntity = accountBalanceService.getAccountBalanceByUId(orderCashoutEntity.getUid());
        FinancialDetailsEntity financialDetailsEntity = FinancialDetailsEntity.builder().orderTime(System.currentTimeMillis()).orderSn(orderCashoutEntity.getOrderSn()).uid(orderCashoutEntity.getUid()).type(2).
                balance(accountBalanceEntity.getAmount()).amount(-orderCashoutEntity.getAmount()).build();
        financialDetailsService.save(financialDetailsEntity);
    }

    private static LinkedMultiValueMap getLinkedMultiValueMap(CollectionParam collectionParam){
        LinkedMultiValueMap linkedMultiValueMap = new LinkedMultiValueMap();
        linkedMultiValueMap.add("merch_id",collectionParam.getMerch_id());
        linkedMultiValueMap.add("payment_id",collectionParam.getPayment_id());
        linkedMultiValueMap.add("order_sn",collectionParam.getOrder_sn());
        linkedMultiValueMap.add("amount",collectionParam.getAmount());
        linkedMultiValueMap.add("payer_account",collectionParam.getPayer_account());
        linkedMultiValueMap.add("payer_ifsc",collectionParam.getPayer_ifsc());
        linkedMultiValueMap.add("payer_mobile",collectionParam.getPayer_mobile());
        linkedMultiValueMap.add("payer_name",collectionParam.getPayer_name());
        linkedMultiValueMap.add("notify_url",collectionParam.getNotify_url());
        return linkedMultiValueMap;
    }


    /**
     * 提案更新用户余额
     * @param orderCashoutEntity
     * @return
     */
    private int updateAcountBalanceForCashout(OrderCashoutEntity orderCashoutEntity) {
        AccountBalanceEntity accountBalanceEntity = new AccountBalanceEntity();
        accountBalanceEntity.setUid(orderCashoutEntity.getUid());
        accountBalanceEntity.setAmount(orderCashoutEntity.getAmount());
        accountBalanceEntity.setUpdateTime(System.currentTimeMillis());
        return accountBalanceService.updateBalance(accountBalanceEntity);
    }

    private CollectionParam getCollectionParam(OrderCashoutEntity orderCashoutEntity) {
        CollectionParam collectionParam = new CollectionParam();
        collectionParam.setMerch_id(orderConfigProperties.getMerchId());
        collectionParam.setAmount(orderCashoutEntity.getAmount());
        collectionParam.setOrder_sn(orderCashoutEntity.getOrderSn());
        collectionParam.setPayer_account(orderCashoutEntity.getPayerAccount());
        collectionParam.setPayment_id(orderCashoutEntity.getPaymentId());
        collectionParam.setPayer_ifsc(orderCashoutEntity.getPayerIfsc());
        collectionParam.setPayer_mobile(orderCashoutEntity.getPayerMobile());
        collectionParam.setPayer_name(orderCashoutEntity.getPayerName());
        collectionParam.setNotify_url(orderConfigProperties.getCollectionCallbackUrl());
        return collectionParam;
    }


    /**
     * 组装存款订单数据
     * @param orderCashoutParam
     * @return
     */
    private OrderCashoutEntity getOrderCashinEntity(OrderCashoutParam orderCashoutParam) {
        OrderCashoutEntity orderCashoutEntity = BeanMapUtils.map(orderCashoutParam, OrderCashoutEntity.class);
        orderCashoutEntity.setOrderTime(System.currentTimeMillis());
        orderCashoutEntity.setStatus(0);
        orderCashoutEntity.setIp(WebTools.getIpAddr(request));
        return orderCashoutEntity;
    }

    /**
     * 回调
     * @param callbackParamm
     * @return
     */
    public String orderCallback(CollectionCallbackParam callbackParamm){
        if (callbackParamm.getStatus() == 2){
            //支付成功
            String order_sn = callbackParamm.getOrder_sn();
            //解析出存款记录id
            String id = order_sn.split("G")[1];
            OrderCashoutEntity orderCashoutEntity = baseMapper.selectById(id);
            if (orderCashoutEntity == null) {
                log.info("order cashout id error:{}",id);
                return "success";
            }
            //更新存款记录
            int result = updateOrderCashin(callbackParamm, orderCashoutEntity);
            if (result == 0) return "success";
            //改为审批时更新余额，此处只更新提现状态
            //updateAccountBalance(orderCashoutEntity);
        }
        return "success";
    }


    private int updateOrderCashin(CollectionCallbackParam callbackParamm, OrderCashoutEntity orderCashoutEntity) {
        orderCashoutEntity.setOrderSn(callbackParamm.getOrder_sn());
        orderCashoutEntity.setPtOrderSn(callbackParamm.getPt_order_sn());
        orderCashoutEntity.setRealAmount(callbackParamm.getAmount());
        orderCashoutEntity.setDealTime(callbackParamm.getTime()*1000);
        orderCashoutEntity.setStatus(2);
        return baseMapper.update(orderCashoutEntity,new UpdateWrapper<OrderCashoutEntity>().ne("status",2).eq("id",orderCashoutEntity.getId()));
    }


}
