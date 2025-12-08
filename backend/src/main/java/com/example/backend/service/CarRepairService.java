package com.example.backend.service;

import com.example.backend.dto.CarRepairRequestDTO;
import com.example.backend.dto.CarRepairResponseDTO;
import com.example.backend.entity.Car;
import com.example.backend.entity.CarRepair;
import com.example.backend.repository.CarRepairRepository;
import com.example.backend.repository.CarRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 车辆维修记录服务类
 * 提供车辆维修记录的业务逻辑处理
 */
@Service
@Transactional
public class CarRepairService {

    @Autowired
    private CarRepairRepository carRepairRepository;

    @Autowired
    private CarRepository carRepository;

    /**
     * 获取所有维修记录
     * @return 维修记录列表
     */
    public List<CarRepairResponseDTO> getAllRepairs() {
        List<CarRepair> repairs = carRepairRepository.findAll();
        return repairs.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据维修编号获取维修记录
     * @param rNo 维修编号
     * @return 维修记录
     */
    public Optional<CarRepairResponseDTO> getRepairById(Integer rNo) {
        return carRepairRepository.findById(rNo)
                .map(this::convertToResponseDTO);
    }

    /**
     * 根据车辆号获取维修记录
     * @param carId 车辆号
     * @return 维修记录列表
     */
    public List<CarRepairResponseDTO> getRepairsByCarId(String carId) {
        List<CarRepair> repairs = carRepairRepository.findByCarIdOrderByRDateDesc(carId);
        return repairs.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 创建新维修记录
     * @param repairRequestDTO 维修记录请求DTO
     * @return 创建的维修记录
     */
    public CarRepairResponseDTO createRepair(CarRepairRequestDTO repairRequestDTO) {
        // 验证车辆是否存在
        Car car = carRepository.findById(repairRequestDTO.getCarId())
                .orElseThrow(() -> new RuntimeException("车辆不存在: " + repairRequestDTO.getCarId()));

        CarRepair repair = convertToEntity(repairRequestDTO);
        CarRepair savedRepair = carRepairRepository.save(repair);

        // 更新车辆状态为维修中
        car.setCarStatus("2");
        carRepository.save(car);

        return convertToResponseDTO(savedRepair);
    }

    /**
     * 更新维修记录
     * @param rNo 维修编号
     * @param repairRequestDTO 维修记录请求DTO
     * @return 更新后的维修记录
     */
    public CarRepairResponseDTO updateRepair(Integer rNo, CarRepairRequestDTO repairRequestDTO) {
        CarRepair existingRepair = carRepairRepository.findById(rNo)
                .orElseThrow(() -> new RuntimeException("维修记录不存在: " + rNo));

        // 更新维修信息
        if (repairRequestDTO.getRDate() != null) {
            existingRepair.setRDate(repairRequestDTO.getRDate());
        }
        if (repairRequestDTO.getRunKm() != null) {
            existingRepair.setRunKm(repairRequestDTO.getRunKm());
        }
        if (repairRequestDTO.getRunHour() != null) {
            existingRepair.setRunHour(repairRequestDTO.getRunHour());
        }

        CarRepair updatedRepair = carRepairRepository.save(existingRepair);
        return convertToResponseDTO(updatedRepair);
    }

    /**
     * 删除维修记录
     * @param rNo 维修编号
     */
    public void deleteRepair(Integer rNo) {
        if (!carRepairRepository.existsById(rNo)) {
            throw new RuntimeException("维修记录不存在: " + rNo);
        }
        carRepairRepository.deleteById(rNo);
    }

    /**
     * 获取车辆的维修次数
     * @param carId 车辆号
     * @return 维修次数
     */
    public long getRepairCountByCarId(String carId) {
        return carRepairRepository.countByCarId(carId);
    }

    /**
     * 将CarRepair实体转换为CarRepairResponseDTO
     */
    private CarRepairResponseDTO convertToResponseDTO(CarRepair repair) {
        CarRepairResponseDTO dto = new CarRepairResponseDTO();
        dto.setRNo(repair.getRNo());
        dto.setRDate(repair.getRDate());
        dto.setRunKm(repair.getRunKm());
        dto.setRunHour(repair.getRunHour());
        dto.setCarId(repair.getCarId());

        // 获取车型信息
        carRepository.findById(repair.getCarId()).ifPresent(car -> {
            dto.setCarModel(car.getTName());
        });

        return dto;
    }

    /**
     * 将CarRepairRequestDTO转换为CarRepair实体
     */
    private CarRepair convertToEntity(CarRepairRequestDTO dto) {
        CarRepair repair = new CarRepair();
        repair.setRDate(dto.getRDate());
        repair.setRunKm(dto.getRunKm());
        repair.setRunHour(dto.getRunHour());
        repair.setCarId(dto.getCarId());
        return repair;
    }
}

