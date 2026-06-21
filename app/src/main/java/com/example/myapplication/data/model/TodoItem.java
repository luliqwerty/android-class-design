package com.example.myapplication.data.model;

import java.io.Serializable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "todo_items",
        foreignKeys = @ForeignKey(
                entity = TodoCategory.class,
                parentColumns = "id",
                childColumns = "category_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = @Index(value = "category_id")
)
public class TodoItem implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "due_date")
    private Long dueDate;

    @ColumnInfo(name = "category_id")
    private long categoryId;

    @ColumnInfo(name = "is_recurring")
    private boolean isRecurring;

    @ColumnInfo(name = "recurrence_interval_days")
    private int recurrenceIntervalDays;

    @ColumnInfo(name = "is_completed")
    private boolean isCompleted;

    @ColumnInfo(name = "is_my_day")
    private boolean isMyDay;

    @ColumnInfo(name = "is_important")
    private boolean isImportant;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    public TodoItem() {
    }

    @Ignore
    public TodoItem(String title, long categoryId) {
        this.title = title;
        this.categoryId = categoryId;
        this.isRecurring = false;
        this.recurrenceIntervalDays = 1;
        this.isCompleted = false;
        this.isMyDay = false;
        this.isImportant = false;
        this.createdAt = System.currentTimeMillis();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getDueDate() {
        return dueDate;
    }

    public void setDueDate(Long dueDate) {
        this.dueDate = dueDate;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }

    public int getRecurrenceIntervalDays() {
        return recurrenceIntervalDays;
    }

    public void setRecurrenceIntervalDays(int recurrenceIntervalDays) {
        this.recurrenceIntervalDays = recurrenceIntervalDays;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public boolean isMyDay() {
        return isMyDay;
    }

    public void setMyDay(boolean myDay) {
        isMyDay = myDay;
    }

    public boolean isImportant() {
        return isImportant;
    }

    public void setImportant(boolean important) {
        isImportant = important;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
