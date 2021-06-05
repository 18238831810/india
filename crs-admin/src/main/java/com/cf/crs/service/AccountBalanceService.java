package com.cf.crs.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cf.crs.entity.AccountBalanceEntity;
import com.cf.crs.mapper.AccountBalanceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 用户余额表
 */
@Slf4j
@Service
public class AccountBalanceService {

    @Autowired
    AccountBalanceMapper accountBalanceMapper;


    public Integer updateBalance(AccountBalanceEntity accountBalanceEntity) {
       return accountBalanceMapper.updateBalance(accountBalanceEntity);
    }

    public AccountBalanceEntity getAccountBalanceByUId(long uid)
    {
        return accountBalanceMapper.selectOne(new QueryWrapper<AccountBalanceEntity>().eq("uid",uid));
    }

}
