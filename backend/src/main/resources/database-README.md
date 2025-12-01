# 汽车租赁系统数据库设计

## 📋 数据库表结构

本系统共包含 **6 张数据表**，用于管理汽车租赁业务的各个方面。

### 1️⃣ Customer（客户表） - 表 3.1
存储客户基本信息。

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| CNo | CHAR(8) | 主键 | 客户号 |
| CName | CHAR(50) | NOT NULL | 客户姓名 |
| Company | CHAR(100) | NULL | 客户单位 |
| Phone | CHAR(20) | NULL | 客户电话 |
| Address | CHAR(200) | NULL | 客户地址 |
| PostCode | CHAR(6) | NULL | 邮编 |

---

### 2️⃣ Driver（司机表） - 表 3.2
存储司机基本信息和工作状态。

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| DNo | CHAR(8) | 主键 | 司机号 |
| DName | CHAR(50) | NOT NULL | 姓名 |
| StartWork | DATE | NULL | 入职年月 |
| Salary | NUMERIC(6,2) | NULL | 基本工资 |
| DStatus | CHAR(5) | NULL | 司机状态（在岗/空闲等） |
| LicenseType | CHAR(10) | NULL | 准驾车型 |

---

### 3️⃣ CarType（汽车类型表） - 表 3.5
定义汽车型号和租金标准。

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| TNo | CHAR(4) | 联合主键 | 类型编号 |
| TName | CHAR(20) | 联合主键 | 汽车型号 |
| RentRate | NUMERIC(4,2) | NULL | 租金率 |

**注意**：`(TNo, TName)` 组成联合主键。

---

### 4️⃣ Car（汽车表） - 表 3.3
存储车辆详细信息。

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| CarID | CHAR(10) | 主键 | 注册号码 |
| PurchaseDate | DATE | NULL | 购入日期 |
| Price | NUMERIC(8,2) | NULL | 价格 |
| CarStatus | CHAR(5) | NULL | 车辆状态（0-可租；1-已租；2-维修） |
| TNo | CHAR(4) | 外键 | 类型编号 |
| TName | CHAR(20) | 外键 | 汽车型号 |

**外键关系**：`(TNo, TName)` → `CarType(TNo, TName)`

---

### 5️⃣ CarRepair（汽车维修表） - 表 3.4
记录车辆维修历史。

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| RNo | INT | 主键，自增 | 维修编号 |
| RDate | DATE | NULL | 维修日期 |
| RunKM | NUMERIC(10,2) | NULL | 运行公里 |
| RunHour | NUMERIC(8,2) | NULL | 运行小时 |
| CarID | CHAR(10) | 外键 | 注册号码 |

**外键关系**：`CarID` → `Car(CarID)`

---

### 6️⃣ Rental（租赁业务表） - 表 3.6
记录每笔租赁业务的详细信息。

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| InvoiceNo | CHAR(20) | 主键 | 发票号 |
| Date | DATE | NULL | 日期 |
| Place | CHAR(100) | NULL | 地点 |
| CarID | CHAR(10) | 外键 | 牌照号 |
| CNo | CHAR(8) | 外键 | 客户号 |
| DNo | CHAR(8) | 外键 | 司机号 |
| RoundTripDistance | NUMERIC(8,2) | NULL | 往返里程 |
| DriveTime | NUMERIC(4,2) | NULL | 行驶时间 |
| Amount | NUMERIC(8,2) | NULL | 应付金额 |

**外键关系**：
- `CarID` → `Car(CarID)`
- `CNo` → `Customer(CNo)`
- `DNo` → `Driver(DNo)`

---

## 🔗 表关系图

```
Customer (客户)
    ↓ 1:N
Rental (租赁业务) ← N:1 → Driver (司机)
    ↓ N:1
Car (汽车) ← 1:N → CarRepair (维修记录)
    ↓ N:1
CarType (汽车类型)
```

---

## 🚀 使用说明

### 自动初始化（推荐）

Spring Boot 会在应用启动时自动执行以下脚本：

1. **`schema.sql`** - 创建所有表结构
2. **`data.sql`** - 插入测试数据

### 配置文件

确保 `application.properties` 中包含以下配置：

```properties
# 数据库连接
spring.datasource.url=jdbc:mysql://localhost:3306/car_rental_db
spring.datasource.username=root
spring.datasource.password=your_password

# 禁用 Hibernate 自动建表
spring.jpa.hibernate.ddl-auto=none

# 启用 SQL 脚本初始化
spring.sql.init.mode=always
spring.sql.init.schema-locations=classpath:schema.sql
spring.sql.init.data-locations=classpath:data.sql
```

### 手动执行（可选）

如果需要手动执行 SQL 脚本：

```bash
# 连接到 MySQL
mysql -u root -p

# 创建数据库
CREATE DATABASE IF NOT EXISTS car_rental_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 使用数据库
USE car_rental_db;

# 执行脚本
SOURCE /path/to/schema.sql;
SOURCE /path/to/data.sql;
```

---

## ⚠️ 注意事项

1. **开发环境**：每次启动会重建表（`DROP TABLE IF EXISTS`），数据会丢失
2. **生产环境**：建议设置 `spring.sql.init.mode=never`，避免数据丢失
3. **外键约束**：删除表时需要按依赖顺序（Rental → CarRepair → Car → CarType, Driver, Customer）
4. **字符集**：使用 `utf8mb4` 支持完整的 Unicode 字符（包括 emoji）

---

## 📝 测试数据说明

`data.sql` 包含以下测试数据：

- **3 个客户**：张三（阿里巴巴）、李四（腾讯）、王五（个人）
- **3 名司机**：赵六、钱七、孙八
- **4 种车型**：丰田凯美瑞、本田雅阁、奔驰E级、宝马5系
- **4 辆汽车**：京A12345、京B67890、京C11111、京D22222
- **2 条维修记录**
- **3 笔租赁业务**

可根据实际需要修改或删除测试数据。

