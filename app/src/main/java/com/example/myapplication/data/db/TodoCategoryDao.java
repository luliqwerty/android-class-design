package com.example.myapplication.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.data.model.TodoCategory;

import java.util.List;

@Dao
public interface TodoCategoryDao {

    @Query("SELECT * FROM todo_categories ORDER BY id ASC")
    LiveData<List<TodoCategory>> getAllCategories();

    @Query("SELECT * FROM todo_categories WHERE id = :id")
    LiveData<TodoCategory> getCategoryById(long id);

    @Insert
    long insert(TodoCategory category);

    @Update
    void update(TodoCategory category);

    @Delete
    void delete(TodoCategory category);

    @Query("DELETE FROM todo_categories WHERE id = :id")
    void deleteById(long id);
}
