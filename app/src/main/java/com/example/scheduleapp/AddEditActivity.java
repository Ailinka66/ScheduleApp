package com.example.scheduleapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class AddEditActivity extends AppCompatActivity {

    private EditText etSubject, etTeacher, etRoom, etStartTime, etEndTime;
    private Spinner spinnerDay;
    private Button btnSave;
    private DatabaseHelper dbHelper;

    private String[] daysOfWeek = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"};

    // Переменные для хранения данных при редактировании
    private boolean isEditMode = false;
    private int editId = -1;

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
        setContentView(R.layout.activity_add_edit);

        // Инициализация полей
        etSubject = findViewById(R.id.etSubject);
        etTeacher = findViewById(R.id.etTeacher);
        etRoom = findViewById(R.id.etRoom);
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime = findViewById(R.id.etEndTime);
        spinnerDay = findViewById(R.id.spinnerDay);
        btnSave = findViewById(R.id.btnSave);

        dbHelper = new DatabaseHelper(this);

        // Настройка Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, daysOfWeek);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(adapter);

        // Проверяем, пришли ли мы в режиме редактирования
        Intent intent = getIntent();
        isEditMode = intent.getBooleanExtra("is_edit_mode", false);

        if (isEditMode) {
            // Если режим редактирования, меняем текст кнопки и заполняем поля старыми данными
            btnSave.setText("Обновить пару");

            editId = intent.getIntExtra("edit_id", -1);
            etSubject.setText(intent.getStringExtra("edit_subject"));
            etTeacher.setText(intent.getStringExtra("edit_teacher"));
            etRoom.setText(intent.getStringExtra("edit_room"));
            etStartTime.setText(intent.getStringExtra("edit_start"));
            etEndTime.setText(intent.getStringExtra("edit_end"));

            // Устанавливаем день недели в Spinner
            String dayFromDb = intent.getStringExtra("edit_day");
            int position = adapter.getPosition(dayFromDb);
            if (position >= 0) {
                spinnerDay.setSelection(position);
            }
        }

        // Обработка нажатия кнопки "Сохранить/Обновить"
        btnSave.setOnClickListener(v -> saveOrUpdateLesson());
    }

    private void saveOrUpdateLesson() {
        String subject = etSubject.getText().toString().trim();
        String teacher = etTeacher.getText().toString().trim();
        String room = etRoom.getText().toString().trim();
        String startTime = etStartTime.getText().toString().trim();
        String endTime = etEndTime.getText().toString().trim();
        String dayOfWeek = spinnerDay.getSelectedItem().toString();

        // ПРОВЕРКА ВВОДА
        if (subject.isEmpty()) {
            etSubject.setError("Введите название предмета");
            return;
        }
        if (teacher.isEmpty()) {
            etTeacher.setError("Введите ФИО преподавателя");
            return;
        }
        if (room.isEmpty()) {
            etRoom.setError("Введите номер аудитории");
            return;
        }
        if (startTime.isEmpty() || !startTime.matches("\\d{2}:\\d{2}")) {
            etStartTime.setError("Формат ЧЧ:ММ");
            return;
        }
        if (endTime.isEmpty() || !endTime.matches("\\d{2}:\\d{2}")) {
            etEndTime.setError("Формат ЧЧ:ММ");
            return;
        }

        if (isEditMode) {
            // РЕЖИМ ОБНОВЛЕНИЯ (UPDATE)
            Lesson updatedLesson = new Lesson(editId, subject, teacher, room, dayOfWeek, startTime, endTime);
            int rowsAffected = dbHelper.updateLesson(updatedLesson);

            if (rowsAffected > 0) {
                Toast.makeText(this, "Пара обновлена!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Ошибка обновления", Toast.LENGTH_SHORT).show();
            }
        } else {
            // РЕЖИМ СОЗДАНИЯ (CREATE)
            Lesson newLesson = new Lesson(subject, teacher, room, dayOfWeek, startTime, endTime);
            long id = dbHelper.addLesson(newLesson);

            if (id != -1) {
                Toast.makeText(this, "Пара сохранена!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
            }
        }
    }
}