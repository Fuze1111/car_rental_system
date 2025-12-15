package com.example.backend.controller;

import com.example.backend.dto.RentalRequestDTO;
import com.example.backend.dto.RentalResponseDTO;
import com.example.backend.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 租赁业务管理控制器
 * 提供租赁业务管理的RESTful API接口
 */
@RestController
@RequestMapping("/api/rentals")
@CrossOrigin(origins = "*")
public class RentalController {

    @Autowired
    private RentalService rentalService;

    /**
     * 获取所有租赁记录
     * GET /api/rentals
     * @return 租赁记录列表
     */
    @GetMapping
    public ResponseEntity<?> getAllRentals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "invoiceNo") String[] sortBy) {
        if (page < 0 || size <= 0) {
            List<RentalResponseDTO> rentals = rentalService.getAllRentals();
            return ResponseEntity.ok(rentals);
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
            Page<RentalResponseDTO> rentalPage = rentalService.getAllRentals(pageable);
            return ResponseEntity.ok(rentalPage);
        }
    }

    /**
     * 根据发票号获取租赁记录
     * GET /api/rentals/{invoiceNo}
     * @param invoiceNo 发票号
     * @return 租赁记录
     */
    @GetMapping("/{invoiceNo}")
    public ResponseEntity<RentalResponseDTO> getRentalById(@PathVariable String invoiceNo) {
        return rentalService.getRentalById(invoiceNo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 创建新租赁记录
     * POST /api/rentals
     * @param rentalRequestDTO 租赁请求DTO
     * @return 创建的租赁记录
     */
    @PostMapping
    public ResponseEntity<RentalResponseDTO> createRental(@RequestBody RentalRequestDTO rentalRequestDTO) {
        RentalResponseDTO createdRental = rentalService.createRental(rentalRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRental);
    }

    /**
     * 更新租赁记录
     * PUT /api/rentals/{invoiceNo}
     * @param invoiceNo 发票号
     * @param rentalRequestDTO 租赁请求DTO
     * @return 更新后的租赁记录
     */
    @PutMapping("/{invoiceNo}")
    public ResponseEntity<RentalResponseDTO> updateRental(
            @PathVariable String invoiceNo,
            @RequestBody RentalRequestDTO rentalRequestDTO) {
        RentalResponseDTO updatedRental = rentalService.updateRental(invoiceNo, rentalRequestDTO);
        return ResponseEntity.ok(updatedRental);
    }

    /**
     * 删除租赁记录
     * DELETE /api/rentals/{invoiceNo}
     * @param invoiceNo 发票号
     * @return 无内容
     */
    @DeleteMapping("/{invoiceNo}")
    public ResponseEntity<Void> deleteRental(@PathVariable String invoiceNo) {
        rentalService.deleteRental(invoiceNo);
        return ResponseEntity.noContent().build();
    }

    /**
     * 根据客户号查询租赁记录
     * GET /api/rentals/customer/{cNo}
     * @param cNo 客户号
     * @return 租赁记录列表
     */
    @GetMapping("/customer/{cNo}")
    public ResponseEntity<List<RentalResponseDTO>> getRentalsByCustomer(@PathVariable String cNo) {
        List<RentalResponseDTO> rentals = rentalService.getRentalsByCustomer(cNo);
        return ResponseEntity.ok(rentals);
    }

    /**
     * 根据司机号查询租赁记录
     * GET /api/rentals/driver/{dNo}
     * @param dNo 司机号
     * @return 租赁记录列表
     */
    @GetMapping("/driver/{dNo}")
    public ResponseEntity<List<RentalResponseDTO>> getRentalsByDriver(@PathVariable String dNo) {
        List<RentalResponseDTO> rentals = rentalService.getRentalsByDriver(dNo);
        return ResponseEntity.ok(rentals);
    }

    /**
     * 根据车辆ID查询租赁记录
     * GET /api/rentals/car/{carId}
     * @param carId 车辆ID
     * @return 租赁记录列表
     */
    @GetMapping("/car/{carId}")
    public ResponseEntity<List<RentalResponseDTO>> getRentalsByCar(@PathVariable String carId) {
        List<RentalResponseDTO> rentals = rentalService.getRentalsByCar(carId);
        return ResponseEntity.ok(rentals);
    }

    /**
     * 根据借车地点模糊查询租赁记录
     * GET /api/rentals/search/place?place=xxx
     * @param loanPlace 借车地点关键字
     * @return 租赁记录列表
     */
    @GetMapping("/search/place")
    public ResponseEntity<List<RentalResponseDTO>> searchRentalsByPlace(@RequestParam String loanPlace) {
        List<RentalResponseDTO> rentals = rentalService.searchRentalsByPlace(loanPlace);
        return ResponseEntity.ok(rentals);
    }

    /**
     * 根据日期范围查询租赁记录
     * GET /api/rentals/date-range?start=yyyy-MM-dd&end=yyyy-MM-dd
     * @param start 开始日期
     * @param end 结束日期
     * @return 租赁记录列表
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<RentalResponseDTO>> getRentalsByDateRange(
            @RequestParam(name = "start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(name = "end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        List<RentalResponseDTO> rentals = rentalService.getRentalsByDateRange(start, end);
        return ResponseEntity.ok(rentals);
    }

    /**
     * 根据金额范围查询租赁记录
     * GET /api/rentals/amount-range?min=xxx&max=xxx
     * @param min 最小金额
     * @param max 最大金额
     * @return 租赁记录列表
     */
    @GetMapping("/amount-range")
    public ResponseEntity<List<RentalResponseDTO>> getRentalsByAmountRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        List<RentalResponseDTO> rentals = rentalService.getRentalsByAmountRange(min, max);
        return ResponseEntity.ok(rentals);
    }

    /**
     * 多条件查询租赁记录
     * GET /api/rentals/search?cNo=xxx&dNo=xxx&carId=xxx&startDate=xxx&endDate=xxx
     * @param cNo 客户号（可选）
     * @param dNo 司机号（可选）
     * @param carId 车辆ID（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 租赁记录列表
     */
    @GetMapping("/search")
    public ResponseEntity<List<RentalResponseDTO>> searchRentals(
            @RequestParam(required = false) String cNo,
            @RequestParam(required = false) String dNo,
            @RequestParam(required = false) String carId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<RentalResponseDTO> rentals = rentalService.searchRentals(cNo, dNo, carId, startDate, endDate);
        return ResponseEntity.ok(rentals);
    }

    /**
     * 根据发票号模糊查询租赁记录
     * GET /api/rentals/search/invoice?invoiceNo=xxx
     * @param invoiceNo 发票号关键字
     * @return 租赁记录列表
     */
    @GetMapping("/search/invoice")
    public ResponseEntity<List<RentalResponseDTO>> searchRentalsByInvoiceNo(@RequestParam String invoiceNo) {
        List<RentalResponseDTO> rentals = rentalService.searchRentalsByInvoiceNo(invoiceNo);
        return ResponseEntity.ok(rentals);
    }

    /**
     * 获取租赁统计信息
     * GET /api/rentals/statistics
     * @return 统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getRentalStatistics() {
        Map<String, Object> statistics = rentalService.getRentalStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * 计算指定日期范围内的收入
     * GET /api/rentals/revenue?start=yyyy-MM-dd&end=yyyy-MM-dd
     * @param start 开始日期
     * @param end 结束日期
     * @return 收入金额
     */
    @GetMapping("/revenue")
    public ResponseEntity<Map<String, BigDecimal>> calculateRevenue(
            @RequestParam(name = "start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(name = "end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        BigDecimal revenue = rentalService.calculateRevenue(start, end);
        return ResponseEntity.ok(Map.of("revenue", revenue));
    }

    /**
     * 获取租赁趋势（按日/周/月/季度分组）
     * GET /api/rentals/trend?start=yyyy-MM-dd&end=yyyy-MM-dd&period=week
     * period: day | week | month | year
     */
    @GetMapping("/trend")
    public ResponseEntity<?> getRentalTrend(
            @RequestParam(name = "start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(name = "end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(name = "period", defaultValue = "day") String period
    ) {
        List<java.util.Map<String, Object>> trend = rentalService.getRentalTrend(start, end, period);
        return ResponseEntity.ok(trend);
    }

}