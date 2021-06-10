package com.cf.crs.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cf.crs.common.constant.OrderErrorEnum;
import com.cf.crs.entity.AccountBalanceEntity;
import com.cf.crs.entity.OrderEntity;
import com.cf.crs.mapper.AccountBalanceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * 用户余额表
 */
@Slf4j
@Service
public class AccountBalanceService extends ServiceImpl<AccountBalanceMapper, AccountBalanceEntity> implements IService<AccountBalanceEntity> {


    /**
     * 用户余额变动统一入口
     * @param accountBalanceEntity
     * @return
     */
    public Integer updateBalance(AccountBalanceEntity accountBalanceEntity) {
        if (accountBalanceEntity.getAmount() == null) return 0;
        if (accountBalanceEntity.getAmount() < 0) return baseMapper.updateSubBalance(accountBalanceEntity);
        else return baseMapper.updateAddBalance(accountBalanceEntity);
    }

    public AccountBalanceEntity getAccountBalanceByUId(long uid) {
        return baseMapper.selectOne(new QueryWrapper<AccountBalanceEntity>().eq("uid", uid));
    }

    /**
     * 从用户账户扣钱
     *
     * @param orderEntity
     * @return
     * @throws Exception
     */
    public OrderErrorEnum updateAmountFromOrder(OrderEntity orderEntity) {
        int row = this.updateBalance(AccountBalanceEntity.builder()
                .amount((float) -orderEntity.getPayment())
                .updateTime(System.currentTimeMillis())
                .uid(orderEntity.getUid()).build());
        if (row <= 0)
        {
            log.info("-> uid->{},amount->{}",orderEntity.getUid(),orderEntity.getPayment());
            return OrderErrorEnum.ERROR_NOT_ENOUGH;
        }
        return null;
    }

}
