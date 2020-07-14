package com.example.managehouse.Fragment.PhongTro;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import com.example.managehouse.Model.Khutrokhoanthu;
import com.example.managehouse.Model.Message;
import com.example.managehouse.Model.Phongtro;
import com.example.managehouse.R;
import com.example.managehouse.Retrofit.API;
import com.example.managehouse.Service.DialogChosenItem;
import com.example.managehouse.Service.DialogLoading;
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
    private EditText editTen, edtGia, edtGhiChu;
    private TextView txtTrangThai, txtChonKhuTro;
    private Button btnChonAnh, btnXoaAnh;

    private HomeActivity homeActivity;
    private AwesomeValidation awesomeValidation;
    private Uri uriAvatar = null;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private API api;
    private Phongtro phongtro;
    private String tenPhongTro = "", ghiChu = "";
    private int gia = 0;
    private int typeChosenItem = 0, sttKhuTro = 0;
    private List<Khutro> khutroList = new ArrayList<>();

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
        if(getArguments() != null) {
            phongtro = (Phongtro) getArguments().getSerializable("phongtro");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_form_phong_tro, container, false);
        Common.checkFormChange = false;
        api = Common.getAPI();
        mapping(view);
        getKhutro();
        textChange();
        if(phongtro == null) setValueDefault();
        else setValue();
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        validator();
    }

    public void mapping(View view) {
        homeActivity.ivAction.setOnClickListener(this);
        ivAvatar = view.findViewById(R.id.ivAvatar);
        ivAvatar.setOnClickListener(this);
        editTen = view.findViewById(R.id.edtTen);
        edtGia = view.findViewById(R.id.edtGia);
        edtGhiChu = view.findViewById(R.id.edtGhiChu);
        txtTrangThai = view.findViewById(R.id.txtChonTrangThai);
        txtTrangThai.setOnClickListener(this);
        txtChonKhuTro = view.findViewById(R.id.txtChonKhuTro);
        txtChonKhuTro.setOnClickListener(this);
        homeActivity.ivAction.setImageResource(R.drawable.ic_save_32dp);
        btnChonAnh = view.findViewById(R.id.btnChonAnh);
        btnChonAnh.setOnClickListener(this);
        btnXoaAnh = view.findViewById(R.id.btnXoaAnh);
        btnXoaAnh.setOnClickListener(this);
        btnXoaAnh.setVisibility(View.GONE);
        edtGia.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!edtGia.getText().toString().equals("")) {
                    if (hasFocus) {
                        String value = edtGia.getText().toString();
                        edtGia.setText(String.valueOf(Common.clearMoney(value)));
                    } else {
                        edtGia.setText(Common.formatMoney(Integer.parseInt(edtGia.getText().toString())));
                        edtGia.setTag(Common.clearMoney(edtGia.getText().toString()));
                    }
                }

            }
        });
    }

    public void textChange() {
        editTen.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")) {
                    if(!s.toString().equals(tenPhongTro)) {
                        Common.checkFormChange = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtGia.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")) {
                    if(!s.toString().equals(Common.formatMoney(gia))) Common.checkFormChange = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtGhiChu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")) {
                    if(!s.toString().equals(ghiChu)) Common.checkFormChange = true;
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
        awesomeValidation.addValidation(getActivity(), R.id.edtGia, new SimpleCustomValidation() {
            @Override
            public boolean compare(String s) {
                if (s.length() > 0) return true;
                return false;
            }
        }, R.string.form_null_value);
    }

    private void setValue() {
        if(phongtro.getImg() != null) {
            Picasso.get().load(phongtro.getImg()).placeholder(R.drawable.ic_home_32dp).error(R.drawable.ic_home_32dp).into(ivAvatar);
            btnXoaAnh.setVisibility(View.VISIBLE);
        }
        else {
            ivAvatar.setImageResource(R.drawable.ic_home_32dp);
        }
        tenPhongTro = phongtro.getTen();
        editTen.setText(tenPhongTro);
        ghiChu = phongtro.getGhichu();
        edtGhiChu.setText(ghiChu);
        gia = phongtro.getGia();
        edtGia.setTag(gia);
        edtGia.setText(Common.formatMoney(phongtro.getGia()));
        String trangThai = "Sử dụng";
        if(phongtro.getStatus() == 0) trangThai = "Không sử dụng";
        txtTrangThai.setText(trangThai);
        txtTrangThai.setTag(phongtro.getStatus());
        txtChonKhuTro.setTag(phongtro.getKhutro_id());
        txtChonKhuTro.setText(phongtro.getKhutro().getTen());
    }

    public void setValueDefault() {
        txtTrangThai.setText("Sử dụng");
        txtTrangThai.setTag("1");
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

    public void addData(RequestBody phongTroId, RequestBody ten, RequestBody khuTroId, RequestBody userId, RequestBody gia, RequestBody trangThai, RequestBody type, MultipartBody.Part file) {
        compositeDisposable.add(api.createPhongTro(phongTroId,ten,khuTroId,userId,gia,trangThai,type,file).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
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

    public void getKhutro() {
        final DialogLoading dialogLoading = new DialogLoading(getActivity(),"Đang lấy dữ liệu khu trọ, đợi chút...");
        dialogLoading.showDialog();
        String url = "khutro/1";
        compositeDisposable.add(api.getKhutroChosen(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Khutro>>() {
                    @Override
                    public void accept(List<Khutro> khutros) throws Exception {
                        khutroList = khutros;
                        if (khutroList.size() > 0 && phongtro == null) {
                            txtChonKhuTro.setTag(khutroList.get(0).getId());
                            txtChonKhuTro.setText(khutroList.get(0).getTen());
                        }
                        dialogLoading.hideDialog();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        dialogLoading.hideDialog();
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
                ivAvatar.setImageResource(R.drawable.ic_home_32dp);
                btnXoaAnh.setVisibility(View.GONE);
                uriAvatar = null;
                break;
            }
            case R.id.txtChonTrangThai : {
                typeChosenItem = 1;
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
            case R.id.txtChonKhuTro: {
                if(khutroList.size() > 0) {
                    typeChosenItem = 0;
                    int id = Integer.parseInt(txtChonKhuTro.getTag().toString());
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
                                    com.example.managehouse.Fragment.KhuTro.FormFragment formFragment = new com.example.managehouse.Fragment.KhuTro.FormFragment();
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
            case R.id.ivAction : {
                if(awesomeValidation.validate()) {
                    MultipartBody.Part file = null;
                    if(uriAvatar != null) {
                        File avatar = new File(getRealPathFromURI(uriAvatar));
                        RequestBody requestBody = RequestBody.create(MediaType.parse(getActivity().getContentResolver().getType(uriAvatar)), avatar);
                        file = MultipartBody.Part.createFormData("avatar", avatar.getName(), requestBody);
                    }
                    RequestBody ten = RequestBody.create(MediaType.parse("multipart/form-data"), editTen.getText().toString());
                    RequestBody gia = RequestBody.create(MediaType.parse("multipart/form-data"), edtGia.getTag().toString());
                    RequestBody khuTroId = RequestBody.create(MediaType.parse("multipart/form-data"), txtChonKhuTro.getTag().toString());
                    RequestBody trangThai = RequestBody.create(MediaType.parse("multipart/form-data"), txtTrangThai.getTag().toString());
                    RequestBody user_id = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(Common.currentUser.getId()));
                    if (phongtro == null) {
                        RequestBody phongTroId = RequestBody.create(MediaType.parse("multipart/form-data"), "-1");
                        RequestBody type = RequestBody.create(MediaType.parse("multipart/form-data"), "1");
                        addData(phongTroId,ten,khuTroId,user_id,gia,trangThai,type,file);
                    }
                    else {
                        RequestBody phongTroId = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(phongtro.getId()));
                        RequestBody type = RequestBody.create(MediaType.parse("multipart/form-data"), "0");
                        addData(phongTroId,ten,khuTroId,user_id,gia,trangThai,type,file);
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
    public void onReceiveItem(List<Item> item) {
        if(typeChosenItem == 0) {
            txtChonKhuTro.setTag(item.get(0).getId());
            txtChonKhuTro.setText(item.get(0).getName());
            sttKhuTro = item.get(0).getStt();
            if(Integer.parseInt(txtChonKhuTro.getTag().toString()) != item.get(0).getId()) Common.checkFormChange = true;
        }
        else {
            txtTrangThai.setText(item.get(0).getName());
            txtTrangThai.setTag(item.get(0).getId());
            if(Integer.parseInt(txtTrangThai.getTag().toString()) != item.get(0).getId()) Common.checkFormChange = true;
        }
    }
}