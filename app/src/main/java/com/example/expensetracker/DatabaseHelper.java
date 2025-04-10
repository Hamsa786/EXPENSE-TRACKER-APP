package com.example.expensetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "expense_tracker.db";
    private static final int DATABASE_VERSION = 1;

    // Table name
    private static final String TABLE_EXPENSES = "expenses";

    // Table columns
    private static final String KEY_ID = "id";
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_DATE = "date";

    // Date format
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EXPENSES_TABLE = "CREATE TABLE " + TABLE_EXPENSES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_AMOUNT + " REAL,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_CATEGORY + " TEXT,"
                + KEY_DATE + " TEXT"
                + ")";
        db.execSQL(CREATE_EXPENSES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        onCreate(db);
    }

    // Add a new expense
    public long addExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_AMOUNT, expense.getAmount());
        values.put(KEY_DESCRIPTION, expense.getDescription());
        values.put(KEY_CATEGORY, expense.getCategory());
        values.put(KEY_DATE, dateFormat.format(expense.getDate()));

        long id = db.insert(TABLE_EXPENSES, null, values);
        db.close();
        return id;
    }

    // Get all expenses
    public List<Expense> getAllExpenses() {
        List<Expense> expenseList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_EXPENSES + " ORDER BY " + KEY_DATE + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
                expense.setAmount(cursor.getDouble(cursor.getColumnIndex(KEY_AMOUNT)));
                expense.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
                expense.setCategory(cursor.getString(cursor.getColumnIndex(KEY_CATEGORY)));
                try {
                    String dateStr = cursor.getString(cursor.getColumnIndex(KEY_DATE));
                    expense.setDate(dateFormat.parse(dateStr));
                } catch (ParseException e) {
                    expense.setDate(new Date());
                }
                expenseList.add(expense);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return expenseList;
    }
    // Get expenses grouped by category - ADD THIS METHOD HERE
    public HashMap<String, Double> getExpensesByCategory() {
        HashMap<String, Double> categoryAmounts = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT " + KEY_CATEGORY + ", SUM(" + KEY_AMOUNT + ") as total FROM " +
                TABLE_EXPENSES + " GROUP BY " + KEY_CATEGORY;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String category = cursor.getString(cursor.getColumnIndex(KEY_CATEGORY));
                double total = cursor.getDouble(cursor.getColumnIndex("total"));
                categoryAmounts.put(category, total);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return categoryAmounts;
    }
    // Get total expense amount
    public double getTotalExpenseAmount() {
        SQLiteDatabase db = this.getReadableDatabase();
        double total = 0;

        String query = "SELECT SUM(" + KEY_AMOUNT + ") as total FROM " + TABLE_EXPENSES;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            total = cursor.getDouble(cursor.getColumnIndex("total"));
        }

        cursor.close();
        db.close();
        return total;
    }
}
