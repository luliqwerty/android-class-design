package com.example.myapplication.ui.addtodo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.R;
import com.example.myapplication.databinding.DialogAddCategoryBinding;

public class AddCategoryDialogFragment extends DialogFragment {

    private DialogAddCategoryBinding binding;

    public interface OnCategoryCreatedListener {
        void onCategoryCreated(String name);
    }

    private OnCategoryCreatedListener listener;

    public void setOnCategoryCreatedListener(OnCategoryCreatedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, com.google.android.material.R.style.ThemeOverlay_Material3_Dialog);
    }

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        binding = DialogAddCategoryBinding.inflate(inflater, null, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(),
                com.google.android.material.R.style.ThemeOverlay_Material3_Dialog);
        builder.setView(binding.getRoot());
        builder.setTitle(R.string.add_category_title);
        builder.setPositiveButton(R.string.save, null);
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dismiss());

        android.app.Dialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            ((AlertDialog) dialogInterface).getButton(DialogInterface.BUTTON_POSITIVE)
                    .setOnClickListener(v -> saveCategory());
        });

        return dialog;
    }

    public void saveCategory() {
        String name = binding.editCategoryName.getText() != null ?
                binding.editCategoryName.getText().toString().trim() : "";
        if (TextUtils.isEmpty(name)) {
            binding.editCategoryName.setError(getString(R.string.title_required));
            return;
        }
        if (listener != null) {
            listener.onCategoryCreated(name);
        }
        dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
