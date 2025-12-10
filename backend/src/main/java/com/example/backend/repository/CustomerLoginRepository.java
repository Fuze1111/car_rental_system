package com.example.backend.repository;

import com.example.backend.entity.CustomerLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 客户登录数据访问接口
 */
@Repository
public interface CustomerLoginRepository extends JpaRepository<CustomerLogin, String> {
    
    /**
     * 根据客户号查找
     * @param cNo 客户号
     * @return 客户登录信息
     */
    @Query("SELECT cl FROM CustomerLogin cl WHERE cl.cNo = :cNo")
    Optional<CustomerLogin> findByCNo(@Param("cNo") String cNo);
    
    /**
     * 根据客户号或手机号查找（用于登录）
     * @param identifier 客户号或手机号
     * @return 客户登录信息
     */
    @Query("SELECT cl FROM CustomerLogin cl LEFT JOIN cl.customer c WHERE cl.cNo = :identifier OR c.phone = :identifier")
    Optional<CustomerLogin> findByIdentifier(@Param("identifier") String identifier);
}
