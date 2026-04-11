-- ============================================================
--  LAPS – data.sql
--  Auto-populated seed data for development / demo.
-- ============================================================

-- ── holidays ────────────────────────────────────────────────────────────────────
INSERT INTO holidays ( name, date, location) VALUES
('New Year Day', '2026-01-01', 'Singapore'),
('Chinese New Year', '2026-02-17', 'Singapore'),
('Chinese New Year', '2026-02-18', 'Singapore'),
('Hari Raya Puasa', '2026-03-21', 'Singapore'),
('Good Friday', '2026-04-03', 'Singapore'),
('Labour Day', '2026-05-01', 'Singapore'),
('Hari Raya Haji', '2026-05-27', 'Singapore'),
('Vesak Day', '2026-05-31', 'Singapore'),
('National Day', '2026-08-09', 'Singapore'),
('Deepavali', '2026-11-08', 'Singapore'),
('Christmas Day', '2026-12-25', 'Singapore');

-- ── roles (OnetoMany → users)────────────────────────────────────────────────────────────────────
INSERT INTO roles (id, name, description) VALUES
  ('1', 'ADMIN', 'Admin user'),
  ('2', 'MANAGER', 'Manager user'),
  ('3', 'EMPLOYEE', 'Employee user');
  
-- ── users (ManytoOne → roles, OnetoOne → employees) ────────────────────────────────────────────────────────────
-- Test login accounts
-- admin(external):    test_admin@gmail.com / 12345abc!
-- admin(internal):    admin_internal@company.com / 12345abc!
-- manager:  test_manager@company.com / 12345abc!
-- employee: test_employeeA@company.com / 12345abc!
-- employee: test_employeeB@company.com/ 12345abc!
-- employee: test_CEO@company.com/ 12345abc!
INSERT INTO users (email, role_id , password_hash,enabled,created_at,updated_at) VALUES
  ('test_admin@gmail.com', '1',  '$2a$10$8SzJSmsqrjhbliFpHZsQSOMRFuBP7trEn4VVCzatYs9KGrjxDnvUW', TRUE, '2026-04-01 00:00:00', '2026-04-01 00:00:00'),
    ('admin_internal@company.com', '1',  '$2a$10$8SzJSmsqrjhbliFpHZsQSOMRFuBP7trEn4VVCzatYs9KGrjxDnvUW', TRUE, '2026-04-01 00:00:00', '2026-04-01 00:00:00'),
  ('test_manager@company.com', '2', '$2a$10$8SzJSmsqrjhbliFpHZsQSOMRFuBP7trEn4VVCzatYs9KGrjxDnvUW', TRUE, '2026-04-01 00:00:00', '2026-04-01 00:00:00'),
  ('test_employeeA@company.com', '3', '$2a$10$8SzJSmsqrjhbliFpHZsQSOMRFuBP7trEn4VVCzatYs9KGrjxDnvUW', TRUE, '2026-04-01 00:00:00', '2026-04-01 00:00:00'),
  ('test_employeeB@company.com', '3', '$2a$10$8SzJSmsqrjhbliFpHZsQSOMRFuBP7trEn4VVCzatYs9KGrjxDnvUW', TRUE, '2026-04-01 00:00:00', '2026-04-01 00:00:00');
('test_CEO@company.com', '2', '$2a$10$8SzJSmsqrjhbliFpHZsQSOMRFuBP7trEn4VVCzatYs9KGrjxDnvUW', TRUE, '2026-04-01 00:00:00', '2026-04-01 00:00:00');
-- ── employees (OnetoOne → users, OnetoMany → leave_applications) ────────────────────────

INSERT INTO employees (id , email, first_name ,last_name , contact_number, job_title, team_name, `rank`, manager_id, created_at,updated_at) VALUES
  (1,'test_manager@company.com', 'Mark' ,'Chan' , '81234567', 'Tech Manager', 'Team 1', 'PROFESSIONAL', 4, '2026-04-01 00:00:00','2026-04-01 00:00:00'), --Manager record
  (2, 'test_employeeA@company.com', 'Amy' , 'Lim' , '9777777', 'Associate Software Engineer', 'Team 1', 'NON_EXECUTIVE', 1, '2026-04-01 00:00:00','2026-04-01 00:00:00'), 
  (3, 'test_employeeB@company.com', 'Ben' , 'White' , '95555555', 'Principal Software Engineer', 'Team 1', 'NON_EXECUTIVE', 1, '2026-04-01 00:00:00','2026-04-01 00:00:00'); 
  (4,'test_CEO@company.com', 'Cathy' ,'Goh' , '8000000', 'CEO', 'Team 1', 'PROFESSIONAL', NULL, '2026-04-01 00:00:00','2026-04-01 00:00:00'), --to appprove Manager's leave
--- employees id hardcode

