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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.managehouse.Activity.HomeActivity;
import com.example.managehouse.Common.Common;
import com.example.managehouse.Model.Khutro;
import com.example.managehouse.Model.Message;
import com.example.managehouse.Model.Thongkekhutro;
import com.example.managehouse.R;
import com.example.managehouse.Retrofit.API;
import com.example.managehouse.Service.DialogNotification;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class DetailFragment extends Fragment {

    private TextView txtTen, txtDiaChi, txtNam, txtTrangThai, txtTotalPrice, txtNumberRoom, txtNumberPeople;
    private ImageView ivAvatar;
    private LottieAnimationView lavLoading;
    private LinearLayout llThongKe;

    private API api;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
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
        api = Common.getAPI();
        mapping(view);
        setValue();
        thongKeKhuTro();
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
        lavLoading = view.findViewById(R.id.lavLoading);
        llThongKe = view.findViewById(R.id.llThongKe);
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

    public void thongKeKhuTro() {
        String url = "khutro/"+khutro.getId()+"/edit";
        compositeDisposable.add(api.thongKeKhuTro(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
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
        txtNumberRoom.setText(String.valueOf(thongKe.getNumber_room()));
        txtNumberPeople.setText(String.valueOf(thongKe.getNumber_people()));
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