package com.cf.crs.controller.pulic;


import com.cf.crs.entity.UserEntity;
import com.cf.crs.service.OrderCashoutService;
import com.cf.crs.service.UserService;
import com.cf.util.http.ResultJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录
 */
@Slf4j
@RestController
public class UserLoginController {

    @Autowired
    UserService userService;

    @Autowired
    OrderCashoutService orderCashoutService;

    /**
     * 登录
     * @param userName
     * @param password
     * @return
     */
    @PostMapping("/public/user/login")
    public ResultJson<UserEntity> login(String userName, String password){
        return userService.login(userName,password);
    }

    /**
     * 退出登录
     * @return
     */
    @GetMapping("/admin/user/logout")
    public ResultJson<String> logout(){
        return userService.logout();
    }

}
