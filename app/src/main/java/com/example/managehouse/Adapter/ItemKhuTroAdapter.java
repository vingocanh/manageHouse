package com.example.managehouse.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.managehouse.Model.Khutro;
import com.example.managehouse.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ItemKhuTroAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Khutro> khutros;
    private boolean layout;
    public static int itemWidth = 0;

    public ItemKhuTroAdapter(Context context, List<Khutro> khutros, boolean layout) {
        this.context = context;
        this.khutros = khutros;
        this.layout = layout;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == 1) {
            View view = null;
            if(layout) {
                view = LayoutInflater.from(context).inflate(R.layout.item_khutro, parent,false);
            }
            else {
                view = LayoutInflater.from(context).inflate(R.layout.item_khutro_grid, parent,false);
            }
            return new MyItemViewHolder(view);
        }
        else {
            if(viewType == 0) {
                View view = LayoutInflater.from(context).inflate(R.layout.item_loading, parent,false);
                return new MyLoadingViewHolder(view);
            }
            else {
                if(viewType == -1) {
                    View view = LayoutInflater.from(context).inflate(R.layout.item_not_found, parent,false);
                    return new MyNotFoundViewHolder(view);
                }
                else {
                    View view = LayoutInflater.from(context).inflate(R.layout.item_empty_data, parent,false);
                    return new MyEmptyDataViewHolder(view);
                }
            }

        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyItemViewHolder) {
            Khutro khutro = khutros.get(position);
            ((MyItemViewHolder) holder).txtName.setText(khutro.getTen());
            ((MyItemViewHolder) holder).txtAddress.setText(khutro.getDiachi());
            ((MyItemViewHolder) holder).txtXayDung.setText(khutro.getNam_xd());
            String trangThai = "Đang sử dụng";
            String color = "#27ae60";
            if(khutro.getStatus() == 0) {
                trangThai = "Không sử dụng";
                color = "#e74c3c";
            }
            ((MyItemViewHolder) holder).txtTrangThai.setText(trangThai);
            ((MyItemViewHolder) holder).txtTrangThai.setTextColor(Color.parseColor(color));
            if(khutro.getImg() != null && !khutro.getImg().equals("")) {
                Picasso.get().load(khutro.getImg()).placeholder(R.drawable.ic_hotel).error(R.drawable.ic_hotel).into(((MyItemViewHolder) holder).ivAvatar);
            }
            else {
                ((MyItemViewHolder) holder).ivAvatar.setImageResource(R.drawable.ic_hotel);
            }
            ((MyItemViewHolder) holder).setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int pos, boolean isLongClick) {
                    Log.d("cuong", "ok");
                }
            });
        }
        else {
            if(holder instanceof MyLoadingViewHolder) {

            }
        }
    }

    @Override
    public int getItemCount() {
        return khutros.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(khutros.get(position) == null) return 0;
        else {
            if(khutros.get(position).getId() == -1) return -1;
            else if(khutros.get(position).getId() == -2) return -2;
        }
        return 1;
    }


    public class MyItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView ivAvatar;
        private TextView txtName, txtAddress, txtXayDung, txtTrangThai;

        private ItemClickListener itemClickListener;

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }


        public MyItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            txtName = itemView.findViewById(R.id.txtName);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            txtXayDung = itemView.findViewById(R.id.txtXayDung);
            txtTrangThai = itemView.findViewById(R.id.txtTrangThai);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v,getAdapterPosition(),false);
        }
    }

    public class MyLoadingViewHolder extends RecyclerView.ViewHolder {

        public MyLoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public class MyNotFoundViewHolder extends RecyclerView.ViewHolder {
        public MyNotFoundViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public class MyEmptyDataViewHolder extends RecyclerView.ViewHolder {
        public MyEmptyDataViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
