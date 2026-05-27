package com.example.scheduleapp; // Проверь, совпадает ли имя пакета с твоим!

public class Lesson {
    private int id;
    private String subject;       // Название предмета
    private String teacher;       // ФИО преподавателя
    private String room;          // Номер аудитории
    private String dayOfWeek;     // День недели (Пн, Вт...)
    private String startTime;     // Время начала
    private String endTime;       // Время конца

    // Конструктор для создания новой записи (без ID, так как его присвоит база)
    public Lesson(String subject, String teacher, String room, String dayOfWeek, String startTime, String endTime) {
        this.subject = subject;
        this.teacher = teacher;
        this.room = room;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Конструктор для получения записи из базы (с ID)
    public Lesson(int id, String subject, String teacher, String room, String dayOfWeek, String startTime, String endTime) {
        this.id = id;
        this.subject = subject;
        this.teacher = teacher;
        this.room = room;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Геттеры и сеттеры
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