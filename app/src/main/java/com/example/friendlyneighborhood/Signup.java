package com.example.friendlyneighborhood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.strictmode.ImplicitDirectBootViolation;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.friendlyneighborhood.Model.UserModel;
import com.example.friendlyneighborhood.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Signup extends AppCompatActivity {
    private ActivitySignupBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ArrayList<String> societyNames;
    ArrayList<String> blockFlats;
    String selectedSociety;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        String[] type = getResources().getStringArray(R.array.type);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, type);
        binding.userType.setAdapter(adapter);

        societyNames = new ArrayList<>();
        ArrayAdapter<String> societyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, societyNames);
        binding.SelectSocietyName.setAdapter(societyAdapter);

        FirebaseDatabase.getInstance().getReference()
                        .child("society").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot: snapshot.getChildren())
                        {
                            String society = dataSnapshot.getKey();
                            societyNames.add(society);
                        }
                        societyAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        blockFlats = new ArrayList<>();
        ArrayAdapter<String> blockFlatAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, blockFlats);
        binding.SelectBlockFlat.setAdapter(blockFlatAdapter);

        binding.SelectSocietyName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                binding.progressBarSignUp.setVisibility(View.VISIBLE);
                binding.SelectBlockFlat.setEnabled(false);
                selectedSociety = binding.SelectSocietyName.getText().toString();
                if(!selectedSociety.isEmpty())
                {
                    FirebaseDatabase.getInstance().getReference().child("society")
                            .child(selectedSociety).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    blockFlats.clear();
                                    for(DataSnapshot dataSnapshot: snapshot.getChildren())
                                    {
                                        String blockflat = dataSnapshot.getKey();
                                        blockFlats.add(blockflat);
                                    }
                                    blockFlatAdapter.notifyDataSetChanged();
                                    binding.SelectBlockFlat.setEnabled(true);
                                    binding.progressBarSignUp.setVisibility(View.INVISIBLE);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
                else
                {
                    binding.SelectBlockFlat.setEnabled(false);
                }
            }

        });

        binding.RedirectToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.progressBarSignUp.setVisibility(View.VISIBLE);
                Intent intent = new Intent(Signup.this, Login.class);
                startActivity(intent);
            }
        });

        binding.SignUpBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.progressBarSignUp.setVisibility(View.VISIBLE);
                Toast.makeText(Signup.this, "Please wait while we register your details...", Toast.LENGTH_SHORT).show();
                String email = binding.SignUpEmail.getText().toString();
                String pwd = binding.Reenterpas.getText().toString();
                auth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            String name = binding.fullname.getText().toString();
                            String contact = binding.contact.getText().toString();
                            String type = binding.userType.getText().toString();
                            String society = binding.SelectSocietyName.getText().toString();
                            String blockFlat = binding.SelectBlockFlat.getText().toString();
                            UserModel user = new UserModel(name, pwd, contact, email, type, society, blockFlat);

                            String id = task.getResult().getUser().getUid();
                            database.getReference().child("Users").child(id).setValue(user);

                            Toast.makeText(Signup.this, "Signed Up Successfully...", Toast.LENGTH_SHORT).show();

                            Toast.makeText(Signup.this, "Please login with your credentials...", Toast.LENGTH_SHORT).show();


                            Intent intent = new Intent(Signup.this, Login.class);
                            intent.putExtra("from","SignUp");
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(Signup.this, "Error, Please check your details..", Toast.LENGTH_SHORT).show();
                            binding.progressBarSignUp.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });

        }

    @Override
    protected void onDestroy()
        {
            super.onDestroy();
        }
}