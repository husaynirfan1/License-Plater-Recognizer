package com.rev1.licenseplate.recognizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.stream.Collectors;

public class Main_Student extends AppCompatActivity {

    public TextView studentNameTV, emailstudentTV, registeredVehicleTV;
    private DatabaseReference mDatabase;
    private Query studentQuery;
    private ValueEventListener studentListener;
    private ValueEventListener registeredListener;
    private FirebaseAuth mAuth;
    public FirebaseUser user;
    public FloatingActionButton addVehiclesBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_student);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //define
        studentNameTV = findViewById(R.id.studentNameTV);
        emailstudentTV = findViewById(R.id.emailstudentTV);
        registeredVehicleTV = findViewById(R.id.registeredtv);
        addVehiclesBtn = findViewById(R.id.addVehiclesBtn);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (user != null){
            String uid = user.getUid();

            studentQuery = mDatabase.child("users");
            studentListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(uid).exists()){
                        User userInfo = (User) snapshot.child(uid).getValue(User.class);
                        if (userInfo != null){
                            String name = userInfo.username;
                            String email = userInfo.email;
                            studentNameTV.setText("Name: "+name);
                            emailstudentTV.setText("Email: "+email);
                        }
                        if (snapshot.child(uid).child("vehicles").exists()){
                            Map<String, Long> registeredList = (Map<String, Long>) snapshot.child(uid).child("vehicles").getValue();
                            if (registeredList != null && !registeredList.isEmpty()){

                                StringBuilder str = new StringBuilder(100);

                                for (Map.Entry<String, Long> entry : registeredList.entrySet()) {
                                    System.out.println("\n"+entry.getKey() + " " + entry.getValue());
                                    str.append("\n"+entry.getKey() + " " + entry.getValue());
                                }
                                registeredVehicleTV.setText(str);
                            }
                        } else registeredVehicleTV.setText("No vehicles registered.");

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            studentQuery.addValueEventListener(studentListener);

            addVehiclesBtn.setOnClickListener(v -> {
                showVehiclesDialog();
            });
        }
    }

    public String convertWithStream(Map<Integer, ?> map) {
        String mapAsString = map.keySet().stream()
                .map(key -> key + "=" + map.get(key))
                .collect(Collectors.joining(", ", "{", "}"));
        return mapAsString;
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
    //todo split int and text

    void addVehicles(String plateText, long plateNumber){
        //to datalist
        mDatabase.child("PlateList").child(plateText).setValue(plateNumber);
        //to each user
        mDatabase.child("users").child(user.getUid()).child("vehicles").child(plateText).setValue(plateNumber).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(Main_Student.this, "Successfully added.", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(Main_Student.this, "Failed to add.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    void showVehiclesDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(this);
        edittext.setTextColor(ContextCompat.getColor(Main_Student.this, R.color.black));

        alert.setMessage("Vehicles Registration");
        alert.setTitle("Enter plate number");

        alert.setView(edittext);

        alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String plateString = edittext.getText().toString();
                String plateText = splitText(plateString);
                long plateNo = splitNumbers(plateString);
                addVehicles(plateText, plateNo);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        alert.show();
    }

    public void onBackPressed() {
        if (mAuth!= null){
            if (mAuth.getCurrentUser() != null){

                AlertDialog alertDialog = new AlertDialog.Builder(Main_Student.this).create();
                alertDialog.setTitle("Log out");
                alertDialog.setMessage("Do you want to log out?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mAuth.signOut();
                                dialog.dismiss();
                                Main_Student.this.finish();
                                Intent i = new Intent(Main_Student.this, student_login.class);
                                startActivity(i);
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        }

    }
}