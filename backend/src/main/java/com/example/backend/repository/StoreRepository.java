package com.example.backend.repository;

import com.example.backend.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 租车门店数据访问层接口
 */
@Repository
public interface StoreRepository extends JpaRepository<Store, String> {
    
    /**
     * 根据城市查询门店
     * @param city 城市名称
     * @return 门店列表
     */
    List<Store> findByCity(String city);
    
    /**
     * 根据门店名称模糊查询
     * @param storeName 门店名称
     * @return 门店列表
     */
    List<Store> findByStoreNameContaining(String storeName);
}
