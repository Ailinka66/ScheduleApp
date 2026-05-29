package com.example.scheduleapp;

public class Lesson {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_LESSON = 1;

    private int id;
    private String subject;
    private String teacher;
    private String room;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private String lessonType; // <-- Новое поле
    private int type;

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
    public void setLessonType(String lessonType) { this.lessonType = lessonType; }

    public int getType() { return type; }
    public void setType(int type) { this.type = type; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getTeacher() { return teacher; }
    public void setTeacher(String teacher) { this.teacher = teacher; }
    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}