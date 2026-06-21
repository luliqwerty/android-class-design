package com.example.myapplication.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.data.model.TodoItem;

import java.util.List;

@Dao
public interface TodoItemDao {

    @Query("SELECT * FROM todo_items WHERE category_id = :categoryId ORDER BY is_completed ASC, created_at DESC")
    LiveData<List<TodoItem>> getTodosByCategory(long categoryId);

    @Query("SELECT * FROM todo_items WHERE category_id = :categoryId AND is_completed = 0 ORDER BY created_at DESC")
    LiveData<List<TodoItem>> getIncompleteTodosByCategory(long categoryId);

    @Query("SELECT * FROM todo_items ORDER BY is_completed ASC, created_at DESC")
    LiveData<List<TodoItem>> getAllTodos();

    @Query("SELECT * FROM todo_items WHERE is_completed = 0 ORDER BY created_at DESC")
    LiveData<List<TodoItem>> getAllIncompleteTodos();

    @Query("SELECT * FROM todo_items WHERE is_my_day = 1 ORDER BY is_completed ASC, created_at DESC")
    LiveData<List<TodoItem>> getMyDayTodos();

    @Query("SELECT * FROM todo_items WHERE is_important = 1 ORDER BY is_completed ASC, created_at DESC")
    LiveData<List<TodoItem>> getImportantTodos();

    @Query("SELECT * FROM todo_items WHERE due_date IS NOT NULL ORDER BY due_date ASC")
    LiveData<List<TodoItem>> getTodosSortedByDueDate();

    @Query("SELECT * FROM todo_items WHERE category_id = :categoryId AND due_date IS NOT NULL ORDER BY due_date ASC")
    LiveData<List<TodoItem>> getTodosByCategorySortedByDueDate(long categoryId);

    @Query("SELECT * FROM todo_items WHERE category_id = :categoryId AND is_completed = 0 AND due_date IS NOT NULL ORDER BY due_date ASC")
    LiveData<List<TodoItem>> getIncompleteTodosByCategorySortedByDueDate(long categoryId);

    @Insert
    long insert(TodoItem item);

    @Update
    void update(TodoItem item);

    @Delete
    void delete(TodoItem item);

    @Query("DELETE FROM todo_items WHERE id = :id")
    void deleteById(long id);
}
