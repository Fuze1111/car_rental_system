# 车辆信息与租赁业务 API 文档

## 概述
本文档描述了汽车租赁系统中车辆信息与租赁业务管理模块的RESTful API接口。

**基础URL**: `http://localhost:8080`

---

## 车辆信息管理 API

### 1. 获取所有车辆信息
**接口**: `GET /api/cars`  
**描述**: 获取系统中所有车辆的信息列表

**查询参数**:
- `page` (可选): 页码，从0开始，默认为0
- `size` (可选): 每页大小，默认为10
- `sortBy` (可选): 排序字段，可指定多个，格式为 `字段名,方向`，方向可为 `asc`(升序) 或 `desc`(降序)，默认为 `carId,asc`

**示例**:
- `/api/cars` - 返回所有车辆信息
- `/api/cars?page=0&size=5` - 返回第1页，每页5条记录
- `/api/cars?page=0&size=5&sortBy=price,desc` - 返回按价格降序排列的第1页，每页5条记录
- `/api/cars?page=0&size=5&sortBy=price,desc&sortBy=tName,asc` - 返回按价格降序、车型升序排列的第1页，每页5条记录

**响应示例** (分页):
```
{
  "content": [
    {
      "carId": "京A12345",
      "purchaseDate": "2025-05-10",
      "price": 250000.00,
      "carStatus": "0",
      "carStatusDesc": "可租",
      "tNo": "T001",
      "tName": "丰田凯美瑞",
      "rentRate": 0.85,
      "needsRepair": false,
      "lastRepairDate": null
    }
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "pageNumber": 0,
    "pageSize": 10,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 25,
  "totalPages": 3,
  "last": false,
  "first": true,
  "numberOfElements": 10,
  "size": 10,
  "number": 0
}
```

---

### 2. 根据车牌号获取车辆信息
**接口**: `GET /api/cars/{carId}`  
**描述**: 根据车牌号获取特定车辆的详细信息

**路径参数**:
- `carId`: 车牌号（如: 京A12345）

**响应示例**:
```
{
  "carId": "京A12345",
  "purchaseDate": "2025-05-10",
  "price": 250000.00,
  "carStatus": "0",
  "carStatusDesc": "可租",
  "tNo": "T001",
  "tName": "丰田凯美瑞",
  "rentRate": 0.85,
  "needsRepair": false,
  "lastRepairDate": null
}
```

---

### 3. 创建新车辆
**接口**: `POST /api/cars`  
**描述**: 在系统中添加新车辆信息

**请求体示例**:
```
{
  "carId": "京E55555",
  "purchaseDate": "2025-12-08",
  "price": 300000.00,
  "carStatus": "0",
  "tNo": "T001",
  "tName": "丰田凯美瑞"
}
```

**响应**: 返回创建的车辆信息（201 Created）

---

### 4. 更新车辆信息
**接口**: `PUT /api/cars/{carId}`  
**描述**: 更新指定车辆的信息

**路径参数**:
- `carId`: 车牌号

**请求体示例**:
```
{
  "purchaseDate": "2025-05-10",
  "price": 260000.00,
  "carStatus": "0",
  "tNo": "T001",
  "tName": "丰田凯美瑞"
}
```

**响应**: 返回更新后的车辆信息

---

### 5. 删除车辆
**接口**: `DELETE /api/cars/{carId}`  
**描述**: 从系统中删除指定车辆

**路径参数**:
- `carId`: 车牌号

**响应**: 204 No Content

---

### 6. 根据车辆状态查询
**接口**: `GET /api/cars/status/{carStatus}`  
**描述**: 根据车辆状态查询车辆列表

**路径参数**:
- `carStatus`: 车辆状态
  - `0`: 可租
  - `1`: 已租
  - `2`: 维修中

**响应**: 返回符合状态的车辆列表

---

