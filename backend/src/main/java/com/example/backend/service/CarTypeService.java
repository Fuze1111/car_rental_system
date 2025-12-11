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
     * @param tName 型号名称
     * @return 车型信息
     */
    public Optional<CarType> getCarTypeById(String tNo, String tName) {
        CarType.CarTypeId id = new CarType.CarTypeId(tNo, tName);
        return carTypeRepository.findById(id);
    }

    /**
     * 创建新车型
     *
     * @param carType 车型信息
     * @return 创建的车型信息
     */
    public CarType createCarType(CarType carType) {
        // 检查车型是否已存在
        CarType.CarTypeId id = new CarType.CarTypeId(carType.getTNo(), carType.getTName());
        if (carTypeRepository.existsById(id)) {
            throw new RuntimeException("车型已存在: " + carType.getTNo() + "/" + carType.getTName());
        }

        return carTypeRepository.save(carType);
    }

    /**
     * 更新车型信息
     *
     * @param tNo     类型编号
     * @param tName   型号名称
     * @param carType 车型信息
     * @return 更新后的车型信息
     */
    public CarType updateCarType(String tNo, String tName, CarType carType) {
        CarType.CarTypeId id = new CarType.CarTypeId(tNo, tName);
        CarType existingCarType = carTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("车型不存在: " + tNo + "/" + tName));

        // 更新车型信息
        existingCarType.setRentRate(carType.getRentRate());

        return carTypeRepository.save(existingCarType);
    }

    /**
     * 删除车型
     *
     * @param tNo   类型编号
     * @param tName 型号名称
     */
    public void deleteCarType(String tNo, String tName) {
        CarType.CarTypeId id = new CarType.CarTypeId(tNo, tName);
        if (!carTypeRepository.existsById(id)) {
            throw new RuntimeException("车型不存在: " + tNo + "/" + tName);
        }

        try {
            carTypeRepository.deleteById(id);
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