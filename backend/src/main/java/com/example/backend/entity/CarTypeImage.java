package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 车型图片实体类
 * 对应数据库中的 car_type_image 表
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
     * 类型编号（外键）
     */
    @Column(name = "TNo", length = 4, nullable = false)
    private String tNo;

    /**
     * 图片路径（URL）
     */
    @Column(name = "ImagePath", length = 255, nullable = false)
    private String imagePath;

    /**
     * 关联的车型
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TNo", referencedColumnName = "TNo", insertable = false, updatable = false)
    private CarType carType;
}
