-- ============================================================
--  LAPS – data.sql
--  Auto-populated seed data for development / demo.
-- ============================================================

-- ── holidays ────────────────────────────────────────────────────────────────────
-- Fall back in case API fails
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
  ('1', 'Admin', 'Admin user'),
  ('2', 'Manager', 'Manager user'),
  ('3', 'Employee', 'Employee user');
  
-- ── users (ManytoOne → roles, OnetoOne → employees) ────────────────────────────────────────────────────────────
-- Test login accounts
-- Internal admin: test_adminA@gmail.com / 12345abc!
-- External admin: test_adminB@gmail.com / 12345abc!
-- manager:  test_manager@gmail.com / 12345abc!
-- employee: test_employeeA@gmail.com / 12345abc!
-- employee: test_employeeB@gmail.com / 12345abc!
INSERT INTO users (email, role_id , password_hash,enabled,created_at,updated_at) VALUES
  ('test_adminA@gmail.com', '1',  '$2a$10$8SzJSmsqrjhbliFpHZsQSOMRFuBP7trEn4VVCzatYs9KGrjxDnvUW', TRUE, '2026-04-01 00:00:00', '2026-04-01 00:00:00'),
  ('test_manager@gmail.com', '2', '$2a$10$8SzJSmsqrjhbliFpHZsQSOMRFuBP7trEn4VVCzatYs9KGrjxDnvUW', TRUE, '2026-04-01 00:00:00', '2026-04-01 00:00:00'),
  ('test_employeeA@gmail.com', '3', '$2a$10$8SzJSmsqrjhbliFpHZsQSOMRFuBP7trEn4VVCzatYs9KGrjxDnvUW', TRUE, '2026-04-01 00:00:00', '2026-04-01 00:00:00'),
  ('test_employeeB@gmail.com', '3', '$2a$10$8SzJSmsqrjhbliFpHZsQSOMRFuBP7trEn4VVCzatYs9KGrjxDnvUW', TRUE, '2026-04-01 00:00:00', '2026-04-01 00:00:00'),
  ('test_adminB@gmail.com', '1',  '$2a$10$8SzJSmsqrjhbliFpHZsQSOMRFuBP7trEn4VVCzatYs9KGrjxDnvUW', TRUE, '2026-04-01 00:00:00', '2026-04-01 00:00:00');

-- ── employees (OnetoOne → users, OnetoMany → leave_applications) ────────────────────────

INSERT INTO employees (id , email, first_name ,last_name , contact_number, job_title, team_name, `rank`, manager_id, created_at,updated_at) VALUES
  (1,'test_manager@gmail.com', 'Mark' ,'Chan' , '81234567', 'Tech Manager', 'Team 1', 'PROFESSIONAL', NULL, '2026-04-01 00:00:00','2026-04-01 00:00:00'), --Manager record
  (2, 'test_employeeA@gmail.com', 'Amy' , 'Lim' , '9777777', 'Associate Software Engineer', 'Team 1', 'NON_EXECUTIVE', 1, '2026-04-01 00:00:00','2026-04-01 00:00:00'), 
  (3, 'test_employeeB@gmail.com', 'Ben' , 'White' , '95555555', 'Principal Software Engineer', 'Team 1', 'NON_EXECUTIVE', 1, '2026-04-01 00:00:00','2026-04-01 00:00:00'),
  (4, 'test_adminA@gmail.com', 'Alice', 'Tan', '90001111', 'System Administrator', 'Admin Team', 'PROFESSIONAL', NULL, '2026-04-01 00:00:00', '2026-04-01 00:00:00');
--- employees id hardcode

-- ── leave_types (OnetoMany → leave_applications, ManytoOne → leave_records) ───────────────────────────
INSERT INTO leave_types (id, leave_type, leave_description) VALUES
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
  (1, 1, 1, '2026-04-30 00:00:00', '2026-05-04 23:59:59', 'NULL', 'Overseas trip', 'Handover to Amy Lim', 'mark.chan@iss.nus.edu.sg', NULL, FALSE, 'APPLIED', '2026-04-01 00:00:00', '2026-04-01 00:00:00'),
  (2, 1, 2, '2026-04-07 00:00:00', '2026-04-07 23:59:59', 'https://proofurl.com/MCBen20260428', 'Clinic appointment', 'Stand-up covered by Ben White', 'mark.chan@iss.nus.edu.sg', NULL, TRUE, 'APPLIED', '2026-04-07 00:00:00', '2026-04-07 00:00:00'),
  (3, 2, 3, '2026-04-08 00:00:00', '2026-04-09 23:59:59', 'NULL', 'Compensation claim for overtime', 'Bug triage reassigned to Mark Chan', 'amy.lim@iss.nus.edu.sg', 'Approved for release deployment support', TRUE, 'APPROVED', '2026-04-01 00:00:00', '2026-04-02 09:30:00');

