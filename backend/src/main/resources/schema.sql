-- ================================================
-- Car Rental System Database Schema
-- ================================================
-- 创建日期: 2025-12-01
-- 说明: 汽车租赁系统数据库表结构
-- ================================================

-- ================================================
-- 删除现有表（按照外键依赖顺序）
-- ================================================
DROP TABLE IF EXISTS rental;
DROP TABLE IF EXISTS car_repair;
DROP TABLE IF EXISTS car;
DROP TABLE IF EXISTS driver;
DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS car_type;

-- ================================================
-- 表 3.1 Customer（客户表）
-- ================================================
CREATE TABLE IF NOT EXISTS customer (
    CNo         CHAR(8)         NOT NULL COMMENT '客户号',
    CName       CHAR(50)        NOT NULL COMMENT '客户姓名',
    Company     CHAR(100)       NULL COMMENT '客户单位',
    Phone       CHAR(20)        NULL COMMENT '客户电话',
    Address     CHAR(200)       NULL COMMENT '客户地址',
    PostCode    CHAR(6)         NULL COMMENT '邮编',

    PRIMARY KEY (CNo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户表';

-- ================================================
-- 表 3.2 Driver（司机表）
-- ================================================
CREATE TABLE IF NOT EXISTS driver (
    DNo             CHAR(8)         NOT NULL COMMENT '司机号',
    DName           CHAR(50)        NOT NULL COMMENT '姓名',
    StartWork       DATE            NULL COMMENT '入职年月',
    Salary          NUMERIC(6,2)    NULL COMMENT '基本工资',
    DStatus         CHAR(5)         NULL COMMENT '司机状态（在岗/空闲等）',
    LicenseType     CHAR(10)        NULL COMMENT '准驾车型',

    PRIMARY KEY (DNo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='司机表';

-- ================================================
-- 表 3.5 CarType（汽车类型表）
-- ================================================
CREATE TABLE IF NOT EXISTS car_type (
    TNo             CHAR(4)         NOT NULL COMMENT '类型编号',
    TName           CHAR(20)        NOT NULL COMMENT '汽车型号',
    RentRate        NUMERIC(4,2)    NULL COMMENT '租金率',

    PRIMARY KEY (TNo, TName)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='汽车类型表';

-- ================================================
-- 表 3.3 Car（汽车表）
-- ================================================
CREATE TABLE IF NOT EXISTS car (
    CarID           CHAR(10)        NOT NULL COMMENT '注册号码',
    PurchaseDate    DATE            NULL COMMENT '购入日期',
    Price           NUMERIC(8,2)    NULL COMMENT '价格',
    CarStatus       CHAR(5)         NULL COMMENT '车辆状态（0-可租；1-已租；2-维修）',
    TNo             CHAR(4)         NULL COMMENT '类型编号（外键）',
    TName           CHAR(20)        NULL COMMENT '汽车型号（外键）',
    RUNKM           NUMERIC(10,2)   NULL COMMENT '运行公里',
    RunHour         NUMERIC(8,2)    NULL COMMENT '运行小时',

    PRIMARY KEY (CarID),
    FOREIGN KEY (TNo, TName) REFERENCES car_type(TNo, TName)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='汽车表';

-- ================================================
-- 表 3.4 CarRepair（汽车维修表）
-- ================================================
CREATE TABLE IF NOT EXISTS car_repair (
    RNo             INT             NOT NULL AUTO_INCREMENT COMMENT '维修编号',
    RDate           DATE            NULL COMMENT '维修日期',
    RunKM           NUMERIC(10,2)   NULL COMMENT '运行公里',
    RunHour         NUMERIC(8,2)    NULL COMMENT '运行小时',
    CarID           CHAR(10)        NULL COMMENT '注册号码（外键）',

    PRIMARY KEY (RNo),
    FOREIGN KEY (CarID) REFERENCES car(CarID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='汽车维修表';

-- ================================================
-- 表 3.6 Rental（租赁业务表）
-- ================================================
CREATE TABLE IF NOT EXISTS rental (
    InvoiceNo           INT             NOT NULL AUTO_INCREMENT COMMENT '发票号',
    LoanDate            DATE            NULL COMMENT '借车日期',
    ReturnDate          DATE            NULL COMMENT '还车日期',
    LoanPlace           CHAR(100)       NULL COMMENT '借车地点',
    ReturnPlace         CHAR(100)       NULL COMMENT '还车地点',
    CarID               CHAR(10)        NULL COMMENT '牌照号（外键）',
    CNo                 CHAR(8)         NULL COMMENT '客户号（外键）',
    DNo                 CHAR(8)         NULL COMMENT '司机号（外键）',
    RoundTripDistance   NUMERIC(8,2)    NULL COMMENT '往返里程',
    DriveTime           NUMERIC(4,2)    NULL COMMENT '行驶时间',
    Amount              NUMERIC(8,2)    NULL COMMENT '应付金额',

    PRIMARY KEY (InvoiceNo),
    FOREIGN KEY (CarID) REFERENCES car(CarID),
    FOREIGN KEY (CNo) REFERENCES customer(CNo),
    FOREIGN KEY (DNo) REFERENCES driver(DNo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租赁业务表';

