package com.example.dailyspending.category;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyspending.R;
import com.example.dailyspending.dao.CategoryDao;
import com.example.dailyspending.entity.Category;

import java.util.ArrayList;
import java.util.HashMap;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryHolder> {
    Context context;
    ArrayList<Category> categories;
    RecyclerView recyclerView;
    boolean spending;
    public CategoryAdapter(Context context, ArrayList<Category> categories, boolean spending) {
        this.context = context;
        this.categories = categories;
        this.spending = spending;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new CategoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
        Category category = categories.get(position);

        holder.txt_name.setText(category.getName());
        holder.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CategoryDao.getInstance().remove(category.getId());
                CategoryDao.getInstance().loadCategoryUI(context, recyclerView, spending);
            }
        });
        holder.img_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

                View layout_update = inflater.inflate(R.layout.category_dialog_update, null);
                // fill view to dialog
                alertDialog.setView(layout_update)
                        .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                TextView textView = (TextView) layout_update.findViewById(R.id.category_update_title);
                                CategoryDao.getInstance().update(category.getId(), textView.getText().toString(), category.getBudget());
                                CategoryDao.getInstance().loadCategoryUI(context, recyclerView, true);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                alertDialog.create().show();
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }



    @Override
    public int getItemCount() {
        return categories.size();
    }

}
