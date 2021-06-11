package com.cf.crs.service;


import com.cf.crs.common.exception.RenException;
import com.cf.crs.entity.*;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import com.cf.util.utils.WalletDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ConsumeService {


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

    /**
     * 赠送礼物
     * @param giveGiftDto
     * @return
     */
    @Transactional(rollbackFor=Exception.class)
    public ResultJson<String> userGiveGift(GiveGiftDto giveGiftDto){
        if (giveGiftDto.getCoverConsumeUserId() == null || giveGiftDto.getGiftId() == null || giveGiftDto.getGiftNum() == null) return HttpWebResult.getMonoError("请求参数异常");

        //加redis分布锁，防止重复点击
        String key = "userGiveGift_" + giveGiftDto.getUid() + "_" + giveGiftDto.getCoverConsumeUserId();
        String value = "userGiveGift_" + giveGiftDto.getUid();
        if(!redisTemplate.boundValueOps(key).setIfAbsent(value, 5, TimeUnit.SECONDS)) return HttpWebResult.getMonoError("请勿过于频繁操作");
        log.info("发送礼物:{}->{}",giveGiftDto.getUid(),giveGiftDto.getCoverConsumeUserId());
        try {
            //获取打赏金额
            GiftEntity giftEntity = giftService.getById(giveGiftDto.getGiftId());
            BigDecimal totalGold = new BigDecimal(giftEntity.getTGiftGold().toString())
                    .multiply(new BigDecimal(giveGiftDto.getGiftNum())).setScale(2, BigDecimal.ROUND_DOWN);
            //扣除用户打赏金额
            long time = System.currentTimeMillis();
            AccountBalanceEntity userAccountBalanceEntity = AccountBalanceEntity.builder().amount(totalGold.negate()).uid(giveGiftDto.getUid()).updateTime(time).build();
            Integer integer = accountBalanceService.updateBalance(userAccountBalanceEntity);
            if (integer <= 0)  return HttpWebResult.getMonoError("余额不足!请充值");

            //主播增加打赏
            ExtractEntity extractEntity = extractService.getExtractEntityByType(Extract.PROJECT_TYPE_PLATFORM);
            BigDecimal copy = totalGold.divide(new BigDecimal(100), 3, RoundingMode.HALF_UP);
            BigDecimal fallInto = new BigDecimal("100");
            fallInto = fallInto.subtract(new BigDecimal(extractEntity.getTExtractRatio()));
            BigDecimal coverGold = copy.multiply(fallInto).setScale(2, BigDecimal.ROUND_DOWN);
            AccountBalanceEntity coverCcountBalanceEntity = AccountBalanceEntity.builder().amount(coverGold).uid(giveGiftDto.getCoverConsumeUserId()).updateTime(time).build();
            accountBalanceService.updateBalance(coverCcountBalanceEntity);

            //更新平台收益记录
            BigDecimal systemGold = copy.multiply(new BigDecimal(extractEntity.getTExtractRatio()).setScale(2, BigDecimal.ROUND_DOWN));
            ProfitDetailEntity profitDetailEntity = ProfitDetailEntity.builder().tProfitType(WalletDetail.CHANGE_CATEGOR_GIFT).tProfitGold(systemGold).tCreateTime(new Timestamp(System.currentTimeMillis())).build();
            profitDetailService.save(profitDetailEntity);
            //更新平台总收益
            PlatformIncomeEntity platformIncomeEntity = PlatformIncomeEntity.builder().tGold(systemGold).build();
            platformIncomeService.updatePlatfoamIncome(platformIncomeEntity);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            //发送礼物报错，则回滚
            throw new RenException("发送礼物失败");
        }finally {
            //释放锁
            redisTemplate.expire(key, -1, TimeUnit.MICROSECONDS);
        }
        return HttpWebResult.getMonoSucStr();
    }

    public static void main(String[] args) {
        BigDecimal copy = new BigDecimal("40").divide(new BigDecimal(100), 3, RoundingMode.HALF_UP);
        System.out.println(copy);
        BigDecimal fallInto = new BigDecimal("100");
        fallInto = fallInto.subtract(new BigDecimal(30));
        System.out.println(fallInto);
        BigDecimal coverGold = copy.multiply(fallInto).setScale(2, BigDecimal.ROUND_DOWN);
        System.out.println(coverGold);
    }

}
