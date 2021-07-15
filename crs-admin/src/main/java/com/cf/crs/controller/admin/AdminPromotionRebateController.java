package com.cf.crs.controller.admin;


import com.cf.crs.common.entity.PagingBase;
import com.cf.crs.common.utils.Result;
import com.cf.crs.entity.AccountDto;
import com.cf.crs.entity.AccountEntity;
import com.cf.crs.entity.PromotionRebateEntity;
import com.cf.crs.service.AccountService;
import com.cf.crs.service.PromotionRebateService;
import com.cf.util.http.ResultJson;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 推广返利规则设置
 * @author frank
 * @date 2021-07-15
 */
@Slf4j
@Api(tags="推广返利规则设置")
@RestController
@RequestMapping("/admin/promotionRebate")
public class AdminPromotionRebateController {

    @Autowired
    PromotionRebateService promotionRebateService;


    @PostMapping("/queryList")
    public ResultJson<PromotionRebateEntity> queryList(){
        return promotionRebateService.queryList();
    }

    @PostMapping("/updateSetting")
    public ResultJson<String> updateSetting(PromotionRebateEntity promotionRebateEntity){
        return promotionRebateService.updateSetting(promotionRebateEntity);
    }


}
