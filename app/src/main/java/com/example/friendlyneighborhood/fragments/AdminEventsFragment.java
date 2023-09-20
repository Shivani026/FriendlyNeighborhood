package com.example.friendlyneighborhood.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import com.example.friendlyneighborhood.Model.NotificationModel;
import com.example.friendlyneighborhood.Model.ReservationModel;
import com.example.friendlyneighborhood.R;
import com.example.friendlyneighborhood.databinding.FragmentAdminEventsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class AdminEventsFragment extends Fragment {

    String newDate;
    FragmentAdminEventsBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminEventsBinding.inflate(inflater,container,false);
        binding.EventDate.setMinDate(System.currentTimeMillis()-1000);

        binding.EventDate.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                month = month = 1;
                newDate = day+"-"+month+"-"+year;
            }
        });

        binding.EventDetails.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String details = binding.EventDetails.getText().toString();
                if(!details.isEmpty())
                {
                    binding.EventsPostBtn.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.btn_bg));
                    binding.EventsPostBtn.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.EventsPostBtn.setEnabled(true);
                }
                else
                {
                    binding.EventsPostBtn.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.btn_bg_disable));
                    binding.EventsPostBtn.setTextColor(getContext().getResources().getColor(R.color.grey));
                    binding.EventsPostBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.EventsPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newDate==null) {
                    Toast.makeText(getContext(), "Please select date ..!", Toast.LENGTH_SHORT).show();
                }
                else {
                    ReservationModel reservation = new ReservationModel();
                    reservation.setReservedBy(FirebaseAuth.getInstance().getUid());
                    reservation.setReservedAt(binding.EventVenue.getText().toString());
                    reservation.setReservationDate(newDate);
                    reservation.setReservedFor(binding.EventDetails.getText().toString());
                    reservation.setReservationType("Event");

                    FirebaseDatabase.getInstance().getReference()
                            .child("reservations")
                            .push()
                            .setValue(reservation).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getContext(), "Posted Successfully.. !", Toast.LENGTH_SHORT).show();

                                    NotificationModel notification = new NotificationModel();
                                    notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                    notification.setNotificationAt(new Date().getTime());
                                    notification.setPostId(newDate);
                                    notification.setPostedBy(FirebaseAuth.getInstance().getUid());
                                    notification.setType("event");

                                    FirebaseDatabase.getInstance().getReference()
                                            .child("notification")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .push()
                                            .setValue(notification);
                                }
                            });

                    binding.EventDetails.setText("");
                    binding.EventVenue.setText("");
                }
            }
        });

        binding.EventsReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("Id", "event");

                SelectTxnDatesFragment selectTxnDatesFragment = new SelectTxnDatesFragment();
                selectTxnDatesFragment.setArguments(bundle);

                getFragmentManager().beginTransaction().replace(R.id.container, selectTxnDatesFragment).commit();
            }
        });

        return binding.getRoot();
    }
}