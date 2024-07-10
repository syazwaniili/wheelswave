package com.example.lab_rest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.R;
import com.example.lab_rest.model.Car;

import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        public TextView tvCategory;
        public TextView tvManufacturer;
        public TextView tvModel;
        public TextView tvYear;
        public TextView tvImage;
        public TextView tvStatus;
        public TextView tvPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvManufacturer = itemView.findViewById(R.id.tvManufacturer);
            tvModel = itemView.findViewById(R.id.tvModel);
            tvYear = itemView.findViewById(R.id.tvYear);
            tvImage = itemView.findViewById(R.id.tvImage);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvPrice = itemView.findViewById(R.id.tvPrice);

            if (isAdmin) {
                itemView.setOnLongClickListener(this);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            currentPos = getAdapterPosition();
            return false;
        }
    }

    private List<Car> carListData;
    private Context mContext;
    private int currentPos;
    private boolean isAdmin;

    public CarAdapter(Context context, List<Car> listData, boolean isAdmin) {
        carListData = listData;
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
        View view = inflater.inflate(R.layout.car_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Car m = carListData.get(position);
        holder.tvCategory.setText(m.getCategory());
        holder.tvManufacturer.setText(m.getManufacturer());
        holder.tvModel.setText(m.getModel());
        holder.tvYear.setText(m.getYear());
        holder.tvImage.setText(m.getImage());
        holder.tvStatus.setText(m.getStatus());
        holder.tvPrice.setText(String.valueOf(m.getPrice()));
    }

    @Override
    public int getItemCount() {
        return carListData.size();
    }

    public Car getSelectedItem() {
        if (currentPos >= 0 && carListData != null && currentPos < carListData.size()) {
            return carListData.get(currentPos);
        }
        return null;
    }
}