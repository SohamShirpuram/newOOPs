# College Admission Portal (OOPS Project)

A basic, clean, and complete **College Admission Portal** modeled after the **CET Cell Centralized Admission Process (CAP)** for undergraduate admissions.

Built strictly using:
* **Frontend:** HTML5 + CSS3 (No React, No Angular, No UI libraries)
* **Backend:** Core Java (Standard Library only — No Spring Boot, No external JARs)
* **Data Storage:** Notepad / Plain text files (`.txt`) using `BufferedReader` and `BufferedWriter`

---

## 1. Project Folder Structure

```
CollegeAdmissionPortal/
│
├── run.bat                     # Double-click runner script for Windows
├── README.md                   # Project documentation & viva guide
│
├── frontend/                   # All user interface files (HTML + CSS)
│   ├── index.html              # Home page with portal overview & admission steps
│   ├── register.html           # Student registration form
│   ├── login.html              # Student login form
│   ├── application.html        # Admission application form (SSC, HSC, CET marks)
│   ├── status.html             # Application tracking & verification status
│   ├── admin-login.html        # Admission officer / Admin login
│   ├── admin-dashboard.html    # Admin panel to view, approve, or reject applications
│   └── style.css               # Professional government/CET portal styling
│
├── backend/                    # Core Java classes demonstrating OOP
│   ├── User.java               # Abstract superclass (Encapsulation, Inheritance, Abstraction)
│   ├── Student.java            # Subclass representing a student (Inheritance, Polymorphism)
│   ├── Admin.java              # Subclass representing an admin (Inheritance, Polymorphism)
│   ├── Application.java        # Model class encapsulating application details
│   ├── AdmissionSystem.java    # Business logic, ArrayLists, and .txt File I/O
│   └── Main.java               # Core Java built-in HttpServer connecting frontend & backend
│
└── data/                       # Plain text database files
    ├── students.txt            # Registered student accounts
    ├── applications.txt        # Submitted admission application records
    └── admin.txt               # Administrator credentials
```

---

## 2. Explanation of What Each File Does

### Backend (Java)
1. **`User.java`**:
   - An `abstract` parent class.
   - Defines common fields (`id`, `name`, `email`, `password`, `phoneNumber`, `role`).
   - Private fields with public getters and setters demonstrate **Encapsulation**.
   - Declares the abstract method `public abstract String getRoleDescription();` which demonstrates **Abstraction** and allows **Polymorphism**.
2. **`Student.java`**:
   - Extends `User` (`public class Student extends User`), demonstrating **Inheritance**.
   - Calls `super(...)` in constructor.
   - Overrides `getRoleDescription()` returning `"Student Applicant eligible to apply for Degree College Admissions."`, demonstrating **Polymorphism (Method Overriding)**.
   - Converts objects to/from text file strings (`toDataString()` and `fromDataString()`).
3. **`Admin.java`**:
   - Extends `User`, demonstrating **Inheritance**.
   - Overrides `getRoleDescription()` returning `"System Administrator with full authority to review, approve, or reject admission applications."`.
4. **`Application.java`**:
   - Represents an admission application with fields: `applicationId`, `studentEmail`, `studentName`, `sscMarks`, `hscMarks`, `cetScore`, `courseChoice`, `category`, `status`, and `remarks`.
   - Uses private fields with getters and setters (**Encapsulation**).
5. **`AdmissionSystem.java`**:
   - The central business controller.
   - Stores data in in-memory collections: `ArrayList<Student>`, `ArrayList<Admin>`, and `ArrayList<Application>`.
   - Reads from and writes to `.txt` files using Java file handling (`FileReader`, `BufferedReader`, `FileWriter`, `BufferedWriter`).
   - Methods include `registerStudent()`, `authenticateStudent()`, `authenticateAdmin()`, `submitApplication()`, and `updateApplicationStatus()`.
6. **`Main.java`**:
   - The execution entry point (`public static void main(String[] args)`).
   - Starts Java's built-in web server `HttpServer` (part of standard JDK since Java 6) on port `8080`.
   - Serves HTML and CSS files to the browser.
   - Receives form data (`POST` requests) and queries (`GET` requests), interacts with `AdmissionSystem`, and returns responses.

### Frontend (HTML & CSS)
1. **`style.css`**: Professional CET-style theme with a navy blue header (`#0d3b66`), golden accents (`#f4a261`), cards, responsive grids, and color-coded status badges (Pending, Approved, Rejected).
2. **`index.html`**: Welcome portal with navigation links, announcements, and quick access cards.
3. **`register.html`**: Student account creation form.
4. **`login.html`**: Student authentication form.
5. **`application.html`**: Form to submit 10th SSC, 12th HSC, CET percentile, course choice, and reservation category.
6. **`status.html`**: Live status tracker displaying application summary, committee remarks, and colored approval badge.
7. **`admin-login.html`**: Official login for admission committee members.
8. **`admin-dashboard.html`**: Admin panel showing count summary cards, full table of applications, and "Approve" / "Reject" buttons.

