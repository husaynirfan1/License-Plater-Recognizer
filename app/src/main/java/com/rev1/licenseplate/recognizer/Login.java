package com.rev1.licenseplate.recognizer;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    public EditText emailET, passET;
    public MaterialButton confirmButton;
    public TextView registerbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //DEFINE
        emailET = findViewById(R.id.emailET);
        passET = findViewById(R.id.passET);
        mAuth = FirebaseAuth.getInstance();
        confirmButton = findViewById(R.id.confirmButton);
        registerbutton = findViewById(R.id.registerbutton);
        SpannableString ss = new SpannableString("Don't have an account? Register now.");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                startActivity(new Intent(Login.this, Register.class));
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, 23, 36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        registerbutton.setText(ss);
        registerbutton.setMovementMethod(LinkMovementMethod.getInstance());
        registerbutton.setHighlightColor(Color.TRANSPARENT);



        confirmButton.setOnClickListener(v -> {
            if (mAuth!= null){
                String email = emailET.getText().toString();
                String pass = passET.getText().toString();

                if (!email.isEmpty() && !pass.isEmpty()){
                    mAuth.signInWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user != null){
                                            if(!user.getDisplayName().isEmpty() && !user.getDisplayName().equals("")){
                                                Toast.makeText(Login.this, "Welcome, "+user.getDisplayName()+".",
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(Login.this, "Welcome!",
                                                        Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                        reload();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(Login.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
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
            reload();
        }
    }

    public void reload(){
        Intent i = new Intent(Login.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        Login.this.finish();
    }

}
