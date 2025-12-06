/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.io.Serializable;

/**
 *
 * @author pbilski
 */
 
// This is the definition of the object that will be passed as the remote argument
public class MyObject implements Serializable {
	// Internal properties
    private int length;
    private int height;
    private int breadth;
    // Constructor
    public MyObject()
    {
        this.length = 4;
        this.height = 5;
        this.breadth = 3;
    }
    // Setters and getters
    int getLength()
    {
        return this.length;
    }
    
    void setLength(int l)
    {
        this.length = l;
    }
    
    int getHeight()
    {
        return this.height;
    }
    
    void setHeight(int h)
    {
        this.height = h;
    }
    
    int getBreadth()
    {
        return this.breadth;
    }
    
    void setBreadth(int b)
    {
        this.breadth = b;
    }
}
