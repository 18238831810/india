package com.cf.crs.controller.admin;


import com.cf.crs.entity.MenuEntity;
import com.cf.crs.entity.UserEntity;
import com.cf.crs.service.MenuService;
import com.cf.crs.service.OrderCashoutService;
import com.cf.crs.service.UserService;
import com.cf.util.http.ResultJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 菜单
 */
@Slf4j
@RestController
@RequestMapping("/admin/menu")
public class MenuController {

    @Autowired
    MenuService menuService;


    /**
     * 获取所有菜单
     * @return
     */
    @GetMapping("/queryAllMenu")
    public ResultJson<List<MenuEntity>> queryAllMenu(){
        return menuService.queryAllMenu();
    }

}
