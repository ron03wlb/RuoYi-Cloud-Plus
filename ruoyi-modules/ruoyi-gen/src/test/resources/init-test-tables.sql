-- 代码生成模块集成测试初始化脚本
-- 创建测试表用于代码生成

-- 清理旧数据
DROP TABLE IF EXISTS test_user;
DROP TABLE IF EXISTS test_product;
DROP TABLE IF EXISTS gen_table;
DROP TABLE IF EXISTS gen_table_column;

-- 创建测试用户表
CREATE TABLE test_user
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username    VARCHAR(50) NOT NULL COMMENT '用户名',
    email       VARCHAR(100) COMMENT '邮箱',
    phone       VARCHAR(20) COMMENT '手机号',
    status      CHAR(1)  DEFAULT '0' COMMENT '状态（0正常 1停用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark      VARCHAR(500) COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测试用户表';

-- 创建测试产品表
CREATE TABLE test_product
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '产品ID',
    product_name VARCHAR(100) NOT NULL COMMENT '产品名称',
    product_code VARCHAR(50) COMMENT '产品编码',
    price        DECIMAL(10, 2) COMMENT '价格',
    stock        INT COMMENT '库存',
    category     VARCHAR(50) COMMENT '分类',
    create_time  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测试产品表';

-- 创建代码生成表信息表
CREATE TABLE gen_table
(
    table_id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '编号',
    data_name         VARCHAR(255) DEFAULT '' COMMENT '数据源名称',
    table_name        VARCHAR(200) DEFAULT '' COMMENT '表名称',
    table_comment     VARCHAR(500) DEFAULT '' COMMENT '表描述',
    sub_table_name    VARCHAR(64)  DEFAULT NULL COMMENT '关联子表的表名',
    sub_table_fk_name VARCHAR(64)  DEFAULT NULL COMMENT '子表关联的外键名',
    class_name        VARCHAR(100) DEFAULT '' COMMENT '实体类名称',
    tpl_category      VARCHAR(200) DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作）',
    package_name      VARCHAR(100) COMMENT '生成包路径',
    module_name       VARCHAR(30) COMMENT '生成模块名',
    business_name     VARCHAR(30) COMMENT '生成业务名',
    function_name     VARCHAR(50) COMMENT '生成功能名',
    function_author   VARCHAR(50) COMMENT '生成功能作者',
    gen_type          CHAR(1)      DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
    gen_path          VARCHAR(200) DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
    options           VARCHAR(1000) COMMENT '其它生成选项',
    create_time       DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time       DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark            VARCHAR(500) DEFAULT NULL COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='代码生成业务表';

-- 创建代码生成表字段信息表
CREATE TABLE gen_table_column
(
    column_id      BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '编号',
    table_id       BIGINT COMMENT '归属表编号',
    column_name    VARCHAR(200) COMMENT '列名称',
    column_comment VARCHAR(500) COMMENT '列描述',
    column_type    VARCHAR(100) COMMENT '列类型',
    java_type      VARCHAR(500) COMMENT 'JAVA类型',
    java_field     VARCHAR(200) COMMENT 'JAVA字段名',
    is_pk          CHAR(1) COMMENT '是否主键（1是）',
    is_increment   CHAR(1) COMMENT '是否自增（1是）',
    is_required    CHAR(1) COMMENT '是否必填（1是）',
    is_insert      CHAR(1) COMMENT '是否为插入字段（1是）',
    is_edit        CHAR(1) COMMENT '是否编辑字段（1是）',
    is_list        CHAR(1) COMMENT '是否列表字段（1是）',
    is_query       CHAR(1) COMMENT '是否查询字段（1是）',
    query_type     VARCHAR(200) DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
    html_type      VARCHAR(200) COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
    dict_type      VARCHAR(200) DEFAULT '' COMMENT '字典类型',
    sort           INT COMMENT '排序',
    create_time    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='代码生成业务表字段';

-- 插入测试数据
INSERT INTO test_user (username, email, phone, status, remark)
VALUES ('testuser1', 'test1@example.com', '13800138001', '0', '测试用户1'),
       ('testuser2', 'test2@example.com', '13800138002', '0', '测试用户2');

INSERT INTO test_product (product_name, product_code, price, stock, category)
VALUES ('测试产品A', 'PRD001', 99.99, 100, '电子产品'),
       ('测试产品B', 'PRD002', 199.99, 50, '家居用品');
