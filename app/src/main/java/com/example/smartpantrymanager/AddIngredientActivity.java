package com.example.smartpantrymanager;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddIngredientActivity extends AppCompatActivity {

    private EditText etIngredientName;
    private EditText etQuantity;
    private EditText etUnit;
    private EditText etExpiryDate;

    private PantryDatabaseHelper databaseHelper;
    private int ingredientId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);

        etIngredientName = findViewById(R.id.etIngredientName);
        etQuantity = findViewById(R.id.etQuantity);
        etUnit = findViewById(R.id.etUnit);
        etExpiryDate = findViewById(R.id.etExpiryDate);

        Button btnSave = findViewById(R.id.btnSaveIngredient);
        Button btnCancel = findViewById(R.id.btnCancelIngredient);

        databaseHelper = new PantryDatabaseHelper(this);

        // Check whether an existing ingredient is being edited.
        ingredientId = getIntent().getIntExtra("ingredient_id", -1);

        if (ingredientId != -1) {
            loadIngredientDetails();
        }

        btnSave.setOnClickListener(v -> saveIngredient());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void loadIngredientDetails() {
        try (Cursor cursor = databaseHelper.getIngredientById(ingredientId)) {
            if (cursor.moveToFirst()) {
                etIngredientName.setText(cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COLUMN_NAME)));

                etQuantity.setText(String.valueOf(cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COLUMN_QUANTITY))));

                etUnit.setText(cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COLUMN_UNIT)));

                String expiryDate = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COLUMN_EXPIRY_DATE));

                etExpiryDate.setText(
                        expiryDate == null ? "" : expiryDate);
            }
        }
    }

    private void saveIngredient() {
        String name = etIngredientName.getText().toString().trim();
        String quantityText = etQuantity.getText().toString().trim();
        String unit = etUnit.getText().toString().trim();
        String expiryDate = etExpiryDate.getText().toString().trim();

        // Validate the expiry date if one was entered.
        if (!expiryDate.isEmpty()) {
            SimpleDateFormat dateFormat =
                    new SimpleDateFormat("yyyy-MM-dd", Locale.US);

            dateFormat.setLenient(false);

            ParsePosition position = new ParsePosition(0);
            Date parsedDate = dateFormat.parse(expiryDate, position);

            if (parsedDate == null
                    || position.getIndex() != expiryDate.length()) {
                Toast.makeText(this,
                        "Enter a valid expiry date in YYYY-MM-DD format.",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Check that all required fields have been entered.
        if (name.isEmpty() || quantityText.isEmpty() || unit.isEmpty()) {
            Toast.makeText(this,
                    "Please enter the name, quantity and unit.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            Toast.makeText(this,
                    "Please enter a valid quantity.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Quantity must be greater than zero.
        if (quantity <= 0) {
            Toast.makeText(this,
                    "Quantity must be greater than zero.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        boolean saved;

        if (ingredientId == -1) {
            saved = databaseHelper.addIngredient(
                    name, quantity, unit, expiryDate);
        } else {
            saved = databaseHelper.updateIngredient(
                    ingredientId, name, quantity, unit, expiryDate);
        }

        if (saved) {
            Toast.makeText(this,
                    ingredientId == -1
                            ? "Ingredient saved successfully!"
                            : "Ingredient updated successfully!",
                    Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this,
                    "Could not save changes. Please try again.",
                    Toast.LENGTH_SHORT).show();
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