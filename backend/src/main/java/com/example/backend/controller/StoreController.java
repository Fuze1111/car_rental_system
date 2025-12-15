package com.example.backend.controller;

import com.example.backend.entity.Store;
import com.example.backend.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 租车门店控制器
 * 提供门店信息查询的RESTful API接口
 */
@RestController
@RequestMapping("/api/stores")
@CrossOrigin(origins = "*")
public class StoreController {

    @Autowired
    private StoreService storeService;

    /**
     * 获取所有门店
     * GET /api/stores
     * @return 门店列表
     */
    @GetMapping
    public ResponseEntity<List<Store>> getAllStores() {
        List<Store> stores = storeService.getAllStores();
        return ResponseEntity.ok(stores);
    }

    /**
     * 根据门店号获取门店信息
     * GET /api/stores/{storeNo}
     * @param storeNo 门店号
     * @return 门店信息
     */
    @GetMapping("/{storeNo}")
    public ResponseEntity<Store> getStoreById(@PathVariable String storeNo) {
        return storeService.getStoreById(storeNo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根据城市查询门店
     * GET /api/stores/city/{city}
     * @param city 城市名称
     * @return 门店列表
     */
    @GetMapping("/city/{city}")
    public ResponseEntity<List<Store>> getStoresByCity(@PathVariable String city) {
        List<Store> stores = storeService.getStoresByCity(city);
        return ResponseEntity.ok(stores);
    }

    /**
     * 根据门店名称模糊查询
     * GET /api/stores/search?name=xxx
     * @param name 门店名称关键字
     * @return 门店列表
     */
    @GetMapping("/search")
    public ResponseEntity<List<Store>> searchStores(@RequestParam String name) {
        List<Store> stores = storeService.searchStoresByName(name);
        return ResponseEntity.ok(stores);
    }
}
