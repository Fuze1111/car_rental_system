# 客户信息管理功能实现总结

## ✅ 已完成的工作

### 1. 实体层（Entity）
- ✅ `Customer.java` - 客户实体类（已存在）
  - 正确使用 `@Column(name = "CNo")` 等大写列名
  - 避免了列名大小写问题

### 2. 数据访问层（Repository）
- ✅ `CustomerRepository.java` - 客户数据访问接口
  - **所有方法都使用 `@Query` 注解**，避免 Spring Data JPA 方法命名解析问题
  - 姓名模糊查询：`findByCNameContaining()`
  - 电话查询：`findByPhone()`, `findByPhoneContaining()`
  - 公司查询：`findByCompany()`, `findByCompanyContaining()`
  - 地址模糊查询：`findByAddressContaining()`
  - 多条件查询：`findByMultipleConditions()`
  - 统计查询：`countByCompany()`

### 3. 数据传输层（DTO）
- ✅ `CustomerRequestDTO.java` - 接收前端请求的DTO
- ✅ `CustomerResponseDTO.java` - 返回给前端的DTO
  - 额外包含 `rentalCount`（租赁次数）

### 4. 业务逻辑层（Service）
- ✅ `CustomerService.java` - 客户业务逻辑处理
  - 获取所有客户：`getAllCustomers()`
  - 按ID查询：`getCustomerById()`
  - 创建客户：`createCustomer()`
  - 更新客户：`updateCustomer()`
  - 删除客户：`deleteCustomer()`
  - 姓名模糊查询：`searchCustomersByName()`
  - 电话查询：`searchCustomersByPhone()`
  - 公司查询：`getCustomersByCompany()`, `searchCustomersByCompany()`
  - 地址查询：`searchCustomersByAddress()`
  - 多条件查询：`searchCustomers()`
  - 更新电话：`updateCustomerPhone()`
  - 更新地址：`updateCustomerAddress()`
  - 统计信息：`getCustomerStatistics()`
  - **租赁次数自动计算**（通过 RentalRepository 统计）

### 5. 控制器层（Controller）
- ✅ `CustomerController.java` - RESTful API 接口
  - `GET /api/customers` - 获取所有客户
  - `GET /api/customers/{cNo}` - 按ID查询
  - `POST /api/customers` - 创建客户
  - `PUT /api/customers/{cNo}` - 更新客户
  - `DELETE /api/customers/{cNo}` - 删除客户
  - `GET /api/customers/search/name?cName=xxx` - 姓名模糊查询
  - `GET /api/customers/search/phone?phone=xxx` - 电话查询
  - `GET /api/customers/company/{company}` - 公司精确查询
  - `GET /api/customers/search/company?company=xxx` - 公司模糊查询
  - `GET /api/customers/search/address?address=xxx` - 地址模糊查询
  - `GET /api/customers/search` - 多条件查询
  - `PATCH /api/customers/{cNo}/phone` - 更新电话
  - `PATCH /api/customers/{cNo}/address` - 更新地址
  - `GET /api/customers/statistics` - 统计信息

### 6. 测试页面
- ✅ `test-customer-admin.html` - 完整的测试页面
  - 包含所有功能的测试界面
  - 一键插入测试数据功能（4个客户）
  - 所有请求带 `?test=true` 参数

---

## 🎯 核心功能特性

### 1. 客户个人信息管理
- 客户号（唯一标识）
- 客户姓名
- 客户单位（可选）
- 客户电话
- 客户地址
- 邮编

### 2. 多维度查询
- **姓名模糊查询**：支持部分匹配
- **电话模糊查询**：支持部分匹配
- **公司精确/模糊查询**：两种查询方式
- **地址模糊查询**：支持部分匹配
- **多条件组合查询**：姓名+电话+公司

### 3. 部分更新（PATCH）
- **更新电话**：单独更新电话号码
- **更新地址**：单独更新地址信息
- RESTful 最佳实践

### 4. 统计功能
- 客户总数
- 企业客户数量（有公司）
- 个人客户数量（无公司）

### 5. 关联信息
- **租赁次数**：自动统计每个客户的租赁次数
- 便于识别高价值客户

---

## 🔧 技术实现亮点

### 1. 避免大小写问题 ✅

**所有 Repository 方法都使用 `@Query` 注解**，完全避免 Spring Data JPA 方法命名解析的大小写陷阱：

```java
// ✅ 使用 @Query 注解，明确指定查询
@Query("SELECT c FROM Customer c WHERE c.cName LIKE CONCAT('%', :cName, '%')")
List<Customer> findByCNameContaining(@Param("cName") String cName);

@Query("SELECT c FROM Customer c WHERE c.phone = :phone")
List<Customer> findByPhone(@Param("phone") String phone);

@Query("SELECT COUNT(c) FROM Customer c WHERE c.company = :company")
long countByCompany(@Param("company") String company);
```

