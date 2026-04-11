-- ============================================================
--  LAPS – schema.sql
-- ============================================================

-- PREVENT ERRORS ON CREATE --
DROP TABLE IF EXISTS leave_records;
DROP TABLE IF EXISTS leave_applications;
DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS leave_types;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS holidays;

-- ── holidays ────────────────────────────────────────────────────────────────────
CREATE TABLE holidays (
	id       BIGINT         AUTO_INCREMENT     PRIMARY KEY,
    name     VARCHAR(255)   NOT NULL,
	date     DATE           NOT NULL,
	location VARCHAR(50)    DEFAULT 'Singapore'
);
-- ── roles (OnetoMany → users)────────────────────────────────────────────────────────────────────
CREATE TABLE roles (
    id           BIGINT       AUTO_INCREMENT      PRIMARY KEY,
    name   	     VARCHAR(10)  NOT NULL            UNIQUE,
    description  VARCHAR(100) 
);

-- ── users (ManytoOne → roles, OnetoOne → employees) ───────────────────────────────────────────────────────────────
CREATE TABLE users (
    email          VARCHAR(256)   NOT NULL                      PRIMARY KEY,
    role_id        BIGINT         NOT NULL,
    password_hash  VARCHAR(255)   NOT NULL,
    enabled        BOOLEAN 		  NOT NULL                      DEFAULT TRUE,
    created_at	        DATETIME	NOT NULL        DEFAULT CURRENT_TIMESTAMP,		              ,
    updated_at          DATETIME    NOT NULL        DEFAULT CURRENT_TIMESTAMP	ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_roles FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- ── employees (OnetoOne → users, OnetoMany → leave_applications) ───────────────────────────────────────────────────────────────
CREATE TABLE employees (
    id             BIGINT          AUTO_INCREMENT           PRIMARY KEY, 
    email          VARCHAR(256)    NOT NULL                 UNIQUE,
    first_name     VARCHAR(50)     NOT NULL ,
    last_name      VARCHAR(50)     NOT NULL,
    contact_number VARCHAR(15)     NOT NULL,
    job_title      VARCHAR(50)     NOT NULL,
    team_name      VARCHAR(50)     NOT NULL,
    `rank`   	   ENUM('NON_EXECUTIVE', 'PROFESSIONAL')    NOT NULL       DEFAULT 'NON_EXECUTIVE', 
    manager_id     BIGINT,
    created_at	        DATETIME	NOT NULL        DEFAULT CURRENT_TIMESTAMP,		              ,
    updated_at          DATETIME    NOT NULL        DEFAULT CURRENT_TIMESTAMP	ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_users FOREIGN KEY (email) REFERENCES users(email)
);

-- ── leave_types (OnetoMany → leave_applications, ManytoOne → leave_records) ───────────────────────────────────────────────────────────────
CREATE TABLE leave_types (
    id          BIGINT          AUTO_INCREMENT      PRIMARY KEY, 
    name        VARCHAR(20)     NOT NULL,
    description VARCHAR(100)    NOT NULL
);

-- ── leave_applications (ManytoOne → employees, ManytoOne → leave_types) ───────────────────────────────────────────────────────────────
CREATE TABLE leave_applications (
    id                  BIGINT      AUTO_INCREMENT  PRIMARY KEY, 
    employee_id         BIGINT      NOT NULL,
    leave_type_id       BIGINT      NOT NULL,
    from_date           DATETIME    NOT NULL,
    to_date             DATETIME    NOT NULL,
    proof               VARCHAR(1048),
    reason              VARCHAR(100),
    work_dissemination  VARCHAR(255),
    contact_details     VARCHAR(100),
    manager_comment     VARCHAR(100),
    is_half_day         BOOLEAN     NOT NULL        DEFAULT FALSE,
    status              ENUM('DRAFT', 'APPLIED', 'UPDATED', 'DELETED', 'CANCELLED', 'APPROVED', 'REJECTED')   NOT NULL,
    created_at	        DATETIME	NOT NULL        DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME    NOT NULL        DEFAULT CURRENT_TIMESTAMP	ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_leave_applications_employees      FOREIGN KEY (employee_id)   REFERENCES employees(id),
    CONSTRAINT fk_leave_applications_types          FOREIGN KEY (leave_type_id) REFERENCES leave_types(id)
);

-- ── leave_records ( ManytoOne → leave_types, ManytoOne → employees) ───────────────────────────────────────────────────────────────
CREATE TABLE leave_records (
    id              BIGINT          AUTO_INCREMENT  PRIMARY KEY,
    employee_id     BIGINT          NOT NULL,
    leave_type_id   BIGINT          NOT NULL,
    calendar_year   INT             NOT NULL,
    entitled_days   DECIMAL (4, 2)  NOT NULL        DEFAULT 0.0, -- Double, user earn/use 0.5 day of leave
    consumed_days   DECIMAL (4, 2)  NOT NULL        DEFAULT 0.0,  -- Double, user earn/use 0.5 day of leave
    created_at	        DATETIME	NOT NULL        DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME    NOT NULL        DEFAULT CURRENT_TIMESTAMP	ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_leave_records_employees FOREIGN KEY (employee_id) REFERENCES employees(id),
    CONSTRAINT fk_leave_records_types FOREIGN KEY (leave_type_id) REFERENCES leave_types(id)
);
