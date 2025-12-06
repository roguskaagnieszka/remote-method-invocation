/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author pbilski
 */
public class Hello extends UnicastRemoteObject implements HelloInterface {
    // Internal property of the object for storing the message
    private final String message;
    // Method for setting the property
    public Hello (String msg) throws RemoteException {
        message = msg;
    }
    // Method for retrieving the property
    public String say() throws RemoteException {
        return message;
    }
    // Method that adds 2 numbers
    public int addNumbers(int a, int b) throws RemoteException {
        return a + b;
    }
    // Method operating on the object-type parameter
    public int calculateVolume(MyObject o) throws RemoteException
    {
        return o.getBreadth() * o.getHeight() * o.getLength();
    }
}
