# 汽车租赁系统 - 车辆信息管理模块

## 项目概述

这是一个基于Spring Boot开发的汽车租赁系统后端服务，当前已完成**车辆信息管理**模块的开发。

### 功能需求

#### 已完成功能
1. ✅ **车辆信息管理**
   - 记录车辆的基本信息（车牌号、购入日期、价格等）
   - 管理车辆当前状态（可租、已租、维修中）
   - 维修记录管理
   - 多条件查询功能
   - 车辆统计信息

#### 待开发功能
2. ⏳ 司机信息管理（司机个人信息、工龄、工资、出车状态）
3. ⏳ 租赁业务管理（发票号、行程记录、客户和司机信息）
4. ⏳ 客户信息管理（客户号、姓名、电话等）
5. ⏳ 多条件查询优化（车辆状态、司机安排、费用计算等）

---

## 技术栈

- **框架**: Spring Boot 3.5.7
- **JDK**: Java 21
- **数据库**: MySQL 8.0
- **ORM**: Spring Data JPA / Hibernate
- **工具**: Lombok, Maven
- **API风格**: RESTful

---

## 项目结构

```
backend/
├── src/main/java/com/example/backend/
│   ├── BackendApplication.java          # 应用启动类
│   ├── config/
│   │   └── CorsConfig.java              # 跨域配置
│   ├── controller/                       # 控制层（RESTful API）
│   │   ├── CarController.java           # 车辆信息API
│   │   ├── CarRepairController.java     # 维修记录API
│   │   └── TestController.java          # 测试API
│   ├── dto/                              # 数据传输对象
│   │   ├── CarRequestDTO.java           # 车辆请求DTO
│   │   ├── CarResponseDTO.java          # 车辆响应DTO
│   │   ├── CarRepairRequestDTO.java     # 维修请求DTO
│   │   └── CarRepairResponseDTO.java    # 维修响应DTO
│   ├── entity/                           # 实体类
│   │   ├── Car.java                     # 车辆实体
│   │   ├── CarType.java                 # 车型实体
│   │   └── CarRepair.java               # 维修记录实体
│   ├── exception/                        # 异常处理
│   │   ├── ErrorResponse.java           # 错误响应模型
│   │   └── GlobalExceptionHandler.java  # 全局异常处理器
│   ├── repository/                       # 数据访问层
│   │   ├── CarRepository.java           # 车辆数据访问
│   │   ├── CarTypeRepository.java       # 车型数据访问
│   │   └── CarRepairRepository.java     # 维修数据访问
│   └── service/                          # 业务逻辑层
│       ├── CarService.java              # 车辆业务逻辑
│       └── CarRepairService.java        # 维修业务逻辑
├── src/main/resources/
│   ├── application.properties           # 应用配置
│   ├── schema.sql                       # 数据库表结构
│   └── data.sql                         # 初始化数据
├── API_DOCUMENTATION.md                  # API文档
├── DATABASE_SETUP.md                     # 数据库设置说明
└── pom.xml                              # Maven配置
```

---

## 快速开始

### 前置条件

1. 安装 JDK 17 或更高版本
2. 安装 MySQL 8.0
3. 安装 Maven（或使用项目自带的 mvnw）

### 数据库配置

1. 启动MySQL服务

2. 修改 `src/main/resources/application.properties` 中的数据库配置：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/car_rental_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=你的密码
```

3. 数据库表和初始数据会在应用启动时自动创建

### 启动应用

#### 方式1: 使用IDE
在IDE中直接运行 `BackendApplication.java`

#### 方式2: 使用Maven命令
```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

#### 方式3: 打包后运行
```bash
# 打包
.\mvnw.cmd clean package

# 运行
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

### 验证安装

应用启动后，访问测试接口：
```
http://localhost:8080/api/test
```

应该返回：`Backend is running!`

---

## API使用说明

详细的API文档请参考：[API_DOCUMENTATION.md](API_DOCUMENTATION.md)

### 快速测试示例

1. **获取所有车辆**
```bash
curl http://localhost:8080/api/cars
```

2. **查询可租车辆**
```bash
curl http://localhost:8080/api/cars/status/0
```

3. **获取车辆统计**
```bash
curl http://localhost:8080/api/cars/statistics
```

4. **创建新车辆**
```bash
curl -X POST http://localhost:8080/api/cars \
  -H "Content-Type: application/json" \
  -d '{
    "carId": "京F88888",
    "purchaseDate": "2025-12-08",
    "price": 500000.00,
    "carStatus": "0",
    "tNo": "T003",
    "tName": "奔驰E级"
  }'
