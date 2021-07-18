package com.cf.crs.controller;


import com.cf.crs.entity.LiveWatchFeeDto;
import com.cf.crs.service.LiveWatchFeeService;
import com.cf.util.http.ResultJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags="直播观看收费记录")
@RestController
@RequestMapping("/api/liveWatchFee")
public class LiveWatchFeeController {

    @Autowired
    LiveWatchFeeService liveWatchFeeService;

    @PostMapping("/save")
    @ApiOperation("直播观看收费")
    public ResultJson<String> order(LiveWatchFeeDto dto){
        return liveWatchFeeService.watchFeeLive(dto);
    }


}
