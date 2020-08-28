package com.example.managehouse.Fragment.ThongKe;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.managehouse.Activity.HomeActivity;
import com.example.managehouse.Callback.ChosenItemCallback;
import com.example.managehouse.Common.Common;
import com.example.managehouse.Fragment.KhuTro.FormFragment;
import com.example.managehouse.Model.Item;
import com.example.managehouse.Model.Khutro;
import com.example.managehouse.Model.Message;
import com.example.managehouse.Model.Thongkekhutro;
import com.example.managehouse.R;
import com.example.managehouse.Retrofit.API;
import com.example.managehouse.Service.DialogChosenItem;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ThongKeKhuTroFragment extends Fragment implements View.OnClickListener, ChosenItemCallback {

    private TextView txtKhuTro,txtTotalPrice, txtNumberRoom, txtNumberPeople, txtYear;
    private LinearLayout llLoading, llKhuTro, llYear;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private API api;
    private HomeActivity homeActivity;
    private List<Khutro> khutroList = new ArrayList<>();
    private int sttKhuTro = 0;
    private MaterialNumberPicker numberPicker;
    private int year, maxYear;

    public ThongKeKhuTroFragment() {
        // Required empty public constructor
    }

    public static ThongKeKhuTroFragment newInstance(String param1, String param2) {
        ThongKeKhuTroFragment fragment = new ThongKeKhuTroFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_thong_ke_khu_tro, container, false);
        api = Common.getAPI();
        mapping(view);
        setValueDefault();
        getKhutro();
        return view;
    }

    public void mapping(View view) {
        txtKhuTro = view.findViewById(R.id.txtKhuTro);
        llLoading = view.findViewById(R.id.llLoading);
        llYear = view.findViewById(R.id.llYear);
        llYear.setOnClickListener(this);
        llKhuTro = view.findViewById(R.id.llKhuTro);
        llKhuTro.setOnClickListener(this);
        txtTotalPrice = view.findViewById(R.id.txtTotalPrice);
        txtNumberRoom = view.findViewById(R.id.txtNumberRoom);
        txtNumberPeople = view.findViewById(R.id.txtNumberPeople);
        txtYear = view.findViewById(R.id.txtYear);
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

    public void setValueTxtKhutro(String name, int id) {
        txtKhuTro.setTag(id);
        txtKhuTro.setText(name);
    }

    public void setValueDefault() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        year = maxYear = calendar.get(Calendar.YEAR);
        txtYear.setText(String.valueOf(year));
    }

    public void getKhutro() {
        llLoading.setVisibility(View.VISIBLE);
        sttKhuTro = 0;
        String url = "khutro/1";
        compositeDisposable.add(api.getKhutroChosen(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Khutro>>() {
                    @Override
                    public void accept(List<Khutro> khutros) throws Exception {
                        khutroList = khutros;
                        if(khutroList.size() > 0) {
                            setValueTxtKhutro(khutroList.get(0).getTen(), khutroList.get(0).getId());
                            thongKe();
                        }
                        else {
                            setValueTxtKhutro("Không có khu trọ", -1);

                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toasty.error(getContext(),"Gặp sự cố, thử lại sau.", Toasty.LENGTH_SHORT);
                        llLoading.setVisibility(View.GONE);
                        throwable.printStackTrace();
                    }
                }));
    }

    public void thongKe() {
        llLoading.setVisibility(View.VISIBLE);
        compositeDisposable.add(api.thongKeChiTiet(khutroList.get(sttKhuTro).getId(),year).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Message>() {
                    @Override
                    public void accept(Message message) throws Exception {
                        if (message.getStatus() == 201) {
                            Log.d("cuong", "ok");
                            Gson gson = new Gson();
                            Thongkekhutro thongkekhutro = gson.fromJson(message.getData(),Thongkekhutro.class);
                            setValueThongKe(thongkekhutro);
                            llLoading.setVisibility(View.GONE);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toasty.error(getContext(),"Gặp sự cố, thử lại sau.", Toasty.LENGTH_SHORT);
                        llLoading.setVisibility(View.GONE);
                        throwable.printStackTrace();
                    }
                }));
    }

    public void setValueThongKe(Thongkekhutro thongKe) {
        txtTotalPrice.setText(Common.formatNumber(thongKe.getTotal_price(),true));
        txtNumberRoom.setText(String.valueOf(thongKe.getNumber_room()));
        txtNumberPeople.setText(String.valueOf(thongKe.getNumber_people()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof HomeActivity) homeActivity = (HomeActivity) context;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llKhuTro : {
                if(khutroList.size() > 0) {
                    int id = Integer.parseInt(txtKhuTro.getTag().toString());
                    List<Item> items = new ArrayList<>();
                    int stt = 0;
                    for (Khutro khutro : khutroList) {
                        if (khutro.getId() == id) items.add(new Item(true, khutro.getId(), stt, khutro.getTen()));
                        else items.add(new Item(false, khutro.getId(), stt, khutro.getTen()));
                        stt++;
                    }
                    DialogChosenItem dialogChosen = new DialogChosenItem(getActivity(), items, "Chọn khu trọ", "single",sttKhuTro, true);
                    dialogChosen.setChosenItemCallback(this);
                    dialogChosen.showDialog();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Không có khu trọ nào, hãy thêm khu trọ mới.")
                            .setPositiveButton(R.string.confirm_delete_button_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FormFragment formFragment = new FormFragment();
                                    homeActivity.replaceFragment(formFragment,true);
                                }
                            })
                            .setNegativeButton(R.string.confirm_delete_button_no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create().show();
                }
                break;
            }
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

    @Override
    public void onReceiveItem(List<Item> item) {
        setValueTxtKhutro(item.get(0).getName(), item.get(0).getId());
        sttKhuTro = item.get(0).getStt();
        thongKe();
    }
}