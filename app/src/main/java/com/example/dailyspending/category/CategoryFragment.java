package com.example.dailyspending.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyspending.MainActivity;
import com.example.dailyspending.R;
import com.example.dailyspending.dao.CategoryDao;
import com.example.dailyspending.entity.Category;

public class CategoryFragment extends Fragment implements AdapterView.OnItemSelectedListener, CategoryAddDialog.CategoryDialogListener {

    Spinner spinner;
    ImageView imageView_add;
    RecyclerView recyclerView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.from(container.getContext()).inflate(R.layout.category_layout, container, false);
        spinner = view.findViewById(R.id.category_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, new String[] {"Expenses", "Income"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        ((MainActivity) getActivity()).setActionBarTitle("Category");

        recyclerView = view.findViewById(R.id.category_recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext())); // create recycle view in

        imageView_add = view.findViewById(R.id.category_add);
        imageView_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CategoryAddDialog categoryAddDialog = new CategoryAddDialog();
                categoryAddDialog.show(getFragmentManager(), "ADD_CATEGORY");
                categoryAddDialog.setTargetFragment(CategoryFragment.this, 1);

            }
        });

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        boolean spending = adapterView.getItemAtPosition(i).toString() == "Income" ? false : true;
        CategoryDao.getInstance().loadCategoryUI(this.getContext(), recyclerView, spending);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void applyCommit(String categoryName, boolean spending) {
        Category category = new Category(categoryName, spending, "");
        CategoryDao.getInstance().add(category);
        CategoryDao.getInstance().loadCategoryUI(this.getContext(), recyclerView, spending);
    }
}
