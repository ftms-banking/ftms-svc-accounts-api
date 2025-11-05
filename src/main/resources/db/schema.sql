-- ============================================
-- FTMS Database Schema (Idempotent)
-- Uses IF NOT EXISTS for tables.
-- For indexes (MySQL < 8.0.21), uses conditional CREATE via information_schema.
-- ============================================

-- Customer Table
CREATE TABLE IF NOT EXISTS customer (
                                        uuid VARCHAR(36) PRIMARY KEY COMMENT 'Customer unique identifier',
    email VARCHAR(255) NOT NULL UNIQUE COMMENT 'Customer email address',
    first_name VARCHAR(50) NOT NULL COMMENT 'Customer first name',
    last_name VARCHAR(50) NOT NULL COMMENT 'Customer last name',
    date_of_birth DATE NOT NULL COMMENT 'Customer date of birth',
    phone_number VARCHAR(20) NOT NULL COMMENT 'Customer phone number (E.164 format)',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'Customer account status',
    kyc_status VARCHAR(20) NOT NULL DEFAULT 'NOT_SUBMITTED' COMMENT 'KYC verification status',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Record creation timestamp',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Record last update timestamp',
    version BIGINT NOT NULL DEFAULT 0 COMMENT 'Optimistic locking version',

    INDEX idx_customer_email (email),
    INDEX idx_customer_status (status),
    INDEX idx_customer_kyc_status (kyc_status),
    INDEX idx_customer_created_at (created_at),
    INDEX idx_customer_name_search (first_name, last_name),

    CONSTRAINT chk_customer_status CHECK (
                                             status IN ('PENDING', 'ACTIVE', 'SUSPENDED', 'BLOCKED', 'CLOSED')
    ),
    CONSTRAINT chk_customer_kyc_status CHECK (
                                                 kyc_status IN ('NOT_SUBMITTED', 'SUBMITTED', 'UNDER_REVIEW', 'APPROVED', 'REJECTED')
    )
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Customer information table';

-- Address Table
CREATE TABLE IF NOT EXISTS address (
                                       uuid VARCHAR(36) PRIMARY KEY COMMENT 'Address unique identifier',
    customer_id VARCHAR(36) NOT NULL COMMENT 'Foreign key to customer',
    street VARCHAR(255) NOT NULL COMMENT 'Street address',
    city VARCHAR(100) NOT NULL COMMENT 'City name',
    state VARCHAR(100) COMMENT 'State or province',
    postal_code VARCHAR(20) NOT NULL COMMENT 'Postal or ZIP code',
    country CHAR(2) NOT NULL COMMENT 'ISO 3166-1 alpha-2 country code',
    address_type VARCHAR(20) NOT NULL DEFAULT 'PRIMARY' COMMENT 'Address type classification',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Record creation timestamp',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Record last update timestamp',
    version BIGINT NOT NULL DEFAULT 0 COMMENT 'Optimistic locking version',

    INDEX idx_address_customer_id (customer_id),
    INDEX idx_address_country (country),
    INDEX idx_address_type (address_type),

    FOREIGN KEY (customer_id) REFERENCES customer(uuid) ON DELETE CASCADE ON UPDATE CASCADE,

    CONSTRAINT chk_address_type CHECK (
                                          address_type IN ('PRIMARY', 'BILLING', 'SHIPPING', 'MAILING')
    )
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Customer address table';

-- KYC Document Table
CREATE TABLE IF NOT EXISTS kyc_document (
                                            uuid VARCHAR(36) PRIMARY KEY COMMENT 'KYC document unique identifier',
    customer_id VARCHAR(36) NOT NULL COMMENT 'Foreign key to customer',
    document_type VARCHAR(30) NOT NULL COMMENT 'Type of KYC document',
    document_number VARCHAR(100) NOT NULL COMMENT 'Document identification number',
    file_path VARCHAR(500) NOT NULL COMMENT 'Storage path (S3/local)',
    file_name VARCHAR(255) NOT NULL COMMENT 'Original file name',
    file_size BIGINT NOT NULL COMMENT 'File size in bytes',
    mime_type VARCHAR(100) NOT NULL COMMENT 'MIME type (e.g., image/jpeg)',
    expiry_date DATE COMMENT 'Document expiration date',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'Verification status',
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Upload timestamp',
    verified_at TIMESTAMP NULL COMMENT 'Verification timestamp',
    verified_by VARCHAR(36) COMMENT 'UUID of verifier',
    rejection_reason TEXT COMMENT 'Reason if rejected',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Record creation timestamp',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Record last update timestamp',
    version BIGINT NOT NULL DEFAULT 0 COMMENT 'Optimistic locking version',

    INDEX idx_kyc_customer_id (customer_id),
    INDEX idx_kyc_document_type (document_type),
    INDEX idx_kyc_status (status),
    INDEX idx_kyc_uploaded_at (uploaded_at),

    FOREIGN KEY (customer_id) REFERENCES customer(uuid) ON DELETE CASCADE ON UPDATE CASCADE,

    CONSTRAINT chk_kyc_document_type CHECK (
                                               document_type IN ('PASSPORT', 'DRIVERS_LICENSE', 'NATIONAL_ID', 'PROOF_OF_ADDRESS', 'UTILITY_BILL')
    ),
    CONSTRAINT chk_kyc_document_status CHECK (
                                                 status IN ('PENDING', 'APPROVED', 'REJECTED', 'EXPIRED')
    )
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='KYC document verification table';

-- ============================================
-- ACCOUNT SERVICE TABLES
-- ============================================

-- Account Table
CREATE TABLE IF NOT EXISTS account (
                                       uuid VARCHAR(36) PRIMARY KEY COMMENT 'Account unique identifier',
    customer_id VARCHAR(36) NOT NULL COMMENT 'Foreign key to customer',
    account_number VARCHAR(20) NOT NULL UNIQUE COMMENT 'Unique account number',
    account_type VARCHAR(20) NOT NULL COMMENT 'Type of account',
    currency CHAR(3) NOT NULL COMMENT 'ISO 4217 currency code',
    balance DECIMAL(19, 4) NOT NULL DEFAULT 0.0000 COMMENT 'Current balance',
    available_balance DECIMAL(19, 4) NOT NULL DEFAULT 0.0000 COMMENT 'Available balance for withdrawal',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'Account status',
    opened_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Account opening timestamp',
    closed_at TIMESTAMP NULL COMMENT 'Account closure timestamp',
    closure_reason TEXT COMMENT 'Reason for closure',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Record creation timestamp',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Record last update timestamp',
    version BIGINT NOT NULL DEFAULT 0 COMMENT 'Optimistic locking version',

    INDEX idx_account_customer_id (customer_id),
    INDEX idx_account_number (account_number),
    INDEX idx_account_type (account_type),
    INDEX idx_account_status (status),
    INDEX idx_account_currency (currency),
    INDEX idx_account_customer_status (customer_id, status),

    FOREIGN KEY (customer_id) REFERENCES customer(uuid) ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT chk_account_type CHECK (
                                          account_type IN ('SAVINGS', 'CHECKING', 'BUSINESS', 'INVESTMENT')
    ),
    CONSTRAINT chk_account_status CHECK (
                                            status IN ('PENDING', 'ACTIVE', 'SUSPENDED', 'DORMANT', 'CLOSED')
    ),
    CONSTRAINT chk_account_balance_non_negative CHECK (balance >= 0),
    CONSTRAINT chk_account_available_balance_non_negative CHECK (available_balance >= 0),
    CONSTRAINT chk_account_available_balance_lte_balance CHECK (available_balance <= balance)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Financial account table';

-- Account Balance History Table
CREATE TABLE IF NOT EXISTS account_balance_history (
                                                       id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Auto-increment ID',
                                                       account_id VARCHAR(36) NOT NULL COMMENT 'Foreign key to account',
    previous_balance DECIMAL(19, 4) NOT NULL COMMENT 'Previous balance before change',
    new_balance DECIMAL(19, 4) NOT NULL COMMENT 'New balance after change',
    change_amount DECIMAL(19, 4) NOT NULL COMMENT 'Amount of change (+ or -)',
    change_reason VARCHAR(100) NOT NULL COMMENT 'Reason for balance change',
    transaction_id VARCHAR(36) COMMENT 'Related transaction ID',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Change timestamp',

    INDEX idx_balance_history_account_id (account_id),
    INDEX idx_balance_history_transaction_id (transaction_id),
    INDEX idx_balance_history_created_at (created_at),
    INDEX idx_balance_history_account_date (account_id, created_at),

    FOREIGN KEY (account_id) REFERENCES account(uuid) ON DELETE CASCADE ON UPDATE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Account balance change history';

-- ============================================
-- TRANSACTION SERVICE TABLES
-- ============================================

-- Transaction Table (note: reserved word; quoted)
CREATE TABLE IF NOT EXISTS `transaction` (
                                             uuid VARCHAR(36) PRIMARY KEY COMMENT 'Transaction unique identifier',
    idempotency_key VARCHAR(100) NOT NULL UNIQUE COMMENT 'Unique key to prevent duplicate transactions',
    source_account_id VARCHAR(36) NOT NULL COMMENT 'Source account foreign key',
    destination_account_id VARCHAR(36) NOT NULL COMMENT 'Destination account foreign key',
    amount DECIMAL(19, 4) NOT NULL COMMENT 'Transaction amount',
    currency CHAR(3) NOT NULL COMMENT 'ISO 4217 currency code',
    description VARCHAR(500) COMMENT 'Transaction description',
    type VARCHAR(20) NOT NULL COMMENT 'Transaction type',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'Transaction status',
    reference_number VARCHAR(50) NOT NULL UNIQUE COMMENT 'Unique reference for tracking',
    reversal_of VARCHAR(36) COMMENT 'UUID of original transaction if this is a reversal',
    reversed_by VARCHAR(36) COMMENT 'UUID of reversal transaction',
    failure_reason TEXT COMMENT 'Reason if transaction failed',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Transaction creation timestamp',
    completed_at TIMESTAMP NULL COMMENT 'Transaction completion timestamp',
    version BIGINT NOT NULL DEFAULT 0 COMMENT 'Optimistic locking version',

    INDEX idx_transaction_source_account_id (source_account_id),
    INDEX idx_transaction_destination_account_id (destination_account_id),
    INDEX idx_transaction_idempotency_key (idempotency_key),
    INDEX idx_transaction_reference_number (reference_number),
    INDEX idx_transaction_type (type),
    INDEX idx_transaction_status (status),
    INDEX idx_transaction_created_at (created_at),
    INDEX idx_transaction_reversal_of (reversal_of),
    INDEX idx_transaction_source_date (source_account_id, created_at),
    INDEX idx_transaction_destination_date (destination_account_id, created_at),

    FOREIGN KEY (source_account_id) REFERENCES account(uuid) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (destination_account_id) REFERENCES account(uuid) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (reversal_of) REFERENCES `transaction`(uuid) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (reversed_by) REFERENCES `transaction`(uuid) ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT chk_transaction_type CHECK (
                                              type IN ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER', 'FEE', 'INTEREST', 'REFUND')
    ),
    CONSTRAINT chk_transaction_status CHECK (
                                                status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'REVERSED', 'CANCELLED')
    ),
    CONSTRAINT chk_transaction_amount_positive CHECK (amount > 0)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Financial transaction table';

-- Transaction SAGA Table
CREATE TABLE IF NOT EXISTS transaction_saga (
                                                saga_id VARCHAR(36) PRIMARY KEY COMMENT 'SAGA unique identifier',
    transaction_id VARCHAR(36) NOT NULL COMMENT 'Related transaction ID',
    saga_type VARCHAR(50) NOT NULL COMMENT 'Type of SAGA (e.g., TRANSFER, PAYMENT)',
    current_step VARCHAR(50) NOT NULL COMMENT 'Current step in SAGA',
    status VARCHAR(20) NOT NULL COMMENT 'SAGA status',
    payload JSON NOT NULL COMMENT 'SAGA payload data',
    error_message TEXT COMMENT 'Error message if SAGA failed',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'SAGA creation timestamp',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'SAGA last update timestamp',

    INDEX idx_saga_transaction_id (transaction_id),
    INDEX idx_saga_status (status),
    INDEX idx_saga_type (saga_type),
    INDEX idx_saga_created_at (created_at),

    FOREIGN KEY (transaction_id) REFERENCES `transaction`(uuid) ON DELETE CASCADE ON UPDATE CASCADE,

    CONSTRAINT chk_saga_status CHECK (
                                         status IN ('STARTED', 'IN_PROGRESS', 'COMPLETED', 'COMPENSATING', 'COMPENSATED', 'FAILED')
    )
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Transaction SAGA orchestration table';

-- ============================================
-- COMPLIANCE SERVICE TABLES
-- ============================================

-- Compliance Event Table
CREATE TABLE IF NOT EXISTS compliance_event (
                                                uuid VARCHAR(36) PRIMARY KEY COMMENT 'Compliance event unique identifier',
    entity_type VARCHAR(20) NOT NULL COMMENT 'Type of entity (CUSTOMER, ACCOUNT, TRANSACTION)',
    entity_id VARCHAR(36) NOT NULL COMMENT 'UUID of the related entity',
    event_type VARCHAR(50) NOT NULL COMMENT 'Type of compliance event',
    description TEXT NOT NULL COMMENT 'Event description',
    severity VARCHAR(20) NOT NULL DEFAULT 'INFO' COMMENT 'Event severity level',
    metadata JSON COMMENT 'Additional event data',
    created_by VARCHAR(36) COMMENT 'UUID of user who triggered event',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Event timestamp',

    INDEX idx_compliance_entity_type_id (entity_type, entity_id),
    INDEX idx_compliance_event_type (event_type),
    INDEX idx_compliance_severity (severity),
    INDEX idx_compliance_created_at (created_at),
    INDEX idx_compliance_created_by (created_by),

    CONSTRAINT chk_compliance_entity_type CHECK (
                                                    entity_type IN ('CUSTOMER', 'ACCOUNT', 'TRANSACTION', 'SYSTEM')
    ),
    CONSTRAINT chk_compliance_severity CHECK (
                                                 severity IN ('INFO', 'WARNING', 'CRITICAL', 'ALERT')
    )
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Compliance event tracking table';

-- Audit Log Table
CREATE TABLE IF NOT EXISTS audit_log (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Auto-increment ID',
                                         event_id VARCHAR(36) NOT NULL UNIQUE COMMENT 'Event unique identifier',
    service_name VARCHAR(50) NOT NULL COMMENT 'Service that generated the event',
    entity_type VARCHAR(50) NOT NULL COMMENT 'Type of entity',
    entity_id VARCHAR(36) NOT NULL COMMENT 'UUID of the entity',
    action VARCHAR(50) NOT NULL COMMENT 'Action performed',
    user_id VARCHAR(36) COMMENT 'UUID of user who performed action',
    ip_address VARCHAR(45) COMMENT 'IP address of request',
    user_agent VARCHAR(500) COMMENT 'User agent string',
    request_payload JSON COMMENT 'Request payload',
    response_payload JSON COMMENT 'Response payload',
    status VARCHAR(20) NOT NULL COMMENT 'Action status',
    error_message TEXT COMMENT 'Error message if action failed',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Audit log timestamp',

    INDEX idx_audit_service_name (service_name),
    INDEX idx_audit_entity_type_id (entity_type, entity_id),
    INDEX idx_audit_user_id (user_id),
    INDEX idx_audit_action (action),
    INDEX idx_audit_status (status),
    INDEX idx_audit_created_at (created_at),
    INDEX idx_audit_service_entity_date (service_name, entity_type, created_at),

    CONSTRAINT chk_audit_status CHECK (
                                          status IN ('SUCCESS', 'FAILED', 'PARTIAL')
    )
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='System-wide audit log table';

-- ============================================
-- Performance Indexes (Idempotent for MySQL < 8.0.21)
-- Each block checks information_schema before creating the index.
-- ============================================

-- Helper macro pattern used repeatedly:
-- SET @exists := (SELECT COUNT(1) FROM information_schema.statistics
--   WHERE table_schema = DATABASE() AND table_name = 'table' AND index_name = 'index');
-- SET @sql := IF(@exists = 0, 'CREATE INDEX index ON table(cols)', 'SELECT 1');
-- PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- Customer Service Indexes
SET @exists := (SELECT COUNT(1) FROM information_schema.statistics WHERE table_schema = DATABASE() AND table_name = 'customer' AND index_name = 'idx_customer_status_kyc');
SET @sql := IF(@exists = 0, 'CREATE INDEX idx_customer_status_kyc ON customer(status, kyc_status)', 'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @exists := (SELECT COUNT(1) FROM information_schema.statistics WHERE table_schema = DATABASE() AND table_name = 'customer' AND index_name = 'idx_customer_email_status');
SET @sql := IF(@exists = 0, 'CREATE INDEX idx_customer_email_status ON customer(email, status)', 'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- Account Service Indexes
SET @exists := (SELECT COUNT(1) FROM information_schema.statistics WHERE table_schema = DATABASE() AND table_name = 'account' AND index_name = 'idx_account_type_currency');
SET @sql := IF(@exists = 0, 'CREATE INDEX idx_account_type_currency ON account(account_type, currency)', 'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @exists := (SELECT COUNT(1) FROM information_schema.statistics WHERE table_schema = DATABASE() AND table_name = 'account' AND index_name = 'idx_account_customer_type');
SET @sql := IF(@exists = 0, 'CREATE INDEX idx_account_customer_type ON account(customer_id, account_type)', 'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- Transaction Service Indexes (table quoted)
SET @exists := (SELECT COUNT(1) FROM information_schema.statistics WHERE table_schema = DATABASE() AND table_name = 'transaction' AND index_name = 'idx_transaction_status_type');
SET @sql := IF(@exists = 0, 'CREATE INDEX idx_transaction_status_type ON `transaction`(status, type)', 'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @exists := (SELECT COUNT(1) FROM information_schema.statistics WHERE table_schema = DATABASE() AND table_name = 'transaction' AND index_name = 'idx_transaction_date_status');
SET @sql := IF(@exists = 0, 'CREATE INDEX idx_transaction_date_status ON `transaction`(created_at DESC, status)', 'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @exists := (SELECT COUNT(1) FROM information_schema.statistics WHERE table_schema = DATABASE() AND table_name = 'transaction' AND index_name = 'idx_transaction_source_dest');
SET @sql := IF(@exists = 0, 'CREATE INDEX idx_transaction_source_dest ON `transaction`(source_account_id, destination_account_id)', 'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- Compliance & Audit Indexes
SET @exists := (SELECT COUNT(1) FROM information_schema.statistics WHERE table_schema = DATABASE() AND table_name = 'compliance_event' AND index_name = 'idx_compliance_entity_date');
SET @sql := IF(@exists = 0, 'CREATE INDEX idx_compliance_entity_date ON compliance_event(entity_type, entity_id, created_at DESC)', 'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @exists := (SELECT COUNT(1) FROM information_schema.statistics WHERE table_schema = DATABASE() AND table_name = 'audit_log' AND index_name = 'idx_audit_user_date');
SET @sql := IF(@exists = 0, 'CREATE INDEX idx_audit_user_date ON audit_log(user_id, created_at DESC)', 'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- Full-text (optional)
-- ALTER TABLE customer ADD FULLTEXT INDEX idx_customer_fulltext (first_name, last_name, email);
-- ALTER TABLE `transaction` ADD FULLTEXT INDEX idx_transaction_fulltext (description, reference_number);
