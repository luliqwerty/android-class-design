package com.example.myapplication.ui.categories;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.R;
import com.example.myapplication.data.model.TodoCategory;
import com.example.myapplication.databinding.FragmentCategoriesBinding;
import com.example.myapplication.ui.addtodo.AddCategoryDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class CategoriesFragment extends Fragment implements CategoriesAdapter.OnItemClickListener {

    private FragmentCategoriesBinding binding;
    private CategoriesViewModel viewModel;
    private CategoriesAdapter adapter;
    private int myDayCount = 0;
    private int importantCount = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(CategoriesViewModel.class);

        adapter = new CategoriesAdapter(this);
        binding.recyclerCategories.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerCategories.setAdapter(adapter);

        viewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            adapter.setCategories(categories);
        });

        viewModel.getMyDayTodos().observe(getViewLifecycleOwner(), todos -> {
            int count = 0;
            for (int i = 0; i < todos.size(); i++) {
                if (!todos.get(i).isCompleted()) count++;
            }
            myDayCount = count;
            adapter.setCounts(myDayCount, importantCount);
        });

        viewModel.getImportantTodos().observe(getViewLifecycleOwner(), todos -> {
            int count = 0;
            for (int i = 0; i < todos.size(); i++) {
                if (!todos.get(i).isCompleted()) count++;
            }
            importantCount = count;
            adapter.setCounts(myDayCount, importantCount);
        });
    }

    @Override
    public void onShortcutClick(int shortcutType) {
        Bundle args = new Bundle();
        // -1 = all, -2 = my day, -3 = important
        if (shortcutType == 1) {
            args.putInt("categoryId", -1);
        } else if (shortcutType == 2) {
            args.putInt("categoryId", -2);
        } else {
            args.putInt("categoryId", -3);
        }
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_categories_to_todos, args);
    }

    @Override
    public void onCategoryClick(TodoCategory category) {
        Bundle args = new Bundle();
        args.putInt("categoryId", (int) category.getId());
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_categories_to_todos, args);
    }

    public void showAddCategoryDialog() {
        AddCategoryDialogFragment dialog = new AddCategoryDialogFragment();
        dialog.setOnCategoryCreatedListener(name -> {
            viewModel.insertCategory(new TodoCategory(name));
        });
        dialog.show(getParentFragmentManager(), "AddCategoryDialog");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
