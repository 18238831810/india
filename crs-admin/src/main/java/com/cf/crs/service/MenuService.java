package com.cf.crs.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cf.crs.entity.MenuEntity;
import com.cf.crs.mapper.MenuMapper;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 菜单
 */
@Slf4j
@Service
public class MenuService extends ServiceImpl<MenuMapper, MenuEntity> implements IService<MenuEntity> {


    /**
     * 获取所有菜单
     * @return
     */
    public ResultJson<List<MenuEntity>> queryAllMenu(){
        List<MenuEntity> list = baseMapper.selectList(new QueryWrapper<MenuEntity>());
        return HttpWebResult.getMonoSucResult(list);
    }


}
