package com.cf.crs.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户列表
 * @author frank
 * @date 20210-07-06
 *
 **/
@Data
@TableName("t_big_room_man")
@Builder
@ApiModel("直播房间")
@NoArgsConstructor
@AllArgsConstructor
public class BigRoomManEntity implements Serializable {

    @TableId
    @JsonProperty("tId")
    private Integer tId;

    @JsonProperty("tUserId")
    private String tUserId;

    @JsonProperty("tSort")
    private Integer tSort;

    @JsonProperty("tIsDebut")
    private String tIsDebut;

    @JsonProperty("tRoomId")
    private Integer tRoomId;

    @JsonProperty("tChatRoomId")
    private Integer tChatRoomId;

}
