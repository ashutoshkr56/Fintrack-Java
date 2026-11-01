# FinTrack - Personal Finance Management Web Application

A Java web application built using Jakarta EE Servlets, JSP, MySQL, and Maven.

---

## 📋 Prerequisites

Ensure the following are installed and configured on your system:
- **Java Development Kit (JDK)**: Version 17 or higher
- **Apache Maven**: Version 3.8 or higher
- **MySQL Server**: Version 8.0 or higher (running on port `3306`)
- **Apache Tomcat**: Version 10.1 or higher (Jakarta EE 10 compatible, e.g. installed at `D:\Tomcat`)

---

## ⚙️ Step 1: Database Configuration & Setup

### 1. Database Credentials
FinTrack connects to MySQL using the settings in [`src/main/resources/db.properties`](src/main/resources/db.properties):
```properties
db.url=jdbc:mysql://localhost:3306/fintrack_db
db.user=root
db.password=@mysql987
```
*(If your local MySQL root password differs, update `db.password` in `db.properties` before building).*

### 2. Initialize Database & Tables
If you haven't initialized the database schema and default categories yet, run:

```powershell
mysql -u root -p@mysql987 < database\schema.sql
```

This creates:
- The `fintrack_db` database
- Required tables: `users`, `categories`, `transactions`, and `budgets`
- 10 default global categories (Salary, Freelance, Groceries, Dining Out, Utilities, etc.)

---

## 🔨 Step 2: Build the Application WAR File

Open PowerShell / Terminal in the project root folder (`FinTrack`):

```powershell
mvn clean package
```

Upon completion (`BUILD SUCCESS`), the deployment package is generated at:
```
target/FinTrack.war
```

---

## 🚀 Step 3: Deploy to Apache Tomcat

### Option A: Direct Copy (Recommended)
Copy `FinTrack.war` into your Tomcat `webapps` directory:

```powershell
Copy-Item "target\FinTrack.war" -Destination "D:\Tomcat\webapps\"
```

Tomcat will automatically detect `FinTrack.war` and extract it into `D:\Tomcat\webapps\FinTrack`.

### Option B: Using Tomcat Manager GUI
1. Open your browser and navigate to `http://localhost:8080/manager/html`.
2. Under **WAR file to deploy**, click **Browse**, select `target/FinTrack.war`, and click **Deploy**.

---

## ▶️ Step 4: Start Tomcat (If not already started)

If your Tomcat service is stopped, start it using one of the following methods:

- **Via System Tray**: Double-click the **Tomcat10w** icon in your system tray and click **Start**.
- **Via PowerShell (Admin)**:
  ```powershell
  net start Tomcat10
  ```
- **Via Startup Script**:
  ```powershell
  & "D:\Tomcat\bin\startup.bat"
  ```

---

## 🌐 Step 5: Access the Application

Open your browser and visit:

👉 **[http://localhost:8080/FinTrack/](http://localhost:8080/FinTrack/)**

### Quick Usage Flow:
1. **Register**: Click the **Register** tab to create an account with your name, email, and password (minimum 6 characters).
2. **Login**: Log in with your registered email and password.
3. **Dashboard**: View your total income, total expenses, net balance, and savings rate.
4. **Transactions**: Add, edit, or delete income and expense transactions.
5. **Categories**: View pre-seeded global categories or create custom categories.
6. **Budgets**: Set monthly spending limits for expense categories.
7. **Reports**: View monthly financial breakdowns and summaries.

---

## 🔄 How to Redeploy After Code Changes

Whenever you modify any Java or JSP files:
1. Rebuild the WAR:
   ```powershell
   mvn clean package
   ```
2. Copy the updated WAR:
   ```powershell
   Copy-Item "target\FinTrack.war" -Destination "D:\Tomcat\webapps\" -Force
   ```
3. Refresh your browser at `http://localhost:8080/FinTrack/`.

---

## 🔍 Troubleshooting

| Issue | Cause | Solution |
| :--- | :--- | :--- |
| **HTTP 404 Not Found** | Tomcat hasn't unpacked the WAR or context path is wrong | Verify `FinTrack.war` is in `D:\Tomcat\webapps\` and check `D:\Tomcat\logs\catalina.*.log`. Access with exact path `/FinTrack/`. |
| **HTTP 500: Database operation failed** | MySQL is not reachable or bad credentials | Check MySQL status (`mysqladmin ping`) and verify password in `src/main/resources/db.properties`. |
| **Categories dropdown empty** | Database seed script wasn't executed | Run `mysql -u root -p@mysql987 < database\schema.sql`. |
| **Tomcat port 8080 in use** | Another service is using port 8080 | Change the port in `D:\Tomcat\conf\server.xml` or stop the conflicting application. |
