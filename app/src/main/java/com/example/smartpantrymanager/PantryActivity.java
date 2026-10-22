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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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

                // Display whole numbers without .0.
                String formattedQuantity;

                if (quantity == (long) quantity) {
                    formattedQuantity =
                            String.valueOf((long) quantity);
                } else {
                    formattedQuantity = String.valueOf(quantity);
                }

                String item = name + " - "
                        + formattedQuantity + " " + unit;

                if (expiryDate != null
                        && !expiryDate.trim().isEmpty()) {

                    item += "\nExpiry date: " + expiryDate.trim();

                    String warning =
                            getExpiryWarning(expiryDate.trim());

                    if (!warning.isEmpty()) {
                        item += "\n" + warning;
                    }
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

        // Display the total number of saved ingredients.
        if (ingredients.isEmpty()) {
            tvPantryEmpty.setText(
                    "Your pantry is empty. Add an ingredient to get started."
            );
            tvPantryEmpty.setVisibility(View.VISIBLE);
            listPantryIngredients.setVisibility(View.GONE);
        } else {
            int totalIngredients = ingredients.size();

            tvPantryEmpty.setText(
                    "Your pantry contains " + totalIngredients
                            + (totalIngredients == 1
                            ? " ingredient."
                            : " ingredients.")
            );

            tvPantryEmpty.setVisibility(View.VISIBLE);
            listPantryIngredients.setVisibility(View.VISIBLE);
        }
    }

    // Displays an expiry warning with the days remaining.
    private String getExpiryWarning(String expiryDate) {
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        dateFormat.setLenient(false);

        try {
            Date expiry = dateFormat.parse(expiryDate);

            if (expiry == null) {
                return "";
            }

            String todayString = dateFormat.format(new Date());
            Date today = dateFormat.parse(todayString);

            if (today == null) {
                return "";
            }

            if (expiry.before(today)) {
                return "EXPIRED!";
            }

            if (expiry.equals(today)) {
                return "Expires today!";
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(today);
            calendar.add(Calendar.DAY_OF_YEAR, 3);

            Date threeDaysFromNow = calendar.getTime();

            if (!expiry.after(threeDaysFromNow)) {
                long differenceInMillis =
                        expiry.getTime() - today.getTime();

                long daysRemaining = differenceInMillis
                        / (24L * 60 * 60 * 1000);

                if (daysRemaining == 1) {
                    return "Expires in 1 day!";
                }

                return "Expires in " + daysRemaining + " days!";
            }

        } catch (ParseException e) {
            return "";
        }

        return "";
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
        String ingredientName = "this ingredient";

        try (Cursor cursor = databaseHelper.getAllIngredients()) {
            while (cursor.moveToNext()) {
                int currentId = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COLUMN_ID));

                if (currentId == ingredientId) {
                    ingredientName = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    PantryDatabaseHelper.COLUMN_NAME));
                    break;
                }
            }
        }

        final String nameToDelete = ingredientName;

        new AlertDialog.Builder(this)
                .setTitle("Delete Ingredient")
                .setMessage(
                        "Are you sure you want to delete "
                                + nameToDelete + "?"
                )
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean deleted =
                            databaseHelper.deleteIngredient(ingredientId);

                    if (deleted) {
                        Toast.makeText(
                                this,
                                nameToDelete + " deleted.",
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