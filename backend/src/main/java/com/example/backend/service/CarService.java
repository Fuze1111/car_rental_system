package com.example.backend.service;

import com.example.backend.dto.CarRequestDTO;
import com.example.backend.dto.CarResponseDTO;
import com.example.backend.entity.Car;
import com.example.backend.entity.CarRepair;
import com.example.backend.repository.CarRepairRepository;
import com.example.backend.repository.CarRepository;
import com.example.backend.repository.CarTypeRepository;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 车辆信息管理服务类
 * 提供车辆信息的业务逻辑处理
 */
@Service
@Transactional
public class CarService {

    private static final Logger logger = LoggerFactory.getLogger(CarService.class);

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarTypeRepository carTypeRepository;

    @Autowired
    private CarRepairRepository carRepairRepository;

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
        logger.info("createCar payload: {}", carRequestDTO);
        // 检查车牌号是否已存在
        if (carRepository.existsById(carRequestDTO.getCarId())) {
            throw new RuntimeException("车牌号已存在: " + carRequestDTO.getCarId());
        }

        // 验证并回填车型信息：如果只提供了 tNo 或 tName，尝试回填另一项
        String tNo = carRequestDTO.getTNo();
        String tName = carRequestDTO.getTName();

        if ((tNo == null || tNo.isBlank()) && (tName == null || tName.isBlank())) {
            throw new RuntimeException("创建车辆时车型不能为空，请提供 tNo 或 tName");
        }

        if (tNo != null && tName != null) {
            carTypeRepository.findByTNoAndTName(tNo, tName)
                    .orElseThrow(() -> new RuntimeException("车型不存在: " + tNo + "/" + tName));
        } else if (tNo != null && (tName == null || tName.isBlank())) {
            List<com.example.backend.entity.CarType> found = carTypeRepository.findByTNoQuery(tNo);
            if (found == null || found.isEmpty()) throw new RuntimeException("车型不存在: tNo=" + tNo);
            carRequestDTO.setTName(found.get(0).getTName());
            logger.info("createCar auto-filled tName={} for tNo={}", found.get(0).getTName(), tNo);
        } else if (tName != null && (tNo == null || tNo.isBlank())) {
            // 尝试按 tName 查找所有匹配的车型并回填 tNo（取第一个匹配项）
            List<com.example.backend.entity.CarType> matches = carTypeRepository.findByTNameQuery(tName);
            if (matches != null && !matches.isEmpty()) {
                carRequestDTO.setTNo(matches.get(0).getTNo());
                logger.info("createCar auto-filled tNo={} for tName={}", matches.get(0).getTNo(), tName);
            }
        }

        Car car = convertToEntity(carRequestDTO);
        Car savedCar = carRepository.save(car);
        logger.info("createCar saved: {}", savedCar);

        // Post-save sanity: if persisted entity missing tNo or tName, try to backfill from carType or request
        Car persisted = carRepository.findById(savedCar.getCarId()).orElse(savedCar);
        boolean changed = false;
        if ((persisted.getTNo() == null || persisted.getTNo().isBlank()) && (carRequestDTO.getTNo() != null && !carRequestDTO.getTNo().isBlank())) {
            persisted.setTNo(carRequestDTO.getTNo());
            changed = true;
            logger.warn("createCar post-fill: set TNo from request: {}", carRequestDTO.getTNo());
        }
        if ((persisted.getTName() == null || persisted.getTName().isBlank()) && (carRequestDTO.getTName() != null && !carRequestDTO.getTName().isBlank())) {
            persisted.setTName(carRequestDTO.getTName());
            changed = true;
            logger.warn("createCar post-fill: set TName from request: {}", carRequestDTO.getTName());
        }
        // If still missing one side, try lookup by the other side
        if ((persisted.getTName() == null || persisted.getTName().isBlank()) && persisted.getTNo() != null && !persisted.getTNo().isBlank()) {
            List<com.example.backend.entity.CarType> found = carTypeRepository.findByTNoQuery(persisted.getTNo());
            if (found != null && !found.isEmpty()) {
                persisted.setTName(found.get(0).getTName());
                changed = true;
                logger.warn("createCar post-fill: set TName from CarType for TNo {} => {}", persisted.getTNo(), found.get(0).getTName());
            }
        }
        if ((persisted.getTNo() == null || persisted.getTNo().isBlank()) && persisted.getTName() != null && !persisted.getTName().isBlank()) {
            List<com.example.backend.entity.CarType> matches = carTypeRepository.findByTNameQuery(persisted.getTName());
            if (matches != null && !matches.isEmpty()) {
                persisted.setTNo(matches.get(0).getTNo());
                changed = true;
                logger.warn("createCar post-fill: set TNo from CarType for TName {} => {}", persisted.getTName(), matches.get(0).getTNo());
            }
        }

        if (changed) {
            Car resaved = carRepository.save(persisted);
            logger.info("createCar post-saved after backfill: {}", resaved);
            return convertToResponseDTO(resaved);
        }

