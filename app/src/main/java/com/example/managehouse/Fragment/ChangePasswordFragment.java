package com.example.managehouse.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.custom.SimpleCustomValidation;
import com.example.managehouse.Activity.HomeActivity;
import com.example.managehouse.Common.Common;
import com.example.managehouse.Model.Message;
import com.example.managehouse.Model.User;
import com.example.managehouse.R;
import com.example.managehouse.Retrofit.API;
import com.example.managehouse.Service.DialogNotification;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ChangePasswordFragment extends Fragment implements View.OnClickListener {

    private EditText edtPassword, edtNewPassword, edtConfirmNewPassword;

    private HomeActivity homeActivity;
    private AwesomeValidation awesomeValidation;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private API api;
    private boolean checkFormChange = false;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    public static ChangePasswordFragment newInstance(String param1, String param2) {
        ChangePasswordFragment fragment = new ChangePasswordFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.checkFormChange = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        api = Common.getAPI();
        mapping(view);
        textChange();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        validator();
    }

    public void mapping(View view) {
        homeActivity.ivAction.setImageResource(R.drawable.ic_save_32dp);
        homeActivity.ivAction.setOnClickListener(this);
        edtPassword = view.findViewById(R.id.edtPassword);
        edtNewPassword = view.findViewById(R.id.edtNewPassword);
        edtConfirmNewPassword = view.findViewById(R.id.edtConfirmNewPassword);
    }

    public void textChange() {
        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    Common.checkFormChange = true;
                    checkFormChange = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    Common.checkFormChange = true;
                    checkFormChange = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtConfirmNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    Common.checkFormChange = true;
                    checkFormChange = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void validator() {
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(getActivity(), R.id.edtPassword, new SimpleCustomValidation() {
            @Override
            public boolean compare(String s) {
                if(s.length() >= 6) return true;
                return false;
            }
        }, R.string.password_length_validator);
        awesomeValidation.addValidation(getActivity(), R.id.edtNewPassword, new SimpleCustomValidation() {
            @Override
            public boolean compare(String s) {
                if(s.length() >= 6) return true;
                return false;
            }
        }, R.string.password_length_validator);
        awesomeValidation.addValidation(getActivity(), R.id.edtConfirmNewPassword, new SimpleCustomValidation() {
            @Override
            public boolean compare(String s) {
                if(s.length() >= 6) return true;
                return false;
            }
        }, R.string.password_length_validator);
    }

    public void update(String password, String newPassword, int type) {
        compositeDisposable.add(api.changePassword(password, newPassword, type).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Message>() {
                    @Override
                    public void accept(Message message) throws Exception {
                        if (message.getStatus() == 401) {
                            new DialogNotification(getActivity(), message.getBody(), "error").showDialog();
                        } else {
                            if (message.getStatus() == 402) {
                                Toasty.error(getContext(), message.getBody()[0], 300, true).show();
                            } else {
                                Toasty.success(getContext(), message.getBody()[0], 300, true).show();
                                Common.checkFormChange = false;
                                checkFormChange = false;
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toasty.error(getContext(), "Gặp sự cố, thử lại sau.", 300, true).show();
                        throwable.printStackTrace();
                    }
                }));
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof HomeActivity) {
            homeActivity = (HomeActivity) context;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivAction : {
                if(awesomeValidation.validate()) {
                    String newPassword = edtNewPassword.getText().toString();
                    String confirmNewPassword = edtConfirmNewPassword.getText().toString();
                    if(newPassword.equals(confirmNewPassword)) {
                        update(edtPassword.getText().toString(),newPassword,2);
                    }
                    else {
                        String[] notify = {"Nhập lại mật khẩu mới không khớp."};
                        DialogNotification dialogNotification = new DialogNotification(getActivity(),notify, "error");
                        dialogNotification.showDialog();
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
        Common.checkFormChange = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        Common.checkFormChange = checkFormChange;
        Common.posMenu = -1;
    }
}