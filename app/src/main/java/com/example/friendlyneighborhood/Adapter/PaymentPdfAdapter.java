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
import com.example.friendlyneighborhood.databinding.PaymentPdfItemSampleBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PaymentPdfAdapter extends RecyclerView.Adapter<PaymentPdfAdapter.ViewHolder> {
    Context context;
    ArrayList<PaymentModel> list;

    public PaymentPdfAdapter(Context context, ArrayList<PaymentModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.payment_pdf_item_sample,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(list != null && list.size()>0)
        {
            PaymentModel model = list.get(position);
            int sr = position+1;
            holder.binding.SrNoTv.setText(""+sr);
            FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(model.getPaidBy()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists())
                            {
                                UserModel user = snapshot.getValue(UserModel.class);
                                holder.binding.payNameTv.setText(user.getName());
                                holder.binding.BlockFlatTv.setText(user.getBlockFlat() + " (" + user.getType() + ")");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            holder.binding.dateTimeTv.setText(model.getPaidAt().toString());
            holder.binding.patTypeTv.setText(model.getPaymentType());
            holder.binding.payAmtTv.setText(model.getPaymentAmount());

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        PaymentPdfItemSampleBinding binding = PaymentPdfItemSampleBinding.bind(itemView);
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
