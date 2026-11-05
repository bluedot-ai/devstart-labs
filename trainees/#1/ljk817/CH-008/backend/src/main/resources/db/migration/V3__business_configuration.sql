ALTER TABLE customer ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE AFTER description;

CREATE TABLE issue_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(80) NOT NULL UNIQUE,
    description VARCHAR(300),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO issue_category (name, description, active, created_at, updated_at) VALUES
('系统故障', '系统异常、无法访问或功能不可用', TRUE, NOW(6), NOW(6)),
('支付与订单', '支付、退款、订单状态相关问题', TRUE, NOW(6), NOW(6)),
('账号与权限', '登录、账号、角色和权限相关问题', TRUE, NOW(6), NOW(6)),
('使用咨询', '产品操作和业务流程咨询', TRUE, NOW(6), NOW(6)),
('其他', '暂未归入其他分类的问题', TRUE, NOW(6), NOW(6));
