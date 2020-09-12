package com.example.managehouse.Adapter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.managehouse.Activity.HomeActivity;
import com.example.managehouse.Common.Common;
import com.example.managehouse.Fragment.PhongTro.DetailFragment;
import com.example.managehouse.Fragment.PhongTro.FormFragment;
import com.example.managehouse.Model.Khutro;
import com.example.managehouse.Model.Phongtro;
import com.example.managehouse.R;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

public class ItemKhuTroPhongTroAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private List<Phongtro> phongtros;
    private Khutro khutro;

    public ItemKhuTroPhongTroAdapter(Activity activity, List<Phongtro> phongtros, Khutro khutro) {
        this.activity = activity;
        this.phongtros = phongtros;
        this.khutro = khutro;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_add, parent, false);
            return new MyNotItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_khutro_phongtro, parent, false);
            return new MyItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Phongtro phongtro = phongtros.get(position);
        if (holder instanceof MyItemViewHolder) {
            if (phongtro.getImg() != null) {
                Picasso.get().load(phongtro.getImg()).placeholder(R.drawable.ic_home_32dp).error(R.drawable.ic_home_32dp).into(((MyItemViewHolder) holder).ivAvatar);
            } else {
                ((MyItemViewHolder) holder).ivAvatar.setImageResource(R.drawable.ic_home_32dp);
            }
            ((MyItemViewHolder) holder).txtTen.setText(phongtro.getTen());
            ((MyItemViewHolder) holder).txtGia.setText(Common.formatNumber(phongtro.getGia(),true));
            ((MyItemViewHolder) holder).setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int pos, boolean isLongClick) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("phongtro", (Serializable) phongtros.get(pos));
                    DetailFragment detailFragment = new DetailFragment();
                    detailFragment.setArguments(bundle);
                    ((HomeActivity) activity).replaceFragment(detailFragment, true);
                }
            });
        } else {
        }
    }

    @Override
    public int getItemCount() {
        return phongtros.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (phongtros.get(position) == null) return 0;
        return 1;
    }

    public class MyItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivAvatar;
        private TextView txtTen, txtGia;

        private ItemClickListener itemClickListener;

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }


        public MyItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            txtTen = itemView.findViewById(R.id.txtTen);
            txtGia = itemView.findViewById(R.id.txtGia);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false);
        }
    }

    public class MyNotItemViewHolder extends RecyclerView.ViewHolder {
        public MyNotItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    int[] data = new int[1];
                    String[] name = new String[1];
                    data[0] = khutro.getId();
                    name[0] = khutro.getTen();
                    bundle.putIntArray("data", data);
                    bundle.putStringArray("name", name);
                    FormFragment formFragment = new FormFragment();
                    formFragment.setArguments(bundle);
                    ((HomeActivity) activity).replaceFragment(formFragment, true);
                }
            });
        }
    }
}
