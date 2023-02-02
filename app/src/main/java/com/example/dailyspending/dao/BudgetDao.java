package com.example.dailyspending.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyspending.budget.BudgetAdapter;
import com.example.dailyspending.entity.Budget;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class BudgetDao {
    private DatabaseReference databaseReference;
    public BudgetDao()
    {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(Budget.class.getSimpleName());
    }

    private static BudgetDao instance;
    static {
        try {
            instance = new BudgetDao();
        } catch (Exception e) {
            throw new RuntimeException("Exception occured in creating singleton instance");
        }
    }
    public static BudgetDao getInstance() {
        return instance;
    }

    public void add(Budget emp)
    {
        String key = databaseReference.push().getKey();
        emp.setId(key);
        databaseReference.child(key).setValue(emp);
    }
    public void update(String key, Budget budget)
    {
        HashMap<String ,Object> hashMap = new HashMap<>();
        hashMap.put(key, budget);
        databaseReference.child(key).updateChildren(hashMap);
    }
    public void remove(String key)
    {
         databaseReference.child(key).removeValue();
    }

    public void loadBudgetUI(Context context, RecyclerView recyclerView, boolean isActive, FragmentActivity activity) {
        ArrayList<Budget> budgets = new ArrayList<>();
        ArrayList<Budget> budgetsOnUI = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date currentDate = new Date();

        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        prefsEditor.remove("budgets");

        FirebaseDatabase.getInstance().getReference(Budget.class.getSimpleName()).get().onSuccessTask(v -> {
            v.getChildren().forEach(child -> {
                Budget budget =  child.getValue(Budget.class);
                budgets.add(budget);
                try {
                    Date startDate = simpleDateFormat.parse(budget.getStartDate());
                    Date endDate = simpleDateFormat.parse(budget.getEndDate());
                    boolean inRange = currentDate.after(startDate) && currentDate.before(endDate);
                    if(inRange == isActive){
                        budgetsOnUI.add(budget);
                    }
                } catch (Exception e){}

            });
            prefsEditor.putString("budgets", gson.toJson(budgets));
            prefsEditor.putString("budgets_OnUI", gson.toJson(budgetsOnUI));

            prefsEditor.commit();

            recyclerView.setAdapter(new BudgetAdapter(context, budgetsOnUI, activity));
            return null;
        });
    }
}