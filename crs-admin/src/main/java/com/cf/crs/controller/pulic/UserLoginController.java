package com.cf.crs.controller.pulic;


import com.cf.crs.entity.UserEntity;
import com.cf.crs.service.OrderCashoutService;
import com.cf.crs.service.UserService;
import com.cf.util.http.ResultJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录
 */
@Slf4j
@RestController
@RequestMapping("/public/user")
public class UserLoginController {

    @Autowired
    UserService userService;

    @Autowired
    OrderCashoutService orderCashoutService;

    /**
     * 存款回调
     * @param userName
     * @param password
     * @return
     */
    @PostMapping("/login")
    public ResultJson<UserEntity> login(String userName, String password){
        return userService.login(userName,password);
    }

}
