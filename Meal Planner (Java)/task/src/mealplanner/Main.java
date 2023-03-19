package mealplanner;


import mealplanner.enums.MealCategory;
import mealplanner.enums.State;
import mealplanner.exceptions.InvalidMealCategory;
import mealplanner.exceptions.WrongFormatException;
import mealplanner.model.Meal;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

import java.util.ArrayList;
import java.util.Scanner;


public class Main {

  public static void main(String[] args) throws SQLException {

      // JDBC database URL, username and password of postgre server
      final String DB_URL = "jdbc:postgresql:meals_db";
      final String USER = "postgres";
      final String PASS = "1111";

      Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
      conn.setAutoCommit(true);
      // creating a Statement object
      Statement stmt = conn.createStatement();
        String sql = "CREATE TABLE IF NOT EXISTS meals " +
                     "(meal_id INTEGER not NULL, " +
                     " category VARCHAR(255), " +
                     " meal VARCHAR(255), " +
                     " PRIMARY KEY ( meal_id ))";
        stmt.executeUpdate(sql);
        sql = "CREATE TABLE IF NOT EXISTS ingredients " +
              "(ingredient_id INTEGER not NULL, " +
              " ingredient VARCHAR(255), " +
              " meal_id INTEGER)";
        stmt.executeUpdate(sql);
        // Check the last meal ID
        sql = "SELECT meal_id FROM meals ORDER BY meal_id DESC LIMIT 1";
        ResultSet rs = stmt.executeQuery(sql);
        int prevMealId = 0;
        if (rs.next()) {
            prevMealId = rs.getInt("meal_id");
        }
        // Check the last ingredient ID
        sql = "SELECT ingredient_id FROM ingredients ORDER BY ingredient_id DESC LIMIT 1";
        rs = stmt.executeQuery(sql);
        int prevIngredientId = 0;
        if (rs.next()) {
            prevIngredientId = rs.getInt("ingredient_id");
        }

      State state;
      do {
          state = inputCommand(); // scanning input for which command to execute
          // options
          switch (state) {
              case ADD -> {
                  // adding a meal
                  Meal newMeal = addmeal();
                  // setting the meal ID
                  newMeal.setMealId(prevMealId + 1);
                  // incrementing the meal ID
                  prevMealId += 1;
                  // inserting data into the table
                    sql = "INSERT INTO meals VALUES (" + newMeal.getMealId() + ", '" + newMeal.getMealCategory() + "', '" + newMeal.getMealName() + "');";
                    stmt.executeUpdate(sql);
                    for (String ingredient : newMeal.getIngredients()) {
                        prevIngredientId += 1;
                        sql = "INSERT INTO ingredients VALUES (" + prevIngredientId + ", '" + ingredient + "', " + newMeal.getMealId() + ");";
                        stmt.executeUpdate(sql);
                    }
              }
              case SHOW -> displayMeal(DB_URL, USER, PASS);
              case PLAN -> planMeal(DB_URL, USER, PASS);
              case SAVE -> saveShoppingList(DB_URL, USER, PASS);
          }
      } while (state != State.EXIT);

      System.out.println("Bye!");
      stmt.close();
      conn.close();
  }

  private static State inputCommand() {
      Scanner scanner = new Scanner(System.in);
      State newState = State.NONE;
      while (newState == State.NONE) {
          try {
              System.out.println("What would you like to do (add, show, plan, save, exit)?"); // prompting command options
              newState = State.valueOf(scanner.nextLine().toUpperCase());
          } catch (IllegalArgumentException e) {

          }
      }
      return newState;
  }

  // input a meal
  private static Meal addmeal() {
      // Meal category
      MealCategory mealCategory = inputMealCategory();
      // Meal name
      String mealName = inputMealName();
      // Meal ingredients
      String[] ingredients = inputMealIngredients();

      System.out.println("The meal has been added!"); // Indication of successfully adding a meal
      return new Meal(mealCategory, mealName, ingredients);
  }

  // Returns the correct meal category input; will prompt invalid input if invalid
  private static MealCategory inputMealCategory() {
      Scanner scanner = new Scanner(System.in);
      MealCategory mealCategory = MealCategory.NONE;
      System.out.println("Which meal do you want to add (breakfast, lunch, dinner)?");
      while (mealCategory == MealCategory.NONE) {
          try {
              mealCategory = MealCategory.valueOf(scanner.nextLine().toUpperCase());
          } catch (IllegalArgumentException e) {
              System.out.println(new InvalidMealCategory().getMessage());
          }
      }
      return mealCategory;
  }

  // Returns the correct meal name input; will prompt invalid input if invalid
  private static String inputMealName() {
      Scanner scanner = new Scanner(System.in);
      String newMealName = "";
      boolean isCorrect = false;
      System.out.println("Input the meal's name:");
      do {
          try {
              newMealName = scanner.nextLine();
              isCorrect = isCorrectFormat(newMealName);
          } catch (WrongFormatException e) {
              System.out.println(e.getMessage());
          }
      } while (!isCorrect);
      return newMealName;
  }

