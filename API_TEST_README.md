# 前后端API连接测试说明

## 项目结构

- **后端**: Spring Boot 3.5.7 (运行在 http://localhost:8080)
- **前端**: Vue 3 + Vite (运行在 http://localhost:5173)

## 运行步骤

### 1. 启动后端服务

```bash
cd backend
mvn spring-boot:run
```

后端将在 `http://localhost:8080` 启动

### 2. 启动前端服务

```bash
cd frontend
npm install  # 如果还没有安装依赖
npm run dev
```

前端将在 `http://localhost:5173` 启动（或Vite显示的端口）

## API端点

后端提供了以下测试端点：

1. **GET /api/test** - 基础连接测试
   - 返回：连接状态和系统信息

2. **GET /api/hello** - Hello消息
   - 返回：欢迎消息和时间戳

3. **POST /api/echo** - 回显测试
   - 请求体：JSON格式的数据
   - 返回：接收到的数据和确认信息

## 测试功能

前端页面提供了三个测试按钮：

1. **测试 GET 请求** - 调用 `/api/test` 端点
2. **测试 POST 请求** - 调用 `/api/echo` 端点并发送测试数据
3. **测试 Hello API** - 调用 `/api/hello` 端点

## 注意事项

- 确保后端服务在启动前端之前已经运行
- 如果遇到CORS错误，检查后端CorsConfig配置
- 如果端口冲突，可以在 `application.properties` 中修改后端端口

