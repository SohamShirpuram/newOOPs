import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * ============================================================================
 * Class Name: AdmissionSystem
 * OOP Concepts Demonstrated:
 *   1. Encapsulation: Stores and manages private ArrayList collections
 *   2. File Handling: Uses FileReader, BufferedReader, FileWriter, BufferedWriter
 *   3. Collections Framework: ArrayList<Student>, ArrayList<Admin>, ArrayList<Application>
 *   4. Separation of Concerns: Separates business logic from network/HTTP server logic
 * ============================================================================
 * What this class does:
 *   - Acts as the central controller and database manager for the portal.
 *   - Loads data from .txt files into memory (ArrayLists) on system startup.
 *   - Handles Student Registration, Login verification, Application submission,
 *     and Admin approval/rejection operations.
 *   - Writes updated records back into the respective .txt files.
 *
 * Where the data is saved:
 *   - data/students.txt     -> Registered student credentials
 *   - data/admin.txt        -> Admin officer credentials
 *   - data/applications.txt -> All submitted admission application forms
 *
 * How frontend communicates with backend:
 *   - Main.java receives HTTP form submissions from HTML frontend, calls methods
 *     in this class, and returns results to the web browser.
 * ============================================================================
 */
public class AdmissionSystem {

    // -------------------------------------------------------------
    // Private instance collections (Encapsulation)
    // -------------------------------------------------------------

    // Dynamic list holding all registered Student objects in memory
    private ArrayList<Student> studentList;

    // Dynamic list holding all Admin objects in memory
    private ArrayList<Admin> adminList;

    // Dynamic list holding all submitted Application objects in memory
    private ArrayList<Application> applicationList;

    // File paths pointing to our text files
    private String studentsFilePath;
    private String adminFilePath;
    private String applicationsFilePath;

    // -------------------------------------------------------------
    // Constructor
    // What it does: Initializes ArrayLists and loads saved records from disk.
    // -------------------------------------------------------------
    public AdmissionSystem() {
        // Initialize the in-memory ArrayList collections
        studentList = new ArrayList<>();
        adminList = new ArrayList<>();
        applicationList = new ArrayList<>();

        // Resolve data file paths (works whether run from project root or backend folder)
        resolveFilePaths();

        // Load existing records from .txt files into the ArrayLists
        loadAdminsFromFile();
        loadStudentsFromFile();
        loadApplicationsFromFile();

        // Print confirmation to the console
        System.out.println("[AdmissionSystem] System initialized successfully.");
        System.out.println("[AdmissionSystem] Loaded " + studentList.size() + " students, " +
                           adminList.size() + " admins, " +
                           applicationList.size() + " applications.");
    }

    // -------------------------------------------------------------
    // Method: resolveFilePaths
    // What it does: Detects where the "data" folder is located so the project
    //               runs smoothly whether started from root or backend/ folder.
    // -------------------------------------------------------------
    private void resolveFilePaths() {
        // Check if "data" directory is in current working folder
        File dataDir = new File("data");
        if (dataDir.exists() && dataDir.isDirectory()) {
            studentsFilePath = "data/students.txt";
            adminFilePath = "data/admin.txt";
            applicationsFilePath = "data/applications.txt";
        } else {
            // Otherwise, check parent directory (e.g., if running from backend/)
            studentsFilePath = "../data/students.txt";
            adminFilePath = "../data/admin.txt";
            applicationsFilePath = "../data/applications.txt";
        }
    }

    // =============================================================
    // FILE HANDLING METHODS (READING FROM .TXT FILES)
    // =============================================================