**不再依赖方法命名规则**，明确、安全、易维护！

### 2. 租赁次数自动统计

```java
// 在 convertToResponseDTO 中自动查询租赁次数
long rentalCount = rentalRepository.countByCNo(customer.getCNo());
dto.setRentalCount(rentalCount);
```

### 3. 多条件查询支持参数可选

```java
@Query("SELECT c FROM Customer c WHERE " +
       "(:cName IS NULL OR c.cName LIKE CONCAT('%', :cName, '%')) AND " +
       "(:phone IS NULL OR c.phone LIKE CONCAT('%', :phone, '%')) AND " +
       "(:company IS NULL OR c.company LIKE CONCAT('%', :company, '%'))")
List<Customer> findByMultipleConditions(...);
```

### 4. PATCH 方法实现部分更新

```java
@PatchMapping("/{cNo}/phone")
public ResponseEntity<CustomerResponseDTO> updateCustomerPhone(...)

@PatchMapping("/{cNo}/address")
public ResponseEntity<CustomerResponseDTO> updateCustomerAddress(...)
```

### 5. 统计功能

```java
public Map<String, Object> getCustomerStatistics() {
    long totalCount = customerRepository.count();
    long withCompanyCount = ...; // 有公司的客户
    long individualCount = ...; // 个人客户
    
    return Map.of(
        "total", totalCount,
        "withCompany", withCompanyCount,
        "individual", individualCount
    );
}
```

---

## 📊 数据库表结构

```sql
CREATE TABLE customer (
    CNo         CHAR(8)         NOT NULL,    -- 客户号（主键）
    CName       CHAR(50)        NOT NULL,    -- 客户姓名
    Company     CHAR(100)       NULL,        -- 客户单位
    Phone       CHAR(20)        NULL,        -- 客户电话
    Address     CHAR(200)       NULL,        -- 客户地址
    PostCode    CHAR(6)         NULL,        -- 邮编
    PRIMARY KEY (CNo)
);
```

---

## 🚀 使用方法

### 1. 启动应用
```bash
cd D:\Java\car_rental_system\backend
.\mvnw spring-boot:run
```

### 2. 访问测试页面
```
http://localhost:8080/test-customer-admin.html
```

### 3. 测试功能
1. 点击"插入测试数据"按钮（插入4个客户）
2. 点击"列出所有客户"查看数据
3. 测试各项查询和更新功能

---

## 📝 API 端点列表

| 方法 | 端点 | 说明 |
|------|------|------|
| GET | `/api/customers` | 获取所有客户 |
| GET | `/api/customers/{cNo}` | 按客户号查询 |
| POST | `/api/customers` | 创建客户 |
| PUT | `/api/customers/{cNo}` | 更新客户 |
| DELETE | `/api/customers/{cNo}` | 删除客户 |
| GET | `/api/customers/search/name?cName=xxx` | 姓名模糊查询 |
| GET | `/api/customers/search/phone?phone=xxx` | 电话查询 |
| GET | `/api/customers/company/{company}` | 公司精确查询 |
| GET | `/api/customers/search/company?company=xxx` | 公司模糊查询 |
| GET | `/api/customers/search/address?address=xxx` | 地址模糊查询 |
| GET | `/api/customers/search` | 多条件查询 |
| PATCH | `/api/customers/{cNo}/phone` | 更新电话 |
| PATCH | `/api/customers/{cNo}/address` | 更新地址 |
| GET | `/api/customers/statistics` | 统计信息 |

---

## ⚠️ 重要说明

### 避免了所有大小写问题！

与司机管理遇到的问题不同，客户管理**从一开始就使用了 `@Query` 注解**，完全避免了 Spring Data JPA 方法命名解析的大小写陷阱。

**经验教训**：
- ✅ **优先使用 `@Query` 注解**
- ✅ 不要依赖 Spring Data JPA 的方法命名规则
- ✅ 明确指定查询语句，更加清晰和可控

---

## 🎉 总结

客户信息管理功能已经完整实现，包括：
- ✅ 完整的后端代码（Entity、Repository、Service、Controller、DTO）
- ✅ RESTful API 接口（14个端点）
- ✅ 租赁次数自动统计
- ✅ 多维度查询功能
- ✅ PATCH 方法实现部分更新
- ✅ 统计功能
- ✅ 测试页面
- ✅ **没有大小写问题！**

所有 Repository 方法都使用 `@Query` 注解，完全避免了之前司机管理中遇到的 Spring Data JPA 方法命名解析问题。

现在可以通过测试页面进行全面的功能测试！

