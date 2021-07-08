package com.cf.crs.service;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cf.crs.common.constant.MsgError;
import com.cf.crs.entity.UserEntity;
import com.cf.crs.mapper.UserMapper;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import com.cf.util.utils.Const;
import com.cf.util.utils.Md5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * 用户信息
 */
@Slf4j
@Service
public class UserService extends ServiceImpl<UserMapper, UserEntity> implements IService<UserEntity> {

    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    @Autowired
    HttpServletRequest request;

    /**
     * 登录
     * @return
     */
    public ResultJson<UserEntity> login(String userName, String password){
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) return HttpWebResult.getMonoError(MsgError.USER_PASSWORD_ERROR);
        UserEntity userEntity = baseMapper.selectOne(new QueryWrapper<UserEntity>().eq("t_is_disable", 0).eq("t_user_name", userName).last(" limit 1"));
        if (userEntity == null) return HttpWebResult.getMonoError(MsgError.USER_PASSWORD_ERROR);
        if (!Md5Util.md5(password).equalsIgnoreCase(userEntity.getTPassWord())) return HttpWebResult.getMonoError(MsgError.USER_PASSWORD_ERROR);
        //保存token
        String token = "authorization_"+UUID.randomUUID().toString()+System.currentTimeMillis();
        userEntity.setTPassWord(token);
        redisTemplate.opsForValue().set(token, JSON.toJSONString(userEntity),2, TimeUnit.HOURS);
        log.info("user:{}",JSON.toJSONString(userEntity));
        return HttpWebResult.getMonoSucResult(userEntity);
    }


    /**
     * 退出登录
     * @return
     */
    public ResultJson<String> logout(){
        String authorization = request.getHeader(Const.AUTHORIZATION);
        redisTemplate.delete(authorization);
        return HttpWebResult.getMonoSucStr();
    }


}
