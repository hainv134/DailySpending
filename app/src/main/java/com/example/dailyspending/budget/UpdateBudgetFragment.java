package com.example.dailyspending.budget;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.dailyspending.R;
import com.example.dailyspending.dao.BudgetDao;
import com.example.dailyspending.entity.Budget;
import com.example.dailyspending.entity.Category;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class UpdateBudgetFragment extends Fragment {
    EditText target, category, startDay, endDay;
    Category selectedCategory;
    ImageView cancel;
    TextView save;
    int startDay_, startMonth, startYear, endDay_, endMonth, endYear;

    Budget budget;
    HashMap<String, String> categories = new HashMap<>();
    SharedPreferences appSharedPrefs;
    Gson gson;

    public UpdateBudgetFragment(Budget budget) {
        this.budget = budget;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.from(container.getContext()).inflate(R.layout.budget_update, container, false);
        target = view.findViewById(R.id.budget_update_target);
        category = view.findViewById(R.id.budget_update_category);
        startDay = view.findViewById(R.id.budget_update_startDay);
        endDay = view.findViewById(R.id.budget_update_endDay);
        save = view.findViewById(R.id.budget_update_button);
        cancel = view.findViewById(R.id.budget_update_cancel_button);


        this.startYear = java.time.LocalDate.now().getYear();
        this.startMonth = java.time.LocalDate.now().getMonthValue()-1;
        this.startDay_ = java.time.LocalDate.now().getDayOfMonth();
        this.endYear = java.time.LocalDate.now().getYear();
        this.endMonth = java.time.LocalDate.now().getMonthValue()-1;
        this.endDay_ = java.time.LocalDate.now().getDayOfMonth();

        appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        gson = new Gson();
        String json = appSharedPrefs.getString("categories", "");
        Type type = new TypeToken<ArrayList<Category>>(){}.getType();
        ArrayList<Category> categories_ = gson.fromJson(json, type);
        categories_.forEach(category -> {
            categories.put(category.getId(), category.getName());
        });


        startDay.setText(budget.getStartDate());
        endDay.setText(budget.getEndDate());
        target.setText(String.valueOf(budget.getTarget()));
        category.setText(categories.get(budget.getCategory()));

        category.setFocusable(false);
        category.setClickable(true);;
        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences appSharedPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getContext());
                Gson gson = new Gson();
                String json = appSharedPrefs.getString("categories", "");
                Type type = new TypeToken<ArrayList<Category>>(){}.getType();
                ArrayList<Category> categories = gson.fromJson(json, type);
                createCategoryListDialog(categories).show();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                if (fm.getBackStackEntryCount() > 0){
                    fm.popBackStack();
                }
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                budget.setEndDate(endDay.getText().toString());
                budget.setStartDate(startDay.getText().toString());
                if (selectedCategory != null){
                    budget.setCategory(selectedCategory.getId());
                }
                budget.setTarget(Float.parseFloat(target.getText().toString()));
                BudgetDao.getInstance().update(budget.getId(), budget);
                FragmentManager fm = getFragmentManager();
                if (fm.getBackStackEntryCount() > 0){
                    fm.popBackStack();
                }
            }
        });

        // set day view
        startDay.setFocusable(false);
        startDay.setClickable(true);
        startDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectStartDate();
            }
        });
        endDay.setFocusable(false);
        endDay.setClickable(true);
        endDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectEndDate();
            }
        });
        return view;
    }

    private Dialog createCategoryListDialog(ArrayList<Category> categoryList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select category");
        ArrayList<String> lists  = new ArrayList<>();
        categoryList.forEach(category -> {
            lists.add(category.getName());
        });
        builder.setItems(lists.toArray(new String[0]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selectedCategory = categoryList.get(i);
                category.setText(selectedCategory.getName());
            }
        });
        return builder.create();
    }

    public void selectStartDate(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                startDay_ = dayOfMonth;
                startMonth = monthOfYear;
                startYear = year;
                startDay.setText(startDay_+"/"+(startMonth+1)+"/"+startYear);
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(this.getContext(),
                android.R.style.Theme_DeviceDefault_Dialog_NoActionBar,
                dateSetListener, startYear, startMonth, startDay_);
        datePickerDialog.show();
    }

    public void selectEndDate(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                endDay_ = dayOfMonth;
                endMonth = monthOfYear;
                endYear = year;
                endDay.setText(endDay_+"/"+(endMonth+1)+"/"+endYear);
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(this.getContext(),
                android.R.style.Theme_DeviceDefault_Dialog_NoActionBar,
                dateSetListener, endYear, endMonth, endDay_);
        datePickerDialog.show();
    }
}
