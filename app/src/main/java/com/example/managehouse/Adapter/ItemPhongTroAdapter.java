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

import com.example.managehouse.Common.Common;
import com.example.managehouse.Model.Phongtro;
import com.example.managehouse.R;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

public class ItemPhongTroAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private List<Phongtro> phongtros;
    private boolean layout;

    public ItemPhongTroAdapter(Activity activity, List<Phongtro> phongtros, boolean layout) {
        this.activity = activity;
        this.phongtros = phongtros;
        this.layout = layout;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 1) {
            View view = null;
            if(layout) {
                view = LayoutInflater.from(activity).inflate(R.layout.item_phongtro, parent,false);
            }
            else {
                view = LayoutInflater.from(activity).inflate(R.layout.item_phongtro_grid, parent,false);
            }
            return new ItemPhongTroAdapter.MyItemViewHolder(view);
        }
        else {
            if(viewType == 0) {
                View view = LayoutInflater.from(activity).inflate(R.layout.item_loading, parent,false);
                return new ItemPhongTroAdapter.MyLoadingViewHolder(view);
            }
            else {
                if(viewType == -1) {
                    View view = LayoutInflater.from(activity).inflate(R.layout.item_not_found, parent,false);
                    return new ItemPhongTroAdapter.MyNotFoundViewHolder(view);
                }
                else {
                    View view = LayoutInflater.from(activity).inflate(R.layout.item_empty_data, parent,false);
                    return new ItemPhongTroAdapter.MyEmptyDataViewHolder(view);
                }
            }

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyItemViewHolder) {
            Phongtro phongtro = phongtros.get(position);
            ((MyItemViewHolder) holder).txtName.setText(phongtro.getTen());
            ((MyItemViewHolder) holder).txtKhuTro.setText(phongtro.getKhutro().getTen());
            ((MyItemViewHolder) holder).txtPrice.setText(Common.formatMoney(phongtro.getGia()));
            String trangThai = "Đang sử dụng";
            String color = "#27ae60";
            if(phongtro.getStatus() == 0) {
                trangThai = "Không sử dụng";
                color = "#e74c3c";
            }
            ((MyItemViewHolder) holder).txtTrangThai.setText(trangThai);
            ((MyItemViewHolder) holder).txtTrangThai.setTextColor(Color.parseColor(color));
            if(phongtro.getImg() != null && !phongtro.getImg().equals("")) {
                Picasso.get().load(phongtro.getImg()).placeholder(R.drawable.ic_home_32dp).error(R.drawable.ic_home_32dp).into(((MyItemViewHolder) holder).ivAvatar);
            }
            else {
                ((MyItemViewHolder) holder).ivAvatar.setImageResource(R.drawable.ic_home_32dp);
            }
            ((MyItemViewHolder) holder).setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int pos, boolean isLongClick) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("phongtro", (Serializable) phongtros.get(pos));
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
        if(phongtros.get(position) == null) return 0;
        else {
            if(phongtros.get(position).getId() == -1) return -1;
            else if(phongtros.get(position).getId() == -2) return -2;
        }
        return 1;
    }

    @Override
    public int getItemCount() {
        return phongtros.size();
    }

    public class MyItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView ivAvatar;
        private TextView txtName, txtKhuTro, txtPrice, txtTrangThai;

        private ItemClickListener itemClickListener;

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }


        public MyItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            txtName = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtKhuTro = itemView.findViewById(R.id.txtKhuTro);
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
