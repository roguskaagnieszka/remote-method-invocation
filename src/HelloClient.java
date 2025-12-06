/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.rmi.Naming;
import java.util.Scanner;

/**
 *
 * @author pbils
 */
public class HelloClient 
{
    public static void main(String[] argv)
    {
		// The object for reading from the keyboard
        Scanner keyb = new Scanner(System.in); 
        int x, y;
		// The object that will be passed as the parameter
        MyObject mo = new MyObject();
        try {
			// Here we retreive the interface provided by the server
            HelloInterface hi = (HelloInterface)Naming.lookup("//localhost:5001/Hello");
			// ... and print the message
            System.out.println(hi.say());
            // This is the part where we pass two numbers for additions
            System.out.println("Please enter the first argument");
            x = keyb.nextInt();
            System.out.println("Please enter the second argument");
            y = keyb.nextInt();
            System.out.println("Result: " + hi.addNumbers(x,y));
			// Here we perform operation on the object passed as the argument
            System.out.println("Volume: " + hi.calculateVolume(mo));
        }
        catch (Exception e)
        {
            System.out.println("Client error: " + e.getMessage());
        }   
    }
}
