package com.example.scheduleapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Lesson> lessonList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Lesson lesson);
    }

    public LessonAdapter(Context context, List<Lesson> lessonList, OnItemClickListener listener) {
        this.context = context;
        this.lessonList = lessonList;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return lessonList.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Lesson.TYPE_HEADER) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_lesson, parent, false);
            return new LessonViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Lesson currentLesson = lessonList.get(position);

        if (currentLesson.getType() == Lesson.TYPE_HEADER) {
            // Заполняем заголовок
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.tvHeaderDay.setText(currentLesson.getDayOfWeek());
        } else {
            // Заполняем карточку пары
            LessonViewHolder lessonHolder = (LessonViewHolder) holder;
            lessonHolder.tvSubject.setText(currentLesson.getSubject());
            lessonHolder.tvTime.setText(currentLesson.getStartTime() + " - " + currentLesson.getEndTime());
            lessonHolder.tvTeacher.setText(currentLesson.getTeacher());
            lessonHolder.tvRoom.setText("Ауд. " + currentLesson.getRoom());

            // Скрываем день недели в карточке, так как он уже есть в заголовке сверху
            lessonHolder.tvDay.setVisibility(View.GONE);

            lessonHolder.itemView.setOnClickListener(v -> listener.onItemClick(currentLesson));
        }
    }

    @Override
    public int getItemCount() {
        return lessonList.size();
    }

    // ViewHolder для заголовка
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeaderDay;
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeaderDay = itemView.findViewById(R.id.tvHeaderDay);
        }
    }

    // ViewHolder для пары
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