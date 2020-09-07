package com.example.managehouse.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.example.managehouse.Activity.HomeActivity;
import com.example.managehouse.Common.Common;
import com.example.managehouse.Model.Dashboard;
import com.example.managehouse.Model.Message;
import com.example.managehouse.R;
import com.example.managehouse.Retrofit.API;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SettingFragment extends Fragment {

    private SwitchCompat scThongBao;

    private HomeActivity homeActivity;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private API api;
    SharedPreferences sharedPreferences;

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
    }

    public void init() {
        scThongBao.setChecked(sharedPreferences.getBoolean("notification", true));
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
}