package com.example.managehouse.Adapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.managehouse.Activity.HomeActivity;
import com.example.managehouse.Fragment.KhuTro.DetailFragment;
import com.example.managehouse.Model.Khutro;
import com.example.managehouse.R;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

public class ItemKhuTroKhoanThuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private List<Khutro> khutros;

    public ItemKhuTroKhoanThuAdapter(Activity activity, List<Khutro> khutros) {
        this.activity = activity;
        this.khutros = khutros;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 0) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_not, parent,false);
            return new MyNotItemViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_khutro_khoanthu, parent,false);
            return new MyItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Khutro khutro = khutros.get(position);
        if(holder instanceof MyItemViewHolder) {
            if(khutro.getImg() != null) {
                Picasso.get().load(khutro.getImg()).placeholder(R.drawable.ic_hotel).error(R.drawable.ic_hotel).into(((MyItemViewHolder) holder).ivAvatar);
            }
            else {
                ((MyItemViewHolder) holder).ivAvatar.setImageResource(R.drawable.ic_hotel);
            }
            ((MyItemViewHolder) holder).txtTen.setText(khutro.getTen());
            ((MyItemViewHolder) holder).setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int pos, boolean isLongClick) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("khutro", (Serializable) khutros.get(pos));
                    DetailFragment detailFragment = new DetailFragment();
                    detailFragment.setArguments(bundle);
                    ((HomeActivity) activity).replaceFragment(detailFragment, true);
                }
            });
        }
        else {
            ((MyNotItemViewHolder) holder).txtTen.setText("Không có khu trọ sử dụng khoản thu này");
        }
    }

    @Override
    public int getItemCount() {
        return khutros.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(khutros.get(position) == null) return 0;
        return 1;
    }

    public class MyItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView ivAvatar;
        private TextView txtTen;

        private ItemClickListener itemClickListener;

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }


        public MyItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            txtTen = itemView.findViewById(R.id.txtTen);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v,getAdapterPosition(),false);
        }
    }

    public class MyNotItemViewHolder extends RecyclerView.ViewHolder{

        private TextView txtTen;

        public MyNotItemViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTen = itemView.findViewById(R.id.txtTenNguoi);
        }
    }
}
