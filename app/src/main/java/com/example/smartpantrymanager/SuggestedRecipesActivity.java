package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private PantryDatabaseHelper databaseHelper;
    private ListView listSuggestedRecipes;
    private TextView tvNoRecipes;

    private final List<Recipe> recipes = Arrays.asList(
            new Recipe("Cheese Omelette",
                    new String[]{"eggs", "cheese"},
                    new double[]{2, 1}),
            new Recipe("Scrambled Eggs",
                    new String[]{"eggs", "milk"},
                    new double[]{2, 0.1}),
            new Recipe("French Toast",
                    new String[]{"bread", "eggs", "milk"},
                    new double[]{2, 1, 0.1}),
            new Recipe("Pancakes",
                    new String[]{"flour", "eggs", "milk"},
                    new double[]{1, 1, 0.2}),
            new Recipe("Banana Smoothie",
                    new String[]{"banana", "milk"},
                    new double[]{1, 0.25}),
            new Recipe("Tomato Omelette",
                    new String[]{"eggs", "tomato"},
                    new double[]{2, 1}),
            new Recipe("Cheese Sandwich",
                    new String[]{"bread", "cheese"},
                    new double[]{2, 1}),
            new Recipe("Butter Toast",
                    new String[]{"bread", "butter"},
                    new double[]{2, 1}),
            new Recipe("Rice and Beans",
                    new String[]{"rice", "beans"},
                    new double[]{1, 1}),
            new Recipe("Tomato Pasta",
                    new String[]{"pasta", "tomato"},
                    new double[]{1, 2}),
            new Recipe("Chicken and Rice",
                    new String[]{"chicken", "rice"},
                    new double[]{1, 1}),
            new Recipe("Mashed Potatoes",
                    new String[]{"potatoes", "milk", "butter"},
                    new double[]{3, 0.1, 1}),
            new Recipe("Apple Oatmeal",
                    new String[]{"apples", "oats", "milk"},
                    new double[]{1, 1, 0.25}),
            new Recipe("Yoghurt and Banana",
                    new String[]{"yoghurt", "banana"},
                    new double[]{1, 1}),
            new Recipe("Fluffy Pancakes",
                    new String[]{"flour", "eggs", "milk", "sugar"},
                    new double[]{1, 2, 0.2, 1})
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        listSuggestedRecipes =
                findViewById(R.id.listSuggestedRecipes);
        tvNoRecipes = findViewById(R.id.tvNoRecipes);

        databaseHelper = new PantryDatabaseHelper(this);

        Button btnBack = findViewById(R.id.btnRecipesBack);
        btnBack.setOnClickListener(v -> finish());

        showMatchingRecipes();

        // Open the details screen when a recipe is tapped.
        listSuggestedRecipes.setOnItemClickListener(
                (parent, view, position, id) -> {
                    String selectedRecipe =
                            (String) parent.getItemAtPosition(position);

                    Intent intent = new Intent(
                            SuggestedRecipesActivity.this,
                            RecipeDetailsActivity.class
                    );

                    intent.putExtra("recipe_name", selectedRecipe);
                    startActivity(intent);
                }
        );
    }

    private void showMatchingRecipes() {
        Map<String, Double> pantry = new HashMap<>();

        try (Cursor cursor = databaseHelper.getAllIngredients()) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        PantryDatabaseHelper.COLUMN_NAME))
                        .trim().toLowerCase(Locale.ROOT);

                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COLUMN_QUANTITY));

                pantry.put(name, pantry.getOrDefault(name, 0.0) + quantity);
            }
        }

        ArrayList<String> matches = new ArrayList<>();

        for (Recipe recipe : recipes) {
            boolean canMake = true;

            for (int i = 0; i < recipe.ingredients.length; i++) {
                String requiredName = recipe.ingredients[i]
                        .toLowerCase(Locale.ROOT);

                double available = pantry.getOrDefault(requiredName, 0.0);

                if (available < recipe.quantities[i]) {
                    canMake = false;
                    break;
                }
            }

            if (canMake) {
                matches.add(recipe.name);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                matches
        );

        listSuggestedRecipes.setAdapter(adapter);

        if (matches.isEmpty()) {
            tvNoRecipes.setText(
                    "No recipes match your pantry yet. " +
                            "Try adding more ingredients!"
            );
            tvNoRecipes.setVisibility(View.VISIBLE);
            listSuggestedRecipes.setVisibility(View.GONE);
        } else {
            tvNoRecipes.setVisibility(View.GONE);
            listSuggestedRecipes.setVisibility(View.VISIBLE);
        }
    }

    private static class Recipe {
        String name;
        String[] ingredients;
        double[] quantities;

        Recipe(String name, String[] ingredients, double[] quantities) {
            this.name = name;
            this.ingredients = ingredients;
            this.quantities = quantities;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}