package com.example.backend.controller;

import com.example.backend.dto.DriverRequestDTO;
import com.example.backend.dto.DriverResponseDTO;
import com.example.backend.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 司机信息管理控制器
 * 提供司机信息管理的RESTful API接口
 */
@RestController
@RequestMapping("/api/drivers")
@CrossOrigin(origins = "*")
public class DriverController {

    @Autowired
    private DriverService driverService;

    /**
     * 获取所有司机信息
     * GET /api/drivers
     * @return 司机列表
     */
    @GetMapping
    public ResponseEntity<List<DriverResponseDTO>> getAllDrivers() {
        List<DriverResponseDTO> drivers = driverService.getAllDrivers();
        return ResponseEntity.ok(drivers);
    }

    /**
     * 根据司机号获取司机信息
     * GET /api/drivers/{dNo}
     * @param dNo 司机号
     * @return 司机信息
     */
    @GetMapping("/{dNo}")
    public ResponseEntity<DriverResponseDTO> getDriverById(@PathVariable String dNo) {
        return driverService.getDriverById(dNo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 创建新司机
     * POST /api/drivers
     * @param driverRequestDTO 司机请求DTO
     * @return 创建的司机信息
     */
    @PostMapping
    public ResponseEntity<DriverResponseDTO> createDriver(@RequestBody DriverRequestDTO driverRequestDTO) {
        DriverResponseDTO createdDriver = driverService.createDriver(driverRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDriver);
    }

    /**
     * 更新司机信息
     * PUT /api/drivers/{dNo}
     * @param dNo 司机号
     * @param driverRequestDTO 司机请求DTO
     * @return 更新后的司机信息
     */
    @PutMapping("/{dNo}")
    public ResponseEntity<DriverResponseDTO> updateDriver(
            @PathVariable String dNo,
            @RequestBody DriverRequestDTO driverRequestDTO) {
        DriverResponseDTO updatedDriver = driverService.updateDriver(dNo, driverRequestDTO);
        return ResponseEntity.ok(updatedDriver);
    }

    /**
     * 删除司机
     * DELETE /api/drivers/{dNo}
     * @param dNo 司机号
     * @return 无内容
     */
    @DeleteMapping("/{dNo}")
    public ResponseEntity<Void> deleteDriver(@PathVariable String dNo) {
        driverService.deleteDriver(dNo);
        return ResponseEntity.noContent().build();
    }

    /**
     * 根据司机状态查询司机
     * GET /api/drivers/status/{dStatus}
     * @param dStatus 司机状态
     * @return 司机列表
     */
    @GetMapping("/status/{dStatus}")
    public ResponseEntity<List<DriverResponseDTO>> getDriversByStatus(@PathVariable String dStatus) {
        List<DriverResponseDTO> drivers = driverService.getDriversByStatus(dStatus);
        return ResponseEntity.ok(drivers);
    }

    /**
     * 根据准驾车型查询司机
     * GET /api/drivers/license/{licenseType}
     * @param licenseType 准驾车型
     * @return 司机列表
     */
    @GetMapping("/license/{licenseType}")
    public ResponseEntity<List<DriverResponseDTO>> getDriversByLicenseType(@PathVariable String licenseType) {
        List<DriverResponseDTO> drivers = driverService.getDriversByLicenseType(licenseType);
        return ResponseEntity.ok(drivers);
    }

    /**
     * 根据姓名模糊查询司机
     * GET /api/drivers/search/name?dName=xxx
     * @param dName 姓名关键字
     * @return 司机列表
     */
    @GetMapping("/search/name")
    public ResponseEntity<List<DriverResponseDTO>> searchDriversByName(@RequestParam String dName) {
        List<DriverResponseDTO> drivers = driverService.searchDriversByName(dName);
        return ResponseEntity.ok(drivers);
    }

    /**
     * 多条件查询司机
     * GET /api/drivers/search?dStatus=xxx&licenseType=xxx&minSalary=xxx&maxSalary=xxx
     * @param dStatus 司机状态（可选）
     * @param licenseType 准驾车型（可选）
     * @param minSalary 最低工资（可选）
     * @param maxSalary 最高工资（可选）
     * @return 司机列表
     */
    @GetMapping("/search")
    public ResponseEntity<List<DriverResponseDTO>> searchDrivers(
            @RequestParam(required = false) String dStatus,
            @RequestParam(required = false) String licenseType,
            @RequestParam(required = false) BigDecimal minSalary,
            @RequestParam(required = false) BigDecimal maxSalary) {
        List<DriverResponseDTO> drivers = driverService.searchDrivers(dStatus, licenseType, minSalary, maxSalary);
        return ResponseEntity.ok(drivers);
    }

    /**
     * 根据工资范围查询司机
     * GET /api/drivers/salary?min=xxx&max=xxx
     * @param min 最低工资
     * @param max 最高工资
     * @return 司机列表
     */
    @GetMapping("/salary")
    public ResponseEntity<List<DriverResponseDTO>> getDriversBySalaryRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        List<DriverResponseDTO> drivers = driverService.getDriversBySalaryRange(min, max);
        return ResponseEntity.ok(drivers);
    }

    /**
     * 根据工龄查询司机
     * GET /api/drivers/work-years/{years}
     * @param years 最少工龄（年）
     * @return 司机列表
     */
    @GetMapping("/work-years/{years}")
    public ResponseEntity<List<DriverResponseDTO>> getDriversByWorkYears(@PathVariable int years) {
        List<DriverResponseDTO> drivers = driverService.getDriversByWorkYears(years);
        return ResponseEntity.ok(drivers);
    }

    /**
     * 更新司机状态
     * PATCH /api/drivers/{dNo}/status
     * @param dNo 司机号
     * @param statusRequest 包含新状态的请求体
     * @return 更新后的司机信息
     */
    @PatchMapping("/{dNo}/status")
    public ResponseEntity<DriverResponseDTO> updateDriverStatus(
            @PathVariable String dNo,
            @RequestBody Map<String, String> statusRequest) {
        String newStatus = statusRequest.get("dStatus");
        DriverResponseDTO updatedDriver = driverService.updateDriverStatus(dNo, newStatus);
        return ResponseEntity.ok(updatedDriver);
    }

    /**
     * 更新司机工资
     * PATCH /api/drivers/{dNo}/salary
     * @param dNo 司机号
     * @param salaryRequest 包含新工资的请求体
     * @return 更新后的司机信息
     */
    @PatchMapping("/{dNo}/salary")
    public ResponseEntity<DriverResponseDTO> updateDriverSalary(
            @PathVariable String dNo,
            @RequestBody Map<String, BigDecimal> salaryRequest) {
        BigDecimal newSalary = salaryRequest.get("salary");
        DriverResponseDTO updatedDriver = driverService.updateDriverSalary(dNo, newSalary);
        return ResponseEntity.ok(updatedDriver);
    }

    /**
     * 获取司机统计信息
     * GET /api/drivers/statistics
     * @return 统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getDriverStatistics() {
        Map<String, Object> statistics = driverService.getDriverStatistics();
        return ResponseEntity.ok(statistics);
    }
}

