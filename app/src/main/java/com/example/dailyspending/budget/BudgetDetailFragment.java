package com.example.dailyspending.budget;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyspending.MainActivity;
import com.example.dailyspending.R;
import com.example.dailyspending.dao.BudgetDao;
import com.example.dailyspending.entity.Budget;
import com.example.dailyspending.entity.Category;
import com.example.dailyspending.entity.Transaction;
import com.example.dailyspending.transaction.TransactionAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class BudgetDetailFragment extends Fragment {

    TextView category, target, used, remain, date;
    RecyclerView recyclerView;
    Budget budget;
    ImageView cancel, delete, edit;

    HashMap<String, String> categories = new HashMap<>();
    SharedPreferences appSharedPrefs;
    Gson gson;
    ArrayList<Transaction> transactions;
    ProgressBar progressBar;

    public BudgetDetailFragment(Budget budget) {
        this.budget = budget;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.from(container.getContext()).inflate(R.layout.budget_view, container, false);
        category = view.findViewById(R.id.budget_item_view_category);
        target = view.findViewById(R.id.budget_item_view_target);
        used = view.findViewById(R.id.budget_item_view_used);
        remain = view.findViewById(R.id.budget_item_view_remain);
        date = view.findViewById(R.id.budget_item_view_date);
        recyclerView = view.findViewById(R.id.budget_item_view_recycleview);
        progressBar = view.findViewById(R.id.budget_item_view_progressingBar);
        cancel = view.findViewById(R.id.budget_item_view_cancel);
        delete = view.findViewById(R.id.budget_item_view_delete);
        edit = view.findViewById(R.id.budget_item_view_edit);

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext())); // create recycle view in

        //progressBar.setIndeterminate(true);
        appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        gson = new Gson();

        String json = appSharedPrefs.getString("categories", "");
        Type type = new TypeToken<ArrayList<Category>>(){}.getType();
        ArrayList<Category> categories_ = gson.fromJson(json, type);

        transactions = gson.fromJson(appSharedPrefs.getString("transactions", ""), new TypeToken<ArrayList<Transaction>>(){}.getType());
        categories_.forEach(category -> {
            categories.put(category.getId(), category.getName());
        });

        loadUI();
        return view;
    }

    private void loadUI() {
        ArrayList<Transaction> budgets_transaction = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        category.setText(categories.get(budget.getCategory()));
        target.setText(String.valueOf(budget.getTarget()));
        AtomicReference<Float> usedMoney = new AtomicReference<>((float) 0);
        transactions.forEach(transaction -> {
            if (transaction.getCategory().equalsIgnoreCase(budget.getCategory())){
                try {
                    Date transactionDate = simpleDateFormat.parse(transaction.getDate());
                    Date startDate = simpleDateFormat.parse(budget.getStartDate());
                    Date endDate = simpleDateFormat.parse(budget.getEndDate());
                    if(transactionDate.after(startDate) && transactionDate.before(endDate) || transactionDate.equals(startDate) || transactionDate.equals(endDate)){
                        budgets_transaction.add(transaction);
                        usedMoney.updateAndGet(v -> new Float((float) (v + (float) transaction.getMoney())));
                    }
                } catch (Exception e){}
            }

        });
        remain.setText(String.valueOf(budget.getTarget() - usedMoney.get()));
        used.setText(String.valueOf(usedMoney.get()));

        progressBar.setMax((int) budget.getTarget());
        progressBar.setProgress((int) Math.floor(usedMoney.get()));

        date.setText(budget.getStartDate() + " - " + budget.getEndDate() + " remain");

        recyclerView.setAdapter(new TransactionAdapter(getContext(), budgets_transaction, getActivity()));

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                if (fm.getBackStackEntryCount() > 0){
                    fm.popBackStack();
                }
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BudgetDao.getInstance().remove(budget.getId());
                FragmentManager fm = getFragmentManager();
                if (fm.getBackStackEntryCount() > 0){
                    fm.popBackStack();
                }
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).setActionBarTitle("Edit Budget");
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_layout, new UpdateBudgetFragment(budget), "UPDATE_BUDGET")
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}
