package com.cf.crs.controller.admin;


import com.cf.crs.common.entity.PagingBase;
import com.cf.crs.common.utils.Result;
import com.cf.crs.entity.AccountDto;
import com.cf.crs.entity.AccountEntity;
import com.cf.crs.service.AccountService;
import com.cf.util.http.ResultJson;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags="用户和主播列表")
@RestController
@RequestMapping("/admin/account")
public class AdminAccountController {

    @Autowired
    AccountService accountService;


    @PostMapping("/queryList")
    public Result<PagingBase<AccountEntity>> list(AccountDto dto){
        PagingBase<AccountEntity> pagingBase = accountService.queryList(dto);
        return new Result<PagingBase<AccountEntity>>().ok(pagingBase);
    }

    @PostMapping("/updateRecordStatus")
    public ResultJson<String> updateRecordStatus(AccountDto dto){
        return accountService.updateRecordStatus(dto);
    }

}
