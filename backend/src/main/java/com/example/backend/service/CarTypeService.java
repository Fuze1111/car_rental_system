package com.example.backend.service;

import com.example.backend.dto.CarTypeDTO;
import com.example.backend.entity.Car;
import com.example.backend.entity.CarType;
import com.example.backend.repository.CarRepository;
import com.example.backend.repository.CarTypeRepository;
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

    /**
     * 获取所有车型信息（包含图片URL）
     * 只返回有可用车辆（carStatus='0'）的车型
     *
     * @return 车型DTO列表
     */
    public List<CarTypeDTO> getAllCarTypes() {
        List<CarType> carTypes = carTypeRepository.findAll();
        
        // 过滤掉没有可用车辆的车型
        return carTypes.stream()
                .filter(carType -> hasAvailableCars(carType.getTNo(), carType.getTName()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 检查指定车型是否有可用车辆
     * @param tNo 车型编号
     * @param tName 车型名称
     * @return true if 有可用车辆，false otherwise
     */
    private boolean hasAvailableCars(String tNo, String tName) {
        // 查询该车型下状态为'0'（可租）的车辆
        List<Car> availableCars = carRepository.findByMultipleConditions("0", tNo, tName);
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
        
        // 根据车型名称匹配后端静态资源中的图片文件
        String imageUrl = resolveImageUrl(carType.getTName(), carType.getTNo());
        dto.setImageUrl(imageUrl);
        
        return dto;
    }
    
    /**
     * 根据车型名称和编号解析图片URL
     */
    private String resolveImageUrl(String tName, String tNo) {
        String name = (tName != null ? tName : "").toLowerCase();
        
        try {
            if (name.contains("丰田") || name.contains("凯美瑞") || name.contains("camry")) {
                return "/images/toyota_camry.jpg";
            }
            if (name.contains("本田") || name.contains("雅阁") || name.contains("accord")) {
                return "/images/honda_accord.jpg";
            }
            if (name.contains("奔驰") || name.contains("e级") || name.contains("benz") || name.contains("mercedes")) {
                return "/images/benz_e_class.jpg";
            }
            if (name.contains("宝马") || name.contains("5系") || name.contains("bmw")) {
                return "/images/bmw_5_series.jpg";
            }
            if (name.contains("奥迪") || name.contains("a6l") || name.contains("audi")) {
                return "/images/audi_a6l.jpg";
            }
            if (name.contains("大众") || name.contains("帕萨特") || name.contains("passat")) {
                return "/images/vw_passat.jpg";
            }
        } catch (Exception ex) {
            // 忽略异常，使用默认图片
        }
        
        // 备用：根据tNo匹配
        if (tNo != null) {
            String tn = tNo.toLowerCase();
            if (tn.startsWith("t001")) return "/images/toyota_camry.jpg";
            if (tn.startsWith("t002")) return "/images/honda_accord.jpg";
            if (tn.startsWith("t003")) return "/images/benz_e_class.jpg";
            if (tn.startsWith("t004")) return "/images/bmw_5_series.jpg";
            if (tn.startsWith("t005")) return "/images/audi_a6l.jpg";
            if (tn.startsWith("t006")) return "/images/vw_passat.jpg";
        }
        
        // 默认图片
        return "/images/default.jpg";
    }
    
    /**
     * 获取原始车型实体列表（保留用于其他地方）
     */
    public List<CarType> getAllCarTypeEntities() {
        return carTypeRepository.findAll();
    }

    /**
     * 根据类型编号和型号名称获取车型信息
     *
     * @param tNo   类型编号
     * @return 车型信息
     */
    public Optional<CarType> getCarTypeById(String tNo) {
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

        // 如果传入的 carType 中包含 tName, 更新 tName 与 rentRate
        if (carType.getTName() != null && !carType.getTName().isBlank()) {
            existingCarType.setTName(carType.getTName());
        }
        if (carType.getRentRate() != null) {
            existingCarType.setRentRate(carType.getRentRate());
        }

        return carTypeRepository.save(existingCarType);
    }

    /**
     * 删除车型
     *
     * @param tNo   类型编号
     */
    public void deleteCarType(String tNo) {
        if (!carTypeRepository.existsById(tNo)) {
            throw new RuntimeException("车型不存在: " + tNo);
        }

        try {
            carTypeRepository.deleteById(tNo);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // 检查是否有外键约束导致删除失败
            if (e.getMessage().contains("foreign key constraint")) {
                throw new RuntimeException("无法删除车型: 存在关联的车辆信息", e);
            } else {
                throw new RuntimeException("删除失败: " + e.getMessage(), e);
            }
        }
    }
}