-- V2__seed_data.sql
-- Seed Initial Academic Demonstration Data

INSERT INTO customers (customer_id, name, email, phone, address, created_at) VALUES
(1, 'Rajesh Sharma', 'rajesh.sharma@example.com', '+91 98765 43210', '42 MG Road, Bangalore, Karnataka - 560001', CURRENT_TIMESTAMP),
(2, 'Priya Patel', 'priya.patel@example.com', '+91 98234 56789', '15 Nehru Place, New Delhi - 110019', CURRENT_TIMESTAMP),
(3, 'Arun Kumar', 'arun.kumar@example.com', '+91 97123 45678', '88 Anna Salai, Chennai, Tamil Nadu - 600002', CURRENT_TIMESTAMP),
(4, 'Bank Staff Amit', 'amit.verma@bankease.internal', '+91 99999 11111', 'BankEase HQ Branch, Mumbai', CURRENT_TIMESTAMP),
(5, 'System Administrator', 'admin@bankease.internal', '+91 99999 00000', 'BankEase Corporate Office, Mumbai', CURRENT_TIMESTAMP);

-- Password for all seed users is: Password@123 (or Admin@123 for admin)
-- BCrypt for Password@123: $2a$10$8K1p/a0dL1LXMIgoEDFrwOfk.b0i05c.8xZ3Zp0E8gE8TqGgX2Zz6
INSERT INTO users (user_id, customer_id, username, password_hash, role, enabled, created_at) VALUES
(1, 1, 'rajesh', '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfk.b0i05c.8xZ3Zp0E8gE8TqGgX2Zz6', 'CUSTOMER', TRUE, CURRENT_TIMESTAMP),
(2, 2, 'priya', '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfk.b0i05c.8xZ3Zp0E8gE8TqGgX2Zz6', 'CUSTOMER', TRUE, CURRENT_TIMESTAMP),
(3, 3, 'arun', '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfk.b0i05c.8xZ3Zp0E8gE8TqGgX2Zz6', 'CUSTOMER', TRUE, CURRENT_TIMESTAMP),
(4, 4, 'staff', '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfk.b0i05c.8xZ3Zp0E8gE8TqGgX2Zz6', 'BANK_STAFF', TRUE, CURRENT_TIMESTAMP),
(5, 5, 'admin', '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfk.b0i05c.8xZ3Zp0E8gE8TqGgX2Zz6', 'ADMIN', TRUE, CURRENT_TIMESTAMP);

INSERT INTO accounts (account_id, customer_id, account_no, account_type, balance, status, created_at) VALUES
(1, 1, '100023456781', 'SAVINGS', 154250.00, 'ACTIVE', CURRENT_TIMESTAMP),
(2, 1, '100023456782', 'CURRENT', 48500.00, 'ACTIVE', CURRENT_TIMESTAMP),
(3, 2, '100098765431', 'SAVINGS', 89200.00, 'ACTIVE', CURRENT_TIMESTAMP),
(4, 3, '100055443321', 'SAVINGS', 215000.00, 'ACTIVE', CURRENT_TIMESTAMP);

INSERT INTO beneficiaries (beneficiary_id, customer_id, name, account_no, bank_name, ifsc_code, status, created_at) VALUES
(1, 1, 'Priya Patel', '100098765431', 'BankEase Net Bank', 'BEAS0001001', 'ACTIVE', CURRENT_TIMESTAMP),
(2, 1, 'Arun Kumar', '100055443321', 'BankEase Net Bank', 'BEAS0001001', 'ACTIVE', CURRENT_TIMESTAMP),
(3, 1, 'Suresh Menon', '987654321098', 'State Bank of India', 'SBIN0004512', 'ACTIVE', CURRENT_TIMESTAMP),
(4, 2, 'Rajesh Sharma', '100023456781', 'BankEase Net Bank', 'BEAS0001001', 'ACTIVE', CURRENT_TIMESTAMP);

