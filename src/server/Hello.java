package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import model.User;
import model.UserValidationException;

// In-memory RMI service implementing full CRUD with audit logging
public class Hello extends UnicastRemoteObject implements HelloInterface {

    private static final long serialVersionUID = 1L;

    // Thread-safe user storage indexed by unique ID
    private final Map<Long, User> users = new ConcurrentHashMap<>();

    // Atomic counter for generating unique user IDs
    private final AtomicLong idGenerator = new AtomicLong(1);

    // Remote service constructor
    public Hello() throws RemoteException {
        super();
    }

    // -------------------- CREATE --------------------

    // Validates input data and stores new user record
    @Override
    public void addUser(User user)
            throws RemoteException, UserValidationException {

        // Business validation of all user fields
        validateUser(user);

        // Generate new unique identifier
        long id = idGenerator.getAndIncrement();
        user.setId(id);

        // Store deep copy to protect internal state
        users.put(id, cloneUser(user));

        // Audit log for successful creation
        System.out.println("[ADD] User created -> " + user);
    }

    // -------------------- DELETE --------------------

    // Removes user by ID and returns operation result
    @Override
    public boolean removeUser(long id)
            throws RemoteException {

        // Attempt to remove record from storage
        User removed = users.remove(id);

        // Successful deletion scenario
        if (removed != null) {
            System.out.println("[DELETE] User removed -> " + removed);
            return true;
        }

        // Deletion attempted for non-existing ID
        System.out.println("[DELETE] User id=" + id + " not found.");
        return false;
    }

    // -------------------- READ --------------------

    // Retrieves single user by ID and returns detached copy
    @Override
    public User getUser(long id)
            throws RemoteException {

        User u = users.get(id);

        // Return deep copy to prevent remote modification
        return u == null ? null : cloneUser(u);
    }

    // Retrieves full user list as detached copies
    @Override
    public List<User> getAllUsers()
            throws RemoteException {

        return users.values()
                .stream()
                // Clone each object before returning outside server
                .map(this::cloneUser)
                .collect(Collectors.toList());
    }

    // -------------------- UPDATE – SALARY --------------------

    // Updates only salary field of existing user
    @Override
    public boolean updateUserSalary(long id, double salary)
            throws RemoteException, UserValidationException {

        // Business validation for salary value
        if (salary < 0)
            throw new UserValidationException("Salary must be non-negative.");

        // Lookup target user
        User u = users.get(id);

        // Target user does not exist
        if (u == null) {
            System.out.println("[UPDATE-SALARY] User id=" + id + " not found.");
            return false;
        }

        double oldSalary = u.getSalary();
        u.setSalary(salary);

        // Audit log for successful update
        System.out.println("[UPDATE-SALARY] User id=" + id +
                " | " + oldSalary + " -> " + salary);

        return true;
    }

    // -------------------- UPDATE – DEPARTMENT & POSITION --------------------

    // Updates department and position fields of existing user
    @Override
    public boolean updateUserDepartmentAndPosition(long id, String department, String position)
            throws RemoteException, UserValidationException {

        // Business validation for department input
        if (department == null || department.isBlank())
            throw new UserValidationException("Department cannot be empty.");

        // Business validation for position input
        if (position == null || position.isBlank())
            throw new UserValidationException("Position cannot be empty.");

        // Lookup target user
        User u = users.get(id);

        // Target user does not exist
        if (u == null) {
            System.out.println("[UPDATE-DEPT] User id=" + id + " not found.");
            return false;
        }

        String oldDep = u.getDepartment();
        String oldPos = u.getPosition();

        // Apply modifications
        u.setDepartment(department);
        u.setPosition(position);

        // Audit log for successful update
        System.out.println("[UPDATE-DEPT] User id=" + id +
                " | Department: " + oldDep + " -> " + department +
                " | Position: " + oldPos + " -> " + position);

        return true;
    }

    // -------------------- SHUTDOWN --------------------

    // Gracefully unexports remote object and shuts down JVM
    @Override
    public void shutdown()
            throws RemoteException {

        System.out.println("[SHUTDOWN] Remote server shutdown requested.");

        // Run shutdown flow in separate thread to safely release RMI resources
        new Thread(() -> {
            try {
                UnicastRemoteObject.unexportObject(this, true);
            } catch (Exception ignored) {}

            System.exit(0);
        }).start();
    }

    // -------------------- VALIDATION --------------------

    // Performs full business validation of user object fields
    private void validateUser(User u)
            throws UserValidationException {

        // Null object check
        if (u == null)
            throw new UserValidationException("User object is null.");

        // Mandatory first name validation
        if (u.getFirstName() == null || u.getFirstName().isBlank())
            throw new UserValidationException("First name required.");

        // Mandatory last name validation
        if (u.getLastName() == null || u.getLastName().isBlank())
            throw new UserValidationException("Last name required.");

        // Birth date must exist and be in the past
        if (u.getBirthDate() == null || u.getBirthDate().isAfter(LocalDate.now()))
            throw new UserValidationException("Invalid birth date.");

        // Salary validation rule
        if (u.getSalary() < 0)
            throw new UserValidationException("Salary must be non-negative.");

        // Gender is required
        if (u.getGender() == null)
            throw new UserValidationException("Gender required.");

        // Department must be provided
        if (u.getDepartment() == null || u.getDepartment().isBlank())
            throw new UserValidationException("Department required.");

        // Position must be provided
        if (u.getPosition() == null || u.getPosition().isBlank())
            throw new UserValidationException("Position required.");
    }

    // -------------------- CLONING --------------------

    // Creates deep copy of User entity to protect server state
    private User cloneUser(User u) {

        User c = new User();

        c.setId(u.getId());
        c.setFirstName(u.getFirstName());
        c.setLastName(u.getLastName());
        c.setBirthDate(u.getBirthDate());
        c.setSalary(u.getSalary());
        c.setGender(u.getGender());
        c.setDepartment(u.getDepartment());
        c.setPosition(u.getPosition());

        return c;
    }
}
