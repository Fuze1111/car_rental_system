package com.example.backend.service;

import com.example.backend.dto.CarRequestDTO;
import com.example.backend.dto.CarResponseDTO;
import com.example.backend.entity.Car;
import com.example.backend.entity.CarRepair;
import com.example.backend.entity.CarType;
import com.example.backend.repository.CarRepairRepository;
import com.example.backend.repository.CarRepository;
import com.example.backend.repository.CarTypeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        // 验证车型是否存在
        if (carRequestDTO.getTNo() != null && carRequestDTO.getTName() != null) {
            carTypeRepository.findByTNoAndTName(carRequestDTO.getTNo(), carRequestDTO.getTName())
                    .orElseThrow(() -> new RuntimeException("车型不存在"));
        }

        Car car = convertToEntity(carRequestDTO);
        Car savedCar = carRepository.save(car);
        return convertToResponseDTO(savedCar);
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
        if (carRequestDTO.getTNo() != null && carRequestDTO.getTName() != null) {
            carTypeRepository.findByTNoAndTName(carRequestDTO.getTNo(), carRequestDTO.getTName())
                    .orElseThrow(() -> new RuntimeException("车型不存在"));
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
            existingCar.setTNo(carRequestDTO.getTNo());
        }
        if (carRequestDTO.getTName() != null) {
            existingCar.setTName(carRequestDTO.getTName());
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
        carRepository.deleteById(carId);
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
        car.setTNo(dto.getTNo());
        car.setTName(dto.getTName());
        return car;
    }
}