### 7. 根据车型查询
**接口**: `GET /api/cars/type?tNo={tNo}&tName={tName}`  
**描述**: 根据车型编号和/或车型名称查询车辆

**查询参数**:
- `tNo` (可选): 车型编号（如: T001）
- `tName` (可选): 车型名称（如: 丰田凯美瑞）

**响应**: 返回符合条件的车辆列表

---

### 8. 多条件查询车辆
**接口**: `GET /api/cars/search?carStatus={status}&tNo={tNo}&tName={tName}`  
**描述**: 支持多条件组合查询车辆

**查询参数** (全部可选):
- `carStatus`: 车辆状态
- `tNo`: 车型编号
- `tName`: 车型名称

**示例**: `/api/cars/search?carStatus=0&tNo=T001`

**响应**: 返回符合所有条件的车辆列表

---

### 9. 更新车辆状态
**接口**: `PATCH /api/cars/{carId}/status`  
**描述**: 单独更新车辆的状态（如：设置为维修中）

**路径参数**:
- `carId`: 车牌号

**请求体示例**:
```
{
  "carStatus": "2"
}
```

**响应**: 返回更新后的车辆信息

---

### 10. 获取车辆统计信息
**接口**: `GET /api/cars/statistics`  
**描述**: 获取车辆的统计信息（总数、可租、已租、维修中）

**响应示例**:
```
{
  "total": 10,
  "available": 5,
  "rented": 3,
  "repair": 2
}
```

---

## 车辆维修记录管理 API

### 1. 获取所有维修记录
**接口**: `GET /api/repairs`  
**描述**: 获取系统中所有车辆的维修记录

**响应示例**:
```
[
  {
    "rNo": 1,
    "rDate": "2025-11-15",
    "runKm": 15000.50,
    "runHour": 500.25,
    "carId": "京D22222",
    "carModel": "宝马5系"
  }
]
```

---

### 2. 根据维修编号获取维修记录
**接口**: `GET /api/repairs/{rNo}`  
**描述**: 获取指定维修记录的详细信息

**路径参数**:
- `rNo`: 维修编号

**响应**: 返回维修记录详情

---

### 3. 根据车辆号获取维修记录
**接口**: `GET /api/repairs/car/{carId}`  
**描述**: 获取指定车辆的所有维修记录（按日期倒序）

**路径参数**:
- `carId`: 车牌号

**响应**: 返回该车辆的维修记录列表

---

### 4. 创建新维修记录
**接口**: `POST /api/repairs`  
**描述**: 为车辆添加新的维修记录（会自动将车辆状态设为"维修中"）

**请求体示例**:
```
{
  "rDate": "2025-12-08",
  "runKm": 20000.00,
  "runHour": 650.50,
  "carId": "京A12345"
}
```

**响应**: 返回创建的维修记录（201 Created）

---

### 5. 更新维修记录
**接口**: `PUT /api/repairs/{rNo}`  
**描述**: 更新指定的维修记录信息

**路径参数**:
- `rNo`: 维修编号

**请求体示例**:
```
{
  "rDate": "2025-12-08",
  "runKm": 21000.00,
  "runHour": 700.00
}
```

**响应**: 返回更新后的维修记录

---

### 6. 删除维修记录
**接口**: `DELETE /api/repairs/{rNo}`  
**描述**: 删除指定的维修记录

**路径参数**:
- `rNo`: 维修编号

**响应**: 204 No Content

---

### 7. 获取车辆的维修次数
**接口**: `GET /api/repairs/car/{carId}/count`  
**描述**: 获取指定车辆的维修次数统计

**路径参数**:
- `carId`: 车牌号

**响应示例**:
```
5
```

---

## 租赁业务管理 API（Rental）

### 1. 数据模型概览

`rental` 表字段（详见 `schema.sql` / `database-README.md`）:

