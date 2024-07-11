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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {

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

            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            currentPos = getAdapterPosition();
            return false;
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onItemClick(getAdapterPosition());
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private List<Car> carListData;
    private Context mContext;
    private int currentPos;
    private OnItemClickListener clickListener;

    public CarAdapter(Context context, List<Car> listData, OnItemClickListener listener) {
        carListData = listData;
        mContext = context;
        clickListener = listener;
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
