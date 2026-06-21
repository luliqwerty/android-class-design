package com.example.myapplication.ui.todos;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.myapplication.data.model.TodoCategory;
import com.example.myapplication.data.model.TodoItem;
import com.example.myapplication.data.repository.TodoRepository;

import java.util.List;

public class TodosViewModel extends AndroidViewModel {

    private final TodoRepository repository;
    private final MutableLiveData<Long> categoryId = new MutableLiveData<>();
    private final MutableLiveData<Boolean> showAll = new MutableLiveData<>(true);
    private final MutableLiveData<Boolean> sortByDueDate = new MutableLiveData<>(false);

    private final LiveData<TodoCategory> category;
    private final LiveData<List<TodoItem>> todos;

    public TodosViewModel(@NonNull Application application) {
        super(application);
        repository = new TodoRepository(application);

        category = Transformations.switchMap(categoryId,
                id -> repository.getCategoryById(id));

        todos = Transformations.switchMap(categoryId, id -> {
            if (id == -1) {
                return repository.getAllTodos();
            } else if (id == -2) {
                return repository.getMyDayTodos();
            } else if (id == -3) {
                return repository.getImportantTodos();
            }
            return repository.getTodosByCategory(id);
        });
    }

    public void setCategoryId(long id) {
        categoryId.setValue(id);
    }

    public LiveData<TodoCategory> getCategory() {
        return category;
    }

    public LiveData<List<TodoItem>> getTodos() {
        return todos;
    }

    public void setShowAll(boolean showAll) {
        this.showAll.setValue(showAll);
    }

    public void setSortByDueDate(boolean sortByDueDate) {
        this.sortByDueDate.setValue(sortByDueDate);
    }

    public void updateTodo(TodoItem item) {
        repository.updateTodo(item);
    }

    public void deleteTodo(TodoItem item) {
        repository.deleteTodo(item);
    }

    public void insertTodo(TodoItem item) {
        repository.insertTodo(item);
    }
}
