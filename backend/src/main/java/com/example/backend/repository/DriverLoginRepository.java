package com.example.backend.repository;

import com.example.backend.entity.DriverLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 司机登录数据访问接口
 */
@Repository
public interface DriverLoginRepository extends JpaRepository<DriverLogin, String> {
    
    /**
     * 根据司机号查找
     * @param dNo 司机号
     * @return 司机登录信息
     */
    @Query("SELECT dl FROM DriverLogin dl WHERE dl.dNo = :dNo")
    Optional<DriverLogin> findByDNo(@Param("dNo") String dNo);
    
    /**
     * 根据司机号或手机号查找（用于登录）
     * @param identifier 司机号或手机号
     * @return 司机登录信息
     */
    @Query("SELECT dl FROM DriverLogin dl WHERE dl.dNo = :identifier OR dl.phone = :identifier")
    Optional<DriverLogin> findByIdentifier(@Param("identifier") String identifier);
    
    /**
     * 根据手机号查找
     * @param phone 手机号
     * @return 司机登录信息
     */
    @Query("SELECT dl FROM DriverLogin dl WHERE dl.phone = :phone")
    Optional<DriverLogin> findByPhone(@Param("phone") String phone);
}
