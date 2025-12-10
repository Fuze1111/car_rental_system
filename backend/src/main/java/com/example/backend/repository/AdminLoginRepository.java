package com.example.backend.repository;

import com.example.backend.entity.AdminLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 管理员登录数据访问接口
 */
@Repository
public interface AdminLoginRepository extends JpaRepository<AdminLogin, String> {
    
    /**
     * 根据用户名查找管理员
     * @param username 用户名
     * @return 管理员登录信息
     */
    Optional<AdminLogin> findByUsername(String username);
    
    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
}
