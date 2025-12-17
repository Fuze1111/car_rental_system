package com.example.backend.service;

import com.example.backend.dto.CarTypeDTO;
import com.example.backend.entity.Car;
import com.example.backend.entity.CarType;
import com.example.backend.repository.CarRepository;
import com.example.backend.repository.CarTypeRepository;
import com.example.backend.repository.CarRepairRepository;
import com.example.backend.repository.RentalRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 车型信息管理服务类
 * 提供车型信息的业务逻辑处理
 */
@Service
@Transactional
public class CarTypeService {

    @Autowired
    private CarTypeRepository carTypeRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarRepairRepository carRepairRepository;

    @Autowired
    private RentalRepository rentalRepository;

    /**
     * 获取所有车型信息（包含图片URL）
     * 只返回有可用车辆（carStatus='0'）的车型
     *
     * @return 车型DTO列表
     */
    public List<CarTypeDTO> getAllCarTypes() {
        return carTypeRepository.findAll().stream()
                .filter(carType -> hasAvailableCars(carType.getTNo()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 检查指定车型是否有可用车辆
     * @param tNo 车型编号
     * @return true if 有可用车辆，false otherwise
     */
    private boolean hasAvailableCars(String tNo) {
        // 查询该车型下状态为'0'（可租）的车辆
        List<Car> availableCars = carRepository.findByMultipleConditions("0", tNo, null);
        return !availableCars.isEmpty();
    }

    /**
     * 将CarType转换为CarTypeDTO（包含图片URL）
     */
    private CarTypeDTO convertToDTO(CarType carType) {
        CarTypeDTO dto = new CarTypeDTO();
        dto.setTNo(carType.getTNo());
        dto.setTName(carType.getTName());
        dto.setRentRate(carType.getRentRate());
        dto.setImageUrl(resolveImageUrl(carType.getTNo()));
        return dto;
    }

    /**
     * 根据车型编号解析图片URL
     */
    private String resolveImageUrl(String tNo) {
        if (tNo == null || tNo.isBlank()) {
            return "/images/default.jpg";
        }
        return carTypeRepository.findById(tNo)
                .map(CarType::getImagePath)
                .filter(path -> path != null && !path.isBlank())
                .orElseGet(() -> suggestImagePathByTNo(tNo));
    }

    private String suggestImagePathByTNo(String tNo) {
        String normalized = tNo.trim().toUpperCase();
        switch (normalized) {
            case "T001":
                return "/images/toyota_camry.jpg";
            case "T002":
                return "/images/honda_accord.jpg";
            case "T003":
                return "/images/benz_e_class.jpg";
            case "T004":
                return "/images/bmw_5_series.jpg";
            case "T005":
                return "/images/audi_a6l.jpg";
            case "T006":
                return "/images/vw_passat.jpg";
            default:
                return "/images/default.jpg";
        }
    }

    /**
     * 获取原始车型实体列表（保留用于其他地方）
     */
    public List<CarType> getAllCarTypeEntities() {
        return carTypeRepository.findAll();
    }

    /**
     * 根据类型编号获取车型信息
     *
     * @param tNo 类型编号
     * @return 车型信息
     */
    public Optional<CarType> getCarTypeById(String tNo) {
        return carTypeRepository.findById(tNo);
    }

    /**
     * 根据类型编号和型号名称获取车型信息（兼容旧接口）
     *
     * @param tNo   类型编号
     * @param tName 型号名称
     * @return 车型信息
     */
    @Deprecated
    public Optional<CarType> getCarTypeById(String tNo, String tName) {
        return carTypeRepository.findById(tNo);
    }

    /**
     * 创建新车型
     *
     * @param carType 车型信息
     * @return 创建的车型信息
     */
    public CarType createCarType(CarType carType) {
        // 检查车型是否已存在
        if (carTypeRepository.existsById(carType.getTNo())) {
            throw new RuntimeException("车型已存在: " + carType.getTNo());
        }
        carType.setImagePath(normalizeImagePath(carType.getImagePath()));
        return carTypeRepository.save(carType);
    }

    /**
     * 更新车型信息
     *
     * @param tNo     类型编号
     * @param carType 车型信息
     * @return 更新后的车型信息
     */
    public CarType updateCarType(String tNo, CarType carType) {
        CarType existingCarType = carTypeRepository.findById(tNo)
                .orElseThrow(() -> new RuntimeException("车型不存在: " + tNo));

        // 更新车型信息
        if (carType.getTName() != null) {
            existingCarType.setTName(carType.getTName());
        }
        if (carType.getRentRate() != null) {
            existingCarType.setRentRate(carType.getRentRate());
        }
        if (carType.getImagePath() != null) {
            existingCarType.setImagePath(normalizeImagePath(carType.getImagePath()));
        }

        return carTypeRepository.save(existingCarType);
    }

    /**
     * 更新车型信息（兼容旧接口）
     *
     * @param tNo     类型编号
     * @param tName   型号名称（已忽略）
     * @param carType 车型信息
     * @return 更新后的车型信息
     */
    @Deprecated
    public CarType updateCarType(String tNo, String tName, CarType carType) {
        return updateCarType(tNo, carType);
    }

    /**
     * 删除车型
     *
     * @param tNo 类型编号
     */
    public void deleteCarType(String tNo) {
        if (!carTypeRepository.existsById(tNo)) {
            throw new RuntimeException("车型不存在: " + tNo);
        }

        // 先检查车型下是否存在车辆
        long carCount = carRepository.countByTNo(tNo);
        if (carCount > 0) {
            // 如果车型下仍有车辆，进一步检查这些车辆是否有未结算订单或维修记录
            List<String> carIds = carRepository.findCarIdsByTNo(tNo);
            long ongoingRentals = 0L;
            long totalRentals = 0L;
            long repairCount = 0L;
            if (carIds != null && !carIds.isEmpty()) {
                try {
                    ongoingRentals = rentalRepository.countOngoingByCarIdIn(carIds);
                    totalRentals = rentalRepository.countByCarIdIn(carIds);
                } catch (Exception ex) {
                    // ignore query problems, we still want to block deletion because cars exist
                }
                try {
                    repairCount = carRepairRepository.countByCarIdIn(carIds);
                } catch (Exception ex) {
                    // ignore
                }
            }

            StringBuilder reason = new StringBuilder();
            reason.append("无法删除车型: 共有 ").append(carCount).append(" 辆车辆使用此车型。");
            if (ongoingRentals > 0) {
                reason.append(" 检测到 ").append(ongoingRentals).append(" 个未结算订单，请先处理这些订单。");
            } else if (totalRentals > 0) {
                reason.append(" 历史租赁记录 ").append(totalRentals).append(" 条，若要删除请先清理或转移相关租赁数据。");
            }
            if (repairCount > 0) {
                reason.append(" 同时检测到 ").append(repairCount).append(" 条维修记录关联，请先删除维修记录或更新车辆信息。");
            }

            throw new com.example.backend.exception.DeleteConflictException(reason.toString());
        }

        // 若没有车辆，则安全删除
        try {
            carTypeRepository.deleteById(tNo);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // 检查是否有外键约束导致删除失败
            if (e.getMessage() != null && e.getMessage().contains("foreign key constraint")) {
                throw new com.example.backend.exception.DeleteConflictException("无法删除车型: 存在关联的记录，例如车辆或租赁/维修记录。", e);
            } else {
                throw new RuntimeException("删除失败: " + e.getMessage(), e);
            }
        }
    }

    private String normalizeImagePath(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            return null;
        }
        String trimmed = imagePath.trim();
        return trimmed.startsWith("/") ? trimmed : "/" + trimmed;
    }
}
