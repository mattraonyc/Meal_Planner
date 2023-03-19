package mealplanner.exceptions;

public class InvalidMealCategory extends IllegalArgumentException{
    public InvalidMealCategory() {
        super("Wrong meal category! Choose from: breakfast, lunch, dinner.");
    }
}
