package com.example.scheduleapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;

public class DetailActivity extends AppCompatActivity {

    private TextView tvSubject, tvTime, tvTeacher, tvRoom, tvDay, tvType;
    private ImageView ivRandomImg;
    private DatabaseHelper dbHelper;
    private int lessonId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 1. Проверяем сохраненную тему
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        // Инициализация компонентов
        tvSubject = findViewById(R.id.tvDetailSubject);
        tvTime = findViewById(R.id.tvDetailTime);
        tvTeacher = findViewById(R.id.tvDetailTeacher);
        tvRoom = findViewById(R.id.tvDetailRoom);
        tvDay = findViewById(R.id.tvDetailDay);
        tvType = findViewById(R.id.tvDetailType);
        ivRandomImg = findViewById(R.id.ivRandomImg);

        Button btnDelete = findViewById(R.id.btnDelete);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnEdit = findViewById(R.id.btnEdit);

        dbHelper = new DatabaseHelper(this);

        // Получаем ID пары из Intent
        Intent intent = getIntent();
        lessonId = intent.getIntExtra("lesson_id", -1);

        if (lessonId != -1) {
            loadLessonDetails(lessonId);
            setRandomImage();
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
            tvTime.setText(getString(R.string.time_format,
                    lesson.getStartTime(), lesson.getEndTime()));
            tvTeacher.setText(getString(R.string.label_teacher, lesson.getTeacher()));
            tvRoom.setText(getString(R.string.label_room, lesson.getRoom()));
            tvDay.setText(getString(R.string.labell_day, lesson.getDayOfWeek()));
            tvType.setText(getString(R.string.label_type, lesson.getLessonType()));
        }
    }

    // Метод открытия экрана редактирования
    private void openEditScreen() {
        Lesson currentLesson = dbHelper.getLesson(lessonId);
        if (currentLesson != null) {
            Intent intent = new Intent(DetailActivity.this, AddEditActivity.class);

            intent.putExtra("is_edit_mode", true);
            intent.putExtra("edit_id", lessonId);
            intent.putExtra("edit_subject", currentLesson.getSubject());
            intent.putExtra("edit_teacher", currentLesson.getTeacher());
            intent.putExtra("edit_room", currentLesson.getRoom());
            intent.putExtra("edit_day", currentLesson.getDayOfWeek());
            intent.putExtra("edit_start", currentLesson.getStartTime());
            intent.putExtra("edit_end", currentLesson.getEndTime());
            intent.putExtra("edit_type", currentLesson.getLessonType()); // <-- ДОБАВИТЬ ЭТУ СТРОКУ

            startActivity(intent);
            finish();
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
    @Override
    protected void onResume() {
        super.onResume();
        // Перезагружаем данные пары из базы, чтобы отобразить актуальные изменения
        if (lessonId != -1) {
            loadLessonDetails(lessonId);
        }
    }

    // Метод установки случайной картинки
    private void setRandomImage() {
        // Массив из твоих картинок (убедись, что они есть в папке drawable!)
        int[] images = new int[] {
                R.drawable.p1, R.drawable.p2, R.drawable.p3,
                R.drawable.p4, R.drawable.p5, R.drawable.p6,
                R.drawable.p7, R.drawable.p8, R.drawable.p9,
                R.drawable.p10, R.drawable.p11, R.drawable.p12,
                R.drawable.p13, R.drawable.p14, R.drawable.p15,
                R.drawable.p16, R.drawable.p17,
                R.drawable.p18, R.drawable.p19, R.drawable.p20, R.drawable.p21,
                R.drawable.p22, R.drawable.p23,
                R.drawable.p24, R.drawable.p25, R.drawable.p26, R.drawable.p27,
        };

        // Выбираем случайную
        int randomIndex = new java.util.Random().nextInt(images.length);

        // Устанавливаем её
        ivRandomImg.setImageResource(images[randomIndex]);
    }
}