-- ── leave_types (OnetoMany → leave_applications, ManytoOne → leave_records) ───────────────────────────
INSERT INTO leave_types (id, name, description) VALUES
  (1, 'Annual', 'Annual leave'),
  (2, 'Medical', 'Medical leave'),
  (3, 'Compensation', 'Compensation leave');
  
-- ── leave_applications (ManytoOne → employees, ManytoOne → leave_types) ───────────────────────────────────────────────────────────────
  INSERT INTO leave_applications (employee_id  ,  leave_type_id,  from_date ,  to_date, proof_url, reason, work_dissemination, contact_details, manager_comment, status, created_at, updated_at) VALUES
(1, 1, '2026-01-05 00:00:00', '2026-01-06 23:59:59', NULL, 'Settle kid in new school', NULL, NULL, NULL, 'APPROVED', '2025-12-15', '2025-12-20'),
(1, 2, '2026-01-20 00:00:00', '2026-01-20 23:59:59', 'https://med.link/Mark002', 'MC', NULL, NULL, 'Checked MC', 'APPROVED', '2026-01-20', '2026-01-21'),
(1, 1, '2026-02-13 00:00:00', '2026-02-13 23:59:59', NULL, 'Attend school event', 'Ben to meet client on behalf', NULL, NULL, 'APPROVED', '2026-02-01', '2026-02-02'),
(1, 3, '2026-02-27 12:00:00', '2026-02-27 23:59:59', NULL, 'Half day PM', NULL, NULL, NULL, 'APPROVED', '2026-02-20', '2026-02-21'),
(1, 1, '2026-03-12 00:00:00', '2026-03-13 23:59:59', NULL, 'Short getaway', 'Ben to lead the project on that week', NULL, NULL, 'APPROVED', '2026-03-01', '2026-03-02'),
(1, 1, '2026-04-01 00:00:00', '2026-04-01 23:59:59', NULL, 'Attend school event', NULL, NULL, NULL, 'APPROVED', '2026-03-20', '2026-03-22'),
(1, 1, '2026-04-10 00:00:00', '2026-04-10 23:59:59', NULL, 'Extended weekend', NULL, NULL, NULL, 'APPROVED', '2026-04-01', '2026-04-05'),
(1, 3, '2026-04-17 08:00:00', '2026-04-17 12:00:00', NULL, 'Compensation leave', NULL, NULL, NULL, 'APPROVED', '2026-04-10', '2026-04-10'),
(1, 2, '2026-05-20 00:00:00', '2026-05-20 23:59:59', 'https://med.link/Mark009', 'Medical Checkup', NULL, NULL, NULL, 'APPLIED', '2026-04-11', '2026-04-11'),
(1, 1, '2026-06-30 08:00:00', '2026-06-30 23:59:59', NULL, 'Personal', NULL, NULL, NULL, 'UPDATED', '2026-04-10', '2026-04-10'),
(2, 1, '2026-01-12 00:00:00', '2026-01-13 23:59:59', NULL, 'Moving house', NULL, NULL, NULL, 'APPROVED', '2026-01-01', '2026-01-05'),
(2, 1, '2026-01-16 00:00:00', '2026-01-16 23:59:59', NULL, 'Moving house', NULL, NULL, NULL, 'APPROVED', '2026-01-01', '2026-01-05'),
(2, 2, '2026-02-05 00:00:00', '2026-02-06 23:59:59', 'https://med.link/Amy201', 'Fever', NULL, NULL, 'Checked MC', 'APPROVED', '2026-02-05', '2026-02-07'),
(2, 1, '2026-03-02 00:00:00', '2026-03-02 23:59:59', NULL, 'Personal', NULL, NULL, 'Rejected due to project deadline', 'REJECTED', '2026-02-25', '2026-02-26'),
(2, 1, '2026-04-12 00:00:00', '2026-04-12 23:59:59', NULL, 'Holiday', NULL, NULL, NULL, 'APPLIED', '2026-04-01', '2026-04-01'),
(2, 3, '2026-05-29 12:00:00', '2026-05-29 23:59:59', NULL, 'Compensation PM', NULL, NULL, NULL, 'APPLIED', '2026-04-01', '2026-04-01'),
(2, 1, '2026-05-15 00:00:00', '2026-05-15 23:59:59', NULL, 'Holiday', NULL, NULL, NULL, 'APPLIED', '2026-04-01', '2026-04-01'),
(2, 1, '2026-08-11 00:00:00', '2026-08-14 23:59:59', NULL, 'Holiday', NULL, NULL, NULL, 'UPDATED', '2026-04-01', '2026-04-01'),
(2, 3, '2026-09-01 00:00:00', '2026-09-01 23:59:59', NULL, 'Compensation leave', NULL, NULL, NULL, 'APPLIED', '2026-04-11', '2026-04-11'),
(3, 1, '2026-01-02 00:00:00', '2026-01-02 23:59:59', NULL, 'Holiday', NULL, NULL, NULL, 'APPROVED', '2025-12-10', '2025-12-20'),
(3, 2, '2026-01-16 00:00:00', '2026-01-16 23:59:59', 'https://med.link/Ben501', 'MC', NULL, NULL, 'Checked MC', 'APPROVED', '2026-01-16', '2026-01-17'),
(3, 1, '2026-02-16 00:00:00', '2026-02-16 23:59:59', NULL, 'Personal', NULL, NULL, 'Rejected, VIP meeting', 'REJECTED', '2026-01-31', '2026-02-01'),
(3, 1, '2026-05-04 00:00:00', '2026-05-04 23:59:59', NULL, 'Personal', NULL, NULL, NULL, 'APPLIED', '2026-04-01', '2026-04-01'),
(3, 3, '2026-05-05 12:00:00', '2026-05-05 23:59:59', NULL, 'Personal PM', NULL, NULL, NULL, 'APPLIED', '2026-04-01', '2026-04-01'),
(3, 1, '2026-05-15 00:00:00', '2026-05-15 23:59:59', NULL, 'Holiday', NULL, NULL, NULL, 'APPLIED', '2026-04-01', '2026-04-01'),
(3, 2, '2026-03-31 00:00:00', '2026-03-31 23:59:59', 'https://med.link/Ben002', 'MC', NULL, NULL, 'Checked MC', 'APPROVED', '2026-04-01', '2026-04-01'),
(3, 1, '2026-06-20 00:00:00', '2026-06-20 23:59:59', NULL, 'Family matters', NULL, NULL, NULL, 'APPLIED', '2026-04-01', '2026-04-01'),
(3, 1, '2026-07-07 00:00:00', '2026-07-07 23:59:59', NULL, 'Apply wrongly', NULL, NULL, NULL, 'DELETED', '2026-04-01', '2026-04-01'),
(3, 1, '2026-07-08 00:00:00', '2026-07-08 23:59:59', NULL, 'Holiday', NULL, NULL, NULL, 'APPLIED', '2026-04-02', '2026-04-02'),
(3, 1, '2026-08-24 00:00:00', '2026-08-24 23:59:59', NULL, 'Family matters', NULL, NULL, 'Change of plans', 'CANCELLED', '2026-04-03', '2026-04-05'),
(3, 1, '2026-11-30 00:00:00', '2026-11-30 23:59:59', NULL, 'Family matters', NULL, NULL, NULL, 'UPDATED', '2026-04-10', '2026-04-11');

