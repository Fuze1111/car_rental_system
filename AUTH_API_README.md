# 认证 API 文档

## 概述
本文档说明汽车租赁系统的登录和注册 API 接口。

## 基础信息
- **Base URL**: `http://localhost:8080/api/auth`
- **Content-Type**: `application/json`

## API 接口

### 1. 登录接口

**Endpoint**: `POST /api/auth/login`

**请求体**:
```json
{
  "username": "账号/手机号/用户名",
  "password": "密码",
  "role": "角色类型 (admin/customer/driver)"
}
```

**示例 - 管理员登录**:
```json
{
  "username": "admin",
  "password": "123456",
  "role": "admin"
}
```

**示例 - 客户登录（通过客户号）**:
```json
{
  "username": "C0000001",
  "password": "123456",
  "role": "customer"
}
```

**示例 - 客户登录（通过手机号）**:
```json
{
  "username": "13800138000",
  "password": "123456",
  "role": "customer"
}
```

**示例 - 司机登录**:
```json
{
  "username": "D0000001",
  "password": "123456",
  "role": "driver"
}
```

**成功响应** (200 OK):
```json
{
  "userId": "C0000001",
  "userName": "张三",
  "role": "customer",
  "phone": "13800138000",
  "extra": "阿里巴巴集团",
  "message": "客户登录成功"
}
```

**失败响应** (401 Unauthorized):
```json
{
  "message": "账号或密码错误"
}
```

---

### 2. 客户注册接口

**Endpoint**: `POST /api/auth/register/customer`

**请求体**:
```json
{
  "name": "客户姓名",
  "phone": "手机号",
  "password": "密码",
  "company": "公司名称（可选）",
  "address": "地址（可选）"
}
```

**示例**:
```json
{
  "name": "李明",
  "phone": "13912345678",
  "password": "password123",
  "company": "测试公司",
  "address": "北京市海淀区"
}
```

**成功响应** (201 Created):
```json
{
  "userId": "C0000009",
  "userName": "李明",
  "role": "customer",
  "phone": "13912345678",
  "extra": "测试公司",
  "message": "注册成功"
}
```

**失败响应** (400 Bad Request):
```json
{
  "message": "该手机号已被注册"
}
```

---

### 3. 司机注册接口

**Endpoint**: `POST /api/auth/register/driver`

**请求体**:
```json
{
  "name": "司机姓名",
  "password": "密码",
  "licenseType": "准驾车型 (C1/C2/A1/A2/B1/B2)"
}
```

**示例**:
```json
{
  "name": "王师傅",
  "password": "driver123",
  "licenseType": "C1"
}
```

**成功响应** (201 Created):
```json
{
  "userId": "D0000007",
  "userName": "王师傅",
  "role": "driver",
  "phone": null,
  "extra": "C1",
  "message": "注册成功，请等待管理员审核"
}
```

---

### 4. 测试接口

**Endpoint**: `GET /api/auth/test`

**响应** (200 OK):
```json
{
  "message": "Auth API is working"
}
```

---

## 测试数据

### 管理员账号
- 用户名: `admin`
- 密码: `123456`

### 客户账号
| 客户号 | 姓名 | 手机号 | 密码 |
|--------|------|--------|------|
| C0000001 | 张三 | 13800138000 | 123456 |
| C0000002 | 李四 | 13900139000 | 123456 |

### 司机账号
| 司机号 | 姓名 | 密码 |
|--------|------|------|
| D0000001 | 赵六 | 123456 |

---

## 密码安全

- 所有密码使用 **BCrypt** 算法加密存储
- 密码哈希长度为 60 字符
- 系统会在启动时自动将测试数据的明文密码加密

---

## 前端集成说明

### 登录请求示例（JavaScript）:
```javascript
const handleLogin = async () => {
  const response = await axios.post('http://localhost:8080/api/auth/login', {
    username: loginForm.username,
    password: loginForm.password,
    role: activeRole.value // 'admin', 'customer', or 'driver'
  })
  
  if (response.data.userId) {
    // 登录成功，保存用户信息
    localStorage.setItem('userInfo', JSON.stringify(response.data))
    
    // 根据角色跳转
    if (response.data.role === 'admin') {
      router.push('/admin/dashboard')
    } else if (response.data.role === 'customer') {
      router.push('/user/cars')
    } else if (response.data.role === 'driver') {
      router.push('/driver/dashboard')
    }
  }
}
```

### 注册请求示例（JavaScript）:
```javascript
const handleRegisterCustomer = async () => {
  const response = await axios.post('http://localhost:8080/api/auth/register/customer', {
    name: customerForm.name,
    phone: customerForm.phone,
    password: customerForm.password,
    company: customerForm.company,
    address: customerForm.address
  })
  
  if (response.data.userId) {
    ElMessage.success('注册成功！')
    router.push('/login')
  }
}
```

---

## 注意事项

1. **CORS 已配置**: 所有接口支持跨域访问
2. **密码加密**: 前端发送明文密码，后端自动加密存储
3. **账号格式**:
   - 客户号: C + 7位数字（如 C0000001）
   - 司机号: D + 7位数字（如 D0000001）
   - 管理员ID: A + 7位数字（如 A0000001）
4. **客户登录**: 支持客户号或手机号登录
5. **司机登录**: 仅支持司机号登录
