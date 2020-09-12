package com.example.managehouse.Fragment.KhuTro;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.example.managehouse.Adapter.ItemKhuTroPhongTroAdapter;
import com.example.managehouse.Adapter.ItemNguoiTroPhongTroAdapter;
import com.example.managehouse.Common.Common;
import com.example.managehouse.Model.Khutro;
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

    private TextView txtTen, txtDiaChi, txtNam, txtTrangThai, txtTotalPrice, txtPhongTroDaThue, txtPhongTroTrong, txtNumberPeople, txtYear;
    private ImageView ivAvatar;
    private LinearLayout llLoading, llYear, llLoadingPhongTro;
    private RecyclerView rvPhongTro;

    private API api;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private HomeActivity homeActivity;
    private Khutro khutro = null;
    private MaterialNumberPicker numberPicker;
    private int year, maxYear;
    private ItemKhuTroPhongTroAdapter itemKhuTroPhongTroAdapter;
    private List<Phongtro> phongtroList = new ArrayList<>();

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
        api = Common.getAPI();
        mapping(view);
        setValue();
        getNguoiTro();
        thongKe();
        return view;
    }

    public void mapping(View view) {
        homeActivity.ivAction.setImageResource(R.drawable.ic_edit);
        homeActivity.ivAction.setOnClickListener(this);
        txtTen = view.findViewById(R.id.txtTen);
        txtDiaChi = view.findViewById(R.id.txtDiaChi);
        txtNam = view.findViewById(R.id.txtNam);
        txtTrangThai = view.findViewById(R.id.txtTrangThai);
        ivAvatar = view.findViewById(R.id.ivAvatar);
        txtTotalPrice = view.findViewById(R.id.txtTotalPrice);
        txtPhongTroDaThue = view.findViewById(R.id.txtPhongTroDaThue);
        txtPhongTroTrong = view.findViewById(R.id.txtPhongTroTrong);
        txtNumberPeople = view.findViewById(R.id.txtNumberPeople);
        llLoading = view.findViewById(R.id.llLoading);
        llLoadingPhongTro = view.findViewById(R.id.llLoadingPhongTro);
        llYear= view.findViewById(R.id.llYear);
        llYear.setOnClickListener(this);
        txtYear= view.findViewById(R.id.txtYear);
        rvPhongTro = view.findViewById(R.id.rvPhongTro);
        rvPhongTro.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        rvPhongTro.setLayoutManager(gridLayoutManager);
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
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        year = maxYear = calendar.get(Calendar.YEAR);
        txtYear.setText(String.valueOf(year));
    }

    public void thongKe() {
        llLoading.setVisibility(View.VISIBLE);
        compositeDisposable.add(api.thongKeChiTiet(khutro.getId(),year).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
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
        phongtroList.add(null);
        itemKhuTroPhongTroAdapter = new ItemKhuTroPhongTroAdapter(getActivity(),phongtroList, khutro);
        rvPhongTro.addItemDecoration(new SpacesItemDecoration(10));
        rvPhongTro.setAdapter(itemKhuTroPhongTroAdapter);
        compositeDisposable.add(api.getKhuTroPhongTro(khutro.getId()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Phongtro>>() {
                    @Override
                    public void accept(List<Phongtro> phongtros) throws Exception {
                        phongtroList.clear();
                        phongtroList.add(null);
                        phongtroList.addAll(phongtros);
                        itemKhuTroPhongTroAdapter.notifyDataSetChanged();
                        llLoadingPhongTro.setVisibility(View.GONE);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        llLoadingPhongTro.setVisibility(View.GONE);
                        throwable.printStackTrace();
                    }
                }));
    }

    public void setValueThongKe(Thongkekhutro thongKe) {
        txtTotalPrice.setText(Common.formatNumber(thongKe.getTotal_price(),true));
        txtPhongTroDaThue.setText(String.valueOf(thongKe.getNumber_room_full()));
        txtPhongTroTrong.setText(String.valueOf(thongKe.getNumber_room_empty()));
        txtNumberPeople.setText(String.valueOf(thongKe.getNumber_people()));
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
                bundle.putSerializable("khutro", (Serializable) khutro);
                FormFragment formFragment = new FormFragment();
                formFragment.setArguments(bundle);
                homeActivity.replaceFragment(formFragment, true);
                break;
            }
        }
    }
}