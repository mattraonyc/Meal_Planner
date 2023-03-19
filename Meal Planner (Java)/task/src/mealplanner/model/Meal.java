package mealplanner.model;

import mealplanner.enums.MealCategory;

public class Meal {
    private final MealCategory mealCategory;
    private final String mealName;
    private final String[] ingredients;
    private static final int MAX_INGREDIENTS = 10;
    private static int mealId = 0;

    public Meal(MealCategory mealCategory, String mealName, String[] ingredients) {
        this.mealCategory = mealCategory;
        this.mealName = mealName;
        this.ingredients = ingredients;
    }

    public MealCategory getMealCategory() {
        return mealCategory;
    }

    public String getMealName() {
        return mealName;
    }

    public String[] getIngredients() {
        return ingredients;
    }

    public int getMealId() {
        return mealId;
    }

    public void setMealId(int mealId) {
        this.mealId = mealId;
    }
}
