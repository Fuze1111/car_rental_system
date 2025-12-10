package com.example.backend.repository;

import com.example.backend.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 客户信息Repository
 * 提供客户信息的数据访问操作
 *
 * 注意：所有查询方法都使用 @Query 注解，避免 Spring Data JPA 方法命名解析的大小写问题
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    /**
     * 根据客户号模糊查询
     * @param cNo 客户号关键字
     * @return 客户列表
     */
    @Query("SELECT c FROM Customer c WHERE c.cNo LIKE CONCAT('%', :cNo, '%')")
    List<Customer> findByCNoContaining(@Param("cNo") String cNo);

    /**
     * 根据客户姓名模糊查询
     * @param cName 客户姓名关键字
     * @return 客户列表
     */
    @Query("SELECT c FROM Customer c WHERE c.cName LIKE CONCAT('%', :cName, '%')")
    List<Customer> findByCNameContaining(@Param("cName") String cName);

    /**
     * 根据电话号码查询客户
     * @param phone 电话号码
     * @return 客户列表
     */
    @Query("SELECT c FROM Customer c WHERE c.phone = :phone")
    List<Customer> findByPhone(@Param("phone") String phone);

    /**
     * 根据电话号码模糊查询客户
     * @param phone 电话号码关键字
     * @return 客户列表
     */
    @Query("SELECT c FROM Customer c WHERE c.phone LIKE CONCAT('%', :phone, '%')")
    List<Customer> findByPhoneContaining(@Param("phone") String phone);

    /**
     * 根据公司名称查询客户
     * @param company 公司名称
     * @return 客户列表
     */
    @Query("SELECT c FROM Customer c WHERE c.company = :company")
    List<Customer> findByCompany(@Param("company") String company);

    /**
     * 根据公司名称模糊查询客户
     * @param company 公司名称关键字
     * @return 客户列表
     */
    @Query("SELECT c FROM Customer c WHERE c.company LIKE CONCAT('%', :company, '%')")
    List<Customer> findByCompanyContaining(@Param("company") String company);

    /**
     * 根据地址模糊查询客户
     * @param address 地址关键字
     * @return 客户列表
     */
    @Query("SELECT c FROM Customer c WHERE c.address LIKE CONCAT('%', :address, '%')")
    List<Customer> findByAddressContaining(@Param("address") String address);

    /**
     * 多条件查询客户
     * @param cName 客户姓名
     * @param phone 电话号码
     * @param company 公司名称
     * @return 客户列表
     */
    @Query("SELECT c FROM Customer c WHERE " +
           "(:cName IS NULL OR c.cName LIKE CONCAT('%', :cName, '%')) AND " +
           "(:phone IS NULL OR c.phone LIKE CONCAT('%', :phone, '%')) AND " +
           "(:company IS NULL OR c.company LIKE CONCAT('%', :company, '%'))")
    List<Customer> findByMultipleConditions(
        @Param("cName") String cName,
        @Param("phone") String phone,
        @Param("company") String company
    );

    /**
     * 统计公司客户数量
     * @param company 公司名称
     * @return 客户数量
     */
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.company = :company")
    long countByCompany(@Param("company") String company);
    
    /**
     * 统计有公司的客户数量
     * @return 有公司的客户数量
     */
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.company IS NOT NULL AND TRIM(c.company) <> ''")
    long countWithCompany();
}