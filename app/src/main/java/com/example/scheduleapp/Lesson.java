package com.example.scheduleapp;

public class Lesson {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_LESSON = 1;

    private int id;
    private final String subject;
    private final String teacher;
    private final String room;
    private final String dayOfWeek;
    private final String startTime;
    private final String endTime;
    private final String lessonType;
    private final int type;

    // 1. Конструктор для СОЗДАНИЯ новой пары (8 параметров)
    public Lesson(String subject, String teacher, String room, String dayOfWeek, String startTime, String endTime, String lessonType) {
        this.subject = subject;
        this.teacher = teacher;
        this.room = room;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.lessonType = lessonType;
        this.type = TYPE_LESSON;
        this.id = -1;
    }

    // 2. Конструктор для РЕДАКТИРОВАНИЯ/БД (9 параметров)
    public Lesson(int id, String subject, String teacher, String room, String dayOfWeek, String startTime, String endTime, String lessonType) {
        this.id = id;
        this.subject = subject;
        this.teacher = teacher;
        this.room = room;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.lessonType = lessonType;
        this.type = TYPE_LESSON;
    }

    // 3. Конструктор для ЗАГОЛОВКА
    public Lesson(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        this.type = TYPE_HEADER;
        this.subject = "";
        this.teacher = "";
        this.room = "";
        this.startTime = "";
        this.endTime = "";
        this.lessonType = "";
        this.id = -1;
    }

    // Геттеры и сеттеры
    public String getLessonType() { return lessonType; }

    public int getType() { return type; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getSubject() { return subject; }
    public String getTeacher() { return teacher; }
    public String getRoom() { return room; }
    public String getDayOfWeek() { return dayOfWeek; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
}