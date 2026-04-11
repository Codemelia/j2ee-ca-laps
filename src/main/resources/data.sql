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
-- admin:    test_admin@gmail.com / 12345abc!
-- manager:  test_manager@gmail.com / 12345abc!
-- employee: test_employeeA@gmail.com / 12345abc!
-- employee: test_employeeB@gmail.com / 12345abc!
INSERT INTO users (email, role_id , password_hash,enabled,created_at,updated_at) VALUES
  ('test_admin@gmail.com', '1',  '$2a$10$8SzJSmsqrjhbliFpHZsQSOMRFuBP7trEn4VVCzatYs9KGrjxDnvUW', TRUE, '2026-04-01 00:00:00', '2026-04-01 00:00:00'),
  ('test_manager@gmail.com', '2', '$2a$10$8SzJSmsqrjhbliFpHZsQSOMRFuBP7trEn4VVCzatYs9KGrjxDnvUW', TRUE, '2026-04-01 00:00:00', '2026-04-01 00:00:00'),
  ('test_employeeA@gmail.com', '3', '$2a$10$8SzJSmsqrjhbliFpHZsQSOMRFuBP7trEn4VVCzatYs9KGrjxDnvUW', TRUE, '2026-04-01 00:00:00', '2026-04-01 00:00:00'),
  ('test_employeeB@gmail.com', '3', '$2a$10$8SzJSmsqrjhbliFpHZsQSOMRFuBP7trEn4VVCzatYs9KGrjxDnvUW', TRUE, '2026-04-01 00:00:00', '2026-04-01 00:00:00');

-- ── employees (OnetoOne → users, OnetoMany → leave_applications) ────────────────────────

INSERT INTO employees (id , email, first_name ,last_name , contact_number, job_title, team_name, `rank`, manager_id, created_at,updated_at) VALUES
  (1,'test_manager@gmail.com', 'Mark' ,'Chan' , '81234567', 'Tech Manager', 'Team 1', 'PROFESSIONAL', NULL, '2026-04-01 00:00:00','2026-04-01 00:00:00'), --Manager record
  (2, 'test_employeeA@gmail.com', 'Amy' , 'Lim' , '9777777', 'Associate Software Engineer', 'Team 1', 'NON_EXECUTIVE', 1, '2026-04-01 00:00:00','2026-04-01 00:00:00'), 
  (3, 'test_employeeB@gmail.com', 'Ben' , 'White' , '95555555', 'Principal Software Engineer', 'Team 1', 'NON_EXECUTIVE', 1, '2026-04-01 00:00:00','2026-04-01 00:00:00'); 
--- employees id hardcode

-- ── leave_types (OnetoMany → leave_applications, ManytoOne → leave_records) ───────────────────────────
INSERT INTO leave_types (id, leaveType, leaveDescription) VALUES
  (1, 'Annual', 'Annual leave'),
  (2, 'Medical', 'Medical leave'),
  (3, 'Compensation', 'Compensation leave');
  
-- ── leave_applications (ManytoOne → employees, ManytoOne → leave_types) ───────────────────────────────────────────────────────────────
INSERT INTO leave_applications (
  id,
  employee_id,
  leave_type_id,
  from_date,
  to_date,
  proof,
  reason,
  work_dissemination,
  contact_details,
  manager_comment,
  is_half_day,
  status,
  created_at,
  updated_at
) VALUES
  (1, 1, 1, '2026-04-30 00:00:00', '2026-05-01 00:00:00', 'https://proofurl.com/MCBen20260428', 'Family matters', 'Handover to Amy Lim', 'mark.chan@iss.nus.edu.sg', NULL, FALSE, 'APPLIED', '2026-04-01 00:00:00', '2026-04-01 00:00:00'),
  (2, 1, 2, '2026-04-05 00:00:00', '2026-04-05 12:00:00', 'https://proofurl.com/MCBen20260428', 'Clinic appointment', 'Stand-up covered by Ben White', 'mark.chan@iss.nus.edu.sg', NULL, TRUE, 'APPLIED', '2026-04-01 00:00:00', '2026-04-01 00:00:00'),
  (3, 2, 3, '2026-04-08 00:00:00', '2026-04-09 00:00:00', 'https://proofurl.com/MCBen20260428', 'Compensation claim for overtime', 'Bug triage reassigned to Mark Chan', 'amy.lim@iss.nus.edu.sg', 'Approved for release deployment support', TRUE, 'APPROVED', '2026-04-01 00:00:00', '2026-04-02 09:30:00');

-- ── leave_records ( ManytoOne → leave_types, ManytoOne → employees) ───────────────────────────────────────────────────────────────
INSERT INTO leave_records (id, employee_id, leave_type_id, calendar_year, entitled_days, consumed_days, created_at, updated_at) VALUES
  ( 1, 1, 1, 2026, 18.0, 0.0, '2026-04-01 00:00:00', '2026-04-01 00:00:00'), 
  ( 2, 2, 1, 2026, 14.0, 0.0, '2026-04-01 00:00:00', '2026-04-01 00:00:00'), 
  ( 3, 1, 2, 2026, 14.0, 0.0, '2026-04-01 00:00:00', '2026-04-01 00:00:00'), 
  ( 4, 2, 2, 2026, 14.0, 0.0, '2026-04-01 00:00:00', '2026-04-01 00:00:00'),
  ( 5, 1, 3, 2026, 14.0, 0.0, '2026-04-01 00:00:00', '2026-04-01 00:00:00'), 
  ( 6, 2, 3, 2026, 14.0, 0.0, '2026-04-01 00:00:00', '2026-04-01 00:00:00');



