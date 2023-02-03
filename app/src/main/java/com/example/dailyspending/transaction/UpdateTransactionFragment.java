package com.example.dailyspending.transaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.dailyspending.R;
import com.example.dailyspending.dao.TransactionDao;
import com.example.dailyspending.entity.Category;
import com.example.dailyspending.entity.Transaction;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class UpdateTransactionFragment extends Fragment {
    EditText money, note, date, with, reminder, category;
    private int lastSelectedYear;
    private int lastSelectedMonth;
    private int lastSelectedDayOfMonth;
    ImageView cancel, delete, save;
    CheckBox onReport;
    Transaction transaction;
    Category selectedCategory;

    HashMap<String, String> categories = new HashMap<>();
    SharedPreferences appSharedPrefs;
    Gson gson;

    public UpdateTransactionFragment(Transaction transaction) {
        this.transaction = transaction;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.from(container.getContext()).inflate(R.layout.transaction_update, container, false);
        this.money = view.findViewById(R.id.transaction_update_money);
        this.note = view.findViewById(R.id.transaction_update_note);
        this.date = view.findViewById(R.id.transaction_update_date);
        this.with = view.findViewById(R.id.transaction_update_with);
        this.reminder = view.findViewById(R.id.transaction_update_remind);
        this.category = view.findViewById(R.id.transaction_update_category);
        this.cancel = view.findViewById(R.id.transaction_update_cancel);
        this.save = view.findViewById(R.id.transaction_update_save_button);
        this.delete = view.findViewById(R.id.transaction_update_delete);

        onReport = view.findViewById(R.id.transaction_update_checkbox);
// Get Current Date
        Calendar c = Calendar.getInstance();
        this.lastSelectedYear = java.time.LocalDate.now().getYear();
        this.lastSelectedMonth = java.time.LocalDate.now().getMonthValue();
        this.lastSelectedDayOfMonth = java.time.LocalDate.now().getDayOfMonth();

        money.setText(String.valueOf(transaction.getMoney()));
        note.setText(transaction.getNote());
        with.setText(transaction.getWith());
        reminder.setText(transaction.getRemind());
        onReport.setChecked(!transaction.isOnReport());
        date.setText(transaction.getDate());


        appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        gson = new Gson();
        String json = appSharedPrefs.getString("categories", "");
        Type type = new TypeToken<ArrayList<Category>>(){}.getType();
        ArrayList<Category> categories_ = gson.fromJson(json, type);
        categories_.forEach(category -> {
            categories.put(category.getId(), category.getName());
        });

        category.setText(categories.get(transaction.getCategory()));

        // set day view
        date.setFocusable(false);
        date.setClickable(true);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDate();
            }
        });

        // set persion selected
        with.setFocusable(false);
        with.setClickable(true);
        with.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
            }
        });
        // set select category
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
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransactionDao.getInstance().remove(transaction.getId());
                FragmentManager fm = getFragmentManager();
                if (fm.getBackStackEntryCount() > 0){
                    fm.popBackStack();
                }
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transaction.setMoney(Float.parseFloat(money.getText().toString().replaceAll("[^\\d\\s]", "")));
                if (selectedCategory != null){
                    transaction.setCategory(selectedCategory.getId());
                    transaction.setSpending(selectedCategory.isSpend());
                }
                transaction.setDate(date.getText().toString());
                transaction.setNote(note.getText().toString());
                transaction.setOnReport(!onReport.isChecked());
                transaction.setWith(with.getText().toString());
                TransactionDao.getInstance().update(transaction.getId(), transaction);
                FragmentManager fm = getFragmentManager();
                if (fm.getBackStackEntryCount() > 0){
                    fm.popBackStack();
                }
            }
        });
        return  view;
    }


    private void checkPermission() {
        if(ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this.getActivity(), new String[] {Manifest.permission.READ_CONTACTS}, 100);
        } else {
            getContactList();
        }
    }

    private void getContactList() {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";
        Cursor cursor = this.getActivity().getContentResolver().query(
                uri, null, null, null, sort
        );
        ArrayList<String> contactList = new ArrayList<>();
        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                contactList.add(name);
            }
        }
        createContactListDialog(contactList).show();
    }
    private Dialog createContactListDialog(ArrayList<String> contactList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select person");
        ArrayList<Integer> selectedItem = new ArrayList<>();
        builder.setMultiChoiceItems(contactList.toArray(new String[0]), null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    // If the user checked the item, add it to the selected items
                    selectedItem.add(which);
                } else if (selectedItem.contains(which)) {
                    // Else, if the item is already in the array, remove it
                    selectedItem.remove(which);
                }
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                StringBuffer buffer = new StringBuffer();
                selectedItem.forEach(index -> {
                    buffer.append(contactList.get(index) + " / ");
                });
                with.setText(buffer.toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        return builder.create();
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

    public void selectDate(){

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                lastSelectedYear = year;
                lastSelectedMonth = monthOfYear+1;
                lastSelectedDayOfMonth = dayOfMonth;
                date.setText(lastSelectedDayOfMonth + "/" + lastSelectedMonth + "/" + lastSelectedYear);
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(this.getContext(),
                android.R.style.Theme_DeviceDefault_Dialog_NoActionBar,
                dateSetListener, lastSelectedYear, lastSelectedMonth-1, lastSelectedDayOfMonth);
        datePickerDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100  && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getContactList();
        } else {
            Toast.makeText(this.getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
            checkPermission();
        }
    }
}
