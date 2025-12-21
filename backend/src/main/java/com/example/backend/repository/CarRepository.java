package com.example.backend.repository;

import com.example.backend.entity.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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
    @Query("SELECT c FROM Car c LEFT JOIN c.carType ct WHERE ct.tNo = :tNo")
    List<Car> findByTNo(@Param("tNo") String tNo);

    /**
     * 根据车型名称查询车辆列表
     * @param tName 车型名称
     * @return 车辆列表
     */
    @Query("SELECT c FROM Car c LEFT JOIN c.carType ct WHERE ct.tName = :tName")
    List<Car> findByTName(@Param("tName") String tName);

    /**
     * 多条件查询车辆
     * @param carStatus 车辆状态
     * @param tNo 车型编号
     * @param tName 车型名称
     * @return 车辆列表
     */
    @Query("SELECT c FROM Car c LEFT JOIN c.carType ct WHERE " +
           "(:carStatus IS NULL OR c.carStatus = :carStatus) AND " +
           "(:tNo IS NULL OR ct.tNo = :tNo) AND " +
           "(:tName IS NULL OR ct.tName = :tName)")
    List<Car> findByMultipleConditions(
        @Param("carStatus") String carStatus,
        @Param("tNo") String tNo,
        @Param("tName") String tName
    );

    /**
     * 根据车牌号模糊查询车辆
     * @param carId 车牌号关键字
     * @return 车辆列表
     */
    @Query("SELECT c FROM Car c WHERE c.carId LIKE CONCAT('%', :carId, '%')")
    List<Car> findByCarIdContaining(@Param("carId") String carId);

    /**
     * 根据车牌号模糊查询车辆（分页且不区分大小写）
     */
    Page<Car> findByCarIdContainingIgnoreCase(String carId, Pageable pageable);

    /**
     * 根据多条件（包含车型和租金率范围）查询车辆（分页）
     */
    @Query("SELECT c FROM Car c LEFT JOIN c.carType ct WHERE " +
           "(:carStatus IS NULL OR c.carStatus = :carStatus) AND " +
           "(:tNo IS NULL OR ct.tNo = :tNo) AND " +
           "(:tName IS NULL OR ct.tName = :tName) AND " +
           "(:minRent IS NULL OR ct.rentRate >= :minRent) AND " +
           "(:maxRent IS NULL OR ct.rentRate <= :maxRent)")
    Page<Car> findByFilters(
            @Param("carStatus") String carStatus,
            @Param("tNo") String tNo,
            @Param("tName") String tName,
            @Param("minRent") BigDecimal minRent,
            @Param("maxRent") BigDecimal maxRent,
            Pageable pageable
    );

    /**
     * 多条件查询车辆（含车牌模糊和租金范围，分页版）
     */
    @Query("SELECT c FROM Car c LEFT JOIN c.carType ct WHERE " +
           "(:carStatus IS NULL OR c.carStatus = :carStatus) AND " +
           "(:tNo IS NULL OR ct.tNo = :tNo) AND " +
           "(:tName IS NULL OR ct.tName = :tName) AND " +
           "(:carId IS NULL OR LOWER(c.carId) LIKE LOWER(CONCAT('%', :carId, '%'))) AND " +
           "(:minRent IS NULL OR ct.rentRate >= :minRent) AND " +
           "(:maxRent IS NULL OR ct.rentRate <= :maxRent)")
    Page<Car> findByFilters(
            @Param("carStatus") String carStatus,
            @Param("tNo") String tNo,
            @Param("tName") String tName,
            @Param("carId") String carId,
            @Param("minRent") BigDecimal minRent,
            @Param("maxRent") BigDecimal maxRent,
            Pageable pageable
    );

    /**
     * 非分页版本的多条件查询（兼容旧接口）
     */
    @Query("SELECT c FROM Car c LEFT JOIN c.carType ct WHERE " +
           "(:carStatus IS NULL OR c.carStatus = :carStatus) AND " +
           "(:tNo IS NULL OR ct.tNo = :tNo) AND " +
           "(:tName IS NULL OR ct.tName = :tName) AND " +
           "(:carId IS NULL OR LOWER(c.carId) LIKE LOWER(CONCAT('%', :carId, '%'))) AND " +
           "(:minRent IS NULL OR ct.rentRate >= :minRent) AND " +
           "(:maxRent IS NULL OR ct.rentRate <= :maxRent)")
    List<Car> findByFilters(
            @Param("carStatus") String carStatus,
            @Param("tNo") String tNo,
            @Param("tName") String tName,
            @Param("carId") String carId,
            @Param("minRent") BigDecimal minRent,
            @Param("maxRent") BigDecimal maxRent
    );

    /**
     * 统计各状态车辆数量
     * @param carStatus 车辆状态
     * @return 车辆数量
     */
    long countByCarStatus(String carStatus);

    /**
     * 统计某车型下车辆数量
     */
    @Query("SELECT COUNT(c) FROM Car c LEFT JOIN c.carType ct WHERE ct.tNo = :tNo")
    long countByTNo(@Param("tNo") String tNo);

    /**
     * 获取指定车型下的车牌号列表（用于在其他表中做关联检查）
     */
    @Query("SELECT c.carId FROM Car c LEFT JOIN c.carType ct WHERE ct.tNo = :tNo")
    List<String> findCarIdsByTNo(@Param("tNo") String tNo);
}