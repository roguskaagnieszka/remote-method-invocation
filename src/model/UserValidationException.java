package model;

import java.io.Serializable;

// Custom checked exception used to propagate business validation errors over RMI
public class UserValidationException extends Exception implements Serializable {

    private static final long serialVersionUID = 1L;

    // Creates validation exception with descriptive business message
    public UserValidationException(String message) {
        super(message);
    }
}