-- Additional randomized leave application samples (new columns)
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
  (4, 1, 1, '2026-01-05 00:00:00', '2026-01-06 23:59:59', NULL, 'Settle child in new school', 'Backlog shared with Amy Lim', 'mark.chan@iss.nus.edu.sg', NULL, FALSE, 'APPROVED', '2025-12-15 09:00:00', '2025-12-20 12:00:00'),
  (5, 1, 2, '2026-01-20 00:00:00', '2026-01-20 23:59:59', 'https://med.link/mark-002', 'Migraine and consultation', 'Ops handover to Ben White', 'mark.chan@iss.nus.edu.sg', NULL, FALSE, 'APPROVED', '2026-01-20 08:00:00', '2026-01-21 09:00:00'),
  (6, 1, 3, '2026-02-27 13:00:00', '2026-02-27 17:00:00', NULL, 'Comp off after weekend deployment', 'Escalations routed to Amy Lim', 'mark.chan@iss.nus.edu.sg', 'Approved due to overtime log #OT-884', TRUE, 'APPROVED', '2026-02-20 10:00:00', '2026-02-21 10:00:00'),
  (7, 2, 1, '2026-01-12 00:00:00', '2026-01-13 23:59:59', NULL, 'Moving house', 'Feature branch ownership moved to Ben White', 'amy.lim@iss.nus.edu.sg', NULL, FALSE, 'APPROVED', '2026-01-01 10:20:00', '2026-01-05 11:00:00'),
  (8, 2, 2, '2026-02-05 00:00:00', '2026-02-06 23:59:59', 'https://med.link/amy-201', 'Fever and rest', 'Daily stand-up delegated to Mark Chan', 'amy.lim@iss.nus.edu.sg', NULL, FALSE, 'APPROVED', '2026-02-05 07:10:00', '2026-02-07 18:30:00'),
  (9, 2, 1, '2026-03-02 00:00:00', '2026-03-02 23:59:59', NULL, 'Personal errand', 'Customer calls assigned to Mark Chan', 'amy.lim@iss.nus.edu.sg', 'Rejected due to sprint release freeze', FALSE, 'REJECTED', '2026-02-25 15:00:00', '2026-02-26 09:00:00'),
  (10, 2, 3, '2026-05-29 13:00:00', '2026-05-29 17:00:00', NULL, 'Compensation leave PM', 'Pager duty switched with Ben White', 'amy.lim@iss.nus.edu.sg', NULL, TRUE, 'APPLIED', '2026-04-01 09:30:00', '2026-04-01 09:30:00'),
  (11, 2, 1, '2026-05-11 00:00:00', '2026-05-14 23:59:59', NULL, 'Family holiday', 'QA support passed to Team 1 roster', 'amy.lim@iss.nus.edu.sg', NULL, FALSE, 'UPDATED', '2026-04-01 16:30:00', '2026-04-15 12:00:00'),
  (12, 3, 1, '2025-12-30 00:00:00', '2025-12-30 23:59:59', NULL, 'New year leave', 'Code review queue assigned to Amy Lim', 'ben.white@iss.nus.edu.sg', NULL, FALSE, 'APPROVED', '2025-12-10 12:00:00', '2025-12-20 14:00:00'),
  (13, 3, 2, '2026-01-05 00:00:00', '2026-01-05 23:59:59', 'https://med.link/ben-501', 'Flu symptoms', 'Incident monitoring handled by Mark Chan', 'ben.white@iss.nus.edu.sg', NULL, FALSE, 'APPROVED', '2026-01-07 08:15:00', '2026-01-10 08:15:00'),
  (14, 3, 1, '2026-01-16 00:00:00', '2026-01-16 23:59:59', NULL, 'Personal appointment', 'Support mailbox shared with Amy Lim', 'ben.white@iss.nus.edu.sg', 'Rejected due to overlapping team leave', FALSE, 'REJECTED', '2026-01-15 09:00:00', '2026-01-16 09:00:00'),
  (15, 3, 3, '2026-02-16 13:00:00', '2026-02-16 17:00:00', NULL, 'Compensation leave PM', 'Production checks delegated to Mark Chan', 'ben.white@iss.nus.edu.sg', NULL, TRUE, 'APPLIED', '2026-01-30 08:45:00', '2026-01-30 08:45:00'),
  (16, 3, 1, '2026-03-20 00:00:00', '2026-03-20 23:59:59', NULL, 'Family matters', 'Ticket triage reassigned to Team 1 backup', 'ben.white@iss.nus.edu.sg', NULL, FALSE, 'CANCELLED', '2026-02-03 13:30:00', '2026-02-05 10:00:00'),
  (17, 3, 1, '2026-04-22 00:00:00', '2026-04-22 23:59:59', NULL, 'Parent-teacher meeting', 'Customer calls assigned to Mark Chan', 'ben.white@iss.nus.edu.sg', NULL, FALSE, 'APPROVED', '2026-02-10 09:30:00', '2026-02-15 18:20:00'),
  (18, 3, 1, '2026-05-04 00:00:00', '2026-05-04 23:59:59', NULL, 'Parent-teacher meeting', 'Daily deployment checks covered by Amy Lim', 'ben.white@iss.nus.edu.sg', NULL, FALSE, 'APPROVED', '2026-02-10 09:30:00', '2026-02-15 09:30:00'),
  (19, 3, 1, '2026-05-06 00:00:00', '2026-05-06 23:59:59', NULL, 'Family matters', 'Customer calls assigned to Mark Chan', 'ben.white@iss.nus.edu.sg', NULL, FALSE, 'APPROVED', '2026-02-10 09:30:00', '2026-02-19 09:30:00'),
  (20, 3, 1, '2026-05-08 00:00:00', '2026-05-08 23:59:59', NULL, 'Personal appointment', 'Daily deployment checks covered by Amy Lim', 'ben.white@iss.nus.edu.sg', NULL, FALSE, 'APPROVED', '2026-04-03 09:30:00', '2026-04-05 09:30:00'),
  (21, 3, 1, '2026-05-11 00:00:00', '2026-05-11 23:59:59', NULL, 'Family matters', 'Customer calls assigned to Mark Chan', 'ben.white@iss.nus.edu.sg', NULL, FALSE, 'APPROVED', '2026-04-09 09:30:00', '2026-04-11 09:30:00'),
  (22, 3, 1, '2026-05-14 00:00:00', '2026-05-14 23:59:59', NULL, 'Personal errand', 'Daily deployment checks covered by Amy Lim', 'ben.white@iss.nus.edu.sg', NULL, FALSE, 'APPROVED', '2026-04-09 09:30:00', '2026-04-11 09:30:00'),
  (23, 3, 1, '2026-05-19 00:00:00', '2026-05-19 23:59:59', NULL, 'Personal errand', 'Backlog shared with Amy Lim', 'ben.white@iss.nus.edu.sg', NULL, FALSE, 'APPROVED', '2026-04-09 09:30:00', '2026-04-11 09:30:00'),
  (24, 3, 1, '2026-05-25 00:00:00', '2026-05-25 23:59:59', NULL, 'Family holiday', 'Daily stand-up delegated to Mark Chan', 'ben.white@iss.nus.edu.sg', NULL, FALSE, 'APPROVED', '2026-04-10 09:30:00', '2026-04-11 13:30:00'),
  (25, 3, 1, '2026-05-29 00:00:00', '2026-05-29 23:59:59', NULL, 'Personal errand', 'Production checks delegated to Mark Chan', 'ben.white@iss.nus.edu.sg', NULL, FALSE, 'UPDATED', '2026-04-09 09:30:00', '2026-04-16 09:30:00'),
  (26, 3, 1, '2026-06-02 00:00:00', '2026-06-02 23:59:59', NULL, 'Personal errand', 'QA support passed to Team 1 roster', 'ben.white@iss.nus.edu.sg', NULL, FALSE, 'UPDATED', '2026-04-09 09:30:00', '2026-04-16 09:30:00'),
  (27, 3, 1, '2026-06-05 00:00:00', '2026-06-05 23:59:59', NULL, 'Personal errand', 'Daily deployment checks covered by Amy Lim', 'ben.white@iss.nus.edu.sg', NULL, FALSE, 'UPDATED', '2026-04-09 09:30:00', '2026-04-16 09:30:00'),
  (28, 3, 1, '2026-06-18 00:00:00', '2026-06-18 23:59:59', NULL, 'Parent-teacher meeting', 'Production checks delegated to Mark Chan', 'ben.white@iss.nus.edu.sg', NULL, FALSE, 'APPLIED', '2026-04-09 09:30:00', '2026-04-09 09:30:00'),
  (29, 3, 1, '2026-06-22 00:00:00', '2026-06-22 23:59:59', NULL, 'Moving house', 'Daily deployment checks covered by Amy Lim', 'ben.white@iss.nus.edu.sg', NULL, FALSE, 'UPDATED', '2026-04-11 09:30:00', '2026-04-16 09:30:00'),
  (30, 3, 1, '2026-06-25 00:00:00', '2026-06-25 23:59:59', NULL, 'Moving house', 'Production checks delegated to Mark Chan', 'ben.white@iss.nus.edu.sg', NULL, FALSE, 'APPLIED', '2026-04-11 09:30:00', '2026-04-11 09:30:00'),
  (31, 3, 1, '2026-06-29 00:00:00', '2026-06-29 23:59:59', NULL, 'Personal appointment', 'QA support passed to Team 1 roster', 'ben.white@iss.nus.edu.sg', NULL, FALSE, 'APPLIED', '2026-04-12 09:30:00', '2026-04-12 09:30:00'),
  (32, 2, 1, '2026-07-15 00:00:00', '2026-07-17 23:59:59', NULL, 'Family trip', 'Task handover to Ben White', 'amy.lim@iss.nus.edu.sg', NULL, FALSE, 'APPLIED', '2026-04-17 09:00:00', '2026-04-17 09:00:00'),
  (33, 3, 3, '2026-06-16 00:00:00', '2026-06-16 23:59:59', NULL, 'Personal matters', 'Coverage by Mark Chan', 'ben.white@iss.nus.edu.sg', NULL, FALSE, 'APPLIED', '2026-04-17 09:10:00', '2026-04-17 09:10:00'),
  (34, 2, 2, '2026-04-14 00:00:00', '2026-04-16 23:59:59', 'https://med.link/amy-309', 'Medical rest', 'Stand-up covered by Ben White', 'amy.lim@iss.nus.edu.sg', NULL, FALSE, 'UPDATED', '2026-04-14 08:40:00', '2026-04-14 12:00:00'),
  (35, 3, 3, '2026-05-11 13:00:00', '2026-05-11 17:00:00', NULL, 'Compensation leave PM', 'Pager duty swapped with Amy Lim', 'ben.white@iss.nus.edu.sg', NULL, TRUE, 'APPLIED', '2026-04-18 09:00:00', '2026-04-18 09:00:00');

