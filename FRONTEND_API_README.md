# 前端API集成说明

本文件说明前端如何与后端 API 进行交互，涵盖：

- 认证相关 API（登录 / 注册）
- 车辆与车型 API（包含运行里程/时长等关键字段）
- 租赁业务 API（创建订单、按客户查询订单）
- 个人中心订单展示所依赖的字段

## 目录结构

```
frontend/src/
├── api/
│   ├── request.js      # Axios实例配置和拦截器
│   ├── auth.js         # 认证相关API
│   ├── car.js          # 车辆 / 车型 / 维修 / 统计相关API
│   └── rental.js       # 租赁业务相关API
└── utils/
    └── auth.js         # 用户认证工具函数
```

## 一、API基础配置

### 1. Axios实例 (`api/request.js`)

- **Base URL**: `http://localhost:8080/api`
- **超时时间**: 10秒
- **请求拦截器**: 自动添加 Authorization token（如后续引入）
- **响应拦截器**: 统一错误处理和消息提示（通过 Element Plus `ElMessage`）

---

## 二、认证API (`api/auth.js`)

#### 登录接口

```javascript
import { login } from '@/api/auth'

const response = await login({
  username: 'user@example.com',  // 用户名/账号/手机号
  password: '123456',             // 密码
  role: 'customer'                // 角色: customer/driver/admin
})

// 响应数据格式
{
  "id": "C0000001",              // 用户ID
  "name": "张三",                // 用户姓名
  "role": "customer",            // 角色
  "phone": "13800138000",        // 电话（客户）
  "company": "某公司",           // 公司（客户）或准驾车型（司机）
  "message": "客户登录成功"      // 消息
}
```

#### 客户注册接口

```javascript
import { registerCustomer } from '@/api/auth'

const response = await registerCustomer({
  name: '张三',                  // 姓名（必填）
  phone: '13800138000',          // 手机号（必填）
  password: '123456',            // 密码（必填）
  company: '某公司',             // 公司（可选）
  address: '某地址'              // 地址（可选）
})

// 响应数据格式
{
  "id": "C0000001",              // 客户号
  "name": "张三",
  "role": "customer",
  "phone": "13800138000",
  "company": "某公司",
  "message": "注册成功"
}
```

#### 司机注册接口

```javascript
import { registerDriver } from '@/api/auth'

const response = await registerDriver({
  name: '李四',                  // 姓名（必填）
  password: '123456',            // 密码（必填）
  licenseType: 'C1'              // 准驾车型（必填）：C1/C2/A1/A2/B1
})

// 响应数据格式
{
  "id": "D0000001",              // 司机号
  "name": "李四",
  "role": "driver",
  "phone": null,
  "company": "C1",               // 准驾车型
  "message": "注册成功，请等待管理员审核"
}
```

---

## 三、车辆与车型 API (`api/car.js`)

> 下述函数名称以现有实现为参考，具体以 `api/car.js` 中导出的函数为准。

### 1. 获取车辆列表

```javascript
import { getCars } from '@/api/car'

const cars = await getCars({ status: '0' }) // 0=可租, 1=已租, 2=维修
```

**典型响应字段：**

```json
{
  "carId": "京C11111",
  "purchaseDate": "2023-01-10",
  "price": 260000.00,
  "carStatus": "0",           // 0-可租；1-已租；2-维修
  "carStatusDesc": "可租",
  "tNo": "T003",
  "tName": "奔驰E级",
  "rentRate": 1.50,            // 每小时租金（元/小时）
  "runKm": 12345.67,           // 车辆累计运行公里数（RUNKM）
  "runHour": 456.00,           // 车辆累计运行小时数（RunHour）
  "needsRepair": false,
  "lastRepairDate": "2024-12-01"
}
```

> 在前端车辆列表/详情中，可使用 `runKm` / `runHour` 展示车辆已累计使用强度，配合维修记录综合判断车辆状态。

### 2. 获取某辆车的维修记录

```javascript
import { getRepairsByCarId } from '@/api/car'

const repairs = await getRepairsByCarId('京C11111')
```

**每条维修记录主要字段：**

- `rNo`：维修编号
- `rDate`：维修日期
- `runKm` / `runHour`：该次维修时的运行公里 / 小时快照（不会回写到 `car` 表）

### 3. 获取车型列表

```javascript
import { getCarTypes } from '@/api/car'

const types = await getCarTypes()
// 每项通常包含：tNo, tName, rentRate, image(如已映射)
```

> 在 `CarManagement.vue` 中，当前通过 `getImageForCarType(type.tname)` 函数根据车型名称映射到本地图片；若后续改为后端返回 `imagePath` / `imageUrl` 字段，可直接绑定到 `el-image` 的 `src`。

---

## 四、租赁业务 API (`api/rental.js`)

### 1. 创建租赁订单

客户在选车界面确认下单时调用，通常来自 `CarSelection.vue`：

```javascript
import { createRental } from '@/api/rental'
import { getUserInfo } from '@/utils/auth'

const userInfo = getUserInfo() // 包含 cNo / userId

const payload = {
  loanDate: selectedStartTime,      // ISO 字符串，如 '2025-12-17T00:00:00'
  returnDate: selectedEndTime,
  loanPlace: form.loanPlace,
  returnPlace: form.returnPlace,
  carId: selectedCar.carId,
  cNo: userInfo.userId,            // 当前登录客户号
  dNo: selectedDriverId || null    // 为空表示自驾
}

const rental = await createRental(payload)
```

返回的 `rental` 对象中会包含：

