package com.cf.crs.service;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cf.crs.common.constant.OrderErrorEnum;
import com.cf.crs.common.entity.PagingBase;
import com.cf.crs.entity.*;
import com.cf.crs.mapper.AccountMapper;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import com.cf.util.utils.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
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
public class AccountService extends ServiceImpl<AccountMapper, AccountEntity> implements IService<AccountEntity> {

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
    public PagingBase<AccountEntity> queryList(AccountDto dto) {
        Page<AccountEntity> iPage = new Page(dto.getPageNum(), dto.getPageSize());
        QueryWrapper<AccountEntity> queryWrapper = getQueryWrapper(dto);
        IPage<AccountEntity> pageList = this.page(iPage, queryWrapper);
        List<AccountEntity> records = pageList.getRecords();
        return new PagingBase<AccountEntity>(records, pageList.getTotal());
    }

    private QueryWrapper<AccountEntity> getQueryWrapper(AccountDto dto) {
        QueryWrapper<AccountEntity> queryWrapper = new QueryWrapper<AccountEntity>().eq(dto.getTId() != null,"t_id", dto.getTId())
                .eq(dto.getTRole() != null,"t_role",dto.getTRole());
        return queryWrapper;
    }

    public ResultJson<String> updateRecordStatus(AccountDto dto) {
        baseMapper.update(null,new UpdateWrapper<AccountEntity>().eq("t_id",dto.getTId()).set("t_record",dto.getTRecord()));
        return HttpWebResult.getMonoSucStr();
    }


}