-- ALTER TABLE leave_applications AUTO_INCREMENT = 36;

-- ── leave_records ( ManytoOne → leave_types, ManytoOne → employees) ───────────────────────────────────────────────────────────────
INSERT INTO leave_records (id, employee_id, leave_type_id, calendar_year, entitled_days, consumed_days, created_at, updated_at) VALUES
  (1, 1, 1, 2026, 18.0, 2.0, '2026-04-01 00:00:00', '2026-04-01 00:00:00'), 
  (2, 2, 1, 2026, 14.0, 2.0, '2026-04-01 00:00:00', '2026-04-01 00:00:00'), 
  (3, 1, 2, 2026, 60.0, 1.0, '2026-04-01 00:00:00', '2026-04-01 00:00:00'), 
  (4, 2, 2, 2026, 60.0, 2.0, '2026-04-01 00:00:00', '2026-04-01 00:00:00'),
  (5, 1, 3, 2026, 9.5, 0.5, '2026-04-01 00:00:00', '2026-04-01 00:00:00'), 
  (6, 2, 3, 2026, 8.0, 2.0, '2026-04-01 00:00:00', '2026-04-01 00:00:00'),
  (7, 3, 1, 2026, 14.0, 9.0, '2026-04-01 00:00:00', '2026-04-01 00:00:00'), 
  (8, 3, 2, 2026, 60.0, 1.0, '2026-04-01 00:00:00', '2026-04-01 00:00:00'),
  (9, 4, 1, 2026, 14.0, 0.0, '2026-04-01 00:00:00', '2026-04-01 00:00:00'),
  (10,4, 2, 2026, 60.0, 0.0, '2026-04-01 00:00:00', '2026-04-01 00:00:00') ,
  (11,4, 3, 2026, 1.0, 0.0, '2026-04-01 00:00:00', '2026-04-01 00:00:00'),
  (12,3, 3, 2026, 8.0, 0.0, '2026-04-01 00:00:00', '2026-04-01 00:00:00') ;
