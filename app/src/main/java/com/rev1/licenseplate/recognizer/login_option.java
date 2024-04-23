package com.rev1.licenseplate.recognizer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.button.MaterialButton;

public class login_option extends AppCompatActivity {

    public MaterialButton staffBtn, studentBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_option);

        //define
        staffBtn = findViewById(R.id.staffBtn);
        staffBtn.setOnClickListener( v-> {
            Intent i = new Intent(login_option.this, Login.class);
            startActivity(i);
        });
        studentBtn = findViewById(R.id.studentBtn);
        studentBtn.setOnClickListener(v ->{
            Intent i = new Intent(login_option.this, student_login.class);
            startActivity(i);
        });
    }
}