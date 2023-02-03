package com.example.dailyspending.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyspending.category.CategoryAdapter;
import com.example.dailyspending.entity.Category;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.gson.Gson;

import java.util.ArrayList;

public class CategoryDao {

    private DatabaseReference databaseReference;

    private static CategoryDao instance;
    static {
        try {
            instance = new CategoryDao();
        } catch (Exception e) {
            throw new RuntimeException("Exception occured in creating singleton instance");
        }
    }
    public static CategoryDao getInstance() {
        return instance;
    }

    public CategoryDao()
    {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(Category.class.getSimpleName());
    }
    public void add(Category emp)
    {
        String key = databaseReference.push().getKey();
        emp.setId(key);
        databaseReference.child(key).setValue(emp);
    }
    public void update(String key, String categoryName, String budget)
    {
        databaseReference.child(key).child("name").setValue(categoryName);
        databaseReference.child(key).child("budget").setValue(categoryName);
    }
    public Task<Void> remove(String key)
    {
        return databaseReference.child(key).removeValue();
    }

    public Query get()
    {
        return databaseReference;
    }

    public void loadCategoryUI(Context context, RecyclerView recyclerView, boolean spending) {
        ArrayList<Category> categories = new ArrayList<>();
        ArrayList<Category> categories_OnUI = new ArrayList<>();
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        prefsEditor.remove("categories");

        FirebaseDatabase.getInstance().getReference(Category.class.getSimpleName()).get().onSuccessTask(v -> {
            v.getChildren().forEach(child -> {
                Category category =  child.getValue(Category.class);
                if (category.isSpend() == spending){
                    categories_OnUI.add(category);
                };
                categories.add(category);
            });
            String json = gson.toJson(categories);
            prefsEditor.putString("categories", json);
            prefsEditor.commit();

            recyclerView.setAdapter(new CategoryAdapter(context, categories_OnUI, spending));
            return null;
        });
    }
}
