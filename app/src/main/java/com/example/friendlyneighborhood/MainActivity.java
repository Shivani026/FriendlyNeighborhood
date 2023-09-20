package com.example.friendlyneighborhood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import com.example.friendlyneighborhood.Model.UserModel;
import com.example.friendlyneighborhood.databinding.ActivityMainBinding;
import com.example.friendlyneighborhood.fragments.AdminEventsFragment;
import com.example.friendlyneighborhood.fragments.AdminPaymentsFragment;
import com.example.friendlyneighborhood.fragments.NotificationsFragment;
import com.example.friendlyneighborhood.fragments.EventsFragment;
import com.example.friendlyneighborhood.fragments.ForumFragment;
import com.example.friendlyneighborhood.fragments.PaymentsFragment;
import com.example.friendlyneighborhood.fragments.ReservationFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iammert.library.readablebottombar.ReadableBottomBar;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.progressBarMain.setVisibility(View.VISIBLE);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new ForumFragment());
        transaction.commit();
        binding.progressBarMain.setVisibility(View.INVISIBLE);

        FirebaseDatabase.getInstance().getReference().child("Users")
                        .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserModel user = snapshot.getValue(UserModel.class);
                            userType = user.getType();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.readableBottomBar.setOnItemSelectListener(new ReadableBottomBar.ItemSelectListener() {
            @Override
            public void onItemSelected(int i) {

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                switch (i)
                {
                    case 0:
                        transaction.replace(R.id.container, new ForumFragment());
                        break;

                    case 1:
                        if(userType.equalsIgnoreCase("secretary"))
                        {
                            transaction.replace(R.id.container, new AdminPaymentsFragment());
                            break;
                        }
                        else
                        {
                        transaction.replace(R.id.container, new PaymentsFragment());
                        break;
                        }
                    case 2:
                        transaction.replace(R.id.container, new NotificationsFragment());
                        break;
                    case 3:
                        if(userType.equalsIgnoreCase("secretary"))
                        {
                            transaction.replace(R.id.container, new AdminEventsFragment());
                            break;
                        }
                        else {
                            transaction.replace(R.id.container, new EventsFragment());
                            break;
                        }
                    case 4:
                        transaction.replace(R.id.container, new ReservationFragment());
                        break;
                }
                transaction.commit();
            }
        });
    }
}