package com.cf.crs.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cf.crs.entity.*;
import com.cf.crs.mapper.AccountMapper;
import com.cf.crs.mapper.CashinRebateMapper;
import com.cf.crs.mapper.OrderCashinMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


/**
 * 用户余额表
 */
@Slf4j
@Service
public class CashinRebateService extends ServiceImpl<CashinRebateMapper, CashinRebateEntity> implements IService<CashinRebateEntity> {

    @Autowired
    AccountMapper accountMapper;

    @Autowired
    OrderCashinMapper orderCashinMapper;

    @Autowired
    PromotionRebateService promotionRebateService;

    @Autowired
    AccountBalanceService accountBalanceService;

    /**
     * 发放注册充值奖励
     * @param uid
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveCashinRebate(Long uid){
        long now = System.currentTimeMillis();

        CashinRebateEntity cashinRebateEntity = baseMapper.selectOne(new QueryWrapper<CashinRebateEntity>().eq("uid", uid));
        if (cashinRebateEntity != null) return;
        log.info("saveCashinRebate -->{}",uid);

        OrderCashinEntity orderCashinEntity = orderCashinMapper.selectOne(new QueryWrapper<OrderCashinEntity>().eq("uid", uid).eq("status", 2).last(" limit 1"));
        if (orderCashinEntity == null) {
            log.info("saveCashinRebate uid --> {} no cashin",uid);
            return;
        }

        PromotionRebateEntity promotionRebateSetting = promotionRebateService.getPromotionRebateSetting();
        if (promotionRebateSetting == null){
            log.info("saveCashinRebate has not setting");
            return;
        }

        AccountEntity accountEntity = accountMapper.selectById(uid);
        Long direct = accountEntity.getTReferee();
        if (direct == null || direct <= 0) {
            log.info("saveCashinRebate uid --> {} no direct",uid);
            return;
        }
        AccountEntity directEntity = accountMapper.selectById(direct);

        cashinRebateEntity = new CashinRebateEntity();
        updateDirectBlance(uid, now, cashinRebateEntity, promotionRebateSetting, direct, directEntity);

        updateIndirectBlance(uid, now, cashinRebateEntity, promotionRebateSetting, directEntity);

        //保存奖励发放记录
        baseMapper.insert(cashinRebateEntity);
    }

    /**
     * 更新直接推荐人余额
     * @param uid
     * @param now
     * @param cashinRebateEntity
     * @param promotionRebateSetting
     * @param direct
     * @param directEntity
     */
    private void updateDirectBlance(Long uid, long now, CashinRebateEntity cashinRebateEntity, PromotionRebateEntity promotionRebateSetting, Long direct, AccountEntity directEntity) {
        //发放直接推荐奖励
        BigDecimal cashinDirect = promotionRebateSetting.getCashinDirect();
        AccountBalanceEntity directBalanceEntity = AccountBalanceEntity.builder().amount(cashinDirect).uid(direct).phone(directEntity.getTPhone()).updateTime(now).build();
        accountBalanceService.updateBalance(directBalanceEntity);
        cashinRebateEntity.setUid(uid);
        cashinRebateEntity.setDirect(direct);
        cashinRebateEntity.setCashinDirect(cashinDirect);
        cashinRebateEntity.setCreateTime(now);
    }

    /**
     * 更新间接推荐人余额
     * @param uid
     * @param now
     * @param cashinRebateEntity
     * @param promotionRebateSetting
     * @param directEntity
     */
    private void updateIndirectBlance(Long uid, long now, CashinRebateEntity cashinRebateEntity, PromotionRebateEntity promotionRebateSetting, AccountEntity directEntity) {
        Long indirect = directEntity.getTReferee();
        if (indirect == null || indirect <= 0) {
            log.info("saveCashinRebate uid --> {} no indirect",uid);
        }else {
            //发放间接推荐奖励
            BigDecimal cashinIndirect = promotionRebateSetting.getCashinIndirect();
            AccountBalanceEntity indirectBalanceEntity = AccountBalanceEntity.builder().amount(cashinIndirect).uid(indirect).updateTime(now).build();
            accountBalanceService.updateBalance(indirectBalanceEntity);
            cashinRebateEntity.setIndirect(indirect);
            cashinRebateEntity.setCashinIndirect(cashinIndirect);
        }
    }


}
