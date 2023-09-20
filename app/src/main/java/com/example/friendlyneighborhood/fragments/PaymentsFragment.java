package com.example.friendlyneighborhood.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.friendlyneighborhood.Model.PaymentModel;
import com.example.friendlyneighborhood.R;
import com.example.friendlyneighborhood.databinding.FragmentPaymentsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PaymentsFragment extends Fragment {
    private FragmentPaymentsBinding binding;
    final int UPI_PAYMENT = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPaymentsBinding.inflate(inflater, container, false);

        binding.payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(binding.enterAmount.getText().toString().trim()))
                {
                    Toast.makeText(getContext(), "Invalid Amount", Toast.LENGTH_SHORT).show();
                }
                else{
                    payUsingUpi("Shivani Awatade", "shivaniawatade026@okaxis",
                            binding.payType.getSelectedItem().toString() , binding.enterAmount.getText().toString());
                }

            }
        });

        ArrayList<String> paytype = new ArrayList<>();
        paytype.add("Maintenance");
        paytype.add("Fine/Utility");
        ArrayAdapter<String> payadapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, paytype);

        Spinner lvType = binding.payType;
        lvType.setAdapter(payadapter);

        return binding.getRoot();
    }

    public void payUsingUpi(  String name,String upiId, String note, String amount) {
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                //.appendQueryParameter("mc", "")
                //.appendQueryParameter("tid", "02125412")
//                .appendQueryParameter("tr", "25584584")
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                //.appendQueryParameter("refUrl", "blueapp")
                .build();
        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);
        // will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
        // check if intent resolves
        if(null != chooser.resolveActivity(getActivity().getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(getContext(),"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    //when user simply back without payment
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }

        public void upiPaymentDataOperation(ArrayList<String> data) {
            if (isConnectionAvailable(getContext())) {
                String str = data.get(0);
                String paymentCancel = "";
                if(str == null) str = "discard";
                String status = "";
                String approvalRefNo = "";
                String response[] = str.split("&");
                for (int i = 0; i < response.length; i++) {
                    String equalStr[] = response[i].split("=");
                    if(equalStr.length >= 2) {
                        if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                            status = equalStr[1].toLowerCase();
                        }
                        else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                            approvalRefNo = equalStr[1];
                        }
                    }
                    else {
                        paymentCancel = "Payment cancelled by user.";
                    }
                }
                if (status.equals("success")) {
                    //Code to handle successful transaction here.
                    Toast.makeText(getContext(), "Transaction successful.", Toast.LENGTH_SHORT).show();

                    PaymentModel payment = new PaymentModel();
                    payment.setPaymentAmount(binding.enterAmount.getText().toString());
                    payment.setPaymentType(binding.payType.getSelectedItem().toString());
                    payment.setPaidBy(FirebaseAuth.getInstance().getUid());
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
                    Date date = new Date();
                    payment.setPaidAt(String.valueOf(format.format(date)));

                    FirebaseDatabase.getInstance().getReference().child("payments").push()
                            .setValue(payment).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    binding.enterAmount.setText("");
                                }
                            });

                }
                else if("Payment cancelled by user.".equals(paymentCancel)) {
                    Toast.makeText(getContext(), "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getContext(), "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
            }
        }

        public static boolean isConnectionAvailable(Context context) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnected()
                        && netInfo.isConnectedOrConnecting()
                        && netInfo.isAvailable()) {
                    return true;
                }
            }
            return false;
        }
    }

