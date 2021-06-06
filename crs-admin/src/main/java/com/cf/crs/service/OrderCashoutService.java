package com.cf.crs.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cf.crs.common.utils.BeanMapUtils;
import com.cf.crs.entity.AccountBalanceEntity;
import com.cf.crs.entity.OrderCashoutDto;
import com.cf.crs.entity.OrderCashoutEntity;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 提现
 * @author frank
 * @date 2021-06-06
 */
@Slf4j
@Service
public class OrderCashoutService {

    @Autowired
    OrderCashoutMapper orderCashoutMapper;

    @Autowired
    OrderConfigProperties orderConfigProperties;

    @Autowired
    HttpServletRequest request;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    AccountBalanceService accountBalanceService;


    /**
     * 代收款下单接口
     * @return
     */
    public ResultJson<String> order(OrderCashoutDto orderCashoutDto){
        OrderCashoutEntity orderCashoutEntity = getOrderCashinEntity(orderCashoutDto);
        //保存订单数据
        orderCashoutMapper.insert(orderCashoutEntity);
        return HttpWebResult.getMonoSucStr();
    }

    /**
     * 提现数据审批
     * @return
     */
    public ResultJson<String> approve(Long id){
        OrderCashoutEntity orderCashoutEntity = orderCashoutMapper.selectById(id);
        String orderSn = new StringBuilder("T").append(DateUtil.timesToDate(orderCashoutEntity.getOrderTime(),DateUtil.DEFAULT)).append("G").append(orderCashoutEntity.getId()).toString();
        orderCashoutEntity.setOrderSn(orderSn);
        CollectionParam collectionParam = getCollectionParam(orderCashoutEntity);
        LinkedMultiValueMap linkedMultiValueMap = getLinkedMultiValueMap(collectionParam);
        String sign = OrderSignUtil.createSign(orderConfigProperties.getApiToken(), JSON.parseObject(JSON.toJSONString(collectionParam), Map.class));
        linkedMultiValueMap.add("sign",sign);
        log.info("order cashout param:{}",JSON.toJSONString(linkedMultiValueMap));
        JSONObject result = restTemplate.postForObject(orderConfigProperties.getCollectionUrl(), linkedMultiValueMap, JSONObject.class);
        log.info("order cashout result:{}",JSON.toJSONString(result));
        if (result != null && result.getInteger("code") == 1) return HttpWebResult.getMonoSucStr();
        return HttpWebResult.getMonoError("fail");
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
     * 组装存款订单数据
     * @param orderCashoutDto
     * @return
     */
    private OrderCashoutEntity getOrderCashinEntity(OrderCashoutDto orderCashoutDto) {
        OrderCashoutEntity orderCashoutEntity = BeanMapUtils.map(orderCashoutDto, OrderCashoutEntity.class);
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
            OrderCashoutEntity orderCashoutEntity = orderCashoutMapper.selectById(id);
            if (orderCashoutEntity == null) {
                log.info("order cashout id error:{}",id);
                return "success";
            }
            //更新存款记录
            updateOrderCashin(callbackParamm, orderCashoutEntity);
            //更新用余额
            updateAccountBalance(orderCashoutEntity);
        }
        return "success";
    }

    private void updateAccountBalance(OrderCashoutEntity orderCashoutEntity) {
        AccountBalanceEntity accountBalanceEntity = new AccountBalanceEntity();
        accountBalanceEntity.setAmount(-orderCashoutEntity.getRealAmount());
        accountBalanceEntity.setUid(orderCashoutEntity.getUid());
        accountBalanceEntity.setUpdateTime(System.currentTimeMillis());
        accountBalanceService.updateBalance(accountBalanceEntity);
    }

    private void updateOrderCashin(CollectionCallbackParam callbackParamm, OrderCashoutEntity orderCashoutEntity) {
        orderCashoutEntity.setOrderSn(callbackParamm.getOrder_sn());
        orderCashoutEntity.setPtOrderSn(callbackParamm.getPt_order_sn());
        orderCashoutEntity.setRealAmount(callbackParamm.getAmount());
        orderCashoutEntity.setDealTime(callbackParamm.getTime()*1000);
        orderCashoutEntity.setStatus(2);
        orderCashoutMapper.updateById(orderCashoutEntity);
    }


}
