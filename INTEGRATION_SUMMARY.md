# 前后端集成完成总结

## 🎉 完成的工作

本次更新成功实现了汽车租赁系统的登录和注册功能的前后端集成，并在此基础上逐步打通了车辆管理、租赁业务以及个人中心订单展示等核心链路。

## 📋 修改内容

### 一、后端修改

#### 1. 用户编号生成优化

**文件**: `backend/src/main/java/com/example/backend/service/AuthService.java`

- ✅ 移除随机生成逻辑
- ✅ 实现客户号依次递增（C0000001, C0000002, ...）
- ✅ 实现司机号依次递增（D0000001, D0000002, ...）

**文件**: `backend/src/main/java/com/example/backend/repository/CustomerRepository.java`

- ✅ 添加 `findMaxCNo()` 方法查询最大客户号

**文件**: `backend/src/main/java/com/example/backend/repository/DriverRepository.java`

- ✅ 添加 `findMaxDNo()` 方法查询最大司机号

#### 2. 租赁业务与结算逻辑

**核心文件**：
- `backend/src/main/java/com/example/backend/entity/Rental.java`
- `backend/src/main/java/com/example/backend/repository/RentalRepository.java`
- `backend/src/main/java/com/example/backend/service/RentalService.java`
- `backend/src/main/java/com/example/backend/controller/RentalController.java`

**主要能力**：
- ✅ 支持创建租赁记录（借车时间/还车时间、取还车地点、车辆、客户、司机）。
- ✅ 在结算时：
  - 统一根据 `LoanDate` / `ReturnDate` 计算本次行驶时间 `DriveTime`（小时，向上取整）；
  - 根据车型 `RentRate`、行驶时间、往返里程 `RoundTripDistance`、固定服务费以及司机服务费计算本次 `Amount`；
  - 采用“增量”方式，将本次租赁新增的里程/小时累加到车辆表 `RUNKM` / `RunHour`：
    - `deltaKm   = newRoundTripDistance - oldRoundTripDistance`
    - `deltaHour = newDriveTime - oldDriveTime`
- ✅ 金额非空时，视为订单已结算：
  - 将车辆状态恢复为可租（CarStatus = `0`）；
  - 如存在司机，将司机状态恢复为“空闲”。

#### 3. 车辆累计里程与运行小时

**文件**：`backend/src/main/java/com/example/backend/entity/Car.java`

- ✅ 新增 / 明确字段：
  - `runKm`（数据库字段 `RUNKM`）：车辆累计运行公里数；
  - `runHour`（数据库字段 `RunHour`）：车辆累计运行小时数。
- ✅ 与租赁结算集成：每次结算调用 `RentalService.updateRental` 时，按增量更新上述两个字段，避免重复累加。

#### 4. 数据库文档与 API 文档同步

- ✅ `backend/src/main/resources/database-README.md`
  - 补充 `RUNKM` / `RunHour` 字段含义以及与 `Rental.RoundTripDistance` / `DriveTime` 的关系；
  - 补充 `Rental` 表中行程字段和金额字段的业务意义；
  - 增加 `CarTypeImage.ImagePath` 的说明。
- ✅ `backend/API_DOCUMENTATION.md`
  - 新增“租赁业务管理 API”章节，详细说明：
    - 创建租赁记录 `POST /api/rentals`；
    - 结算接口 `PUT /api/rentals/{invoiceNo}` 的业务逻辑；
    - 金额计算规则 `calculateRentalAmount`；
    - 按客户 / 司机 / 车辆查询租赁记录；
    - 租赁收入统计接口示例。

### 二、前端新增与调整

#### 1. API 服务层

**新建文件**: `frontend/src/api/request.js`

- ✅ Axios实例配置
- ✅ 请求拦截器（预留 token 处理位）
- ✅ 响应拦截器（统一错误处理）

**新建文件**: `frontend/src/api/auth.js`

- ✅ `login()` - 登录接口
- ✅ `registerCustomer()` - 客户注册接口
- ✅ `registerDriver()` - 司机注册接口
- ✅ `testApi()` - Auth 测试接口

**扩展文件**: `frontend/src/api/car.js`

- ✅ `getCars()` / `getCarById()` / `getCarTypes()` 等车辆/车型 API 调用封装；
- ✅ 响应中暴露 `runKm` / `runHour` 等字段，供前端展示车辆累计运行里程与小时数。

