package com.example.myapplication.ui.addtodo;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.data.model.TodoCategory;
import com.example.myapplication.data.model.TodoItem;
import com.example.myapplication.databinding.DialogAddTodoBinding;
import com.example.myapplication.ui.categories.CategoriesViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddTodoDialogFragment extends DialogFragment {

    private static final String ARG_CATEGORY_ID = "category_id";
    private static final String ARG_TODO_ITEM = "todo_item";

    private DialogAddTodoBinding binding;
    private long categoryId;
    private TodoItem existingItem;
    private List<TodoCategory> categories = new ArrayList<>();
    private long selectedCategoryId;

    public interface OnTodoSavedListener {
        void onTodoSaved(TodoItem todo);
    }

    public interface OnTodoDeletedListener {
        void onTodoDeleted(TodoItem todo);
    }

    private OnTodoSavedListener listener;
    private OnTodoDeletedListener deleteListener;

    public static AddTodoDialogFragment newInstance(long categoryId, @Nullable TodoItem item) {
        AddTodoDialogFragment fragment = new AddTodoDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CATEGORY_ID, categoryId);
        if (item != null) {
            args.putSerializable(ARG_TODO_ITEM, item);
        }
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnTodoSavedListener(OnTodoSavedListener listener) {
        this.listener = listener;
    }

    public void setOnTodoDeletedListener(OnTodoDeletedListener listener) {
        this.deleteListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, com.google.android.material.R.style.ThemeOverlay_Material3_Dialog);
        if (getArguments() != null) {
            categoryId = getArguments().getLong(ARG_CATEGORY_ID);
            existingItem = (TodoItem) getArguments().getSerializable(ARG_TODO_ITEM);
        }
    }

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        binding = DialogAddTodoBinding.inflate(inflater, null, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(),
                com.google.android.material.R.style.ThemeOverlay_Material3_Dialog);
        builder.setView(binding.getRoot());

        if (existingItem != null) {
            builder.setTitle(R.string.edit_todo_title);
        } else {
            builder.setTitle(R.string.add_todo_title);
        }

        builder.setPositiveButton(R.string.save, null);
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dismiss());

        android.app.Dialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            ((AlertDialog) dialogInterface).getButton(DialogInterface.BUTTON_POSITIVE)
                    .setOnClickListener(v -> saveTodo());
        });

        setupViews();

        return dialog;
    }

    private void setupViews() {
        selectedCategoryId = categoryId > 0 ? categoryId : 0;

        CategoriesViewModel categoriesViewModel = new ViewModelProvider(requireActivity())
                .get(CategoriesViewModel.class);
        categoriesViewModel.getAllCategories().observe(this, cats -> {
            categories = cats;
            List<String> names = new ArrayList<>();
            for (TodoCategory c : cats) {
                names.add(c.getName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(), android.R.layout.simple_dropdown_item_1line, names);
            binding.editCategory.setAdapter(adapter);

            if (existingItem != null) {
                for (int i = 0; i < cats.size(); i++) {
                    if (cats.get(i).getId() == existingItem.getCategoryId()) {
                        binding.editCategory.setText(cats.get(i).getName(), false);
                        selectedCategoryId = cats.get(i).getId();
                        break;
                    }
                }
            } else if (categoryId > 0) {
                for (int i = 0; i < cats.size(); i++) {
                    if (cats.get(i).getId() == categoryId) {
                        binding.editCategory.setText(cats.get(i).getName(), false);
                        break;
                    }
                }
            }
        });

        if (existingItem != null) {
            binding.editTitle.setText(existingItem.getTitle());
            if (existingItem.getDueDate() != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(existingItem.getDueDate());
                binding.editDueDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(cal.getTime()));
                binding.editDueDate.setTag(existingItem.getDueDate());
            }
            binding.switchRecurring.setChecked(existingItem.isRecurring());
            binding.editRecurrenceInterval.setText(String.valueOf(existingItem.getRecurrenceIntervalDays()));
            binding.switchImportant.setChecked(existingItem.isImportant());
            binding.switchMyDay.setChecked(existingItem.isMyDay());

            if (existingItem.isRecurring()) {
                binding.layoutRecurrenceInterval.setVisibility(View.VISIBLE);
            }

            binding.btnDelete.setVisibility(View.VISIBLE);
            binding.btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(requireContext())
                        .setMessage(R.string.confirm_delete)
                        .setPositiveButton(R.string.delete, (dialog, which) -> {
                            if (deleteListener != null) {
                                deleteListener.onTodoDeleted(existingItem);
                            }
                            dismiss();
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
            });
        }

        binding.cardDueDate.setOnClickListener(v -> showDatePicker());

        binding.switchRecurring.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.layoutRecurrenceInterval.setVisibility(
                    isChecked ? View.VISIBLE : View.GONE);
        });

        binding.editCategory.setOnItemClickListener((parent, view1, position, id) -> {
            if (position < categories.size()) {
                selectedCategoryId = categories.get(position).getId();
            }
        });
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        if (existingItem != null && existingItem.getDueDate() != null) {
            cal.setTimeInMillis(existingItem.getDueDate());
        }
        DatePickerDialog dialog = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);
                    selected.set(Calendar.HOUR_OF_DAY, 23);
                    selected.set(Calendar.MINUTE, 59);
                    selected.set(Calendar.SECOND, 59);
                    binding.editDueDate.setText(
                            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    .format(selected.getTime()));
                    binding.editDueDate.setTag(selected.getTimeInMillis());
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    public void saveTodo() {
        String title = binding.editTitle.getText() != null ?
                binding.editTitle.getText().toString().trim() : "";
        if (TextUtils.isEmpty(title)) {
            binding.editTitle.setError(getString(R.string.title_required));
            return;
        }

        TodoItem item;
        if (existingItem != null) {
            item = existingItem;
        } else {
            item = new TodoItem(title, selectedCategoryId);
        }

        item.setTitle(title);
        item.setCategoryId(selectedCategoryId);

        Long dueDate = (Long) binding.editDueDate.getTag();
        item.setDueDate(dueDate);

        item.setRecurring(binding.switchRecurring.isChecked());
        if (binding.switchRecurring.isChecked()) {
            String intervalStr = binding.editRecurrenceInterval.getText() != null ?
                    binding.editRecurrenceInterval.getText().toString() : "";
            int interval = 1;
            if (!TextUtils.isEmpty(intervalStr)) {
                try {
                    interval = Integer.parseInt(intervalStr);
                } catch (NumberFormatException ignored) {}
            }
            item.setRecurrenceIntervalDays(Math.max(1, interval));
        } else {
            item.setRecurrenceIntervalDays(1);
        }

        item.setImportant(binding.switchImportant.isChecked());
        item.setMyDay(binding.switchMyDay.isChecked());

        if (listener != null) {
            listener.onTodoSaved(item);
        }
        dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
