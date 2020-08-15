package com.example.managehouse.Adapter;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.managehouse.Activity.HomeActivity;
import com.example.managehouse.Fragment.KhoanThu.DetailFragment;
import com.example.managehouse.Model.Khoanthu;
import com.example.managehouse.R;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

public class ItemKhoanThuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private List<Khoanthu> khoanthus;
    private boolean layout;

    public ItemKhoanThuAdapter(Activity activity, List<Khoanthu> khoanthus, boolean layout) {
        this.activity = activity;
        this.khoanthus = khoanthus;
        this.layout = layout;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == 1) {
            View view = null;
            if(layout) {
                view = LayoutInflater.from(activity).inflate(R.layout.item_khoanthu, parent,false);
            }
            else {
                view = LayoutInflater.from(activity).inflate(R.layout.item_khoanthu_grid, parent,false);
            }
            return new MyItemViewHolder(view);
        }
        else {
            if(viewType == 0) {
                View view = LayoutInflater.from(activity).inflate(R.layout.item_loading, parent,false);
                return new MyLoadingViewHolder(view);
            }
            else {
                if(viewType == -1) {
                    View view = LayoutInflater.from(activity).inflate(R.layout.item_not_found, parent,false);
                    return new MyNotFoundViewHolder(view);
                }
                else {
                    View view = LayoutInflater.from(activity).inflate(R.layout.item_empty_data, parent,false);
                    return new MyEmptyDataViewHolder(view);
                }
            }

        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyItemViewHolder) {
            Khoanthu khoanthu = khoanthus.get(position);
            ((MyItemViewHolder) holder).txtName.setText(khoanthu.getTen());
            String trangThai = "Đang sử dụng";
            String color = "#27ae60";
            if(khoanthu.getStatus() == 0) {
                trangThai = "Không sử dụng";
                color = "#e74c3c";
            }
            ((MyItemViewHolder) holder).txtTrangThai.setText(trangThai);
            ((MyItemViewHolder) holder).txtTrangThai.setTextColor(Color.parseColor(color));
            if(khoanthu.getAvatar() != null && !khoanthu.getAvatar().equals("")) {
                Picasso.get().load(khoanthu.getAvatar()).placeholder(R.drawable.ic_energy).error(R.drawable.ic_energy).into(((MyItemViewHolder) holder).ivAvatar);
            }
            else {
                ((MyItemViewHolder) holder).ivAvatar.setImageResource(R.drawable.ic_energy);
            }
            ((MyItemViewHolder) holder).setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int pos, boolean isLongClick) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("khoanthu", (Serializable) khoanthus.get(pos));
                    DetailFragment detailFragment = new DetailFragment();
                    detailFragment.setArguments(bundle);
                    ((HomeActivity) activity).replaceFragment(detailFragment, true);
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
        return khoanthus.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(khoanthus.get(position) == null) return 0;
        else {
            if(khoanthus.get(position).getId() == -1) return -1;
            else if(khoanthus.get(position).getId() == -2) return -2;
        }
        return 1;
    }


    public class MyItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView ivAvatar;
        private TextView txtName, txtTrangThai;

        private ItemClickListener itemClickListener;

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }


        public MyItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            txtName = itemView.findViewById(R.id.txtName);
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
