package com.example.managehouse.Adapter;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.L;
import com.example.managehouse.Activity.HomeActivity;
import com.example.managehouse.Fragment.NguoiTro.DetailFragment;
import com.example.managehouse.Fragment.NguoiTro.FormFragment;
import com.example.managehouse.Model.Nguoitro;
import com.example.managehouse.Model.Phongtro;
import com.example.managehouse.R;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ItemNguoiTroPhongTroAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private List<Nguoitro> nguoitros;
    private Phongtro phongtro;

    public ItemNguoiTroPhongTroAdapter(Activity activity, List<Nguoitro> nguoitros, Phongtro phongtro) {
        this.activity = activity;
        this.nguoitros = nguoitros;
        this.phongtro = phongtro;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 0) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_add, parent,false);
            return new MyNotItemViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_nguoitro_phongtro, parent,false);
            return new MyItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Nguoitro nguoitro = nguoitros.get(position);
        if(holder instanceof MyItemViewHolder) {
            if(nguoitro.getAvatar() != null) {
                Picasso.get().load(nguoitro.getAvatar()).placeholder(R.drawable.ic_person_outline_32dp).error(R.drawable.ic_person_outline_32dp).into(((MyItemViewHolder) holder).ivAvatar);
            }
            else {
                ((MyItemViewHolder) holder).ivAvatar.setImageResource(R.drawable.ic_person_outline_32dp);
            }
            ((MyItemViewHolder) holder).txtTenNguoi.setText(nguoitro.getTen());
            ((MyItemViewHolder) holder).txtNgaySinh.setText(nguoitro.getNgaysinh2());
            ((MyItemViewHolder) holder).txtQueQuan.setText(nguoitro.getQuequan());
            ((MyItemViewHolder) holder).setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int pos, boolean isLongClick) {
                    if(nguoitros.get(pos) != null) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("nguoitro", (Serializable) nguoitros.get(pos));
                        DetailFragment detailFragment = new DetailFragment();
                        detailFragment.setArguments(bundle);
                        ((HomeActivity) activity).replaceFragment(detailFragment, true);
                    }
                }
            });
        }
        else {
        }
    }

    @Override
    public int getItemCount() {
        return nguoitros.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(nguoitros.get(position) == null) return 0;
        return 1;
    }

    public class MyItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView ivAvatar;
        private TextView txtTenNguoi, txtNgaySinh, txtQueQuan;

        private ItemClickListener itemClickListener;

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }


        public MyItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            txtTenNguoi = itemView.findViewById(R.id.txtTenNguoi);
            txtNgaySinh = itemView.findViewById(R.id.txtNgaySinh);
            txtQueQuan = itemView.findViewById(R.id.txtQueQuan);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v,getAdapterPosition(),false);
        }
    }

    public class MyNotItemViewHolder extends RecyclerView.ViewHolder{
        public MyNotItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    int[] data = new int[2];
                    String[] name = new String[2];
                    data[0] = phongtro.getKhutro_id();
                    data[1] = phongtro.getId();
                    name[0] = phongtro.getKhutro().getTen();
                    name[1] = phongtro.getTen();
                    bundle.putIntArray("data",data);
                    bundle.putStringArray("name",name);
                    FormFragment formFragment =new FormFragment();
                    formFragment.setArguments(bundle);
                    ((HomeActivity) activity).replaceFragment(formFragment, true);
                }
            });
        }
    }
}
