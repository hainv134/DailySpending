package com.example.dailyspending.budget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyspending.MainActivity;
import com.example.dailyspending.R;
import com.example.dailyspending.dao.BudgetDao;
import com.example.dailyspending.transaction.AddTransactionFragment;

public class BudgetFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    RecyclerView recyclerView;
    ImageView imageView_add;
    Spinner spinner;
    String currentFilter = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.from(container.getContext()).inflate(R.layout.budget_layout, container, false);
        spinner = view.findViewById(R.id.budget_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, new String[] {"Active", "Expired"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        recyclerView = view.findViewById(R.id.budget_recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext())); // create recycle view in
        imageView_add = view.findViewById(R.id.budget_add_button);
        ((MainActivity) getActivity()).setActionBarTitle("Budget");
        imageView_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).setActionBarTitle("Add Budget");
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_layout, new AddBudgetFragment(), "ADD_BUDGET")
                        .addToBackStack(null)
                        .commit();
            }
        });
        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        currentFilter = adapterView.getItemAtPosition(i).toString();
        BudgetDao.getInstance().loadBudgetUI(getContext(), recyclerView, currentFilter.equalsIgnoreCase("Active"), getActivity());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
