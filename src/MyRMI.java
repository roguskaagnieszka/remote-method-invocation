/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author pbilski
 */
public class MyRMI {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
			// Here we create the registry for interfaces
            Registry r = LocateRegistry.createRegistry(5001);
			// ... and make the Hello object available from the outside
            r.rebind("Hello", new Hello("Hello from the RMI server!"));
            System.out.println("The RMI server is up!");
        }
        catch (Exception e)
        {
            System.out.println("Server error: " + e.getMessage());
        }
        
    }
    
}
