package com.cf.crs.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cf.crs.common.constant.MsgError;
import com.cf.crs.entity.AccountBalanceEntity;
import com.cf.crs.entity.AccountEntity;
import com.cf.crs.entity.LiveWatchFeeDto;
import com.cf.crs.entity.LiveWatchFeeEntity;
import com.cf.crs.mapper.AccountMapper;
import com.cf.crs.mapper.LiveWatchFeeMapper;
import com.cf.crs.mapper.LiveWatchFeeResultMapper;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import com.cf.util.utils.DataChange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
public class LiveWatchFeeService extends ServiceImpl<LiveWatchFeeMapper, LiveWatchFeeEntity> implements IService<LiveWatchFeeEntity> {

    /**
     * 默认主播收费标准（/min）
     */
    private final static BigDecimal Fee = new BigDecimal(1);

    /**
     * 默认主播分层比例（%）
     */
    private final static Integer FeeRadio = 20;

    @Autowired
    AccountMapper accountMapper;

    @Autowired
    AccountBalanceService accountBalanceService;

    @Autowired
    TaskExecutor taskExecutor;

    @Autowired
    LiveWatchFeeResultMapper liveWatchFeeResultMapper;

    /**
     * 观看直播收费
     * @param dto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultJson<String> watchFeeLive(LiveWatchFeeDto dto){

        long now = System.currentTimeMillis();

        //获取主播收费标准
        Long coverId = dto.getCoverId();
        AccountEntity anchorEntity = accountMapper.selectById(coverId);
        if (anchorEntity == null){
            log.info("watch live {} --> no anchor",coverId);
            return HttpWebResult.getMonoError("当前主播不存在");
        }
        //获取主播收费标准和分层设置
        BigDecimal tFeeStandard = anchorEntity.getTFeeStandard();
        if (tFeeStandard == null) tFeeStandard = Fee;
        Integer tFeeRadio = anchorEntity.getTFeeRadio();
        if (tFeeRadio == null) tFeeRadio = FeeRadio;

        //扣除用户观看费用
        AccountBalanceEntity accountBalanceEntity = AccountBalanceEntity.builder().uid(dto.getUid()).amount(tFeeStandard.negate()).updateTime(now).build();
        Integer integer = accountBalanceService.updateBalance(accountBalanceEntity);
        if (integer <= 0) return HttpWebResult.getMonoError(MsgError.BALANCE_NOT_ENOUGH);

        //添加主播分层收益金额
        BigDecimal coverFee = getCoverAmount(tFeeStandard,tFeeRadio);
        AccountBalanceEntity anchorBalanceEntity = AccountBalanceEntity.builder().uid(dto.getCoverId()).phone(anchorEntity.getTPhone()).amount(coverFee).updateTime(now).build();
        accountBalanceService.updateBalance(anchorBalanceEntity);

        //保存主播扣费记录
        LiveWatchFeeEntity liveWatchFeeEntity = LiveWatchFeeEntity.builder().uid(dto.getUid()).coverId(coverId).totalAmount(tFeeStandard.negate()).coverAmount(coverFee).type(dto.getType()).feeType(0).liveId(dto.getLiveId()).createTime(now).build();
        baseMapper.insert(liveWatchFeeEntity);
        liveWatchFeeResultMapper.updateOrInsert(liveWatchFeeEntity);
        return HttpWebResult.getMonoSucStr();
    }

    /**
     * 获取分层比例
     * @param totalGold  总金额
     * @param feeRadio   分层比例
     * @return
     */
    private BigDecimal getCoverAmount(BigDecimal totalGold, Integer feeRadio) {
        BigDecimal copy = totalGold.divide(new BigDecimal(100), 3, RoundingMode.HALF_UP);
        BigDecimal fallInto = new BigDecimal("100");
        fallInto = fallInto.subtract(new BigDecimal(feeRadio));
        return copy.multiply(fallInto).setScale(2, BigDecimal.ROUND_DOWN);
    }



}
