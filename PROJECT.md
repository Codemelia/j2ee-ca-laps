# J2EE CA - LAPS
Group 3's Leave Application Processing System (G3LAPS) for the J2EE course assignment.

## Project Overview
This PROJECT.md file is organized as a practical project structure and workflow guide.

1. Project Information
2. Business Logic and Validation Constraints
3. Repository Structure

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
8. Configuration and localization: Profile-based configuration (application.properties, application-railway properties, application-aws.properties) and messages.properties
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

## 2. Business Logic & Validation Constraints

### 2.1 General Temporal & Integrity Validations 
These checks apply to leave operations (Drafts, Submissions, and Updates) to ensure data consistency. 

1. Chronological Order: The "Start Date" must be before or equal to the "End Date." 
2. Future/Present Only: Leave cannot be applied for dates earlier than today. 
3. Working Days Only: The Start and End dates must fall on a weekday (Monday to Friday). 
4. Self-Identity Check: Users can only update, delete, or cancel leave applications that belong to their own Employee ID. 

### 2.2 Leave-Type Specific Constraints
Different rules apply for is Annual, Medical, or Compensation leave.

1. Medical Leave: "Proof" URL (e.g., Medical Certificate) must be provided. 
2. Annual/Medical Leave: Half-day applications are not allowed. 
3. Compensation Leave: 0.5-day increments are permitted but not fully implemented for now.
4. All Leaves: New leave applications cannot overlap with any existing "Approved" leave applications. 
5. All Leaves: "Reason" must be provided

### 2.3 State-Based Action Rules
The system restricts what a user or manager can do based on the current LeaveStatus:
```
| Action           | Permitted Statuses        | Notes                                                    |
| -----------------| ------------------------- | -------------------------------------------------------- |
| Save             | DRAFT                     | Draft updates overwrite previous drafts                  |
| Submit           | DRAFT, -                  | Only new applications or drafts can be submitted         |
| Update           | APPLIED, UPDATED          | Leave Type cannot be changed during an update            |
| Delete           | APPLIED, UPDATED          | Performs a "Soft-Delete" by setting status to DELETED    |
| Approve/Reject   | APPLIED, UPDATED          | Managers must provide a comment if rejecting             |
| Cancel           | APPROVED                  | Only allowed if the leave start date has not yet passed  |
```

### 2.4 Complex Business Rules
The system uses a sophisticated method to calculate how many days are actually deducted from an employee's balance. 

1. The Chain Logic: 
If a new Annual Leave application is submitted, the system "dials back"/"dials forward" to check if it connects to an existing Approved Annual Leave (even across weekends). 

2. Effective Duration Calculation: 
If ≤ 14 Days: Only working days (excluding weekends and Public Holidays) are deducted. 
If > 14 Days: The rule changes to Calendar Days. All days within the range (including weekends and holidays) are deducted. 

3. Medical Leave: Always deducted as calendar days (Total span). 

### 2.5 Leave Balance & Year-End Logic 

1. Insufficient Balance:
The system prevents approval if the calculated deduction exceeds the user's EntitledDays for that year. 

2. Year Crossover: 
If a leave period spans across December 31st and January 1st, the system splits the deduction logic between the two years. It applies the 14-day rule to the total span. 

3. Just-In-Time (JIT) Initialization:
If a record for the next year doesn't exist yet, the system automatically initializes it based on the employee's entitlement from the previous year; if it is not available, the system falls back to the employee's rank default entitlement.

### 2.6 Reversal & Restoration 
Cancellation Re-calculation: 
When an approved leave is cancelled, the system re-runs the "Effective Duration" logic to ensure the exact number of days previously deducted is restored to the LeaveRecord.

### 2.7 Automated Maintenance of Singapore Public Holiday Data
The HolidayService acts as a bridge between the Singapore Government's Open Data Portal (data.gov.sg) and application’s database. 

---

## 3. Repository Structure
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