  // Returns the correct meal ingredients input; will prompt invalid input if invalid
  private static String[] inputMealIngredients() {
      Scanner scanner = new Scanner(System.in);
      String[] newMealIngredients = new String[0];
      boolean isCorrect = false;
      System.out.println("Input the ingredients:");
      do {
          try {
              String str = scanner.nextLine();
              isCorrect = isCorrectFormat(str.split(","));
              newMealIngredients = str.split("\\s*,\\s*");
          } catch (WrongFormatException e) {
              System.out.println(e.getMessage());
          }
      } while (!isCorrect);
      return newMealIngredients;
  }

  private static boolean isCorrectFormat(String... args) throws WrongFormatException {
      for (String arg : args) {
          if (!arg.trim().matches("[a-zA-Z]+[a-zA-Z ]*")) {
              throw new WrongFormatException();
          }
      }
      return true;
  }

  private static void displayMeal(String DB_URL, String USER, String PASS) throws SQLException {
      Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
      Statement stmt = conn.createStatement();
        String sql = "SELECT * FROM meals ORDER BY meal_id";
        ResultSet rs = stmt.executeQuery(sql);
      // Check if there are any meals saved in the database
      if (!rs.next()) {
          System.out.println("No meals saved. Add a meal first.");
      } else {

          // Ask the user which category to print
          System.out.println("Which category do you want to print (breakfast, lunch, dinner)?");
            Scanner scanner = new Scanner(System.in);
            String category = scanner.nextLine();
            // Check if the category is valid
            while (!category.equals("breakfast") && !category.equals("lunch") && !category.equals("dinner")) {
                System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
                category = scanner.nextLine();
            }
            sql = "SELECT * FROM meals WHERE category = '" + category.toUpperCase() + "' ORDER BY meal_id";
            rs = stmt.executeQuery(sql);
            // Check if there are any meals in the category
            if (!rs.next()) {
                System.out.println("No meals found.");
            } else {
                int[] mealIds = new int[100];
                int i = 0;
                // Store the meal IDs in an array
                do {
                    mealIds[i] = rs.getInt("meal_id");
                    i++;
                } while (rs.next());
                // Print the meals
                System.out.println("Category: " + category.toLowerCase());
                for (int mealId : mealIds) {
                    if (mealId == 0) {
                        break;
                    }
                    sql = "SELECT * FROM meals WHERE meal_id = " + mealId;
                    rs = stmt.executeQuery(sql);
                    rs.next();
                    int id = rs.getInt("meal_id");
                    String mealName = rs.getString("meal");
                    System.out.println();
                    System.out.println("Name: " + mealName);
                    System.out.println("Ingredients: ");
                    // Display the ingredients
                    sql = "SELECT * FROM ingredients WHERE meal_id = " + id;
                    ResultSet rs2 = stmt.executeQuery(sql);
                    while (rs2.next()) {
                        String ingredient = rs2.getString("ingredient");
                        System.out.println(ingredient);
                    }
                }
            }
      }
      rs.close();
      stmt.close();
      conn.close();
  }

