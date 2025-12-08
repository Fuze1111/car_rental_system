# 司机信息管理 API 文档

## 基础信息

- **基础路径**: `/api/drivers`
- **测试页面**: `http://localhost:8080/test-driver-admin.html`
- **所有测试请求都带有** `?test=true` **参数**

---

## API 端点列表

### 1. 获取所有司机信息

- **端点**: `GET /api/drivers`
- **描述**: 获取所有司机信息列表
- **响应示例**:
```json
[
  {
    "dNo": "D0000001",
    "dName": "赵六",
    "startWork": "2020-01-15",
    "salary": 6000.00,
    "dStatus": "在岗",
    "licenseType": "C1",
    "workYears": 4,
    "workMonths": 11,
    "isOnDuty": true
  }
]
```

---

### 2. 根据司机号获取司机信息

- **端点**: `GET /api/drivers/{dNo}`
- **描述**: 根据司机号获取单个司机的详细信息
- **路径参数**:
  - `dNo`: 司机号（例如：D0000001）
- **响应**: 司机详细信息或 404 Not Found

---

### 3. 创建新司机

- **端点**: `POST /api/drivers`
- **描述**: 创建新的司机记录
- **请求体示例**:
```json
{
  "dNo": "D0000005",
  "dName": "王五",
  "startWork": "2024-01-01",
  "salary": 6200,
  "dStatus": "空闲",
  "licenseType": "C1"
}
```
- **响应**: 创建的司机信息（HTTP 201）

---

### 4. 更新司机信息

- **端点**: `PUT /api/drivers/{dNo}`
- **描述**: 更新司机的完整信息
- **路径参数**:
  - `dNo`: 司机号
- **请求体**: 与创建司机相同的 JSON 格式
- **响应**: 更新后的司机信息

---

### 5. 删除司机

- **端点**: `DELETE /api/drivers/{dNo}`
- **描述**: 删除指定司机
- **路径参数**:
  - `dNo`: 司机号
- **响应**: HTTP 204 No Content

---

### 6. 根据状态查询司机

- **端点**: `GET /api/drivers/status/{dStatus}`
- **描述**: 查询指定状态的所有司机
- **路径参数**:
  - `dStatus`: 司机状态（在岗/空闲）
- **示例**: `/api/drivers/status/在岗`

---

### 7. 根据准驾车型查询司机

- **端点**: `GET /api/drivers/license/{licenseType}`
- **描述**: 查询持有指定准驾车型的所有司机
- **路径参数**:
  - `licenseType`: 准驾车型（C1/A2/B2等）
- **示例**: `/api/drivers/license/C1`

---

### 8. 根据姓名模糊查询司机

- **端点**: `GET /api/drivers/search/name?dName={name}`
- **描述**: 根据姓名关键字模糊查询司机
- **查询参数**:
  - `dName`: 姓名关键字
- **示例**: `/api/drivers/search/name?dName=赵`

---

### 9. 多条件查询司机

- **端点**: `GET /api/drivers/search`
- **描述**: 根据多个条件组合查询司机
- **查询参数**（均可选）:
  - `dStatus`: 司机状态
  - `licenseType`: 准驾车型
  - `minSalary`: 最低工资
  - `maxSalary`: 最高工资
- **示例**: `/api/drivers/search?dStatus=在岗&minSalary=5000&maxSalary=7000`

---

### 10. 根据工资范围查询司机

- **端点**: `GET /api/drivers/salary?min={min}&max={max}`
- **描述**: 查询工资在指定范围内的司机
- **查询参数**:
  - `min`: 最低工资
  - `max`: 最高工资
- **示例**: `/api/drivers/salary?min=5000&max=7000`

---

### 11. 根据工龄查询司机

- **端点**: `GET /api/drivers/work-years/{years}`
- **描述**: 查询工龄达到指定年数的司机
- **路径参数**:
  - `years`: 最少工龄（年）
- **示例**: `/api/drivers/work-years/2`（查询工龄2年以上的司机）

---

### 12. 更新司机状态

- **端点**: `PATCH /api/drivers/{dNo}/status`
- **描述**: 单独更新司机的工作状态
- **路径参数**:
  - `dNo`: 司机号
