package com.example.backend.repository;

import com.example.backend.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 车辆信息Repository
 * 提供车辆信息的数据访问操作
 */
@Repository
public interface CarRepository extends JpaRepository<Car, String> {

    /**
     * 根据车辆状态查询车辆列表
     * @param carStatus 车辆状态（0-可租，1-已租，2-维修）
     * @return 车辆列表
     */
    List<Car> findByCarStatus(String carStatus);

    /**
     * 根据车型编号查询车辆列表
     * @param tNo 车型编号
     * @return 车辆列表
     */
    @Query("SELECT c FROM Car c WHERE c.tNo = :tNo")
    List<Car> findByTNo(@Param("tNo") String tNo);

    /**
     * 根据车型名称查询车辆列表
     * @param tName 车型名称
     * @return 车辆列表
     */
    @Query("SELECT c FROM Car c WHERE c.tName = :tName")
    List<Car> findByTName(@Param("tName") String tName);

    /**
     * 多条件查询车辆
     * @param carStatus 车辆状态
     * @param tNo 车型编号
     * @param tName 车型名称
     * @return 车辆列表
     */
    @Query("SELECT c FROM Car c WHERE " +
           "(:carStatus IS NULL OR c.carStatus = :carStatus) AND " +
           "(:tNo IS NULL OR c.tNo = :tNo) AND " +
           "(:tName IS NULL OR c.tName = :tName)")
    List<Car> findByMultipleConditions(
        @Param("carStatus") String carStatus,
        @Param("tNo") String tNo,
        @Param("tName") String tName
    );

    /**
     * 统计各状态车辆数量
     * @param carStatus 车辆状态
     * @return 车辆数量
     */
    long countByCarStatus(String carStatus);
}

