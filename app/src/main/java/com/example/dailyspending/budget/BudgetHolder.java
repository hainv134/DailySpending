package com.example.dailyspending.budget;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyspending.R;

public class BudgetHolder extends RecyclerView.ViewHolder{
    TextView category, target, remain;
    ProgressBar progressBar;
    View view;
    public BudgetHolder(@NonNull View itemView) {
        super(itemView);
        this.category = itemView.findViewById(R.id.budget_item_name);
        this.target = itemView.findViewById(R.id.budget_item_budgetTarget);
        this.remain = itemView.findViewById(R.id.budget_item_remain);
        this.view = itemView;
    }
}
