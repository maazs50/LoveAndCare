package com.example.loveandcare.Activities;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.loveandcare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private EditText reg_email_field;
    private EditText reg_pass_field;
    private EditText reg_confirm_pass_field;
    private Button reg_btn;
    private Button reg_login_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        reg_email_field = findViewById(R.id.username);
        reg_pass_field = findViewById(R.id.password);
        reg_confirm_pass_field = findViewById(R.id.confirmPassword);
        reg_btn = findViewById(R.id.l_registerbtn);
        reg_login_btn = findViewById(R.id.r_login);

        mAuth = FirebaseAuth.getInstance();
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=reg_email_field.getText().toString();
                String pass = reg_pass_field.getText().toString();
                String confirm_pass = reg_confirm_pass_field.getText().toString();
                if (!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(pass)&&!TextUtils.isEmpty(confirm_pass)){
                    if (pass.equals(confirm_pass)){
                        mAuth.createUserWithEmailAndPassword(email, pass)
                                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Intent intent=new Intent(RegisterActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            // If sign in fails, display a message to the user.

                                            String error=task.getException().toString();
                                            Toast.makeText(RegisterActivity.this,error,Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                    }
                    else{
                        Toast.makeText(RegisterActivity.this,"Confirm Password does not match",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(RegisterActivity.this,"Enter all the fields",Toast.LENGTH_LONG).show();
                }
            }
        });


        reg_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToLogin();
            }
        });


    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.        Log.i("Created","RegisterActivity");

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){

            sendToMain();

        }
    }
    public void sendToMain(){
        Intent mainIntent=new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
    private void sendToLogin() {
        Intent intent=new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}