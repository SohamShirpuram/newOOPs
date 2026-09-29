/**
 * ============================================================================
 * Class Name: Student
 * OOP Concepts Demonstrated:
 *   1. Inheritance: Extends User class (is-a relationship: Student is a User)
 *   2. Super Keyword: Calls parent constructor using super(...)
 *   3. Polymorphism / Method Overriding: Overrides getRoleDescription()
 *   4. Encapsulation: Protects fields and provides clean representation
 * ============================================================================
 * What this class does:
 *   - Represents a student applicant in the admission portal.
 *   - Holds student account information.
 *   - Converts data to and from the text file format (students.txt).
 *
 * Where the data is saved:
 *   - Persisted in the "data/students.txt" file by AdmissionSystem.java.
 *
 * How frontend communicates with backend:
 *   - When the student fills the register form on register.html, the HTML form
 *     sends a POST request to Main.java, which creates a new Student object.
 * ============================================================================
 */
public class Student extends User {

    // -------------------------------------------------------------
    // Constructor
    // What it does: Calls the parent (User) constructor using 'super'.
    // Why it is used: Initializes all inherited user properties for the student.
    // -------------------------------------------------------------
    public Student(String id, String name, String email, String password, String phoneNumber) {
        // Call the parent User class constructor with role hardcoded as "STUDENT"
        super(id, name, email, password, phoneNumber, "STUDENT");
    }

    // -------------------------------------------------------------
    // Method Overriding (Polymorphism)
    // What it does: Provides Student's specific implementation of the abstract
    //               method declared in User.java.
    // Why it is used: Demonstrates run-time polymorphism.
    // -------------------------------------------------------------
    @Override
    public String getRoleDescription() {
        // Return a description unique to the Student role
        return "Student Applicant eligible to apply for Degree College Admissions.";
    }

    // -------------------------------------------------------------
    // Method: toDataString
    // What it does: Converts the Student object into a single line of text
    //               separated by the '|' (pipe) delimiter.
    // Why it is used: To write student records into the data/students.txt file.
    // Format: ID|Name|Email|Password|PhoneNumber
    // -------------------------------------------------------------
    public String toDataString() {
        // Combine all variables into a single text line with '|' separator
        return getId() + "|" + getName() + "|" + getEmail() + "|" + getPassword() + "|" + getPhoneNumber();
    }

    // -------------------------------------------------------------
    // Static Method: fromDataString
    // What it does: Takes a line read from data/students.txt and reconstructs
    //               a Student object from it.
    // Why it is used: Enables file reading in AdmissionSystem.java.
    // -------------------------------------------------------------
    public static Student fromDataString(String dataLine) {
        // If the line is empty or is a comment line starting with '#', return null
        if (dataLine == null || dataLine.trim().isEmpty() || dataLine.startsWith("#")) {
            return null;
        }

        // Split the line by the '|' delimiter (using \\| because | is a special regex character)
        String[] parts = dataLine.split("\\|");

        // Verify that the line contains all 5 required fields
        if (parts.length >= 5) {
            String id = parts[0].trim();           // Extract student ID
            String name = parts[1].trim();         // Extract student name
            String email = parts[2].trim();        // Extract email
            String password = parts[3].trim();     // Extract password
            String phone = parts[4].trim();        // Extract phone number

            // Instantiate and return a new Student object using the extracted data
            return new Student(id, name, email, password, phone);
        }

        // Return null if data format is invalid
        return null;
    }
}

