package com.example.managehouse.Fragment.KhuTro;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.airbnb.lottie.LottieAnimationView;
import com.example.managehouse.Activity.HomeActivity;
import com.example.managehouse.Common.Common;
import com.example.managehouse.Model.Khutro;
import com.example.managehouse.Model.Message;
import com.example.managehouse.Model.Thongkekhutro;
import com.example.managehouse.R;
import com.example.managehouse.Retrofit.API;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DetailFragment extends Fragment implements View.OnClickListener {

    private TextView txtTen, txtDiaChi, txtNam, txtTrangThai, txtTotalPrice, txtNumberRoom, txtNumberPeople, txtYear;
    private ImageView ivAvatar;
    private LinearLayout llLoading, llYear;

    private API api;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private HomeActivity homeActivity;
    private Khutro khutro = null;
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
        thongKe();
        return view;
    }

    public void mapping(View view) {
        txtTen = view.findViewById(R.id.txtTen);
        txtDiaChi = view.findViewById(R.id.txtDiaChi);
        txtNam = view.findViewById(R.id.txtNam);
        txtTrangThai = view.findViewById(R.id.txtTrangThai);
        ivAvatar = view.findViewById(R.id.ivAvatar);
        txtTotalPrice = view.findViewById(R.id.txtTotalPrice);
        txtNumberRoom = view.findViewById(R.id.txtNumberRoom);
        txtNumberPeople = view.findViewById(R.id.txtNumberPeople);
        llLoading = view.findViewById(R.id.llLoading);
        homeActivity.ivAction.setVisibility(View.GONE);
        llYear= view.findViewById(R.id.llYear);
        llYear.setOnClickListener(this);
        txtYear= view.findViewById(R.id.txtYear);
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

    public void setValueThongKe(Thongkekhutro thongKe) {
        txtTotalPrice.setText(Common.formatNumber(thongKe.getTotal_price(),true));
        txtNumberRoom.setText(String.valueOf(thongKe.getNumber_room()));
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
        homeActivity.ivAction.setVisibility(View.VISIBLE);
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
        }
    }
}