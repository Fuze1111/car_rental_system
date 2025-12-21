package com.example.backend.service;

import com.example.backend.dto.CarRequestDTO;
import com.example.backend.dto.CarResponseDTO;
import com.example.backend.entity.Car;
import com.example.backend.entity.CarRepair;
import com.example.backend.entity.CarType;
import com.example.backend.repository.CarRepairRepository;
import com.example.backend.repository.CarRepository;
import com.example.backend.repository.CarTypeRepository;
import com.example.backend.repository.RentalRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 车辆信息管理服务类
 * 提供车辆信息的业务逻辑处理
 */
@Service
@Transactional
public class CarService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarTypeRepository carTypeRepository;

    @Autowired
    private CarRepairRepository carRepairRepository;

    @Autowired
    private RentalRepository rentalRepository;

    /**
     * 获取所有车辆信息
     * @return 车辆信息列表
     */
    public List<CarResponseDTO> getAllCars() {
        List<Car> cars = carRepository.findAll();
        return cars.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 分页获取所有车辆信息
     * @param pageable 分页参数
     * @return 车辆信息分页结果
     */
    public Page<CarResponseDTO> getAllCars(Pageable pageable) {
        Page<Car> carPage = carRepository.findAll(pageable);
        List<CarResponseDTO> carDTOs = carPage.getContent().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(carDTOs, pageable, carPage.getTotalElements());
    }

    /**
     * 根据车牌号获取车辆信息
     * @param carId 车牌号
     * @return 车辆信息
     */
    public Optional<CarResponseDTO> getCarById(String carId) {
        return carRepository.findById(carId)
                .map(this::convertToResponseDTO);
    }

    /**
     * 创建新车辆
     * @param carRequestDTO 车辆请求DTO
     * @return 创建的车辆信息
     */
    public CarResponseDTO createCar(CarRequestDTO carRequestDTO) {
        // 检查车牌号是否已存在
        if (carRepository.existsById(carRequestDTO.getCarId())) {
            throw new RuntimeException("车牌号已存在: " + carRequestDTO.getCarId());
        }

        // 验证车型是否存在（支持前端传入带空白字符的情况）
        if (carRequestDTO.getTNo() != null) {
            String tnoTrim = carRequestDTO.getTNo().trim();
            boolean exists = carTypeRepository.findById(tnoTrim).isPresent();
            if (!exists) {
                System.out.println("[WARN] createCar: provided tNo not found after trim: '" + tnoTrim + "' (original='" + carRequestDTO.getTNo() + "')");
                // 继续：允许创建但不关联车型，或者抛错取决于业务；这里保持原有行为：抛出异常
                throw new RuntimeException("车型不存在: " + tnoTrim);
            }
        }

        Car car = convertToEntity(carRequestDTO);
        Car savedCar = carRepository.save(car);
        // 重新查询以确保CarType关联被加载
        Car reloadedCar = carRepository.findById(savedCar.getCarId())
                .orElseThrow(() -> new RuntimeException("保存后查询失败"));
        return convertToResponseDTO(reloadedCar);
    }

    /**
     * 更新车辆信息
     * @param carId 车牌号
     * @param carRequestDTO 车辆请求DTO
     * @return 更新后的车辆信息
     */
    public CarResponseDTO updateCar(String carId, CarRequestDTO carRequestDTO) {
        Car existingCar = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("车辆不存在: " + carId));

        // 验证车型是否存在
        if (carRequestDTO.getTNo() != null) {
            String tnoTrim = carRequestDTO.getTNo().trim();
            boolean exists = carTypeRepository.findById(tnoTrim).isPresent();
            if (!exists) {
                System.out.println("[WARN] updateCar: provided tNo not found after trim: '" + tnoTrim + "' (original='" + carRequestDTO.getTNo() + "')");
                throw new RuntimeException("车型不存在: " + tnoTrim);
            }
        }

        // 更新车辆信息
        if (carRequestDTO.getPurchaseDate() != null) {
            existingCar.setPurchaseDate(carRequestDTO.getPurchaseDate());
        }
        if (carRequestDTO.getPrice() != null) {
            existingCar.setPrice(carRequestDTO.getPrice());
        }
        if (carRequestDTO.getCarStatus() != null) {
            existingCar.setCarStatus(carRequestDTO.getCarStatus());
        }
        if (carRequestDTO.getTNo() != null) {
            String tnoTrim = carRequestDTO.getTNo().trim();
            CarType carType = carTypeRepository.findById(tnoTrim)
                    .orElse(null);
            if (carType == null) {
                System.out.println("[WARN] updateCar while setting carType: not found tNo='" + tnoTrim + "'");
            }
            existingCar.setCarType(carType);
        }
        if (carRequestDTO.getRunKm() != null) {
            existingCar.setRunKm(carRequestDTO.getRunKm());
        }
        if (carRequestDTO.getRunHour() != null) {
            existingCar.setRunHour(carRequestDTO.getRunHour());
        }

        Car updatedCar = carRepository.save(existingCar);
        return convertToResponseDTO(updatedCar);
    }

    /**
     * 删除车辆
     * @param carId 车牌号
     */
    public void deleteCar(String carId) {
        if (!carRepository.existsById(carId)) {
            throw new RuntimeException("车辆不存在: " + carId);
        }

        // 检查与租赁/维修的关联情况，给出更明确的拒绝删除原因
        try {
            // 统计历史租赁与未结算订单
            long totalRentals = rentalRepository.countByCarId(carId);
            long ongoingRentals = 0L;
            try {
                // reuse list-based method for single car
                ongoingRentals = rentalRepository.countOngoingByCarIdIn(java.util.List.of(carId));
            } catch (Exception ex) {
                // ignore
            }

            long repairCount = carRepairRepository.countByCarId(carId);

            if (ongoingRentals > 0) {
                throw new com.example.backend.exception.DeleteConflictException("无法删除车辆: 存在 " + ongoingRentals + " 个未结算订单，请先处理结算或撤销订单。");
            }
            if (totalRentals > 0) {
                throw new com.example.backend.exception.DeleteConflictException("无法删除车辆: 该车辆有 " + totalRentals + " 条租赁历史记录，若需删除请先清理或转移相关租赁数据。");
            }
            if (repairCount > 0) {
                throw new com.example.backend.exception.DeleteConflictException("无法删除车辆: 存在 " + repairCount + " 条维修记录关联，无法删除。");
            }
        } catch (com.example.backend.exception.DeleteConflictException e) {
            throw e; // 直接抛出给上层处理
        } catch (Exception e) {
            // 若检查过程出错，记录并允许数据库的完整性异常处理走默认流程
            System.out.println("[WARN] deleteCar: 关联检查异常: " + e.getMessage());
        }

        try {
            carRepository.deleteById(carId);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // 检查是否有外键约束导致删除失败
            if (e.getMessage() != null && e.getMessage().contains("foreign key constraint")) {
                throw new com.example.backend.exception.DeleteConflictException("无法删除车辆: 存在关联的租赁记录或维修记录", e);
            } else {
                throw new RuntimeException("删除失败: " + e.getMessage(), e);
            }
        }
    }

    /**
     * 根据车辆状态查询车辆
     * @param carStatus 车辆状态（0-可租，1-已租，2-维修）
     * @return 车辆列表
     */
    public List<CarResponseDTO> getCarsByStatus(String carStatus) {
        List<Car> cars = carRepository.findByCarStatus(carStatus);
        return cars.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据车型查询车辆
     * @param tNo 车型编号
     * @param tName 车型名称
     * @return 车辆列表
     */
    public List<CarResponseDTO> getCarsByType(String tNo, String tName) {
        List<Car> cars;
        if (tNo != null) {
            cars = carRepository.findByTNo(tNo);
        } else if (tName != null) {
            cars = carRepository.findByTName(tName);
        } else {
            cars = carRepository.findAll();
        }
        return cars.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 多条件查询车辆
     * @param carStatus 车辆状态
     * @param tNo 车型编号
     * @param tName 车型名称
     * @return 车辆列表
     */
    public List<CarResponseDTO> searchCars(String carStatus, String tNo, String tName) {
        return searchCars(carStatus, tNo, tName, null, null, null);
    }

    /**
     * 多条件查询车辆（状态、车型、车牌模糊、租金范围）
     */
    public List<CarResponseDTO> searchCars(String carStatus,
                                           String tNo,
                                           String tName,
                                           String carId,
                                           BigDecimal minRent,
                                           BigDecimal maxRent) {
        // 处理空字符串为 null，避免 JPQL 将 "" 当作有效值
        if (carStatus != null && carStatus.trim().isEmpty()) carStatus = null;
        if (tNo != null && tNo.trim().isEmpty()) tNo = null;
        if (tName != null && tName.trim().isEmpty()) tName = null;
        if (carId != null && carId.trim().isEmpty()) carId = null;

        // 处理价格区间：若最小值大于最大值则交换，避免查询不到数据
        if (minRent != null && maxRent != null && minRent.compareTo(maxRent) > 0) {
            BigDecimal tmp = minRent;
            minRent = maxRent;
            maxRent = tmp;
        }

        List<Car> cars = carRepository.findByFilters(carStatus, tNo, tName, carId, minRent, maxRent);
        return cars.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据车牌号模糊查询车辆
     * @param carId 车牌号关键字
     * @return 车辆列表
     */
    public List<CarResponseDTO> searchCarsByCarId(String carId) {
        List<Car> cars = carRepository.findByCarIdContaining(carId);
        return cars.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据车牌号模糊查询车辆（分页，大小写不敏感）
     * @param carId 车牌号关键字
     * @param pageable 分页参数
     * @return 分页车辆信息
     */
    public Page<CarResponseDTO> searchCarsByCarId(String carId, Pageable pageable) {
        if (pageable == null) {
            List<Car> cars = carRepository.findByCarIdContaining(carId);
            List<CarResponseDTO> dtos = cars.stream().map(this::convertToResponseDTO).collect(Collectors.toList());
            return new PageImpl<>(dtos);
        }
        Page<Car> carPage = carRepository.findByCarIdContainingIgnoreCase(carId, pageable);
        List<CarResponseDTO> carDTOs = carPage.getContent().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(carDTOs, pageable, carPage.getTotalElements());
    }

    /**
     * 更新车辆状态
     * @param carId 车牌号
     * @param carStatus 新状态
     * @return 更新后的车辆信息
     */
    public CarResponseDTO updateCarStatus(String carId, String carStatus) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("车辆不存在: " + carId));

        car.setCarStatus(carStatus);
        Car updatedCar = carRepository.save(car);
        return convertToResponseDTO(updatedCar);
    }

    /**
     * 获取车辆统计信息
     * @return 统计信息Map
     */
    public java.util.Map<String, Long> getCarStatistics() {
        long availableCount = carRepository.countByCarStatus("0");
        long rentedCount = carRepository.countByCarStatus("1");
        long repairCount = carRepository.countByCarStatus("2");
        long totalCount = carRepository.count();

        return java.util.Map.of(
            "total", totalCount,
            "available", availableCount,
            "rented", rentedCount,
            "repair", repairCount
        );
    }

    /**
     * 根据多条件查询车辆（支持按车型 tNo/tName 和租金率范围 minRent/maxRent），分页
     */
    public Page<CarResponseDTO> getCarsByFilters(String carStatus, String tNo, String tName, BigDecimal minRent, BigDecimal maxRent, Pageable pageable) {
        if (pageable == null) {
            // 这里不按车牌过滤，carId 传 null，保持与原语义一致
            List<Car> cars = carRepository.findByFilters(carStatus, tNo, tName, null, minRent, maxRent);
            List<CarResponseDTO> dtos = cars.stream().map(this::convertToResponseDTO).collect(Collectors.toList());
            return new PageImpl<>(dtos);
        }
        Page<Car> carPage = carRepository.findByFilters(carStatus, tNo, tName, null, minRent, maxRent, pageable);
        List<CarResponseDTO> carDTOs = carPage.getContent().stream().map(this::convertToResponseDTO).collect(Collectors.toList());
        return new PageImpl<>(carDTOs, pageable, carPage.getTotalElements());
    }

    /**
     * 将Car实体转换为CarResponseDTO
     */
    private CarResponseDTO convertToResponseDTO(Car car) {
        CarResponseDTO dto = new CarResponseDTO();
        dto.setCarId(car.getCarId());
        dto.setPurchaseDate(car.getPurchaseDate());
        dto.setPrice(car.getPrice());
        dto.setCarStatus(car.getCarStatus());
        // tNo和tName通过carType关联获取
        if (car.getCarType() != null) {
            dto.setTNo(car.getCarType().getTNo());
            dto.setTName(car.getCarType().getTName());
        }
        dto.setRunKm(car.getRunKm());
        dto.setRunHour(car.getRunHour());

        // 设置租金率（如果车型信息存在）
        if (car.getCarType() != null) {
            dto.setRentRate(car.getCarType().getRentRate());
        }

        // 判断是否需要维修（状态为2）
        dto.setNeedsRepair("2".equals(car.getCarStatus()));

        // 获取最后维修日期
        Optional<CarRepair> lastRepair = carRepairRepository.findLatestRepairByCarId(car.getCarId());
        lastRepair.ifPresent(repair -> dto.setLastRepairDate(repair.getRDate()));

        return dto;
    }

    /**
     * 将CarRequestDTO转换为Car实体
     */
    private Car convertToEntity(CarRequestDTO dto) {
        Car car = new Car();
        car.setCarId(dto.getCarId());
        car.setPurchaseDate(dto.getPurchaseDate());
        car.setPrice(dto.getPrice());
        car.setCarStatus(dto.getCarStatus() != null ? dto.getCarStatus() : "0"); // 默认可租
        car.setRunKm(dto.getRunKm());
        car.setRunHour(dto.getRunHour());
        
        // 设置CarType关联（支持前端传入带空白字符）
        if (dto.getTNo() != null) {
            String tnoTrim = dto.getTNo().trim();
            CarType carType = carTypeRepository.findById(tnoTrim)
                    .orElse(null);
            if (carType == null) {
                System.out.println("[WARN] convertToEntity: carType not found for tNo='" + tnoTrim + "' (original='" + dto.getTNo() + "')");
            }
            car.setCarType(carType);
        }
        
        return car;
    }
}
