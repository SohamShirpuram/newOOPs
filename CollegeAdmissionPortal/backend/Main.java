import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * ============================================================================
 * Class Name: Main
 * OOP & System Architecture Role:
 *   1. Driver / Entry Point: Contains main(String[] args) method to launch program.
 *   2. Web Server: Uses Core Java's built-in HttpServer (com.sun.net.httpserver).
 *      No external framework (no Spring Boot, Tomcat, or Node.js) required.
 *   3. Controller: Routes incoming web requests to AdmissionSystem and returns responses.
 * ============================================================================
 * What this class does:
 *   - Starts a local web server at port 8080 (http://localhost:8080/).
 *   - Serves HTML and CSS files from the frontend/ directory to the user's browser.
 *   - Handles form submissions (Registration, Login, Application Form, Admin Actions).
 *   - Calls AdmissionSystem methods to process data and save records into .txt files.
 *
 * How the frontend communicates with the backend:
 *   - The browser sends HTTP requests (GET to view pages, POST when submitting forms).
 *   - Main.java reads the request parameters, executes the requested action using
 *     AdmissionSystem, and sends back an HTTP response.
 * ============================================================================
 */
public class Main {

    // The port number where our server will listen for browser connections
    private static final int PORT = 8080;

    // Instance of AdmissionSystem that manages our data and .txt files
    private static AdmissionSystem admissionSystem;

    // Path to the frontend folder containing HTML and CSS files
    private static String frontendPath;

    // -------------------------------------------------------------
    // Main Method (Execution Entry Point)
    // -------------------------------------------------------------
    public static void main(String[] args) {
        try {
            System.out.println("=================================================");
            System.out.println("   COLLEGE ADMISSION PORTAL (OOPS PROJECT)       ");
            System.out.println("=================================================");

            // Step 1: Initialize the core admission business logic and load text files
            admissionSystem = new AdmissionSystem();

            // Step 2: Detect where frontend folder is located (supports running from root or backend folder)
            locateFrontendFolder();

            // Step 3: Create Java's built-in HTTP server listening on port 8080
            // InetSocketAddress binds the server to localhost:8080
            // Second parameter 0 means use the system default backlog queue size
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

            // Step 4: Register HTTP route handlers (Endpoints)
            // Route "/" handles static frontend files (HTML, CSS)
            server.createContext("/", new StaticFileHandler());

            // API Routes for handling Student actions
            server.createContext("/api/register", new RegisterHandler());
            server.createContext("/api/login", new LoginHandler());
            server.createContext("/api/apply", new ApplyHandler());
            server.createContext("/api/status", new StatusHandler());

            // API Routes for handling Admin actions
            server.createContext("/api/admin-login", new AdminLoginHandler());
            server.createContext("/api/admin/applications", new AdminApplicationsHandler());
            server.createContext("/api/admin/action", new AdminActionHandler());

            // Step 5: Start the server thread
            // Passing null tells the server to use the default thread executor
            server.setExecutor(null);
            server.start();

            // Print helpful instructions to the console
            System.out.println("\n[SERVER STARTED] Web server is running!");
            System.out.println(">>> Open your browser and go to: http://localhost:" + PORT + "/");
            System.out.println(">>> Press Ctrl+C in terminal to stop the server.\n");

        } catch (IOException e) {
            System.err.println("[SERVER ERROR] Failed to start HTTP server: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------
    // Helper Method: locateFrontendFolder
    // What it does: Checks whether "frontend" folder is in current directory
    //               or parent directory.
    // -------------------------------------------------------------
    private static void locateFrontendFolder() {
        File dir = new File("frontend");
        if (dir.exists() && dir.isDirectory()) {
            frontendPath = "frontend";
        } else {
            frontendPath = "../frontend";
        }
        System.out.println("[Main] Serving frontend files from: " + new File(frontendPath).getAbsolutePath());
    }

    // =============================================================
    // HTTP HANDLER: Serves HTML and CSS files to the web browser
    // =============================================================
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Get requested URL path (e.g. "/" or "/style.css" or "/register.html")
            String requestPath = exchange.getRequestURI().getPath();

            // If user visits the root "/", default to "index.html"
            if (requestPath.equals("/")) {
                requestPath = "/index.html";
            }

            // Construct file path inside the frontend folder
            File file = new File(frontendPath + requestPath);

            // Check if requested file exists on disk
            if (file.exists() && !file.isDirectory()) {
                // Determine the correct Content-Type (MIME type)
                String contentType = "text/plain";
                if (requestPath.endsWith(".html")) {
                    contentType = "text/html; charset=UTF-8";
                } else if (requestPath.endsWith(".css")) {
                    contentType = "text/css; charset=UTF-8";
                } else if (requestPath.endsWith(".js")) {
                    contentType = "application/javascript; charset=UTF-8";
                }

                // Read file content into byte array
                byte[] fileBytes = new byte[(int) file.length()];
                try (FileInputStream fis = new FileInputStream(file)) {
                    fis.read(fileBytes);
                }

                // Set response header and status 200 (OK)
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, fileBytes.length);

                // Send file bytes back to browser
                OutputStream os = exchange.getResponseBody();
                os.write(fileBytes);
                os.close();
            } else {
                // Return 404 Not Found if file doesn't exist
                String notFound = "<h1>404 Not Found</h1><p>The requested page was not found.</p>";
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(404, notFound.length());
                OutputStream os = exchange.getResponseBody();
                os.write(notFound.getBytes());
                os.close();
            }
        }
    }

    // =============================================================
    // HTTP HANDLER: Student Registration (/api/register)
    // =============================================================
    static class RegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Only process POST requests
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                // Parse submitted form data (key=value pairs)
                Map<String, String> params = parseRequestBody(exchange);

                String name = params.getOrDefault("name", "");
                String email = params.getOrDefault("email", "");
                String password = params.getOrDefault("password", "");
                String phone = params.getOrDefault("phone", "");

                // Validate that all fields were entered
                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
                    sendJsonResponse(exchange, 400, "{\"success\":false,\"message\":\"All fields are required!\"}");
                    return;
                }

                // Call AdmissionSystem to register student (saves to data/students.txt)
                boolean success = admissionSystem.registerStudent(name, email, password, phone);

                if (success) {
                    sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Registration successful! You can now login.\"}");
                } else {
                    sendJsonResponse(exchange, 400, "{\"success\":false,\"message\":\"This email is already registered! Please login.\"}");
                }
            } else {
                sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method not allowed\"}");
            }
        }
    }

    // =============================================================
    // HTTP HANDLER: Student Login (/api/login)
    // =============================================================
    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                Map<String, String> params = parseRequestBody(exchange);

                String email = params.getOrDefault("email", "");
                String password = params.getOrDefault("password", "");

                // Verify credentials against Student list loaded from data/students.txt
                Student student = admissionSystem.authenticateStudent(email, password);

                if (student != null) {
                    // Send success JSON containing student details
                    String json = String.format(
                        "{\"success\":true,\"id\":\"%s\",\"name\":\"%s\",\"email\":\"%s\",\"message\":\"Login successful!\"}",
                        escapeJson(student.getId()),
                        escapeJson(student.getName()),
                        escapeJson(student.getEmail())
                    );
                    sendJsonResponse(exchange, 200, json);
                } else {
                    sendJsonResponse(exchange, 401, "{\"success\":false,\"message\":\"Invalid email or password! Please check credentials.\"}");
                }
            } else {
                sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method not allowed\"}");
            }
        }
    }

    // =============================================================
    // HTTP HANDLER: Admission Form Submission (/api/apply)
    // =============================================================
    static class ApplyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                Map<String, String> params = parseRequestBody(exchange);

                String email = params.getOrDefault("email", "");
                String name = params.getOrDefault("name", "");
                String sscStr = params.getOrDefault("sscMarks", "0");
                String hscStr = params.getOrDefault("hscMarks", "0");
                String cetStr = params.getOrDefault("cetScore", "0");
                String course = params.getOrDefault("course", "");
                String category = params.getOrDefault("category", "General");

                // Parse marks
                double ssc = 0.0, hsc = 0.0, cet = 0.0;
                try {
                    ssc = Double.parseDouble(sscStr);
                    hsc = Double.parseDouble(hscStr);
                    cet = Double.parseDouble(cetStr);
                } catch (NumberFormatException e) {
                    sendJsonResponse(exchange, 400, "{\"success\":false,\"message\":\"Please enter valid numerical marks!\"}");
                    return;
                }

                // Submit application and save to data/applications.txt
                Application app = admissionSystem.submitApplication(email, name, ssc, hsc, cet, course, category);

                String json = String.format(
                    "{\"success\":true,\"appId\":\"%s\",\"message\":\"Application submitted successfully!\"}",
                    escapeJson(app.getApplicationId())
                );
                sendJsonResponse(exchange, 200, json);
            } else {
                sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method not allowed\"}");
            }
        }
    }

    // =============================================================
    // HTTP HANDLER: Check Application Status (/api/status?email=...)
    // =============================================================
    static class StatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                // Extract query parameters from URL
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> queryParams = parseQueryString(query);
                String email = queryParams.getOrDefault("email", "");

                // Find application by email
                Application app = admissionSystem.getApplicationByEmail(email);

                if (app != null) {
                    // Return application details as JSON
                    String json = String.format(
                        "{\"found\":true,\"appId\":\"%s\",\"name\":\"%s\",\"email\":\"%s\"," +
                        "\"ssc\":%.2f,\"hsc\":%.2f,\"cet\":%.2f,\"course\":\"%s\"," +
                        "\"category\":\"%s\",\"status\":\"%s\",\"remarks\":\"%s\"}",
                        escapeJson(app.getApplicationId()),
                        escapeJson(app.getStudentName()),
                        escapeJson(app.getStudentEmail()),
                        app.getSscMarks(),
                        app.getHscMarks(),
                        app.getCetScore(),
                        escapeJson(app.getCourseChoice()),
                        escapeJson(app.getCategory()),
                        escapeJson(app.getStatus()),
                        escapeJson(app.getRemarks())
                    );
                    sendJsonResponse(exchange, 200, json);
                } else {
                    sendJsonResponse(exchange, 200, "{\"found\":false,\"message\":\"No application found for this email address.\"}");
                }
            } else {
                sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method not allowed\"}");
            }
        }
    }

    // =============================================================
    // HTTP HANDLER: Admin Login (/api/admin-login)
    // =============================================================
    static class AdminLoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                Map<String, String> params = parseRequestBody(exchange);

                String email = params.getOrDefault("email", "");
                String password = params.getOrDefault("password", "");

                // Verify credentials against Admin accounts loaded from data/admin.txt
                Admin admin = admissionSystem.authenticateAdmin(email, password);

                if (admin != null) {
                    String json = String.format(
                        "{\"success\":true,\"id\":\"%s\",\"name\":\"%s\",\"email\":\"%s\",\"role\":\"%s\",\"message\":\"Admin authentication successful!\"}",
                        escapeJson(admin.getId()),
                        escapeJson(admin.getName()),
                        escapeJson(admin.getEmail()),
                        escapeJson(admin.getRoleDescription())
                    );
                    sendJsonResponse(exchange, 200, json);
                } else {
                    sendJsonResponse(exchange, 401, "{\"success\":false,\"message\":\"Invalid Admin credentials! Access Denied.\"}");
                }
            } else {
                sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method not allowed\"}");
            }
        }
    }

    // =============================================================
    // HTTP HANDLER: Admin Get All Applications (/api/admin/applications)
    // =============================================================
    static class AdminApplicationsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                ArrayList<Application> allApps = admissionSystem.getAllApplications();

                // Build a JSON array string manually without needing third-party libraries
                StringBuilder sb = new StringBuilder();
                sb.append("[");
                for (int i = 0; i < allApps.size(); i++) {
                    Application a = allApps.get(i);
                    sb.append(String.format(
                        "{\"appId\":\"%s\",\"name\":\"%s\",\"email\":\"%s\"," +
                        "\"ssc\":%.2f,\"hsc\":%.2f,\"cet\":%.2f,\"course\":\"%s\"," +
                        "\"category\":\"%s\",\"status\":\"%s\",\"remarks\":\"%s\"}",
                        escapeJson(a.getApplicationId()),
                        escapeJson(a.getStudentName()),
                        escapeJson(a.getStudentEmail()),
                        a.getSscMarks(),
                        a.getHscMarks(),
                        a.getCetScore(),
                        escapeJson(a.getCourseChoice()),
                        escapeJson(a.getCategory()),
                        escapeJson(a.getStatus()),
                        escapeJson(a.getRemarks())
                    ));
                    if (i < allApps.size() - 1) {
                        sb.append(",");
                    }
                }
                sb.append("]");

                sendJsonResponse(exchange, 200, sb.toString());
            } else {
                sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method not allowed\"}");
            }
        }
    }

    // =============================================================
    // HTTP HANDLER: Admin Action - Approve or Reject (/api/admin/action)
    // =============================================================
    static class AdminActionHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                Map<String, String> params = parseRequestBody(exchange);

                String appId = params.getOrDefault("appId", "");
                String action = params.getOrDefault("action", ""); // "Approved" or "Rejected"
                String remarks = params.getOrDefault("remarks", "");

                boolean success = admissionSystem.updateApplicationStatus(appId, action, remarks);

                if (success) {
                    sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Application status updated to " + action + "!\"}");
                } else {
                    sendJsonResponse(exchange, 404, "{\"success\":false,\"message\":\"Application ID not found.\"}");
                }
            } else {
                sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method not allowed\"}");
            }
        }
    }

    // =============================================================
    // UTILITY METHODS FOR HTTP PARSING AND RESPONSES
    // =============================================================

    // Helper: Reads and parses URL-encoded body data (e.g. name=John&email=john%40gmail.com)
    private static Map<String, String> parseRequestBody(HttpExchange exchange) throws IOException {
        Map<String, String> params = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
            // Parse parameters separated by '&'
            String[] pairs = body.toString().split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length >= 2) {
                    String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8.name());
                    String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8.name());
                    params.put(key, value);
                } else if (keyValue.length == 1) {
                    String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8.name());
                    params.put(key, "");
                }
            }
        }
        return params;
    }

    // Helper: Parses URL query parameters (e.g. /api/status?email=test%40gmail.com)
    private static Map<String, String> parseQueryString(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return params;
        }
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length >= 2) {
                String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                params.put(key, value);
            }
        }
        return params;
    }

    // Helper: Sends JSON response with status code and UTF-8 encoding
    private static void sendJsonResponse(HttpExchange exchange, int statusCode, String jsonResponse) throws IOException {
        byte[] bytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    // Helper: Escapes quotes and backslashes in JSON strings safely
    private static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ").replace("\r", " ");
    }
}

