-- 创建供应商表
CREATE TABLE IF NOT EXISTS `supplier` (
    `id` int NOT NULL COMMENT '供应商ID',
    `code` varchar(20) NOT NULL COMMENT '供应商编号',
    `name` varchar(100) NOT NULL COMMENT '供应商名称',
    `contact_person` varchar(50) DEFAULT NULL COMMENT '联系人',
    `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
    `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
    `address` varchar(255) DEFAULT NULL COMMENT '地址',
    `product_range` varchar(500) DEFAULT NULL COMMENT '产品范围',
    `status` int NOT NULL DEFAULT '1' COMMENT '供应商状态（1: 合作中, 2: 已暂停, 3: 已终止）',
    `bank_account` varchar(50) DEFAULT NULL COMMENT '银行账号',
    `bank_name` varchar(100) DEFAULT NULL COMMENT '开户银行',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `update_time` datetime NOT NULL COMMENT '更新时间',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_name` (`name`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='供应商信息表';

-- 示例数据
INSERT INTO `supplier` (`id`, `code`, `name`, `contact_person`, `phone`, `email`, `address`, `product_range`, `status`, `bank_account`, `bank_name`, `create_time`, `update_time`, `remark`) VALUES
(1001, 'SP10001', '北京科技有限公司', '张三', '13800138001', 'zhangsan@example.com', '北京市海淀区中关村科技园', '办公设备、电子产品', 1, '622848123456789', '中国银行北京分行', '2023-01-15 10:30:00', '2023-01-15 10:30:00', '重要供应商'),
(1002, 'SP10002', '上海商贸有限公司', '李四', '13900139002', 'lisi@example.com', '上海市浦东新区陆家嘴金融区', '办公用品、文具', 1, '622848987654321', '中国工商银行上海分行', '2023-02-20 14:15:00', '2023-02-20 14:15:00', '常规供应商'),
(1003, 'SP10003', '广州贸易有限公司', '王五', '13700137003', 'wangwu@example.com', '广州市天河区珠江新城', '食品、饮料', 2, '622848456789123', '招商银行广州分行', '2023-03-10 09:45:00', '2023-05-15 16:30:00', '合作暂停，质量问题待解决'); 