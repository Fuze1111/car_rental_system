package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 汽车维修记录实体类
 * 对应数据库中的car_repair表
 */
@Entity
@Table(name = "car_repair")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarRepair {

    /**
     * 维修编号 - 主键（自增）
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RNo")
    private Integer rNo;

    /**
     * 维修日期
     */
    @Column(name = "RDate")
    private LocalDate rDate;

    /**
     * 运行公里
     */
    @Column(name = "RunKM", precision = 10, scale = 2)
    private BigDecimal runKm;

    /**
     * 运行小时
     */
    @Column(name = "RunHour", precision = 8, scale = 2)
    private BigDecimal runHour;

    /**
     * 车辆注册号码（外键）
     */
    @Column(name = "CarID", length = 10)
    private String carId;

    /**
     * 关联的车辆信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CarID", referencedColumnName = "CarID", insertable = false, updatable = false)
    private Car car;
}

