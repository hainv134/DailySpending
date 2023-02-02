package com.example.dailyspending.transaction;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyspending.MainActivity;
import com.example.dailyspending.R;
import com.example.dailyspending.entity.Category;
import com.example.dailyspending.entity.Transaction;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionHolder>{

    Context context;
    FragmentActivity activity;
    ArrayList<Transaction> transactions;
    RecyclerView recyclerView;
    HashMap<String, String> categories;

    public TransactionAdapter(Context context, ArrayList<Transaction> transactions, FragmentActivity activity) {
        this.context = context;
        this.transactions = transactions;
        this.categories = new HashMap<>();
        this.activity = activity;
    }

    @NonNull
    @Override
    public TransactionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item, parent, false);

        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.context);
        Gson gson = new Gson();
        String json = appSharedPrefs.getString("categories", "");
        Type type = new TypeToken<ArrayList<Category>>(){}.getType();
        ArrayList<Category> transactions = gson.fromJson(json, type);
        transactions.forEach(category -> {
            categories.put(category.getId(), category.getName());
        });

        return new TransactionHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.category.setText(categories.get(transaction.getCategory()));
        holder.note.setText(transaction.getNote());
        holder.price.setText(String.valueOf(transaction.getMoney()));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) activity).setActionBarTitle("Update Transaction");

                // use fragment tag to hold id
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_layout, new UpdateTransactionFragment(transaction), "TRANSACTION_UPDATE")
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

}
