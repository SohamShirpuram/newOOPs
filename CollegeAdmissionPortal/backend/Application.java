/**
 * ============================================================================
 * Class Name: Application
 * OOP Concepts Demonstrated:
 *   1. Encapsulation: Private member variables with public getters and setters
 *   2. Constructors: Overloaded constructors to initialize application objects
 *   3. Methods: Business methods to validate marks and format file text
 * ============================================================================
 * What this class does:
 *   - Encapsulates all data related to a student's admission form.
 *   - Stores academic performance (10th SSC, 12th HSC, CET score).
 *   - Stores course preference and current admission status (Pending / Approved / Rejected).
 *
 * Where the data is saved:
 *   - Persisted in the "data/applications.txt" file by AdmissionSystem.java.
 *
 * How frontend communicates with backend:
 *   - When student submits application.html, Main.java creates an Application object
 *     and saves it.
 *   - When admin views admin-dashboard.html, applications are read from file and
 *     sent to the browser.
 * ============================================================================
 */
public class Application {

    // -------------------------------------------------------------
    // Private instance variables (Encapsulation)
    // -------------------------------------------------------------

    // Unique application reference ID (e.g. APP1001)
    private String applicationId;

    // Email of the student who submitted this application
    private String studentEmail;

    // Full name of the applicant student
    private String studentName;

    // 10th Standard (SSC) percentage
    private double sscMarks;

    // 12th Standard (HSC) percentage
    private double hscMarks;

    // CET entrance exam percentile/score
    private double cetScore;

    // Branch / Course applied for (e.g., Computer Engineering)
    private String courseChoice;

    // Admission category (e.g., General, OBC, SC, ST)
    private String category;

    // Current status: "Pending", "Approved", or "Rejected"
    private String status;

    // Remarks or feedback from the admission committee
    private String remarks;

    // -------------------------------------------------------------
    // Constructor (Parameterized)
    // What it does: Creates a new Application object with all provided values.
    // -------------------------------------------------------------
    public Application(String applicationId, String studentEmail, String studentName,
                       double sscMarks, double hscMarks, double cetScore,
                       String courseChoice, String category, String status, String remarks) {
        this.applicationId = applicationId;
        this.studentEmail = studentEmail;
        this.studentName = studentName;
        this.sscMarks = sscMarks;
        this.hscMarks = hscMarks;
        this.cetScore = cetScore;
        this.courseChoice = courseChoice;
        this.category = category;
        this.status = status;
        this.remarks = remarks;
    }

    // -------------------------------------------------------------
    // Method: toDataString
    // What it does: Serializes this application into a pipe-delimited line.
    // Why it is used: To write into data/applications.txt file.
    // Format: AppID|Email|Name|SSC|HSC|CET|Course|Category|Status|Remarks
    // -------------------------------------------------------------
    public String toDataString() {
        return applicationId + "|" +
               studentEmail + "|" +
               studentName + "|" +
               sscMarks + "|" +
               hscMarks + "|" +
               cetScore + "|" +
               courseChoice + "|" +
               category + "|" +
               status + "|" +
               remarks;
    }

    // -------------------------------------------------------------
    // Static Method: fromDataString
    // What it does: Parses a single line from data/applications.txt into an
    //               Application object.
    // Why it is used: Recreates application objects when loading from disk.
    // -------------------------------------------------------------
    public static Application fromDataString(String dataLine) {
        // Skip null, empty, or comment lines starting with '#'
        if (dataLine == null || dataLine.trim().isEmpty() || dataLine.startsWith("#")) {
            return null;
        }

        // Split by pipe '|'
        String[] parts = dataLine.split("\\|");

        // Validate that we have all 10 fields
        if (parts.length >= 10) {
            String appId = parts[0].trim();
            String email = parts[1].trim();
            String name = parts[2].trim();
            // Parse numerical marks with safe fallback if parsing fails
            double ssc = 0.0;
            double hsc = 0.0;
            double cet = 0.0;
            try {
                ssc = Double.parseDouble(parts[3].trim());
                hsc = Double.parseDouble(parts[4].trim());
                cet = Double.parseDouble(parts[5].trim());
            } catch (NumberFormatException e) {
                // Keep defaults if format error occurs
            }
            String course = parts[6].trim();
            String cat = parts[7].trim();
            String status = parts[8].trim();
            String remarks = parts[9].trim();

            // Construct and return the Application object
            return new Application(appId, email, name, ssc, hsc, cet, course, cat, status, remarks);
        }

        return null;
    }

    // -------------------------------------------------------------
    // Getters and Setters (Encapsulation)
    // -------------------------------------------------------------

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public double getSscMarks() {
        return sscMarks;
    }

    public void setSscMarks(double sscMarks) {
        this.sscMarks = sscMarks;
    }

    public double getHscMarks() {
        return hscMarks;
    }

    public void setHscMarks(double hscMarks) {
        this.hscMarks = hscMarks;
    }

    public double getCetScore() {
        return cetScore;
    }

    public void setCetScore(double cetScore) {
        this.cetScore = cetScore;
    }

    public String getCourseChoice() {
        return courseChoice;
    }

    public void setCourseChoice(String courseChoice) {
        this.courseChoice = courseChoice;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}

