package com.cf.crs.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cf.crs.common.constant.OrderErrorEnum;
import com.cf.crs.common.entity.PagingBase;
import com.cf.crs.entity.*;
import com.cf.crs.mapper.AccountBalanceMapper;
import com.cf.crs.mapper.AccountMapper;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import com.cf.util.utils.DateUtil;
import com.cf.util.utils.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;


/**
 * 用户余额表
 */
@Slf4j
@Service
public class AccountBalanceService extends ServiceImpl<AccountBalanceMapper, AccountBalanceEntity> implements IService<AccountBalanceEntity> {

    @Autowired
    AccountMapper accountMapper;

    @Autowired
    HttpServletResponse response;


    @Autowired
    MapperFacade mapperFacade;

    /**
     * 分页查询用户的资金列表
     *
     * @param dto
     * @return
     */
    public PagingBase<AccountBalanceEntity> queryList(AccountBalanceDto dto) {
        Page<AccountBalanceEntity> iPage = new Page(dto.getPageNum(), dto.getPageSize());
        QueryWrapper<AccountBalanceEntity> queryWrapper = getQueryWrapper(dto);
        IPage<AccountBalanceEntity> pageList = this.page(iPage, queryWrapper);
        List<AccountBalanceEntity> records = pageList.getRecords();
        return new PagingBase<AccountBalanceEntity>(records, pageList.getTotal());
    }

    private QueryWrapper<AccountBalanceEntity> getQueryWrapper(AccountBalanceDto dto) {
        QueryWrapper<AccountBalanceEntity> queryWrapper = new QueryWrapper<AccountBalanceEntity>().eq(dto.getUid() != null,"uid", dto.getUid());
        if (dto.getType() == 1) queryWrapper.gt("consume_count",0);
        else queryWrapper.and(wrapper -> wrapper.eq("consume_count", 0).or().isNull("consume_count"));
        return queryWrapper;
    }


    public void export(AccountBalanceDto dto) {
        try {
            QueryWrapper<AccountBalanceEntity> queryWrapper = getQueryWrapper(dto);
            List<AccountBalanceEntity> list = baseMapper.selectList(queryWrapper);
            if (dto.getType() == 0) {
                //普通用户导出
                ExcelUtils.exportExcelWithDict(response, null, list, AccountBalanceEntity.class);
            }else{
                //主播导出
                List<AnchorBalanceEntity> anchorList = mapperFacade.mapAsList(list, AnchorBalanceEntity.class);
                ExcelUtils.exportExcelWithDict(response, null, anchorList, AnchorBalanceEntity.class);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 用户余额变动统一入口
     * @param accountBalanceEntity
     * @return
     */
    public Integer updateBalance(AccountBalanceEntity accountBalanceEntity) {
        if (accountBalanceEntity.getUid() == null) return 0;
        if (accountBalanceEntity.getAmount() == null) return 0;
        AccountEntity accountEntity = accountMapper.selectById(accountBalanceEntity.getUid());
        if (accountEntity != null) accountBalanceEntity.setPhone(accountEntity.getTPhone());
        if (accountBalanceEntity.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            accountBalanceEntity.setAmount(accountBalanceEntity.getAmount().negate());
            return baseMapper.updateSubBalance(accountBalanceEntity);
        }
        else return baseMapper.updateAddBalance(accountBalanceEntity);
    }

    public ResultJson<AccountBalanceEntity> getAccountBalanceByUId(Long uid) {
        AccountBalanceEntity accountBalanceEntity = getAccountBalanceByUid(uid);
        if (accountBalanceEntity != null) {
            accountBalanceEntity.setAmount(accountBalanceEntity.getAmount().setScale(0, BigDecimal.ROUND_DOWN));
            return HttpWebResult.getMonoSucResult(accountBalanceEntity);
        }
        return HttpWebResult.getMonoSucResult(AccountBalanceEntity.builder().amount(BigDecimal.ZERO).uid(uid).updateTime(System.currentTimeMillis()).build());
    }

    public AccountBalanceEntity getAccountBalanceByUid(Long uid) {
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
                .amount(new BigDecimal(Double.toString(orderEntity.getPayment())).negate())
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