-- ── leave_records ( ManytoOne → leave_types, ManytoOne → employees) ───────────────────────────────────────────────────────────────
INSERT INTO leave_records (id, employee_id, leave_type_id, calendar_year, entitled_days, consumed_days, created_at, updated_at) VALUES
(1, 1, 2026, 18.0, 7.0, '2026-04-01 00:00:00', '2026-04-01 00:00:00'), -- Jan(2), Feb(1), Mar(2), Apr 1(1), Apr 10(1) = 7.0
(1, 2, 2026, 60.0, 1.0, '2026-04-01 00:00:00', '2026-04-01 00:00:00'), -- Jan 20(1) = 1.0
(1, 3, 2026, 5.0, 1.0, '2026-04-01 00:00:00', '2026-04-01 00:00:00'), -- Feb 27 PM(0.5), Apr 17 AM(0.5) = 1.0
(2, 1, 2026, 14.0, 3.0, '2026-04-01 00:00:00', '2026-04-01 00:00:00'), -- Jan 12-13(2), Jan 16(1) = 3.0
(2, 2, 2026, 60.0, 2.0, '2026-04-01 00:00:00', '2026-04-01 00:00:00'), -- Feb 5-6(2) = 2.0
(2, 3, 2026, 9.0, 0.0, '2026-04-01 00:00:00', '2026-04-01 00:00:00'),  -- No approved Comp leave
(3, 1, 2026, 14.0, 1.0, '2026-04-01 00:00:00', '2026-04-01 00:00:00'), -- Jan 2(1) = 1.0
(3, 2, 2026, 60.0, 2.0, '2026-04-01 00:00:00', '2026-04-01 00:00:00'), -- Jan 16(1), Mar 31(1) = 2.0
(3, 3, 2026, 8.5, 0.0, '2026-04-01 00:00:00', '2026-04-01 00:00:00'),  -- No approved Comp leave
(4, 1, 2026, 18.0, 0.0, '2026-04-01 00:00:00', '2026-04-01 00:00:00'), 
(4, 2, 2026, 60.0, 0.0, '2026-04-01 00:00:00', '2026-04-01 00:00:00'), 
(4, 3, 2026, 0.0, 0.0, '2026-04-01 00:00:00', '2026-04-01 00:00:00'); 


