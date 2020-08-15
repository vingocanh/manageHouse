package com.example.managehouse.Fragment.KhoanThu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.managehouse.Callback.ChosenItemCallback;
import com.example.managehouse.Common.Common;
import com.example.managehouse.Model.Item;
import com.example.managehouse.Model.Khoanthu;
import com.example.managehouse.Model.Khutro;
import com.example.managehouse.Model.Message;
import com.example.managehouse.R;
import com.example.managehouse.Retrofit.API;
import com.example.managehouse.Service.DialogChosenItem;
import com.example.managehouse.Service.DialogNotification;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class FormFragment extends Fragment implements View.OnClickListener, ChosenItemCallback {

    private ImageView ivAvatar;
    private EditText editTen, edtMoTa;
    private TextView txtTrangThai;
    private Button btnChonAnh, btnXoaAnh;

    private HomeActivity homeActivity;
    private AwesomeValidation awesomeValidation;
    private Uri uriAvatar = null;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private API api;
    private Khoanthu khoanthu;
    private String ten = "", moTa = "";
    private boolean checkFormChange = false;

    public FormFragment() {
        // Required empty public constructor
    }

    public static FormFragment newInstance(String param1, String param2) {
        FormFragment fragment = new FormFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.checkFormChange = false;
        if(getArguments() != null) {
            khoanthu = (Khoanthu) getArguments().getSerializable("khoanthu");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_form_khoanthu, container, false);
        api = Common.getAPI();
        mapping(view);
        textChange();
        if(khoanthu == null) setValueDefault();
        else setValue();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        validator();
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
    }

    public void mapping(View view) {
        homeActivity.ivAction.setOnClickListener(this);
        ivAvatar = view.findViewById(R.id.ivAvatar);
        ivAvatar.setOnClickListener(this);
        editTen = view.findViewById(R.id.edtTen);
        edtMoTa = view.findViewById(R.id.edtMoTa);
        txtTrangThai = view.findViewById(R.id.txtChonTrangThai);
        txtTrangThai.setOnClickListener(this);
        homeActivity.ivAction.setImageResource(R.drawable.ic_save_32dp);
        btnChonAnh = view.findViewById(R.id.btnChonAnh);
        btnChonAnh.setOnClickListener(this);
        btnXoaAnh = view.findViewById(R.id.btnXoaAnh);
        btnXoaAnh.setOnClickListener(this);
        btnXoaAnh.setVisibility(View.GONE);
    }

    public void textChange() {
        editTen.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")) {
                    if(!s.toString().equals(ten)) {
                        Common.checkFormChange = true;
                        checkFormChange = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtMoTa.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")) {
                    if(!s.toString().equals(moTa)) {
                        Common.checkFormChange = true;
                        checkFormChange = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void setValueDefault() {
        txtTrangThai.setText("Sử dụng");
        txtTrangThai.setTag("1");
    }

    private void setValue() {
        if(khoanthu.getAvatar() != null) {
            Picasso.get().load(khoanthu.getAvatar()).placeholder(R.drawable.ic_energy).error(R.drawable.ic_energy).into(ivAvatar);
            btnXoaAnh.setVisibility(View.VISIBLE);
        }
        else {
            ivAvatar.setImageResource(R.drawable.ic_energy);
        }
        ten = khoanthu.getTen();
        editTen.setText(ten);
        moTa = khoanthu.getMota();
        edtMoTa.setText(moTa);
        String trangThai = "Sử dụng";
        if(khoanthu.getStatus() == 0) trangThai = "Không sử dụng";
        txtTrangThai.setText(trangThai);
        txtTrangThai.setTag(khoanthu.getStatus());
    }

    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 112);
    }

    public String getRealPathFromURI(Uri uri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(uri,proj,null,null,null);
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

    }

    public void addData(RequestBody id, RequestBody ten, RequestBody mota, RequestBody trangThai, RequestBody userId, RequestBody type, MultipartBody.Part file) {
        compositeDisposable.add(api.createKhoanThu(id,ten,mota,userId,trangThai, type, file).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
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
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
        Common.checkFormChange = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        Common.checkFormChange = checkFormChange;
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
                Common.checkFormChange =true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivAvatar:
            case R.id.btnChonAnh : {
                selectImage();
                break;
            }
            case R.id.btnXoaAnh : {
                ivAvatar.setImageResource(R.drawable.ic_energy);
                btnXoaAnh.setVisibility(View.GONE);
                uriAvatar = null;
                break;
            }
            case R.id.txtChonTrangThai : {
                List<Item> items = new ArrayList<>();
                int trangThai = Integer.parseInt(txtTrangThai.getTag().toString());
                if(trangThai == 1) {
                    items.add(new Item(true, 1, 0, "Sử dụng"));
                    items.add(new Item(false, 0, 1, "Không sử dụng"));
                }
                else {
                    items.add(new Item(false, 1, 0, "Sử dụng"));
                    items.add(new Item(true, 0, 1, "Không sử dụng"));
                }
                DialogChosenItem dialogChosenItem = new DialogChosenItem(getActivity(),items,"Chọn tình trạng","single",0, false);
                dialogChosenItem.setChosenItemCallback(this);
                dialogChosenItem.showDialog();
                break;
            }
            case R.id.ivAction : {
                if(awesomeValidation.validate()) {
                    MultipartBody.Part file = null;
                    if(uriAvatar != null) {
                        File avatar = new File(getRealPathFromURI(uriAvatar));
                        RequestBody requestBody = RequestBody.create(MediaType.parse(getActivity().getContentResolver().getType(uriAvatar)), avatar);
                        file = MultipartBody.Part.createFormData("avatar", avatar.getName(), requestBody);
                    }
                    RequestBody ten = RequestBody.create(MediaType.parse("multipart/form-data"), editTen.getText().toString());
                    RequestBody mota = RequestBody.create(MediaType.parse("multipart/form-data"), edtMoTa.getText().toString());
                    RequestBody trangThai = RequestBody.create(MediaType.parse("multipart/form-data"), txtTrangThai.getTag().toString());
                    RequestBody id = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(Common.currentUser.getId()));
                    if (khoanthu == null) {
                        RequestBody khoanThuId = RequestBody.create(MediaType.parse("multipart/form-data"), "-1");
                        RequestBody type = RequestBody.create(MediaType.parse("multipart/form-data"), "1");
                        addData(khoanThuId,ten,mota,trangThai,id,type,file);
                    }
                    else {
                        RequestBody khutroId = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(khoanthu.getId()));
                        RequestBody type = RequestBody.create(MediaType.parse("multipart/form-data"), "0");
                        addData(khutroId,ten,mota,trangThai,id,type,file);
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onReceiveItem(List<Item> item) {
        txtTrangThai.setText(item.get(0).getName());
        txtTrangThai.setTag(item.get(0).getId());
        if(Integer.parseInt(txtTrangThai.getTag().toString()) != item.get(0).getId()) {
            Common.checkFormChange = true;
            checkFormChange = true;
        }
    }
}