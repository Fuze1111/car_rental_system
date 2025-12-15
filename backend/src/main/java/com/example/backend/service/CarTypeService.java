package com.example.backend.service;

import com.example.backend.entity.CarType;
import com.example.backend.repository.CarTypeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 车型信息管理服务类
 * 提供车型信息的业务逻辑处理
 */
@Service
@Transactional
public class CarTypeService {

    @Autowired
    private CarTypeRepository carTypeRepository;

    /**
     * 获取所有车型信息
     *
     * @return 车型信息列表
     */
    public List<CarType> getAllCarTypes() {
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