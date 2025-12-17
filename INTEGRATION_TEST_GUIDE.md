# 前后端集成测试指南

本指南将帮助您测试登录和注册功能的前后端集成，以及租赁业务与个人中心订单展示的功能。

## 前置条件

### 1. 数据库准备

确保MySQL数据库已启动并创建了 `car_rental_db` 数据库：

```sql
CREATE DATABASE IF NOT EXISTS car_rental_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 后端启动

在 `backend` 目录下启动Spring Boot应用：

```bash
cd backend
./mvnw spring-boot:run
```

或者在Windows上使用：
```cmd
cd backend
mvnw.cmd spring-boot:run
```

确认后端启动成功，应该看到类似输出：
```
Started BackendApplication in X.XXX seconds
```

后端服务运行在: `http://localhost:8080`

### 3. 前端启动

在 `frontend` 目录下启动Vue开发服务器：

```bash
cd frontend
npm install  # 首次运行需要安装依赖
npm run dev
```

前端服务将运行在: `http://localhost:5173`

## 测试步骤

### 测试1: 验证后端API

在浏览器中访问测试接口：
```
http://localhost:8080/api/auth/test
```

应该看到以下JSON响应：
```json
{
  "message": "Auth API is working"
}
```

### 测试2: 客户注册

1. 访问前端应用: `http://localhost:5173`
2. 点击"立即注册"按钮
3. 选择"客户注册"标签
4. 填写注册表单：
   - **姓名**: 张三
   - **手机号**: 13800138000
   - **密码**: 123456
   - **确认密码**: 123456
   - **工作单位**: 测试公司（可选）
   - **联系地址**: 测试地址（可选）
5. 点击"立即注册"

**预期结果**:
- 显示成功消息："注册成功"
- 自动跳转到登录页面
- 数据库中 `customer` 表应该有新记录，客户号为 `C0000001`
- 数据库中 `customer_login` 表应该有对应的登录凭证

### 测试3: 客户登录

1. 在登录页面选择"客户"标签
2. 填写登录表单：
   - **账号**: 13800138000（手机号）或 C0000001（客户号）
   - **密码**: 123456
3. 点击"登录"

**预期结果**:
- 显示成功消息："客户登录成功"
- 跳转到客户页面
- LocalStorage中保存了用户信息和角色

### 测试4: 司机注册

1. 访问注册页面
2. 选择"司机注册"标签
3. 填写注册表单：
   - **姓名**: 李四
   - **密码**: 123456
   - **确认密码**: 123456
   - **准驾车型**: C1
4. 点击"提交申请"

**预期结果**:
- 显示成功消息："注册成功，请等待管理员审核"
- 自动跳转到登录页面
- 数据库中 `driver` 表应该有新记录，司机号为 `D0000001`
- 数据库中 `driver_login` 表应该有对应的登录凭证

### 测试5: 司机登录

1. 在登录页面选择"司机"标签
2. 填写登录表单：
   - **工号**: D0000001
   - **密码**: 123456
3. 点击"登录"

**预期结果**:
- 显示成功消息："司机登录成功"
- 跳转到司机页面

### 测试6: 管理员登录

1. 在登录页面选择"管理员"标签
2. 填写登录表单：
   - **账号**: admin（默认管理员账号）
   - **密码**: admin123（默认密码）
3. 点击"登录"

**预期结果**:
- 显示成功消息："管理员登录成功"
- 跳转到管理员页面

**注意**: 需要在数据库中预先创建管理员账号。

## 租赁业务与订单展示测试

在登录/注册验证通过后，建议继续完成以下测试场景，确保租赁业务链路和车辆累计数据更新正确。

### 测试10: 客户创建租赁订单

1. 使用客户账号登录（例如前面注册的 `C0000001`）。
2. 在前端进入“租车大厅/选车页面”（如 `/user/cars` 或对应菜单）。
3. 选择一辆可租车辆（车牌如 `京C11111`），填写：
   - 借车时间：例如 `2025-12-17 00:00`；
   - 还车时间：例如 `2025-12-18 00:00`；
   - 取车地点 / 还车地点；
   - 是否选择司机（如选择 `钱七`，则会有 `DNo`）。
