# J2EE CA - LAPS
Group 3's Leave Application Processing System (G3LAPS) for the J2EE course assignment.

For project information on LAPS, please view **PROJECT.md**.

## Guide Overview
This README is organized as a practical setup and workflow guide.

1. Packaging and deployment
2. Git workflow 
3. Environment variable configuration
4. Project run commands

---

## 1. Packaging and Deployment
This section covers the packaging and deployment of G3LAPS application.
For proof of execution, please view Group3_Part0_ProjectPrimer.docx.

As requested, credentials are exposed on this README file.

### 1.1 Environment Variables
MYSQL_HOSTNAME=<RDS endpoint>
MYSQL_USERNAME=<username>
MYSQL_PASSWORD=<password>
* Please view application-aws.properties for set-up

### 1.2 Khairul's Account: RDS + EC2 direct
http://34.229.133.41:8080/auth/admin/login
http://34.229.133.41:8080/auth/employee/login

On AWS Learner Lab — RDS Setup
1. Create an Amazon RDS MySQL instance.
   - DB instance identifier: database-1
   - Master username: root
   - Password: 12345abc!
   - Public access: Enabled

2. Configure the RDS security group inbound rule.
    Type: MySQL/Aurora
    Port: 3306
    Source: EC2 instance security group

3. Retrieve the RDS endpoint.
    database-1.cj0isuoooeym.us-east-1.rds.amazonaws.com

Application Packaging (Local Machine)
4. Package the application.
    java -jar gdipsa-j2ee-ca-laps.jar --spring.profiles.active=aws

Deployment on EC2 Instance
5. Establish an SSH connection to the EC2 instance.

6. Set-Up in EC2 CLI Env.: nano ~/.bash_profile

7. Transfer the application JAR to the EC2 instance.
    scp target/gdipsa-j2ee-ca-laps-SNAPSHOT.jar ec2-user@<EC2-public-ip>:/home/ec2-user/

8. Execute the application on the EC2 instance.

9. Configure the EC2 security group inbound rule to allow application access.
    Type: Custom TCP
    Port: 8080
    Source: 0.0.0.0/0

### 1.3 Amelia’s Account: Docker + RDS + ECR + EC2 → containerised deployment
http://ec2-3-85-192-51.compute-1.amazonaws.com:8080/auth/admin/login
http://ec2-3-85-192-51.compute-1.amazonaws.com:8080/auth/employee/login

On AWS Learner Lab — RDS Setup
1. Create RDS (MySQL) instance
   - DB instance identifier: database-1
   - Master username: root
   - Password: password
   - Public access: Yes

2. Configure RDS security group inbound rule
    Type: MySQL/Aurora
    Port: 3306
    Source: EC2 security group (sg-xxxx)

3. Get RDS endpoint
    database-1.cxaskw62mcmk.us-east-1.rds.amazonaws.com

On Local Machine
4. Build Docker image
   docker build -t codemelia/laps:v6 .

5. Test locally with RDS
   docker run -d -p 8080:8080 ^
   -e MYSQL_HOSTNAME=jdbc:mysql://database-1.cxaskw62mcmk.us-east-1.rds.amazonaws.com:3306/lapsdb ^
   -e MYSQL_USERNAME=root ^
   -e MYSQL_PASSWORD=password ^
   codemelia/laps:v6

On AWS Learner Lab / EC2 Instance
6. Create ECR repository lapsrepo

7. Configure AWS credentials
   - Learner Lab → AWS Details
   - Paste into .aws/credentials

8. Verify credentials
    aws configure list
    aws sts get-caller-identity

9. Login to ECR
    aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 590183705020.dkr.ecr.us-east-1.amazonaws.com

10. Configure the EC2 security group inbound rule to allow application access.
    Type: Custom TCP
    Port: 8080
    Source: 0.0.0.0/0

On Local Machine
12. Tag image
    docker tag codemelia/laps:v6 590183705020.dkr.ecr.us-east-1.amazonaws.com/lapsrepo:v1

13. Push image
    docker push 590183705020.dkr.ecr.us-east-1.amazonaws.com/lapsrepo:v1

On EC2 Instance
14. Pull image
    docker pull 590183705020.dkr.ecr.us-east-1.amazonaws.com/lapsrepo:v1

