package com.example.myapplication.ui.todos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.R;
import com.example.myapplication.data.model.TodoItem;
import com.example.myapplication.databinding.FragmentTodosBinding;
import com.example.myapplication.ui.addtodo.AddTodoDialogFragment;
import com.google.android.material.chip.Chip;

public class TodosFragment extends Fragment implements TodosAdapter.OnItemClickListener {

    private static final String ARG_CATEGORY_ID = "categoryId";

    private FragmentTodosBinding binding;
    private TodosViewModel viewModel;
    private TodosAdapter adapter;
    private long categoryId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTodosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        categoryId = getArguments() != null ? getArguments().getInt(ARG_CATEGORY_ID, 0) : 0;

        viewModel = new ViewModelProvider(this).get(TodosViewModel.class);
        viewModel.setCategoryId(categoryId);

        adapter = new TodosAdapter(this);
        binding.recyclerTodos.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerTodos.setAdapter(adapter);

        viewModel.getTodos().observe(getViewLifecycleOwner(), todos -> {
            adapter.submitList(todos);
            binding.textEmpty.setVisibility(
                    todos == null || todos.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.getCategory().observe(getViewLifecycleOwner(), category -> {
            if (category != null) {
                requireActivity().setTitle(category.getName());
            } else {
                if (categoryId == -1) requireActivity().setTitle(R.string.my_day_title);
                else if (categoryId == -2) requireActivity().setTitle(R.string.important_title);
                else if (categoryId == -3) requireActivity().setTitle(R.string.all_tasks_title);
            }
        });

        binding.chipAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                viewModel.setShowAll(true);
                viewModel.setCategoryId(categoryId);
            }
        });

        binding.chipIncomplete.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                viewModel.setShowAll(false);
                viewModel.setCategoryId(categoryId);
            }
        });
    }

    public void showAddTodoDialog() {
        AddTodoDialogFragment dialog = AddTodoDialogFragment.newInstance(categoryId, null);
        dialog.setOnTodoSavedListener(todo -> {
            if (todo.getId() == 0) {
                viewModel.insertTodo(todo);
            } else {
                viewModel.updateTodo(todo);
            }
        });
        dialog.show(getParentFragmentManager(), "AddTodoDialog");
    }

    @Override
    public void onItemClick(TodoItem item) {
        AddTodoDialogFragment dialog = AddTodoDialogFragment.newInstance(categoryId, item);
        dialog.setOnTodoSavedListener(todo -> {
            viewModel.updateTodo(todo);
        });
        dialog.show(getParentFragmentManager(), "EditTodoDialog");
    }

    @Override
    public void onCompleteToggle(TodoItem item, boolean completed) {
        item.setCompleted(completed);
        viewModel.updateTodo(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
