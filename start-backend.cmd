@echo off
echo ================================
echo 启动后端服务 (Spring Boot)
echo ================================
echo.

cd backend

echo 正在启动后端服务...
echo 服务将运行在: http://localhost:8080
echo.
echo 按 Ctrl+C 停止服务
echo.

call mvnw.cmd spring-boot:run

pause
