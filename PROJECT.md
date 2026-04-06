# J2EE CA - LAPS

Leave Application Processing System (LAPS) for the J2EE course assignment.

The LAPS is a leave processing system that is used by three primary user role groups:
1. Employee [Internal user]
2. Manager [Internal user]
3. Admin [Internal/External user - can be outsourced]

## Project Scope
The system for LAPS is web-based; it allows employees to apply for leave, managers to approve/reject leave requests, admins to manage users/employees and roles. LAPS supports annual/medical/compensation leave.

## Project Role Matrix
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
| Manage Leave Types     | N        | N       |         Y        |         Y        |
| Manage Entitlements    | N        | N       |         Y        |         Y        |
| Manage Roles           | N        | N       |         Y        |         Y        |
```

---

## Project Overview
This PROJECT is organized as a practical project structure and workflow guide.

1. Project Stack
2. Repository Structure

---

## 1. Project Stack
This application uses the following stack components for development.
1. Language/runtime: Java Version 17
2. Framework: Spring Boot Version 4.0.5, Spring MVC, Spring Data JPA, Thymeleaf
3. Database: MySQL Version 8.0.45, Hibernate as ORM
4. Build and Dependency: Maven
5. Frontend: Thymeleaf templates, CSS templates, JavaScript
6. Validation and error handling: Jakarta Validation, GlobalControllerAdvice
7. Authentication and authorization: Spring Security, Role model
8. Testing: JUnit 5, Spring Boot test starters
9. Environment and configuration: application.properties, environment variables

---

## 2. Repository Structure
This application uses a feature-based, scalable structure. 
Please view the respective Controller/Service classes for top-level functions.

### Skeleton Repository:
```
LAPS
├── src/main/java/sg/edu/nus/laps
│   ├── approval [MANAGER: APPROVE TEAM LEAVES]
│   │     ├── ApprovalController.java
│   │     └── ApprovalService.java
│
│   ├── auth [USER AND AUTHENTICATION]
│   │     ├── security [AUTH: SPRING SECURITY]
│   │     |     ├── AuthUserDetails.java
│   │     |     └── AuthUserDetailsService.java
│   │     ├── user [USER DATA]
│   │     |     ├── model
│   │     |     |     ├── Role.java
│   │     |     |     └── User.java
│   │     |     ├── repository
│   │     |     |     ├── RoleRepository.java
│   │     |     |     └── UserRepository.java
│   │     |     └── UserService.java
│   │     └── AuthController.java [AUTH: LOGIN/LOGOUT]
|
│   ├── common [COMMON FILES USED ACROSS PACKAGES]
│   │     ├── exception
│   │     |     └── GlobalExceptionHandler.java
│   │     └── util
│   │           └── SetCreatedUpdated.java
|
│   ├── config [APP CONFIGURATION]
│   │     └── SecurityConfig.java
|
│   ├── employee [ADMIN: EMPLOYEE CRUD]
│   │     ├── model
│   │     |     ├── Employee.java
│   │     |     └── EmployeeRank.java
│   │     ├── repository
│   │     |     └── EmployeeRepository.java
│   │     ├── EmployeeController.java
│   │     └── EmployeeService.java
|
│   ├── leave [LEAVE CRUD]
│   │     ├── model
│   │     |     ├── Holiday.java
│   │     |     ├── LeaveApplication.java
│   │     |     ├── LeaveRecord.java
│   │     |     ├── LeaveStatus.java
│   │     |     └── LeaveType.java
│   │     ├── repository
│   │     |     ├── HolidayRepository.java
│   │     |     ├── LeaveApplicationRepository.java
│   │     |     ├── LeaveRecordRepository.java
│   │     |     └── LeaveTypeRepository.java
│   │     ├── LeaveController.java
│   │     └── LeaveService.java
│
│   ├── me [SERVE USER/EMP-FACING PAGES]
│   │     ├── MeController.java
│   │     └── MeService.java
│
│   └── LapsApplication.java
│
├── src/main/resources
│
│   ├── templates
│   │     ├── approval [BASEPATH: "/requests"]
│   │     │     └── team-leave-requests.html
│   │     │
│   │     ├── auth [BASEPATH: "/auth"]
│   │     │     └── login.html
│   │     │
│   │     ├── employee [BASEPATH: "/employees"]
│   │     │     ├── create-employee-form.html
│   │     │     ├── details.html
│   │     │     └── update-employee-form.html
│   │     │
│   │     ├── error [BASEPATH: "/error"]
│   │     │     └── error.html
│   │     │
│   │     ├── fragment [COMMON SECTIONS ACROSS PAGES]
│   │     |     ├── footer.html
│   │     |     └── header.html
│   │     │
│   │     ├── leave [BASEPATH: "/leaves"]
│   │     │     ├── apply-leave-form.html
│   │     │     ├── details.html
│   │     │     └── update-leave-form.html
│   │     │
│   │     └── me [BASEPATH: {"/", "/me"}]
│   │           ├── dashboard.html
│   │           ├── profile.html
│   │           └── notifications.html
│
│   ├── static
│   │     └── css
│   │           └── style.css
│
│   ├── application.properties
│   ├── data.sql
│   ├── message.properties
│   └── schema.sql
│
└── pom.xml
...
```

---

## 3. Scope and Assumptions

### 3.1 Scope
The system is a web-based LAPS that allows