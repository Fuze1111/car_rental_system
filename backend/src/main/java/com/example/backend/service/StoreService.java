package com.example.backend.service;

import com.example.backend.entity.Store;
import com.example.backend.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 租车门店服务类
 */
@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;

    /**
     * 获取所有门店
     * @return 门店列表
     */
    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    /**
     * 根据门店号获取门店信息
     * @param storeNo 门店号
     * @return 门店信息
     */
    public Optional<Store> getStoreById(String storeNo) {
        return storeRepository.findById(storeNo);
    }

    /**
     * 根据城市查询门店
     * @param city 城市名称
     * @return 门店列表
     */
    public List<Store> getStoresByCity(String city) {
        return storeRepository.findByCity(city);
    }

    /**
     * 根据门店名称模糊查询
     * @param storeName 门店名称
     * @return 门店列表
     */
    public List<Store> searchStoresByName(String storeName) {
        return storeRepository.findByStoreNameContaining(storeName);
    }
}
