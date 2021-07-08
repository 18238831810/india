package com.cf.crs.controller.admin;


import com.cf.crs.common.entity.PagingBase;
import com.cf.crs.common.utils.Result;
import com.cf.crs.entity.AccountBalanceDto;
import com.cf.crs.entity.AccountBalanceEntity;
import com.cf.crs.service.AccountBalanceService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags="用户余额")
@RestController
@RequestMapping("/admin/balance")
public class AdminAccountBalenceController {

    @Autowired
    AccountBalanceService accountBalanceService;


    @PostMapping("/queryList")
    public Result<PagingBase<AccountBalanceEntity>> list(AccountBalanceDto dto){
        PagingBase<AccountBalanceEntity> pagingBase = accountBalanceService.queryList(dto);
        return new Result<PagingBase<AccountBalanceEntity>>().ok(pagingBase);
    }

    @GetMapping("/export")
    public void export(AccountBalanceDto dto) {
        accountBalanceService.export(dto);
    }

}
