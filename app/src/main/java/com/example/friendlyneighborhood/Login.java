package com.example.friendlyneighborhood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.friendlyneighborhood.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private ActivityLoginBinding binding;
    FirebaseAuth auth;
    FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        binding.LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.progressBarLogin.setVisibility(View.VISIBLE);
                String email = binding.LoginEmail.getText().toString();
                String pwd = binding.LoginPassword.getText().toString();
                auth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful())
                       {
                           Toast.makeText(Login.this, "Logged in..", Toast.LENGTH_SHORT).show();
                           Intent intent = new Intent(Login.this , MainActivity.class);
                           startActivity(intent);
                       }
                    }
                });
            }
        });

        binding.RedirectToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.progressBarLogin.setVisibility(View.VISIBLE);
                Intent intent = new Intent(Login.this, Signup.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intentSignUp =getIntent();
        String from = intentSignUp.getStringExtra("from");
        if(from==null)
        {
            if(currentUser!=null)
            {
            Toast.makeText(this, "You are already logged in...", Toast.LENGTH_SHORT).show();
            binding.progressBarLogin.setVisibility(View.VISIBLE);
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        }
    }}
}