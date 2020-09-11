package com.example.managehouse.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.managehouse.Activity.HomeActivity;
import com.example.managehouse.Callback.ChosenItemCallback;
import com.example.managehouse.Common.Common;
import com.example.managehouse.Model.Config;
import com.example.managehouse.Model.Dashboard;
import com.example.managehouse.Model.Item;
import com.example.managehouse.Model.Khutro;
import com.example.managehouse.Model.Message;
import com.example.managehouse.R;
import com.example.managehouse.Retrofit.API;
import com.example.managehouse.Service.DialogChosenItem;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

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

public class SettingFragment extends Fragment implements View.OnClickListener, ChosenItemCallback {

    private SwitchCompat scThongBao;
    private TextView txtThuTien;

    private HomeActivity homeActivity;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private API api;
    private SharedPreferences sharedPreferences;
    private List<Config> configList = new ArrayList<>();
    private MaterialNumberPicker numberPicker;
    private int dayDefault = 1;

    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
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
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        api = Common.getAPI();
        sharedPreferences = getContext().getSharedPreferences("token", Context.MODE_PRIVATE);
        mapping(view);
        init();
        getConfig();
        return view;
    }

    public void mapping(View view) {
        scThongBao = view.findViewById(R.id.scThongBao);
        scThongBao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateNotification((isChecked) ? 1 : 0, sharedPreferences.getString("device",null));
            }
        });
        txtThuTien = view.findViewById(R.id.txtThuTien);
        txtThuTien.setOnClickListener(this);
        txtThuTien.setTextColor(Color.parseColor("#27ae60"));
    }

    public void init() {
        scThongBao.setChecked(sharedPreferences.getBoolean("notification", true));
        txtThuTien.setTag(0);
    }

    public void updateNotification(int checked, String token) {
        compositeDisposable.add(api.switchNotification(checked,token).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Message>() {
                    @Override
                    public void accept(Message message) throws Exception {
                        if(message.getStatus() == 201) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("notification",(checked == 1) ? true : false);
                            editor.commit();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        throwable.printStackTrace();
                    }
                }));
    }

    public void getConfig() {
        compositeDisposable.add(api.getConfig().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Config>>() {
                    @Override
                    public void accept(List<Config> configs) throws Exception {
                        configList = configs;
                        for (Config config : configList) {
                            if(config.getName().equals("thu-tien")) {
                                if(config.getUser_id() == Common.currentUser.getId()) {
                                    txtThuTien.setTag(config.getValue());
                                    txtThuTien.setText(config.getText());
                                    dayDefault = Integer.parseInt(config.getText());
                                }

                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        throwable.printStackTrace();
                    }
                }));
    }

    public void updateConfig(String name, int value, String text) {
        compositeDisposable.add(api.updateConfig(name,value,Common.currentUser.getId(),text).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Message>() {
                    @Override
                    public void accept(Message message) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        throwable.printStackTrace();
                    }
                }));
    }

    public void initNumberPicker() {
        numberPicker = new MaterialNumberPicker.Builder(getContext())
                .minValue(1)
                .maxValue(31)
                .defaultValue(dayDefault)
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
        if(context instanceof HomeActivity) homeActivity = (HomeActivity) context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        Common.posMenu = 8;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtThuTien : {
                int id = Integer.parseInt(txtThuTien.getTag().toString());
                List<Item> items = new ArrayList<>();
                items.add(new Item((id == 0) ? true : false, 0,0,"Theo từng phòng"));
                items.add(new Item((id == -1) ? true : false, -1,1,"Chọn ngày"));
                DialogChosenItem dialogChosen = new DialogChosenItem(getActivity(), items, "Chọn ngày thông báo thu tiền", "single",0, false);
                dialogChosen.setChosenItemCallback(this);
                dialogChosen.showDialog();
                break;
            }
        }
    }

    @Override
    public void onReceiveItem(List<Item> item) {
        String name = "thu-tien";
        if(item.get(0).getId() == -1) {
            txtThuTien.setTag(item.get(0).getId());
            initNumberPicker();
            new AlertDialog.Builder(getContext())
                    .setTitle("Chọn ngày thông báo")
                    .setView(numberPicker)
                    .setPositiveButton(R.string.confirm_delete_button_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int day = numberPicker.getValue();
                            txtThuTien.setText(day + "");
                            updateConfig(name,item.get(0).getId(),String.valueOf(day));
                        }
                    })
                    .setNegativeButton(R.string.confirm_delete_button_no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).create().show();
        }
        else {
            txtThuTien.setTag(item.get(0).getId());
            txtThuTien.setText(item.get(0).getName());
            updateConfig(name,item.get(0).getId(),item.get(0).getName());
        }
    }
}