@echo off
echo ================================
echo 启动前端服务 (Vue + Vite)
echo ================================
echo.

cd frontend

echo 检查依赖...
if not exist "node_modules\" (
    echo 首次运行，正在安装依赖...
    call npm install
)

echo.
echo 正在启动前端服务...
echo 服务将运行在: http://localhost:5173
echo.
echo 按 Ctrl+C 停止服务
echo.

call npm run dev

pause
