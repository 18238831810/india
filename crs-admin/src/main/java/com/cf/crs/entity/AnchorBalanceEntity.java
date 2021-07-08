package com.cf.crs.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 用户余额表
 */
@Data
@TableName("u_account_balance")
public class AnchorBalanceEntity extends AccountBalanceEntity{


    @Excel(name = "打赏收益")
    @ApiModelProperty(value = "打赏收益")
    private BigDecimal consumeCount = BigDecimal.ZERO;

    @Excel(name = "交易流水分层")
    @ApiModelProperty(value = "交易流水分层")
    private BigDecimal orderCount = BigDecimal.ZERO;

}
