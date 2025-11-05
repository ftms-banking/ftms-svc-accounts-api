-- ============================================
-- FTMS Database Seed Data (Idempotent)
-- Works across re-runs without errors
-- ============================================

SET FOREIGN_KEY_CHECKS = 0;

-- =========================
-- CUSTOMER SERVICE SEEDS
-- =========================
INSERT INTO customer (uuid, email, first_name, last_name, date_of_birth, phone_number, status, kyc_status) VALUES
                                                                                                               ('550e8400-e29b-41d4-a716-446655440001', 'john.doe@example.com', 'John', 'Doe', '1990-05-15', '+1234567890', 'ACTIVE', 'APPROVED'),
                                                                                                               ('550e8400-e29b-41d4-a716-446655440002', 'jane.smith@example.com', 'Jane', 'Smith', '1985-08-20', '+1234567891', 'ACTIVE', 'APPROVED'),
                                                                                                               ('550e8400-e29b-41d4-a716-446655440003', 'bob.johnson@example.com', 'Bob', 'Johnson', '1992-03-10', '+1234567892', 'PENDING', 'SUBMITTED'),
                                                                                                               ('550e8400-e29b-41d4-a716-446655440004', 'alice.williams@example.com', 'Alice', 'Williams', '1988-11-25', '+1234567893', 'ACTIVE', 'APPROVED'),
                                                                                                               ('550e8400-e29b-41d4-a716-446655440005', 'charlie.brown@example.com', 'Charlie', 'Brown', '1995-07-30', '+1234567894', 'SUSPENDED', 'APPROVED')
    ON DUPLICATE KEY UPDATE uuid = uuid;  -- no-op

INSERT INTO address (uuid, customer_id, street, city, state, postal_code, country, address_type) VALUES
                                                                                                     ('650e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', '123 Main St', 'New York', 'NY', '10001', 'US', 'PRIMARY'),
                                                                                                     ('650e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440002', '456 Oak Ave', 'Los Angeles', 'CA', '90001', 'US', 'PRIMARY'),
                                                                                                     ('650e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440003', '789 Pine Rd', 'Chicago', 'IL', '60601', 'US', 'PRIMARY'),
                                                                                                     ('650e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440004', '321 Elm St', 'Houston', 'TX', '77001', 'US', 'PRIMARY'),
                                                                                                     ('650e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440005', '654 Maple Dr', 'Phoenix', 'AZ', '85001', 'US', 'PRIMARY')
    ON DUPLICATE KEY UPDATE uuid = uuid;

INSERT INTO kyc_document (uuid, customer_id, document_type, document_number, file_path, file_name, file_size, mime_type, status) VALUES
                                                                                                                                     ('750e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', 'PASSPORT', 'P12345678', '/kyc/2025/01/passport_john_doe.pdf', 'passport_john_doe.pdf', 2048576, 'application/pdf', 'APPROVED'),
                                                                                                                                     ('750e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440002', 'DRIVERS_LICENSE', 'DL987654321', '/kyc/2025/01/dl_jane_smith.jpg', 'dl_jane_smith.jpg', 1048576, 'image/jpeg', 'APPROVED')
    ON DUPLICATE KEY UPDATE uuid = uuid;

-- =========================
-- ACCOUNT SERVICE SEEDS
-- =========================
INSERT INTO account (uuid, customer_id, account_number, account_type, currency, balance, available_balance, status) VALUES
                                                                                                                        ('850e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', 'ACC0000000001', 'SAVINGS',   'USD',  5000.0000,  5000.0000, 'ACTIVE'),
                                                                                                                        ('850e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440001', 'ACC0000000002', 'CHECKING', 'USD',  2000.0000,  2000.0000, 'ACTIVE'),
                                                                                                                        ('850e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440002', 'ACC0000000003', 'SAVINGS',   'USD', 10000.0000, 10000.0000, 'ACTIVE'),
                                                                                                                        ('850e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440003', 'ACC0000000004', 'CHECKING', 'USD',  1500.0000,  1500.0000, 'PENDING'),
                                                                                                                        ('850e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440004', 'ACC0000000005', 'BUSINESS', 'USD', 25000.0000, 25000.0000, 'ACTIVE')
    ON DUPLICATE KEY UPDATE uuid = uuid;

