package com.cf.crs.controller;

import com.cf.crs.entity.GiveGiftDto;
import com.cf.crs.service.ConsumeService;
import com.cf.util.http.ResultJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "赠送礼物")
@RestController
@RequestMapping("/api/consume")
public class ConsumeController extends BaseController {

    @Autowired
    ConsumeService consumeService;


    @PostMapping("/userGiveGift")
    @ApiOperation("赠送礼物")
    public ResultJson<String> userGiveGift(GiveGiftDto giveGiftDto) {
        return consumeService.userGiveGift(giveGiftDto);
    }

}
