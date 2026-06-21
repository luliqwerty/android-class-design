package com.example.myapplication.ui.categories;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.model.TodoCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_SHORTCUT = 1;
    private static final int TYPE_CATEGORY = 2;

    public interface OnItemClickListener {
        void onShortcutClick(int shortcutType);
        void onCategoryClick(TodoCategory category);
    }

    private final OnItemClickListener listener;
    private List<Object> items = new ArrayList<>();

    private int myDayCount = 0;
    private int importantCount = 0;

    public CategoriesAdapter(OnItemClickListener listener) {
        this.listener = listener;
        rebuildItems();
    }

    public void setCategories(List<TodoCategory> categories) {
        this.categories = categories;
        rebuildItems();
        notifyDataSetChanged();
    }

    public void setCounts(int myDayCount, int importantCount) {
        this.myDayCount = myDayCount;
        this.importantCount = importantCount;
        rebuildItems();
        notifyDataSetChanged();
    }

    private List<TodoCategory> categories = new ArrayList<>();

    private void rebuildItems() {
        items = new ArrayList<>();
        items.add("header_shortcuts");
        items.add("shortcut_mine");      // position 1 - 我的一天
        items.add("shortcut_important"); // position 2 - 重要
        items.add("shortcut_all");       // position 3 - 全部待办
        items.add("header_categories");
        items.addAll(categories);
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof String) {
            String s = (String) item;
            if (s.startsWith("header")) return TYPE_HEADER;
            if (s.startsWith("shortcut")) return TYPE_SHORTCUT;
        }
        return TYPE_CATEGORY;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            TextView tv = new TextView(parent.getContext());
            int pad = parent.getContext().getResources().getDimensionPixelSize(R.dimen.dp_16);
            tv.setPadding(pad, pad, pad, 0);
            tv.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_TitleSmall);
            return new HeaderViewHolder(tv);
        } else if (viewType == TYPE_SHORTCUT) {
            View v = inflater.inflate(R.layout.item_shortcut, parent, false);
            return new ShortcutViewHolder(v);
        } else {
            View v = inflater.inflate(R.layout.item_category, parent, false);
            return new CategoryViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = items.get(position);
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder h = (HeaderViewHolder) holder;
            String s = (String) item;
            h.text.setText(s.equals("header_shortcuts")
                    ? R.string.shortcuts_header : R.string.categories_header);
        } else if (holder instanceof ShortcutViewHolder) {
            ShortcutViewHolder s = (ShortcutViewHolder) holder;
            String type = (String) item;
            switch (type) {
                case "shortcut_mine":
                    s.title.setText(R.string.my_day_title);
                    s.count.setText(String.valueOf(myDayCount));
                    s.count.setVisibility(View.VISIBLE);
                    s.itemView.setOnClickListener(v -> listener.onShortcutClick(1));
                    break;
                case "shortcut_important":
                    s.title.setText(R.string.important_title);
                    s.count.setText(String.valueOf(importantCount));
                    s.count.setVisibility(View.VISIBLE);
                    s.itemView.setOnClickListener(v -> listener.onShortcutClick(2));
                    break;
                case "shortcut_all":
                    s.title.setText(R.string.all_tasks_title);
                    s.count.setVisibility(View.GONE);
                    s.itemView.setOnClickListener(v -> listener.onShortcutClick(3));
                    break;
            }
        } else if (holder instanceof CategoryViewHolder) {
            TodoCategory category = (TodoCategory) item;
            CategoryViewHolder c = (CategoryViewHolder) holder;
            c.name.setText(category.getName());
            c.itemView.setOnClickListener(v -> listener.onCategoryClick(category));
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        HeaderViewHolder(TextView tv) {
            super(tv);
            text = tv;
        }
    }

    static class ShortcutViewHolder extends RecyclerView.ViewHolder {
        TextView title, count;
        ShortcutViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.text_shortcut_title);
            count = v.findViewById(R.id.text_shortcut_count);
        }
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        CategoryViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.text_category_name);
        }
    }
}
