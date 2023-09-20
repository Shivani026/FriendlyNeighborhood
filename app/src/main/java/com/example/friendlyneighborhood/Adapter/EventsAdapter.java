package com.example.friendlyneighborhood.Adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendlyneighborhood.Model.ReservationModel;
import com.example.friendlyneighborhood.Model.UserModel;
import com.example.friendlyneighborhood.R;
import com.example.friendlyneighborhood.databinding.EventSampleBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.viewHolder> {
    ArrayList<ReservationModel> list;
    Context context;

    public EventsAdapter(ArrayList<ReservationModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_sample, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        ReservationModel model = list.get(position);
        if(model.getReservationType().toString().equalsIgnoreCase("reservation"))
        {
            FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(model.getReservedBy())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists())
                            {
                                UserModel user = snapshot.getValue(UserModel.class);
                                holder.binding.eventText.setText("Reserved By " + user.getName() + " for Occasion : " + model.getReservedFor());
                                holder.binding.venueDetails.setText(Html.fromHtml("<b>Venue : </b>Club House"));
                                holder.binding.EventType.setText("Reservation");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
        }
        else
        {
            holder.binding.eventText.setText(model.getReservedFor());
            holder.binding.venueDetails.setText(Html.fromHtml("<b>Venue : </b>"+model.getReservedAt()));
            holder.binding.EventType.setText("Event");

        }
        holder.binding.eventTimeStamp.setText(model.getReservationDate());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        EventSampleBinding binding = EventSampleBinding.bind(itemView);;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
