package com.cf.crs.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cf.crs.common.entity.PagingBase;
import com.cf.crs.entity.*;
import com.cf.crs.mapper.AccountMapper;
import com.cf.crs.mapper.CashinRebateMapper;
import com.cf.crs.mapper.OrderCashinMapper;
import com.cf.util.utils.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;


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

    @Autowired
    HttpServletResponse response;

    /**
     * 查询用户的资金明细列表
     *
     * @param dto
     * @return
     */
    public PagingBase<CashinRebateEntity> queryList(CashinRebateDto dto) {
        Page<CashinRebateEntity> iPage = new Page(dto.getPageNum(), dto.getPageSize());
        QueryWrapper<CashinRebateEntity> queryWrapper = getQueryWrapper(dto);
        IPage<CashinRebateEntity> pageList = this.page(iPage, queryWrapper);
        List<CashinRebateEntity> records = pageList.getRecords();
        return new PagingBase<CashinRebateEntity>(records, pageList.getTotal());
    }

    private QueryWrapper<CashinRebateEntity> getQueryWrapper(CashinRebateDto dto) {
        return new QueryWrapper<CashinRebateEntity>().orderByDesc("create_time");
    }

    public void export(CashinRebateDto dto) {
        try {
            QueryWrapper<CashinRebateEntity> queryWrapper = getQueryWrapper(dto);
            List<CashinRebateEntity> list = baseMapper.selectList(queryWrapper);
            ExcelUtils.exportExcelWithDict(response, null, list, CashinRebateEntity.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

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
        if (accountEntity == null){
            log.info("saveCashinRebate uid --> {} no uid",uid);
            return;
        }
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

        //System.out.println(1/0);
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
        cashinRebateEntity.setType(0);
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
