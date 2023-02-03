package com.example.dailyspending.transaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyspending.MainActivity;
import com.example.dailyspending.R;
import com.example.dailyspending.dao.TransactionDao;
import com.example.dailyspending.entity.Transaction;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class TransactionFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    Spinner spinner;
    RecyclerView recyclerView;

    private int year, month, dayOfMonth;
    private String currentFilter = "Day";

    TextView currentValueFilter, income, expenses, balance, totals;
    ImageButton pre, next;
    ImageButton addNewTransaction;
    Calendar calendar = Calendar.getInstance();

    ArrayList<Transaction> transactions;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.from(container.getContext()).inflate(R.layout.transaction_layout, container, false);
        spinner = view.findViewById(R.id.transaction_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, new String[] {"Day", "Month", "Year"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        income = view.findViewById(R.id.transaction_income);
        expenses = view.findViewById(R.id.transaction_expense);
        balance = view.findViewById(R.id.transaction_balance_final);
        totals = view.findViewById(R.id.transaction_totals);
        addNewTransaction = view.findViewById(R.id.transaction_add);
        recyclerView = view.findViewById(R.id.transaction_recycleview);
        pre = view.findViewById(R.id.transaction_pre);
        next = view.findViewById(R.id.transaction_next);
        currentValueFilter = view.findViewById(R.id.transaction_current_day);


        ((MainActivity) getActivity()).setActionBarTitle("Transaction");

        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        Gson gson = new Gson();

        Thread refreshThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String json = appSharedPrefs.getString("transactions_OnUI", "");
                                Type type = new TypeToken<ArrayList<Transaction>>(){}.getType();
                                transactions = gson.fromJson(json, type);

                                float res_income = 0;
                                float res_expenses = 0;
                                for (int i = 0; i < transactions.size(); i++) {
                                    Transaction transaction = transactions.get(i);
                                    if (transaction.isSpending()) res_expenses += (float) transactions.get(i).getMoney();
                                    if (!transaction.isSpending()) res_income += (float) transactions.get(i).getMoney();
                                }
                                income.setText(String.valueOf(res_income));
                                expenses.setText(String.valueOf(res_expenses));
                                balance.setText(String.valueOf(res_income-res_expenses));

                                json = appSharedPrefs.getString("transactions", "");
                                type = new TypeToken<ArrayList<Transaction>>(){}.getType();
                                transactions = gson.fromJson(json, type);
                                float res =0 ;
                                for (int i = 0; i < transactions.size(); i++) {
                                    Transaction transaction = transactions.get(i);
                                    if (transaction.isSpending()) res -= (float) transactions.get(i).getMoney();
                                    if (!transaction.isSpending()) res += (float) transactions.get(i).getMoney();
                                }
                                totals.setText(String.valueOf(res) + " VND");
                            }
                        });
                    }
                } catch (Exception e) {
                }
            }
        };
        refreshThread.start();

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext())); // create recycle view in
        addNewTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).setActionBarTitle("Add Transaction");
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_layout, new AddTransactionFragment(), "TRANSACTION_FRAGMENT")
                        .addToBackStack(null)
                        .commit();
            }
        });

        pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (currentFilter){
                    case "Day":
                        calendar.add(Calendar.DAY_OF_MONTH, -1);
                        break;
                    case "Month":
                        calendar.add(Calendar.MONTH, -1);
                        break;
                    case "Year":
                        calendar.add(Calendar.YEAR, -1);
                        break;
                    default:
                        break;
                }
                loadUI();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (currentFilter){
                    case "Day":
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                        break;
                    case "Month":
                        calendar.add(Calendar.MONTH, 1);
                        break;
                    case "Year":
                        calendar.add(Calendar.YEAR, 1);
                        break;
                    default:
                        break;
                }
               loadUI();
            }
        });

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        currentFilter = adapterView.getItemAtPosition(i).toString();
        loadUI();
    }

    private void loadUI(){

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        switch (currentFilter){
            case "Day":
                currentValueFilter.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                TransactionDao.getInstance().loadCategoryUI(this.getContext(), recyclerView, dayOfMonth + "/"+(month+1)+"/"+year, getActivity());
                break;
            case "Month":
                currentValueFilter.setText((month+1)+"/"+year);
                TransactionDao.getInstance().loadCategoryUI(this.getContext(), recyclerView, "/"+(month+1)+"/"+year, getActivity());
                break;
            case "Year":
                currentValueFilter.setText(String.valueOf(year));
                TransactionDao.getInstance().loadCategoryUI(this.getContext(), recyclerView,  "/"+year, getActivity());
                break;
            default:
                break;
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }


}