15. Run container
    docker run -d -p 8080:8080 \
    -e MYSQL_HOSTNAME=jdbc:mysql://database-1.cxaskw62mcmk.us-east-1.rds.amazonaws.com:3306/lapsdb \
    -e MYSQL_USERNAME=root \
    -e MYSQL_PASSWORD=password \
    590183705020.dkr.ecr.us-east-1.amazonaws.com/lapsrepo:v1

---

## 2. Git Workflow Guide
Use either command line Git or GitHub Desktop.

### 2.1 Option A: Git Command Line
### Prerequisites

1. Install Git from https://git-scm.com/
2. Verify installation:
	git --version

### Initial setup (first time only)
1. Set your name:
	git config --global user.name "Your Name"

2. Set your email:
	git config --global user.email "you@example.com"

3. Optional check:
	git config --list

### Daily workflow
1. Pull latest changes:
	git pull origin master

2. Check changed files:
	git status

3. Pull latest changes from master branch:
    git pull origin master

4. Stage changes:
	git add .

5. Commit changes:
	git commit -m "Describe your change"

6. Push branch:
	git push --set-upstream origin master

### Useful commands
1. View commit history:
	git log --oneline

2. Discard unstaged changes to a file:
	git restore path/to/file

3. Unstage a file:
	git restore --staged path/to/file

### 2.2 Option B: GitHub Desktop
### Setup

1. Install GitHub Desktop from https://desktop.github.com/
2. Sign in with your GitHub account.
3. Clone this repository.

### Daily workflow
1. Fetch origin.
2. Make your code changes in IDE.
3. Review changed files in GitHub Desktop.
4. Pull latest changes from master branch.
5. Add a commit message and commit.
6. Push origin.

---

## 3. Configuring Environment Variables
Use environment variables for sensitive information (DO NOT store in source code)

### What to configure
Current environment variables in this project

1. MYSQL_URL
2. MYSQL_USERNAME
3. MYSQL_PASSWORD

Replace or expand this list based on what your app needs.

### 3.1 Option A: Windows (System Setting)
1. Open Start menu and search for Edit the system environment variables.
2. Click Environment Variables.
3. Under User variables, click New.
4. Add each variable name and value
    - Name: MYSQL_URL | Value: jdbc:mysql://localhost:3306
    - Name: MYSQL_USERNAME | Value: [your MySQL username]
    - Name: MYSQL_PASSWORD | Value: [your MySQL password]
5. Restart terminal/IDE after saving.

### 3.2 Option B: CLI / Terminal (Temporary Setting)
1. Open your terminal/IDE
2. Find path to this repo in your local machine
    - eg. cd Documents\GDipSA\Classes\J2EE\laps
3. Add each variable name and value:
    - Windows: 
        * SET MYSQL_URL=jdbc:mysql://localhost:3306
        * SET MYSQL_USERNAME=[your MySQL username]
        * SET MYSQL_PASSWORD=[your MySQL password]
    - macOS:
        * EXPORT MYSQL_URL=jdbc:mysql://localhost:3306
        * EXPORT MYSQL_USERNAME=[your MySQL username]
        * EXPORT MYSQL_PASSWORD=[your MySQL password]
4. Restart terminal/IDE after executing.

### 3.3 Verify variables
1. Windows:
    - echo %MYSQL_URL%
    - echo %MYSQL_USERNAME%
    - echo %MYSQL_PASSWORD%

2. macOS:
    - echo $MYSQL_URL
    - echo $MYSQL_USERNAME
    - echo $MYSQL_PASSWORD

### 3.4 Spring Boot usage notes
Spring Boot can read environment variables directly.

Example mapping:

1. APP_PORT -> server.port
2. SPRING_PROFILES_ACTIVE -> spring.profiles.active

You can also keep local-only settings in application-local.properties while keeping secrets out of Git.

---

## 4. Project Run Commands

### 4.1 Run with Maven wrapper:
Run these from the project root (same folder as pom.xml).

2. Windows Command Prompt (cmd):
    mvnw.cmd spring-boot:run

3. macOS/Linux:
    ./mvnw spring-boot:run

To run with a fresh build, add a "clean" argument before spring-boot:run.

### 4.2 Clear corrupted/old artifacts:
2. Windows Command Prompt (cmd):
    mvnw.cmd clean package

3. macOS/Linux:
    ./mvnw clean package

---
