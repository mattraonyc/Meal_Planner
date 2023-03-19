package mealplanner.enums;

public enum MealCategory {
    NONE(""),
    BREAKFAST("breakfast"),
    LUNCH("lunch"),
    DINNER("dinner");

    private final String meal;

    MealCategory(String meal) {
        this.meal = meal;
    }

    public String getMeal() {
        return meal;
    }
}
