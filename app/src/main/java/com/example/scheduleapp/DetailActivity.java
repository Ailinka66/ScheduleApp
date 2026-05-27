package com.example.scheduleapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class DetailActivity extends AppCompatActivity {

    private TextView tvSubject, tvTime, tvTeacher, tvRoom, tvDay;
    private Button btnDelete, btnBack, btnEdit; // Добавили btnEdit
    private DatabaseHelper dbHelper;
    private int lessonId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 1. Проверяем сохраненную тему
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("isDarkMode", false);

        // 2. Применяем тему ДО создания активности
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Инициализация компонентов
        tvSubject = findViewById(R.id.tvDetailSubject);
        tvTime = findViewById(R.id.tvDetailTime);
        tvTeacher = findViewById(R.id.tvDetailTeacher);
        tvRoom = findViewById(R.id.tvDetailRoom);
        tvDay = findViewById(R.id.tvDetailDay);

        btnDelete = findViewById(R.id.btnDelete);
        btnBack = findViewById(R.id.btnBack);
        btnEdit = findViewById(R.id.btnEdit); // Находим кнопку

        dbHelper = new DatabaseHelper(this);

        // Получаем ID пары из Intent
        Intent intent = getIntent();
        lessonId = intent.getIntExtra("lesson_id", -1);

        if (lessonId != -1) {
            loadLessonDetails(lessonId);
        } else {
            Toast.makeText(this, "Ошибка: пара не найдена", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Кнопка Назад
        btnBack.setOnClickListener(v -> finish());

        // Кнопка Удалить
        btnDelete.setOnClickListener(v -> showDeleteConfirmation());

        // Кнопка Изменить
        btnEdit.setOnClickListener(v -> openEditScreen());
    }

    private void loadLessonDetails(int id) {
        Lesson lesson = dbHelper.getLesson(id);
        if (lesson != null) {
            tvSubject.setText(lesson.getSubject());
            tvTime.setText(lesson.getStartTime() + " - " + lesson.getEndTime());
            tvTeacher.setText("Преподаватель: " + lesson.getTeacher());
            tvRoom.setText("Аудитория: " + lesson.getRoom());
            tvDay.setText("День: " + lesson.getDayOfWeek());
        }
    }

    // Метод открытия экрана редактирования
    private void openEditScreen() {
        Lesson currentLesson = dbHelper.getLesson(lessonId);
        if (currentLesson != null) {
            Intent intent = new Intent(DetailActivity.this, AddEditActivity.class);

            // Передаем все данные старой пары в AddEditActivity
            intent.putExtra("is_edit_mode", true); // Флаг, что мы в режиме редактирования
            intent.putExtra("edit_id", lessonId);
            intent.putExtra("edit_subject", currentLesson.getSubject());
            intent.putExtra("edit_teacher", currentLesson.getTeacher());
            intent.putExtra("edit_room", currentLesson.getRoom());
            intent.putExtra("edit_day", currentLesson.getDayOfWeek());
            intent.putExtra("edit_start", currentLesson.getStartTime());
            intent.putExtra("edit_end", currentLesson.getEndTime());

            startActivity(intent);
            finish(); // Закрываем экран деталей, чтобы не дублировать их в стеке
        }
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Удаление пары")
                .setMessage("Вы уверены, что хотите удалить эту пару из расписания?")
                .setPositiveButton("Да", (dialog, which) -> {
                    dbHelper.deleteLesson(lessonId);
                    Toast.makeText(this, "Пара удалена", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Нет", null)
                .show();
    }
}