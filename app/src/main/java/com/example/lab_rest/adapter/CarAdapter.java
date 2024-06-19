package com.example.lab_rest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.R;
import com.example.lab_rest.model.Car;

import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.ViewHolder> {

    private List<Car> carListData;
    private Context mContext;
    private int currentPos;

    public CarAdapter(Context context, List<Car> listData) {
        carListData = listData;
        mContext = context;
    }

    private Context getmContext() {
        return mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.car_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Car car = carListData.get(position);
        holder.tvCategory.setText(car.getCategory());
        holder.tvManuf.setText(car.getManufacturer());
        holder.tvModel.setText(car.getModel());
        holder.tvYear.setText(car.getYear());
        holder.tvImage.setText(car.getImage());
        holder.tvStatus.setText(car.getStatus());
        holder.tvPrice.setText(String.valueOf(car.getPrice()));
    }

    @Override
    public int getItemCount() {
        return carListData.size();
    }

    public Car getSelectedItem() {
        if(currentPos >= 0 && carListData != null && currentPos < carListData.size()) {
            return carListData.get(currentPos);
        }
        return null;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        public TextView tvCategory;
        public TextView tvManuf;
        public TextView tvModel;
        public TextView tvYear;
        public TextView tvImage;
        public TextView tvStatus;
        public TextView tvPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvManuf = itemView.findViewById(R.id.tvManuf);
            tvModel = itemView.findViewById(R.id.tvModel);
            tvYear = itemView.findViewById(R.id.tvYear);
            tvImage = itemView.findViewById(R.id.tvImage);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvPrice = itemView.findViewById(R.id.tvPrice);

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            currentPos = getAdapterPosition();
            return false;
        }
    }
}
