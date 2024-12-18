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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.example.enrollmentapp.DatabaseHelper;

public class EnrollmentActivity extends AppCompatActivity {

    private ListView listViewSubjects;
    private DatabaseHelper databaseHelper;
    private List<String> subjectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_enrollment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listViewSubjects = findViewById(R.id.listViewSubjects);
        databaseHelper = new DatabaseHelper(this);

        subjectList = databaseHelper.getAllSubjects();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, subjectList);
        listViewSubjects.setAdapter(adapter);

        listViewSubjects.setOnItemClickListener((parent, view, position, id) -> {
            String selectedSubject = subjectList.get(position);
            boolean isEnrolled = databaseHelper.enrollSubject(selectedSubject);
            if (isEnrolled) {
                Toast.makeText(EnrollmentActivity.this, "Subject enrolled successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(EnrollmentActivity.this, "Credit limit exceeded or already enrolled", Toast.LENGTH_SHORT).show();
            }
        });



    }
}