INSERT INTO transactions (transaction_id, account_id, type, amount, balance_after, status, description, reference_no, created_at) VALUES
(1, 1, 'CREDIT', 100000.00, 100000.00, 'COMPLETED', 'Initial Account Opening Deposit', 'TXN-BEAS-20260110-001', CURRENT_TIMESTAMP),
(2, 1, 'CREDIT', 75000.00, 175000.00, 'COMPLETED', 'Salary Credit - TechCorp Global', 'TXN-BEAS-20260201-002', CURRENT_TIMESTAMP),
(3, 1, 'DEBIT', 15000.00, 160000.00, 'COMPLETED', 'Transfer to Priya Patel', 'TXN-BEAS-20260221-003', CURRENT_TIMESTAMP),
(4, 1, 'DEBIT', 3250.00, 156750.00, 'COMPLETED', 'Electricity Bill Payment - BESCOM', 'TXN-BEAS-20260228-004', CURRENT_TIMESTAMP),
(5, 1, 'DEBIT', 2500.00, 154250.00, 'COMPLETED', 'Internet Fiber Bill - Airtel Broadband', 'TXN-BEAS-20260305-005', CURRENT_TIMESTAMP),
(6, 3, 'CREDIT', 200000.00, 200000.00, 'COMPLETED', 'Consulting Fees Deposit', 'TXN-BEAS-20260301-006', CURRENT_TIMESTAMP);

INSERT INTO transfers (transfer_id, source_account_id, destination_account_no, beneficiary_id, amount, status, reference_no, remarks, created_at) VALUES
(1, 1, '100098765431', 1, 15000.00, 'COMPLETED', 'TRF-BEAS-20260221-001', 'Monthly Maintenance', CURRENT_TIMESTAMP);

INSERT INTO bill_payments (bill_id, account_id, customer_id, category, biller_name, consumer_number, amount, status, reference_no, created_at) VALUES
(1, 1, 1, 'ELECTRICITY', 'BESCOM Bangalore', 'BES-99887766', 3250.00, 'PAID', 'BILL-BEAS-20260228-001', CURRENT_TIMESTAMP),
(2, 1, 1, 'INTERNET', 'Airtel Broadband Fiber', 'AIR-080-456789', 2500.00, 'PAID', 'BILL-BEAS-20260305-002', CURRENT_TIMESTAMP);

INSERT INTO loans (loan_id, customer_id, loan_type, amount, interest_rate, tenure_months, monthly_emi, status, remarks, created_at) VALUES
(1, 1, 'HOME', 3500000.00, 8.50, 180, 34487.50, 'UNDER_REVIEW', 'Home purchase in Bangalore East. KYC documents verified.', CURRENT_TIMESTAMP),
(2, 2, 'PERSONAL', 200000.00, 11.25, 24, 9345.00, 'APPROVED', 'Approved based on credit score.', CURRENT_TIMESTAMP);

INSERT INTO investments (investment_id, customer_id, investment_type, amount, tenure_months, expected_return_rate, status, created_at) VALUES
(1, 1, 'FIXED_DEPOSIT', 100000.00, 12, 7.10, 'ACTIVE', CURRENT_TIMESTAMP),
(2, 1, 'MUTUAL_FUNDS', 25000.00, 36, 12.50, 'ACTIVE', CURRENT_TIMESTAMP);

INSERT INTO notifications (notification_id, customer_id, title, message, type, is_read, created_at) VALUES
(1, 1, 'Welcome to BankEase', 'Welcome to BankEase! Your accounts are active and ready for online services.', 'SYSTEM', TRUE, CURRENT_TIMESTAMP),
(2, 1, 'Transfer Completed', 'Rs. 15,000.00 successfully transferred to Priya Patel. Ref: TRF-BEAS-20260221-001', 'TRANSACTION', TRUE, CURRENT_TIMESTAMP),
(3, 1, 'Loan Application Submitted', 'Your Home Loan application for Rs. 35,00,000.00 is currently under review.', 'LOAN', FALSE, CURRENT_TIMESTAMP),
(4, 1, 'Bill Payment Successful', 'Electricity bill of Rs. 3,250.00 paid to BESCOM Bangalore. Ref: BILL-BEAS-20260228-001', 'TRANSACTION', FALSE, CURRENT_TIMESTAMP);
