package com.example.managehouse.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.custom.SimpleCustomValidation;
import com.example.managehouse.Activity.MainActivity;
import com.example.managehouse.Common.Common;
import com.example.managehouse.Model.Message;
import com.example.managehouse.Model.User;
import com.example.managehouse.R;
import com.example.managehouse.Retrofit.API;
import com.example.managehouse.Service.DialogLoading;
import com.example.managehouse.Service.DialogNotification;

import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class RegisterFragment extends Fragment implements View.OnClickListener {

    private EditText edtName, edtUsername, edtPassword, edtPasswordConfirm;

    private MainActivity mainActivity;
    private API api;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private AwesomeValidation awesomeValidation;

    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.token = "login";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        mapping(view);
        api = Common.getAPI();
        return view;
    }

    public void mapping(View view) {
        TextView txtLogin = view.findViewById(R.id.txtLogin);
        edtName = view.findViewById(R.id.edtName);
        edtUsername = view.findViewById(R.id.edtUsername);
        edtPassword = view.findViewById(R.id.edtPassword);
        edtPasswordConfirm = view.findViewById(R.id.edtPasswordConfirm);
        Button btnRegister = view.findViewById(R.id.btnRegister);
        txtLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    public void register(String name, String username, String password, String passwordConfirm) {
        final DialogLoading dialogLoading = new DialogLoading(getActivity(), "Đang đăng ký...");
        dialogLoading.showDialog();
        compositeDisposable.add(api.register(name,username,Common.getDeviceToken(getContext()),password,passwordConfirm).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Message>() {
                    @Override
                    public void accept(Message message) throws Exception {
                        dialogLoading.hideDialog();
                        if(message.getStatus() == 401) {
                            new DialogNotification(getActivity(),message.getBody(),"error").showDialog();
                        }
                        else {
                            Toasty.success(getContext(), "Đăng ký tài khoản thành công.", 300, true).show();
                            mainActivity.replaceFragment(new LoginFragment(), false);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        dialogLoading.hideDialog();
                        Toasty.error(getContext(), "Gặp sự cố, thử lại sau.", 300, true).show();
                        throwable.printStackTrace();
                    }
                }));
    }

    public void validator() {
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(getActivity(), R.id.edtName, new SimpleCustomValidation() {
            @Override
            public boolean compare(String s) {
                if(s.length() != 0) return true;
                return false;
            }
        }, R.string.name_null_validator);
        awesomeValidation.addValidation(getActivity(),R.id.edtUsername, "^[A-Za-z0-9]+$", R.string.username_validator);
        awesomeValidation.addValidation(getActivity(), R.id.edtPassword, new SimpleCustomValidation() {
            @Override
            public boolean compare(String s) {
                if(s.length() >= 6) return true;
                return false;
            }
        }, R.string.password_length_validator);
        awesomeValidation.addValidation(getActivity(), R.id.edtPasswordConfirm, new SimpleCustomValidation() {
            @Override
            public boolean compare(String s) {
                if(s.length() >= 6) return true;
                return false;
            }
        }, R.string.password_length_validator);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        validator();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) mainActivity = (MainActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mainActivity = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtLogin: {
                mainActivity.lavLogin.playAnimation();
                mainActivity.replaceFragment(new LoginFragment(), false);
                break;
            }
            case R.id.btnRegister: {
                if(awesomeValidation.validate()) {
                    String password = edtPassword.getText().toString();
                    String passwordConfirm = edtPasswordConfirm.getText().toString();
                    if(!password.equals(passwordConfirm)) {
                        Toasty.warning(getContext(), "Nhập lại mật khẩu không đúng.", 300, true).show();
                    }
                    else {
                        register(edtName.getText().toString(),
                                edtUsername.getText().toString(),
                                edtPassword.getText().toString(),
                                edtPasswordConfirm.getText().toString());
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
    }
}
