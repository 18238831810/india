package com.cf.crs.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.binance.api.client.constant.OrderErrorEnum;
import com.cf.crs.common.exception.RenException;
import com.cf.crs.entity.AccountBalanceEntity;
import com.cf.crs.entity.OrderEntity;
import com.cf.crs.mapper.AccountBalanceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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

    public AccountBalanceEntity getAccountBalanceByUId(long uid) {
        return accountBalanceMapper.selectOne(new QueryWrapper<AccountBalanceEntity>().eq("uid", uid));
    }

    /**
     * 从用户账户扣钱
     *
     * @param orderEntity
     * @return
     * @throws Exception
     */
    public OrderErrorEnum updateAmountFromOrder(OrderEntity orderEntity) {
        int row = this.updateBalanceForCashout(AccountBalanceEntity.builder()
                .amount((float) -orderEntity.getPayment())
                .updateTime(System.currentTimeMillis())
                .uid(orderEntity.getUid()).build());
        if (row <= 0)
            return OrderErrorEnum.ERROR_NOT_ENOUGH;
        return null;
    }

    /**
     * 提现更新余额
     *
     * @param accountBalanceEntity
     * @return
     */
    public int updateBalanceForCashout(AccountBalanceEntity accountBalanceEntity) {
        return accountBalanceMapper.updateForCashin(accountBalanceEntity);
    }

}
