package com.rev1.licenseplate.recognizer;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class student_register extends AppCompatActivity {

    private FirebaseAuth mAuth;
    public EditText usernameET, emailET, passET, confirmpassET;
    public MaterialButton regButton;
    public String username, email, pass, confirmpass;
    private DatabaseReference mDatabase;
// ...
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //define
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        usernameET = findViewById(R.id.usernameET);
        emailET = findViewById(R.id.emailET);
        passET = findViewById(R.id.passET);
        confirmpassET = findViewById(R.id.confirmpassET);
        regButton = findViewById(R.id.regButton);

        //firebase
        regButton.setOnClickListener(v -> {
            username = usernameET.getText().toString();
            email = emailET.getText().toString();
            pass = passET.getText().toString();
            confirmpass = confirmpassET.getText().toString();
            if (mAuth != null){
                if (!username.isEmpty() && !email.isEmpty() && !pass.isEmpty() && !confirmpass.isEmpty()){

                    if (pass.length() > 6){
                        mAuth.createUserWithEmailAndPassword(email, pass)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d(TAG, "createUserWithEmail:success");
                                            FirebaseUser user = mAuth.getCurrentUser();

                                            if (user != null){
                                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                        .setDisplayName(username).build();
                                                user.updateProfile(profileUpdates);
                                                writeNewUser(user.getUid().toString().trim(), username, email, "student");
                                                Toast.makeText(student_register.this, "Welcome, "+username+".",
                                                        Toast.LENGTH_SHORT).show();
                                                reload();
                                            }

                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                            Toast.makeText(student_register.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(student_register.this, "Password length must be more than 6 characters.",
                                Toast.LENGTH_SHORT).show();
                    }


                }
            }

        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent i = new Intent(student_register.this, MainActivity.class);
            startActivity(i);
        }
    }

    public void writeNewUser(String userId, String name, String email, String userType) {
        User user = new User(name, email, userType);
        mDatabase.child("users").child(userId).setValue(user);
    }

    public void reload(){
        Intent i = new Intent(student_register.this, student_login.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        student_register.this.finish();
    }
}