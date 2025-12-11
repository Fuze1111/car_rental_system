package com.example.backend.controller;

import com.example.backend.dto.CarRepairRequestDTO;
import com.example.backend.dto.CarRepairResponseDTO;
import com.example.backend.service.CarRepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 车辆维修记录控制器
 * 提供车辆维修记录管理的RESTful API接口
 */
@RestController
@RequestMapping("/api/repairs")
@CrossOrigin(origins = "*")
public class CarRepairController {

    @Autowired
    private CarRepairService carRepairService;

    /**
     * 获取所有维修记录
     * GET /api/repairs
     * @return 维修记录列表
     */
    @GetMapping
    public ResponseEntity<List<CarRepairResponseDTO>> getAllRepairs() {
        List<CarRepairResponseDTO> repairs = carRepairService.getAllRepairs();
        return ResponseEntity.ok(repairs);
    }

    /**
     * 根据维修编号获取维修记录
     * GET /api/repairs/{rNo}
     * @param rNo 维修编号
     * @return 维修记录
     */
    @GetMapping("/{rNo}")
    public ResponseEntity<CarRepairResponseDTO> getRepairById(@PathVariable String rNo) {
        return carRepairService.getRepairById(rNo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根据车辆号获取维修记录
     * GET /api/repairs/car/{carId}
     * @param carId 车辆号
     * @return 维修记录列表
     */
    @GetMapping("/car/{carId}")
    public ResponseEntity<List<CarRepairResponseDTO>> getRepairsByCarId(@PathVariable String carId) {
        List<CarRepairResponseDTO> repairs = carRepairService.getRepairsByCarId(carId);
        return ResponseEntity.ok(repairs);
    }

    /**
     * 创建新维修记录
     * POST /api/repairs
     * @param repairRequestDTO 维修记录请求DTO
     * @return 创建的维修记录
     */
    @PostMapping
    public ResponseEntity<CarRepairResponseDTO> createRepair(@RequestBody CarRepairRequestDTO repairRequestDTO) {
        CarRepairResponseDTO createdRepair = carRepairService.createRepair(repairRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRepair);
    }

    /**
     * 更新维修记录
     * PUT /api/repairs/{rNo}
     * @param rNo 维修编号
     * @param repairRequestDTO 维修记录请求DTO
     * @return 更新后的维修记录
     */
    @PutMapping("/{rNo}")
    public ResponseEntity<CarRepairResponseDTO> updateRepair(
            @PathVariable String rNo,
            @RequestBody CarRepairRequestDTO repairRequestDTO) {
        CarRepairResponseDTO updatedRepair = carRepairService.updateRepair(rNo, repairRequestDTO);
        return ResponseEntity.ok(updatedRepair);
    }

    /**
     * 删除维修记录
     * DELETE /api/repairs/{rNo}
     * @param rNo 维修编号
     * @return 无内容
     */
    @DeleteMapping("/{rNo}")
    public ResponseEntity<Void> deleteRepair(@PathVariable String rNo) {
        carRepairService.deleteRepair(rNo);
        return ResponseEntity.noContent().build();
    }

    /**
     * 获取车辆的维修次数
     * GET /api/repairs/car/{carId}/count
     * @param carId 车辆号
     * @return 维修次数
     */
    @GetMapping("/car/{carId}/count")
    public ResponseEntity<Long> getRepairCountByCarId(@PathVariable String carId) {
        long count = carRepairService.getRepairCountByCarId(carId);
        return ResponseEntity.ok(count);
    }
}

