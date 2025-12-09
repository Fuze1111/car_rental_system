package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 租赁业务实体类
 * 对应数据库中的rental表
 */
@Entity
@Table(name = "rental")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rental {

    /**
     * 发票号 - 主键
     */
    @Id
    @Column(name = "InvoiceNo", length = 20, nullable = false)
    private String invoiceNo;

    /**
     * 借车日期
     */
    @Column(name = "LoanDate")
    private LocalDate loanDate;

    /**
     * 还车日期
     */
    @Column(name = "ReturnDate")
    private LocalDate returnDate;

    /**
     * 借车地点
     */
    @Column(name = "LoanPlace", length = 100)
    private String loanPlace;

    /**
     * 还车地点
     */
    @Column(name = "ReturnPlace", length = 100)
    private String returnPlace;

    /**
     * 牌照号（外键）
     */
    @Column(name = "CarID", length = 10)
    private String carId;

    /**
     * 客户号（外键）
     */
    @Column(name = "CNo", length = 8)
    private String cNo;

    /**
     * 司机号（外键）
     */
    @Column(name = "DNo", length = 8)
    private String dNo;

    /**
     * 往返里程
     */
    @Column(name = "RoundTripDistance", precision = 8, scale = 2)
    private BigDecimal roundTripDistance;

    /**
     * 行驶时间
     */
    @Column(name = "DriveTime", precision = 4, scale = 2)
    private BigDecimal driveTime;

    /**
     * 应付金额
     */
    @Column(name = "Amount", precision = 8, scale = 2)
    private BigDecimal amount;

    /**
     * 关联的车辆
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CarID", referencedColumnName = "CarID", insertable = false, updatable = false)
    private Car car;

    /**
     * 关联的客户
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CNo", referencedColumnName = "CNo", insertable = false, updatable = false)
    private Customer customer;

    /**
     * 关联的司机
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DNo", referencedColumnName = "DNo", insertable = false, updatable = false)
    private Driver driver;
}