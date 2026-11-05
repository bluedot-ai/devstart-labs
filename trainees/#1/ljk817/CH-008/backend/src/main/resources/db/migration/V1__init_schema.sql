CREATE TABLE customer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    contact_name VARCHAR(80),
    phone VARCHAR(40),
    email VARCHAR(160),
    description VARCHAR(500),
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    INDEX idx_customer_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE customer_issue (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    issue_no VARCHAR(40) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(80) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    assignee VARCHAR(80),
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    closed_at DATETIME(6),
    CONSTRAINT fk_issue_customer FOREIGN KEY (customer_id) REFERENCES customer(id),
    INDEX idx_issue_customer (customer_id),
    INDEX idx_issue_status (status),
    INDEX idx_issue_severity (severity)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE issue_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    issue_id BIGINT NOT NULL,
    content VARCHAR(1000) NOT NULL,
    operator_name VARCHAR(80) NOT NULL,
    old_status VARCHAR(20),
    new_status VARCHAR(20),
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_record_issue FOREIGN KEY (issue_id) REFERENCES customer_issue(id) ON DELETE CASCADE,
    INDEX idx_record_issue_created (issue_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