    // -------------------------------------------------------------
    // Method: loadStudentsFromFile
    // What it does: Reads data/students.txt line by line using BufferedReader.
    // Why it is used: Restores student accounts when server restarts.
    // -------------------------------------------------------------
    public void loadStudentsFromFile() {
        // Clear current list to avoid duplicates
        studentList.clear();

        File file = new File(studentsFilePath);
        // If file does not exist yet, nothing to load
        if (!file.exists()) {
            return;
        }

        // Try-with-resources automatically closes the BufferedReader when finished
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            // Read line by line until reaching end of file (null)
            while ((line = reader.readLine()) != null) {
                // Parse line into Student object
                Student student = Student.fromDataString(line);
                if (student != null) {
                    // Add valid student to the ArrayList
                    studentList.add(student);
                }
            }
        } catch (IOException e) {
            System.err.println("[AdmissionSystem] Error reading students file: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------
    // Method: loadAdminsFromFile
    // What it does: Reads data/admin.txt line by line using BufferedReader.
    // Why it is used: Loads administrator accounts.
    // -------------------------------------------------------------
    public void loadAdminsFromFile() {
        adminList.clear();
        File file = new File(adminFilePath);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Admin admin = Admin.fromDataString(line);
                if (admin != null) {
                    adminList.add(admin);
                }
            }
        } catch (IOException e) {
            System.err.println("[AdmissionSystem] Error reading admin file: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------
    // Method: loadApplicationsFromFile
    // What it does: Reads data/applications.txt line by line using BufferedReader.
    // Why it is used: Loads all submitted applications into memory.
    // -------------------------------------------------------------
    public void loadApplicationsFromFile() {
        applicationList.clear();
        File file = new File(applicationsFilePath);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Application app = Application.fromDataString(line);
                if (app != null) {
                    applicationList.add(app);
                }
            }
        } catch (IOException e) {
            System.err.println("[AdmissionSystem] Error reading applications file: " + e.getMessage());
        }
    }

    // =============================================================
    // FILE HANDLING METHODS (WRITING TO .TXT FILES)
    // =============================================================

    // -------------------------------------------------------------
    // Method: saveStudentsToFile
    // What it does: Writes all Student objects from studentList into data/students.txt
    //               using BufferedWriter and FileWriter.
    // Why it is used: Persists new registrations permanently to disk.
    // -------------------------------------------------------------
    public synchronized void saveStudentsToFile() {
        try {
            File file = new File(studentsFilePath);
            // Ensure parent directory exists
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            // Open file for writing (false = overwrite full file with current list)
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
                // Write header comment
                writer.write("# Format: StudentID|FullName|Email|Password|PhoneNumber");
                writer.newLine();

                // Loop through all students in our ArrayList
                for (Student student : studentList) {
                    // Write student's data string to file
                    writer.write(student.toDataString());
                    // Write newline character
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("[AdmissionSystem] Error saving students: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------
    // Method: saveApplicationsToFile
    // What it does: Writes all Application objects from applicationList into
    //               data/applications.txt using BufferedWriter and FileWriter.
    // Why it is used: Persists new applications and status updates (Approve/Reject).
    // -------------------------------------------------------------
    public synchronized void saveApplicationsToFile() {
        try {
            File file = new File(applicationsFilePath);
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
                // Write header comment
                writer.write("# Format: ApplicationID|StudentEmail|StudentName|SSCMarks|HSCMarks|CETScore|CourseChoice|Category|Status|Remarks");
                writer.newLine();

                // Loop through all applications in our ArrayList
                for (Application app : applicationList) {
                    writer.write(app.toDataString());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("[AdmissionSystem] Error saving applications: " + e.getMessage());
        }
    }

    // =============================================================
    // BUSINESS LOGIC METHODS
    // =============================================================

    // -------------------------------------------------------------
    // Method: registerStudent
    // What it does: Checks if email already exists, creates a new Student object,
    //               adds it to the ArrayList, and saves to students.txt.
    // Returns: true if registration is successful, false if email is duplicate.
    // -------------------------------------------------------------
    public synchronized boolean registerStudent(String name, String email, String password, String phone) {
        // Check if student with this email is already registered
        for (Student s : studentList) {
            if (s.getEmail().equalsIgnoreCase(email.trim())) {
                System.out.println("[AdmissionSystem] Registration failed: Email already exists -> " + email);
                return false;
            }
        }

        // Generate a new sequential student ID (e.g. STU101, STU102)
        String newId = "STU" + (100 + studentList.size() + 1);

        // Create a new Student object using the Student constructor
        Student newStudent = new Student(newId, name.trim(), email.trim(), password.trim(), phone.trim());

        // Add the new student to our ArrayList collection
        studentList.add(newStudent);

        // Save the updated list to data/students.txt
        saveStudentsToFile();

        System.out.println("[AdmissionSystem] Student registered successfully: " + newStudent.getName() + " (" + newId + ")");
        return true;
    }

    // -------------------------------------------------------------
    // Method: authenticateStudent
    // What it does: Validates student login credentials (email & password).
    // Returns: The matching Student object if credentials match, else null.
    // -------------------------------------------------------------
    public Student authenticateStudent(String email, String password) {
        if (email == null || password == null) {
            return null;
        }

        // Iterate through all students in our ArrayList
        for (Student s : studentList) {
            // Check if email and password match
            if (s.getEmail().equalsIgnoreCase(email.trim()) && s.getPassword().equals(password.trim())) {
                return s; // Login successful
            }
        }
        return null; // Login failed
    }

    // -------------------------------------------------------------
    // Method: authenticateAdmin
    // What it does: Validates admin login credentials.
    // Returns: The matching Admin object if credentials match, else null.
    // -------------------------------------------------------------
    public Admin authenticateAdmin(String email, String password) {
        if (email == null || password == null) {
            return null;
        }

        // Iterate through all admins in our ArrayList
        for (Admin a : adminList) {
            if (a.getEmail().equalsIgnoreCase(email.trim()) && a.getPassword().equals(password.trim())) {
                return a; // Admin login successful
            }
        }
        return null; // Admin login failed
    }

    // -------------------------------------------------------------
    // Method: submitApplication
    // What it does: Creates a new admission Application with initial status "Pending",
    //               adds it to the ArrayList, and saves to data/applications.txt.
    // Returns: The created Application object.
    // -------------------------------------------------------------
    public synchronized Application submitApplication(String studentEmail, String studentName,
                                                     double sscMarks, double hscMarks, double cetScore,
                                                     String courseChoice, String category) {
        // If student already submitted an application, update existing application
        for (Application existing : applicationList) {
            if (existing.getStudentEmail().equalsIgnoreCase(studentEmail.trim())) {
                existing.setStudentName(studentName);
                existing.setSscMarks(sscMarks);
                existing.setHscMarks(hscMarks);
                existing.setCetScore(cetScore);
                existing.setCourseChoice(courseChoice);
                existing.setCategory(category);
                existing.setStatus("Pending");
                existing.setRemarks("Application updated. Verification in progress.");
                saveApplicationsToFile();
                return existing;
            }
        }

        // Generate new sequential application ID (e.g. APP1001)
        String newAppId = "APP" + (1000 + applicationList.size() + 1);

        // Default status is "Pending" and default remark is "Verification Pending"
        Application newApp = new Application(newAppId, studentEmail.trim(), studentName.trim(),
                                             sscMarks, hscMarks, cetScore, courseChoice.trim(),
                                             category.trim(), "Pending", "Documents under scrutiny by CET committee.");

        // Add to in-memory list
        applicationList.add(newApp);

        // Write to text file
        saveApplicationsToFile();

        System.out.println("[AdmissionSystem] New Application submitted: " + newAppId + " for " + studentEmail);
        return newApp;
    }

    // -------------------------------------------------------------
    // Method: getApplicationByEmail
    // What it does: Finds a student's application using their registered email.
    // -------------------------------------------------------------
    public Application getApplicationByEmail(String email) {
        if (email == null) return null;
        for (Application app : applicationList) {
            if (app.getStudentEmail().equalsIgnoreCase(email.trim())) {
                return app;
            }
        }
        return null;
    }

    // -------------------------------------------------------------
    // Method: getApplicationById
    // What it does: Finds an application using its Application ID (e.g. APP1001).
    // -------------------------------------------------------------
    public Application getApplicationById(String appId) {
        if (appId == null) return null;
        for (Application app : applicationList) {
            if (app.getApplicationId().equalsIgnoreCase(appId.trim())) {
                return app;
            }
        }
        return null;
    }

    // -------------------------------------------------------------
    // Method: getAllApplications
    // What it does: Returns the full list of applications for the Admin panel.
    // -------------------------------------------------------------
    public ArrayList<Application> getAllApplications() {
        return applicationList;
    }

    // -------------------------------------------------------------
    // Method: updateApplicationStatus
    // What it does: Allows admin to Approve or Reject an application and add remarks.
    // -------------------------------------------------------------
    public synchronized boolean updateApplicationStatus(String appId, String newStatus, String remarks) {
        Application app = getApplicationById(appId);
        if (app != null) {
            // Update the status ("Approved" or "Rejected")
            app.setStatus(newStatus.trim());
            // Update remarks
            if (remarks != null && !remarks.trim().isEmpty()) {
                app.setRemarks(remarks.trim());
            }
            // Save updated state to applications.txt
            saveApplicationsToFile();
            System.out.println("[AdmissionSystem] Application " + appId + " status changed to: " + newStatus);
            return true;
        }
        return false;
    }

    // Getter for student list
    public ArrayList<Student> getStudentList() {
        return studentList;
    }

    // Getter for admin list
    public ArrayList<Admin> getAdminList() {
        return adminList;
    }
}

