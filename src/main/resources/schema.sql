-- ============================================================
--  LAPS – schema.sql
-- ============================================================

-- ── holidays ────────────────────────────────────────────────────────────────────
CREATE TABLE holidays (
	id    BIGINT      AUTO_INCREMENT PRIMARY KEY,
name  VARCHAR(255) NOT NULL,
	date  DATE NOT NULL,
	location VARCHAR(50) DEFAULT 'Singapore'
);
-- ── roles (OnetoMany → users)────────────────────────────────────────────────────────────────────
CREATE TABLE roles (
    id         BIGINT       NOT NULL UNIQUE PRIMARY KEY,
    name   	   VARCHAR(10)  NOT NULL UNIQUE,
    description  VARCHAR(100) 
);

-- ── users (ManytoOne → roles, OnetoOne → employees) ───────────────────────────────────────────────────────────────
CREATE TABLE users (
    email          VARCHAR(256)    NOT NULL UNIQUE PRIMARY KEY,
    role_id        BIGINT         NOT NULL,
    password_hash  VARCHAR(255)   NOT NULL,
    enabled        BOOLEAN 		  NOT NULL DEFAULT TRUE,
    created_at	   DATETIME		  NOT NULL,
    updated_at     DATETIME       NOT NULL,
    CONSTRAINT fk_roles FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- ── employees (OnetoOne → users, OnetoMany → leave_applications) ───────────────────────────────────────────────────────────────
CREATE TABLE employees (
    id          BIGINT      AUTO_INCREMENT PRIMARY KEY, 
    email          VARCHAR(256)    NOT NULL UNIQUE,
    first_name    VARCHAR(50)  NOT NULL ,
    last_name     VARCHAR(50) NOT NULL,
    contact_number VARCHAR(15)NOT NULL,
    rank   			 VARCHAR(20)   NOT NULL , 
    manager_id      BIGINT,
    created_at	   DATETIME		  NOT NULL,
    updated_at     DATETIME       NOT NULL,
     CONSTRAINT fk_users FOREIGN KEY (email) REFERENCES users(email)
);

-- ── leave_types (OnetoMany → leave_applications, ManytoOne → leave_records) ───────────────────────────────────────────────────────────────
CREATE TABLE leave_types (
    id        BIGINT   PRIMARY KEY, 
   name        VARCHAR(20) NOT NULL,
   description VARCHAR(100) NOT NULL
);

-- ── leave_applications (ManytoOne → employees, ManytoOne → leave_types) ───────────────────────────────────────────────────────────────
CREATE TABLE leave_applications (
    id          BIGINT      AUTO_INCREMENT PRIMARY KEY, 
    employee_id          BIGINT   NOT NULL,
    leave_type_id  BIGINT NOT NULL,
    from_date      DATETIME NOT NULL,
    to_date           DATETIME NOT NULL,
    proof_url     VARCHAR(2048) ,
    reason        VARCHAR(100) ,
    status        VARCHAR(20)   NOT NULL DEFAULT 'PENDING',-- Enum as String
    created_at	   DATETIME		  NOT NULL,
    updated_at     DATETIME       NOT NULL,
     CONSTRAINT fk_employees FOREIGN KEY (employee_id) REFERENCES employees(id),
      CONSTRAINT fk_leave_types FOREIGN KEY (leave_type_id) REFERENCES leave_types(id)
);



-- ── leave_records ( ManytoOne → leave_types, ManytoOne → employees) ───────────────────────────────────────────────────────────────
CREATE TABLE leave_records (
    id          BIGINT      AUTO_INCREMENT PRIMARY KEY, 
    employee_id          BIGINT   NOT NULL,
    leave_type_id  BIGINT NOT NULL,
    entitled_days DECIMAL (4, 2) NOT NULL DEFAULT 0.0, -- Double, user earn/use 0.5 day of leave
    consumed_days DECIMAL (4, 2) NOT NULL DEFAULT 0.0,  -- Double, user earn/use 0.5 day of leave
    created_at	   DATETIME		  NOT NULL,
    updated_at     DATETIME       NOT NULL,
     CONSTRAINT fk_employees FOREIGN KEY (employee_id) REFERENCES employees(id),
      CONSTRAINT fk_leave_types FOREIGN KEY (leave_type_id) REFERENCES leave_types(id)
);
