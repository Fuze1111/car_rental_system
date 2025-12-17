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
| LoanDate | DATETIME | NULL | 借车日期时间 |
| ReturnDate | DATETIME | NULL | 还车日期时间 |
| LoanPlace | VARCHAR(100) | NULL | 借车地点 |
| ReturnPlace | VARCHAR(100) | NULL | 还车地点 |
| CarID | CHAR(10) | 外键 | 牌照号 |
| CNo | CHAR(8) | 外键 | 客户号 |
| DNo | CHAR(8) | 外键 | 司机号 |
| RoundTripDistance | NUMERIC(8,2) | NULL | 往返里程(公里) |
| DriveTime | NUMERIC(4,2) | NULL | 行驶时间(小时) |
| Amount | NUMERIC(8,2) | NULL | 应付金额(元) |

**外键关系**：
- `CarID` → `Car(CarID)`
- `CNo` → `Customer(CNo)`
- `DNo` → `Driver(DNo)`

**说明**：
- DriveTime单位为小时，精确到小数点后两位（如1.50表示1小时30分钟）
- RoundTripDistance单位为公里
- Amount单位为人民币元

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

---

# 数据库结构说明

## 表 3.3 Car（汽车表）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| CarID | CHAR(10) | 主键 | 注册号码 |
| PurchaseDate | DATE | NULL | 购入日期 |
| Price | NUMERIC(8,2) | NULL | 价格 |
| CarStatus | CHAR(5) | NULL | 车辆状态（0-可租；1-已租；2-维修） |
| TNo | CHAR(4) | 外键 | 类型编号 |
| TName | CHAR(20) | 外键 | 汽车型号 |

新增字段说明：

- `RUNKM` (`NUMERIC(10,2)`): 车辆**累计运行公里数**。在每次租赁订单结算时，会将本次订单的新增里程（`deltaKm = Rental.RoundTripDistance(新) - Rental.RoundTripDistance(旧)`）累加到该字段，用于反映车辆累计使用情况。
- `RunHour` (`NUMERIC(8,2)`): 车辆**累计运行小时数**。在租赁订单结算时，系统会根据 `LoanDate` / `ReturnDate` 计算本次行驶时间（小时，向上取整），并按增量方式累加到该字段。

> 注意：`RUNKM` / `RunHour` 记录的是**所有租赁行为累积后的总里程/总小时**，可用于判断车辆折旧、保养周期等；与 `car_repair` 表中的 `RunKM` / `RunHour` 属于不同维度：后者是“某次维修记录时的运行数据快照”。

## 表 3.4 CarRepair（汽车维修表）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| RNo | INT | 主键，自增 | 维修编号 |
| RDate | DATE | NULL | 维修日期 |
| RunKM | NUMERIC(10,2) | NULL | 运行公里 |
| RunHour | NUMERIC(8,2) | NULL | 运行小时 |
| CarID | CHAR(10) | 外键 | 注册号码 |

补充说明：

- `RunKM` / `RunHour`：记录车辆在本次维修前累计的运行公里 / 小时，用于维修决策和历史追踪。并不会自动同步到 `car` 表，而是作为每次维修的独立采样值。

## 表 3.6 Rental（租赁业务表）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| InvoiceNo | CHAR(20) | 主键 | 发票号 |
| LoanDate | DATETIME | NULL | 借车日期时间 |
| ReturnDate | DATETIME | NULL | 还车日期时间 |
| LoanPlace | VARCHAR(100) | NULL | 借车地点 |
| ReturnPlace | VARCHAR(100) | NULL | 还车地点 |
| CarID | CHAR(10) | 外键 | 牌照号 |
| CNo | CHAR(8) | 外键 | 客户号 |
| DNo | CHAR(8) | 外键 | 司机号 |
| RoundTripDistance | NUMERIC(8,2) | NULL | 往返里程(公里) |
| DriveTime | NUMERIC(4,2) | NULL | 行驶时间(小时) |
| Amount | NUMERIC(8,2) | NULL | 应付金额(元) |

补充业务含义：

- `RoundTripDistance` (`NUMERIC(8,2)`): 本次租赁的**往返行驶里程（公里）**。通常由管理员在还车结算时，根据行驶记录或车载设备读数录入。该值参与金额计算，也用于更新车辆表中的累计里程 `RUNKM`。
- `DriveTime` (`NUMERIC(4,2)`): 本次租赁的**行驶时间（小时）**。系统会根据 `LoanDate` / `ReturnDate` 自动计算，向上取整到整小时（有零头分钟即 +1 小时），用于金额计算和更新车辆的累计运行小时 `RunHour`。
- `Amount` (`NUMERIC(8,2)`): 本次租赁的**应付金额**。由后端统一根据车辆租金率、时间费用、里程费用、固定服务费以及司机服务费等规则计算，不依赖前端传入的金额值。

> 租赁订单结算时，系统会：
> 1. 记录旧的 `RoundTripDistance` / `DriveTime`；
> 2. 更新新值并重新计算 `Amount`；
> 3. 使用“新值 - 旧值”的增量更新对应车辆的 `RUNKM` / `RunHour`；
> 4. 当金额非空时，将车辆状态置为可租(`CarStatus = '0'`)，并将关联司机（若有）状态置为“空闲”。

## 表 4.4 CarTypeImage（车型图片表）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| TNo | CHAR(4) | 外键 | 类型编号 |
| ImagePath | VARCHAR(255) | NULL | 图片路径 |

补充说明：

- `ImagePath` (`VARCHAR(255)`): 车型图片的存储路径或文件名。推荐仅存相对路径或文件名（例如 `"sedan-basic.png"`），由后端或前端在运行时拼接静态资源前缀（如 `/static/images/car-types/`），以便后续迁移到 CDN 或对象存储时无需修改数据库记录。
- `TNo` 外键与 `car_type.TNo` 关联：同一车型类型可以存放多张图片（多条 car_type_image 记录），前端可根据业务需要选择封面图或轮播图。
