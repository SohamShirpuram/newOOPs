// Package statement is omitted to keep compilation simple for beginners.

/**
 * ============================================================================
 * Class Name: User
 * OOP Concepts Demonstrated:
 *   1. Abstraction (abstract class that cannot be directly instantiated)
 *   2. Encapsulation (private variables accessed via public getters and setters)
 *   3. Inheritance (serves as the parent superclass for Student and Admin)
 *   4. Polymorphism (declares abstract method getRoleDescription for overriding)
 * ============================================================================
 * What this class does:
 *   - This is the base (parent) class representing any user in the admission system.
 *   - Both Students and Admins share common identity details like ID, Name,
 *     Email, Password, Phone Number, and Role.
 *   - Instead of writing these same variables twice in Student and Admin,
 *     we define them once here in User.java. This is code reusability via Inheritance.
 * ============================================================================
 */
public abstract class User {

    // -------------------------------------------------------------
    // Private instance variables (Encapsulation: data hiding)
    // These variables are private so outside classes cannot change them directly.
    // -------------------------------------------------------------

    // Stores unique ID of the user (e.g. STU101 for student, ADM001 for admin)
    private String id;

    // Stores the full name of the user
    private String name;

    // Stores the email address used for login
    private String email;

    // Stores the account password
    private String password;

    // Stores the contact phone number of the user
    private String phoneNumber;

    // Stores the user role ("STUDENT" or "ADMIN")
    private String role;

    // -------------------------------------------------------------
    // Constructor
    // What it does: Initializes a new User object with all required details.
    // Why it is used: Ensures every user object starts with valid data.
    // -------------------------------------------------------------
    public User(String id, String name, String email, String password, String phoneNumber, String role) {
        // 'this.id' refers to the private instance variable above
        this.id = id;
        // 'this.name' assigns the parameter 'name' to the object's 'name'
        this.name = name;
        // Assign email parameter
        this.email = email;
        // Assign password parameter
        this.password = password;
        // Assign phoneNumber parameter
        this.phoneNumber = phoneNumber;
        // Assign role parameter
        this.role = role;
    }

    // -------------------------------------------------------------
    // Abstract Method (Abstraction & Polymorphism)
    // What it does: Declares a method with no body (no implementation).
    // Why it is used: Every child class (Student, Admin) MUST provide its
    // own implementation of this method. This allows polymorphic behavior.
    // -------------------------------------------------------------
    public abstract String getRoleDescription();

    // -------------------------------------------------------------
    // Concrete Method
    // What it does: Displays user details and calls getRoleDescription().
    // Why it is used: Demonstrates Run-time Polymorphism because the
    // getRoleDescription() output depends on whether the object is a Student or Admin.
    // -------------------------------------------------------------
    public void displayUserDetails() {
        // Print the user's name
        System.out.println("Name: " + this.name);
        // Print the user's email
        System.out.println("Email: " + this.email);
        // Print the role description (calls the overridden child method)
        System.out.println("Role: " + getRoleDescription());
    }

    // -------------------------------------------------------------
    // Getters and Setters (Encapsulation)
    // What they do: Provide controlled public access to private variables.
    // Why they are used: Protect the internal state of the object.
    // -------------------------------------------------------------

    // Getter for ID: returns the user's ID
    public String getId() {
        return id;
    }

    // Setter for ID: updates the user's ID
    public void setId(String id) {
        this.id = id;
    }

    // Getter for Name: returns the user's full name
    public String getName() {
        return name;
    }

    // Setter for Name: updates the user's name
    public void setName(String name) {
        this.name = name;
    }

    // Getter for Email: returns the user's email
    public String getEmail() {
        return email;
    }

    // Setter for Email: updates the user's email
    public void setEmail(String email) {
        this.email = email;
    }

    // Getter for Password: returns the user's password
    public String getPassword() {
        return password;
    }

    // Setter for Password: updates the user's password
    public void setPassword(String password) {
        this.password = password;
    }

    // Getter for Phone Number: returns user's phone number
    public String getPhoneNumber() {
        return phoneNumber;
    }

    // Setter for Phone Number: updates user's phone number
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // Getter for Role: returns the user's role string
    public String getRole() {
        return role;
    }

    // Setter for Role: updates the user's role string
    public void setRole(String role) {
        this.role = role;
    }
}

