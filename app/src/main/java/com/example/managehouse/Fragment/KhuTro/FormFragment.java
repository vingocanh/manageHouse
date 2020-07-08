package com.example.managehouse.Fragment.KhuTro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.recyclerview.widget.RecyclerView;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.custom.SimpleCustomValidation;
import com.example.managehouse.Activity.HomeActivity;
import com.example.managehouse.Adapter.ItemKhoanThuKhuTro;
import com.example.managehouse.Callback.ChosenItemCallback;
import com.example.managehouse.Callback.ChosenKhoanThuKhuTroCallback;
import com.example.managehouse.Common.Common;
import com.example.managehouse.Model.Donvitinh;
import com.example.managehouse.Model.Item;
import com.example.managehouse.Model.Khoanthu;
import com.example.managehouse.Model.Khutro;
import com.example.managehouse.Model.Khutrokhoanthu;
import com.example.managehouse.Model.Message;
import com.example.managehouse.R;
import com.example.managehouse.Retrofit.API;
import com.example.managehouse.Service.DialogChosenItem;
import com.example.managehouse.Service.DialogNotification;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialog;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class FormFragment extends Fragment implements View.OnClickListener, ChosenItemCallback, ChosenKhoanThuKhuTroCallback {

    private ImageView ivAvatar;
    private EditText editTen, edtDiaChi, edtNamXayDung;
    private TextView txtTrangThai;
    private Button btnChonAnh, btnXoaAnh;
    private RecyclerView rvKhoanThu;

    private HomeActivity homeActivity;
    private AwesomeValidation awesomeValidation;
    private Uri uriAvatar = null;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private API api;
    private Khutro khutro;
    private List<Khoanthu> khoanthuList = new ArrayList<>();
    private ItemKhoanThuKhuTro itemKhoanThuKhuTro;

    public FormFragment() {
        // Required empty public constructor
    }

    public static FormFragment newInstance() {
        FormFragment fragment = new FormFragment();
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
        View view = inflater.inflate(R.layout.fragment_form_khu_tro, container, false);
        api = Common.getAPI();
        mapping(view);
        getKhoanThu();
        getDonViTinh();
        if(khutro == null) setValueDefault();
        else setValue();
        return view;
    }

    private void setValue() {
        if(khutro.getImg() != null) {
            Picasso.get().load(khutro.getImg()).placeholder(R.drawable.ic_hotel).error(R.drawable.ic_hotel).into(ivAvatar);
            btnXoaAnh.setVisibility(View.VISIBLE);
        }
        else {
            ivAvatar.setImageResource(R.drawable.ic_hotel);
        }
        editTen.setText(khutro.getTen());
        edtDiaChi.setText(khutro.getDiachi());
        edtNamXayDung.setText(khutro.getNam_xd());
        String trangThai = "Sử dụng";
        if(khutro.getStatus() == 0) trangThai = "Không sử dụng";
        txtTrangThai.setText(trangThai);
        txtTrangThai.setTag(khutro.getStatus());
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
        edtDiaChi = view.findViewById(R.id.edtDiaChi);
        edtNamXayDung = view.findViewById(R.id.edtNamXayDung);
        edtNamXayDung.setCursorVisible(false);
        edtNamXayDung.setFocusableInTouchMode(false);
        edtNamXayDung.setFocusable(false);
        edtNamXayDung.setOnClickListener(this);
        txtTrangThai = view.findViewById(R.id.txtChonTrangThai);
        txtTrangThai.setOnClickListener(this);
        homeActivity.ivAction.setImageResource(R.drawable.ic_save_32dp);
        btnChonAnh = view.findViewById(R.id.btnChonAnh);
        btnChonAnh.setOnClickListener(this);
        btnXoaAnh = view.findViewById(R.id.btnXoaAnh);
        btnXoaAnh.setOnClickListener(this);
        btnXoaAnh.setVisibility(View.GONE);
        rvKhoanThu = view.findViewById(R.id.rvKhoanThu);
        rvKhoanThu.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvKhoanThu.setLayoutManager(linearLayoutManager);

    }

    public void setValueDefault() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        edtNamXayDung.setText((calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR));
        txtTrangThai.setText("Sử dụng");
        txtTrangThai.setTag("1");
    }

    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 112);
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
        awesomeValidation.addValidation(getActivity(), R.id.edtNamXayDung, new SimpleCustomValidation() {
            @Override
            public boolean compare(String s) {
                if (s.length() > 0) return true;
                return false;
            }
        }, R.string.form_null_value);
    }

    public void addKhuTro(RequestBody id, RequestBody ten, RequestBody diaChi, RequestBody namXayDung, RequestBody trangThai, RequestBody userId, RequestBody type, MultipartBody.Part file, RequestBody idKhoanThu, RequestBody giaKhoanThu, RequestBody dvtKhoanThu) {
        compositeDisposable.add(api.createKhuTro(id,ten,diaChi,namXayDung,trangThai, userId, type, file, idKhoanThu, giaKhoanThu, dvtKhoanThu ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
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

    public String getRealPathFromURI(Uri uri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(uri,proj,null,null,null);
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

    }

    public void chosenMonthYear() {
        String namCurrent = edtNamXayDung.getText().toString();
        int monthCurrent = Integer.parseInt(namCurrent.substring(0, namCurrent.indexOf("/"))) - 1;
        int yearCurrent = Integer.parseInt(namCurrent.substring(namCurrent.indexOf("/") + 1));
        MonthYearPickerDialogFragment monthYearPickerDialogFragment = MonthYearPickerDialogFragment.getInstance(monthCurrent, yearCurrent);
        monthYearPickerDialogFragment.setOnDateSetListener(new MonthYearPickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(int year, int monthOfYear) {
                String value = (monthOfYear + 1) + "/" + year;
                edtNamXayDung.setText(value);
            }
        });
        monthYearPickerDialogFragment.show(getFragmentManager(), null);
    }

    public void getKhoanThu() {
        khoanthuList.add(null);
        itemKhoanThuKhuTro = new ItemKhoanThuKhuTro(getActivity(),khoanthuList);
        itemKhoanThuKhuTro.setChosenKhoanThuKhuTroCallback(this);
        rvKhoanThu.setAdapter(itemKhoanThuKhuTro);
        compositeDisposable.add(api.getKhoanThuKhuTro().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Khoanthu>>() {
                    @Override
                    public void accept(List<Khoanthu> khoanthus) throws Exception {
                        khoanthuList.clear();
                        khoanthuList.addAll(khoanthus);
                        if(khutro != null) {
//                            List<Integer> idKhoanThu = new ArrayList<>();
//                            List<Integer> giaKhoanThuList = new ArrayList<>();
//                            List<Integer> dvtKhoanThuList = new ArrayList<>();
//                            for(Khutrokhoanthu khutrokhoanthu : khutro.getKhutrokhoanthu()) {
//                                idKhoanThu.add(khutrokhoanthu.getKhoanthu_id());
//                                giaKhoanThuList.add(khutrokhoanthu.getKhoanthu_id(), khutrokhoanthu.getGia());
//                                dvtKhoanThuList.add(khutrokhoanthu.getKhoanthu_id(), khutrokhoanthu.getDonvitinh_id());
//                            }
                            int i = 0;
                            int[] giaKhoanThu = new int[khoanthus.size()];
                            int[] idKhoanThu = new int[khoanthus.size()];
                            int[] dvtKhoanThu = new int[khoanthus.size()];
                            String[] tenDonVi = new String[khoanthus.size()];
                            for(Khoanthu khoanthu : khoanthuList) {
                                for (Khutrokhoanthu khutrokhoanthu : khutro.getKhutrokhoanthu()) {
                                    if(khutrokhoanthu.getKhoanthu_id() == khoanthu.getId()) {
                                        khoanthu.setChecked(true);
                                        giaKhoanThu[i] = khutrokhoanthu.getGia();
                                        dvtKhoanThu[i] = khutrokhoanthu.getDonvitinh_id();
                                        idKhoanThu[i] = khoanthu.getId();
                                        tenDonVi[i] = khutrokhoanthu.getDonvitinh().getName();
                                    }
                                }
                                i++;
                            }
                            itemKhoanThuKhuTro.setGiaKhoanThu(giaKhoanThu);
                            itemKhoanThuKhuTro.setIdKhoanThu(idKhoanThu);
                            itemKhoanThuKhuTro.setDonViTinhKhoanThu(dvtKhoanThu);
                            itemKhoanThuKhuTro.setTenDonViTinh(tenDonVi);
                        }
                        else {
                            itemKhoanThuKhuTro.setGiaKhoanThu(new int[khoanthus.size()]);
                            itemKhoanThuKhuTro.setDonViTinhKhoanThu(new int[khoanthus.size()]);
                            itemKhoanThuKhuTro.setIdKhoanThu(new int[khoanthus.size()]);
                        }
                        itemKhoanThuKhuTro.notifyDataSetChanged();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toasty.error(getContext(), "Gặp sự cố, thử lại sau.", 300, true).show();
                        throwable.printStackTrace();
                    }
                }));
    }

    public void getDonViTinh() {
        String url = "donvitinh/1";
        compositeDisposable.add(api.getDonViTinhChon(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Donvitinh>>() {
                    @Override
                    public void accept(List<Donvitinh> donvitinhs) throws Exception {
                        itemKhoanThuKhuTro.setDonvitinhs(donvitinhs);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                }));
    }

    public boolean checkKhoanThu() {
        int i = 0;
        for (Khoanthu khoanthu : khoanthuList) {
            if(khoanthu.isChecked()) {
                if(itemKhoanThuKhuTro.getGiaKhoanThu()[i] == 0 || itemKhoanThuKhuTro.getDonViTinhKhoanThu()[i] == 0) return false;
            }
            i++;
        }
        return true;
    }

    public String convertString(int[] arr) {
        String value = "";
        for (int i = 0; i < arr.length; i++) {
            if(i == arr.length - 1) {
                value += arr[i];
            }
            else {
                value += arr[i] + "-";
            }
        }
        return value;
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
                ivAvatar.setImageResource(R.drawable.ic_hotel);
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
                DialogChosenItem dialogChosenItem = new DialogChosenItem(getActivity(),items,"Chọn tình trạng","single",0);
                dialogChosenItem.setChosenItemCallback(this);
                dialogChosenItem.showDialog();
                break;
            }
            case R.id.edtNamXayDung : {
                chosenMonthYear();
                break;
            }
            case R.id.ivAction : {
                if(awesomeValidation.validate()) {
                    if(checkKhoanThu()) {
                        MultipartBody.Part file = null;
                        if(uriAvatar != null) {
                            File avatar = new File(getRealPathFromURI(uriAvatar));
                            RequestBody requestBody = RequestBody.create(MediaType.parse(getActivity().getContentResolver().getType(uriAvatar)), avatar);
                            file = MultipartBody.Part.createFormData("avatar", avatar.getName(), requestBody);
                        }
                        RequestBody ten = RequestBody.create(MediaType.parse("multipart/form-data"), editTen.getText().toString());
                        RequestBody diaChi = RequestBody.create(MediaType.parse("multipart/form-data"), edtDiaChi.getText().toString());
                        RequestBody namXayDung = RequestBody.create(MediaType.parse("multipart/form-data"), edtNamXayDung.getText().toString());
                        RequestBody trangThai = RequestBody.create(MediaType.parse("multipart/form-data"), txtTrangThai.getTag().toString());
                        RequestBody id = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(Common.currentUser.getId()));
                        RequestBody idKhoanThu = RequestBody.create(MediaType.parse("multipart/form-data"), convertString(itemKhoanThuKhuTro.getIdKhoanThu()));
                        RequestBody giaKhoanThu = RequestBody.create(MediaType.parse("multipart/form-data"), convertString(itemKhoanThuKhuTro.getGiaKhoanThu()));
                        RequestBody donViTinhKhoanThu = RequestBody.create(MediaType.parse("multipart/form-data"), convertString(itemKhoanThuKhuTro.getDonViTinhKhoanThu()));
                        if (khutro == null) {
                            RequestBody khutroId = RequestBody.create(MediaType.parse("multipart/form-data"), "-1");
                            RequestBody type = RequestBody.create(MediaType.parse("multipart/form-data"), "1");
                            addKhuTro(khutroId,ten,diaChi,namXayDung,trangThai,id,type,file, idKhoanThu,giaKhoanThu,donViTinhKhoanThu);
                        }
                        else {
                            RequestBody khutroId = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(khutro.getId()));
                            RequestBody type = RequestBody.create(MediaType.parse("multipart/form-data"), "0");
                            addKhuTro(khutroId,ten,diaChi,namXayDung,trangThai,id,type,file, idKhoanThu,giaKhoanThu,donViTinhKhoanThu);
                        }
                    }
                    else {
                        DialogNotification dialogNotification = new DialogNotification(getActivity(),new String[]{"Bạn chưa nhập đủ giá và đơn vị tính của các khoản thu đã chọn."},"error");
                        dialogNotification.showDialog();
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    @Override
    public void onReceiveKhoanThu() {
        itemKhoanThuKhuTro.notifyDataSetChanged();
    }
}