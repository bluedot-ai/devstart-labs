ALTER TABLE system_user
    ADD COLUMN customer_id BIGINT NULL AFTER role,
    ADD CONSTRAINT fk_system_user_customer FOREIGN KEY (customer_id) REFERENCES customer(id),
    ADD INDEX idx_system_user_customer (customer_id);

ALTER TABLE issue_record
    ADD COLUMN customer_visible BOOLEAN NOT NULL DEFAULT FALSE AFTER operator_name,
    ADD COLUMN author_role VARCHAR(20) NULL AFTER customer_visible;