| 字段名              | 类型          | 说明                       |
|---------------------|---------------|----------------------------|
| InvoiceNo           | CHAR(10)      | 发票号 / 订单号（主键）    |
| LoanDate            | DATETIME      | 借车时间                   |
| ReturnDate          | DATETIME      | 还车时间                   |
| LoanPlace           | CHAR(100)     | 借车地点                   |
| ReturnPlace         | CHAR(100)     | 还车地点                   |
| CarID               | CHAR(10)      | 车辆牌照号（外键 car.CarID）|
| CNo                 | CHAR(8)       | 客户号（外键 customer.CNo）|
| DNo                 | CHAR(8)       | 司机号（外键 driver.DNo，可为空＝自驾）|
| RoundTripDistance   | NUMERIC(8,2)  | 本次往返行驶里程（公里）   |
| DriveTime           | NUMERIC(4,2)  | 本次行驶时间（小时，向上取整）|
| Amount              | NUMERIC(8,2)  | 本次应付金额（元）         |

> 说明：`RoundTripDistance` / `DriveTime` 是“单次租赁的行程数据”；车辆表 `car` 中的 `RUNKM` / `RunHour` 为车辆的**累计运行里程/累计运行小时**，会在结算时按增量同步更新。

### 2. 创建租赁记录

- **接口**：`POST /api/rentals`
- **说明**：客户在前端选车下单时调用，创建一条租赁记录。发票号由后端自动生成。
- **请求体 JSON 示例：**

```json
{
  "loanDate": "2025-12-17T00:00:00",
  "returnDate": "2025-12-18T00:00:00",
  "loanPlace": "广州白云国际机场店",
  "returnPlace": "广州天河珠江新城店",
  "carId": "京C11111",
  "cNo": "C0000001",
  "dNo": "D0000001",          // 可为空表示自驾
  "roundTripDistance": 70.0    // 可先为空，结算时由管理员补录
}
```

- **响应示例：**

```json
{
  "invoiceNo": "INV0000011",
  "loanDate": "2025-12-17T00:00:00",
  "returnDate": "2025-12-18T00:00:00",
  "loanPlace": "广州白云国际机场店",
  "returnPlace": "广州天河珠江新城店",
  "carId": "京C11111",
  "cNo": "C0000001",
  "dNo": "D0000001",
  "roundTripDistance": null,
  "driveTime": null,
  "amount": null
}
```

> 刚创建时，`RoundTripDistance` / `DriveTime` / `Amount` 为空，实际行程与金额由管理员在“订单结算”环节录入并计算。

---

### 3. 更新 / 结算租赁记录

- **接口**：`PUT /api/rentals/{invoiceNo}`
- **说明**：管理员在“订单管理/结算”页面中，根据实际行程录入 `roundTripDistance`，并确认 `loanDate` / `returnDate` 后，由后端统一计算 `DriveTime` 和 `Amount`，同时将本次订单的里程/小时增量累加到车辆 `RUNKM` / `RunHour` 中，并在金额非空时释放车辆与司机状态。

- **请求体 JSON 示例：**

```json
{
  "roundTripDistance": 70.0,
  "returnDate": "2025-12-18T00:00:00",
  "loanPlace": "广州白云国际机场店",
  "returnPlace": "广州天河珠江新城店"
}
```

- **核心业务逻辑（摘自 `RentalService.updateRental`）：**

1. 记录结算前订单的旧值：
   - `oldDistance = existingRental.roundTripDistance`（默认 0）
   - `oldDriveTime = existingRental.driveTime`（默认 0）
2. 根据请求体更新基础字段（包括 `roundTripDistance`）。
3. 若 `loanDate` 与 `returnDate` 均非空，则按两者差值计算行驶时长（小时）：
   - 使用 `Duration.between(loanDate, returnDate).toHours()` 取整小时；
   - 若存在零头分钟，则向上取整 1 小时；
   - 小于 0 的异常数据强制置 0；
   - 结果写入 `driveTime`（BigDecimal）。
