package com.cf.crs.controller.admin;


import com.cf.crs.common.entity.PagingBase;
import com.cf.crs.common.utils.Result;
import com.cf.crs.entity.ConsumeDto;
import com.cf.crs.entity.ConsumeEntity;
import com.cf.crs.entity.OrderCashinDto;
import com.cf.crs.entity.OrderCashinEntity;
import com.cf.crs.service.ConsumeService;
import com.cf.crs.service.OrderCashinService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags="用户消费")
@RestController
@RequestMapping("/admin/consume")
public class AdminConsumeController {

    @Autowired
    ConsumeService consumeService;

    @PostMapping("/queryList")
    public Result<PagingBase<ConsumeEntity>> list(ConsumeDto dto){
        PagingBase<ConsumeEntity> pagingBase = consumeService.queryList(dto);
        return new Result<PagingBase<ConsumeEntity>>().ok(pagingBase);
    }
}
