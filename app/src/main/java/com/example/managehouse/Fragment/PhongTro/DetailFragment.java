package com.example.managehouse.Fragment.PhongTro;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.managehouse.Activity.HomeActivity;
import com.example.managehouse.Adapter.ItemNguoiTroPhongTroAdapter;
import com.example.managehouse.Common.Common;
import com.example.managehouse.Model.Khutro;
import com.example.managehouse.Model.Khutrokhoanthu;
import com.example.managehouse.Model.Message;
import com.example.managehouse.Model.Nguoitro;
import com.example.managehouse.Model.Phongtro;
import com.example.managehouse.Model.Thongkekhutro;
import com.example.managehouse.R;
import com.example.managehouse.Retrofit.API;
import com.example.managehouse.Service.SpacesItemDecoration;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DetailFragment extends Fragment {

    private TextView txtTen, txtKhuTro, txtGia, txtSoDien, txtSoNuoc, txtTrangThai, txtGhiChu, txtTotalPrice;
    private ImageView ivAvatar;
    private LinearLayout llThongKe, llSoDien, llSoNuoc;
    private LottieAnimationView lavLoading;
    private RecyclerView rvNguoiTro;

    private API api;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private HomeActivity homeActivity;
    private Phongtro phongtro = null;
    private List<Nguoitro> nguoitroList = new ArrayList<>();
    private ItemNguoiTroPhongTroAdapter itemNguoiTroPhongTroAdapter;

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
            phongtro = (Phongtro) getArguments().getSerializable("phongtro");
            nguoitroList = phongtro.getNguoitro();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_phongtro, container, false);
        api = Common.getAPI();
        mapping(view);
        setValue();
        showSoDienNuoc();
        setNguoiTro();
        thongKe();
        return view;
    }

    public void mapping(View view) {
        txtTen = view.findViewById(R.id.txtTen);
        txtKhuTro = view.findViewById(R.id.txtKhuTro);
        txtGia = view.findViewById(R.id.txtGia);
        txtSoDien = view.findViewById(R.id.txtSoDien);
        txtSoNuoc = view.findViewById(R.id.txtSoNuoc);
        txtTrangThai = view.findViewById(R.id.txtTrangThai);
        txtGhiChu = view.findViewById(R.id.txtGhiChu);
        txtTotalPrice = view.findViewById(R.id.txtTotalPrice);
        ivAvatar = view.findViewById(R.id.ivAvatar);
        llThongKe = view.findViewById(R.id.llThongKe);
        llSoDien = view.findViewById(R.id.llSoDien);
        llSoNuoc = view.findViewById(R.id.llSoNuoc);
        lavLoading = view.findViewById(R.id.lavLoading);
        rvNguoiTro = view.findViewById(R.id.rvNguoiTro);
        rvNguoiTro.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        rvNguoiTro.setLayoutManager(gridLayoutManager);
    }

    public void setNguoiTro() {
        if(nguoitroList.size() <= 0) {
            nguoitroList.add(null);
        }
        itemNguoiTroPhongTroAdapter = new ItemNguoiTroPhongTroAdapter(getActivity(),nguoitroList);
        rvNguoiTro.addItemDecoration(new SpacesItemDecoration(10));
        rvNguoiTro.setAdapter(itemNguoiTroPhongTroAdapter);
    }

    public void setValue() {
        if(phongtro.getImg() != null) {
            Picasso.get().load(phongtro.getImg()).placeholder(R.drawable.ic_home_32dp).error(R.drawable.ic_home_32dp).into(ivAvatar);
        }
        else {
            ivAvatar.setImageResource(R.drawable.ic_home_32dp);
        }
        txtTen.setText(phongtro.getTen());
        txtKhuTro.setText(phongtro.getKhutro().getTen());
        txtGia.setText(Common.formatMoney(phongtro.getGia()));
        txtGhiChu.setText(phongtro.getGhichu());
        txtSoDien.setText(String.valueOf(phongtro.getChotsodien()));
        txtSoNuoc.setText(String.valueOf(phongtro.getChotsonuoc()));
        String trangThai = "Đang sử dụng";
        String color = "#27ae60";
        if(phongtro.getStatus() == 0) {
            trangThai = "Không sử dụng";
            color = "#e74c3c";
        }
        txtTrangThai.setText(trangThai);
        txtTrangThai.setTextColor(Color.parseColor(color));
    }

    public void thongKe() {
        String url = "phongtro/"+phongtro.getId()+"/edit";
        compositeDisposable.add(api.thongKeChiTiet(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Message>() {
                    @Override
                    public void accept(Message message) throws Exception {
                        if (message.getStatus() == 201) {
                            Gson gson = new Gson();
                            Thongkekhutro thongkekhutro = gson.fromJson(message.getData(),Thongkekhutro.class);
                            setValueThongKe(thongkekhutro);
                            lavLoading.setVisibility(View.GONE);
                            llThongKe.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                }));
    }

    public void setValueThongKe(Thongkekhutro thongKe) {
        txtTotalPrice.setText(Common.formatMoney(thongKe.getTotal_price()));
    }

    public void showSoDienNuoc() {
        Khutro khutro = phongtro.getKhutro();
        for (Khutrokhoanthu khutrokhoanthu : khutro.getKhutrokhoanthu()) {
            if(khutrokhoanthu.getKhoanthu().getTen().equals("Điện")) {
                llSoDien.setVisibility(View.VISIBLE);
            }
            if(khutrokhoanthu.getKhoanthu().getTen().equals("Nước")) {
                if(khutrokhoanthu.getDonvitinh().getName().equals("Khối")) llSoNuoc.setVisibility(View.VISIBLE);
            }
        }
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