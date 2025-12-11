package com.example.backend.service;

import com.example.backend.dto.DriverRequestDTO;
import com.example.backend.dto.DriverResponseDTO;
import com.example.backend.entity.Driver;
import com.example.backend.repository.DriverRepository;
import com.example.backend.repository.RentalRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 司机信息管理服务类
 * 提供司机信息的业务逻辑处理
 */
@Service
@Transactional
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private RentalRepository rentalRepository;

    /**
     * 获取所有司机信息
     * @return 司机信息列表
     */
    public List<DriverResponseDTO> getAllDrivers() {
        List<Driver> drivers = driverRepository.findAll();
        return drivers.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 分页获取所有司机信息
     * @param pageable 分页参数
     * @return 司机信息分页结果
     */
    public Page<DriverResponseDTO> getAllDrivers(Pageable pageable) {
        Page<Driver> driverPage = driverRepository.findAll(pageable);
        List<DriverResponseDTO> driverDTOs = driverPage.getContent().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(driverDTOs, driverPage.getPageable(), driverPage.getTotalElements());
    }

    /**
     * 根据司机号获取司机信息
     * @param dNo 司机号
     * @return 司机信息
     */
    public Optional<DriverResponseDTO> getDriverById(String dNo) {
        return driverRepository.findById(dNo)
                .map(this::convertToResponseDTO);
    }

    /**
     * 创建新司机
     * @param driverRequestDTO 司机请求DTO
     * @return 创建的司机信息
     */
    public DriverResponseDTO createDriver(DriverRequestDTO driverRequestDTO) {
        // 检查司机号是否已存在
        if (driverRepository.existsById(driverRequestDTO.getDNo())) {
            throw new RuntimeException("司机号已存在: " + driverRequestDTO.getDNo());
        }

        Driver driver = convertToEntity(driverRequestDTO);
        Driver savedDriver = driverRepository.save(driver);
        return convertToResponseDTO(savedDriver);
    }

    /**
     * 更新司机信息
     * @param dNo 司机号
     * @param driverRequestDTO 司机请求DTO
     * @return 更新后的司机信息
     */
    public DriverResponseDTO updateDriver(String dNo, DriverRequestDTO driverRequestDTO) {
        Driver existingDriver = driverRepository.findById(dNo)
                .orElseThrow(() -> new RuntimeException("司机不存在: " + dNo));

        // 更新司机信息
        if (driverRequestDTO.getDName() != null) {
            existingDriver.setDName(driverRequestDTO.getDName());
        }
        if (driverRequestDTO.getStartWork() != null) {
            existingDriver.setStartWork(driverRequestDTO.getStartWork());
        }
        if (driverRequestDTO.getSalary() != null) {
            existingDriver.setSalary(driverRequestDTO.getSalary());
        }
        if (driverRequestDTO.getDStatus() != null) {
            existingDriver.setDStatus(driverRequestDTO.getDStatus());
        }
        if (driverRequestDTO.getLicenseType() != null) {
            existingDriver.setLicenseType(driverRequestDTO.getLicenseType());
        }

        Driver updatedDriver = driverRepository.save(existingDriver);
        return convertToResponseDTO(updatedDriver);
    }

    /**
     * 删除司机
     * @param dNo 司机号
     */
    public void deleteDriver(String dNo) {
        if (!driverRepository.existsById(dNo)) {
            throw new RuntimeException("司机不存在: " + dNo);
        }
        driverRepository.deleteById(dNo);
    }

    /**
     * 根据司机状态查询司机
     * @param dStatus 司机状态
     * @return 司机列表
     */
    public List<DriverResponseDTO> getDriversByStatus(String dStatus) {
        List<Driver> drivers = driverRepository.findByDStatus(dStatus);
        return drivers.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据准驾车型查询司机
     * @param licenseType 准驾车型
     * @return 司机列表
     */
    public List<DriverResponseDTO> getDriversByLicenseType(String licenseType) {
        List<Driver> drivers = driverRepository.findByLicenseType(licenseType);
        return drivers.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据姓名模糊查询司机
     * @param dName 姓名关键字
     * @return 司机列表
     */
    public List<DriverResponseDTO> searchDriversByName(String dName) {
        List<Driver> drivers = driverRepository.findByDNameContaining(dName);
        return drivers.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 多条件查询司机
     * @param dStatus 司机状态
     * @param licenseType 准驾车型
     * @param minSalary 最低工资
     * @param maxSalary 最高工资
     * @return 司机列表
     */
    public List<DriverResponseDTO> searchDrivers(String dStatus, String licenseType,
                                                  BigDecimal minSalary, BigDecimal maxSalary) {
        List<Driver> drivers = driverRepository.findByMultipleConditions(dStatus, licenseType, minSalary, maxSalary);
        return drivers.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据司机号模糊查询司机
     * @param dNo 司机号关键字
     * @return 司机列表
     */
    public List<DriverResponseDTO> searchDriversByDNo(String dNo) {
        List<Driver> drivers = driverRepository.findByDNoContaining(dNo);
        return drivers.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 更新司机状态
     * @param dNo 司机号
     * @param dStatus 新状态
     * @return 更新后的司机信息
     */
    public DriverResponseDTO updateDriverStatus(String dNo, String dStatus) {
        Driver driver = driverRepository.findById(dNo)
                .orElseThrow(() -> new RuntimeException("司机不存在: " + dNo));

        driver.setDStatus(dStatus);
        Driver updatedDriver = driverRepository.save(driver);
        return convertToResponseDTO(updatedDriver);
    }

    /**
     * 更新司机工资
     * @param dNo 司机号
     * @param salary 新工资
     * @return 更新后的司机信息
     */
    public DriverResponseDTO updateDriverSalary(String dNo, BigDecimal salary) {
        Driver driver = driverRepository.findById(dNo)
                .orElseThrow(() -> new RuntimeException("司机不存在: " + dNo));

        driver.setSalary(salary);
        Driver updatedDriver = driverRepository.save(driver);
        return convertToResponseDTO(updatedDriver);
    }

    /**
     * 根据工资范围查询司机
     * @param minSalary 最低工资
     * @param maxSalary 最高工资
     * @return 司机列表
     */
    public List<DriverResponseDTO> getDriversBySalaryRange(BigDecimal minSalary, BigDecimal maxSalary) {
        List<Driver> drivers = driverRepository.findBySalaryBetween(minSalary, maxSalary);
        return drivers.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据工龄查询司机（入职时间在指定日期之前）
     * @param years 工龄（年）
     * @return 司机列表
     */
    public List<DriverResponseDTO> getDriversByWorkYears(int years) {
        LocalDate date = LocalDate.now().minusYears(years);
        List<Driver> drivers = driverRepository.findByStartWorkBefore(date);
        return drivers.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取司机统计信息
     * @return 统计信息Map
     */
    public Map<String, Object> getDriverStatistics() {
        long totalCount = driverRepository.count();

        // 统计各状态司机数量（假设状态为：在岗、空闲）
        long onDutyCount = driverRepository.countByDStatus("在岗");
        long freeCount = driverRepository.countByDStatus("空闲");

        // 计算平均工资
        List<Driver> allDrivers = driverRepository.findAll();
        double avgSalary = allDrivers.stream()
                .filter(d -> d.getSalary() != null)
                .mapToDouble(d -> d.getSalary().doubleValue())
                .average()
                .orElse(0.0);

        return Map.of(
            "total", totalCount,
            "onDuty", onDutyCount,
            "free", freeCount,
            "averageSalary", BigDecimal.valueOf(avgSalary).setScale(2, RoundingMode.HALF_UP)
        );
    }

    /**
     * 将Driver实体转换为DriverResponseDTO
     */
    private DriverResponseDTO convertToResponseDTO(Driver driver) {
        DriverResponseDTO dto = new DriverResponseDTO();
        dto.setDNo(driver.getDNo());
        dto.setDName(driver.getDName());
        dto.setStartWork(driver.getStartWork());
        dto.setSalary(driver.getSalary());
        dto.setDStatus(driver.getDStatus());
        dto.setLicenseType(driver.getLicenseType());

        // 计算工龄
        if (driver.getStartWork() != null) {
            Period period = Period.between(driver.getStartWork(), LocalDate.now());
            dto.setWorkYears(period.getYears());
            dto.setWorkMonths(period.getMonths());
        }

        // 判断是否在岗
        dto.setIsOnDuty("在岗".equals(driver.getDStatus()));

        return dto;
    }

    /**
     * 管理员专用：分页多条件搜索司机
     * @param name 姓名关键字
     * @param status 状态
     * @param licenseType 准驾车型
     * @param pageable 分页参数
     * @return 分页结果
     */
    public Page<DriverResponseDTO> searchDriversWithPage(String name, String status, String licenseType, Pageable pageable) {
        Page<Driver> driverPage = driverRepository.searchDrivers(name, status, licenseType, pageable);
        List<DriverResponseDTO> dtoList = driverPage.getContent().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, driverPage.getTotalElements());
    }

    /**
     * 将DriverRequestDTO转换为Driver实体
     */
    private Driver convertToEntity(DriverRequestDTO dto) {
        Driver driver = new Driver();
        driver.setDNo(dto.getDNo());
        driver.setDName(dto.getDName());
        driver.setStartWork(dto.getStartWork());
        driver.setSalary(dto.getSalary());
        driver.setDStatus(dto.getDStatus() != null ? dto.getDStatus() : "空闲"); // 默认空闲
        driver.setLicenseType(dto.getLicenseType());
        return driver;
    }
}