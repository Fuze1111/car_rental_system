package com.example.backend.repository;

import com.example.backend.entity.CarRepair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 汽车维修记录Repository
 * 提供汽车维修记录的数据访问操作
 */
@Repository
public interface CarRepairRepository extends JpaRepository<CarRepair, Integer> {

    /**
     * 根据车辆号查询所有维修记录
     * @param carId 车辆号
     * @return 维修记录列表
     */
    @Query("SELECT cr FROM CarRepair cr WHERE cr.carId = :carId ORDER BY cr.rDate DESC")
    List<CarRepair> findByCarIdOrderByRDateDesc(@Param("carId") String carId);

    /**
     * 查询车辆最后一次维修记录
     * @param carId 车辆号
     * @return 最后一次维修记录
     */
    @Query("SELECT cr FROM CarRepair cr WHERE cr.carId = :carId ORDER BY cr.rDate DESC LIMIT 1")
    Optional<CarRepair> findLatestRepairByCarId(@Param("carId") String carId);

    /**
     * 统计车辆的维修次数
     * @param carId 车辆号
     * @return 维修次数
     */
    long countByCarId(String carId);
}

