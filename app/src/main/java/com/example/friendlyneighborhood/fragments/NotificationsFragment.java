package com.example.friendlyneighborhood.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.friendlyneighborhood.Adapter.NotificationAdapter;
import com.example.friendlyneighborhood.Model.NotificationModel;
import com.example.friendlyneighborhood.databinding.FragmentNotificationsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {
    FragmentNotificationsBinding binding;
    RecyclerView recyclerView;
    ArrayList<NotificationModel> list;
    ArrayList<NotificationModel> Oldlist;
    FirebaseDatabase database;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container,  false);

        recyclerView = binding.notificationRv;
        list = new ArrayList<>();
        Oldlist = new ArrayList<>();

        NotificationAdapter adapter = new NotificationAdapter(list, getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference().child("notification")
                .child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren())
                        {
                            NotificationModel notification = dataSnapshot.getValue(NotificationModel.class);
                            notification.setNotificationID(dataSnapshot.getKey());
                            Oldlist.add(notification);
                        }
                        for (int i = Oldlist.size() - 1; i >= 0; i--)
                        {
                            // Append the elements in reverse order
                            list.add(Oldlist.get(i));
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return binding.getRoot();

    }
}