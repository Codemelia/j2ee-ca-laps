# J2EE CA - LAPS
Group 3's Leave Application Processing System (G3LAPS) for the J2EE course assignment.

For detailed planning on LAPS, view PROJECT.md.

## Guide Overview
This README is organized as a practical setup and workflow guide.

1. Git workflow
2. Environment variable configuration
3. Project run commands
4. Packaging and deployment

---

## 1. Git Workflow Guide
Use either command line Git or GitHub Desktop.

### 1.1 Option A: Git Command Line
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

### 1.2 Option B: GitHub Desktop
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

## 2. Configuring Environment Variables
Use environment variables for sensitive information (DO NOT store in source code)

### What to configure
Current environment variables in this project

1. MYSQL_URL
2. MYSQL_USERNAME
3. MYSQL_PASSWORD

Replace or expand this list based on what your app needs.

### 2.1 Option A: Windows (System Setting)
1. Open Start menu and search for Edit the system environment variables.
2. Click Environment Variables.
3. Under User variables, click New.
4. Add each variable name and value
    - Name: MYSQL_URL | Value: jdbc:mysql://localhost:3306
    - Name: MYSQL_USERNAME | Value: [your MySQL username]
    - Name: MYSQL_PASSWORD | Value: [your MySQL password]
5. Restart terminal/IDE after saving.

### 2.2 Option B: CLI / Terminal (Temporary Setting)
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

### 2.3 Verify variables
1. Windows:
    - echo %MYSQL_URL%
    - echo %MYSQL_USERNAME%
    - echo %MYSQL_PASSWORD%

2. macOS:
    - echo $MYSQL_URL
    - echo $MYSQL_USERNAME
    - echo $MYSQL_PASSWORD

### 2.4 Spring Boot usage notes
Spring Boot can read environment variables directly.

Example mapping:

1. APP_PORT -> server.port
2. SPRING_PROFILES_ACTIVE -> spring.profiles.active

You can also keep local-only settings in application-local.properties while keeping secrets out of Git.

---

## 3. Project Run Commands

### 3.1 Run with Maven wrapper:
Run these from the project root (same folder as pom.xml).

2. Windows Command Prompt (cmd):
    mvnw.cmd spring-boot:run

3. macOS/Linux:
    ./mvnw spring-boot:run

To run with a fresh build, add a "clean" argument before spring-boot:run.

### 3.2 Clear corrupted/old artifacts:
2. Windows Command Prompt (cmd):
    mvnw.cmd clean package

3. macOS/Linux:
    ./mvnw clean package

---

## 4. Packaging and Deployment
This section identifies the steps our team took to package and deploy our application
