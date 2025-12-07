package model;

import java.io.Serializable;
import java.time.LocalDate;

// Serializable User entity transferred between RMI client and server
public class User implements Serializable {

    // Technical identifier assigned by server
    private long id;

    // Business data: user's first name
    private String firstName;

    // Business data: user's last name
    private String lastName;

    // Business data: date of birth
    private LocalDate birthDate;

    // Business data: user's salary value
    private double salary;

    // Business data: gender enum
    private Gender gender;

    // Business data: organizational department
    private String department;

    // Business data: job position
    private String position;

    // Returns unique user identifier
    public long getId() { return id; }

    // Assigns unique user identifier (server-side only)
    public void setId(long id) { this.id = id; }

    // Returns user's first name
    public String getFirstName() { return firstName; }

    // Updates user's first name
    public void setFirstName(String firstName) { this.firstName = firstName; }

    // Returns user's last name
    public String getLastName() { return lastName; }

    // Updates user's last name
    public void setLastName(String lastName) { this.lastName = lastName; }

    // Returns user's birth date
    public LocalDate getBirthDate() { return birthDate; }

    // Updates user's birth date
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    // Returns current salary value
    public double getSalary() { return salary; }

    // Updates salary value
    public void setSalary(double salary) { this.salary = salary; }

    // Returns gender enum value
    public Gender getGender() { return gender; }

    // Updates gender value
    public void setGender(Gender gender) { this.gender = gender; }

    // Returns department assignment
    public String getDepartment() { return department; }

    // Updates organizational department
    public void setDepartment(String department) { this.department = department; }

    // Returns job position
    public String getPosition() { return position; }

    // Updates job position
    public void setPosition(String position) { this.position = position; }

    // Converts full user object to readable log output
    @Override
    public String toString() {
        return "User {" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDate=" + birthDate +
                ", salary=" + salary +
                ", gender=" + gender +
                ", department='" + department + '\'' +
                ", position='" + position + '\'' +
                '}';
    }
}
