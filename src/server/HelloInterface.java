package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import model.User;
import model.UserValidationException;

// Public RMI service contract – defines all remote operations available to clients
public interface HelloInterface extends Remote {

    // Creates and stores new user record on the server
    void addUser(User user)
            throws RemoteException, UserValidationException;

    // Deletes user identified by ID; returns true if deletion succeeded
    boolean removeUser(long id)
            throws RemoteException;

    // Retrieves single user by ID or returns null if not found
    User getUser(long id)
            throws RemoteException;

    // Returns full list of all stored users
    List<User> getAllUsers()
            throws RemoteException;

    // Updates salary of an existing user; returns true if update was successful
    boolean updateUserSalary(long id, double salary)
            throws RemoteException, UserValidationException;

    // Updates department and position of an existing user; returns true if update was successful
    boolean updateUserDepartmentAndPosition(long id, String department, String position)
            throws RemoteException, UserValidationException;

    // Requests controlled shutdown of the RMI server
    void shutdown()
            throws RemoteException;
}
