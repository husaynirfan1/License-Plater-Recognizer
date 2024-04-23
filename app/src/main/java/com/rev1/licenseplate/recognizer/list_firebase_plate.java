package com.rev1.licenseplate.recognizer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class list_firebase_plate extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private HashMap<String, Long> list;
    private ArrayList<String> data;
    public RecyclerView rvplates;
    public RvAdapter adapter;
    public FloatingActionButton addBTN, submitBtn, backBtn;
    public EditText editText;
    public LinearLayout LL;
    private DatabaseReference myRef;
    private Query plateQuery;
    private ValueEventListener plateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_firebase_plate);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        mAuth = FirebaseAuth.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        rvplates = findViewById(R.id.rvplates);
        addBTN = findViewById(R.id.addBTN);
        editText = findViewById(R.id.editText);
        LL = findViewById(R.id.UpperLL);
        submitBtn = findViewById(R.id.submitBtn);
        backBtn=findViewById(R.id.backBtn);

        rvplates.setHasFixedSize(true);
        rvplates.setLayoutManager(new LinearLayoutManager(this));

        addBTN.setOnClickListener(v -> {
            LL.setVisibility(View.VISIBLE);
            Toast.makeText(list_firebase_plate.this, "test", Toast.LENGTH_SHORT).show();

        });

        backBtn.setOnClickListener(v -> {
            super.onBackPressed();
        });

        submitBtn.setOnClickListener(v -> {
            if (!editText.getText().toString().equals("") && !editText.getText().toString().isEmpty()){
                String plateText = splitText(editText.getText().toString());
                long plateNumbers = splitNumbers(editText.getText().toString());

                mDatabase.child("PlateList").child(plateText).setValue(plateNumbers).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(list_firebase_plate.this, "Successfully added.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(list_firebase_plate.this, "Failed. Please retry.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else Toast.makeText(list_firebase_plate.this, "Fill in plate number.", Toast.LENGTH_SHORT).show();

            editText.getText().clear();
            LL.setVisibility(View.GONE);

        });

        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                submitBtn.performClick();
                return true;
            }
            return false;
        });


        getList();

    }

    public void getList(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myRef = database.getReference();
        plateQuery = myRef.child("PlateList");

        list = new HashMap<>();
        data = new ArrayList<>();
        plateListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data.clear();
                for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                    String key = noteSnapshot.getKey(); //parent key
                    long value = (long) noteSnapshot.getValue();
                    list.put(key, value);

                    String plate = key+" "+Long.toString(value);
                    data.add(plate);
                }
                adapter = new RvAdapter(list_firebase_plate.this, data, new RvAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(String item) {
                        Intent i = new Intent(list_firebase_plate.this, student_info.class);
                        i.putExtra("plateNumber", item);
                        startActivity(i);
                    }
                });
                rvplates.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("color","Error: " + error.getMessage());
            }
        };
        plateQuery.addValueEventListener(plateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (plateListener != null){
            myRef.removeEventListener(plateListener);

        }
    }

    public String splitText(String plate){
        //To split text and number
        String str = plate.replace(" ", "").trim();
        String[] part = str.split("(?<=\\D)(?=\\d)");
        System.out.println(part[0]);
        System.out.println(part[1]);
        String text = part[0];
        Long numbers = Long.valueOf(part[1]);
        return text;
    }


    public Long splitNumbers(String plate){
        //To split text and number
        String str = plate.replace(" ", "").trim();
        String[] part = str.split("(?<=\\D)(?=\\d)");
        System.out.println(part[0]);
        System.out.println(part[1]);
        String text = part[0];
        Long numbers = Long.valueOf(part[1]);
        return numbers;
    }


}