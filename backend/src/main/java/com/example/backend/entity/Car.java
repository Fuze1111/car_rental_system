package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 车辆实体类
 * 对应数据库中的car表
 */
@Entity
@Table(name = "car")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Car {

    /**
     * 注册号码（车牌号）- 主键
     */
    @Id
    @Column(name = "CarID", length = 10, nullable = false)
    private String carId;

    /**
     * 购入日期
     */
    @Column(name = "PurchaseDate")
    private LocalDate purchaseDate;

    /**
     * 价格
     */
    @Column(name = "Price", precision = 8, scale = 2)
    private BigDecimal price;

    /**
     * 车辆状态
     * 0 - 可租
     * 1 - 已租
     * 2 - 维修
     */
    @Column(name = "CarStatus", length = 5)
    private String carStatus;

    /**
     * 运行公里
     */
    @Column(name = "RUNKM", precision = 10, scale = 2)
    private BigDecimal runKm;

    /**
     * 运行小时
     */
    @Column(name = "RunHour", precision = 8, scale = 2)
    private BigDecimal runHour;

    /**
     * 关联的汽车类型
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TNo", referencedColumnName = "TNo")
    private CarType carType;
}