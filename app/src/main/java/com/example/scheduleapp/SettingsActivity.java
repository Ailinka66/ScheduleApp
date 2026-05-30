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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 1. Применяем тему ДО создания активности (чтобы не мерцало)
        ThemeUtils.applyTheme(this); // ✅ Заменили 10 строк на одну!

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

        // 4. Получаем текущее состояние темы для переключателя
        boolean isDarkMode = getSharedPreferences("AppSettings", MODE_PRIVATE)
                .getBoolean("isDarkMode", false);

        // 5. Устанавливаем начальное положение переключателя БЕЗ триггера слушателя
        switchTheme.setOnCheckedChangeListener(null); // Отключаем слушатель
        switchTheme.setChecked(isDarkMode);           // Ставим галочку

        // 6. Включаем слушатель для будущих изменений
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveThemePreference(isChecked);
            applyTheme(isChecked);
        });

        // 7. Логика очистки данных
        btnClearData.setOnClickListener(v -> showClearDataDialog());
    }

    // Метод сохранения темы
    private void saveThemePreference(boolean isDarkMode) {
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isDarkMode", isDarkMode);
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
                    dbHelper.deleteAllLessons();
                    Toast.makeText(this, "Все данные удалены", Toast.LENGTH_SHORT).show();
                    finish(); // Закрываем экран настроек
                })
                .setNegativeButton("Нет", null)
                .show();
    }
}