package com.example.friendlyneighborhood.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
import com.example.friendlyneighborhood.databinding.FragmentReservationBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;


public class ReservationFragment extends Fragment {
    FragmentReservationBinding binding;
    String newDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReservationBinding.inflate(inflater,container, false);

        binding.calendarView.setMinDate(System.currentTimeMillis()-1000);

        binding.calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                month = month = 1;
                newDate = day+"-"+month+"-"+year;
            }
        });

        binding.occasion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String details = binding.occasion.getText().toString();
                if(!details.isEmpty())
                {
                    binding.ReserveBtn.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.btn_bg));
                    binding.ReserveBtn.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.ReserveBtn.setEnabled(true);
                }
                else
                {
                    binding.ReserveBtn.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.btn_bg_disable));
                    binding.ReserveBtn.setTextColor(getContext().getResources().getColor(R.color.grey));
                    binding.ReserveBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

         binding.ReserveBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if (newDate==null) {
                     Toast.makeText(getContext(), "Please select date ..!", Toast.LENGTH_SHORT).show();
                 }
                 else {
                     ReservationModel reservation = new ReservationModel();
                     reservation.setReservedBy(FirebaseAuth.getInstance().getUid());
                     reservation.setReservedAt("Club House");
                     reservation.setReservationDate(newDate);
                     reservation.setReservedFor(binding.occasion.getText().toString());
                     reservation.setReservationType("Reservation");

                     FirebaseDatabase.getInstance().getReference()
                             .child("reservations")
                             .push()
                             .setValue(reservation).addOnSuccessListener(new OnSuccessListener<Void>() {
                                 @Override
                                 public void onSuccess(Void unused) {
                                     Toast.makeText(getContext(), "Reservation Successful", Toast.LENGTH_SHORT).show();

                                     NotificationModel notification = new NotificationModel();
                                     notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                     notification.setNotificationAt(new Date().getTime());
                                     notification.setPostId(newDate);
                                     notification.setPostedBy(FirebaseAuth.getInstance().getUid());
                                     notification.setType("reservation");

                                     FirebaseDatabase.getInstance().getReference()
                                             .child("notification")
                                             .child(FirebaseAuth.getInstance().getUid())
                                             .push()
                                             .setValue(notification);
                                 }
                             });

                     binding.occasion.setText("");
                 }
             }
         });


         return binding.getRoot();
    }
}