        return convertToResponseDTO(persisted);
    }

    /**
     * 更新车辆信息
     * @param carId 车牌号
     * @param carRequestDTO 车辆请求DTO
     * @return 更新后的车辆信息
     */
    public CarResponseDTO updateCar(String carId, CarRequestDTO carRequestDTO) {
        logger.info("updateCar called for {} payload: {}", carId, carRequestDTO);
        Car existingCar = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("车辆不存在: " + carId));

        // 验证车型是否存在并回填缺失字段
        String tNo = carRequestDTO.getTNo();
        String tName = carRequestDTO.getTName();

        // Keep resolved values in local vars to avoid accidental null overrides
        String resolvedTNo = tNo;
        String resolvedTName = tName;

        if (tNo != null && tName != null) {
            // both provided: validate pair exists
            carTypeRepository.findByTNoAndTName(tNo, tName)
                    .orElseThrow(() -> new RuntimeException("车型不存在: " + tNo + "/" + tName));
            logger.info("updateCar validated tNo/tName pair {} / {}", tNo, tName);
        } else if (tNo != null && (tName == null || tName.isBlank())) {
            List<com.example.backend.entity.CarType> found = carTypeRepository.findByTNoQuery(tNo);
            if (found != null && !found.isEmpty()) {
                resolvedTName = found.get(0).getTName();
                // also normalize resolvedTNo from repository result (in case of casing)
                resolvedTNo = found.get(0).getTNo();
                carRequestDTO.setTName(resolvedTName);
                logger.info("updateCar resolved tName={} for tNo={}", resolvedTName, resolvedTNo);
            } else {
                // if user provided an explicit tNo that doesn't exist, throw
                throw new RuntimeException("车型不存在: tNo=" + tNo);
            }
        } else if (tName != null && (tNo == null || tNo.isBlank())) {
            // Try to find a CarType by name and fill tNo
            List<com.example.backend.entity.CarType> matches = carTypeRepository.findByTNameQuery(tName);
            if (matches != null && !matches.isEmpty()) {
                resolvedTNo = matches.get(0).getTNo();
                resolvedTName = matches.get(0).getTName();
                carRequestDTO.setTNo(resolvedTNo);
                logger.info("updateCar resolved tNo={} for tName={}", resolvedTNo, resolvedTName);
            }
        }

        // 更新车辆信息（确保使用解析后的 resolvedTNo/resolvedTName）
        if (carRequestDTO.getPurchaseDate() != null) {
            existingCar.setPurchaseDate(carRequestDTO.getPurchaseDate());
        }
        if (carRequestDTO.getPrice() != null) {
            existingCar.setPrice(carRequestDTO.getPrice());
        }
        if (carRequestDTO.getCarStatus() != null) {
            existingCar.setCarStatus(carRequestDTO.getCarStatus());
        }
        if (resolvedTNo != null) {
            existingCar.setTNo(resolvedTNo);
        }
        if (resolvedTName != null) {
            existingCar.setTName(resolvedTName);
        }
        if (carRequestDTO.getRunKm() != null) {
            existingCar.setRunKm(carRequestDTO.getRunKm());
        }
        if (carRequestDTO.getRunHour() != null) {
            existingCar.setRunHour(carRequestDTO.getRunHour());
        }

        Car updatedCar = carRepository.save(existingCar);
        logger.info("updateCar persisted: {}", updatedCar);
        // reload to ensure all JPA-managed relations are initialized and values persisted
        Car persisted = carRepository.findById(updatedCar.getCarId()).orElse(updatedCar);
        logger.info("updateCar after reload tNo/tName: {}/{}", persisted.getTNo(), persisted.getTName());
        return convertToResponseDTO(persisted);
    }

    /**
     * 删除车辆
     * @param carId 车牌号
     */
    public void deleteCar(String carId) {
        if (!carRepository.existsById(carId)) {
            throw new RuntimeException("车辆不存在: " + carId);
        }
        
        try {
            carRepository.deleteById(carId);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // 检查是否有外键约束导致删除失败
            if (e.getMessage().contains("foreign key constraint")) {
                throw new RuntimeException("无法删除车辆: 存在关联的租赁记录或维修记录", e);
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
        if (tNo != null && tName != null) {
            cars = carRepository.findByMultipleConditions(null, tNo, tName);
        } else if (tNo != null) {
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
        List<Car> cars = carRepository.findByMultipleConditions(carStatus, tNo, tName);
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
            List<Car> cars = carRepository.findByFilters(carStatus, tNo, tName, minRent, maxRent);
            List<CarResponseDTO> dtos = cars.stream().map(this::convertToResponseDTO).collect(Collectors.toList());
            return new PageImpl<>(dtos);
        }
        Page<Car> carPage = carRepository.findByFilters(carStatus, tNo, tName, minRent, maxRent, pageable);
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
        dto.setTNo(car.getTNo());
        dto.setTName(car.getTName());
        dto.setRunKm(car.getRunKm());
        dto.setRunHour(car.getRunHour());

        // 设置租金率（如果车型信息存在）
        if (car.getCarType() != null) {
            dto.setRentRate(car.getCarType().getRentRate());
            // if tName is missing on Car entity, try fill from associated CarType
            if ((dto.getTName() == null || dto.getTName().isBlank()) && car.getCarType().getTName() != null) {
                dto.setTName(car.getCarType().getTName());
            }
        } else {
            // 如果 carType 为空，但 car.tNo 有值，尝试通过 repository 回填 tName 与 rentRate
            if (car.getTNo() != null && (dto.getTName() == null || dto.getTName().isBlank())) {
                List<com.example.backend.entity.CarType> matches = carTypeRepository.findByTNoQuery(car.getTNo());
                if (matches != null && !matches.isEmpty()) {
                    com.example.backend.entity.CarType ct = matches.get(0);
                    dto.setTName(ct.getTName());
                    dto.setRentRate(ct.getRentRate());
                }
            }
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
        car.setTNo(dto.getTNo());
        car.setTName(dto.getTName());
        if (dto.getRunKm() != null) car.setRunKm(dto.getRunKm());
        if (dto.getRunHour() != null) car.setRunHour(dto.getRunHour());
        return car;
    }
}
