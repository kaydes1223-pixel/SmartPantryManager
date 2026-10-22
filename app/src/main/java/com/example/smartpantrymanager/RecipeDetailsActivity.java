package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        TextView tvRecipeName = findViewById(R.id.tvRecipeName);
        TextView tvRecipeIngredients =
                findViewById(R.id.tvRecipeIngredients);
        TextView tvRecipeInstructions =
                findViewById(R.id.tvRecipeInstructions);

        Button btnBack = findViewById(R.id.btnRecipeDetailsBack);

        String recipeName = getIntent().getStringExtra("recipe_name");

        if (recipeName == null || recipeName.trim().isEmpty()) {
            recipeName = "Recipe Details";
        }

        tvRecipeName.setText(recipeName);

        String ingredients;
        String instructions;

        switch (recipeName) {
            case "Scrambled Eggs":
                ingredients = "• 2 eggs\n• 0.1 units of milk\n• Salt and pepper (optional)";
                instructions = "1. Crack the eggs into a bowl.\n\n"
                        + "2. Add the milk and whisk well.\n\n"
                        + "3. Heat a lightly greased pan over medium-low heat.\n\n"
                        + "4. Pour in the egg mixture and stir gently until cooked.\n\n"
                        + "5. Season with salt and pepper if desired, then serve.";
                break;

            case "Cheese Omelette":
                ingredients = "• 2 eggs\n• 1 unit of cheese\n• Salt and pepper (optional)";
                instructions = "1. Beat the eggs in a bowl.\n\n"
                        + "2. Heat a lightly greased frying pan.\n\n"
                        + "3. Pour in the eggs and cook until nearly set.\n\n"
                        + "4. Add the cheese, fold the omelette, and cook until done.";
                break;

            case "French Toast":
                ingredients = "• 2 slices of bread\n• 1 egg\n• 0.1 units of milk";
                instructions = "1. Whisk the egg and milk together.\n\n"
                        + "2. Dip each slice of bread into the mixture.\n\n"
                        + "3. Cook in a lightly greased pan until golden on both sides.\n\n"
                        + "4. Serve warm.";
                break;

            case "Pancakes":
            case "Fluffy Pancakes":
                ingredients = "• Flour\n• Eggs\n• Milk\n• Sugar (for fluffy pancakes)";
                instructions = "1. Mix the flour and other required ingredients into a batter.\n\n"
                        + "2. Heat a lightly greased pan.\n\n"
                        + "3. Pour in a small amount of batter.\n\n"
                        + "4. Cook until bubbles form, then flip and cook the other side.\n\n"
                        + "5. Serve warm.";
                break;

            case "Banana Smoothie":
                ingredients = "• 1 banana\n• 0.25 units of milk";
                instructions = "1. Peel and slice the banana.\n\n"
                        + "2. Add the banana and milk to a blender.\n\n"
                        + "3. Blend until smooth and serve.";
                break;

            case "Tomato Omelette":
                ingredients = "• 2 eggs\n• 1 tomato";
                instructions = "1. Beat the eggs in a bowl.\n\n"
                        + "2. Chop the tomato and mix it with the eggs.\n\n"
                        + "3. Cook in a lightly greased pan until the eggs are fully set.";
                break;

            case "Cheese Sandwich":
                ingredients = "• 2 slices of bread\n• Cheese";
                instructions = "1. Place cheese between two slices of bread.\n\n"
                        + "2. Serve as is, or toast until golden.";
                break;

            case "Butter Toast":
                ingredients = "• 2 slices of bread\n• Butter";
                instructions = "1. Toast the bread.\n\n"
                        + "2. Spread butter on the warm toast.\n\n"
                        + "3. Serve immediately.";
                break;

            case "Rice and Beans":
                ingredients = "• Rice\n• Beans";
                instructions = "1. Cook the rice according to its package instructions.\n\n"
                        + "2. Cook the beans until tender, if they are not already cooked.\n\n"
                        + "3. Combine and serve.";
                break;

            case "Tomato Pasta":
                ingredients = "• Pasta\n• Tomatoes";
                instructions = "1. Cook the pasta according to its package instructions.\n\n"
                        + "2. Chop and cook the tomatoes in a pan.\n\n"
                        + "3. Mix the tomato sauce with the drained pasta and serve.";
                break;

            case "Chicken and Rice":
                ingredients = "• Chicken\n• Rice";
                instructions = "1. Cook the rice according to its package instructions.\n\n"
                        + "2. Cook the chicken thoroughly until it reaches 74°C internally.\n\n"
                        + "3. Serve the chicken with the rice.";
                break;

            case "Mashed Potatoes":
                ingredients = "• 3 potatoes\n• Milk\n• Butter";
                instructions = "1. Peel and boil the potatoes until soft.\n\n"
                        + "2. Drain and mash them.\n\n"
                        + "3. Mix in the milk and butter until smooth.";
                break;

            case "Apple Oatmeal":
                ingredients = "• 1 apple\n• Oats\n• Milk";
                instructions = "1. Chop the apple into small pieces.\n\n"
                        + "2. Cook the oats with milk according to the package instructions.\n\n"
                        + "3. Stir in the apple and serve.";
                break;

            case "Yoghurt and Banana":
                ingredients = "• Yoghurt\n• 1 banana";
                instructions = "1. Slice the banana.\n\n"
                        + "2. Add the banana to a bowl of yoghurt.\n\n"
                        + "3. Mix gently and enjoy.";
                break;

            default:
                ingredients = "Check the recipe requirements in Suggested Recipes.";
                instructions = "Follow a suitable recipe method using the required ingredients.";
                break;
        }

        tvRecipeIngredients.setText(ingredients);
        tvRecipeInstructions.setText(instructions);

        btnBack.setOnClickListener(v -> finish());
    }
}
