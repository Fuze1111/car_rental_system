package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 车辆信息响应DTO
 * 用于返回给前端的车辆信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarResponseDTO {

    /**
     * 车牌号
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
     * 车辆状态描述
     */
    private String carStatusDesc;

    /**
     * 类型编号
     */
    private String tNo;

    /**
     * 汽车型号
     */
    private String tName;

    /**
     * 租金率
     */
    private BigDecimal rentRate;

    /**
     * 是否需要维修
     */
    private Boolean needsRepair;

    /**
     * 最后维修日期
     */
    private LocalDate lastRepairDate;

    /**
     * 获取车辆状态描述
     */
    public String getCarStatusDesc() {
        if (carStatus == null) {
            return "未知";
        }
        switch (carStatus) {
            case "0":
                return "可租";
            case "1":
                return "已租";
            case "2":
                return "维修中";
            default:
                return "未知";
        }
    }
}

