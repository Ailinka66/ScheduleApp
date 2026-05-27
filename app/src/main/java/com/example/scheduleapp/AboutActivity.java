package com.example.scheduleapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

public class AboutActivity extends AppCompatActivity {

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

        // 3. Только после этого вызываем super.onCreate()
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Настройка Toolbar (шапки), если он есть в верстке activity_about.xml
        // Если в activity_about.xml нет Toolbar, эти 3 строки можно удалить
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("О приложении");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Кнопка "Назад"
        }
    }
}