4. 调用 `calculateRentalAmount(existingRental)` 统一计算本次金额 `amount`。
5. 计算增量：
   - `deltaKm   = newDistance - oldDistance`
   - `deltaHour = newDriveTime - oldDriveTime`
6. 若 `deltaKm > 0` 或 `deltaHour > 0`，则找到对应车辆 `CarId`：
   - `car.RUNKM  = (car.RUNKM  ?? 0) + deltaKm`
   - `car.RunHour = (car.RunHour ?? 0) + deltaHour`
   - 保存更新车辆。
7. 若 `amount` 非空，视为订单已结算：
   - `car.CarStatus = "0"`（可租），保存；
   - 若有司机 `DNo`，则将其 `DStatus` 设为“空闲”。

> **注意：** 通过“增量”方式更新车辆累计里程和小时，可以避免管理员多次点击保存导致重复累加同一笔行程。

---

### 4. 金额计算规则（`calculateRentalAmount`）

金额计算集中于 `RentalService.calculateRentalAmount(Rental rental)`，核心规则如下：

1. 前置条件：
   - 必须存在 `carId`、`loanDate`、`returnDate`；否则金额返回 0。
   - 通过 `carId` 找到车辆及其 `CarType`，从 `CarType.RentRate` 读取**每小时费用** `hourlyRate`（元/小时）。

2. 时间费用：
   - 使用 `Duration.between(loanDate, returnDate).toHours()` 计算小时差 `hoursDiff`（不足整小时不向上取整，用于计费）;
   - `timeCost = hourlyRate × hoursDiff`。

3. 里程费用：
   - 将每小时费用折算为每公里：`kilometerRate = hourlyRate ÷ 50`（保留 2 位小数，四舍五入）;
   - 如 `roundTripDistance` 非空，则 `distanceCost = kilometerRate × roundTripDistance`，否则为 0。

4. 固定服务费：
   - `serviceFee = 50` 元，始终计入。

5. 司机服务费：
   - 若订单存在 `DNo`（表示选择司机服务）:
     - 先确定用于计费的小时数 `hours`：优先使用 `DriveTime`，否则退回到 `hoursDiff`；
     - 換算成天数：`days = ceil(hours / 24.0)`，最少按 1 天计费；
     - `driverFee = 200 × days`。
   - 若为自驾（`DNo` 为空），则 `driverFee = 0`。

6. 总金额：
   - `totalAmount = timeCost + distanceCost + serviceFee + driverFee`；
   - 最终四舍五入到 2 位小数作为 `amount`。

---

### 5. 按客户 / 司机 / 车辆查询租赁记录

> 具体 URL 以当前 `RentalController` 实现为准，这里给出推荐设计与字段说明，便于前后端对齐。

#### 5.1 按客户查询

- **接口建议**：`GET /api/rentals/customer/{cNo}`
- **说明**：返回指定客户的全部租赁记录，用于前端“个人中心-我的订单”展示。
- **响应 JSON 每条记录字段（示例）：**

```json
{
  "invoiceNo": "INV0000011",
  "loanDate": "2025-12-17T00:00:00",
  "returnDate": "2025-12-18T00:00:00",
  "loanPlace": "广州白云国际机场店",
  "returnPlace": "广州天河珠江新城店",
  "carId": "京C11111",
  "carType": "奔驰E级",
  "carBrand": "奔驰E级",      // 当前实现中 carType 与 carBrand 复用车型名称
  "dNo": "D0000001",         // 可为空表示自驾
  "driverName": "钱七",       // 若存在司机
  "roundTripDistance": 70.0,
  "driveTime": 24.0,
  "amount": 1774.00
}
```

前端 `UserProfile.vue` 会基于这些字段计算并展示：

- **基本费用**：根据车型 `rentRate`、租期天数、服务费、司机费等计算，与下单时预计总额保持一致；
- **里程费用**：`amount - 基本费用`，作为“里程费用”部分展示；
- **订单状态**：
  - 若 `amount` 为空，则视为“进行中 / 待结算”;
  - 若 `amount` 非空，则视为“已完成”，并将订单移入“历史订单”。

