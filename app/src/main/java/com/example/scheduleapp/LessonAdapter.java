package com.example.scheduleapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {

    private Context context;
    private List<Lesson> lessonList;
    private OnItemClickListener listener;

    // Интерфейс для обработки кликов по элементу
    public interface OnItemClickListener {
        void onItemClick(Lesson lesson);
    }

    public LessonAdapter(Context context, List<Lesson> lessonList, OnItemClickListener listener) {
        this.context = context;
        this.lessonList = lessonList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Загружаем разметку item_lesson.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_lesson, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        Lesson currentLesson = lessonList.get(position);

        // Заполняем поля данными из объекта Lesson
        holder.tvSubject.setText(currentLesson.getSubject());
        holder.tvTime.setText(currentLesson.getStartTime() + " - " + currentLesson.getEndTime());
        holder.tvTeacher.setText(currentLesson.getTeacher());
        holder.tvRoom.setText("Ауд. " + currentLesson.getRoom());
        holder.tvDay.setText(currentLesson.getDayOfWeek());

        // Обработка клика по элементу
        holder.itemView.setOnClickListener(v -> listener.onItemClick(currentLesson));
    }

    @Override
    public int getItemCount() {
        return lessonList.size();
    }

    // Класс ViewHolder (хранит ссылки на элементы внутри одной карточки)
    public static class LessonViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubject, tvTime, tvTeacher, tvRoom, tvDay;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvTeacher = itemView.findViewById(R.id.tvTeacher);
            tvRoom = itemView.findViewById(R.id.tvRoom);
            tvDay = itemView.findViewById(R.id.tvDay);
        }
    }
}