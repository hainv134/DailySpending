package com.example.dailyspending.category;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyspending.R;

public class CategoryHolder extends RecyclerView.ViewHolder {

    TextView txt_name;
    ImageView img_edit, img_delete;

    public CategoryHolder(@NonNull View itemView) {
        super(itemView);
        this.txt_name = itemView.findViewById(R.id.category_item_name);
        this.img_edit = itemView.findViewById(R.id.category_item_edit);
        this.img_delete = itemView.findViewById(R.id.category_item_delete);
    }
}
