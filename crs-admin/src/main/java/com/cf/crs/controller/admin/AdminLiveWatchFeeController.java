package com.cf.crs.controller.admin;


import com.cf.crs.common.entity.PagingBase;
import com.cf.crs.common.utils.Result;
import com.cf.crs.entity.*;
import com.cf.crs.service.AccountService;
import com.cf.crs.service.LiveWatchFeeService;
import com.cf.util.http.ResultJson;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags="直播观看收费")
@RestController
@RequestMapping("/admin/liveWatchFee")
public class AdminLiveWatchFeeController {

    @Autowired
    LiveWatchFeeService liveWatchFeeService;


    @PostMapping("/queryList")
    public Result<PagingBase<LiveWatchFeeEntity>> queryList(LiveWatchFeePage dto){
        PagingBase<LiveWatchFeeEntity> pagingBase = liveWatchFeeService.queryList(dto);
        return new Result<PagingBase<LiveWatchFeeEntity>>().ok(pagingBase);
    }

    @PostMapping("/queryResultList")
    public Result<PagingBase<LiveWatchFeeResultEntity>> queryResultList(LiveWatchFeePage dto){
        PagingBase<LiveWatchFeeResultEntity> pagingBase = liveWatchFeeService.queryResultList(dto);
        return new Result<PagingBase<LiveWatchFeeResultEntity>>().ok(pagingBase);
    }


}