**新建文件**: `frontend/src/api/rental.js`

- ✅ `createRental()` - 前端选车下单调用，用于创建租赁记录；
- ✅ `getRentalsByCustomer(cNo)` - 个人中心按客户查询订单列表；
- ✅ （预留）`getRentalsByDriver(dNo)` / `getRentalsByCar(carId)` 等接口封装。

#### 2. 认证工具函数

**新建文件**: `frontend/src/utils/auth.js`

- ✅ `setUserInfo()` / `getUserInfo()` - 用户信息管理（LocalStorage）
- ✅ `setUserRole()` / `getUserRole()` - 用户角色管理
- ✅ `setToken()` / `getToken()` - Token 管理（预留）
- ✅ `clearAuth()` - 清除认证信息
- ✅ `isLoggedIn()` - 登录状态检查

#### 3. 前端页面更新

**修改文件**: `frontend/src/views/Login.vue`

- ✅ 引入认证 API 与工具函数
- ✅ 移除模拟登录逻辑，改为真实接口调用
- ✅ 登录成功后持久化保存用户信息与角色
- ✅ 完善错误处理

**修改文件**: `frontend/src/views/Register.vue`

- ✅ 引入注册 API
- ✅ 实现客户/司机注册界面切换与对应接口调用
- ✅ 增强表单校验（手机号格式、密码长度、一致性等）

**新增 / 调整文件**: `frontend/src/views/user/UserProfile.vue`

- ✅ 从登录信息中获取当前客户编号 `CNo`；
- ✅ 调用 `getRentalsByCustomer(CNo)` 获取“现有订单”和“历史订单”；
- ✅ 根据是否有 `amount` 字段，将订单拆分为：
  - 现有订单（进行中且未结算）；
  - 历史订单（已结算或已完成）；
- ✅ 在订单详情中展示：
  - 基本信息：订单号、车辆、车型、司机（自驾或司机姓名）;
  - 费用与数据：金额状态（`待结算` / `¥金额`）、订单状态（`进行中` / `已完成`）、行驶里程、行驶时长；
  - 行程路线：借车/还车时间与地点；
- ✅ 计算并展示“基本费用 + 里程费用”：
  - 基本费用根据车型 `rentRate`、租期天数、服务费、司机服务费等计算；
  - 里程费用 = `amount - 基本费用`。

### 三、项目文档

#### 1. 新增 / 扩展文档

**`FRONTEND_API_README.md`**
- ✅ 记录前端 API 使用说明；
- ✅ 增加车辆与租赁相关 API 示例；
- ✅ 说明个人中心订单展示所依赖的字段。

**`INTEGRATION_TEST_GUIDE.md`**
- ✅ 提供登录/注册集成测试步骤；
- （后续计划）补充租赁业务与订单展示集成测试场景。

**`QUICK_START.md`**
- ✅ 快速启动指引；
- ✅ 环境配置说明；
- ✅ 项目结构说明与功能概览。

**Windows 启动脚本**:
- ✅ `start-backend.cmd` - 后端启动脚本
- ✅ `start-frontend.cmd` - 前端启动脚本

---

## 🧱 技术栈

### 后端
- Spring Boot 3.x
- Spring Data JPA
- MySQL 8.0
- BCrypt 密码加密
- Maven

### 前端
- Vue 3 (Composition API)
- Vite
- Element Plus
- Axios
- Vue Router
- SCSS

---

## 📈 已实现的主要功能

### 1. 用户认证

- ✅ 客户注册 / 登录
- ✅ 司机注册 / 登录
- ✅ 管理员登录
- ✅ 客户号 / 司机号自动递增生成
- ✅ 密码加密存储与校验

### 2. 车辆与维修管理（见 `backend/API_DOCUMENTATION.md`）

- ✅ 车辆增删改查
- ✅ 按状态、按车型、多条件检索
- ✅ 车辆统计信息
- ✅ 维修记录创建 / 查询 / 统计

### 3. 租赁业务与订单展示

