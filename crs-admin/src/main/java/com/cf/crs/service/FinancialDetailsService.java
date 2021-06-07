package com.cf.crs.service;


import com.cf.crs.entity.FinancialDetailsEntity;
import com.cf.crs.mapper.FinancialDetailsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 资金明细
 * @author frank
 * @date 2021-06-07
 */
@Slf4j
@Service
public class FinancialDetailsService {

    @Autowired
    FinancialDetailsMapper financialDetailsMapper;


    /**
     * 从用户账户扣钱
     * @param
     * @return
     * @throws Exception
     */
    public Integer save(FinancialDetailsEntity financialDetailsEntity){
       return financialDetailsMapper.insert(financialDetailsEntity);
    }



}
