package com.example.dailyspending.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyspending.entity.Transaction;
import com.example.dailyspending.transaction.TransactionAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

public class TransactionDao {
    private DatabaseReference databaseReference;
    private static TransactionDao instance;
    static {
        try {
            instance = new TransactionDao();
        } catch (Exception e) {
            throw new RuntimeException("Exception occured in creating singleton instance");
        }
    }
    public static TransactionDao getInstance() {
        return instance;
    }

    public TransactionDao()
    {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(Transaction.class.getSimpleName());
    }
    public void add(Transaction emp)
    {
        String key = databaseReference.push().getKey();
        emp.setId(key);
        databaseReference.child(key).setValue(emp);
    }
    public void update(String key, Transaction transaction)
    {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(key, transaction);
        databaseReference.updateChildren(hashMap);
    }
    public void remove(String key)
    {
        databaseReference.child(key).removeValue();
    }
    public Query get()
    {
        return databaseReference;
    }

    public void loadCategoryUI(Context context, RecyclerView recyclerView, String value, FragmentActivity activity) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        ArrayList<Transaction> transactionsOnUI = new ArrayList<>();

        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        prefsEditor.remove("transactions");

        FirebaseDatabase.getInstance().getReference(Transaction.class.getSimpleName()).get().onSuccessTask(v -> {
            v.getChildren().forEach(child -> {
                Transaction transaction =  child.getValue(Transaction.class);
                transactions.add(transaction);
                if (transaction.getDate().contains(value)){
                    transactionsOnUI.add(transaction);
                }
            });
            prefsEditor.putString("transactions", gson.toJson(transactions));
            prefsEditor.putString("transactions_OnUI", gson.toJson(transactionsOnUI));

            prefsEditor.commit();

            recyclerView.setAdapter(new TransactionAdapter(context, transactionsOnUI, activity));
            return null;
        });
    }
}
