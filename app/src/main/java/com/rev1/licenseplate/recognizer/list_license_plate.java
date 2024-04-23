package com.rev1.licenseplate.recognizer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class list_license_plate extends AppCompatActivity {

    public RecyclerView rvnoplate;
    public ItemAdapter adapter;
    public FloatingActionButton addBTN, submitBtn;
    ViewModalPlate viewModalPlate;
    List<PlateModal> plateModalList;
    public EditText editText;
    public LinearLayout LL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_license_plate);

        //Define
        rvnoplate = findViewById(R.id.RVnoplate);
        addBTN = findViewById(R.id.addBTN);
        editText = findViewById(R.id.editText);
        LL = findViewById(R.id.UpperLL);
        submitBtn = findViewById(R.id.submitBtn);

        //Setup ViewModal
        viewModalPlate = new ViewModelProvider(this).get(ViewModalPlate.class);

        rvnoplate.setHasFixedSize(true);
        rvnoplate.setLayoutManager(new LinearLayoutManager(this));

        addBTN.setOnClickListener(v -> {
            LL.setVisibility(View.VISIBLE);
        });

        submitBtn.setOnClickListener(v -> {
            if (!editText.getText().toString().equals("") && !editText.getText().toString().isEmpty()){
                PlateModal plateModal = new PlateModal(editText.getText().toString(), true);
                viewModalPlate.insert(plateModal);
            } else Toast.makeText(list_license_plate.this, "Fill in plate number.", Toast.LENGTH_SHORT).show();

            editText.getText().clear();
            LL.setVisibility(View.GONE);
        });

        editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    submitBtn.performClick();
                    return true;
                }
                return false;
            }
        });
        adapter = new ItemAdapter();
        rvnoplate.setAdapter(adapter);
        viewModalPlate.getAllplate().observe(this, new Observer<List<PlateModal>>() {
            @Override
            public void onChanged(List<PlateModal> plateModals) {
                if (plateModals != null){
                    plateModalList = plateModals;
                    adapter.updateList(plateModals);
                    Log.d("ONCREATE", plateModals.get(0).getNo_plate());
                }
            }
        });

    }
}