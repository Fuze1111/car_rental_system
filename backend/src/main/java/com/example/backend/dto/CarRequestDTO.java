package com.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 车辆信息请求DTO
 * 用于创建和更新车辆信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarRequestDTO {

    /**
     * 车牌号（创建时必需）
     */
    private String carId;

    /**
     * 购入日期
     */
    private LocalDate purchaseDate;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 车辆状态：0-可租，1-已租，2-维修
     */
    private String carStatus;

    /**
     * 类型编号
     */
    @JsonProperty("tNo")
    @JsonAlias({"TNo", "tno", "TNO", "TNO"})
    private String tNo;

    /**
     * 汽车型号
     */
    @JsonProperty("tName")
    @JsonAlias({"TName", "tname", "TNAME"})
    private String tName;

    /**
     * 运行公里
     */
    private BigDecimal runKm;

    /**
     * 运行小时
     */
    private BigDecimal runHour;
}