4. 提交订单，前端会调用 `createRental`（`POST /api/rentals`）。

**预期结果：**
- 后端返回包含 `invoiceNo` 的响应，例如 `INV0000011`；
- 数据库 `rental` 表新增一条记录：
  - `LoanDate` / `ReturnDate` / `LoanPlace` / `ReturnPlace` 正确；
  - `CarID` / `CNo` / `DNo` 正确；
  - `RoundTripDistance` / `DriveTime` / `Amount` 初始为 `NULL`；
- 客户个人中心“现有订单”中可以看到该订单，金额状态为“待结算”，订单状态为“进行中”。

### 测试11: 管理员结算租赁订单

1. 使用管理员账号登录后台（如 `admin`）。
2. 打开“订单管理/结算”页面（对应 `OrderManagement.vue` 或类似界面）。
3. 找到刚刚创建的订单（例如 `INV0000011`，车辆 `京C11111`）。
4. 填写结算信息：
   - 往返行驶里程 `RoundTripDistance`：例如 `70` 公里；
   - 确认 `LoanDate` / `ReturnDate`（保持为 24 小时差）；
   - 确认取还车地点无误；
5. 点击保存/结算，触发 `PUT /api/rentals/{invoiceNo}` 调用。

**预期结果（数据库层面）：**
- `rental` 表中该记录的：
  - `RoundTripDistance` 更新为 70；
  - `DriveTime` 自动计算为 24（小时，向上取整）；
  - `Amount` 字段计算为非空金额（例如 `1774.00`）；
- 对应车辆 `CarID = 京C11111` 的 `car` 表记录：
  - `RUNKM` 在原有基础上增加 70；
  - `RunHour` 在原有基础上增加 24；
  - `CarStatus` 由“已租”恢复为“可租（0）”；
- 若订单有司机 `DNo`，`driver` 表中对应司机的 `DStatus` 更新为“空闲”。

**验证 SQL 示例：**

```sql
-- 查看指定订单
SELECT InvoiceNo, LoanDate, ReturnDate, RoundTripDistance, DriveTime, Amount
FROM rental
WHERE InvoiceNo = 'INV0000011';

-- 查看车辆结算前/后的累计里程和小时数
SELECT CarID, RUNKM, RunHour, CarStatus
FROM car
WHERE CarID = '京C11111';
```

### 测试12: 客户个人中心查看订单

1. 使用创建订单的客户账号重新登录前端。
2. 进入“个人中心”页面（`/user/profile`），切换到“我的订单”标签。 
3. 在“现有订单”列表中确认：
   - 刚结算完成的订单已从“现有订单”列表移出；
4. 切换到“历史订单”列表：
   - 可以看到刚才结算的订单；
   - 金额状态显示 `¥1774`（以实际计算为准）；
   - 订单状态显示为“已完成”；
   - 行驶里程显示为 `70 km`；
   - 行驶时长显示为 `24 h`；
   - 若订单选择了司机，则司机栏显示具体司机姓名（如“钱七”），未选择则显示“自驾”。
5. 点击“详情”按钮打开订单详情弹窗，检查：
   - 基本信息中订单号、车辆、车型、司机显示正确；
   - “金额状态”展示的是 `¥金额` 而非“待结算”；
   - “订单状态”显示“已完成”；
   - “行驶里程/行驶时长”与数据库中的 `RoundTripDistance` / `DriveTime` 一致；
   - 行程路线的时间与地点正确展示。

### 测试13: 重复结算/多次保存的安全性

1. 在管理员端对同一订单执行第二次“保存”（不修改行程数据，或仅做微调）。
2. 再次检查车辆 `RUNKM` / `RunHour`：
   - 若未修改 `RoundTripDistance` / `DriveTime`，则车辆累计里程/小时不应再增加；
   - 若将里程从 70 改为 80，则 `RUNKM` 仅应增加 10，而不是再次增加 80。

**验证 SQL 示例：**

```sql
-- 多次保存前后，对比车辆累计运行数据
SELECT CarID, RUNKM, RunHour
FROM car
WHERE CarID = '京C11111';
```

> 说明：系统在 `RentalService.updateRental` 中使用“新值 - 旧值”的增量方式更新车辆行驶数据，避免管理员多次保存导致重复累加。

