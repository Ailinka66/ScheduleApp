package com.example.scheduleapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Название базы данных и таблицы
    private static final String DATABASE_NAME = "schedule_db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_LESSONS = "lessons";

    // Названия колонок в таблице
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_SUBJECT = "subject";
    private static final String COLUMN_TEACHER = "teacher";
    private static final String COLUMN_ROOM = "room";
    private static final String COLUMN_DAY_OF_WEEK = "day_of_week";
    private static final String COLUMN_START_TIME = "start_time";
    private static final String COLUMN_END_TIME = "end_time";

    // SQL запрос для создания таблицы
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_LESSONS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_SUBJECT + " TEXT,"
            + COLUMN_TEACHER + " TEXT,"
            + COLUMN_ROOM + " TEXT,"
            + COLUMN_DAY_OF_WEEK + " TEXT,"
            + COLUMN_START_TIME + " TEXT,"
            + COLUMN_END_TIME + " TEXT" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LESSONS);
        onCreate(db);
    }

    // --- МЕТОДЫ ДЛЯ РАБОТЫ С ДАННЫМИ (CRUD) ---

    // 1. CREATE: Добавить новую пару
    public long addLesson(Lesson lesson) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SUBJECT, lesson.getSubject());
        values.put(COLUMN_TEACHER, lesson.getTeacher());
        values.put(COLUMN_ROOM, lesson.getRoom());
        values.put(COLUMN_DAY_OF_WEEK, lesson.getDayOfWeek());
        values.put(COLUMN_START_TIME, lesson.getStartTime());
        values.put(COLUMN_END_TIME, lesson.getEndTime());

        long id = db.insert(TABLE_LESSONS, null, values);
        db.close();
        return id;
    }

    // 2. READ: Получить список всех пар
    public List<Lesson> getAllLessons() {
        List<Lesson> lessons = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_LESSONS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Lesson lesson = new Lesson(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBJECT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEACHER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROOM)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAY_OF_WEEK)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_TIME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_TIME))
                );
                lessons.add(lesson);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lessons;
    }

    // 3. READ: Получить одну пару по ID
    public Lesson getLesson(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_LESSONS,
                new String[]{COLUMN_ID, COLUMN_SUBJECT, COLUMN_TEACHER, COLUMN_ROOM, COLUMN_DAY_OF_WEEK, COLUMN_START_TIME, COLUMN_END_TIME},
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);

        Lesson lesson = null;
        if (cursor != null && cursor.moveToFirst()) {
            lesson = new Lesson(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBJECT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEACHER)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROOM)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAY_OF_WEEK)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_TIME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_TIME))
            );
            cursor.close();
        }
        db.close();
        return lesson;
    }

    // 4. UPDATE: Обновить пару
    public int updateLesson(Lesson lesson) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SUBJECT, lesson.getSubject());
        values.put(COLUMN_TEACHER, lesson.getTeacher());
        values.put(COLUMN_ROOM, lesson.getRoom());
        values.put(COLUMN_DAY_OF_WEEK, lesson.getDayOfWeek());
        values.put(COLUMN_START_TIME, lesson.getStartTime());
        values.put(COLUMN_END_TIME, lesson.getEndTime());

        int rowsAffected = db.update(TABLE_LESSONS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(lesson.getId())});
        db.close();
        return rowsAffected;
    }

    // 5. DELETE: Удалить пару
    public void deleteLesson(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LESSONS, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    // 6. DELETE ALL: Удалить все записи (для настроек)
    public void deleteAllLessons() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_LESSONS);
        db.close();
    }
}