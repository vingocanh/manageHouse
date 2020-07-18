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

import com.example.managehouse.Model.Nguoitro;
import com.example.managehouse.R;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

public class ItemNguoiTroAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private List<Nguoitro> nguoitros;
    private boolean layout;

    public ItemNguoiTroAdapter(Activity activity, List<Nguoitro> nguoitros, boolean layout) {
        this.activity = activity;
        this.nguoitros = nguoitros;
        this.layout = layout;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 1) {
            View view = null;
            if(layout) {
                view = LayoutInflater.from(activity).inflate(R.layout.item_nguoitro, parent,false);
            }
            else {
                view = LayoutInflater.from(activity).inflate(R.layout.item_nguoitro_grid, parent,false);
            }
            return new ItemNguoiTroAdapter.MyItemViewHolder(view);
        }
        else {
            if(viewType == 0) {
                View view = LayoutInflater.from(activity).inflate(R.layout.item_loading, parent,false);
                return new ItemNguoiTroAdapter.MyLoadingViewHolder(view);
            }
            else {
                if(viewType == -1) {
                    View view = LayoutInflater.from(activity).inflate(R.layout.item_not_found, parent,false);
                    return new ItemNguoiTroAdapter.MyNotFoundViewHolder(view);
                }
                else {
                    View view = LayoutInflater.from(activity).inflate(R.layout.item_empty_data, parent,false);
                    return new ItemNguoiTroAdapter.MyEmptyDataViewHolder(view);
                }
            }

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyItemViewHolder) {
            Nguoitro nguoitro = nguoitros.get(position);
            ((MyItemViewHolder) holder).txtTen.setText(nguoitro.getHoten());
            ((MyItemViewHolder) holder).txtPhongTro.setText(nguoitro.getPhongtro().getTen());
            ((MyItemViewHolder) holder).txtNgaySinh.setText(nguoitro.getNgaysinh());
            ((MyItemViewHolder) holder).txtQueQuan.setText(nguoitro.getQuequan());
            String trangThai = "Đang trọ";
            String color = "#27ae60";
            if(nguoitro.getStatus() == 0) {
                trangThai = "Đã thôi trọ";
                color = "#e74c3c";
            }
            ((MyItemViewHolder) holder).txtTrangThai.setText(trangThai);
            ((MyItemViewHolder) holder).txtTrangThai.setTextColor(Color.parseColor(color));
            if(nguoitro.getAvatar() != null) {
                Picasso.get().load(nguoitro.getAvatar()).placeholder(R.drawable.ic_person_outline_32dp).error(R.drawable.ic_person_outline_32dp).into(((MyItemViewHolder) holder).ivAvatar);
            }
            else {
                ((MyItemViewHolder) holder).ivAvatar.setImageResource(R.drawable.ic_person_outline_32dp);
            }
            ((MyItemViewHolder) holder).setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int pos, boolean isLongClick) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("nguoitro", (Serializable) nguoitros.get(pos));
//                    DetailFragment detailFragment = new DetailFragment();
//                    detailFragment.setArguments(bundle);
//                    ((HomeActivity) activity).replaceFragment(detailFragment, true);
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
        if(nguoitros.get(position) == null) return 0;
        else {
            if(nguoitros.get(position).getId() == -1) return -1;
            else if(nguoitros.get(position).getId() == -2) return -2;
        }
        return 1;
    }

    @Override
    public int getItemCount() {
        return nguoitros.size();
    }

    public class MyItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView ivAvatar;
        private TextView txtTen, txtPhongTro, txtNgaySinh, txtQueQuan, txtTrangThai;

        private ItemClickListener itemClickListener;

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }


        public MyItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            txtTen = itemView.findViewById(R.id.txtTen);
            txtPhongTro = itemView.findViewById(R.id.txtPhongTro);
            txtNgaySinh = itemView.findViewById(R.id.txtNgaySinh);
            txtQueQuan = itemView.findViewById(R.id.txtQueQuan);
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
