package com.example.lab_rest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.R;
import com.example.lab_rest.model.Booking;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView tvBookingID, tvPickup_date, tvReturn_date, tvPickup_location, tvReturn_location, tvBookingstatus, tvTotalprice, tvUser_id, tvAdmin_id, tvCar_id;

        public ViewHolder(View itemView) {
            super(itemView);
            tvBookingID = itemView.findViewById(R.id.tvBookingID);
            tvPickup_date = itemView.findViewById(R.id.tvPickup_date);
            tvReturn_date = itemView.findViewById(R.id.tvReturn_date);
            tvPickup_location = itemView.findViewById(R.id.tvPickup_location);
            tvReturn_location = itemView.findViewById(R.id.tvReturn_location);
            tvBookingstatus = itemView.findViewById(R.id.tvBookingstatus);
            tvTotalprice = itemView.findViewById(R.id.tvTotalprice);
            tvUser_id = itemView.findViewById(R.id.tvUser_id);
            tvAdmin_id = itemView.findViewById(R.id.tvAdmin_id);
            tvCar_id = itemView.findViewById(R.id.tvCar_id);

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            currentPos = getAdapterPosition();
            return false;
        }
    }

    private List<Booking> bookingListData;
    private Context mContext;
    private int currentPos;
    private boolean isAdmin;

    public BookingAdapter(Context context, List<Booking> listData, boolean isAdmin) {
        bookingListData = listData;
        mContext = context;
        this.isAdmin = isAdmin;
    }

    private Context getmContext() {
        return mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.booking_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking b = bookingListData.get(position);
        holder.tvBookingID.setText(String.valueOf(b.getBookingID()));
        holder.tvPickup_date.setText(String.valueOf(b.getPickup_date()));
        holder.tvReturn_date.setText(String.valueOf(b.getReturn_date()));
        holder.tvPickup_location.setText(b.getPickup_location());
        holder.tvReturn_location.setText(b.getReturn_location());
        holder.tvBookingstatus.setText(b.getBooking_status());
        holder.tvTotalprice.setText(String.valueOf(b.getTotalPrice()));
        holder.tvUser_id.setText(String.valueOf(b.getUser_id()));
        holder.tvAdmin_id.setText(String.valueOf(b.getAdmin_id()));
        holder.tvCar_id.setText(String.valueOf(b.getCar_id()));
    }

    @Override
    public int getItemCount() {
        return bookingListData.size();
    }

    public Booking getSelectedItem() {
        if (currentPos >= 0 && bookingListData != null && currentPos < bookingListData.size()) {
            return bookingListData.get(currentPos);
        }
        return null;
    }
}
