package com.example.friendlyneighborhood.Adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendlyneighborhood.Model.PaymentModel;
import com.example.friendlyneighborhood.Model.UserModel;
import com.example.friendlyneighborhood.R;
import com.example.friendlyneighborhood.databinding.AdminPaymentSampleBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.viewHolder>{
    ArrayList<PaymentModel> list;
    Context context;

    public PaymentAdapter(ArrayList<PaymentModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_payment_sample, parent, false);
        return new PaymentAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        PaymentModel model = list.get(position);
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(model.getPaidBy()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            UserModel user = snapshot.getValue(UserModel.class);
                            holder.binding.PaidBy.setText(Html.fromHtml("<b>Paid By : </b>" +user.getName()));
                            holder.binding.PayeeDetails.setText(user.getBlockFlat() + " (" + user.getType() + ")");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        holder.binding.PaymentReason.setText(Html.fromHtml("<b>Paid towards : </b>" +model.getPaymentType()));
        holder.binding.TxnDateTime.setText(Html.fromHtml("<b>Paid on : </b>"+model.getPaidAt()));
        holder.binding.PaidAmount.setText(Html.fromHtml("<b>Amount : ₹</b>" +model.getPaymentAmount()));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        AdminPaymentSampleBinding binding = AdminPaymentSampleBinding.bind(itemView);
        public viewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
