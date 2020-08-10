package com.example.managehouse.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.managehouse.Activity.ShowBillActivity;
import com.example.managehouse.Common.Common;
import com.example.managehouse.Model.Hoadon;
import com.example.managehouse.R;

import java.io.Serializable;
import java.util.List;

public class ItemHoaDonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private List<Hoadon> hoadons;
    private boolean layout;
    private View view = null;

    public ItemHoaDonAdapter(Activity activity, List<Hoadon> hoadons, boolean layout) {
        this.activity = activity;
        this.hoadons = hoadons;
        this.layout = layout;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 1) {
            if(layout) {
                view = LayoutInflater.from(activity).inflate(R.layout.item_hoadon, parent,false);
            }
            else {
                view = LayoutInflater.from(activity).inflate(R.layout.item_hoadon_grid, parent,false);
            }
            return new ItemHoaDonAdapter.MyItemViewHolder(view);
        }
        else {
            if(viewType == 0) {
                View view = LayoutInflater.from(activity).inflate(R.layout.item_loading, parent,false);
                return new ItemHoaDonAdapter.MyLoadingViewHolder(view);
            }
            else {
                if(viewType == -1) {
                    View view = LayoutInflater.from(activity).inflate(R.layout.item_not_found, parent,false);
                    return new ItemHoaDonAdapter.MyNotFoundViewHolder(view);
                }
                else {
                    View view = LayoutInflater.from(activity).inflate(R.layout.item_empty_data, parent,false);
                    return new ItemHoaDonAdapter.MyEmptyDataViewHolder(view);
                }
            }

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyItemViewHolder) {
            Hoadon hoadon = hoadons.get(position);
            ((MyItemViewHolder) holder).txtTen.setText(hoadon.getTen());
            ((MyItemViewHolder) holder).txtPhongTro.setText(hoadon.getPhongtro().getTen());
            ((MyItemViewHolder) holder).txtKhuTro.setText(hoadon.getPhongtro().getKhutro().getTen());
            ((MyItemViewHolder) holder).txtNgayThang.setText(hoadon.getThang() + "/" + hoadon.getNam());
            ((MyItemViewHolder) holder).txtTongTien.setText(Common.formatMoney(hoadon.getTongtien()));
            String trangThai = "Đã thu tiền";
            String color = "#27ae60";
            if(hoadon.getStatus() == 0) {
                trangThai = "Đã hủy";
                color = "#e74c3c";
            }
            else {
                if(hoadon.getStatus() == 1) {
                    trangThai = "Chờ thu tiền";
                    color = "#f39c12";
                }
            }
            ((MyItemViewHolder) holder).txtTrangThai.setText(trangThai);
            ((MyItemViewHolder) holder).txtTrangThai.setTextColor(Color.parseColor(color));
            ((MyItemViewHolder) holder).setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int pos, boolean isLongClick) {
                    Intent intent = new Intent(activity, ShowBillActivity.class);
                    intent.putExtra("hoadon", hoadon.getRaw());
                    intent.putExtra("checkDonViNuoc", hoadon.getCheck_water());
                    activity.startActivity(intent);
                }
            });
        }
        else {
            if(holder instanceof MyLoadingViewHolder) {

            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(hoadons.get(position) == null) return 0;
        else {
            if(hoadons.get(position).getId() == -1) return -1;
            else if(hoadons.get(position).getId() == -2) return -2;
        }
        return 1;
    }

    @Override
    public int getItemCount() {
        return hoadons.size();
    }

    public class MyItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView ivAvatar;
        private TextView txtTen, txtPhongTro, txtNgayThang, txtTongTien, txtTrangThai, txtKhuTro;

        private ItemClickListener itemClickListener;

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }


        public MyItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            txtTen = itemView.findViewById(R.id.txtTen);
            txtPhongTro = itemView.findViewById(R.id.txtPhongTro);
            txtNgayThang = itemView.findViewById(R.id.txtNgayThang);
            txtTongTien = itemView.findViewById(R.id.txtTongTien);
            txtTrangThai = itemView.findViewById(R.id.txtTrangThai);
            txtKhuTro = itemView.findViewById(R.id.txtKhuTro);
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
