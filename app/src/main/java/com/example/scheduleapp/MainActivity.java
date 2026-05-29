package com.example.scheduleapp;

import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LessonAdapter adapter;
    private List<Lesson> lessonList;
    private DatabaseHelper dbHelper;
    private FloatingActionButton fabAdd;
    private LinearLayout emptyStateView;

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
        setContentView(R.layout.activity_main);

        // Инициализация компонентов
        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerViewLessons);
        fabAdd = findViewById(R.id.fabAddLesson);
        emptyStateView = findViewById(R.id.emptyStateView);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Настройка RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        lessonList = new ArrayList<>();

        // Создаем адаптер и передаем ему список и слушатель кликов
        adapter = new LessonAdapter(this, lessonList, this::openDetailActivity);
        recyclerView.setAdapter(adapter);

        // Загружаем данные из базы при запуске
        loadLessonsFromDatabase();

        // Обработка нажатия на кнопку "+"
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
            startActivity(intent);
        });
    }

    // Метод загрузки пар из базы данных
    private void loadLessonsFromDatabase() {
        lessonList.clear();

        // 1. Получаем все пары из базы
        List<Lesson> lessonsFromDb = dbHelper.getAllLessons();

        // 2. Сортируем их по дню и времени (используем метод из прошлого шага)
        sortLessonsByDayAndTime(lessonsFromDb);

        // 3. Группируем: добавляем заголовки перед каждой новой группой дней
        String lastDay = "";
        for (Lesson lesson : lessonsFromDb) {
            String currentDay = lesson.getDayOfWeek();

            // Если день изменился, добавляем заголовок
            if (!currentDay.equals(lastDay)) {
                lessonList.add(new Lesson(currentDay)); // Добавляем объект-заголовок
                lastDay = currentDay;
            }

            // Добавляем саму пару
            lessonList.add(lesson);
        }

        adapter.notifyDataSetChanged();

        // Логика пустого состояния
        if (lessonList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyStateView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateView.setVisibility(View.GONE);
        }
    }

    // Метод открытия экрана деталей
    private void openDetailActivity(Lesson lesson) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra("lesson_id", lesson.getId()); // Передаем ID выбранной пары
        startActivity(intent);
    }

    // Этот метод вызывается каждый раз, когда мы возвращаемся на главный экран
    // Например, после добавления или удаления пары
    @Override
    protected void onResume() {
        super.onResume();
        loadLessonsFromDatabase(); // Перезагружаем список, чтобы увидеть изменения
    }

    // Создание меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // Обработка нажатий на пункты меню
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            // Открываем экран настроек
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_about) {
            // Открываем экран "О программе"
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /**
     * Сортирует список пар по дням недели и времени начала
     */
    private void sortLessonsByDayAndTime(List<Lesson> lessons) {
        // Создаём карту для порядка дней недели
        java.util.Map<String, Integer> dayOrder = new java.util.HashMap<>();
        dayOrder.put("Понедельник", 1);
        dayOrder.put("Вторник", 2);
        dayOrder.put("Среда", 3);
        dayOrder.put("Четверг", 4);
        dayOrder.put("Пятница", 5);
        dayOrder.put("Суббота", 6);
        dayOrder.put("Воскресенье", 7);

        java.util.Collections.sort(lessons, new java.util.Comparator<Lesson>() {
            @Override
            public int compare(Lesson l1, Lesson l2) {
                // 1. Сначала сравниваем по дню недели
                int day1 = dayOrder.getOrDefault(l1.getDayOfWeek(), 99);
                int day2 = dayOrder.getOrDefault(l2.getDayOfWeek(), 99);

                if (day1 != day2) {
                    return Integer.compare(day1, day2);
                }

                // 2. Если дни одинаковые, сравниваем по времени начала
                return l1.getStartTime().compareTo(l2.getStartTime());
            }
        });
    }
}