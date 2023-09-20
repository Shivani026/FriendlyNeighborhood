package com.example.friendlyneighborhood.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendlyneighborhood.Model.PaymentModel;
import com.example.friendlyneighborhood.Model.ReservationModel;
import com.example.friendlyneighborhood.Model.UserModel;
import com.example.friendlyneighborhood.R;
import com.example.friendlyneighborhood.databinding.EventPdfItemSampleBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventPdfAdapter extends RecyclerView.Adapter<EventPdfAdapter.ViewHolder> {
    Context context;
    ArrayList<ReservationModel> list;

    public EventPdfAdapter(Context context, ArrayList<ReservationModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_pdf_item_sample,parent,false);

        return new EventPdfAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(list != null && list.size()>0)
        {
            ReservationModel model = list.get(position);
            int sr = position+1;
            holder.binding.SrNoTvE.setText(""+sr);
            FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(model.getReservedBy()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists())
                            {
                                UserModel user = snapshot.getValue(UserModel.class);
                                holder.binding.NameTvE.setText(user.getName());
                                if(model.getReservationType().equalsIgnoreCase("Reservation")) {
                                    holder.binding.BlockFlatTvE.setText(user.getBlockFlat() + " (" + user.getType() + ")");
                                }
                                else
                                {
                                    holder.binding.BlockFlatTvE.setText("Secretary");

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            holder.binding.dateTimeTvE.setText(model.getReservationDate().toString());
            holder.binding.TypeTvE.setText(model.getReservationType());
            holder.binding.VenueTv.setText(model.getReservedAt());

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        EventPdfItemSampleBinding binding = EventPdfItemSampleBinding.bind(itemView);
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
