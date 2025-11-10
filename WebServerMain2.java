package backend;

import java.io.IOException;

public class WebServerMain {
    public static void main(String[] args) {
        try {
            WebServer server = new WebServer(8080);
            server.start();
            System.out.println("Social Media Feed Viewer running at http://localhost:8080");
            System.out.println("Press Ctrl+C to stop the server");
            
            // Keep the server running
            Thread.currentThread().join();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }
}
