# J2EE CA - LAPS
Group 3's Leave Application Processing System (G3LAPS) for the J2EE course assignment.

## Project Overview
This PROJECT.md file is organized as a practical project structure and workflow guide.

1. Project Information
2. Repository Structure

---

## 1. Project Information

### 1.1 What this project does
The G3LAPS is a leave processing system that is used by three primary user role groups:
1. Employee [Internal user]
2. Manager [Internal user]
3. Admin [Internal/External user - can be outsourced]

### 1.2 Main users and responsibilities
1. Employee: apply leave, view own leave, manage own account.
2. Manager: all employee functions plus approve/reject subordinate leave.
3. Admin: manage employees, users, leave types, entitlements, and roles.

### 1.3 Project role matrix
This is an overview of the functions of each role (Y = Yes, N = No):
```
| Function               | Employee | Manager | Admin (Internal) | Admin (External) |
| ---------------------- | -------- | ------- | ---------------- | ---------------- |
| Login / Logout         | Y        | Y       |         Y        |         Y        |
| Apply Leave            | Y        | Y       |         Y        |         N        |
| View Own Leave         | Y        | Y       |         Y        |         N        |
| Approve Leave          | N        | Y       |         N        |         N        |
| View Subordinate Leave | N        | Y       |         N        |         N        |
| Manage Users/Employees | N        | N       |         Y        |         Y        |
| Manage Entitlements    | N        | N       |         Y        |         Y        |
| Manage Roles           | N        | N       |         Y        |         Y        |
```

### 1.4 Core technology stack
This application uses the following stack components for development.
1. Language and runtime: Java 17
2. Application framework: Spring Boot 4.0.5 with Spring MVC, Spring Data JPA, and Thymeleaf
3. Database and ORM: MySQL with Hibernate/JPA
4. Build and dependency management: Maven (Maven Wrapper included)
5. Frontend layer: Thymeleaf templates, CSS, JavaScript, jQuery + DataTables, Bootstrap 5
6. Validation and exception handling: Jakarta Validation and centralized/separate exception handling
7. Authentication and authorization: Spring Security, Thymeleaf Extras - Spring Security 6, role/authority-based access control, custom authentication provider, and custom invalid/expired session strategies
8. Configuration and localization: Profile-based configuration (application.properties, application-railway.properties, application-aws.properties) and messages.properties
9. Database initialization and dummy data: schema.sql and data.sql for schema setup and test/demo data population
10. Packaging and deployment: Dockerfile with cloud deployment configuration for Railway and AWS EC2/RDS via Learner Lab
11. External API integration: Spring RestTemplate-based integration for holiday data retrieval
12. Testing support: Spring Boot test starters for Web MVC, Security, JPA, Thymeleaf, and Validation

### 1.5 Main feature modules
1. auth: login/logout and user security support
2. leave: leave application lifecycle and leave records
3. approval: manager approval/rejection workflows
4. employee: employee and user administration
5. me: authenticated employee dashboard

---

## 2. Repository Structure
This application uses a feature-based, scalable structure. 

### Skeleton
```
j2ee-ca-laps
├── src/main/java/sg/edu/nus/laps
│   ├── approval [MANAGER: APPROVE/REJECT TEAM LEAVES]
│   │     ├── ApprovalController.java
│   │     └── ApprovalService.java
│
│   ├── auth [USER DATA]
│   │     ├── exception
│   │     |     ├── InvalidPasswordException.java
│   │     |     └── InvalidUserException.java
│   │     │
│   │     ├── model
│   │     |     ├── PasswordDTO.java
│   │     |     ├── Role.java
│   │     |     └── User.java
│   │     │
│   │     ├── repository
│   │     |     ├── RoleRepository.java
│   │     |     └── UserRepository.java
│   │     │
│   │     ├── service
│   │     |     ├── RoleService.java
│   │     |     └── UserService.java
│   │     │
│   │     └── AuthController.java
|
│   ├── common [COMMON FILES USED ACROSS PACKAGES]
│   │     ├── exception
│   │     |     ├── GlobalExceptionHandler.java
│   │     |     └── UnauthorisedUserException.java
│   │     │
│   │     ├── util
│   │     |     └── SetCreatedUpdated.java
│   │     │
│   │     └── GlobalModelAttributes.java
|
│   ├── employee [ADMIN: EMPLOYEE CRUD]
│   │     ├── exception
│   │     |     └── InvalidEmployeeException.java
│   │     │
│   │     ├── model
│   │     |     ├── Employee.java
│   │     |     ├── EmployeeRank.java
│   │     |     └── NewEmployeeRecord.java
│   │     │
│   │     ├── repository
│   │     |     └── EmployeeRepository.java
│   │     │
│   │     ├── EmployeeController.java
│   │     └── EmployeeService.java
|
│   ├── leave [LEAVE CRUD]
│   │     ├── dto
│   │     |     ├── DataGovResponse.java
│   │     |     ├── DataGovResult.java
│   │     |     └── HolidayRecordDto.java
│   │     │
│   │     ├── model
│   │     |     ├── Holiday.java
│   │     |     ├── LeaveApplication.java
│   │     |     ├── LeaveRecord.java
│   │     |     ├── LeaveStatus.java
│   │     |     └── LeaveType.java
│   │     │
│   │     ├── repository
│   │     |     ├── HolidayRepository.java
│   │     |     ├── LeaveApplicationRepository.java
│   │     |     ├── LeaveRecordRepository.java
│   │     |     └── LeaveTypeRepository.java
│   │     │
│   │     ├── service
│   │     |     ├── HolidayService.java
│   │     |     ├── LeaveRecordService.java
│   │     |     ├── LeaveService.java
│   │     |     └── LeaveTypeService.java
│   │     │
│   │     └── LeaveController.java
│
│   ├── me [EMPLOYEE: DASHBOARD]
│   │     ├── MeController.java
│
│   ├── security [SERVE USER/EMP-FACING PAGES]
│   │     ├── custom
│   │     |     ├── LapsExpiredSessionStrategy.java
│   │     |     └── LapsInvalidSessionStrategy.java
│   │     │
│   │     ├── AuthUserDetails.java
│   │     ├── AuthUserDetailsService.java
│   │     ├── SecurityConfig.java
│   │     └── SecurityUtil.java
│
│   └── LapsApplication.java
│
├── src/main/resources
│   ├── static
│   │     ├── css
│   │     ├── images
│   │     ├── js
│   │     │     ├── change-password.js [PUT REQUEST TO AuthController]
│   │     │     ├── employee-form.js [INTERACTIVITY ON EMPLOYEE FORM]
│   │     │     ├── leave-form.js [INTERACTIVITY ON LEAVE FORM]
│   │     │     └── main.js [PASSWORD CHANGE MODAL + DATATABLES]
│
│   ├── templates
│   │     ├── approval ["/manager/team-leaves"]
│   │     │     └── team-leave-list.html
│   │     │
│   │     ├── auth ["/auth/employee/login", "/auth/admin/login"]
│   │     │     └── login.html
│   │     │
│   │     ├── employee ["/admin/employees"]
│   │     │     ├── employee-form.html
│   │     │     └── employee-mgmt.html
│   │     │
│   │     ├── fragments
│   │     │
│   │     ├── leave ["/leaves"]
│   │     │     ├── leave-details.html
│   │     │     ├── leave-form.html
│   │     │     └── leave-list.html
│   │     │
│   │     └── me ["/", "/me"]
│               └── dashboard.html
...
```