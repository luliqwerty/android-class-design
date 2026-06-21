package com.example.myapplication.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.myapplication.data.db.AppDatabase;
import com.example.myapplication.data.db.TodoCategoryDao;
import com.example.myapplication.data.db.TodoItemDao;
import com.example.myapplication.data.model.TodoCategory;
import com.example.myapplication.data.model.TodoItem;

import java.util.List;

public class TodoRepository {

    private final TodoCategoryDao categoryDao;
    private final TodoItemDao itemDao;

    public TodoRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        categoryDao = db.todoCategoryDao();
        itemDao = db.todoItemDao();
    }

    public LiveData<List<TodoCategory>> getAllCategories() {
        return categoryDao.getAllCategories();
    }

    public LiveData<TodoCategory> getCategoryById(long id) {
        return categoryDao.getCategoryById(id);
    }

    public void insertCategory(TodoCategory category) {
        AppDatabase.databaseWriteExecutor.execute(() -> categoryDao.insert(category));
    }

    public void updateCategory(TodoCategory category) {
        AppDatabase.databaseWriteExecutor.execute(() -> categoryDao.update(category));
    }

    public void deleteCategory(TodoCategory category) {
        AppDatabase.databaseWriteExecutor.execute(() -> categoryDao.delete(category));
    }

    public LiveData<List<TodoItem>> getTodosByCategory(long categoryId) {
        return itemDao.getTodosByCategory(categoryId);
    }

    public LiveData<List<TodoItem>> getAllTodos() {
        return itemDao.getAllTodos();
    }

    public LiveData<List<TodoItem>> getMyDayTodos() {
        return itemDao.getMyDayTodos();
    }

    public LiveData<List<TodoItem>> getImportantTodos() {
        return itemDao.getImportantTodos();
    }

    public LiveData<List<TodoItem>> getTodosSortedByDueDate(long categoryId, boolean showAll) {
        if (showAll) {
            return itemDao.getTodosByCategorySortedByDueDate(categoryId);
        } else {
            return itemDao.getIncompleteTodosByCategorySortedByDueDate(categoryId);
        }
    }

    public LiveData<List<TodoItem>> getIncompleteTodosByCategory(long categoryId) {
        return itemDao.getIncompleteTodosByCategory(categoryId);
    }

    public void insertTodo(TodoItem item) {
        AppDatabase.databaseWriteExecutor.execute(() -> itemDao.insert(item));
    }

    public void updateTodo(TodoItem item) {
        AppDatabase.databaseWriteExecutor.execute(() -> itemDao.update(item));
    }

    public void deleteTodo(TodoItem item) {
        AppDatabase.databaseWriteExecutor.execute(() -> itemDao.delete(item));
    }

    public void deleteTodoById(long id) {
        AppDatabase.databaseWriteExecutor.execute(() -> itemDao.deleteById(id));
    }

    public void completeTodo(TodoItem item) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            item.setCompleted(true);
            itemDao.update(item);

            if (item.isRecurring() && item.getDueDate() != null && item.getRecurrenceIntervalDays() > 0) {
                TodoItem next = new TodoItem(item.getTitle(), item.getCategoryId());
                next.setDueDate(item.getDueDate() + (long) item.getRecurrenceIntervalDays() * 24 * 60 * 60 * 1000);
                next.setRecurring(true);
                next.setRecurrenceIntervalDays(item.getRecurrenceIntervalDays());
                next.setImportant(item.isImportant());
                next.setMyDay(item.isMyDay());
                itemDao.insert(next);
            }
        });
    }
}
