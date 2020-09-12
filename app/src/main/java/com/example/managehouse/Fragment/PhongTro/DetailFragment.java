package com.example.managehouse.Fragment.PhongTro;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DetailFragment extends Fragment implements View.OnClickListener {

    private TextView txtTen, txtKhuTro, txtGia, txtSoDien, txtSoNuoc, txtTrangThai, txtGhiChu, txtTotalPrice, txtYear;
    private ImageView ivAvatar;
    private LinearLayout llLoading, llSoDien, llSoNuoc, llYear, llLoadingNguoiTro;
    private RecyclerView rvNguoiTro;

    private API api;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private HomeActivity homeActivity;
    private Phongtro phongtro = null;
    private List<Nguoitro> nguoitroList = new ArrayList<>();
    private ItemNguoiTroPhongTroAdapter itemNguoiTroPhongTroAdapter;
    private MaterialNumberPicker numberPicker;
    private int year, maxYear;

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
        getNguoiTro();
        thongKe();
        return view;
    }

    public void mapping(View view) {
        homeActivity.ivAction.setImageResource(R.drawable.ic_edit);
        homeActivity.ivAction.setOnClickListener(this);
        txtTen = view.findViewById(R.id.txtTen);
        txtKhuTro = view.findViewById(R.id.txtKhuTro);
        txtGia = view.findViewById(R.id.txtGia);
        txtSoDien = view.findViewById(R.id.txtSoDien);
        txtSoNuoc = view.findViewById(R.id.txtSoNuoc);
        txtTrangThai = view.findViewById(R.id.txtTrangThai);
        txtGhiChu = view.findViewById(R.id.txtGhiChu);
        txtTotalPrice = view.findViewById(R.id.txtTotalPrice);
        txtYear = view.findViewById(R.id.txtYear);
        ivAvatar = view.findViewById(R.id.ivAvatar);
        llLoading = view.findViewById(R.id.llLoading);
        llLoadingNguoiTro = view.findViewById(R.id.llLoadingNguoiTro);
        llSoDien = view.findViewById(R.id.llSoDien);
        llSoNuoc = view.findViewById(R.id.llSoNuoc);
        llYear = view.findViewById(R.id.llYear);
        llYear.setOnClickListener(this);
        rvNguoiTro = view.findViewById(R.id.rvNguoiTro);
        rvNguoiTro.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        rvNguoiTro.setLayoutManager(gridLayoutManager);
    }

    public void initNumberPicker() {
        numberPicker = new MaterialNumberPicker.Builder(getContext())
                .minValue(1997)
                .maxValue(maxYear)
                .defaultValue(year)
                .backgroundColor(Color.WHITE)
                .separatorColor(Color.TRANSPARENT)
                .textColor(R.color.colorPrimary)
                .textSize(20)
                .enableFocusability(false)
                .wrapSelectorWheel(true)
                .build();
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
        txtGia.setText(Common.formatNumber(phongtro.getGia(),true));
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
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        year = maxYear = calendar.get(Calendar.YEAR);
        txtYear.setText(String.valueOf(year));
    }

    public void thongKe() {
        llLoading.setVisibility(View.VISIBLE);
        compositeDisposable.add(api.thongKeChiTietPhongTro(phongtro.getId(),year).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Message>() {
                    @Override
                    public void accept(Message message) throws Exception {
                        if (message.getStatus() == 201) {
                            Gson gson = new Gson();
                            Thongkekhutro thongkekhutro = gson.fromJson(message.getData(),Thongkekhutro.class);
                            setValueThongKe(thongkekhutro);
                            llLoading.setVisibility(View.GONE);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                }));
    }

    public void getNguoiTro() {
        itemNguoiTroPhongTroAdapter = new ItemNguoiTroPhongTroAdapter(getActivity(),nguoitroList, phongtro);
        rvNguoiTro.addItemDecoration(new SpacesItemDecoration(10));
        rvNguoiTro.setAdapter(itemNguoiTroPhongTroAdapter);
        compositeDisposable.add(api.getNguoiTroPhongTro(phongtro.getId()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Nguoitro>>() {
                    @Override
                    public void accept(List<Nguoitro> nguoitros) throws Exception {
                        nguoitroList.clear();
                        nguoitroList.add(null);
                        nguoitroList.addAll(nguoitros);
                        itemNguoiTroPhongTroAdapter.notifyDataSetChanged();
                        llLoadingNguoiTro.setVisibility(View.GONE);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        llLoadingNguoiTro.setVisibility(View.GONE);
                        throwable.printStackTrace();
                    }
                }));
    }

    public void setValueThongKe(Thongkekhutro thongKe) {
        txtTotalPrice.setText(Common.formatNumber(thongKe.getTotal_price(),true));
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llYear : {
                initNumberPicker();
                new AlertDialog.Builder(getContext())
                        .setTitle("Chọn năm")
                        .setView(numberPicker)
                        .setPositiveButton(R.string.confirm_delete_button_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                year = numberPicker.getValue();
                                txtYear.setText(String.valueOf(year));
                                thongKe();
                            }
                        })
                        .setNegativeButton(R.string.confirm_delete_button_no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).create().show();
                break;
            }
            case R.id.ivAction : {
                Bundle bundle = new Bundle();
                bundle.putSerializable("phongtro", (Serializable) phongtro);
                FormFragment formFragment = new FormFragment();
                formFragment.setArguments(bundle);
                homeActivity.replaceFragment(formFragment, true);
                break;
            }
        }
    }
}