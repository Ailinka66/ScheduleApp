package com.example.scheduleapp;

import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LessonAdapter adapter;
    private List<Lesson> lessonList;
    private FloatingActionButton fabAdd;
    private LinearLayout emptyStateView;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Проверяем сохраненную тему
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        // Инициализация компонентов
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
        Map<String, Integer> dayOrder = new HashMap<>();
        dayOrder.put("Понедельник", 1);
        dayOrder.put("Вторник", 2);
        dayOrder.put("Среда", 3);
        dayOrder.put("Четверг", 4);
        dayOrder.put("Пятница", 5);
        dayOrder.put("Суббота", 6);
        dayOrder.put("Воскресенье", 7);

        lessons.sort((l1, l2) -> {
            String day1Str = l1.getDayOfWeek();
            String day2Str = l2.getDayOfWeek();

            // Безопасное получение значения
            int day1 = (day1Str != null) ? dayOrder.getOrDefault(day1Str, 99) : 99;
            int day2 = (day2Str != null) ? dayOrder.getOrDefault(day2Str, 99) : 99;

            if (day1 != day2) {
                return Integer.compare(day1, day2);
            }

            // Аналогично для времени
            String time1 = l1.getStartTime();
            String time2 = l2.getStartTime();

            if (time1 != null && time2 != null) {
                return time1.compareTo(time2);
            }

            return 0;
        });
    }
}