package com.example.dailyspending.report;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.example.dailyspending.ChartFragment;
import com.example.dailyspending.MainActivity;
import com.example.dailyspending.R;
import com.example.dailyspending.entity.Category;
import com.example.dailyspending.entity.Transaction;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ReportFragment extends Fragment {

    String startDay="", endDay="";
    ArrayList<Transaction> transactions;

    HashMap<String, Transaction> category_transaction_map = new HashMap<>();
    HashMap<String, String> categories_map =  new HashMap<>();
    List<DataEntry> date_transaction_map = new ArrayList<>();

    SharedPreferences appSharedPrefs;
    Gson gson;

    TextView startBalances, endBalances, finalGross, income, expenses;
    ImageButton income_chart, expense_chart, final_gross_chart;
    EditText edt_startDay, edt_endDay;
    ImageView report_view;

    AnyChartView anyChartView;
    public ReportFragment(){
    }
    public ReportFragment(String startDay, String endDay){
        this.startDay = startDay;
        this.endDay = endDay;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.from(container.getContext()).inflate(R.layout.report, container, false);
        startBalances = view.findViewById(R.id.report_begin_balances);
        endBalances = view.findViewById(R.id.report_end_balances);
        finalGross = view.findViewById(R.id.report_final_gross);
        income = view.findViewById(R.id.report_income);
        expenses = view.findViewById(R.id.report_expenses);

        edt_startDay = view.findViewById(R.id.report_startDay);
        edt_endDay = view.findViewById(R.id.report_endDay);
        report_view = view.findViewById(R.id.report_view);

        income_chart = view.findViewById(R.id.report_view_chart_income);
        expense_chart = view.findViewById(R.id.report_view_chart_expense);
        final_gross_chart = view.findViewById(R.id.report_view_final_gross_chart);

        if (startDay.isEmpty() || endDay.isEmpty()){
            Calendar c = Calendar.getInstance();
            endDay = c.get(Calendar.DAY_OF_MONTH)+"/"+(c.get(Calendar.MONTH)+1)+"/"+c.get(Calendar.YEAR);
            c.add(Calendar.DATE, -10);
            startDay = c.get(Calendar.DAY_OF_MONTH)+"/"+(c.get(Calendar.MONTH)+1)+"/"+c.get(Calendar.YEAR);
        }
        edt_startDay.setText(startDay);
        edt_endDay.setText(endDay);
        edt_startDay.setFocusable(false);
        edt_startDay.setClickable(true);
        edt_endDay.setFocusable(false);
        edt_endDay.setClickable(true);

        edt_startDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectStartDate();
            }
        });
        edt_endDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectEndDate();
            }
        });

        report_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData();
            }
        });
        appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        gson = new Gson();
        String json = appSharedPrefs.getString("categories", "");
        Type type = new TypeToken<ArrayList<Category>>(){}.getType();
        ArrayList<Category> categories_ = gson.fromJson(json, type);
        categories_.forEach(category -> {
            categories_map.put(category.getId(), category.getName());
        });
        transactions = gson.fromJson(appSharedPrefs.getString("transactions", ""), new TypeToken<ArrayList<Transaction>>(){}.getType());


        income_chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category_transaction_map = new HashMap<>();
                transactions.forEach(transaction -> {
                    if (!transaction.isSpending()){
                        category_transaction_map.put(categories_map.get(transaction.getCategory()), transaction);
                    }
                });
                ((MainActivity) getActivity()).setActionBarTitle("Income Transaction");
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_layout, new ChartFragment("pie",category_transaction_map), "INCOME_TRANSACTION")
                        .addToBackStack(null)
                        .commit();
            }
        });
        expense_chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category_transaction_map = new HashMap<>();
                transactions.forEach(transaction -> {
                    if (transaction.isSpending()){
                        category_transaction_map.put(categories_map.get(transaction.getCategory()), transaction);
                    }
                });
                ((MainActivity) getActivity()).setActionBarTitle("Expense Transaction");
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_layout, new ChartFragment("pie",category_transaction_map), "EXPENSES_TRANSACTION")
                        .addToBackStack(null)
                        .commit();
            }
        });
        final_gross_chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    date_transaction_map = new ArrayList<>();

                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Date startDate = formatter.parse(startDay);
                    Date endDate = formatter.parse(endDay);
                    LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                    for (LocalDate date = start; date.isBefore(end.plusDays(1)); date = date.plusDays(1)) {
                        String dateAfterFormat = date.getDayOfMonth() + "/"+date.getMonthValue() + "/"+date.getYear();

                        AtomicReference<Float> inc = new AtomicReference<>((float) 0);
                        AtomicReference<Float> exp = new AtomicReference<>((float) 0);
                        transactions.forEach(transaction -> {
                            if (transaction.getDate().equalsIgnoreCase(dateAfterFormat)){
                                if (transaction.isSpending()) exp.updateAndGet(v -> new Float((float) (v - transaction.getMoney())));
                                if (!transaction.isSpending()) inc.updateAndGet(v -> new Float((float) (v + transaction.getMoney())));
                            }
                        });
                        date_transaction_map.add(new CustomDataEntry(dateAfterFormat, inc.get(), exp.get()));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                ((MainActivity) getActivity()).setActionBarTitle("Final gross transaction");
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_layout, new ChartFragment(date_transaction_map), "FINAL_GROSS_TRANSACTION")
                        .addToBackStack(null)
                        .commit();
            }
        });
        loadData();
        return view;
    }

    private void loadData(){
        ///
        System.out.println("Loading data................... startDay: " + startDay + " ... endDay: " + endDay);
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date startDate = formatter.parse(startDay);
            Date endDate = formatter.parse(endDay);
            AtomicReference<Float> start_bl = new AtomicReference<>((float) 0);
            AtomicReference<Float> end_bl = new AtomicReference<>((float) 0);
            AtomicReference<Float> income_bl = new AtomicReference<>((float) 0);
            AtomicReference<Float> expenses_bl = new AtomicReference<>((float) 0);
            transactions.forEach(transaction -> {
                try {
                    Date transactionDate = formatter.parse(transaction.getDate());
                    // calculate to start date balances
                    if (transactionDate.before(startDate) || transactionDate.equals(startDate)){
                        if (transaction.isSpending())  {
                            start_bl.updateAndGet(v -> new Float((float) (v + (float) transaction.getMoney())));
                        } else {
                            start_bl.updateAndGet(v -> new Float((float) (v - (float) transaction.getMoney())));
                        }
                    }
                    // calculate to end date balances
                    if (transactionDate.before(endDate) || transactionDate.equals(endDate)){
                        if (transaction.isSpending()){
                            end_bl.updateAndGet(v -> new Float((float) (v - (float) transaction.getMoney())));
                        } else {
                            end_bl.updateAndGet(v -> new Float((float) (v + (float) transaction.getMoney())));
                        }
                    }
                    if (transactionDate.after(startDate) && transactionDate.before(endDate) || transactionDate.equals(startDate) || transactionDate.equals(endDate)) {
                        if (transaction.isSpending()) {
                            expenses_bl.updateAndGet(v -> new Float((float) (v + (float) transaction.getMoney())));
                        } else {
                            income_bl.updateAndGet(v -> new Float((float) (v + (float) transaction.getMoney())));
                        }
                    }
                } catch (ParseException e) {
                    System.out.println("Error parse............" + e.toString());
                    e.printStackTrace();
                }
            });

            startBalances.setText(String.valueOf(start_bl));
            endBalances.setText(String.valueOf(end_bl));
            income.setText(String.valueOf(income_bl));
            expenses.setText(String.valueOf(expenses_bl));
            finalGross.setText(String.valueOf(income_bl.get() - expenses_bl.get()));
        } catch (Exception e){
            System.out.println("Error............" + e.toString());
        }
    }

    public class CustomDataEntry extends ValueDataEntry {
        CustomDataEntry(String x, Number value, Number value2) {
            super(x, value);
            setValue("value2", value2);
            System.out.println("value..........:" + x + "///" + value.toString() + "///"+value2.toString());
        }
    }


    public void selectStartDate(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                startDay = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
                edt_startDay.setText(startDay);
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(this.getContext(),
                android.R.style.Theme_DeviceDefault_Dialog_NoActionBar,
                dateSetListener, 2022, 2, (new Date().getDay()));
        datePickerDialog.show();
    }

    public void selectEndDate(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                endDay = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
                edt_endDay.setText(endDay);
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(this.getContext(),
                android.R.style.Theme_DeviceDefault_Dialog_NoActionBar,
                dateSetListener, 2022, 2, (new Date().getDay()));
        datePickerDialog.show();
    }
}
