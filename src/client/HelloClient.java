package client;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import model.Gender;
import model.User;
import model.UserValidationException;
import server.HelloInterface;

// Console CRUD RMI client with full input validation
public class HelloClient {

    // Max number of RMI connection attempts
    private static final int MAX_RETRY = 5;

    // Delay between connection attempts (ms)
    private static final long DELAY = 2000;

    // Allows only letters and spaces (including Polish letters), min 2 chars
    private static final String NAME_REGEX =
            "^[A-Za-ząćęłńóśżźĄĆĘŁŃÓŚŻŹ -]{2,}$";

    // Entry point: establishes RMI connection and starts interactive menu
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        try {
            // Default RMI host is localhost, can be overridden by CLI argument
            String host = "localhost";
            if (args.length > 0) host = args[0];

            // Try to connect with retry logic
            HelloInterface hi = connectWithRetry(host);

            // If connection failed after all retries, terminate client
            if (hi == null) {
                System.out.println("Unable to connect to server.");
                return;
            }

            // Start interactive CRUD menu
            runMenu(hi, sc);

        } finally {
            // Always close scanner on exit
            sc.close();
        }
    }

    // Tries to obtain RMI stub with a few retries and delay between attempts
    private static HelloInterface connectWithRetry(String host) {

        // Sequential connection attempts up to MAX_RETRY
        for (int i = 1; i <= MAX_RETRY; i++) {
            try {
                System.out.println("Connecting to RMI server (" + i + "/" + MAX_RETRY + ")...");
                return (HelloInterface) Naming.lookup("//" + host + ":5001/Hello");

            } catch (Exception e) {
                System.out.println("Connection failed: " + e.getMessage());

                // Wait before next attempt (except after last one)
                if (i < MAX_RETRY) {
                    try {
                        Thread.sleep(DELAY);
                    } catch (InterruptedException ignored) {}
                }
            }
        }
        // Null indicates that all attempts failed
        return null;
    }

    // Main interactive loop: displays menu and dispatches user actions
    private static void runMenu(HelloInterface hi, Scanner sc) {

        boolean exit = false;

        // Menu loop until user chooses to exit or remote error occurs
        while (!exit) {

            printMenu();

            System.out.print("Choose option: ");
            String choice = sc.nextLine().trim();

            try {
                // Dispatch menu options to remote operations
                switch (choice) {

                    case "1":
                        // Create new user on server
                        addUser(hi, sc);
                        break;

                    case "2":
                        // Remove existing user by ID
                        removeUser(hi, sc);
                        break;

                    case "3":
                        // Fetch and display single user by ID
                        getUser(hi, sc);
                        break;

                    case "4":
                        // Fetch and display all users
                        listUsers(hi);
                        break;

                    case "5":
                        // Modify selected fields of existing user
                        modifyUser(hi, sc);
                        break;

                    case "9":
                        // Request graceful server shutdown and exit client
                        hi.shutdown();
                        exit = true;
                        System.out.println("Shutdown request sent to server.");
                        break;

                    case "0":
                        // Exit client without contacting server
                        exit = true;
                        break;

                    default:
                        // Handles unsupported menu choices
                        System.out.println("Invalid menu option.");
                }
            }
            // Business validation error coming from server-side UserValidationException
            catch (UserValidationException ve) {
                System.out.println("Validation error: " + ve.getMessage());
            }
            // Technical error in remote communication: stop client
            catch (RemoteException re) {
                System.out.println("REMOTE ERROR: " + re.getMessage());
                exit = true;
            }
        }

        System.out.println("Client closed.");
    }

    // Prints main menu for user database operations
    private static void printMenu() {

        System.out.println("\n========= USER DATABASE MENU =========");
        System.out.println(" 1 - Add user");
        System.out.println(" 2 - Remove user");
        System.out.println(" 3 - Get user by ID");
        System.out.println(" 4 - List users");
        System.out.println(" 5 - Modify user");
        System.out.println(" 9 - Shutdown server");
        System.out.println(" 0 - Exit");
        System.out.println("--------------------------------------");
    }

    // Collects user data from console, validates it and sends add request to server
    private static void addUser(HelloInterface hi, Scanner sc)
            throws RemoteException, UserValidationException {

        // Read and validate first / last name
        String fn = readName(sc, "First name");
        String ln = readName(sc, "Last name");

        System.out.print("Birth date (yyyy-MM-dd): ");
        // Read and validate date in ISO format
        LocalDate date = readDate(sc);

        System.out.print("Salary: ");
        // Read and validate numeric salary
        double sal = readDouble(sc);

        System.out.print("Gender (MALE/FEMALE): ");
        // Read and validate enum-based gender
        Gender g = readGender(sc);

        // Read free-text department and position with minimal length check
        String dep = readText(sc, "Department");
        String pos = readText(sc, "Position");

        // Build user DTO for remote call
        User u = new User();
        u.setFirstName(fn);
        u.setLastName(ln);
        u.setBirthDate(date);
        u.setSalary(sal);
        u.setGender(g);
        u.setDepartment(dep);
        u.setPosition(pos);

        // Invoke remote create operation
        hi.addUser(u);
        System.out.println("User added.");
    }

    // Removes user on server by ID
    private static void removeUser(HelloInterface hi, Scanner sc)
            throws RemoteException {

        System.out.print("User ID: ");
        long id = readLong(sc);

        // Remote call returns true if user was physically removed
        if (hi.removeUser(id))
            System.out.println("User deleted.");
        else
            System.out.println("User not found.");
    }

    // Retrieves and prints single user by ID
    private static void getUser(HelloInterface hi, Scanner sc)
            throws RemoteException {

        System.out.print("User ID: ");
        long id = readLong(sc);

        // Null indicates that user with given ID does not exist
        User u = hi.getUser(id);

        if (u == null)
            System.out.println("User not found.");
        else
            System.out.println(u);
    }

    // Retrieves and prints all users stored on server
    private static void listUsers(HelloInterface hi)
            throws RemoteException {

        List<User> users = hi.getAllUsers();

        // Business check: empty list means no data in remote store
        if (users.isEmpty()) {
            System.out.println("No users stored.");
            return;
        }

        // Print each user in a separate line using toString()
        users.forEach(System.out::println);
    }

    // Allows to update selected user fields (salary or department & position)
    private static void modifyUser(HelloInterface hi, Scanner sc)
            throws RemoteException, UserValidationException {

        System.out.print("User ID: ");
        long id = readLong(sc);

        System.out.println("1 - Change salary");
        System.out.println("2 - Change department & position");

        String option = sc.nextLine().trim();

        // Local choice which determines which remote update method will be called
        switch (option) {

            case "1":
                // Update only salary for given user
                System.out.print("New salary: ");
                double sal = readDouble(sc);
                hi.updateUserSalary(id, sal);
                System.out.println("Salary updated.");
                break;

            case "2":
                // Update department and position for given user
                String dep = readText(sc, "New department");
                String pos = readText(sc, "New position");

                hi.updateUserDepartmentAndPosition(id, dep, pos);
                System.out.println("Department and position updated.");
                break;

            default:
                // Handles unsupported modification options
                System.out.println("Invalid option.");
        }
    }

    // ------------ VALIDATION HELPERS ------------

    // Reads and validates name-like field using NAME_REGEX
    private static String readName(Scanner sc, String label) {

        while (true) {
            System.out.print(label + ": ");
            String val = sc.nextLine().trim();

            // Business rule: name must match regex for allowed characters
            if (val.matches(NAME_REGEX))
                return val;

            System.out.println("Invalid value → use letters only (min 2 chars).");
        }
    }

    // Reads generic text field and enforces minimal length
    private static String readText(Scanner sc, String label) {

        while (true) {
            System.out.print(label + ": ");
            String val = sc.nextLine().trim();

            // Business rule: at least 2 characters required
            if (val.length() >= 2)
                return val;

            System.out.println("Value required → minimum 2 characters.");
        }
    }

    // Reads long ID from console with retry on invalid numeric format
    private static long readLong(Scanner sc) {
        while (true) {
            try {
                return Long.parseLong(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid number. Try again: ");
            }
        }
    }

    // Reads double value (e.g. salary) with retry on invalid numeric format
    private static double readDouble(Scanner sc) {
        while (true) {
            try {
                return Double.parseDouble(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid number. Try again: ");
            }
        }
    }

    // Reads date in ISO format and retries until valid LocalDate is provided
    private static LocalDate readDate(Scanner sc) {
        while (true) {
            try {
                return LocalDate.parse(sc.nextLine());
            } catch (DateTimeParseException e) {
                System.out.print("Invalid date (yyyy-MM-dd). Try again: ");
            }
        }
    }

    // Reads gender value and converts it to Gender enum (MALE/FEMALE only)
    private static Gender readGender(Scanner sc) {
        while (true) {
            try {
                return Gender.valueOf(sc.nextLine().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.print("Use MALE/FEMALE: ");
            }
        }
    }
}
