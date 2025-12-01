# 数据库初始化指南

## 🚀 首次运行（新团队成员）

### 步骤 1：修改配置文件

打开 `src/main/resources/application.properties`，找到以下配置：

```properties
# 首次运行设置为 always，表创建后改为 never
spring.sql.init.mode=never
```

将 `never` **临时改为** `always`：

```properties
spring.sql.init.mode=always
```

### 步骤 2：配置数据库连接

确保数据库连接信息正确：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/car_rental_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=你的密码
```

### 步骤 3：启动应用

运行 Spring Boot 应用，系统会：
1. 自动创建数据库 `CarRentalDB`（如果不存在）
2. 执行 `schema.sql` 创建 6 张表
3. 执行 `data.sql` 插入测试数据

### 步骤 4：改回配置（重要！）

初始化完成后，**立即**将配置改回：

```properties
spring.sql.init.mode=never
```

这样下次启动时不会重新执行 SQL 脚本。

---

## 🔄 日常开发

配置改为 `never` 后：
- ✅ 每次启动**不会**删除表
- ✅ 数据会**保留**
- ✅ 可以正常进行开发和测试

---

## 🔧 重置数据库（需要时）

如果需要重置所有数据：

### 方法 1：通过配置文件（推荐）

1. 将 `spring.sql.init.mode=never` 改为 `always`
2. 重启应用
3. 改回 `never`

### 方法 2：手动执行 SQL

在 MySQL 中执行：

```sql
-- 删除所有表（注意顺序）
DROP TABLE IF EXISTS rental;
DROP TABLE IF EXISTS car_repair;
DROP TABLE IF EXISTS car;
DROP TABLE IF EXISTS car_type;
DROP TABLE IF EXISTS driver;
DROP TABLE IF EXISTS customer;

-- 重新执行 schema.sql 和 data.sql
SOURCE /path/to/schema.sql;
SOURCE /path/to/data.sql;
```

---

## ⚙️ 配置说明

### `spring.sql.init.mode` 参数

| 值 | 说明 | 使用场景 |
|----|------|----------|
| `never` | 从不执行 SQL 脚本 | 日常开发（默认） |
| `always` | 总是执行 SQL 脚本 | 首次初始化 |
| `embedded` | 仅在嵌入式数据库时执行 | H2 等内存数据库 |

### `CREATE TABLE IF NOT EXISTS`

- 只在表不存在时创建
- 不会删除已有数据
- 适合团队协作

---

## 📋 表清单

✅ 6 张表已创建：

1. `customer` - 客户表
2. `driver` - 司机表
3. `car_type` - 汽车类型表
4. `car` - 汽车表
5. `car_repair` - 汽车维修表
6. `rental` - 租赁业务表

---

## ⚠️ 注意事项

1. **首次运行后务必将 `mode` 改回 `never`**，否则每次启动都会执行脚本
2. **不要直接删除 `schema.sql` 和 `data.sql`**，新成员需要这些文件初始化
3. **生产环境必须设置 `spring.sql.init.mode=never`**
4. **测试数据可根据需要修改 `data.sql`**

---

## 🆘 常见问题

### Q: 启动时报错 "Table 'xxx' already exists"

**A:** 将 `spring.sql.init.mode` 改为 `never`

### Q: 表已创建但没有数据

**A:** 检查 `data.sql` 是否执行，确保 `spring.sql.init.mode=always`

### Q: 外键约束错误

**A:** 删除表时顺序错误，使用提供的删除脚本（按依赖关系）

### Q: 想要清空数据但保留表结构

**A:** 执行 `TRUNCATE TABLE 表名` 或删除 `data.sql` 的内容