### Data Storage (.txt Files)
* **`data/students.txt`**: Pipe-delimited records: `StudentID|FullName|Email|Password|PhoneNumber`
* **`data/applications.txt`**: Records: `ApplicationID|StudentEmail|StudentName|SSCMarks|HSCMarks|CETScore|CourseChoice|Category|Status|Remarks`
* **`data/admin.txt`**: Records: `AdminID|FullName|Email|Password|PhoneNumber`

---

## 3. How the HTML Frontend Connects to the Java Backend

1. **Local Server**:
   When you run `java Main`, Java's built-in `HttpServer` begins listening at `http://localhost:8080/`.
2. **Serving Web Pages**:
   When you open `http://localhost:8080/` in Google Chrome or Microsoft Edge, `Main.java` reads `frontend/index.html` and sends the HTML and CSS bytes to the browser.
3. **Sending Form Data**:
   When a user clicks "Register" or "Submit Application", JavaScript's standard `fetch()` sends the input values to the Java server via an HTTP POST request (e.g., to `/api/register` or `/api/apply`).
4. **Processing & File Saving**:
   `Main.java` receives the form data, parses the parameters, creates the appropriate Java object (`new Student(...)` or `new Application(...)`), adds it to the `ArrayList`, and writes the record into the respective `.txt` file using `BufferedWriter`.
5. **Updating the Browser**:
   `Main.java` sends back a JSON response (e.g. `{"success": true, "message": "Registered successfully"}`). The frontend reads this response and updates the page or redirects the user without needing a full page reload.

---

## 4. OOPS Concepts Viva Cheat Sheet

| OOP Concept | Where it is Used in Code | Explanation for Viva |
| :--- | :--- | :--- |
| **Classes & Objects** | All files | `Student`, `Admin`, and `Application` are blueprints (classes). When `new Student(...)` is executed, an instance (object) is created in memory. |
| **Encapsulation** | `User.java`, `Application.java` | Variables are declared `private` to prevent unauthorized direct modification from outside classes. They can only be read or modified through public `getter` and `setter` methods. |
| **Inheritance** | `Student extends User`, `Admin extends User` | `User` is the base class containing common fields (`id`, `name`, `email`, `password`). Both `Student` and `Admin` inherit these fields using `extends`, eliminating code duplication. |
| **Constructor Chaining (`super`)** | `Student.java`, `Admin.java` | Child constructors call `super(id, name, email, password, phone, role)` to invoke the parent `User` constructor. |
| **Abstraction** | `User.java` | `User` is declared `abstract`. It cannot be instantiated directly and forces subclasses to implement required methods like `getRoleDescription()`. |
| **Polymorphism (Method Overriding)** | `getRoleDescription()` in `Student` and `Admin` | Both `Student` and `Admin` override `getRoleDescription()` to provide their own distinct role descriptions. The exact method called is decided at runtime based on the object type. |
| **Collections (`ArrayList`)** | `AdmissionSystem.java` | `ArrayList<Student>` and `ArrayList<Application>` are used to dynamically store and manage objects in memory during program execution. |
| **File Handling (I/O)** | `AdmissionSystem.java` | Uses `BufferedReader` and `FileReader` to read text files line by line, and `BufferedWriter` and `FileWriter` to write updated records to `.txt` files. |

---

## 5. How to Compile and Run on Windows

### Option 1: One-Click Runner (Easiest)
1. Open the `CollegeAdmissionPortal` folder.
2. Double-click the file named **`run.bat`**.
3. It will compile the Java backend, start the server, and automatically launch your browser to `http://localhost:8080/`.

---

### Option 2: Manual Command Prompt / PowerShell
1. Open **Command Prompt** (`cmd`) or **PowerShell**.
2. Navigate to the `backend` folder:
   ```cmd
   cd "D:\Projects\oOPS NEW\CollegeAdmissionPortal\backend"
   ```
3. Compile all Java files:
   ```cmd
   javac *.java
   ```
4. Run the server:
   ```cmd
   java Main
   ```
5. Open your web browser (Chrome, Edge, Firefox) and navigate to:
   ```
   http://localhost:8080/
   ```

To stop the server at any time, press **`Ctrl + C`** in the terminal window.

---

## 6. Pre-configured Demo Accounts

### Student Account:
* **Email:** `rahul@gmail.com`
* **Password:** `rahul123`
*(Or click "New Registration" to create your own account)*

### Admin Officer Account:
* **Email:** `admin@cet.gov.in`
* **Password:** `admin123`
*(Pre-configured in `data/admin.txt`)*

