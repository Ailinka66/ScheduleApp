package com.example.scheduleapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddEditActivity extends AppCompatActivity {

    private EditText etSubject, etTeacher, etRoom, etStartTime, etEndTime;
    private Spinner spinnerDay, spinnerType; // <-- Добавили spinnerType
    private Button btnSave;
    private DatabaseHelper dbHelper;

    private final String[] daysOfWeek = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"};

    // Массив типов занятий
    private final String[] lessonTypes = {
            "Лекция",
            "Практическое занятие",
            "Лабораторная работа",
            "Семинар",
            "Зачёт",
            "Экзамен",
            "Консультация",
            "Факультатив",
            "Курсовая работа"
    };

    // Переменные для хранения данных при редактировании
    private boolean isEditMode = false;
    private int editId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 1. Проверяем сохраненную тему
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        // Инициализация полей
        etSubject = findViewById(R.id.etSubject);
        etTeacher = findViewById(R.id.etTeacher);
        etRoom = findViewById(R.id.etRoom);
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime = findViewById(R.id.etEndTime);
        spinnerDay = findViewById(R.id.spinnerDay);
        spinnerType = findViewById(R.id.spinnerType); // <-- Находим новый спиннер
        btnSave = findViewById(R.id.btnSave);

        dbHelper = new DatabaseHelper(this);

        // Настройка Spinner дней
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, daysOfWeek);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(dayAdapter);

        // Настройка Spinner типов
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lessonTypes);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

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
            int dayPosition = dayAdapter.getPosition(dayFromDb);
            if (dayPosition >= 0) {
                spinnerDay.setSelection(dayPosition);
            }

            // Устанавливаем тип занятия в Spinner
            String typeFromDb = intent.getStringExtra("edit_type");
            int typePosition = typeAdapter.getPosition(typeFromDb);
            if (typePosition >= 0) {
                spinnerType.setSelection(typePosition);
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
        String lessonType = spinnerType.getSelectedItem().toString(); // <-- Получаем выбранный тип

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

        if (!isValidTime(startTime)) {
            etStartTime.setError("Некорректное время (формат ЧЧ:ММ, часы 0-23, минуты 0-59)");
            etStartTime.requestFocus();
            return;
        }

        // ПРОВЕРКА ВРЕМЕНИ КОНЦА
        if (!isValidTime(endTime)) {
            etEndTime.setError("Некорректное время (формат ЧЧ:ММ, часы 0-23, минуты 0-59)");
            etEndTime.requestFocus();
            return;
        }

        // ДОПОЛНИТЕЛЬНО: Проверка, что время конца позже времени начала
        if (startTime.compareTo(endTime) >= 0) {
            Toast.makeText(this, "Время конца должно быть позже времени начала!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isEditMode) {
            // РЕЖИМ ОБНОВЛЕНИЯ (UPDATE) - используем конструктор с 9 параметрами
            Lesson updatedLesson = new Lesson(editId, subject, teacher, room, dayOfWeek, startTime, endTime, lessonType);
            int rowsAffected = dbHelper.updateLesson(updatedLesson);

            if (rowsAffected > 0) {
                Toast.makeText(this, "Пара обновлена!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Ошибка обновления", Toast.LENGTH_SHORT).show();
            }
        } else {
            // РЕЖИМ СОЗДАНИЯ (CREATE) - используем конструктор с 8 параметрами
            Lesson newLesson = new Lesson(subject, teacher, room, dayOfWeek, startTime, endTime, lessonType);
            long id = dbHelper.addLesson(newLesson);

            if (id != -1) {
                Toast.makeText(this, "Пара сохранена!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Проверяет, является ли строка корректным временем в формате ЧЧ:ММ
     */
    private boolean isValidTime(String timeString) {
        if (timeString == null || !timeString.matches("\\d{2}:\\d{2}")) {
            return false;
        }

        try {
            String[] parts = timeString.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            // Часы от 0 до 23, минуты от 0 до 59
            return (hour >= 0 && hour <= 23) && (minute >= 0 && minute <= 59);
        } catch (NumberFormatException e) {
            return false;
        }
    }
} // <-- Закрывающая скобка класса