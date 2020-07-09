package com.example.managehouse.Fragment.KhuTro;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.managehouse.Activity.HomeActivity;
import com.example.managehouse.Model.Khutro;
import com.example.managehouse.R;
import com.squareup.picasso.Picasso;

public class DetailFragment extends Fragment {

    private TextView txtTen, txtDiaChi, txtNam, txtTrangThai;
    private ImageView ivAvatar;

    private HomeActivity homeActivity;
    private Khutro khutro = null;

    public DetailFragment() {
        // Required empty public constructor
    }

    public static DetailFragment newInstance(String param1, String param2) {
        DetailFragment fragment = new DetailFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            khutro = (Khutro) getArguments().getSerializable("khutro");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_khutro, container, false);
        mapping(view);
        setValue();
        return view;
    }

    public void mapping(View view) {
        txtTen = view.findViewById(R.id.txtTen);
        txtDiaChi = view.findViewById(R.id.txtDiaChi);
        txtNam = view.findViewById(R.id.txtNam);
        txtTrangThai = view.findViewById(R.id.txtTrangThai);
        ivAvatar = view.findViewById(R.id.ivAvatar);
        homeActivity.ivAction.setVisibility(View.GONE);
    }

    public void setValue() {
        if(khutro.getImg() != null) {
            Picasso.get().load(khutro.getImg()).placeholder(R.drawable.ic_hotel).error(R.drawable.ic_hotel).into(ivAvatar);
        }
        else {
            ivAvatar.setImageResource(R.drawable.ic_hotel);
        }
        txtTen.setText(khutro.getTen());
        txtDiaChi.setText(khutro.getDiachi());
        txtNam.setText(khutro.getNam_xd());
        String trangThai = "Đang sử dụng";
        String color = "#27ae60";
        if(khutro.getStatus() == 0) {
            trangThai = "Không sử dụng";
            color = "#e74c3c";
        }
        txtTrangThai.setText(trangThai);
        txtTrangThai.setTextColor(Color.parseColor(color));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof HomeActivity) {
            homeActivity = (HomeActivity) context;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        homeActivity.ivAction.setVisibility(View.VISIBLE);
    }
}