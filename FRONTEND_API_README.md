# 前端API集成说明

本文档说明了前端如何与后端API进行交互。

## 目录结构

```
frontend/src/
├── api/
│   ├── request.js      # Axios实例配置和拦截器
│   └── auth.js         # 认证相关API
└── utils/
    └── auth.js         # 用户认证工具函数
```

## API基础配置

### 1. Axios实例 (`api/request.js`)

- **Base URL**: `http://localhost:8080/api`
- **超时时间**: 10秒
- **请求拦截器**: 自动添加Authorization token
- **响应拦截器**: 统一错误处理和消息提示

### 2. 认证API (`api/auth.js`)

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

## 用户认证工具 (`utils/auth.js`)

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

## 错误处理

所有API请求的错误都会被统一处理：

- **400**: 请求参数错误 - 显示错误消息
- **401**: 未授权 - 清除token并提示登录
- **403**: 拒绝访问
- **404**: 资源不存在
- **500**: 服务器错误
- **网络错误**: 提示检查网络连接

错误消息会自动通过 `ElMessage` 显示给用户。

## 在组件中使用

### Login.vue 示例

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

### Register.vue 示例

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

## 开发环境配置

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