- `invoiceNo`：订单号/发票号
- `loanDate` / `returnDate`
- `loanPlace` / `returnPlace`
- `carId`
- `cNo` / `dNo`
- `roundTripDistance`：创建时通常为 `null`，后续由管理员结算时填写
- `driveTime`：创建时为 `null`
- `amount`：创建时为 `null`

### 2. 按客户查询租赁订单（个人中心）

```javascript
import { getRentalsByCustomer } from '@/api/rental'
import { getUserInfo } from '@/utils/auth'

const userInfo = getUserInfo()
const rentals = await getRentalsByCustomer(userInfo.userId)
```

典型响应中的每条记录字段：

```json
{
  "invoiceNo": "INV0000011",
  "loanDate": "2025-12-17T00:00:00",
  "returnDate": "2025-12-18T00:00:00",
  "loanPlace": "广州白云国际机场店",
  "returnPlace": "广州天河珠江新城店",
  "carId": "京C11111",
  "carType": "奔驰E级",
  "carBrand": "奔驰E级",
  "dNo": "D0000001",          // 为空表示自驾
  "driverName": "钱七",        // 若有司机
  "roundTripDistance": 70.0,
  "driveTime": 24.0,
  "rentRate": 1.50,
  "amount": 1774.00
}
```

在 `UserProfile.vue` 中，会将这些字段映射为：

- 「基本费用」：基于 `rentRate` + 租期天数 + 固定服务费 + 司机服务费计算，尽量与下单时的预计总额一致；
- 「里程费用」：`amount - 基本费用`；
- 「金额状态」：
  - `amount` 为空：显示“待结算”;
  - `amount` 非空：显示 `¥金额`；
- 「订单状态」：
  - `amount` 为空 或 当前时间 < `returnDate`：视为“进行中”;
  - `amount` 非空：视为“已完成”，在 UI 上归入“历史订单”。

---

## 五、用户认证工具 (`utils/auth.js`)

### 使用示例

```javascript
import { 
  setUserInfo, 
  getUserInfo, 
  setUserRole,
  getUserRole,
  clearAuth,
  isLoggedIn 
} from '@/utils/auth'

// 登录成功后保存用户信息
const response = await login(loginData)
setUserInfo(response)
setUserRole('customer')

// 获取当前用户信息
const userInfo = getUserInfo()
const userRole = getUserRole()

// 检查登录状态
if (isLoggedIn()) {
  // 已登录
}

// 退出登录
clearAuth()
```

---

## 六、错误处理

所有API请求的错误都会被统一处理：

- **400**: 请求参数错误 - 显示错误消息
- **401**: 未授权 - 清除token并提示登录
- **403**: 拒绝访问
- **404**: 资源不存在
- **500**: 服务器错误
- **网络错误**: 提示检查网络连接

错误消息会自动通过 `ElMessage` 显示给用户。

---

## 七、在组件中的使用要点

### 1. Login / Register 页面

```vue
<script setup>
import { login } from '@/api/auth'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()

const handleLogin = async () => {
  try {
    const response = await login({
      username: loginForm.username,
      password: loginForm.password,
      role: activeRole.value
    })
    
    // 保存用户信息
    localStorage.setItem('userInfo', JSON.stringify(response))
    localStorage.setItem('userRole', activeRole.value)
    
    ElMessage.success(response.message)
    
    // 跳转
    router.push('/dashboard')
  } catch (error) {
    console.error('登录失败:', error)
  }
}
</script>
```

```vue
<script setup>
import { registerCustomer, registerDriver } from '@/api/auth'

const handleRegister = async () => {
  try {
    let response
    if (activeRole.value === 'customer') {
      response = await registerCustomer({
        name: customerForm.name,
        phone: customerForm.phone,
        password: customerForm.password,
        company: customerForm.company,
        address: customerForm.address
      })
    } else {
      response = await registerDriver({
        name: driverForm.name,
        password: driverForm.password,
        licenseType: driverForm.licenseType
      })
    }
    
    ElMessage.success(response.message)
    router.push('/login')
  } catch (error) {
    console.error('注册失败:', error)
  }
}
</script>
```

### 2. 个人中心订单（`UserProfile.vue`）

- 登录成功后，`userInfo` 会存入 LocalStorage，其中 `userId` 就是客户号 `CNo`；
- 个人中心中通过 `getRentalsByCustomer(userInfo.userId)` 拉取当前客户的所有订单；
- 组件内部根据是否有 `amount` 字段，将订单分为「现有订单」和「历史订单」，并展示：
  - 订单号、车型、车牌、起止时间、取/还车地点；
  - 行驶里程、行驶时长（由后端在结算时计算并回写）；
  - 金额状态与订单状态。

---

## 八、开发环境配置

### 启动后端服务

```bash
cd backend
./mvnw spring-boot:run
```

后端服务将运行在: `http://localhost:8080`

### 启动前端开发服务器

```bash
cd frontend
npm install
npm run dev
```

前端服务将运行在: `http://localhost:5173`

## CORS配置

后端已经配置了CORS，允许来自任何源的请求：

```java
@CrossOrigin(origins = "*")
```

## 注意事项

1. **客户号生成规则**: C + 7位数字（如 C0000001），依次递增
2. **司机号生成规则**: D + 7位数字（如 D0000001），依次递增
3. **密码加密**: 后端使用BCrypt加密存储密码
4. **客户登录标识**: 可以使用客户号或手机号登录
5. **司机登录标识**: 使用司机工号登录
6. **管理员登录**: 使用用户名登录

## API测试

可以使用以下方式测试API连接：

```javascript
import { testApi } from '@/api/auth'

testApi().then(response => {
  console.log(response) // { message: "Auth API is working" }
})
```

或者直接访问: `http://localhost:8080/api/auth/test`
