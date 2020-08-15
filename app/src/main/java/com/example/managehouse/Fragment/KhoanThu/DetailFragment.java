package com.example.managehouse.Fragment.KhoanThu;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.managehouse.Activity.HomeActivity;
import com.example.managehouse.Adapter.ItemKhuTroKhoanThuAdapter;
import com.example.managehouse.Adapter.ItemNguoiTroPhongTroAdapter;
import com.example.managehouse.Common.Common;
import com.example.managehouse.Model.Khoanthu;
import com.example.managehouse.Model.Khutro;
import com.example.managehouse.R;
import com.example.managehouse.Retrofit.API;
import com.example.managehouse.Service.SpacesItemDecoration;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class DetailFragment extends Fragment {

    private TextView txtTen, txtMoTa, txtTrangThai;
    private ImageView ivAvatar;
    private LottieAnimationView lavLoading;
    private RecyclerView rvKhuTro;

    private API api;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private HomeActivity homeActivity;
    private Khoanthu khoanthu = null;
    private List<Khutro> khutroList = new ArrayList<>();
    private ItemKhuTroKhoanThuAdapter itemKhuTroKhoanThuAdapter = null;

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
            khoanthu = (Khoanthu) getArguments().getSerializable("khoanthu");
            khutroList = khoanthu.getKhutro();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_khoanthu, container, false);
        api = Common.getAPI();
        mapping(view);
        setValue();
        setKhuTro();
        return view;
    }

    public void mapping(View view) {
        txtTen = view.findViewById(R.id.txtTen);
        txtMoTa = view.findViewById(R.id.txtMoTa);
        txtTrangThai = view.findViewById(R.id.txtTrangThai);
        ivAvatar = view.findViewById(R.id.ivAvatar);
        lavLoading = view.findViewById(R.id.lavLoading);
        rvKhuTro = view.findViewById(R.id.rvKhuTro);
        homeActivity.ivAction.setVisibility(View.GONE);
        rvKhuTro.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        rvKhuTro.setLayoutManager(gridLayoutManager);
    }

    public void setValue() {
        if(khoanthu.getAvatar() != null) {
            Picasso.get().load(khoanthu.getAvatar()).placeholder(R.drawable.ic_energy).error(R.drawable.ic_energy).into(ivAvatar);
        }
        else {
            ivAvatar.setImageResource(R.drawable.ic_energy);
        }
        txtTen.setText(khoanthu.getTen());
        txtMoTa.setText(khoanthu.getMota());
        String trangThai = "Đang sử dụng";
        String color = "#27ae60";
        if(khoanthu.getStatus() == 0) {
            trangThai = "Không sử dụng";
            color = "#e74c3c";
        }
        txtTrangThai.setText(trangThai);
        txtTrangThai.setTextColor(Color.parseColor(color));
    }

    public void setKhuTro() {
        if(khutroList.size() <= 0) {
            khutroList.add(null);
        }
        itemKhuTroKhoanThuAdapter = new ItemKhuTroKhoanThuAdapter(getActivity(),khutroList);
        rvKhuTro.addItemDecoration(new SpacesItemDecoration(10));
        rvKhuTro.setAdapter(itemKhuTroKhoanThuAdapter);
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