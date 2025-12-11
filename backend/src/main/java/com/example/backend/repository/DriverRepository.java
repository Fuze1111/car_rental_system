package com.example.backend.repository;

import com.example.backend.entity.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 司机信息Repository
 * 提供司机信息的数据访问操作
 */
@Repository
public interface DriverRepository extends JpaRepository<Driver, String> {

    /**
     * 根据司机号模糊查询
     * @param dNo 司机号关键字
     * @return 司机列表
     */
    @Query("SELECT d FROM Driver d WHERE d.dNo LIKE CONCAT('%', :dNo, '%')")
    List<Driver> findByDNoContaining(@Param("dNo") String dNo);

    /**
     * 根据司机状态查询司机列表
     * @param dStatus 司机状态
     * @return 司机列表
     */
    @Query("SELECT d FROM Driver d WHERE d.dStatus = :dStatus")
    List<Driver> findByDStatus(@Param("dStatus") String dStatus);

    /**
     * 根据姓名模糊查询司机
     * @param dName 姓名关键字
     * @return 司机列表
     */
    @Query("SELECT d FROM Driver d WHERE d.dName LIKE CONCAT('%', :dName, '%')")
    List<Driver> findByDNameContaining(@Param("dName") String dName);

    /**
     * 根据准驾车型查询司机
     * @param licenseType 准驾车型
     * @return 司机列表
     */
    @Query("SELECT d FROM Driver d WHERE d.licenseType = :licenseType")
    List<Driver> findByLicenseType(@Param("licenseType") String licenseType);

    /**
     * 多条件查询司机
     * @param dStatus 司机状态
     * @param licenseType 准驾车型
     * @param minSalary 最低工资
     * @param maxSalary 最高工资
     * @return 司机列表
     */
    @Query("SELECT d FROM Driver d WHERE " +
           "(:dStatus IS NULL OR d.dStatus = :dStatus) AND " +
           "(:licenseType IS NULL OR d.licenseType = :licenseType) AND " +
           "(:minSalary IS NULL OR d.salary >= :minSalary) AND " +
           "(:maxSalary IS NULL OR d.salary <= :maxSalary)")
    List<Driver> findByMultipleConditions(
        @Param("dStatus") String dStatus,
        @Param("licenseType") String licenseType,
        @Param("minSalary") BigDecimal minSalary,
        @Param("maxSalary") BigDecimal maxSalary
    );

    /**
     * 统计各状态司机数量
     * @param dStatus 司机状态
     * @return 司机数量
     */
    @Query("SELECT COUNT(d) FROM Driver d WHERE d.dStatus = :dStatus")
    long countByDStatus(@Param("dStatus") String dStatus);

    /**
     * 查询入职时间在指定日期之前的司机
     * @param date 日期
     * @return 司机列表
     */
    @Query("SELECT d FROM Driver d WHERE d.startWork <= :date")
    List<Driver> findByStartWorkBefore(@Param("date") LocalDate date);

    /**
     * 查询工资在指定范围内的司机
     * @param minSalary 最低工资
     * @param maxSalary 最高工资
     * @return 司机列表
     */
    @Query("SELECT d FROM Driver d WHERE d.salary BETWEEN :minSalary AND :maxSalary")
    List<Driver> findBySalaryBetween(@Param("minSalary") BigDecimal minSalary, @Param("maxSalary") BigDecimal maxSalary);

    /**
     * 查询最大的司机号（用于生成递增编号）
     * @return 最大的司机号
     */
    @Query("SELECT d.dNo FROM Driver d ORDER BY d.dNo DESC LIMIT 1")
    String findMaxDNo();

    /**
     * 管理员专用：分页多条件搜索司机
     * @param name 姓名关键字（可为null）
     * @param status 状态（可为null）
     * @param licenseType 准驾车型（可为null）
     * @param pageable 分页参数
     * @return 分页结果
     */
    @Query("SELECT d FROM Driver d WHERE " +
           "(:name = '' OR :name IS NULL OR d.dName LIKE CONCAT('%', :name, '%')) AND " +
           "(:status = '' OR :status IS NULL OR d.dStatus = :status) AND " +
           "(:licenseType = '' OR :licenseType IS NULL OR d.licenseType = :licenseType)")
    Page<Driver> searchDrivers(@Param("name") String name, 
                               @Param("status") String status, 
                               @Param("licenseType") String licenseType, 
                               Pageable pageable);
}