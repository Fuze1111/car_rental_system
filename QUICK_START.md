# 快速启动指南

本指南帮助您快速启动汽车租赁系统的前后端服务。

## 前置要求

在开始之前，请确保已安装以下软件：

- ✅ **Java JDK 17+** - 运行后端Spring Boot应用
- ✅ **Node.js 20+** - 运行前端Vue应用
- ✅ **MySQL 8.0+** - 数据库服务
- ✅ **Maven** - Java项目构建工具（后端自带Maven Wrapper）

## 第一步: 数据库配置

### 1. 启动MySQL服务

确保MySQL服务正在运行。

### 2. 创建数据库

```sql
CREATE DATABASE IF NOT EXISTS car_rental_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;
```

### 3. 配置数据库连接

编辑 `backend/src/main/resources/application.properties`：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/car_rental_db
spring.datasource.username=root
spring.datasource.password=your_password
```

将 `your_password` 替换为您的MySQL密码。

### 4. 初始化数据

应用启动时会自动执行：
- `backend/src/main/resources/schema.sql` - 创建表结构
- `backend/src/main/resources/data.sql` - 插入初始数据

## 第二步: 启动服务

### 方式1: 使用启动脚本（推荐）

在Windows上，双击运行以下脚本：

#### 启动后端
```
start-backend.cmd
```

#### 启动前端
```
start-frontend.cmd
```

### 方式2: 手动启动

#### 启动后端

打开终端，在项目根目录执行：

```bash
cd backend
./mvnw spring-boot:run
```

Windows用户使用：
```cmd
cd backend
mvnw.cmd spring-boot:run
```

**等待后端启动成功**，看到以下输出：
```
Started BackendApplication in X.XXX seconds
```

后端服务地址: `http://localhost:8080`

#### 启动前端

打开**新的**终端窗口，在项目根目录执行：

```bash
cd frontend
npm install    # 首次运行需要安装依赖
npm run dev
```

**等待前端启动成功**，看到以下输出：
```
VITE vX.X.X  ready in XXX ms

  ➜  Local:   http://localhost:5173/
  ➜  Network: use --host to expose
```

前端服务地址: `http://localhost:5173`

## 第三步: 访问应用

在浏览器中打开: `http://localhost:5173`

您将看到登录页面，可以进行以下操作：

### 注册新用户

1. 点击"立即注册"
2. 选择角色（客户或司机）
3. 填写注册信息
4. 提交注册

### 登录

#### 客户登录
- 账号: 客户号（如 C0000001）或手机号
- 密码: 您设置的密码

#### 司机登录
- 工号: 司机号（如 D0000001）
- 密码: 您设置的密码

#### 管理员登录（需要先在数据库中创建）
- 账号: admin
- 密码: admin123

## 测试API连接

### 测试后端API

在浏览器访问:
```
http://localhost:8080/api/auth/test
```

应该看到:
```json
{
  "message": "Auth API is working"
}
```

## 项目结构

```
car_rental_system/
├── backend/                    # Spring Boot后端
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/example/backend/
│   │   │   │       ├── controller/      # 控制器
│   │   │   │       ├── service/         # 服务层
│   │   │   │       ├── repository/      # 数据访问层
│   │   │   │       ├── entity/          # 实体类
│   │   │   │       └── dto/             # 数据传输对象
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       ├── schema.sql           # 数据库表结构
│   │   │       └── data.sql             # 初始数据
│   │   └── test/
│   └── pom.xml
│
├── frontend/                   # Vue 3前端
│   ├── src/
│   │   ├── api/               # API接口
│   │   │   ├── request.js     # Axios配置
│   │   │   └── auth.js        # 认证API
│   │   ├── utils/             # 工具函数
│   │   │   └── auth.js        # 认证工具
│   │   ├── views/             # 页面组件
│   │   │   ├── Login.vue      # 登录页面
│   │   │   └── Register.vue   # 注册页面
│   │   ├── components/        # 通用组件
│   │   ├── router/            # 路由配置
│   │   └── main.js            # 入口文件
│   └── package.json
│
├── start-backend.cmd          # 后端启动脚本
├── start-frontend.cmd         # 前端启动脚本
├── QUICK_START.md            # 本文件
├── INTEGRATION_TEST_GUIDE.md # 测试指南
└── FRONTEND_API_README.md    # API文档
```

## 功能特性

### ✅ 已实现功能

- **用户认证**
  - 客户注册和登录
  - 司机注册和登录
  - 管理员登录
  - 密码BCrypt加密
  - 客户号/司机号自动递增生成

- **前端功能**
  - 现代化UI设计（Glassmorphism风格）
  - 表单验证
  - 错误处理和消息提示
  - 响应式布局

- **后端功能**
  - RESTful API
  - JPA数据持久化
  - 统一异常处理
  - CORS支持

## 常见问题

### 后端启动失败

**问题**: 端口8080已被占用
```
Web server failed to start. Port 8080 was already in use.
```

**解决方案**:
1. 停止占用8080端口的程序
2. 或修改 `application.properties` 中的端口：
   ```properties
   server.port=8081
   ```

### 前端无法连接后端

**问题**: 网络错误

**解决方案**:
1. 确认后端正在运行
2. 检查 `frontend/src/api/request.js` 中的baseURL配置
3. 如果修改了后端端口，需要同步修改前端配置

### 数据库连接失败

**问题**: 
```
Access denied for user 'root'@'localhost'
```

**解决方案**:
1. 检查MySQL用户名和密码
2. 确认MySQL服务正在运行
3. 验证数据库 `car_rental_db` 已创建

## 下一步

完成基本启动后，建议：

1. 📖 阅读 [INTEGRATION_TEST_GUIDE.md](./INTEGRATION_TEST_GUIDE.md) 进行功能测试
2. 📖 阅读 [FRONTEND_API_README.md](./FRONTEND_API_README.md) 了解API使用
3. 📖 阅读 [API_TEST_README.md](./API_TEST_README.md) 测试后端API
4. 🔧 根据需求继续开发其他功能

## 获取帮助

如果遇到问题：

1. 检查控制台错误输出
2. 查看浏览器开发者工具的Console和Network标签
3. 检查数据库表是否正确创建
4. 参考项目中的各个README文档

祝您使用愉快！🚗
