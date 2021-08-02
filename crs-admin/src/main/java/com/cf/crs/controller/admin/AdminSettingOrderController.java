package com.cf.crs.controller.admin;


import com.cf.crs.common.entity.PagingBase;
import com.cf.crs.common.entity.QueryPage;
import com.cf.crs.common.utils.Result;
import com.cf.crs.entity.SettingOrderEntity;
import com.cf.crs.service.SettingOrderService;
import com.cf.util.http.ResultJson;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags="自定义下单数据")
@RestController
@RequestMapping("/admin/settingOrder")
public class AdminSettingOrderController {

    @Autowired
    SettingOrderService settingOrderService;


    @PostMapping("/queryList")
    public Result<PagingBase<SettingOrderEntity>> list(QueryPage dto){
        PagingBase<SettingOrderEntity> pagingBase = settingOrderService.queryList(dto);
        return new Result<PagingBase<SettingOrderEntity>>().ok(pagingBase);
    }


    @PostMapping("/update")
    public ResultJson<String> updateEntity(SettingOrderEntity entity){
        return settingOrderService.updateEntity(entity);
    }

    @PostMapping("/delete")
    public ResultJson<String> updateFee(Long id){
        return settingOrderService.deleteEntity(id);
    }

}
