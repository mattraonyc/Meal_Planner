package mealplanner.exceptions;

public class WrongFormatException extends RuntimeException {
    public WrongFormatException() {
        super("Wrong format. Use letters only!");
    }
}
