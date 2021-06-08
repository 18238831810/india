package com.cf.crs.controller;


import com.cf.crs.common.entity.PagingBase;
import com.cf.crs.common.utils.Result;
import com.cf.crs.entity.FinancialDetailsDto;
import com.cf.crs.entity.FinancialDetailsEntity;
import com.cf.crs.service.FinancialDetailsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags="资金明细")
@RestController
@RequestMapping("/api/financialDetails")
public class FinancialDetailsController {

    @Autowired
    FinancialDetailsService financialDetailsService;

    @GetMapping("/list")
    @ApiOperation("资金明细列表查询")
    public Result<PagingBase> list(FinancialDetailsDto dto){
        PagingBase<FinancialDetailsEntity> pagingBase = financialDetailsService.list(dto);
        return new Result<PagingBase>().ok(pagingBase);
    }

}
