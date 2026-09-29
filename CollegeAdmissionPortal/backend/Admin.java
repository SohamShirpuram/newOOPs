/**
 * ============================================================================
 * Class Name: Admin
 * OOP Concepts Demonstrated:
 *   1. Inheritance: Extends User class (is-a relationship: Admin is a User)
 *   2. Super Keyword: Invokes parent constructor using super(...)
 *   3. Polymorphism / Method Overriding: Overrides getRoleDescription()
 *   4. Encapsulation: Protects internal state and variables
 * ============================================================================
 * What this class does:
 *   - Represents a college admission officer or system administrator.
 *   - Admins have elevated privileges: they can view all submitted applications,
 *     verify student marks, and update application status (Approve / Reject).
 *
 * Where the data is saved:
 *   - Stored in the "data/admin.txt" file by AdmissionSystem.java.
 *
 * How frontend communicates with backend:
 *   - When the admin logs in via admin-login.html, credentials are sent to
 *     Main.java, which validates against loaded Admin objects.
 * ============================================================================
 */
public class Admin extends User {

    // -------------------------------------------------------------
    // Constructor
    // What it does: Calls the parent (User) constructor using 'super'.
    // Why it is used: Initializes all inherited user variables for the admin.
    // -------------------------------------------------------------
    public Admin(String id, String name, String email, String password, String phoneNumber) {
        // Call the parent User class constructor with role hardcoded as "ADMIN"
        super(id, name, email, password, phoneNumber, "ADMIN");
    }

    // -------------------------------------------------------------
    // Method Overriding (Polymorphism)
    // What it does: Provides Admin's specific implementation of getRoleDescription().
    // Why it is used: Demonstrates run-time polymorphism.
    // -------------------------------------------------------------
    @Override
    public String getRoleDescription() {
        // Return a description unique to the Admin role
        return "System Administrator with full authority to review, approve, or reject admission applications.";
    }

    // -------------------------------------------------------------
    // Method: toDataString
    // What it does: Converts the Admin object into a single line of text
    //               separated by the '|' (pipe) delimiter.
    // Why it is used: To write admin records into data/admin.txt.
    // Format: ID|Name|Email|Password|PhoneNumber
    // -------------------------------------------------------------
    public String toDataString() {
        // Combine all fields into a single text line
        return getId() + "|" + getName() + "|" + getEmail() + "|" + getPassword() + "|" + getPhoneNumber();
    }

    // -------------------------------------------------------------
    // Static Method: fromDataString
    // What it does: Reconstructs an Admin object from a line read from data/admin.txt.
    // Why it is used: Enables file reading in AdmissionSystem.java.
    // -------------------------------------------------------------
    public static Admin fromDataString(String dataLine) {
        // If line is empty or is a comment line starting with '#', ignore it
        if (dataLine == null || dataLine.trim().isEmpty() || dataLine.startsWith("#")) {
            return null;
        }

        // Split line by pipe character
        String[] parts = dataLine.split("\\|");

        // Check if line contains all 5 required fields
        if (parts.length >= 5) {
            String id = parts[0].trim();           // Extract admin ID
            String name = parts[1].trim();         // Extract admin name
            String email = parts[2].trim();        // Extract admin email
            String password = parts[3].trim();     // Extract password
            String phone = parts[4].trim();        // Extract phone number

            // Return a new Admin object
            return new Admin(id, name, email, password, phone);
        }

        // Return null if data line is invalid
        return null;
    }
}

