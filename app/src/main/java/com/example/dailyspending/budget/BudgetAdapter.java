package com.example.dailyspending.budget;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyspending.MainActivity;
import com.example.dailyspending.R;
import com.example.dailyspending.entity.Budget;
import com.example.dailyspending.entity.Category;
import com.example.dailyspending.entity.Transaction;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetHolder>{

    Context context;
    ArrayList<Budget> budgets;
    RecyclerView recyclerView;
    HashMap<String, String> categories;
    SharedPreferences appSharedPrefs;
    Gson gson;
    ArrayList<Transaction> transactions;
    FragmentActivity activity;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public BudgetAdapter(Context context, ArrayList<Budget> budgets, FragmentActivity activity) {
        this.context = context;
        this.budgets = budgets;
        this.categories = new HashMap<>();
        this.activity = activity;
    }

    @NonNull
    @Override
    public BudgetHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.budget_item, parent, false);

        appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.context);
        gson = new Gson();
        String json = appSharedPrefs.getString("categories", "");
        Type type = new TypeToken<ArrayList<Category>>(){}.getType();
        ArrayList<Category> categories_ = gson.fromJson(json, type);
        categories_.forEach(category -> {
            categories.put(category.getId(), category.getName());
        });
        transactions = gson.fromJson(appSharedPrefs.getString("transactions", ""), new TypeToken<ArrayList<Transaction>>(){}.getType());

        return new BudgetHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetHolder holder, int position) {
        Budget budget = budgets.get(position);
        AtomicReference<Float> remainMoney = new AtomicReference<>((float) budget.getTarget());

        holder.category.setText(categories.get(budget.getCategory()));
        holder.target.setText(String.valueOf(budget.getTarget()));

        transactions.forEach(transaction -> {
            if (transaction.getCategory().equalsIgnoreCase(budget.getCategory())){
                try {
                    Date transactionDate = simpleDateFormat.parse(transaction.getDate());
                    Date startDate = simpleDateFormat.parse(budget.getStartDate());
                    Date endDate = simpleDateFormat.parse(budget.getEndDate());
                    if(transactionDate.after(startDate) && transactionDate.before(endDate) || transactionDate.equals(startDate) || transactionDate.equals(endDate)){
                        remainMoney.updateAndGet(v -> new Float((float) (v - transaction.getMoney())));
                    }
                } catch (Exception e){}
            }

        });
        holder.remain.setText("remain " + remainMoney.toString());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) activity).setActionBarTitle("View Budget");
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_layout, new BudgetDetailFragment(budget), "DETAIL_BUDGET")
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
        return budgets.size();
    }
}
