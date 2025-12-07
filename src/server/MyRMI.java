package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.net.BindException;

// RMI registry bootstrap and server object binding
public class MyRMI {

    // TCP port used by RMI registry
    public static final int PORT = 5001;

    // Server startup entry point
    public static void main(String[] args) {

        try {
            // Create local RMI registry on given port
            Registry registry = LocateRegistry.createRegistry(PORT);

            // Instantiate remote service implementation
            Hello server = new Hello();

            // Bind remote object under logical service name
            registry.rebind("Hello", server);

            // Informational startup banner
            System.out.println("==================================");
            System.out.println(" RMI SERVER STARTED SUCCESSFULLY ");
            System.out.println(" Registry port : " + PORT);
            System.out.println(" Bind name     : Hello");
            System.out.println("==================================");

        }
        // Technical error handling during registry startup or object binding
        catch (Exception e) {

            System.out.println("==================================");
            System.out.println(" RMI SERVER START FAILED ");
            System.out.println("==================================");

            // Walk through exception chain to determine root cause
            Throwable root = e;
            while (root.getCause() != null) {
                root = root.getCause();
            }

            // Business-friendly message for port conflict scenario
            if (root instanceof BindException) {
                System.out.println("ERROR: Port " + PORT + " is already in use.");
                System.out.println("Stop existing RMI server and retry.");
            }
            // Generic fallback for any other unexpected server startup error
            else {
                System.out.println("Unexpected server error:");
                System.out.println(e.getMessage());
            }
        }
    }
}
