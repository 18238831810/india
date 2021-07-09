package com.cf.crs.controller;

import com.cf.crs.properties.AgoraParam;
import com.cf.crs.service.AgoraService;
import com.cf.util.http.ResultJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "声网")
@RestController
@RequestMapping("/api/agora")
public class AgoraController{

    @Autowired
    AgoraService agoraService;


    @GetMapping("/getToken")
    @ApiOperation("获取声网token")
    public ResultJson<String> getAgoraToken(AgoraParam agoraParam) {
        return agoraService.getAgoraToken(agoraParam);
    }

}
