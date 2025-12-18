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
    public ResponseEntity<?> getAllRepairs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String carId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        // 解析时间字符串为 LocalDateTime（支持 YYYY-MM-DD 或 YYYY-MM-DDTHH:mm:ss）
        java.time.LocalDateTime start = null;
        java.time.LocalDateTime end = null;
        try {
            if (startDate != null && !startDate.isBlank()) {
                // 尝试解析为 LocalDate then start of day
                start = java.time.LocalDate.parse(startDate).atStartOfDay();
            }
            if (endDate != null && !endDate.isBlank()) {
                // 尝试解析 as LocalDate then end of day
                end = java.time.LocalDate.parse(endDate).atTime(23,59,59);
            }
        } catch (Exception ex) {
            // ignore parsing errors - treat as null
        }

        if (page < 0 || size <= 0) {
            // non-paged list
            java.util.List<com.example.backend.dto.CarRepairResponseDTO> repairs =
                    carRepairService.getRepairsByFilters(carId, start, end, null).getContent();
            return ResponseEntity.ok(repairs);
        } else {
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "rDate"));
            org.springframework.data.domain.Page<com.example.backend.dto.CarRepairResponseDTO> repairPage = carRepairService.getRepairsByFilters(carId, start, end, pageable);
            return ResponseEntity.ok(repairPage);
        }
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

    /**
     * 完成指定车辆的维修（填入维修完成时间并恢复车辆为可租状态）
     * POST /api/repairs/car/{carId}/complete
     * @param carId 车辆号
     * @param repairRequestDTO 包含完成时间的请求体
     * @return 更新后的维修记录
     */
    @PostMapping("/car/{carId}/complete")
    public ResponseEntity<CarRepairResponseDTO> completeRepair(
            @PathVariable String carId,
            @RequestBody CarRepairRequestDTO repairRequestDTO) {
        CarRepairResponseDTO dto = carRepairService.completeRepair(carId, repairRequestDTO.getRDate());
        return ResponseEntity.ok(dto);
    }
}
