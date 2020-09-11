package com.example.managehouse.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.custom.SimpleCustomValidation;
import com.example.managehouse.Activity.HomeActivity;
import com.example.managehouse.Activity.MainActivity;
import com.example.managehouse.Common.Common;
import com.example.managehouse.Model.Message;
import com.example.managehouse.Model.User;
import com.example.managehouse.R;
import com.example.managehouse.Retrofit.API;
import com.example.managehouse.Retrofit.RetrofitClient;
import com.example.managehouse.Service.DialogLoading;
import com.example.managehouse.Service.DialogNotification;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class LoginFragment extends Fragment implements View.OnClickListener {


    private TextView txtRegister;
    private EditText edtUsername, edtPassword;
    private ImageView ivHidePassword;

    private MainActivity mainActivity;
    private AwesomeValidation awesomeValidation;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private API api;
    private SharedPreferences sharedPreferences;
    private boolean hidePassword = true;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mapping(view);
        //init api
        api = Common.getAPI();
        setValue();
        return view;
    }

    public void mapping(View view) {
        edtPassword = view.findViewById(R.id.edtPassword);
        edtUsername = view.findViewById(R.id.edtUsername);
        txtRegister = view.findViewById(R.id.txtRegister);
        ivHidePassword = view.findViewById(R.id.ivHidePassword);
        ivHidePassword.setOnClickListener(this);
        txtRegister.setOnClickListener(this);
        Button btnLogin = view.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
    }

    public void setValue() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("account",Context.MODE_PRIVATE);
        if(sharedPreferences != null) {
            edtUsername.setText(sharedPreferences.getString("username",  ""));
            edtPassword.setText(sharedPreferences.getString("password",  ""));
            sharedPreferences.edit().clear().commit();
        }
    }

    public void login(final String username, String password, int remember) {
        final DialogLoading dialogLoading = new DialogLoading(getActivity(), "Đang đăng nhập...");
        dialogLoading.showDialog();
        compositeDisposable.add(api.login(username,password,Common.getDeviceToken(getContext()),remember).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Message>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void accept(Message message) throws Exception {
                        dialogLoading.hideDialog();
                        if(message.getStatus() == 401) {
                            new DialogNotification(getActivity(),message.getBody(),"error").showDialog();
                        }
                        else {
                            if(message.getStatus() == 402) {
                                Toasty.error(getContext(), message.getBody()[0], 300, true).show();
                            }
                            else {
                                Gson gson = new Gson();
                                User user = gson.fromJson(message.getData(),User.class);
                                Common.currentUser = user;
                                saveUserLogin(user);
                                Common.token = user.getToken_type() + " " + user.getAccess_token();
                                Intent intent = new Intent(mainActivity, HomeActivity.class);
                                mainActivity.startActivity(intent);
                                mainActivity.finish();
                                Toasty.success(getContext(),message.getBody()[0], 300, true).show();
                                RetrofitClient.instance = null;
                            }
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
        awesomeValidation.addValidation(getActivity(),R.id.edtUsername, "^[A-Za-z0-9]+$", R.string.username_validator);
        awesomeValidation.addValidation(getActivity(), R.id.edtUsername, new SimpleCustomValidation() {
            @Override
            public boolean compare(String s) {
                if(s.length() != 0) return true;
                return false;
            }
        }, R.string.name_null_validator);
        awesomeValidation.addValidation(getActivity(), R.id.edtPassword, new SimpleCustomValidation() {
            @Override
            public boolean compare(String s) {
                if(s.length() >= 6) return true;
                return false;
            }
        }, R.string.password_length_validator);
    }

    public void saveUserLogin(User user) throws NoSuchPaddingException, UnsupportedEncodingException, InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidParameterSpecException {
        sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("id",String.valueOf(user.getId()));
        editor.putString("name",user.getName());
        editor.putString("avatar",(user.getAvatar() == null) ? "" : user.getAvatar());
        editor.putString("phone",user.getPhone());
        editor.putString("email",user.getEmail());
        editor.putString("username",user.getUsername());
        editor.putString("address",user.getAddress());
        editor.putString("roles",user.getRoles());
        editor.putString("created_at",user.getCreated_at());
        editor.putString("updated_at",user.getUpdated_at());
        editor.putString("access_token",user.getAccess_token());
        editor.putString("token_type",user.getToken_type());
        editor.putString("expires_at",user.getExpires_at());
        editor.commit();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof MainActivity) mainActivity = (MainActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mainActivity = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        validator();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin: {
                if(awesomeValidation.validate()) {
                    login(edtUsername.getText().toString(),
                            edtPassword.getText().toString(),
                            1);
                }
                break;
            }
            case R.id.txtRegister: {
                mainActivity.lavLogin.playAnimation();
                mainActivity.replaceFragment(new RegisterFragment(), false);
                break;
            }
            case R.id.ivHidePassword : {
                if(hidePassword) {
                    ivHidePassword.setImageResource(R.drawable.ic_eye_hide);
                    edtPassword.setTransformationMethod(null);
                }
                else {
                    ivHidePassword.setImageResource(R.drawable.ic_eye);
                    edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                hidePassword = !hidePassword;
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
