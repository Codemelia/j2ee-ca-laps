-- ============================================================
--  LAPS – data.sql
--  Auto-populated seed data for development / demo.
-- ============================================================

-- ── holidays ────────────────────────────────────────────────────────────────────
INSERT INTO holidays ( name, date, location) VALUES
('New Year Day','2026-01-01','Singapore'),
('Chinese New Year','2026-02-17','Singapore'),
('Chinese New Year','2026-02-18','Singapore'),
('Hari Raya Puasa','2026-03-21','Singapore'),
('Good Friday','2026-04-03','Singapore'),
('Labour Day','2026-05-01','Singapore'),
('Hari Raya Haji','2026-05-27','Singapore'),
('Vesak Day','2026-05-31','Singapore'),
('National Day','2026-08-09','Singapore'),
('Deepavali','2026-11-08','Singapore'),
('Christmas Day','2026-12-25','Singapore');



-- ── roles (OnetoMany → users)────────────────────────────────────────────────────────────────────
INSERT INTO roles (id, name, description) VALUES
  ('1', 'Admin',    'Admin user'),
  ('2', 'Manager' , 'Manager user'),
  ('3', 'Employee' , 'Employee user');
  
  -- ── users (ManytoOne → roles, OnetoOne → employees) ────────────────────────────────────────────────────────────
  INSERT INTO roles (email, role_id , password_hash,enabled,created_at,updated_at) VALUES
  ('test_Admin@gmail.com', '1',  '12345abc',TRUE,'2026-04-01 00:00:00','2026-04-01 00:00:00'),
  ('test_Manager@gmail.com', '2','12345abc',TRUE,'2026-04-01 00:00:00','2026-04-01 00:00:00'),
  ('test_EmployeeA@gmail.com', '3', '12345abc',TRUE,'2026-04-01 00:00:00','2026-04-01 00:00:00'),
  ('test_EmployeeB@gmail.com', '3', '12345abc',TRUE,'2026-04-01 00:00:00','2026-04-01 00:00:00');
  
  -- ── employees (OnetoOne → users, OnetoMany → leave_applications) ────────────────────────
  
  INSERT INTO employees ( id , email, first_name ,last_name , contact_number,rank, manager_id, created_at,updated_at) VALUES
  (1,'test_Manager@gmail.com', 'Mark' ,'Chan' , '81234567', 'Professional', NULL, '2026-04-01 00:00:00','2026-04-01 00:00:00'), --Manager record
  (2, 'test_EmployeeA@gmail.com', 'Amy' , 'Lim' , '9777777', 'Non_executive', 1, '2026-04-01 00:00:00','2026-04-01 00:00:00'), 
  (3, 'test_EmployeeB@gmail.com', 'Ben' , 'White' , '95555555', 'Non_executive',1, '2026-04-01 00:00:00','2026-04-01 00:00:00'); 
--- employees id hardcode
  
  -- ── leave_types (OnetoMany → leave_applications, ManytoOne → leave_records) ───────────────────────────
  INSERT INTO leave_types ( id, name, description) VALUES
  (1, 'Annual',    'Annual leave'),
  (2, 'Medical',  'Medical leave'),
  (3, 'Compensation',  'Compensation leave');
    
 -- ── leave_applications (ManytoOne → employees, ManytoOne → leave_types) ───────────────────────────────────────────────────────────────
   INSERT INTO leave_applications ( id,  employee_id  ,  leave_type_id,  from_date ,  to_date,proof_url,reason,status,created_at,updated_at) VALUES
  (2 , 1,  '2026-04-31 00:00:00' ,'2026-05-01 00:00:00',NULL,NULL,'PENDING','2026-04-01 00:00:00','2026-04-01 00:00:00'),
  (3 , 1,  '2026-04-05 00:00:00' ,'2026-04-05 12:00:00',NULL,NULL,'PENDING','2026-04-01 00:00:00','2026-04-01 00:00:00'), -- half day AL
  (3, 2,  '2026-04-08 00:00:00' ,'2026-04-09 00:00:00','https://proofurl.com/MCBen20260428',NULL,'PENDING','2026-04-01 00:00:00','2026-04-01 00:00:00');
 
 
 -- ── leave_records ( ManytoOne → leave_types, ManytoOne → employees) ───────────────────────────────────────────────────────────────
INSERT INTO leave_records ( id, employee_id, leave_type_id,entitled_days , consumed_days, created_at, updated_at) VALUES
( 1, 1, 18.0 , 0.0, '2026-04-01 00:00:00', '2026-04-01 00:00:00'), 
( 1, 2, 14.0 , 0.0, '2026-04-01 00:00:00', '2026-04-01 00:00:00'), 
( 2, 1, 14.0 , 0.0, '2026-04-01 00:00:00', '2026-04-01 00:00:00'), 
( 2, 2, 14.0 , 0.0, '2026-04-01 00:00:00', '2026-04-01 00:00:00') ,
( 3, 1, 14.0 , 0.0, '2026-04-01 00:00:00', '2026-04-01 00:00:00'), 
( 3, 2, 14.0 , 0.0, '2026-04-01 00:00:00', '2026-04-01 00:00:00') ;



