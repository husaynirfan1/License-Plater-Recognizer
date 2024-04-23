
package com.rev1.licenseplate.recognizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class student_info extends AppCompatActivity {
    private DatabaseReference myRef;
    private Query plateQuery;
    private ValueEventListener plateListener;
    private FirebaseAuth mAuth;
    public TextView studentNameTV, emailstudentTV, registeredVehicleTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_info);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        mAuth = FirebaseAuth.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        //define
        studentNameTV = findViewById(R.id.studentNameTV);
        emailstudentTV = findViewById(R.id.emailstudentTV);
        registeredVehicleTV = findViewById(R.id.registeredtv);

        queryPlate();


    }

    public void queryPlate(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myRef = database.getReference();
        plateQuery = myRef.child("users");
        Intent intent = getIntent();

        String plateI = intent.getStringExtra("plateNumber");

        plateListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    Log.d("uidkeychildren", ds.getKey());
                    if (ds.getKey() != null){
                        if (snapshot.child(ds.getKey()).hasChild("vehicles")){
                            String studentUID = ds.getKey();
                            String key  = snapshot.child(studentUID).child("vehicles").getKey();
                            Map<String, Long> valss = (Map) snapshot.child(studentUID).child("vehicles").getValue();

                            if (valss != null){
                                Log.d("uidkeychildrenvalss", valss.toString());
                                for (Map.Entry<String,Long> entry : valss.entrySet()) {
                                    String text = entry.getKey();
                                    Long value = entry.getValue();
                                    StringBuilder str = new StringBuilder(100);

                                    str.append("\n"+entry.getKey() + " " + entry.getValue());

                                    String plateFinal = text+" "+String.valueOf(value);

                                    if (plateFinal.equals(plateI)){
                                        String email = (String) snapshot.child(studentUID).child("email").getValue();
                                        String username = (String) snapshot.child(studentUID).child("username").getValue();

                                        if (email != null && username != null){
                                            emailstudentTV.setText(email);
                                            studentNameTV.setText(username);
                                            registeredVehicleTV.setText(str);

/*
                                            Toast.makeText(student_info.this, plateFinal+" exist " + email, Toast.LENGTH_SHORT).show();
*/
                                            break;
                                        }


                                    }

                                }


                            }


                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(student_info.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        };

        plateQuery.addListenerForSingleValueEvent(plateListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (plateListener != null){
            myRef.removeEventListener(plateListener);

        }
    }
}