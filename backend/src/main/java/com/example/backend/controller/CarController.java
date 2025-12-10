package com.example.backend.controller;

import com.example.backend.dto.CarRequestDTO;
import com.example.backend.dto.CarResponseDTO;
import com.example.backend.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 车辆信息管理控制器
 * 提供车辆信息管理的RESTful API接口
 */
@RestController
@RequestMapping("/api/cars")
@CrossOrigin(origins = "*")
public class CarController {

    @Autowired
    private CarService carService;

    /**
     * 获取所有车辆信息
     * GET /api/cars
     * @return 车辆列表
     */
    @GetMapping
    public ResponseEntity<?> getAllCars(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "carId") String[] sortBy) {
        if (page < 0 || size <= 0) {
            List<CarResponseDTO> cars = carService.getAllCars();
            return ResponseEntity.ok(cars);
        } else {
            // 解析排序参数，支持多字段排序和方向控制
            Sort sort = Sort.unsorted();
            for (String sortParam : sortBy) {
                String[] parts = sortParam.split(",");
                String field = parts[0];
                Sort.Direction direction = Sort.Direction.ASC;
                if (parts.length > 1 && "desc".equalsIgnoreCase(parts[1])) {
                    direction = Sort.Direction.DESC;
                }
                sort = sort.and(Sort.by(direction, field));
            }
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<CarResponseDTO> carPage = carService.getAllCars(pageable);
            return ResponseEntity.ok(carPage);
        }
    }

    /**
     * 根据车牌号获取车辆信息
     * GET /api/cars/{carId}
     * @param carId 车牌号
     * @return 车辆信息
     */
    @GetMapping("/{carId}")
    public ResponseEntity<CarResponseDTO> getCarById(@PathVariable String carId) {
        return carService.getCarById(carId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 创建新车辆
     * POST /api/cars
     * @param carRequestDTO 车辆请求DTO
     * @return 创建的车辆信息
     */
    @PostMapping
    public ResponseEntity<CarResponseDTO> createCar(@RequestBody CarRequestDTO carRequestDTO) {
        CarResponseDTO createdCar = carService.createCar(carRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCar);
    }

    /**
     * 更新车辆信息
     * PUT /api/cars/{carId}
     * @param carId 车牌号
     * @param carRequestDTO 车辆请求DTO
     * @return 更新后的车辆信息
     */
    @PutMapping("/{carId}")
    public ResponseEntity<CarResponseDTO> updateCar(
            @PathVariable String carId,
            @RequestBody CarRequestDTO carRequestDTO) {
        CarResponseDTO updatedCar = carService.updateCar(carId, carRequestDTO);
        return ResponseEntity.ok(updatedCar);
    }

    /**
     * 删除车辆
     * DELETE /api/cars/{carId}
     * @param carId 车牌号
     * @return 无内容
     */
    @DeleteMapping("/{carId}")
    public ResponseEntity<Void> deleteCar(@PathVariable String carId) {
        carService.deleteCar(carId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 根据车辆状态查询车辆
     * GET /api/cars/status/{carStatus}
     * @param carStatus 车辆状态（0-可租，1-已租，2-维修）
     * @return 车辆列表
     */
    @GetMapping("/status/{carStatus}")
    public ResponseEntity<List<CarResponseDTO>> getCarsByStatus(@PathVariable String carStatus) {
        List<CarResponseDTO> cars = carService.getCarsByStatus(carStatus);
        return ResponseEntity.ok(cars);
    }

    /**
     * 根据车型查询车辆
     * GET /api/cars/type?tNo=xxx&tName=xxx
     * @param tNo 车型编号（可选）
     * @param tName 车型名称（可选）
     * @return 车辆列表
     */
    @GetMapping("/type")
    public ResponseEntity<List<CarResponseDTO>> getCarsByType(
            @RequestParam(required = false) String tNo,
            @RequestParam(required = false) String tName) {
        List<CarResponseDTO> cars = carService.getCarsByType(tNo, tName);
        return ResponseEntity.ok(cars);
    }

    /**
     * 多条件查询车辆
     * GET /api/cars/search?carStatus=xxx&tNo=xxx&tName=xxx
     * @param carStatus 车辆状态（可选）
     * @param tNo 车型编号（可选）
     * @param tName 车型名称（可选）
     * @return 车辆列表
     */
    @GetMapping("/search")
    public ResponseEntity<List<CarResponseDTO>> searchCars(
            @RequestParam(required = false) String carStatus,
            @RequestParam(required = false) String tNo,
            @RequestParam(required = false) String tName) {
        List<CarResponseDTO> cars = carService.searchCars(carStatus, tNo, tName);
        return ResponseEntity.ok(cars);
    }

    /**
     * 根据车牌号模糊查询车辆
     * GET /api/cars/search?carId=xxx
     * @param carId 车牌号关键字
     * @return 车辆列表
     */
    @GetMapping("/search/carId")
    public ResponseEntity<List<CarResponseDTO>> searchCarsByCarId(
            @RequestParam String carId) {
        List<CarResponseDTO> cars = carService.searchCarsByCarId(carId);
        return ResponseEntity.ok(cars);
    }

    /**
     * 更新车辆状态
     * PATCH /api/cars/{carId}/status
     * @param carId 车牌号
     * @param statusRequest 包含新状态的请求体
     * @return 更新后的车辆信息
     */
    @PatchMapping("/{carId}/status")
    public ResponseEntity<CarResponseDTO> updateCarStatus(
            @PathVariable String carId,
            @RequestBody Map<String, String> statusRequest) {
        String newStatus = statusRequest.get("carStatus");
        CarResponseDTO updatedCar = carService.updateCarStatus(carId, newStatus);
        return ResponseEntity.ok(updatedCar);
    }

    /**
     * 获取车辆统计信息
     * GET /api/cars/statistics
     * @return 统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Long>> getCarStatistics() {
        Map<String, Long> statistics = carService.getCarStatistics();
        return ResponseEntity.ok(statistics);
    }
}