  private static void planMeal(String DB_URL, String USER, String PASS) throws SQLException {
      Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
      Statement stmt = conn.createStatement();
      String sql = "SELECT * FROM meals ORDER BY meal_id";
      ResultSet rs = stmt.executeQuery(sql);
      // Check if there are any meals saved in the database
      if (!rs.next()) {
          System.out.println("No meals saved. Add a meal first.");
      } else {
          // Create a new table named plan and delete the old one if it exists
            sql = "DROP TABLE IF EXISTS plan";
            stmt.executeUpdate(sql);
            sql = "CREATE TABLE plan (day VARCHAR(10), meal VARCHAR(255), category VARCHAR(255), meal_id INT NOT NULL)";
            stmt.executeUpdate(sql);
            // Create an array of days
            String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
            // Print each day and ask the user to input a meal
            for (String day : days) {
                System.out.println(day);
                // Output the meals for breakfast, lunch and dinner
                for (String category : new String[]{"breakfast", "lunch", "dinner"}) {
                    sql = "SELECT * FROM meals WHERE category = '" + category.toUpperCase() + "' ORDER BY meal";
                    rs = stmt.executeQuery(sql);
                    // Check if there are any meals in the category
                    if (!rs.next()) {
                        System.out.println("No meals found for " + category + ".");
                    } else {
                        int[] mealIds = new int[100];
                        int i = 0;
                        // Store the meal IDs in an array
                        do {
                            mealIds[i] = rs.getInt("meal_id");
                            i++;
                        } while (rs.next());
                        // Print the meals
                        for (int mealId : mealIds) {
                            if (mealId == 0) {
                                break;
                            }
                            sql = "SELECT * FROM meals WHERE meal_id = " + mealId;
                            rs = stmt.executeQuery(sql);
                            rs.next();
                            String mealName = rs.getString("meal");
                            System.out.println(mealName);
                        }
                        // Ask the user to input a choice
                        System.out.println("Choose the " + category + " for " + day + " from the list above:");
                        Scanner scanner = new Scanner(System.in);
                        String mealName = scanner.nextLine();
                        // Check if the meal exists
                        sql = "SELECT * FROM meals WHERE meal = '" + mealName + "' AND category = '" + category.toUpperCase() + "'";
                        rs = stmt.executeQuery(sql);
                        boolean isMeal = false;
                        while (!isMeal) {
                            System.out.println("This meal doesn’t exist. Choose a meal from the list above.");
                            mealName = scanner.nextLine();
                            sql = "SELECT * FROM meals WHERE meal = '" + mealName + "' AND category = '" + category.toUpperCase() + "'";
                            rs = stmt.executeQuery(sql);
                            isMeal = rs.next();
                        }
                        // Store the meal in the plan table
                        sql = "INSERT INTO plan (day, meal, category, meal_id) VALUES ('" + day + "', '" + mealName + "', '" + category.toUpperCase() + "', " + rs.getInt("meal_id") + ")";
                        stmt.executeUpdate(sql);

                    }
                }
                System.out.println("Yeah! We planned the meals for " + day + ".\n");
            }
            // Print the plan
            // Iterate through the days
            for (String day : days) {
                System.out.println(day);
                // Print the meals for breakfast, lunch and dinner
                for (String cat : new String[]{"Breakfast", "Lunch", "Dinner"}) {
                    sql = "SELECT * FROM plan WHERE day = '" + day + "' AND category = '" + cat.toUpperCase() + "'";
                    rs = stmt.executeQuery(sql);
                    if (!rs.next()) {
                        System.out.println("No " + cat + " planned.");
                    } else {
                        System.out.println(cat + ": " + rs.getString("meal"));
                    }
                }
            }
            rs.close();
            stmt.close();
            conn.close();
      }
  }

  private static void saveShoppingList(String DB_URL, String USER, String PASS) throws SQLException {
      // Check if the plan table is complete
      Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
      Statement stmt = conn.createStatement();
      try {
          String sql = "SELECT * FROM plan";
          stmt.executeQuery(sql);
      } catch (SQLException e) {
          System.out.println("Unable to save. Plan your meals first.");
          return;
      }
        // Create a new table named shopping_list and delete the old one if it exists
        String sql = "DROP TABLE IF EXISTS shopping_list";
        stmt.executeUpdate(sql);
        sql = "CREATE TABLE shopping_list (ingredient VARCHAR(255), quantity INT)";
        stmt.executeUpdate(sql);
        // Iterate through the plan table
        sql = "SELECT * FROM plan";
        ResultSet rs = stmt.executeQuery(sql);
        ArrayList<Integer> mealIds = new ArrayList<>();
        while (rs.next()) {
            // Get the meal ID
            int mealId = rs.getInt("meal_id");
            // Store all the meal IDs in an array
            mealIds.add(mealId);
        }
        // Iterate through the array
        for (int mealId : mealIds) {
            // Get the ingredients for the meal
            sql = "SELECT * FROM ingredients WHERE meal_id = " + mealId;
            ResultSet rs2 = stmt.executeQuery(sql);
            while (rs2.next()) {
                // Get the ingredient
                String ingredient = rs2.getString("ingredient");
                // Check if the ingredient is already in the shopping list
                sql = "SELECT * FROM shopping_list WHERE ingredient = '" + ingredient + "'";
                Statement stmt3 = conn.createStatement();
                Statement stmt4 = conn.createStatement();
                ResultSet rs3 = stmt3.executeQuery(sql);
                if (!rs3.next()) {
                    // Add the ingredient to the shopping list
                    sql = "INSERT INTO shopping_list (ingredient, quantity) VALUES ('" + ingredient + "', " + 1 + ")";
                    stmt4.executeUpdate(sql);
                } else {
                    // Update the quantity
                    int newQuantity = rs3.getInt("quantity") + 1;
                    sql = "UPDATE shopping_list SET quantity = " + newQuantity + " WHERE ingredient = '" + ingredient + "'";
                    stmt4.executeUpdate(sql);
                }
            }
        }

    // Store the shopping list as a file
    System.out.println("Input a filename:");
    String fileName = new Scanner(System.in).nextLine();
    try {
        // Create a new file
        FileWriter fileWriter = new FileWriter(fileName);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        sql = "SELECT * FROM shopping_list";
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
            String ingredient = rs.getString("ingredient");
            int quantity = rs.getInt("quantity");
            // If the quantity is 1, don't print it
            if (quantity == 1) {
                bufferedWriter.write(ingredient);
            } else {
                bufferedWriter.write(ingredient + " x" + quantity);
            }
            bufferedWriter.newLine();
        }
        bufferedWriter.close();
        System.out.println("Saved!");
    } catch (IOException e) {
        System.out.println("Unable to save the shopping list.");
    }

  }

}