INSERT INTO account_balance_history (account_id, previous_balance, new_balance, change_amount, change_reason) VALUES
                                                                                                                  ('850e8400-e29b-41d4-a716-446655440001', 0.0000,  5000.0000,  5000.0000,  'INITIAL_DEPOSIT'),
                                                                                                                  ('850e8400-e29b-41d4-a716-446655440002', 0.0000,  2000.0000,  2000.0000,  'INITIAL_DEPOSIT'),
                                                                                                                  ('850e8400-e29b-41d4-a716-446655440003', 0.0000, 10000.0000, 10000.0000,  'INITIAL_DEPOSIT')
    ON DUPLICATE KEY UPDATE account_id = account_id;

-- =========================
-- TRANSACTION SERVICE SEEDS
-- =========================
INSERT INTO `transaction`
(uuid, idempotency_key, source_account_id, destination_account_id, amount, currency, description, type, status, reference_number, completed_at) VALUES
                                                                                                                                                    ('950e8400-e29b-41d4-a716-446655440001', 'TXN-2025-11-05-001', '850e8400-e29b-41d4-a716-446655440001', '850e8400-e29b-41d4-a716-446655440003',  500.0000, 'USD', 'Payment for services', 'TRANSFER', 'COMPLETED', 'REF0000000001', NOW()),
                                                                                                                                                    ('950e8400-e29b-41d4-a716-446655440002', 'TXN-2025-11-05-002', '850e8400-e29b-41d4-a716-446655440002', '850e8400-e29b-41d4-a716-446655440001',  300.0000, 'USD', 'Refund',               'TRANSFER', 'COMPLETED', 'REF0000000002', NOW()),
                                                                                                                                                    ('950e8400-e29b-41d4-a716-446655440003', 'TXN-2025-11-05-003', '850e8400-e29b-41d4-a716-446655440003', '850e8400-e29b-41d4-a716-446655440005', 1000.0000, 'USD', 'Business payment',    'TRANSFER', 'COMPLETED', 'REF0000000003', NOW())
    ON DUPLICATE KEY UPDATE uuid = uuid;

-- =========================
-- COMPLIANCE / AUDIT SEEDS
-- =========================
INSERT INTO compliance_event (uuid, entity_type, entity_id, event_type, description, severity, metadata) VALUES
                                                                                                             ('a50e8400-e29b-41d4-a716-446655440001', 'CUSTOMER',    '550e8400-e29b-41d4-a716-446655440001', 'KYC_APPROVED',     'Customer KYC documents approved', 'INFO',    '{"approver":"system","documents":["passport"]}'),
                                                                                                             ('a50e8400-e29b-41d4-a716-446655440002', 'ACCOUNT',     '850e8400-e29b-41d4-a716-446655440001', 'ACCOUNT_CREATED',  'New account created successfully', 'INFO',    '{"account_type":"SAVINGS"}'),
                                                                                                             ('a50e8400-e29b-41d4-a716-446655440003', 'TRANSACTION', '950e8400-e29b-41d4-a716-446655440001', 'LARGE_TRANSACTION','Transaction exceeds threshold',     'WARNING', '{"amount":500.00,"threshold":500.00}')
    ON DUPLICATE KEY UPDATE uuid = uuid;

INSERT INTO audit_log (event_id, service_name, entity_type, entity_id, action, user_id, status, ip_address) VALUES
                                                                                                                ('b50e8400-e29b-41d4-a716-446655440001', 'customer-service',    'CUSTOMER',    '550e8400-e29b-41d4-a716-446655440001', 'CREATE_CUSTOMER',    'system', 'SUCCESS', '127.0.0.1'),
                                                                                                                ('b50e8400-e29b-41d4-a716-446655440002', 'account-service',     'ACCOUNT',     '850e8400-e29b-41d4-a716-446655440001', 'CREATE_ACCOUNT',     'system', 'SUCCESS', '127.0.0.1'),
                                                                                                                ('b50e8400-e29b-41d4-a716-446655440003', 'transaction-service', 'TRANSACTION', '950e8400-e29b-41d4-a716-446655440001', 'PROCESS_TRANSACTION','system', 'SUCCESS', '127.0.0.1')
    ON DUPLICATE KEY UPDATE event_id = event_id;

SET FOREIGN_KEY_CHECKS = 1;
