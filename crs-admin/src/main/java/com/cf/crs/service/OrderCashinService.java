package com.cf.crs.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cf.crs.common.exception.RenException;
import com.cf.crs.entity.OrderCashinDto;
import com.cf.crs.entity.OrderCashinEntity;
import com.cf.crs.mapper.OrderCashinMapper;
import com.cf.crs.properties.*;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import com.cf.util.utils.DateUtil;
import com.cf.util.utils.OrderSignUtil;
import com.cf.util.utils.WebTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@Service
public class OrderCashinService {

    @Autowired
    OrderCashinMapper orderCashinMapper;

    @Autowired
    OrderConfigProperties orderConfigProperties;

    @Autowired
    HttpServletRequest request;

    @Autowired
    RestTemplate restTemplate;


    /**
     * 代收款下单接口
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultJson<String> order(OrderCashinDto orderCashinDto){
        long now = System.currentTimeMillis();
        //获取订单数据
        OrderCashinEntity orderCashinEntity = getOrderCashinEntity(orderCashinDto, now);
        orderCashinMapper.insert(orderCashinEntity);

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

        if (result != null && result.getInteger("code") == 1) return HttpWebResult.getMonoSucResult(result.getJSONObject("data").getString("pay_pageurl"));

        throw new RenException(result.getString("msg"));

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

    public static void main(String[] args) {
        OrderParam orderParam = new OrderParam();
        orderParam.setIp("dfd");
        LinkedMultiValueMap<String,Object> linkedMultiValueMap = getLinkedMultiValueMap(orderParam);
        System.out.println(JSON.toJSONString(linkedMultiValueMap));
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
     * @param orderCashinDto
     * @param now
     * @return
     */
    private OrderCashinEntity getOrderCashinEntity(OrderCashinDto orderCashinDto, long now) {
        OrderCashinEntity orderCashinEntity = new OrderCashinEntity();
        orderCashinEntity.setAmount(orderCashinDto.getAmount());
        orderCashinEntity.setGoodsInfo(orderCashinDto.getGoodsInfo());
        orderCashinEntity.setPaymentId(orderCashinDto.getPaymentId());
        orderCashinEntity.setPayerAccount(orderCashinDto.getPayerAccount());
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
    public String orderCallback(OrderCallbackParam callbackParamm){
        log.info("order cashin callback:{}",JSON.toJSONString(callbackParamm));
        if (callbackParamm.getStatus() == 1){
            //支付成功
            String order_sn = callbackParamm.getOrder_sn();
            String[] order = order_sn.split("G");
            //更新存款订单数据
            orderCashinMapper.update(null,new UpdateWrapper<OrderCashinEntity>().eq("id",order[1]).set("order_sn",callbackParamm.getOrder_sn()).set("pt_irder_sn",callbackParamm.getPt_order_sn()).
                    set("real_amount",callbackParamm.getAmount()).set("deal_time",callbackParamm.getTime()*1000).set("status",2));
        }
        return "success";
    }


}
