package com.cf.crs.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cf.crs.common.constant.MsgError;
import com.cf.crs.common.entity.PagingBase;
import com.cf.crs.common.exception.RenException;
import com.cf.crs.entity.*;
import com.cf.crs.mapper.ConsumeMapper;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.MacSpi;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ConsumeService extends ServiceImpl<ConsumeMapper, ConsumeEntity> implements IService<ConsumeEntity> {


    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    GiftService giftService;

    @Autowired
    ExtractService extractService;

    @Autowired
    AccountBalanceService accountBalanceService;

    @Autowired
    ProfitDetailService profitDetailService;

    @Autowired
    PlatformIncomeService platformIncomeService;

    @Autowired
    FinancialDetailsService financialDetailsService;


    /**
     * 分页查询用户的消费记录（打赏主播）
     *
     * @param dto
     * @return
     */
    public PagingBase<ConsumeEntity> queryList(ConsumeDto dto) {
        Page<ConsumeEntity> iPage = new Page(dto.getPageNum(), dto.getPageSize());
        QueryWrapper<ConsumeEntity> queryWrapper = new QueryWrapper<ConsumeEntity>().eq(dto.getUid() != null,"uid", dto.getUid()).eq(dto.getCoverId() != null,"cover_id",dto.getCoverId())
                .orderByDesc("create_time");
        IPage<ConsumeEntity> pageList = this.page(iPage, queryWrapper);
        List<ConsumeEntity> records = pageList.getRecords();
        return new PagingBase<ConsumeEntity>(records, pageList.getTotal());
    }

    /**
     * 赠送礼物
     * @param giveGiftDto
     * @return
     */
    @Transactional(rollbackFor=Exception.class)
    public ResultJson<String> userGiveGift(GiveGiftDto giveGiftDto){
        if (giveGiftDto.getCoverConsumeUserId() == null || giveGiftDto.getGiftId() == null || giveGiftDto.getGiftNum() == null) return HttpWebResult.getMonoError(MsgError.PARAM_ERROR);
        //加redis分布锁，防止重复点击
        String key = "userGiveGift_" + giveGiftDto.getUid() + "_" + giveGiftDto.getCoverConsumeUserId();
        String value = "userGiveGift_" + giveGiftDto.getUid();
        if(!redisTemplate.boundValueOps(key).setIfAbsent(value, 5, TimeUnit.SECONDS)) return HttpWebResult.getMonoError(MsgError.REQUEST_FREQUENTLY);
        log.info("giveGift:{}->{}",giveGiftDto.getUid(),giveGiftDto.getCoverConsumeUserId());
        try {
            long time = System.currentTimeMillis();
            //获取打赏金额
            GiftEntity giftEntity = giftService.getById(giveGiftDto.getGiftId());
            //计算消费金额
            BigDecimal totalGold = new BigDecimal(giftEntity.getTGiftGold().toString()).multiply(new BigDecimal(giveGiftDto.getGiftNum())).setScale(2, BigDecimal.ROUND_DOWN);

            //扣除用户打赏金额
            AccountBalanceEntity userAccountBalanceEntity = AccountBalanceEntity.builder().amount(totalGold.negate()).uid(giveGiftDto.getUid()).updateTime(time).build();
            Integer integer = accountBalanceService.updateBalance(userAccountBalanceEntity);
            if (integer <= 0)  return HttpWebResult.getMonoError(MsgError.BALANCE_NOT_ENOUGH);

            //主播增加打赏
            ExtractEntity extractEntity = extractService.getExtractEntityByType(Extract.PROJECT_TYPE_PLATFORM);
            //计算收益金额
            BigDecimal coverGold = getCoverAmount(totalGold, extractEntity);
            AccountBalanceEntity coverCcountBalanceEntity = AccountBalanceEntity.builder().amount(coverGold).uid(giveGiftDto.getCoverConsumeUserId()).updateTime(time).consumeCount(coverGold).build();
            accountBalanceService.updateBalance(coverCcountBalanceEntity);

            //新增消费记录
            String giftRemark = new StringBuffer(giftEntity.getTGiftName()).append("_" + giftEntity.getTGiftId() + "*").append(giveGiftDto.getGiftNum()).toString();
            ConsumeEntity consumeEntity = ConsumeEntity.builder().uid(giveGiftDto.getUid()).coverId(giveGiftDto.getCoverConsumeUserId()).totalAmount(totalGold).coverAmount(coverGold).
                    remark(giftRemark).type(1).createTime(time).build();
            this.save(consumeEntity);

            //新增资金明细记录
            addFinancialDetails(giveGiftDto,totalGold,coverGold);
            //更新平台收益记录
            /*BigDecimal systemGold = copy.multiply(new BigDecimal(extractEntity.getTExtractRatio()).setScale(2, BigDecimal.ROUND_DOWN));
            ProfitDetailEntity profitDetailEntity = ProfitDetailEntity.builder().tProfitType(WalletDetail.CHANGE_CATEGOR_GIFT).tProfitGold(systemGold).tCreateTime(new Timestamp(System.currentTimeMillis())).build();
            profitDetailService.save(profitDetailEntity);*/
            //更新平台总收益
           /* PlatformIncomeEntity platformIncomeEntity = PlatformIncomeEntity.builder().tGold(systemGold).build();
            platformIncomeService.updatePlatfoamIncome(platformIncomeEntity);*/
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            //发送礼物报错，则回滚
            throw new RenException(MsgError.SEND_FAIL);
        }finally {
            //释放锁
            redisTemplate.expire(key, -1, TimeUnit.MICROSECONDS);
        }
        return HttpWebResult.getMonoSucStr();
    }

    private BigDecimal getCoverAmount(BigDecimal totalGold, ExtractEntity extractEntity) {
        BigDecimal copy = totalGold.divide(new BigDecimal(100), 3, RoundingMode.HALF_UP);
        BigDecimal fallInto = new BigDecimal("100");
        fallInto = fallInto.subtract(new BigDecimal(extractEntity.getTExtractRatio()));
        return copy.multiply(fallInto).setScale(2, BigDecimal.ROUND_DOWN);
    }


    /**
     * 新增资金明细
     * @param giveGiftDto
     * @param totalGold   用户赠送金币
     * @param coverGold   主播收益金币
     */
    private void addFinancialDetails(GiveGiftDto giveGiftDto,BigDecimal totalGold,BigDecimal coverGold) {
        FinancialDetailsEntity userFinancialDetailsEntity = FinancialDetailsEntity.builder().orderTime(System.currentTimeMillis()).orderSn(giveGiftDto.getUid()+"-"+giveGiftDto.getCoverConsumeUserId()).uid(giveGiftDto.getUid()).type(4).
                amount(totalGold.negate()).build();
        FinancialDetailsEntity coverFinancialDetailsEntity = FinancialDetailsEntity.builder().orderTime(System.currentTimeMillis()).orderSn(giveGiftDto.getUid()+"-"+giveGiftDto.getCoverConsumeUserId()).uid(giveGiftDto.getCoverConsumeUserId()).type(4).
                amount(coverGold).build();
        List<FinancialDetailsEntity> list = Lists.newArrayList(userFinancialDetailsEntity, coverFinancialDetailsEntity);
        financialDetailsService.myInsertBatch(list);

    }

}