- ✅ 订单创建：在前端选车后创建 `Rental` 记录；
- ✅ 订单结算：管理员录入实际行程，后端统一计算行驶时间和应付金额；
- ✅ 车辆累计里程/小时：根据每次订单的增量里程/小时，累加到 `Car.RUNKM` / `Car.RunHour`；
- ✅ 个人中心订单：客户可查看当前进行中订单与历史订单，金额状态、订单状态、行程数据与费用拆分一目了然。

---

## 🔐 安全特性

- ✅ 密码使用 BCrypt 加密存储
- ✅ 避免重复注册（手机号唯一性约束）
- ✅ 表单输入校验
- ✅ 使用 JPA 防止 SQL 注入
- ✅ CORS 配置，保证前后端跨域访问

---

## 📂 文件清单（与本次集成相关）

### 新建文件
```
frontend/src/api/request.js
frontend/src/api/auth.js
frontend/src/api/car.js
frontend/src/api/rental.js
frontend/src/utils/auth.js
FRONTEND_API_README.md
INTEGRATION_TEST_GUIDE.md
QUICK_START.md
start-backend.cmd
start-frontend.cmd
INTEGRATION_SUMMARY.md (本文档)
```

### 修改文件（节选）
```
backend/src/main/java/com/example/backend/service/AuthService.java
backend/src/main/java/com/example/backend/repository/CustomerRepository.java
backend/src/main/java/com/example/backend/repository/DriverRepository.java
backend/src/main/java/com/example/backend/service/RentalService.java
backend/src/main/resources/database-README.md
backend/API_DOCUMENTATION.md
frontend/src/views/Login.vue
frontend/src/views/Register.vue
frontend/src/views/user/UserProfile.vue
frontend/src/api/car.js
frontend/src/api/rental.js
```

---

## 🚀 如何使用

### 快速启动

1. **启动后端**
   - Windows: 双击 `start-backend.cmd`
   - Linux/Mac: `cd backend && ./mvnw spring-boot:run`

2. **启动前端**
   - Windows: 双击 `start-frontend.cmd`
   - Linux/Mac: `cd frontend && npm install && npm run dev`

3. **访问应用**
   - 打开浏览器访问: `http://localhost:5173`

### 详细指引

- 📖 快速启动: 参见 [QUICK_START.md](./QUICK_START.md)
- 📖 功能测试: 参见 [INTEGRATION_TEST_GUIDE.md](./INTEGRATION_TEST_GUIDE.md)
- 📖 前端 API 文档: 参见 [FRONTEND_API_README.md](./FRONTEND_API_README.md)
- 📖 后端 API 文档: 参见 `backend/API_DOCUMENTATION.md`

---

## ✅ 测试检查清单

- [ ] 后端服务成功启动在 8080 端口
- [ ] 前端服务成功启动在 5173 端口
- [ ] Auth 测试接口返回 `{ "message": "Auth API is working" }`
- [ ] 客户注册成功，生成 C0000001
- [ ] 客户登录成功，LocalStorage 中有正确的 `userInfo` / `userRole`
- [ ] 司机注册成功，生成 D0000001
- [ ] 司机登录成功
- [ ] 重复手机号注册被拒绝
- [ ] 错误密码登录被拒绝
- [ ] 表单校验正常工作
- [ ] 多次注册客户号和司机号递增无误
- [ ] 创建至少 1 笔租赁订单，数据库中 `rental` 表有记录
- [ ] 管理员结算后，`rental.Amount` / `RoundTripDistance` / `DriveTime` 正确写入
- [ ] 对应车辆的 `RUNKM` / `RunHour` 按预期增加
- [ ] 客户个人中心能看到进行中订单和历史订单，金额状态和订单状态显示正确

---

## 🎯 下一步建议

1. **完善路由守卫**
   - 实现基于角色的访问控制（客户/司机/管理员）;
   - 未登录访问受限页面时重定向到登录页。

2. **实现退出登录**
   - 清除本地用户信息与 token;
   - 跳转回登录页。

3. **完善 Token 机制**
   - 引入 JWT token 生成与校验;
   - 在 Axios 拦截器中自动附加与刷新 token。

4. **丰富统计报表**
   - 使用租赁收入统计接口构建后端数据看板;
   - 增加按车辆/客户/司机维度的多维度统计报表。

5. **优化车型图片与资源管理**
   - 利用 `CarTypeImage.ImagePath` 存储车型图片文件名;
   - 前端统一从静态资源或 CDN 加载图片，实现车型图片统一管理。