## 错误场景测试

### 测试7: 重复注册

1. 尝试用相同的手机号再次注册客户
2. 点击"立即注册"

**预期结果**:
- 显示错误消息："该手机号已被注册"
- 不会跳转页面

### 测试8: 错误密码登录

1. 使用正确的账号但错误的密码登录
2. 点击"登录"

**预期结果**:
- 显示错误消息："账号或密码错误"（客户）或"工号或密码错误"（司机）
- 不会跳转页面

### 测试9: 表单验证

1. 尝试提交空表单
2. 尝试输入格式错误的手机号（客户注册）
3. 尝试输入少于6位的密码
4. 尝试输入不一致的确认密码

**预期结果**:
- 每个字段都显示相应的验证错误消息
- 不会发送API请求

## 数据库验证

### 查看客户数据

```sql
-- 查看客户信息
SELECT * FROM customer;

-- 查看客户登录凭证
SELECT * FROM customer_login;
```

### 查看司机数据

```sql
-- 查看司机信息
SELECT * FROM driver;

-- 查看司机登录凭证
SELECT * FROM driver_login;
```

### 验证编号递增

```sql
-- 注册多个客户，验证客户号是否递增
-- C0000001, C0000002, C0000003 ...
SELECT CNo FROM customer ORDER BY CNo;

-- 注册多个司机，验证司机号是否递增
-- D0000001, D0000002, D0000003 ...
SELECT DNo FROM driver ORDER BY DNo;
```

## 浏览器开发者工具检查

### 1. Network标签

- 打开浏览器开发者工具（F12）
- 切换到Network标签
- 执行登录或注册操作
- 检查API请求：
  - 请求URL: `http://localhost:8080/api/auth/login` 或 `/register/customer` 或 `/register/driver`
  - 请求方法: POST
  - 状态码: 200（成功）或 4xx/5xx（错误）
  - 响应数据: JSON格式

### 2. Console标签

- 查看是否有JavaScript错误
- 查看console.log输出的调试信息

### 3. Application标签

- 查看LocalStorage
- 验证 `userInfo` 和 `userRole` 是否正确保存

## 常见问题排查

### 问题1: 无法连接后端

**症状**: 显示"网络错误，请检查您的网络连接"

**解决方案**:
1. 确认后端服务是否正在运行
2. 检查后端端口是否为8080
3. 检查前端API配置中的baseURL是否正确

### 问题2: CORS错误

**症状**: 浏览器控制台显示CORS相关错误

**解决方案**:
1. 确认后端Controller上有 `@CrossOrigin(origins = "*")` 注解
2. 清除浏览器缓存并重新加载

### 问题3: 数据库连接失败

**症状**: 后端日志显示数据库连接错误

**解决方案**:
1. 检查MySQL服务是否启动
2. 验证 `application.properties` 中的数据库配置
3. 确认数据库用户名和密码正确

### 问题4: 密码验证失败

**症状**: 使用正确的密码仍然提示密码错误

**解决方案**:
1. 检查密码是否正确加密存储
2. 如果是手动插入的测试数据，确保使用BCrypt加密

## 性能测试

### 并发注册测试

可以使用以下脚本测试多个用户同时注册（验证编号生成的线程安全性）：

```javascript
// 在浏览器控制台执行
const registerCustomers = async () => {
  const promises = []
  for (let i = 0; i < 10; i++) {
    promises.push(
      fetch('http://localhost:8080/api/auth/register/customer', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name: `测试用户${i}`,
          phone: `1380013800${i}`,
          password: '123456'
        })
      })
    )
  }
  const results = await Promise.all(promises)
  console.log('完成', results.length, '个注册请求')
}

registerCustomers()
```

然后查询数据库验证所有客户号是否唯一且连续。

## 成功标志

完成以上所有测试后，您应该能够：

✅ 成功注册客户账号
✅ 成功注册司机账号
✅ 使用客户号或手机号登录
✅ 使用司机工号登录
✅ 使用管理员账号登录
✅ 客户号和司机号按递增顺序生成
✅ 密码正确加密存储
✅ 错误场景得到正确处理
✅ 用户信息正确保存到LocalStorage

恭喜！前后端集成已成功完成！
