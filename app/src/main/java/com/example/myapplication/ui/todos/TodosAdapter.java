package com.example.myapplication.ui.todos;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.model.TodoItem;

import java.util.Calendar;
import java.util.Objects;

public class TodosAdapter extends ListAdapter<TodoItem, TodosAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(TodoItem item);
        void onCompleteToggle(TodoItem item, boolean completed);
    }

    private final OnItemClickListener listener;

    public TodosAdapter(OnItemClickListener listener) {
        super(new DiffUtil.ItemCallback<TodoItem>() {
            @Override
            public boolean areItemsTheSame(@NonNull TodoItem oldItem, @NonNull TodoItem newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull TodoItem oldItem, @NonNull TodoItem newItem) {
                return Objects.equals(oldItem.getTitle(), newItem.getTitle())
                        && oldItem.isCompleted() == newItem.isCompleted()
                        && Objects.equals(oldItem.getDueDate(), newItem.getDueDate())
                        && oldItem.isImportant() == newItem.isImportant()
                        && oldItem.isRecurring() == newItem.isRecurring();
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_todo, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TodoItem item = getItem(position);
        holder.bind(item, listener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final CheckBox checkComplete;
        final TextView title;
        final TextView dueDate;
        final TextView recurring;
        final View important;

        ViewHolder(View v) {
            super(v);
            checkComplete = v.findViewById(R.id.check_complete);
            title = v.findViewById(R.id.text_todo_title);
            dueDate = v.findViewById(R.id.text_due_date);
            recurring = v.findViewById(R.id.text_recurring);
            important = v.findViewById(R.id.img_important);
        }

        void bind(final TodoItem item, final OnItemClickListener listener) {
            title.setText(item.getTitle());
            checkComplete.setChecked(item.isCompleted());

            if (item.isCompleted()) {
                title.setPaintFlags(title.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                title.setAlpha(0.5f);
            } else {
                title.setPaintFlags(title.getPaintFlags() & ~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                title.setAlpha(1f);
            }

            if (item.getDueDate() != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(item.getDueDate());
                dueDate.setText(DateFormat.format("yyyy-MM-dd", cal));
                dueDate.setVisibility(View.VISIBLE);
            } else {
                dueDate.setVisibility(View.GONE);
            }

            if (item.isRecurring()) {
                recurring.setVisibility(View.VISIBLE);
                recurring.setText("每" + item.getRecurrenceIntervalDays() + "天");
            } else {
                recurring.setVisibility(View.GONE);
            }

            important.setVisibility(item.isImportant() ? View.VISIBLE : View.GONE);

            checkComplete.setOnClickListener(v ->
                    listener.onCompleteToggle(item, checkComplete.isChecked()));

            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}
