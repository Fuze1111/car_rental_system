package com.example.backend.service;

import com.example.backend.dto.RentalRequestDTO;
import com.example.backend.dto.RentalResponseDTO;
import com.example.backend.entity.Car;
import com.example.backend.entity.CarType;
import com.example.backend.entity.Driver;
import com.example.backend.entity.Rental;
import com.example.backend.repository.CarRepository;
import com.example.backend.repository.CarTypeRepository;
import com.example.backend.repository.DriverRepository;
import com.example.backend.repository.RentalRepository;
import com.example.backend.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
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

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarTypeRepository carTypeRepository;

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
     * 分页获取所有租赁记录
     * @param pageable 分页参数
     * @return 租赁记录分页结果
     */
    public Page<RentalResponseDTO> getAllRentals(Pageable pageable) {
        Page<Rental> rentalPage = rentalRepository.findAll(pageable);
        List<RentalResponseDTO> rentalDTOs = rentalPage.getContent().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(rentalDTOs, rentalPage.getPageable(), rentalPage.getTotalElements());
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
        // 打印详细调试信息
        System.out.println("========== 接收到租赁请求 ==========");
        System.out.println("Received rentalRequestDTO: " + rentalRequestDTO);
        System.out.println("InvoiceNo: " + rentalRequestDTO.getInvoiceNo());
        System.out.println("CarId: " + rentalRequestDTO.getCarId());
        System.out.println("CNo: " + rentalRequestDTO.getCNo());
        System.out.println("DNo: " + rentalRequestDTO.getDNo());
        System.out.println("LoanDate: " + rentalRequestDTO.getLoanDate());
        System.out.println("ReturnDate: " + rentalRequestDTO.getReturnDate());
        System.out.println("LoanPlace: " + rentalRequestDTO.getLoanPlace());
        System.out.println("ReturnPlace: " + rentalRequestDTO.getReturnPlace());
        System.out.println("RoundTripDistance: " + rentalRequestDTO.getRoundTripDistance());
        System.out.println("DriveTime: " + rentalRequestDTO.getDriveTime());
        System.out.println("Amount: " + rentalRequestDTO.getAmount());
        System.out.println("=====================================");
        
        // 自动生成发票号（基于数据库最大值+1）
        String invoiceNo = generateInvoiceNo();
        rentalRequestDTO.setInvoiceNo(invoiceNo);
        System.out.println("Generated InvoiceNo: " + invoiceNo);

        // 验证客户是否存在
        if (rentalRequestDTO.getCNo() != null && !rentalRequestDTO.getCNo().isEmpty()) {
            customerRepository.findById(rentalRequestDTO.getCNo())
                    .orElseThrow(() -> new RuntimeException("客户不存在: " + rentalRequestDTO.getCNo()));
        }

        // 验证车辆是否存在
        if (rentalRequestDTO.getCarId() == null || rentalRequestDTO.getCarId().trim().isEmpty()) {
            throw new RuntimeException("车辆ID不能为空");
        }
        
        Car car = carRepository.findById(rentalRequestDTO.getCarId().trim())
                .orElseThrow(() -> new RuntimeException("车辆不存在: " + rentalRequestDTO.getCarId().trim()));
        
        // 检查车辆状态是否为可租(0)
        if (!"0".equals(car.getCarStatus())) {
            throw new RuntimeException("车辆状态不是可租状态: " + car.getCarStatus());
        }
        
        // 验证司机（如果提供了司机ID）
        // dNo为null或空字符串表示自驾，无需司机
        Driver driver = null;
        if (rentalRequestDTO.getDNo() != null && !rentalRequestDTO.getDNo().trim().isEmpty()) {
            String trimmedDNo = rentalRequestDTO.getDNo().trim();
            driver = driverRepository.findById(trimmedDNo)
                    .orElseThrow(() -> new RuntimeException("司机不存在: " + trimmedDNo));

            // 检查司机状态是否为空闲
            if (!"空闲".equals(driver.getDStatus())) {
                throw new RuntimeException("司机状态不是空闲状态: " + driver.getDStatus());
            }
        }

        Rental rental = convertToEntity(rentalRequestDTO);
        // 确保ID已正确设置
        if (rental.getInvoiceNo() == null) {
            throw new RuntimeException("租赁记录ID不能为空");
        }
        
        // 订单创建时，强制设置以下字段为null（还车时由管理员填写）
        rental.setRoundTripDistance(null);
        rental.setDriveTime(null);
        rental.setAmount(null);
        
        Rental savedRental = rentalRepository.save(rental);
        
        // 更新车辆状态为已租(1)
        car.setCarStatus("1");
        carRepository.save(car);
        
        // 更新司机状态为在岗（仅当有司机时）
        if (driver != null) {
            driver.setDStatus("在岗");
            driverRepository.save(driver);
        }
        
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

        // 如果更换了车辆，则恢复原车辆状态，设置新车辆状态
        if (rentalRequestDTO.getCarId() != null && !rentalRequestDTO.getCarId().equals(existingRental.getCarId())) {
            // 恢复原车辆状态
            if (existingRental.getCarId() != null) {
                Car oldCar = carRepository.findById(existingRental.getCarId())
                        .orElse(null);
                if (oldCar != null) {
                    oldCar.setCarStatus("0"); // 设置为可租
                    carRepository.save(oldCar);
                }
            }
            
            // 设置新车辆状态
            Car newCar = carRepository.findById(rentalRequestDTO.getCarId())
                    .orElseThrow(() -> new RuntimeException("车辆不存在: " + rentalRequestDTO.getCarId()));
            if (!"0".equals(newCar.getCarStatus())) {
                throw new RuntimeException("新车辆状态不是可租状态: " + newCar.getCarStatus());
            }
            newCar.setCarStatus("1"); // 设置为已租
            carRepository.save(newCar);
        }

        // 如果更换了司机，则恢复原司机状态，设置新司机状态
        if (rentalRequestDTO.getDNo() != null && !rentalRequestDTO.getDNo().equals(existingRental.getDNo())) {
            // 恢复原司机状态
            if (existingRental.getDNo() != null) {
                Driver oldDriver = driverRepository.findById(existingRental.getDNo())
                        .orElse(null);
                if (oldDriver != null) {
                    oldDriver.setDStatus("空闲"); // 设置为空闲
                    driverRepository.save(oldDriver);
                }
            }
            
            // 设置新司机状态
            Driver newDriver = driverRepository.findById(rentalRequestDTO.getDNo())
                    .orElseThrow(() -> new RuntimeException("司机不存在: " + rentalRequestDTO.getDNo()));
            if (!"空闲".equals(newDriver.getDStatus())) {
                throw new RuntimeException("新司机状态不是空闲状态: " + newDriver.getDStatus());
            }
            newDriver.setDStatus("在岗"); // 设置为在岗
            driverRepository.save(newDriver);
        }

        // 更新租赁信息
        if (rentalRequestDTO.getLoanDate() != null) {
            existingRental.setLoanDate(rentalRequestDTO.getLoanDate());
        }
        if (rentalRequestDTO.getReturnDate() != null) {
            existingRental.setReturnDate(rentalRequestDTO.getReturnDate());
        }
        if (rentalRequestDTO.getLoanPlace() != null) {
            existingRental.setLoanPlace(rentalRequestDTO.getLoanPlace());
        }
        if (rentalRequestDTO.getReturnPlace() != null) {
            existingRental.setReturnPlace(rentalRequestDTO.getReturnPlace());
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
        
        // 自动计算金额
        existingRental.setAmount(calculateRentalAmount(existingRental));

        Rental updatedRental = rentalRepository.save(existingRental);
        return convertToResponseDTO(updatedRental);
    }

    /**
     * 删除租赁记录
     * @param invoiceNo 发票号
     */
    public void deleteRental(String invoiceNo) {
        Rental rental = rentalRepository.findById(invoiceNo)
                .orElseThrow(() -> new RuntimeException("租赁记录不存在: " + invoiceNo));
        
        // 查找关联的车辆和司机
        Car car = carRepository.findById(rental.getCarId())
                .orElse(null);
        
        Driver driver = driverRepository.findById(rental.getDNo())
                .orElse(null);

        // 删除租赁记录
        rentalRepository.deleteById(invoiceNo);
        
        // 恢复车辆状态为可租(0)
        if (car != null) {
            car.setCarStatus("0");
            carRepository.save(car);
        }
        
        // 恢复司机状态为空闲
        if (driver != null) {
            driver.setDStatus("空闲");
            driverRepository.save(driver);
        }
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
     * 根据借车地点模糊查询租赁记录
     * @param loanPlace 借车地点关键字
     * @return 租赁记录列表
     */
    public List<RentalResponseDTO> searchRentalsByPlace(String loanPlace) {
        List<Rental> rentals = rentalRepository.findByLoanPlaceContaining(loanPlace);
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
        // convert LocalDate to LocalDateTime covering the full day to match entity fields
        java.time.LocalDateTime startDateTime = startDate.atStartOfDay();
        java.time.LocalDateTime endDateTime = endDate.atTime(java.time.LocalTime.MAX);
        List<Rental> rentals = rentalRepository.findByDateBetween(startDateTime, endDateTime);
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
        // convert optional LocalDate to LocalDateTime range (startOfDay / endOfDay) for repository
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;
        if (startDate != null) startDateTime = startDate.atStartOfDay();
        if (endDate != null) endDateTime = endDate.atTime(LocalTime.MAX);
        List<Rental> rentals = rentalRepository.findByMultipleConditions(cNo, dNo, carId, startDateTime, endDateTime);
        return rentals.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据发票号模糊查询租赁记录
     * @param invoiceNo 发票号关键字
     * @return 租赁记录列表
     */
    public List<RentalResponseDTO> searchRentalsByInvoiceNo(String invoiceNo) {
        List<Rental> rentals = rentalRepository.findByInvoiceNoContaining(invoiceNo);
        return rentals.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取租赁统计信息
     * @return 统计信息Map
     */
    public java.util.Map<String, Object> getRentalStatistics() {
        long totalCount = rentalRepository.count();
        BigDecimal totalRevenue = rentalRepository.calculateTotalRevenue();

        // 计算平均金额
        double avgAmount = totalCount > 0 ? totalRevenue.doubleValue() / totalCount : 0.0;

        return java.util.Map.of(
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
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        return rentalRepository.calculateRevenueByDateRange(startDateTime, endDateTime);
    }

    /**
     * 获取租赁趋势数据（按 period 分组）
     * period: day | week | month | year
     * 返回列表，每项为 map 包含: label (String), orders (int), revenue (BigDecimal)
     */
    public List<java.util.Map<String, Object>> getRentalTrend(LocalDate startDate, LocalDate endDate, String period) {
        // normalize
        if (startDate == null || endDate == null) return java.util.Collections.emptyList();
        if (startDate.isAfter(endDate)) {
            LocalDate tmp = startDate; startDate = endDate; endDate = tmp;
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Rental> rentals = rentalRepository.findByDateBetween(startDateTime, endDateTime);

        List<java.util.Map<String, Object>> buckets = new java.util.ArrayList<>();

        String p = period == null ? "day" : period.toLowerCase();

        switch (p) {
            case "month": {
                // 4 weekly buckets: W1..W4
                for (int i = 1; i <= 4; i++) {
                    java.util.Map<String, Object> m = new java.util.HashMap<>();
                    m.put("label", "W" + i);
                    m.put("orders", 0);
                    m.put("revenue", BigDecimal.ZERO);
                    buckets.add(m);
                }
                // assign by day-of-month to buckets
                for (Rental r : rentals) {
                    if (r.getLoanDate() == null) continue;
                    LocalDate d = r.getLoanDate().toLocalDate();
                    if (d.isBefore(startDate) || d.isAfter(endDate)) continue;
                    int day = d.getDayOfMonth();
                    int idx = Math.min(3, (day - 1) / 7); // 0..3
                    java.util.Map<String, Object> m = buckets.get(idx);
                    int cnt = (int) m.get("orders") + 1;
                    BigDecimal rev = (BigDecimal) m.get("revenue");
                    rev = rev.add(r.getAmount() == null ? BigDecimal.ZERO : r.getAmount());
                    m.put("orders", cnt);
                    m.put("revenue", rev);
                }
                break;
            }
            case "year": {
                // quarters Q1..Q4
                for (int i = 1; i <= 4; i++) {
                    java.util.Map<String, Object> m = new java.util.HashMap<>();
                    m.put("label", "Q" + i);
                    m.put("orders", 0);
                    m.put("revenue", BigDecimal.ZERO);
                    buckets.add(m);
                }
                for (Rental r : rentals) {
                    if (r.getLoanDate() == null) continue;
                    LocalDate d = r.getLoanDate().toLocalDate();
                    if (d.isBefore(startDate) || d.isAfter(endDate)) continue;
                    int month = d.getMonthValue();
                    int idx = (month - 1) / 3; // 0..3
                    java.util.Map<String, Object> m = buckets.get(idx);
                    int cnt = (int) m.get("orders") + 1;
                    BigDecimal rev = (BigDecimal) m.get("revenue");
                    rev = rev.add(r.getAmount() == null ? BigDecimal.ZERO : r.getAmount());
                    m.put("orders", cnt);
                    m.put("revenue", rev);
                }
                break;
            }
            default: {
                // day/week -> per day buckets
                java.time.LocalDate d = startDate;
                java.util.Map<String, java.util.Map<String, Object>> map = new java.util.LinkedHashMap<>();
                while (!d.isAfter(endDate)) {
                    java.util.Map<String, Object> m = new java.util.HashMap<>();
                    m.put("label", d.toString());
                    m.put("orders", 0);
                    m.put("revenue", BigDecimal.ZERO);
                    map.put(d.toString(), m);
                    d = d.plusDays(1);
                }
                // assign rentals
                for (Rental r : rentals) {
                    if (r.getLoanDate() == null) continue;
                    LocalDate ld = r.getLoanDate().toLocalDate();
                    if (ld.isBefore(startDate) || ld.isAfter(endDate)) continue;
                    String key = ld.toString();
                    java.util.Map<String, Object> m = map.get(key);
                    if (m == null) continue;
                    int cnt = (int) m.get("orders") + 1;
                    BigDecimal rev = (BigDecimal) m.get("revenue");
                    rev = rev.add(r.getAmount() == null ? BigDecimal.ZERO : r.getAmount());
                    m.put("orders", cnt);
                    m.put("revenue", rev);
                }
                buckets.addAll(map.values());
                break;
            }
        }

        return buckets;
    }
    
    /**
     * 自动生成发票号（基于数据库最大值+1）
     * 格式：INV + 7位数字（0000001-9999999）
     * @return 新的发票号
     */
    private String generateInvoiceNo() {
        String maxInvoiceNo = rentalRepository.findMaxInvoiceNo();
        
        if (maxInvoiceNo == null || maxInvoiceNo.isEmpty()) {
            // 如果数据库为空，从INV0000001开始
            return "INV0000001";
        }
        
        try {
            // 提取数字部分（格式为INVxxxxxxx，3个字符后是7位数字）
            String numberPart = maxInvoiceNo.substring(3);
            int nextNumber = Integer.parseInt(numberPart) + 1;
            
            // 格式化为7位数字，不足补0
            return String.format("INV%07d", nextNumber);
        } catch (Exception e) {
            // 如果解析失败，返回默认值
            System.err.println("Failed to parse invoice number: " + maxInvoiceNo);
            return "INV0000001";
        }
    }

    /**
     * 根据租赁信息自动计算金额
     * 计算公式：amount = 每小时费用 × 时间差 + (每小时费用 ÷ 50) × 公里数
     * @param rental 租赁实体
     * @return 计算出的金额
     */
    private BigDecimal calculateRentalAmount(Rental rental) {
        // 检查必要字段是否存在
        if (rental.getCarId() == null || rental.getLoanDate() == null || rental.getReturnDate() == null) {
            return BigDecimal.ZERO;
        }

        // 获取车辆信息
        Car car = carRepository.findById(rental.getCarId()).orElse(null);
        if (car == null) {
            System.err.println("警告: 未找到车辆ID为 " + rental.getCarId() + " 的车辆信息");
            return BigDecimal.ZERO;
        }
        
        if (car.getCarType() == null) {
            System.err.println("警告: 车辆ID为 " + rental.getCarId() + " 的车辆类型信息为空");
            return BigDecimal.ZERO;
        }

        // 获取每小时费用
        BigDecimal hourlyRate = car.getCarType().getRentRate();
        if (hourlyRate == null) {
            System.err.println("警告: 车辆类型 " + car.getCarType().getTName() + " 的租金率为空");
            return BigDecimal.ZERO;
        }

        // 计算时间差（小时）
        long hoursDiff = java.time.Duration.between(rental.getLoanDate(), rental.getReturnDate()).toHours();
        BigDecimal timeCost = hourlyRate.multiply(BigDecimal.valueOf(hoursDiff));

        // 计算里程费用（每小时费用 ÷ 50 × 公里数）
        BigDecimal kilometerRate = hourlyRate.divide(BigDecimal.valueOf(50), 2, java.math.RoundingMode.HALF_UP);
        BigDecimal distanceCost = BigDecimal.ZERO;
        if (rental.getRoundTripDistance() != null) {
            distanceCost = kilometerRate.multiply(rental.getRoundTripDistance());
        }

        // 总费用 = 时间费用 + 里程费用
        BigDecimal totalAmount = timeCost.add(distanceCost);
        return totalAmount.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * 将Rental实体转换为RentalResponseDTO
     */
    private RentalResponseDTO convertToResponseDTO(Rental rental) {
        RentalResponseDTO dto = new RentalResponseDTO();
        dto.setInvoiceNo(rental.getInvoiceNo());
        dto.setLoanDate(rental.getLoanDate());
        dto.setReturnDate(rental.getReturnDate());
        dto.setLoanPlace(rental.getLoanPlace());
        dto.setReturnPlace(rental.getReturnPlace());
        dto.setCarId(rental.getCarId());
        dto.setCNo(rental.getCNo());
        dto.setDNo(rental.getDNo());
        dto.setRoundTripDistance(rental.getRoundTripDistance());
        dto.setDriveTime(rental.getDriveTime());
        dto.setAmount(rental.getAmount());

        // 设置关联信息
        if (rental.getCustomer() != null) {
            dto.setCustomerName(rental.getCustomer().getCName());
            dto.setCustomerPhone(rental.getCustomer().getPhone());
        }
        if (rental.getDriver() != null) {
            dto.setDriverName(rental.getDriver().getDName());
        }
        
        // 通过carRepository获取车辆信息，而不是依赖于rental.getCar()
        if (rental.getCarId() != null) {
            carRepository.findById(rental.getCarId()).ifPresent(car -> {
                if (car.getTName() != null) {
                    dto.setCarType(car.getTName());
                    dto.setCarBrand(car.getTName()); // Car实体中用tName表示车型/品牌
                }
                // 获取车型的租金率
                if (car.getTNo() != null && car.getTName() != null) {
                    carTypeRepository.findById(car.getTNo())
                        .ifPresent(carType -> dto.setRentRate(carType.getRentRate()));
                }
            });
        }

        return dto;
    }

    /**
     * 将RentalRequestDTO转换为Rental实体
     */
    private Rental convertToEntity(RentalRequestDTO dto) {
        Rental rental = new Rental();
        rental.setInvoiceNo(dto.getInvoiceNo());
        rental.setLoanDate(dto.getLoanDate());
        rental.setReturnDate(dto.getReturnDate());
        rental.setLoanPlace(dto.getLoanPlace());
        rental.setReturnPlace(dto.getReturnPlace());
        rental.setCarId(dto.getCarId());
        rental.setCNo(dto.getCNo());
        rental.setDNo(dto.getDNo());
        rental.setRoundTripDistance(dto.getRoundTripDistance());
        rental.setDriveTime(dto.getDriveTime());
        // 不再从DTO获取amount，而是通过calculateRentalAmount方法计算
        return rental;
    }
}

