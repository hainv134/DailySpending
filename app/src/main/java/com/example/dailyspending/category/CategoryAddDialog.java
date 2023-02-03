package com.example.dailyspending.category;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.dailyspending.R;

public class CategoryAddDialog extends DialogFragment {

    private EditText editText;
    private RadioButton r_income;
    private  RadioButton r_expenses;
    private Button ok, cancel;
    private CategoryDialogListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.category_dialog_add, container, false);
        editText = view.findViewById(R.id.category_add_title);
        r_income = view.findViewById(R.id.category_r_income);
        r_expenses = view.findViewById(R.id.category_r_expenses);
        ok = view.findViewById(R.id.category_add_button_apply);
        cancel = view.findViewById(R.id.category_add_button_cancel);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean spending = false;
                if (r_expenses.isChecked()) {
                    spending = true;
                }
                listener.applyCommit(editText.getText().toString(), spending);
                getDialog().dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (CategoryDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw  new ClassCastException(context.toString() + "Muse implement CategoryDialogListener");
        }
    }

    public interface CategoryDialogListener {
        void applyCommit(String categoryName, boolean spending);
    }
}
