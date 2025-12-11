package com.example.backend.controller;

import com.example.backend.entity.CarType;
import com.example.backend.service.CarTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 车型管理控制器
 * 提供车型信息管理的RESTful API接口
 */
@RestController
@RequestMapping("/api/car-types")
@CrossOrigin(origins = "*")
public class CarTypeController {

    @Autowired
    private CarTypeService carTypeService;

    /**
     * 获取所有车型信息
     * GET /api/car-types
     *
     * @return 车型列表
     */
    @GetMapping
    public ResponseEntity<List<CarType>> getAllCarTypes() {
        List<CarType> carTypes = carTypeService.getAllCarTypes();
        return ResponseEntity.ok(carTypes);
    }

    /**
     * 根据类型编号和型号名称获取车型信息
     * GET /api/car-types/{tNo}/{tName}
     *
     * @param tNo   类型编号
     * @param tName 型号名称
     * @return 车型信息
     */
    @GetMapping("/{tNo}/{tName}")
    public ResponseEntity<CarType> getCarTypeById(
            @PathVariable String tNo,
            @PathVariable String tName) {
        return carTypeService.getCarTypeById(tNo, tName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 创建新车型
     * POST /api/car-types
     *
     * @param carType 车型信息
     * @return 创建的车型信息
     */
    @PostMapping
    public ResponseEntity<CarType> createCarType(@RequestBody CarType carType) {
        CarType createdCarType = carTypeService.createCarType(carType);
        return ResponseEntity.ok(createdCarType);
    }

    /**
     * 更新车型信息
     * PUT /api/car-types/{tNo}/{tName}
     *
     * @param tNo     类型编号
     * @param tName   型号名称
     * @param carType 车型信息
     * @return 更新后的车型信息
     */
    @PutMapping("/{tNo}/{tName}")
    public ResponseEntity<CarType> updateCarType(
            @PathVariable String tNo,
            @PathVariable String tName,
            @RequestBody CarType carType) {
        CarType updatedCarType = carTypeService.updateCarType(tNo, tName, carType);
        return ResponseEntity.ok(updatedCarType);
    }

    /**
     * 删除车型
     * DELETE /api/car-types/{tNo}/{tName}
     *
     * @param tNo   类型编号
     * @param tName 型号名称
     * @return 无内容
     */
    @DeleteMapping("/{tNo}/{tName}")
    public ResponseEntity<Void> deleteCarType(
            @PathVariable String tNo,
            @PathVariable String tName) {
        carTypeService.deleteCarType(tNo, tName);
        return ResponseEntity.noContent().build();
    }
}