```

---

## 数据模型

### 车辆状态 (CarStatus)
- `0` - 可租：车辆可用，可以出租
- `1` - 已租：车辆已被客户租用
- `2` - 维修中：车辆正在维修，不可出租

### 实体关系
- **Car** (车辆) ←→ **CarType** (车型)：多对一关系
- **Car** (车辆) ←→ **CarRepair** (维修记录)：一对多关系

---

## 核心功能特性

### 1. 车辆信息管理
- ✅ CRUD完整操作（创建、读取、更新、删除）
- ✅ 多条件组合查询
- ✅ 车辆状态管理
- ✅ 统计信息查询

### 2. 维修记录管理
- ✅ 维修记录的增删改查
- ✅ 按车辆查询维修历史
- ✅ 维修次数统计
- ✅ 自动更新车辆状态

### 3. 业务规则
- ✅ 车牌号唯一性验证
- ✅ 车型存在性验证
- ✅ 创建维修记录时自动将车辆状态设为"维修中"
- ✅ 事务管理确保数据一致性

### 4. 异常处理
- ✅ 全局异常处理器
- ✅ 统一错误响应格式
- ✅ 友好的错误提示信息

---

## 开发规范

### 分层架构
```
Controller → Service → Repository → Entity
    ↓          ↓          ↓           ↓
  API层    业务逻辑层   数据访问层   数据模型层
```

### 命名规范
- **Entity**: 实体类，如 `Car.java`
- **DTO**: 数据传输对象，如 `CarRequestDTO.java`, `CarResponseDTO.java`
- **Repository**: 数据访问接口，如 `CarRepository.java`
- **Service**: 业务逻辑类，如 `CarService.java`
- **Controller**: 控制器类，如 `CarController.java`

### 代码风格
- 使用Lombok简化代码（@Data, @NoArgsConstructor, @AllArgsConstructor等）
- 使用Optional处理可能为空的返回值
- 使用Stream API处理集合操作
- 统一使用RESTful API风格

---

## 测试数据

系统启动时会自动加载以下测试数据：

### 车型数据
- T001 - 丰田凯美瑞 (每小时租金: 30.00元/小时，每公里租金: 0.60元/公里)
- T002 - 本田雅阁 (每小时租金: 35.00元/小时，每公里租金: 0.70元/公里)
- T003 - 奔驰E级 (每小时租金: 60.00元/小时，每公里租金: 1.20元/公里)
- T004 - 宝马5系 (每小时租金: 70.00元/小时，每公里租金: 1.40元/公里)
- T005 - 奥迪A6L (每小时租金: 55.00元/小时，每公里租金: 1.10元/公里)
- T006 - 大众帕萨特 (每小时租金: 25.00元/小时，每公里租金: 0.50元/公里)

### 车辆数据
- 京A12345 - 丰田凯美瑞 (状态: 可租)
- 京B67890 - 本田雅阁 (状态: 已租)
- 京C11111 - 奔驰E级 (状态: 可租)
- 京D22222 - 宝马5系 (状态: 维修中)

---

## 常见问题

### Q1: 数据库连接失败？
**A**: 检查MySQL服务是否启动，以及 `application.properties` 中的用户名和密码是否正确。

### Q2: 端口8080被占用？
**A**: 修改 `application.properties` 中的 `server.port` 配置。

### Q3: 表结构没有自动创建？
**A**: 确认 `spring.sql.init.mode=always` 配置是否正确。

### Q4: 跨域问题？
**A**: 已在 `CorsConfig.java` 中配置全局跨域支持，允许所有来源访问。

---

## 后续开发计划

### 第二阶段：司机信息管理
- [ ] 司机实体和数据库映射
- [ ] 司机CRUD接口
- [ ] 司机状态管理
- [ ] 工龄和工资计算

### 第三阶段：客户信息管理
- [ ] 客户实体和数据库映射
- [ ] 客户CRUD接口
- [ ] 客户信息查询

### 第四阶段：租赁业务管理
- [ ] 租赁订单管理
- [ ] 行程记录
- [ ] 费用计算
- [ ] 客户-司机-车辆关联

### 第五阶段：高级查询和统计
- [ ] 复杂多表查询
- [ ] 数据统计和报表
- [ ] 收益分析

---

## 贡献指南

如需扩展新功能，请遵循以下步骤：

1. 在 `entity` 包中创建实体类
2. 在 `repository` 包中创建数据访问接口
3. 在 `dto` 包中创建请求和响应DTO
4. 在 `service` 包中实现业务逻辑
5. 在 `controller` 包中创建RESTful API
6. 更新API文档

---

## 联系方式

如有问题或建议，请通过以下方式联系：
- 提交Issue
- 发送邮件

---

## 许可证

本项目仅用于学习和教育目的。