- **请求体**:
```json
{
  "dStatus": "在岗"
}
```

---

### 13. 更新司机工资

- **端点**: `PATCH /api/drivers/{dNo}/salary`
- **描述**: 单独更新司机的工资
- **路径参数**:
  - `dNo`: 司机号
- **请求体**:
```json
{
  "salary": 6500
}
```

---

### 14. 获取司机统计信息

- **端点**: `GET /api/drivers/statistics`
- **描述**: 获取司机的统计数据
- **响应示例**:
```json
{
  "total": 10,
  "onDuty": 6,
  "free": 4,
  "averageSalary": 6125.00
}
```

---

## 字段说明

### Driver 对象

| 字段 | 类型 | 说明 | 示例 |
|------|------|------|------|
| dNo | String | 司机号（主键） | "D0000001" |
| dName | String | 姓名 | "赵六" |
| startWork | Date | 入职日期 | "2020-01-15" |
| salary | BigDecimal | 基本工资 | 6000.00 |
| dStatus | String | 司机状态 | "在岗" / "空闲" |
| licenseType | String | 准驾车型 | "C1" / "A2" / "B2" |

### DriverResponse 额外字段

| 字段 | 类型 | 说明 | 示例 |
|------|------|------|------|
| workYears | Integer | 工龄（年） | 4 |
| workMonths | Integer | 工龄余数（月） | 11 |
| isOnDuty | Boolean | 是否在岗 | true |

---

## 状态值说明

### dStatus（司机状态）

- **在岗**: 司机当前在工作中
- **空闲**: 司机当前空闲可出车

### licenseType（准驾车型）

常见准驾车型：
- **C1**: 小型汽车
- **A2**: 牵引车
- **B2**: 大型货车
- 其他标准驾驶证类型

---

## 测试数据

测试页面中的"插入测试数据"按钮会插入以下4条司机记录：

```json
[
  {
    "dNo": "D0000001",
    "dName": "赵六",
    "startWork": "2020-01-15",
    "salary": 6000,
    "dStatus": "在岗",
    "licenseType": "C1"
  },
  {
    "dNo": "D0000002",
    "dName": "钱七",
    "startWork": "2021-06-20",
    "salary": 6500,
    "dStatus": "空闲",
    "licenseType": "A2"
  },
  {
    "dNo": "D0000003",
    "dName": "孙八",
    "startWork": "2022-03-10",
    "salary": 5800,
    "dStatus": "在岗",
    "licenseType": "B2"
  },
  {
    "dNo": "D0000004",
    "dName": "李九",
    "startWork": "2023-09-01",
    "salary": 5500,
    "dStatus": "空闲",
    "licenseType": "C1"
  }
]
```

---

## 错误处理

所有API都使用统一的错误响应格式：

```json
{
  "timestamp": "2025-12-08T10:00:00",
  "status": 400,
  "message": "操作失败",
  "details": "详细错误信息"
}
```

常见错误：
- **404 Not Found**: 司机不存在
- **400 Bad Request**: 请求参数错误或司机号已存在
- **500 Internal Server Error**: 服务器内部错误

---

## 注意事项

1. **司机号格式**: 建议使用 `D` 开头 + 7位数字，例如：D0000001
2. **日期格式**: 使用 ISO 8601 格式：`YYYY-MM-DD`
3. **工资精度**: 支持两位小数
4. **工龄计算**: 自动根据入职日期计算，返回年和月
5. **状态值**: 确保使用正确的状态值（"在岗" 或 "空闲"）

---

## 测试页面功能

访问 `http://localhost:8080/test-driver-admin.html` 可以测试以下功能：

- ✅ 列出所有司机
- ✅ 插入测试数据（一键插入4名司机）
- ✅ 按司机号查询
- ✅ 新增司机
- ✅ 更新司机信息
- ✅ 删除司机
- ✅ 按状态查询
- ✅ 按准驾车型查询
- ✅ 按姓名模糊查询
- ✅ 按工资范围查询
- ✅ 按工龄查询
- ✅ 多条件组合查询
- ✅ 更新司机状态（PATCH）
- ✅ 更新司机工资（PATCH）
- ✅ 获取统计信息

