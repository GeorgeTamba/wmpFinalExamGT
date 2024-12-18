package com.example.enrollmentapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.example.enrollmentapp.DatabaseHelper;

public class SummaryActivity extends AppCompatActivity {

    private ListView listViewEnrolledSubjects;
    private TextView textViewTotalCredits;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_summary);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listViewEnrolledSubjects = findViewById(R.id.listViewEnrolledSubjects);
        textViewTotalCredits = findViewById(R.id.textViewTotalCredits);

        databaseHelper = new DatabaseHelper(this);

        // Get enrolled subjects
        List<String> enrolledSubjects = databaseHelper.getEnrolledSubjects();

        // Display total credits
        int totalCredits = databaseHelper.getTotalCredits();
        textViewTotalCredits.setText("Total Credits: " + totalCredits);

        // Populate ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, enrolledSubjects);
        listViewEnrolledSubjects.setAdapter(adapter);

    }
}
