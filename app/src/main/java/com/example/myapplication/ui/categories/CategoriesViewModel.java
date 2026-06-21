package com.example.myapplication.ui.categories;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myapplication.data.model.TodoCategory;
import com.example.myapplication.data.model.TodoItem;
import com.example.myapplication.data.repository.TodoRepository;

import java.util.List;

public class CategoriesViewModel extends AndroidViewModel {

    private final TodoRepository repository;
    private final LiveData<List<TodoCategory>> allCategories;
    private final LiveData<List<TodoItem>> myDayTodos;
    private final LiveData<List<TodoItem>> importantTodos;

    public CategoriesViewModel(@NonNull Application application) {
        super(application);
        repository = new TodoRepository(application);
        allCategories = repository.getAllCategories();
        myDayTodos = repository.getMyDayTodos();
        importantTodos = repository.getImportantTodos();
    }

    public LiveData<List<TodoCategory>> getAllCategories() {
        return allCategories;
    }

    public LiveData<List<TodoItem>> getMyDayTodos() {
        return myDayTodos;
    }

    public LiveData<List<TodoItem>> getImportantTodos() {
        return importantTodos;
    }

    public void insertCategory(TodoCategory category) {
        repository.insertCategory(category);
    }

    public void deleteCategory(TodoCategory category) {
        repository.deleteCategory(category);
    }
}
