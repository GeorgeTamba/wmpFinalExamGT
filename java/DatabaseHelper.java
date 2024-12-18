package com.example.enrollmentapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "StudentEnrollment.db";
    private static final int DATABASE_VERSION = 1;

    // Table and columns
    private static final String TABLE_STUDENT = "student";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";

    private static final String TABLE_SUBJECT = "subject";
    private static final String COLUMN_SUBJECT_ID = "subject_id";
    private static final String COLUMN_SUBJECT_NAME = "subject_name";
    private static final String COLUMN_CREDITS = "credits";

    private static final String TABLE_ENROLLMENT = "enrollment";
    private static final String COLUMN_ENROLLMENT_ID = "enrollment_id";
    private static final String COLUMN_TOTAL_CREDITS = "total_credits";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_STUDENT_TABLE = "CREATE TABLE " + TABLE_STUDENT + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_EMAIL + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT)";
        db.execSQL(CREATE_STUDENT_TABLE);

        String CREATE_SUBJECT_TABLE = "CREATE TABLE " + TABLE_SUBJECT + " (" +
                COLUMN_SUBJECT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SUBJECT_NAME + " TEXT, " +
                COLUMN_CREDITS + " INTEGER)";
        db.execSQL(CREATE_SUBJECT_TABLE);

        String CREATE_ENROLLMENT_TABLE = "CREATE TABLE " + TABLE_ENROLLMENT + " (" +
                COLUMN_ENROLLMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ID + " INTEGER, " +
                COLUMN_SUBJECT_ID + " INTEGER, " +
                COLUMN_TOTAL_CREDITS + " INTEGER, " +
                "FOREIGN KEY (" + COLUMN_ID + ") REFERENCES " + TABLE_STUDENT + " (" + COLUMN_ID + "), " +
                "FOREIGN KEY (" + COLUMN_SUBJECT_ID + ") REFERENCES " + TABLE_SUBJECT + " (" + COLUMN_SUBJECT_ID + "))";
        db.execSQL(CREATE_ENROLLMENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENROLLMENT);
        onCreate(db);
    }

    public boolean validateLogin(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM student WHERE email = ? AND password = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    public List<String> getAllSubjects() {
        List<String> subjects = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT subject_name FROM subject", null);
        while (cursor.moveToNext()) {
            subjects.add(cursor.getString(0));
        }
        cursor.close();
        return subjects;
    }

    public boolean enrollSubject(String subjectName) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Get subject credits
        Cursor cursor = db.rawQuery("SELECT credits FROM subject WHERE subject_name = ?", new String[]{subjectName});
        if (cursor.moveToFirst()) {
            int subjectCredits = cursor.getInt(0);

            // Calculate total credits
            Cursor totalCursor = db.rawQuery("SELECT SUM(credits) FROM enrollment", null);
            int totalCredits = 0;
            if (totalCursor.moveToFirst()) {
                totalCredits = totalCursor.getInt(0);
            }
            totalCursor.close();

            if (totalCredits + subjectCredits <= 24) {
                // Enroll subject
                ContentValues values = new ContentValues();
                values.put("subject_name", subjectName);
                db.insert("enrollment", null, values);
                cursor.close();
                return true;
            }
        }
        cursor.close();
        return false;
    }

    public List<String> getEnrolledSubjects() {
        List<String> enrolledSubjects = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT subject_name FROM enrollment e " +
                "INNER JOIN subject s ON e.subject_id = s.subject_id";
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            enrolledSubjects.add(cursor.getString(0)); // Subject name
        }
        cursor.close();
        return enrolledSubjects;
    }

    public int getTotalCredits() {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT SUM(s.credits) FROM enrollment e " +
                "INNER JOIN subject s ON e.subject_id = s.subject_id";
        Cursor cursor = db.rawQuery(query, null);

        int totalCredits = 0;
        if (cursor.moveToFirst()) {
            totalCredits = cursor.getInt(0);
        }
        cursor.close();
        return totalCredits;
    }

    public long addStudent(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);

        // Insert the student and return the row ID (or -1 if an error occurred)
        return db.insert(TABLE_STUDENT, null, values);
    }


    // Add your CRUD operations for student registration, login, subject selection, and enrollment.
}
