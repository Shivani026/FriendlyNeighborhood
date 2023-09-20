package com.example.friendlyneighborhood.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.friendlyneighborhood.Adapter.PaymentAdapter;
import com.example.friendlyneighborhood.Model.PaymentModel;
import com.example.friendlyneighborhood.R;
import com.example.friendlyneighborhood.databinding.FragmentAdminPaymentsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminPaymentsFragment extends Fragment {

    FragmentAdminPaymentsBinding binding;
    RecyclerView recyclerView;
    ArrayList<PaymentModel> list;
    ArrayList<PaymentModel> Oldlist;
    FirebaseDatabase database;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminPaymentsBinding.inflate(inflater, container, false);
        recyclerView = binding.AdminPaymentsRv;
        list= new ArrayList<>();
        Oldlist= new ArrayList<>();

        PaymentAdapter adapter = new PaymentAdapter(list, getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);


        FirebaseDatabase.getInstance().getReference().child("payments")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot: snapshot.getChildren())
                        {
                            PaymentModel payment = dataSnapshot.getValue(PaymentModel.class);
                            payment.setPaymentID(dataSnapshot.getKey());
                            Oldlist.add(payment);
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

        binding.printToPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("Id", "payment");

                SelectTxnDatesFragment selectTxnDatesFragment = new SelectTxnDatesFragment();
                selectTxnDatesFragment.setArguments(bundle);

                getFragmentManager().beginTransaction().replace(R.id.container, selectTxnDatesFragment).commit();
            }
        });

         return binding.getRoot();
    }
}