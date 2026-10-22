package com.example.smartpantrymanager;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class PantryActivity extends AppCompatActivity {

    private PantryDatabaseHelper databaseHelper;
    private ListView listPantryIngredients;
    private TextView tvPantryEmpty;

    private ArrayList<Integer> ingredientIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        listPantryIngredients =
                findViewById(R.id.listPantryIngredients);
        tvPantryEmpty = findViewById(R.id.tvPantryEmpty);

        Button btnAdd =
                findViewById(R.id.btnPantryAddIngredient);
        Button btnBack =
                findViewById(R.id.btnPantryBack);

        databaseHelper = new PantryDatabaseHelper(this);

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(
                    PantryActivity.this,
                    AddIngredientActivity.class
            );
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(
                    PantryActivity.this,
                    MainActivity.class
            );
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        // Allow users to edit or delete an ingredient.
        listPantryIngredients.setOnItemClickListener(
                (parent, view, position, id) -> {
                    showIngredientOptions(position);
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadIngredients();
    }

    private void loadIngredients() {
        ArrayList<String> ingredients = new ArrayList<>();
        ingredientIds.clear();

        try (Cursor cursor = databaseHelper.getAllIngredients()) {
            while (cursor.moveToNext()) {

                int ingredientId = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COLUMN_ID));

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COLUMN_NAME));

                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COLUMN_QUANTITY));

                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COLUMN_UNIT));

                String expiryDate = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COLUMN_EXPIRY_DATE));

                String item = name + " - " + quantity + " " + unit;

                if (expiryDate != null && !expiryDate.trim().isEmpty()) {
                    item += "\nExpiry date: " + expiryDate;
                }

                ingredients.add(item);
                ingredientIds.add(ingredientId);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                ingredients
        );

        listPantryIngredients.setAdapter(adapter);

        if (ingredients.isEmpty()) {
            tvPantryEmpty.setVisibility(View.VISIBLE);
            listPantryIngredients.setVisibility(View.GONE);
        } else {
            tvPantryEmpty.setVisibility(View.GONE);
            listPantryIngredients.setVisibility(View.VISIBLE);
        }
    }

    private void showIngredientOptions(int position) {
        if (position < 0 || position >= ingredientIds.size()) {
            return;
        }

        int ingredientId = ingredientIds.get(position);

        String[] options = {
                "Edit Ingredient",
                "Delete Ingredient"
        };

        new AlertDialog.Builder(this)
                .setTitle("Manage Ingredient")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        editIngredient(ingredientId);
                    } else {
                        confirmDelete(ingredientId);
                    }
                })
                .show();
    }

    private void editIngredient(int ingredientId) {
        Intent intent = new Intent(
                PantryActivity.this,
                AddIngredientActivity.class
        );

        intent.putExtra("ingredient_id", ingredientId);
        startActivity(intent);
    }

    private void confirmDelete(int ingredientId) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Ingredient")
                .setMessage(
                        "Are you sure you want to delete this ingredient?"
                )
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean deleted =
                            databaseHelper.deleteIngredient(ingredientId);

                    if (deleted) {
                        Toast.makeText(
                                this,
                                "Ingredient deleted.",
                                Toast.LENGTH_SHORT
                        ).show();

                        loadIngredients();
                    } else {
                        Toast.makeText(
                                this,
                                "Could not delete ingredient.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}