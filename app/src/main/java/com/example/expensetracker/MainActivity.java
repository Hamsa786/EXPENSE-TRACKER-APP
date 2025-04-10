package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ExpenseAdapter expenseAdapter;
    private DatabaseHelper dbHelper;
    private TextView totalAmountTextView;
    private Button addExpenseButton;
    private Button viewChartsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Initialize UI components
        totalAmountTextView = findViewById(R.id.totalAmountTextView);
        addExpenseButton = findViewById(R.id.addExpenseButton);
        viewChartsButton = findViewById(R.id.viewChartsButton);
        RecyclerView expensesRecyclerView = findViewById(R.id.expensesRecyclerView);

        // Set up RecyclerView
        expensesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        expenseAdapter = new ExpenseAdapter(this);
        expensesRecyclerView.setAdapter(expenseAdapter);

        // Set click listeners
        addExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
                startActivity(intent);
            }
        });

        viewChartsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChartActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExpenses();
        updateTotalAmount();
    }

    private void loadExpenses() {
        List<Expense> expenses = dbHelper.getAllExpenses();
        expenseAdapter.setExpenses(expenses);
        expenseAdapter.notifyDataSetChanged();
    }

    private void updateTotalAmount() {
        double totalAmount = dbHelper.getTotalExpenseAmount();
        totalAmountTextView.setText(String.format("Total: $%.2f", totalAmount));
    }
}
