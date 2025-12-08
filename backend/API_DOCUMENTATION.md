# 车辆信息管理模块 RESTful API 文档

## 概述
本文档描述了汽车租赁系统中车辆信息管理模块的RESTful API接口。

**基础URL**: `http://localhost:8080`

---

## 车辆信息管理 API

### 1. 获取所有车辆信息
**接口**: `GET /api/cars`  
**描述**: 获取系统中所有车辆的信息列表

**响应示例**:
```json
[
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
]
```

---

### 2. 根据车牌号获取车辆信息
**接口**: `GET /api/cars/{carId}`  
**描述**: 根据车牌号获取特定车辆的详细信息

**路径参数**:
- `carId`: 车牌号（如: 京A12345）

**响应示例**:
```json
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
```json
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
```json
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
```json
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
```json
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
```json
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
```json
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
```json
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
```json
5
```

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
```bash
curl -X GET http://localhost:8080/api/cars/status/0
```

### 示例2: 创建新车辆
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

### 示例3: 将车辆设置为维修状态
```bash
curl -X PATCH http://localhost:8080/api/cars/京A12345/status \
  -H "Content-Type: application/json" \
  -d '{"carStatus": "2"}'
```

### 示例4: 添加维修记录
```bash
curl -X POST http://localhost:8080/api/repairs \
  -H "Content-Type: application/json" \
  -d '{
    "rDate": "2025-12-08",
    "runKm": 18000.00,
    "runHour": 600.00,
    "carId": "京A12345"
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

