package com.sanmay.intenshala.ui.dashboard;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sanmay.intenshala.R;
import com.sanmay.intenshala.adapter.DashboardAdapter;
import com.sanmay.intenshala.data.DatabaseHelper;
import com.sanmay.intenshala.databinding.FragmentDashboardBinding;

import java.util.List;

// DashboardFragment.java
public class DashboardFragment extends Fragment {
    private FragmentDashboardBinding binding;
    private RecyclerView recyclerView;
    private DatabaseHelper dbHelper;
    private DashboardAdapter adapter;
    SharedPreferences sharedPreferences ;
    SharedPreferences.Editor editor ;

    DatabaseHelper DB;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        sharedPreferences = requireActivity().getSharedPreferences("internshala", MODE_PRIVATE);
        editor = sharedPreferences.edit();

//        recyclerView = root.findViewById(R.id.recyclerView);
        dbHelper = new DatabaseHelper(requireContext());
        Cursor cursor = dbHelper.getData(sharedPreferences.getString("email", ""));
        adapter = new DashboardAdapter(cursor);
        if(!sharedPreferences.getString("email", "").equals("")){
            LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
            binding.recyclerView.setLayoutManager(layoutManager);

            binding.recyclerView.setAdapter(adapter);


            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.noDataTextView.setVisibility(View.GONE);
        }else{
            binding.noDataTextView.setText("No data to show please login");
            binding.noDataTextView.setVisibility(View.VISIBLE);
            binding.recyclerView.setVisibility(View.GONE);
        }

//        adapter.setOnItemClickListener(new DashboardAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(String title, String description) {
//                // Handle item click event here
//                // You can access the title and description of the clicked item
//                Toast.makeText(requireContext(), "Title: " + title + ", Description: " + description, Toast.LENGTH_SHORT).show();
//            }
//        });
        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddItemDialog();
            }
        });
        binding.updateFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpdateItemDialog();
            }
        });

        return root;
    }

    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add Item");

        // Inflate the dialog layout
        View viewInflated = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_form_layout, null);
        final EditText editTextTitle = viewInflated.findViewById(R.id.editTextTitle);
        final EditText editTextDescription = viewInflated.findViewById(R.id.editTextDescription);

        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the positive button click
                String email = sharedPreferences.getString("email", "");;
                String title = editTextTitle.getText().toString().trim();
                String description = editTextDescription.getText().toString().trim();

                // Handle the data (you can save it to the database or do something else)
                // For simplicity, just printing the data for now
                System.out.println("Title: " + title);
                System.out.println("Description: " + description);

                long newRowId = dbHelper.insertUserData(email, title, description);
                if (newRowId != -1) {
                    Cursor newCursor = dbHelper.getData(email);
                    adapter.swapCursor(newCursor);
                    Toast.makeText(requireContext(), "New Entry Inserted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "New Entry Not Inserted", Toast.LENGTH_SHORT).show();
                }

            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the negative button click
                dialog.cancel();
            }
        });
        editTextTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        editTextDescription.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));

        // Show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.setView(viewInflated);

        // Set the background color of the AlertDialog window to transparent
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_bg);

        // Set the text color for the EditText fields

        dialog.show();
    }
    private void showUpdateItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Update Item");

        // Inflate the dialog layout
        View viewInflated = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_form_layout_update, null);
        final Spinner spinnerTitle = viewInflated.findViewById(R.id.spinnerTitle);
        final EditText editTextDescription = viewInflated.findViewById(R.id.editTextDescription);

        // Populate spinner with titles from the database
        List<String> titles = dbHelper.getAllTitles(sharedPreferences.getString("email", ""));
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, titles);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTitle.setAdapter(spinnerAdapter);

        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the positive button click
                String email = sharedPreferences.getString("email", "");
                String title = spinnerTitle.getSelectedItem().toString();
                String description = editTextDescription.getText().toString().trim();

                // Handle the data (you can save it to the database or do something else)
                // For simplicity, just printing the data for now
                System.out.println("Title: " + title);
                System.out.println("Description: " + description);

                boolean updated = dbHelper.updateUserData(email, title, description);
                if (updated) {
                    Cursor newCursor = dbHelper.getData(email);
                    adapter.swapCursor(newCursor);
                    Toast.makeText(requireContext(), "Item Updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Failed to Update Item", Toast.LENGTH_SHORT).show();
                }

            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the negative button click
                dialog.cancel();
            }
        });

        // Show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.setView(viewInflated);

        // Set the background color of the AlertDialog window to transparent
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_bg);

        // Set the text color for the EditText fields
        editTextDescription.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));

        dialog.show();
    }





    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
