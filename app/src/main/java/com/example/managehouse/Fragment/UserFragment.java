package com.example.managehouse.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.custom.SimpleCustomValidation;
import com.example.managehouse.Activity.HomeActivity;
import com.example.managehouse.Adapter.ItemKhoanThuKhuTro;
import com.example.managehouse.Common.Common;
import com.example.managehouse.Model.Item;
import com.example.managehouse.Model.Khoanthu;
import com.example.managehouse.Model.Khutro;
import com.example.managehouse.Model.Message;
import com.example.managehouse.Model.User;
import com.example.managehouse.R;
import com.example.managehouse.Retrofit.API;
import com.example.managehouse.Service.DialogChosenItem;
import com.example.managehouse.Service.DialogNotification;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UserFragment extends Fragment implements View.OnClickListener {

    private ImageView ivAvatar;
    private EditText edtTen, edtDiaChi, edtEmail, edtPhone;
    private Button btnChonAnh, btnXoaAnh, btnChangePass;

    private HomeActivity homeActivity;
    private AwesomeValidation awesomeValidation;
    private Uri uriAvatar = null;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private API api;
    private boolean checkFormChange = false;
    private SharedPreferences sharedPreferences;

    public UserFragment() {
        // Required empty public constructor
    }

    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
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
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        api = Common.getAPI();
        mapping(view);
        textChange();
        setValue();
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
        ivAvatar = view.findViewById(R.id.ivAvatar);
        ivAvatar.setOnClickListener(this);
        edtTen = view.findViewById(R.id.edtTen);
        edtDiaChi = view.findViewById(R.id.edtDiaChi);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPhone = view.findViewById(R.id.edtPhone);
        btnChonAnh = view.findViewById(R.id.btnChonAnh);
        btnChonAnh.setOnClickListener(this);
        btnXoaAnh = view.findViewById(R.id.btnXoaAnh);
        btnXoaAnh.setOnClickListener(this);
        btnChangePass = view.findViewById(R.id.btnChangePass);
        btnChangePass.setOnClickListener(this);
        btnXoaAnh.setVisibility(View.GONE);
    }

    private void setValue() {
        if (!Common.currentUser.getAvatar().equals("")) {
            Picasso.get().load(Common.currentUser.getAvatar()).placeholder(R.drawable.ic_account_blue).error(R.drawable.ic_account_blue).into(ivAvatar);
            btnXoaAnh.setVisibility(View.VISIBLE);
        } else {
            ivAvatar.setImageResource(R.drawable.ic_account_blue);
        }
        edtTen.setText(Common.currentUser.getName());
        edtDiaChi.setText(Common.currentUser.getAddress());
        edtEmail.setText(Common.currentUser.getEmail());
        edtPhone.setText(Common.currentUser.getPhone());
    }

    public void textChange() {
        edtTen.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    if (!s.toString().equals(Common.currentUser.getName())) {
                        Common.checkFormChange = true;
                        checkFormChange = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtDiaChi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    if (!s.toString().equals(Common.currentUser.getAddress())) {
                        Common.checkFormChange = true;
                        checkFormChange = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    if (!s.toString().equals(Common.currentUser.getPhone())) {
                        Common.checkFormChange = true;
                        checkFormChange = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    if (!s.toString().equals(Common.currentUser.getEmail())) {
                        Common.checkFormChange = true;
                        checkFormChange = true;
                    }
                } else {
                    if (awesomeValidation != null) {
                        awesomeValidation.clear();
                        validator();
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void validator() {
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(getActivity(), R.id.edtTen, new SimpleCustomValidation() {
            @Override
            public boolean compare(String s) {
                if (s.length() > 0) return true;
                return false;
            }
        }, R.string.form_null_value);
        awesomeValidation.addValidation(getActivity(), R.id.edtDiaChi, new SimpleCustomValidation() {
            @Override
            public boolean compare(String s) {
                if (s.length() > 0) return true;
                return false;
            }
        }, R.string.form_null_value);
        awesomeValidation.addValidation(getActivity(), R.id.edtPhone, new SimpleCustomValidation() {
            @Override
            public boolean compare(String s) {
                if (s.length() > 0) return true;
                return false;
            }
        }, R.string.form_null_value);
    }

    public String getRealPathFromURI(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, proj, null, null, null);
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

    }

    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 112);
    }

    public String convertString(int[] arr) {
        String value = "";
        for (int i = 0; i < arr.length; i++) {
            if (i == arr.length - 1) {
                value += arr[i];
            } else {
                value += arr[i] + "-";
            }
        }
        return value;
    }

    public void update(RequestBody ten, RequestBody address, RequestBody email, RequestBody phone, MultipartBody.Part file, RequestBody type) {
        compositeDisposable.add(api.updateUser(ten, address, email, phone, file,type).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
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
                                Gson gson = new Gson();
                                User user = gson.fromJson(message.getData(), User.class);
                                Common.currentUser = user;
                                saveUserLogin(user);
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

    public void saveUserLogin(User user) throws NoSuchPaddingException, UnsupportedEncodingException, InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidParameterSpecException {
        sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("id", String.valueOf(user.getId()));
        editor.putString("name", user.getName());
        editor.putString("avatar", (user.getAvatar() == null) ? "" : user.getAvatar());
        editor.putString("phone", user.getPhone());
        editor.putString("username", user.getUsername());
        editor.putString("address", user.getAddress());
        editor.putString("roles", user.getRoles());
        editor.putString("created_at", user.getCreated_at());
        editor.putString("updated_at", user.getUpdated_at());
        editor.putString("access_token", user.getAccess_token());
        editor.putString("token_type", user.getToken_type());
        editor.putString("expires_at", user.getExpires_at());
        editor.commit();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof HomeActivity) {
            homeActivity = (HomeActivity) context;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 112 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            uriAvatar = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uriAvatar);
                ivAvatar.setImageBitmap(bitmap);
                btnXoaAnh.setVisibility(View.VISIBLE);
                Common.checkFormChange = true;
                checkFormChange = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivAvatar:
            case R.id.btnChonAnh: {
                selectImage();
                break;
            }
            case R.id.btnXoaAnh: {
                ivAvatar.setImageResource(R.drawable.ic_account_blue);
                btnXoaAnh.setVisibility(View.GONE);
                uriAvatar = null;
                break;
            }
            case R.id.btnChangePass : {
                homeActivity.replaceFragment(new ChangePasswordFragment(), true);
                break;
            }
            case R.id.ivAction: {

                if (edtEmail.getText().length() > 0) {
                    awesomeValidation.addValidation(getActivity(), R.id.edtEmail, Patterns.EMAIL_ADDRESS, R.string.email_validator);
                }
                if (awesomeValidation.validate()) {
                    MultipartBody.Part file = null;
                    RequestBody type = RequestBody.create(MediaType.parse("multipart/form-data"), "1");
                    if (uriAvatar != null) {
                        File avatar = new File(getRealPathFromURI(uriAvatar));
                        RequestBody requestBody = RequestBody.create(MediaType.parse(getActivity().getContentResolver().getType(uriAvatar)), avatar);
                        file = MultipartBody.Part.createFormData("avatar", avatar.getName(), requestBody);
                    }
                    RequestBody ten = RequestBody.create(MediaType.parse("multipart/form-data"), edtTen.getText().toString());
                    RequestBody diaChi = RequestBody.create(MediaType.parse("multipart/form-data"), edtDiaChi.getText().toString());
                    RequestBody email = (edtEmail.getText().length() > 0) ? RequestBody.create(MediaType.parse("multipart/form-data"), edtEmail.getText().toString()) : null;
                    RequestBody phone = RequestBody.create(MediaType.parse("multipart/form-data"), edtPhone.getText().toString());
                    update(ten, diaChi, email, phone, file, type);
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