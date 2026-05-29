package com.example.scheduleapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {

    private androidx.appcompat.widget.SwitchCompat switchTheme;
    private Button btnClearData;
    private DatabaseHelper dbHelper;

    // Ключ для сохранения настройки темы
    private static final String PREFS_NAME = "AppSettings";
    private static final String KEY_THEME = "isDarkMode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 1. Применяем тему ДО создания активности (чтобы не мерцало)
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("isDarkMode", false);

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 2. Инициализация компонентов
        dbHelper = new DatabaseHelper(this);
        switchTheme = findViewById(R.id.switchTheme);
        btnClearData = findViewById(R.id.btnClearData);

        // Проверка на null, чтобы избежать краша, если элемент не найден
        if (switchTheme == null || btnClearData == null) {
            Toast.makeText(this, "Ошибка верстки: элементы не найдены", Toast.LENGTH_LONG).show();
            return;
        }

        // 3. Настройка Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Настройки");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 4. Устанавливаем начальное положение переключателя БЕЗ триггера слушателя
        switchTheme.setOnCheckedChangeListener(null); // Отключаем слушатель
        switchTheme.setChecked(isDarkMode);           // Ставим галочку

        // 5. Включаем слушатель для будущих изменений
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveThemePreference(isChecked);
            applyTheme(isChecked);
        });

        // 6. Логика очистки данных
        btnClearData.setOnClickListener(v -> showClearDataDialog());
    }

    // Метод загрузки сохраненной темы
    private void loadThemePreference() {
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("isDarkMode", false);

        // Просто устанавливаем положение переключателя БЕЗ вызова логики применения темы
        // Чтобы не триггерить OnCheckedChangeListener лишний раз
        switchTheme.setOnCheckedChangeListener(null); // Временно отключаем слушатель
        switchTheme.setChecked(isDarkMode);           // Ставим галочку
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveThemePreference(isChecked);
            applyTheme(isChecked);
        });                                           // Включаем слушатель обратно

        // Применяем тему сразу при запуске экрана настроек, чтобы фон соответствовал
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    // Метод сохранения темы
    private void saveThemePreference(boolean isDarkMode) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_THEME, isDarkMode);
        editor.apply();
    }

    // Метод применения темы
    private void applyTheme(boolean isDarkMode) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Не вызываем recreate() здесь!
        // Тема применится глобально. При возврате на главный экран он уже будет в новой теме.
        String message = isDarkMode ? "Темная тема включена" : "Светлая тема включена";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Диалог подтверждения очистки
    private void showClearDataDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Очистка данных")
                .setMessage("Вы уверены? Все записи расписания будут удалены безвозвратно.")
                .setPositiveButton("Да", (dialog, which) -> {
                    // Вызываем метод удаления всех записей из DatabaseHelper
                    // (Тебе нужно будет добавить этот метод в DatabaseHelper, см. ниже!)
                    dbHelper.deleteAllLessons();

                    Toast.makeText(this, "Все данные удалены", Toast.LENGTH_SHORT).show();
                    finish(); // Закрываем экран настроек
                })
                .setNegativeButton("Нет", null)
                .show();
    }
}