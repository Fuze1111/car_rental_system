package com.example.backend.service;

import com.example.backend.dto.RentalRequestDTO;
import com.example.backend.dto.RentalResponseDTO;
import com.example.backend.entity.Rental;
import com.example.backend.repository.RentalRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 租赁业务管理服务类
 * 提供租赁业务的业务逻辑处理
 */
@Service
@Transactional
public class RentalService {

    @Autowired
    private RentalRepository rentalRepository;

    /**
     * 获取所有租赁记录
     * @return 租赁记录列表
     */
    public List<RentalResponseDTO> getAllRentals() {
        List<Rental> rentals = rentalRepository.findAll();
        return rentals.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据发票号获取租赁记录
     * @param invoiceNo 发票号
     * @return 租赁记录
     */
    public Optional<RentalResponseDTO> getRentalById(String invoiceNo) {
        return rentalRepository.findById(invoiceNo)
                .map(this::convertToResponseDTO);
    }

    /**
     * 创建新租赁记录
     * @param rentalRequestDTO 租赁请求DTO
     * @return 创建的租赁记录
     */
    public RentalResponseDTO createRental(RentalRequestDTO rentalRequestDTO) {
        // 检查发票号是否已存在
        if (rentalRepository.existsById(rentalRequestDTO.getInvoiceNo())) {
            throw new RuntimeException("发票号已存在: " + rentalRequestDTO.getInvoiceNo());
        }

        Rental rental = convertToEntity(rentalRequestDTO);
        Rental savedRental = rentalRepository.save(rental);
        return convertToResponseDTO(savedRental);
    }

    /**
     * 更新租赁记录
     * @param invoiceNo 发票号
     * @param rentalRequestDTO 租赁请求DTO
     * @return 更新后的租赁记录
     */
    public RentalResponseDTO updateRental(String invoiceNo, RentalRequestDTO rentalRequestDTO) {
        Rental existingRental = rentalRepository.findById(invoiceNo)
                .orElseThrow(() -> new RuntimeException("租赁记录不存在: " + invoiceNo));

        // 更新租赁信息
        if (rentalRequestDTO.getDate() != null) {
            existingRental.setDate(rentalRequestDTO.getDate());
        }
        if (rentalRequestDTO.getPlace() != null) {
            existingRental.setPlace(rentalRequestDTO.getPlace());
        }
        if (rentalRequestDTO.getCarId() != null) {
            existingRental.setCarId(rentalRequestDTO.getCarId());
        }
        if (rentalRequestDTO.getCNo() != null) {
            existingRental.setCNo(rentalRequestDTO.getCNo());
        }
        if (rentalRequestDTO.getDNo() != null) {
            existingRental.setDNo(rentalRequestDTO.getDNo());
        }
        if (rentalRequestDTO.getRoundTripDistance() != null) {
            existingRental.setRoundTripDistance(rentalRequestDTO.getRoundTripDistance());
        }
        if (rentalRequestDTO.getDriveTime() != null) {
            existingRental.setDriveTime(rentalRequestDTO.getDriveTime());
        }
        if (rentalRequestDTO.getAmount() != null) {
            existingRental.setAmount(rentalRequestDTO.getAmount());
        }

        Rental updatedRental = rentalRepository.save(existingRental);
        return convertToResponseDTO(updatedRental);
    }

    /**
     * 删除租赁记录
     * @param invoiceNo 发票号
     */
    public void deleteRental(String invoiceNo) {
        if (!rentalRepository.existsById(invoiceNo)) {
            throw new RuntimeException("租赁记录不存在: " + invoiceNo);
        }
        rentalRepository.deleteById(invoiceNo);
    }

    /**
     * 根据客户号查询租赁记录
     * @param cNo 客户号
     * @return 租赁记录列表
     */
    public List<RentalResponseDTO> getRentalsByCustomer(String cNo) {
        List<Rental> rentals = rentalRepository.findByCNo(cNo);
        return rentals.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据司机号查询租赁记录
     * @param dNo 司机号
     * @return 租赁记录列表
     */
    public List<RentalResponseDTO> getRentalsByDriver(String dNo) {
        List<Rental> rentals = rentalRepository.findByDNo(dNo);
        return rentals.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据车辆ID查询租赁记录
     * @param carId 车辆ID
     * @return 租赁记录列表
     */
    public List<RentalResponseDTO> getRentalsByCar(String carId) {
        List<Rental> rentals = rentalRepository.findByCarId(carId);
        return rentals.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据地点模糊查询租赁记录
     * @param place 地点关键字
     * @return 租赁记录列表
     */
    public List<RentalResponseDTO> searchRentalsByPlace(String place) {
        List<Rental> rentals = rentalRepository.findByPlaceContaining(place);
        return rentals.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据日期范围查询租赁记录
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 租赁记录列表
     */
    public List<RentalResponseDTO> getRentalsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Rental> rentals = rentalRepository.findByDateBetween(startDate, endDate);
        return rentals.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据金额范围查询租赁记录
     * @param minAmount 最小金额
     * @param maxAmount 最大金额
     * @return 租赁记录列表
     */
    public List<RentalResponseDTO> getRentalsByAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {
        List<Rental> rentals = rentalRepository.findByAmountBetween(minAmount, maxAmount);
        return rentals.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 多条件查询租赁记录
     * @param cNo 客户号
     * @param dNo 司机号
     * @param carId 车辆ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 租赁记录列表
     */
    public List<RentalResponseDTO> searchRentals(String cNo, String dNo, String carId,
                                                   LocalDate startDate, LocalDate endDate) {
        List<Rental> rentals = rentalRepository.findByMultipleConditions(cNo, dNo, carId, startDate, endDate);
        return rentals.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取租赁统计信息
     * @return 统计信息Map
     */
    public Map<String, Object> getRentalStatistics() {
        long totalCount = rentalRepository.count();
        BigDecimal totalRevenue = rentalRepository.calculateTotalRevenue();

        // 计算平均金额
        double avgAmount = totalCount > 0 ? totalRevenue.doubleValue() / totalCount : 0.0;

        return Map.of(
            "totalRentals", totalCount,
            "totalRevenue", totalRevenue,
            "averageAmount", BigDecimal.valueOf(avgAmount).setScale(2, java.math.RoundingMode.HALF_UP)
        );
    }

    /**
     * 计算指定日期范围内的收入
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 收入金额
     */
    public BigDecimal calculateRevenue(LocalDate startDate, LocalDate endDate) {
        return rentalRepository.calculateRevenueByDateRange(startDate, endDate);
    }

    /**
     * 将Rental实体转换为RentalResponseDTO
     */
    private RentalResponseDTO convertToResponseDTO(Rental rental) {
        RentalResponseDTO dto = new RentalResponseDTO();
        dto.setInvoiceNo(rental.getInvoiceNo());
        dto.setDate(rental.getDate());
        dto.setPlace(rental.getPlace());
        dto.setCarId(rental.getCarId());
        dto.setCNo(rental.getCNo());
        dto.setDNo(rental.getDNo());
        dto.setRoundTripDistance(rental.getRoundTripDistance());
        dto.setDriveTime(rental.getDriveTime());
        dto.setAmount(rental.getAmount());

        // 设置关联信息
        if (rental.getCustomer() != null) {
            dto.setCustomerName(rental.getCustomer().getCName());
        }
        if (rental.getDriver() != null) {
            dto.setDriverName(rental.getDriver().getDName());
        }
        if (rental.getCar() != null && rental.getCar().getTName() != null) {
            dto.setCarType(rental.getCar().getTName());
        }

        return dto;
    }

    /**
     * 将RentalRequestDTO转换为Rental实体
     */
    private Rental convertToEntity(RentalRequestDTO dto) {
        Rental rental = new Rental();
        rental.setInvoiceNo(dto.getInvoiceNo());
        rental.setDate(dto.getDate());
        rental.setPlace(dto.getPlace());
        rental.setCarId(dto.getCarId());
        rental.setCNo(dto.getCNo());
        rental.setDNo(dto.getDNo());
        rental.setRoundTripDistance(dto.getRoundTripDistance());
        rental.setDriveTime(dto.getDriveTime());
        rental.setAmount(dto.getAmount());
        return rental;
    }
}