#### 5.2 按司机查询

- **接口建议**：`GET /api/rentals/driver/{dNo}`
- **用途**：司机后台查看自己接单记录（当前实现程度根据实际代码补充）。

#### 5.3 按车辆查询

- **接口建议**：`GET /api/rentals/car/{carId}`
- **用途**：管理员在车辆详情中查看该车历史租赁记录，与 CarRepair 维修信息结合评估车辆状态。

---

### 6. 租赁统计与收入

结合 `RentalRepository` 的统计方法，可以提供如下统计接口（根据实际实现程度选择暴露）：

- `GET /api/rentals/statistics/revenue?startDate=2025-01-01&endDate=2025-12-31`
  - 调用 `calculateRevenue(startDate, endDate)`，返回某时间范围内租赁收入总额。
- `GET /api/rentals/statistics/summary`：
  - 返回：
    - `totalRentals`：租赁总笔数；
    - `totalRevenue`：租赁总收入；
    - `averageAmount`：平均每单金额。

> 这些统计接口可用于后台“报表/仪表盘”模块，为运营人员提供整体经营数据。

---

## 数据模型

### 车辆状态说明
- `0`: 可租 - 车辆可用，可以出租
- `1`: 已租 - 车辆已被租出
- `2`: 维修中 - 车辆正在维修，不可出租

### 错误响应格式
```json
{
  "timestamp": "2025-12-08T10:30:00",
  "status": 400,
  "message": "操作失败",
  "details": "车牌号已存在: 京A12345"
}
```

---

## 使用示例

### 示例1: 查询所有可租车辆
```
curl -X GET http://localhost:8080/api/cars/status/0
```

### 示例2: 创建新车辆
```
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

### 示例3: 将车辆设置为维修状态
```
curl -X PATCH http://localhost:8080/api/cars/京A12345/status \
  -H "Content-Type: application/json" \
  -d '{"carStatus": "2"}'
```

### 示例4: 添加维修记录
```
curl -X POST http://localhost:8080/api/repairs \
  -H "Content-Type: application/json" \
  -d '{
    "rDate": "2025-12-08",
    "runKm": 18000.00,
    "runHour": 600.00,
    "carId": "京A12345"
  }'
```

### 示例5: 创建租赁记录
```
curl -X POST http://localhost:8080/api/rentals \
  -H "Content-Type: application/json" \
  -d '{
    "loanDate": "2025-12-17T00:00:00",
    "returnDate": "2025-12-18T00:00:00",
    "loanPlace": "广州白云国际机场店",
    "returnPlace": "广州天河珠江新城店",
    "carId": "京C11111",
    "cNo": "C0000001",
    "dNo": "D0000001"
  }'
```

### 示例6: 更新租赁记录并结算
```
curl -X PUT http://localhost:8080/api/rentals/INV0000011 \
  -H "Content-Type: application/json" \
  -d '{
    "roundTripDistance": 70.0,
    "returnDate": "2025-12-18T00:00:00",
    "loanPlace": "广州白云国际机场店",
    "returnPlace": "广州天河珠江新城店"
  }'
```

---

## 注意事项

1. **跨域支持**: 所有API接口都已配置CORS，支持跨域访问
2. **事务管理**: 所有涉及数据修改的操作都在事务中执行，确保数据一致性
3. **级联关系**: 车辆信息与车型、维修记录存在关联关系
4. **状态自动更新**: 创建维修记录时，车辆状态会自动更新为"维修中"
5. **验证**: API会验证车型是否存在、车牌号是否重复等业务规则

---

## 后续扩展接口预留

本模块已为以下功能预留接口扩展：
- 司机信息管理
- 客户信息管理
- 租赁业务管理
- 费用计算
- 报表统计
