package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 租车门店实体类
 * 对应数据库中的store表
 */
@Entity
@Table(name = "store")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Store {

    /**
     * 门店号 - 主键
     */
    @Id
    @Column(name = "StoreNo", length = 4, nullable = false)
    private String storeNo;

    /**
     * 城市
     */
    @Column(name = "City", length = 50, nullable = false)
    private String city;

    /**
     * 门店名称
     */
    @Column(name = "StoreName", length = 100, nullable = false)
    private String storeName;

    /**
     * 门店位置
     */
    @Column(name = "Address", length = 200, nullable = false)
    private String address;
}
