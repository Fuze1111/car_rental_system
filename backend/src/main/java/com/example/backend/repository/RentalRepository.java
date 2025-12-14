package com.example.backend.repository;

import com.example.backend.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 租赁业务Repository
 * 提供租赁业务的数据访问操作
 *
 * 注意：所有查询方法都使用 @Query 注解，避免 Spring Data JPA 方法命名解析的大小写问题
 */
@Repository
public interface RentalRepository extends JpaRepository<Rental, String> {

    /**
     * 根据发票号模糊查询
     * @param invoiceNo 发票号关键字
     * @return 租赁记录列表
     */
    @Query("SELECT r FROM Rental r WHERE r.invoiceNo LIKE CONCAT('%', :invoiceNo, '%')")
    List<Rental> findByInvoiceNoContaining(@Param("invoiceNo") String invoiceNo);

    /**
     * 根据客户号查询租赁记录
     * @param cNo 客户号
     * @return 租赁记录列表
     */
    @Query("SELECT r FROM Rental r WHERE r.cNo = :cNo")
    List<Rental> findByCNo(@Param("cNo") String cNo);

    /**
     * 根据司机号查询租赁记录
     * @param dNo 司机号
     * @return 租赁记录列表
     */
    @Query("SELECT r FROM Rental r WHERE r.dNo = :dNo")
    List<Rental> findByDNo(@Param("dNo") String dNo);

    /**
     * 根据车辆ID查询租赁记录
     * @param carId 车辆ID
     * @return 租赁记录列表
     */
    @Query("SELECT r FROM Rental r WHERE r.carId = :carId")
    List<Rental> findByCarId(@Param("carId") String carId);

    /**
     * 根据借车地点模糊查询租赁记录
     * @param loanPlace 借车地点关键字
     * @return 租赁记录列表
     */
    @Query("SELECT r FROM Rental r WHERE r.loanPlace LIKE CONCAT('%', :loanPlace, '%')")
    List<Rental> findByLoanPlaceContaining(@Param("loanPlace") String loanPlace);

    /**
     * 根据日期范围查询租赁记录
     * @param startDateTime 开始日期时间
     * @param endDateTime 结束日期时间
     * @return 租赁记录列表
     */
    @Query("SELECT r FROM Rental r WHERE r.loanDate BETWEEN :startDateTime AND :endDateTime")
    List<Rental> findByDateBetween(@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);

    /**
     * 根据金额范围查询租赁记录
     * @param minAmount 最小金额
     * @param maxAmount 最大金额
     * @return 租赁记录列表
     */
    @Query("SELECT r FROM Rental r WHERE r.amount BETWEEN :minAmount AND :maxAmount")
    List<Rental> findByAmountBetween(@Param("minAmount") BigDecimal minAmount, @Param("maxAmount") BigDecimal maxAmount);

    /**
     * 多条件查询租赁记录
     * @param cNo 客户号
     * @param dNo 司机号
     * @param carId 车辆ID
     * @param startDateTime 开始日期时间（可空）
     * @param endDateTime 结束日期时间（可空）
     * @return 租赁记录列表
     */
    @Query("SELECT r FROM Rental r WHERE " +
           "(:cNo IS NULL OR r.cNo = :cNo) AND " +
           "(:dNo IS NULL OR r.dNo = :dNo) AND " +
           "(:carId IS NULL OR r.carId = :carId) AND " +
           "(:startDateTime IS NULL OR r.loanDate >= :startDateTime) AND " +
           "(:endDateTime IS NULL OR r.loanDate <= :endDateTime)")
    List<Rental> findByMultipleConditions(
        @Param("cNo") String cNo,
        @Param("dNo") String dNo,
        @Param("carId") String carId,
        @Param("startDateTime") LocalDateTime startDateTime,
        @Param("endDateTime") LocalDateTime endDateTime
    );

    /**
     * 统计客户的租赁次数
     * @param cNo 客户号
     * @return 租赁次数
     */
    @Query("SELECT COUNT(r) FROM Rental r WHERE r.cNo = :cNo")
    long countByCNo(@Param("cNo") String cNo);

    /**
     * 统计司机的出车次数
     * @param dNo 司机号
     * @return 出车次数
     */
    @Query("SELECT COUNT(r) FROM Rental r WHERE r.dNo = :dNo")
    long countByDNo(@Param("dNo") String dNo);

    /**
     * 统计车辆的租赁次数
     * @param carId 车辆ID
     * @return 租赁次数
     */
    @Query("SELECT COUNT(r) FROM Rental r WHERE r.carId = :carId")
    long countByCarId(@Param("carId") String carId);

    /**
     * 计算总收入
     * @return 总收入
     */
    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM Rental r")
    BigDecimal calculateTotalRevenue();

    /**
     * 计算指定日期范围内的收入
     * @param startDateTime 开始日期时间
     * @param endDateTime 结束日期时间
     * @return 收入金额
     */
    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM Rental r WHERE r.loanDate BETWEEN :startDateTime AND :endDateTime")
    BigDecimal calculateRevenueByDateRange(@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);
}