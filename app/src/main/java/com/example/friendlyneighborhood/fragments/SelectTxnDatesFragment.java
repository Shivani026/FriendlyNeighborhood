package com.example.friendlyneighborhood.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import com.example.friendlyneighborhood.R;
import com.example.friendlyneighborhood.databinding.FragmentSelectTxnDatesBinding;

import java.util.Date;

public class SelectTxnDatesFragment extends Fragment {

    FragmentSelectTxnDatesBinding binding;
    String fromDate;
    String toDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSelectTxnDatesBinding.inflate(inflater,container,false);

        binding.FromTxnCalVw.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                month = month = 1;
                fromDate = day+"-"+month+"-"+year;
            }
        });

        binding.ToTxnCalVw.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                month = month = 1;
                toDate = day+"-"+month+"-"+year;
                if (fromDate==null) {
                    Toast.makeText(getContext(), "Please select from date ..!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Bundle bundle = new Bundle();
                    bundle.putString("from",fromDate);
                    bundle.putString("to", toDate);

                    if(getArguments().getString("Id").equalsIgnoreCase("payment")) {

                        PaymentPdfFragment paymentPdfFragment = new PaymentPdfFragment();
                        paymentPdfFragment.setArguments(bundle);

                        getFragmentManager().beginTransaction().replace(R.id.container, paymentPdfFragment).commit();
                    }
                    else
                    {
                        EventPdfFragment eventPdfFragment = new EventPdfFragment();
                        eventPdfFragment.setArguments(bundle);

                        getFragmentManager().beginTransaction().replace(R.id.container, eventPdfFragment).commit();
                }
            }
        }});



        return binding.getRoot();
    }
}