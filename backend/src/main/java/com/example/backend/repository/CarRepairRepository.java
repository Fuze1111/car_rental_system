package com.example.backend.repository;

import com.example.backend.entity.CarRepair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public interface CarRepairRepository extends JpaRepository<CarRepair, String> {

    /**
     * 根据车辆号查询所有维修记录，按维修编号倒序排列
     */
    @Query("SELECT cr FROM CarRepair cr WHERE cr.carId = :carId ORDER BY cr.rNo DESC")
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

    /**
     * 根据过滤条件查询维修记录（分页），仅返回已完成的维修记录（rDate 非空），按维修编号倒序
     */
    @Query("SELECT cr FROM CarRepair cr WHERE cr.rDate IS NOT NULL AND " +
           "(:carId IS NULL OR LOWER(cr.carId) LIKE CONCAT('%', LOWER(:carId), '%')) AND " +
           "(:start IS NULL OR cr.rDate >= :start) AND (:end IS NULL OR cr.rDate <= :end) ORDER BY cr.rNo DESC")
    Page<CarRepair> findByFilters(
            @Param("carId") String carId,
            @Param("start") java.time.LocalDateTime start,
            @Param("end") java.time.LocalDateTime end,
            Pageable pageable
    );

    /**
     * 根据过滤条件查询维修记录（不分页），仅返回已完成的维修记录（rDate 非空），按维修编号倒序
     */
    @Query("SELECT cr FROM CarRepair cr WHERE cr.rDate IS NOT NULL AND " +
           "(:carId IS NULL OR LOWER(cr.carId) LIKE CONCAT('%', LOWER(:carId), '%')) AND " +
           "(:start IS NULL OR cr.rDate >= :start) AND (:end IS NULL OR cr.rDate <= :end) ORDER BY cr.rNo DESC")
    List<CarRepair> findByFilters(
            @Param("carId") String carId,
            @Param("start") java.time.LocalDateTime start,
            @Param("end") java.time.LocalDateTime end
    );

    /**
     * 统计指定车牌集合的维修记录数量
     */
    @Query("SELECT COUNT(cr) FROM CarRepair cr WHERE cr.carId IN :carIds")
    long countByCarIdIn(@Param("carIds") List<String> carIds);

    /**
     * 查找指定车辆尚未完成（rDate 为空）的维修记录，按维修编号倒序排列
     */
    @Query("SELECT cr FROM CarRepair cr WHERE cr.carId = :carId AND cr.rDate IS NULL ORDER BY cr.rNo DESC")
    List<CarRepair> findOpenRepairsByCarIdOrderByRNoDesc(@Param("carId") String carId);
}
