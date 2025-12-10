package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 车型图片实体类
 * 对应数据库中的car_type_image表
 */
@Entity
@Table(name = "car_type_image")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarTypeImage {

    /**
     * 图片索引 - 主键（自增）
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ImageID")
    private Integer imageId;

    /**
     * 类型编号（外键部分）
     */
    @Column(name = "TNo", length = 4, nullable = false)
    private String tNo;

    /**
     * 汽车型号（外键部分）
     */
    @Column(name = "TName", length = 20, nullable = false)
    private String tName;

    /**
     * 图片路径
     */
    @Column(name = "ImagePath", length = 255, nullable = false)
    private String imagePath;

    /**
     * 关联的车型
     * 注意：CarType使用联合主键
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "TNo", referencedColumnName = "TNo", insertable = false, updatable = false),
        @JoinColumn(name = "TName", referencedColumnName = "TName", insertable = false, updatable = false)
    })
    private CarType carType;
}
