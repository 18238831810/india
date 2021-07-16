package com.cf.crs.controller.admin;


import com.cf.crs.common.entity.PagingBase;
import com.cf.crs.common.utils.Result;
import com.cf.crs.entity.CashinRebateDto;
import com.cf.crs.entity.CashinRebateEntity;
import com.cf.crs.entity.OrderCashinDto;
import com.cf.crs.entity.OrderCashinEntity;
import com.cf.crs.service.CashinRebateService;
import com.cf.crs.service.OrderCashinService;
import com.cf.util.http.ResultJson;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Slf4j
@Api(tags="充值")
@RestController
@RequestMapping("/admin/cashinRebate")
public class AdminCashinRebateController {

    @Autowired
    CashinRebateService cashinRebateService;


    @PostMapping("/queryList")
    public Result<PagingBase<CashinRebateEntity>> list(CashinRebateDto dto){
        PagingBase<CashinRebateEntity> pagingBase = cashinRebateService.queryList(dto);
        return new Result<PagingBase<CashinRebateEntity>>().ok(pagingBase);
    }


    @GetMapping("/export")
    public void export(CashinRebateDto dto) {
        cashinRebateService.export(dto);
    }

}
