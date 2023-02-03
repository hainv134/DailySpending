package com.example.dailyspending.transaction;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyspending.R;

public class TransactionHolder extends RecyclerView.ViewHolder{

    TextView category, note, price;
    View view;
    public TransactionHolder(@NonNull View itemView) {
        super(itemView);

        category = itemView.findViewById(R.id.transaction_item_category);
        note = itemView.findViewById(R.id.transaction_item_note);
        price = itemView.findViewById(R.id.transaction_item_price);
        this.view = itemView;
    }
}
