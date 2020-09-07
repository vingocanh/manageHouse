package com.example.managehouse.Fragment.NguoiTro;

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

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.custom.SimpleCustomValidation;
import com.example.managehouse.Activity.HomeActivity;
import com.example.managehouse.Callback.ChosenItemCallback;
import com.example.managehouse.Common.Common;
import com.example.managehouse.Model.Item;
import com.example.managehouse.Model.Khutro;
import com.example.managehouse.Model.Message;
import com.example.managehouse.Model.Nguoitro;
import com.example.managehouse.Model.Phongtro;
import com.example.managehouse.Model.Trinhdo;
import com.example.managehouse.R;
import com.example.managehouse.Retrofit.API;
import com.example.managehouse.Service.DialogChosenItem;
import com.example.managehouse.Service.DialogLoading;
import com.example.managehouse.Service.DialogNotification;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

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

public class FormFragment extends Fragment implements View.OnClickListener, ChosenItemCallback, DatePickerDialog.OnDateSetListener {

    private ImageView ivAvatar;
    private EditText edtHo, edtTen, edtNgaySinh, edtSocmnd, edtQueQuan, edtNgheNghiep, edtNoiLamViec, edtGhiChu;
    private EditText edtTenCha, edtNamSinhCha, edtNgheNghiepCha, edtNoiCongTacCha, edtChoOCha, edtTenMe, edtNamSinhMe, edtNgheNghiepMe, edtNoiCongTacMe, edtChoOMe;
    private TextView txtChonPhongTro, txtChonTrinhDo, txtTrangThai, txtChonKhuTro;
    private Button btnChonAnh, btnXoaAnh;

    private HomeActivity homeActivity;
    private AwesomeValidation awesomeValidation;
    private Uri uriAvatar = null;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private API api;
    private Nguoitro nguoitro;
    private String ho = "", ten = "", ngaySinh = "", soCmnd = "", queQuan = "", ngheNhiep = "", noiLamViec = "", ghiChu = "",
    tenCha = "", namSinhCha = "", ngheNghiepCha = "", noiCongTacCha = "", choOCha = "", tenMe = "", namSinhMe = "", ngheNghiepMe = "", noiCongTacMe = "", choOMe = "";
    private int typeChosenItem = 0, sttPhongTro = 0, sttKhuTro = 0, sttTrinhDo = 0;
    private List<Phongtro> phongtroList = new ArrayList<>();
    private List<Khutro> khutroList = new ArrayList<>();
    private List<Trinhdo> trinhdoList = new ArrayList<>();
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
            nguoitro = (Nguoitro) getArguments().getSerializable("nguoitro");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_form_nguoi_tro, container, false);
        api = Common.getAPI();
        mapping(view);
        getKhuTro();
        textChange();
        if(nguoitro == null) setValueDefault();
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
        edtGhiChu = view.findViewById(R.id.edtGhiChu);
        edtHo = view.findViewById(R.id.edtHo);
        edtTen = view.findViewById(R.id.edtTen);
        edtNgaySinh = view.findViewById(R.id.edtNgaySinh);
        edtNgaySinh.setCursorVisible(false);
        edtNgaySinh.setFocusableInTouchMode(false);
        edtNgaySinh.setFocusable(false);
        edtNgaySinh.setOnClickListener(this);
        edtSocmnd = view.findViewById(R.id.edtSocmnd);
        edtQueQuan = view.findViewById(R.id.edtQueQuan);
        edtNgheNghiep = view.findViewById(R.id.edtNgheNghiep);
        edtNoiLamViec = view.findViewById(R.id.edtNoiLamViec);
        txtTrangThai = view.findViewById(R.id.txtChonTrangThai);
        txtTrangThai.setOnClickListener(this);
        txtChonPhongTro = view.findViewById(R.id.txtChonPhongTro);
        txtChonPhongTro.setOnClickListener(this);
        txtChonTrinhDo = view.findViewById(R.id.txtChonTrinhDo);
        txtChonTrinhDo.setOnClickListener(this);
        txtChonKhuTro = view.findViewById(R.id.txtChonKhuTro);
        txtChonKhuTro.setOnClickListener(this);
        homeActivity.ivAction.setImageResource(R.drawable.ic_save_32dp);
        btnChonAnh = view.findViewById(R.id.btnChonAnh);
        btnChonAnh.setOnClickListener(this);
        btnXoaAnh = view.findViewById(R.id.btnXoaAnh);
        btnXoaAnh.setOnClickListener(this);
        btnXoaAnh.setVisibility(View.GONE);

        edtTenCha = view.findViewById(R.id.edtTenCha);
        edtNamSinhCha = view.findViewById(R.id.edtNamSinhCha);
        edtNgheNghiepCha = view.findViewById(R.id.edtNgheNghiepCha);
        edtNoiCongTacCha = view.findViewById(R.id.edtNoiCongTacCha);
        edtChoOCha = view.findViewById(R.id.edtChoOCha);
        edtTenMe = view.findViewById(R.id.edtTenMe);
        edtNamSinhMe = view.findViewById(R.id.edtNamSinhMe);
        edtNgheNghiepMe = view.findViewById(R.id.edtNgheNghiepMe);
        edtNoiCongTacMe = view.findViewById(R.id.edtNoiCongTacMe);
        edtChoOMe = view.findViewById(R.id.edtChoOMe);
    }

    public void initDatePicker() {
        Calendar calendar = Calendar.getInstance();
        if(nguoitro != null) {
            Date date = new Date(nguoitro.getNgaysinh2());
            calendar.setTime(date);
        }
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.setOkText("Đồng ý");
        datePickerDialog.show(getActivity().getSupportFragmentManager(), "Datepickerdialog");
    }

    public void textChange() {
        edtGhiChu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")) {
                    if(!s.toString().equals(ghiChu)) {
                        checkFormChange = true;
                        Common.checkFormChange = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtHo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")) {
                    if(!s.toString().equals(ho)) {
                        checkFormChange = true;
                        Common.checkFormChange = true;

                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtTen.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")) {
                    if(!s.toString().equals(ten)) {
                        checkFormChange = true;
                        Common.checkFormChange = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtNgaySinh.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")) {
                    if(!s.toString().equals(ngaySinh)) {
                        checkFormChange = true;
                        Common.checkFormChange = true;
                    }
                }
                else {
                    checkFormChange = false;
                    Common.checkFormChange = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtSocmnd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")) {
                    if(!s.toString().equals(soCmnd)) {
                        checkFormChange = true;
                        Common.checkFormChange = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtQueQuan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")) {
                    if(!s.toString().equals(queQuan)) {
                        checkFormChange = true;
                        Common.checkFormChange = true;
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtNgheNghiep.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")) {
                    if(!s.toString().equals(ngheNhiep)) {
                        checkFormChange = true;
                        Common.checkFormChange = true;
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtNoiLamViec.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")) {
                    if(!s.toString().equals(noiLamViec)) {
                        checkFormChange = true;
                        Common.checkFormChange = true;
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtTenCha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")) {
                    if(!s.toString().equals(tenCha)) {
                        Common.checkFormChange = true;
                        checkFormChange = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtNamSinhCha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")) {
                    if(!s.toString().equals(namSinhCha)) {
                        Common.checkFormChange = true;
                        checkFormChange = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtNgheNghiepCha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")) {
                    if(!s.toString().equals(ngheNghiepCha)) {
                        Common.checkFormChange = true;

                        checkFormChange = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtNoiCongTacCha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")) {
                    if(!s.toString().equals(noiCongTacCha)) {
                        Common.checkFormChange = true;
                        checkFormChange = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtChoOCha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")) {
                    if(!s.toString().equals(choOCha)) {
                        Common.checkFormChange = true;
                        checkFormChange = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtTenMe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")) {
                    if(!s.toString().equals(tenMe)) {
                        Common.checkFormChange = true;
                        checkFormChange = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtNamSinhMe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")) {
                    if(!s.toString().equals(namSinhMe)) {
                        Common.checkFormChange = true;
                        checkFormChange = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtNgheNghiepMe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")) {
                    if(!s.toString().equals(ngheNghiepMe)) {
                        Common.checkFormChange = true;
                        checkFormChange = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtNoiCongTacMe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")) {
                    if(!s.toString().equals(noiCongTacMe)) {
                        Common.checkFormChange = true;
                        checkFormChange = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtChoOMe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")) {
                    if(!s.toString().equals(choOMe)) {
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

    public void validator() {
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(getActivity(), R.id.edtHo, new SimpleCustomValidation() {
            @Override
            public boolean compare(String s) {
                if (s.length() > 0) return true;
                return false;
            }
        }, R.string.form_null_value);
        awesomeValidation.addValidation(getActivity(), R.id.edtTen, new SimpleCustomValidation() {
            @Override
            public boolean compare(String s) {
                if (s.length() > 0) return true;
                return false;
            }
        }, R.string.form_null_value);
        awesomeValidation.addValidation(getActivity(), R.id.edtNgaySinh, new SimpleCustomValidation() {
            @Override
            public boolean compare(String s) {
                if (s.length() > 0) return true;
                return false;
            }
        }, R.string.form_null_value);
        awesomeValidation.addValidation(getActivity(), R.id.edtSocmnd, new SimpleCustomValidation() {
            @Override
            public boolean compare(String s) {
                if (s.length() > 0) return true;
                return false;
            }
        }, R.string.form_null_value);
        awesomeValidation.addValidation(getActivity(), R.id.edtQueQuan, new SimpleCustomValidation() {
            @Override
            public boolean compare(String s) {
                if (s.length() > 0) return true;
                return false;
            }
        }, R.string.form_null_value);
        awesomeValidation.addValidation(getActivity(), R.id.edtNgheNghiep, new SimpleCustomValidation() {
            @Override
            public boolean compare(String s) {
                if (s.length() > 0) return true;
                return false;
            }
        }, R.string.form_null_value);
        awesomeValidation.addValidation(getActivity(), R.id.edtNoiLamViec, new SimpleCustomValidation() {
            @Override
            public boolean compare(String s) {
                if (s.length() > 0) return true;
                return false;
            }
        }, R.string.form_null_value);
    }

    public void getKhuTro() {
        final DialogLoading dialogLoading = new DialogLoading(getActivity(),"Đang lấy dữ liệu khu trọ, đợi chút...");
        dialogLoading.showDialog();
        String url = "khutro/1";
        compositeDisposable.add(api.getKhutroChosen(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Khutro>>() {
                    @Override
                    public void accept(List<Khutro> khutros) throws Exception {
                        khutroList = khutros;
                        if (khutroList.size() > 0 && nguoitro == null) {
                            txtChonKhuTro.setTag(khutroList.get(0).getId());
                            txtChonKhuTro.setText(khutroList.get(0).getTen());
                            phongtroList = khutroList.get(0).getPhongtro();
                            if(phongtroList.size() > 0) {
                                txtChonPhongTro.setTag(phongtroList.get(0).getId());
                                txtChonPhongTro.setText(String.valueOf(phongtroList.get(0).getTen()));
                            }
                            else {
                                txtChonPhongTro.setTag(-1);
                                txtChonPhongTro.setText("Không có phòng trọ");
                            }
                        }
                        else {
                            if(khutroList.size() == 0) {
                                txtChonKhuTro.setText("Không có khu trọ");
                                txtChonKhuTro.setTag(-1);
                                txtChonPhongTro.setTag(-1);
                                txtChonPhongTro.setText("Không có phòng trọ");
                            }
                            else {
                                phongtroList = nguoitro.getKhutro().getPhongtro();
                                if(phongtroList.size() > 0) {
                                    txtChonPhongTro.setTag(phongtroList.get(0).getId());
                                    txtChonPhongTro.setText(String.valueOf(phongtroList.get(0).getTen()));
                                }
                                else {
                                    txtChonPhongTro.setTag(-1);
                                    txtChonPhongTro.setText("Không có phòng trọ");
                                }
                            }
                        }
                        getTrinhDo(dialogLoading);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                }));
    }

    public void getTrinhDo(final DialogLoading dialogLoading) {
        String url = "trinhdo/1";
        compositeDisposable.add(api.getTrinhdoChosen(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Trinhdo>>() {
                    @Override
                    public void accept(List<Trinhdo> trinhdos) throws Exception {
                        trinhdoList = trinhdos;
                        if(trinhdoList.size() > 0) {
                            if(nguoitro == null) {
                                txtChonTrinhDo.setTag(trinhdoList.get(0).getId());
                                txtChonTrinhDo.setText(trinhdoList.get(0).getTen());
                            }
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

    private void setValue() {
        if(nguoitro.getAvatar() != null) {
            Picasso.get().load(nguoitro.getAvatar()).placeholder(R.drawable.ic_person_outline_32dp).error(R.drawable.ic_person_outline_32dp).into(ivAvatar);
            btnXoaAnh.setVisibility(View.VISIBLE);
        }
        else {
            ivAvatar.setImageResource(R.drawable.ic_person_outline_32dp);
        }
        ho = nguoitro.getHo();
        edtHo.setText(ho);
        ten = nguoitro.getTen();
        edtTen.setText(ten);
        ngaySinh = nguoitro.getNgaysinh2();
        edtNgaySinh.setText(ngaySinh);
        edtNgaySinh.setTag(nguoitro.getNgaysinh());
        soCmnd = nguoitro.getSocmnd();
        edtSocmnd.setText(soCmnd);
        queQuan = nguoitro.getQuequan();
        edtQueQuan.setText(queQuan);
        ngheNhiep = nguoitro.getNghenghiep();
        edtNgheNghiep.setText(ngheNhiep);
        noiLamViec = nguoitro.getNoilamviec();
        edtNoiLamViec.setText(noiLamViec);
        ghiChu = nguoitro.getGhichu();
        edtGhiChu.setText(ghiChu);
        String trangThai = "Đang trọ";
        if(nguoitro.getStatus() == 0) trangThai = "Đã thôi trọ";
        txtTrangThai.setText(trangThai);
        txtTrangThai.setTag(nguoitro.getStatus());
        txtChonPhongTro.setTag(nguoitro.getPhongtro_id());
        txtChonPhongTro.setText(nguoitro.getPhongtro().getTen());
        txtChonTrinhDo.setTag(nguoitro.getTrinhdo().getId());
        txtChonTrinhDo.setText(nguoitro.getTrinhdo().getTen());
        txtChonKhuTro.setTag(nguoitro.getKhutro_id());
        txtChonKhuTro.setText(nguoitro.getKhutro().getTen());

        //người trọ gia đình
        tenCha = nguoitro.getNguoitrogiadinh().getBo_hoten();
        edtTenCha.setText(tenCha);
        namSinhCha = nguoitro.getNguoitrogiadinh().getBo_namsinh();
        edtNamSinhCha.setText(namSinhCha);
        ngheNghiepCha = nguoitro.getNguoitrogiadinh().getBo_nghenghiep();
        edtNgheNghiepCha.setText(ngheNghiepCha);
        noiCongTacCha = nguoitro.getNguoitrogiadinh().getBo_noicongtac();
        edtNoiCongTacCha.setText(noiCongTacCha);
        choOCha = nguoitro.getNguoitrogiadinh().getBo_choohiennay();
        edtChoOCha.setText(choOCha);
        tenMe = nguoitro.getNguoitrogiadinh().getMe_hoten();
        edtTenMe.setText(tenMe);
        namSinhMe = nguoitro.getNguoitrogiadinh().getMe_namsinh();
        edtNamSinhMe.setText(namSinhMe);
        ngheNghiepMe = nguoitro.getNguoitrogiadinh().getMe_nghenghiep();
        edtNgheNghiepMe.setText(ngheNghiepMe);
        namSinhMe = nguoitro.getNguoitrogiadinh().getMe_namsinh();
        edtNamSinhMe.setText(namSinhMe);
        noiCongTacMe = nguoitro.getNguoitrogiadinh().getMe_noicongtac();
        edtNoiCongTacMe.setText(noiCongTacMe);
        choOMe = nguoitro.getNguoitrogiadinh().getMe_choohiennay();
        edtChoOMe.setText(choOMe);
    }

    public void setValueDefault() {
        txtTrangThai.setText("Đang trọ");
        txtTrangThai.setTag("1");
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        edtNgaySinh.setTag(calendar.get(calendar.YEAR) + "/" + (calendar.get(calendar.MONTH)+1) + "/" + calendar.get(calendar.DAY_OF_MONTH));
        ngaySinh = calendar.get(calendar.DAY_OF_MONTH) + "/" + (calendar.get(calendar.MONTH)+1) + "/" + calendar.get(calendar.YEAR);
        edtNgaySinh.setText(ngaySinh);
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

    public void addData(RequestBody nguoitro_id, RequestBody ho, RequestBody ten, MultipartBody.Part file, RequestBody ngaysinh, RequestBody socmnd, RequestBody quequan, RequestBody trinhdo_id, RequestBody nghenghiep, RequestBody noilamviec, RequestBody phongtro_id, RequestBody khutro_id, RequestBody user_id, RequestBody ghichu, RequestBody status, RequestBody type,
                        RequestBody bo_hoten,RequestBody bo_nghenghiep,RequestBody bo_namsinh,RequestBody bo_noicongtac,RequestBody bo_choohiennay,RequestBody me_hoten,RequestBody me_nghenghiep,RequestBody me_noicongtac,RequestBody me_choohiennay,RequestBody me_namsinh) {
        compositeDisposable.add(api.createNguoiTro(nguoitro_id,ho,ten,file,ngaysinh,socmnd,quequan,nghenghiep,noilamviec,user_id,ghichu,status,type,phongtro_id,khutro_id,trinhdo_id,
                bo_hoten,bo_namsinh,bo_nghenghiep,bo_noicongtac,bo_choohiennay,me_hoten,me_nghenghiep,me_noicongtac,me_choohiennay,me_namsinh).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
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
                                checkFormChange =  Common.checkFormChange = false;
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 112 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            uriAvatar = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uriAvatar);
                ivAvatar.setImageBitmap(bitmap);
                btnXoaAnh.setVisibility(View.VISIBLE);
                checkFormChange =true;
                Common.checkFormChange = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edtNgaySinh : {
                initDatePicker();
                break;
            }
            case R.id.ivAvatar:
            case R.id.btnChonAnh : {
                selectImage();
                break;
            }
            case R.id.btnXoaAnh : {
                ivAvatar.setImageResource(R.drawable.ic_person_outline_32dp);
                btnXoaAnh.setVisibility(View.GONE);
                uriAvatar = null;
                if(nguoitro != null) {
                    if(nguoitro.getAvatar() != null) {
                        checkFormChange= true;
                        Common.checkFormChange = true;
                    }
                }
                else {
                    checkFormChange = false;
                    Common.checkFormChange = false;
                }
                break;
            }
            case R.id.txtChonTrangThai : {
                typeChosenItem = 3;
                List<Item> items = new ArrayList<>();
                int trangThai = Integer.parseInt(txtTrangThai.getTag().toString());
                if(trangThai == 1) {
                    items.add(new Item(true, 1, 0, "Đang trọ"));
                    items.add(new Item(false, 0, 1, "Đã thôi trọ"));
                }
                else {
                    items.add(new Item(false, 1, 0, "Đang trọ"));
                    items.add(new Item(true, 0, 1, "Đã thôi trọ"));
                }
                DialogChosenItem dialogChosenItem = new DialogChosenItem(getActivity(),items,"Chọn tình trạng","single",0, false);
                dialogChosenItem.setChosenItemCallback(this);
                dialogChosenItem.showDialog();
                break;
            }
            case R.id.txtChonPhongTro: {
                if(phongtroList.size() > 0) {
                    typeChosenItem = 1;
                    int id = Integer.parseInt(txtChonPhongTro.getTag().toString());
                    List<Item> items = new ArrayList<>();
                    int stt = 0;
                    for (Phongtro phongtro : phongtroList) {
                        if (phongtro.getId() == id) items.add(new Item(true, phongtro.getId(), stt, phongtro.getTen()));
                        else items.add(new Item(false, phongtro.getId(), stt, phongtro.getTen()));
                        stt++;
                    }
                    DialogChosenItem dialogChosen = new DialogChosenItem(getActivity(), items, "Chọn khu trọ", "single",sttPhongTro, true);
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
            case R.id.txtChonTrinhDo : {
                if(trinhdoList.size() > 0) {
                    typeChosenItem = 2;
                    int id = Integer.parseInt(txtChonTrinhDo.getTag().toString());
                    List<Item> items = new ArrayList<>();
                    int stt = 0;
                    for (Trinhdo trinhdo : trinhdoList) {
                        if (trinhdo.getId() == id) items.add(new Item(true, trinhdo.getId(), stt, trinhdo.getTen()));
                        else items.add(new Item(false, trinhdo.getId(), stt, trinhdo.getTen()));
                        stt++;
                    }
                    DialogChosenItem dialogChosen = new DialogChosenItem(getActivity(), items, "Chọn trình độ", "single",sttTrinhDo, false);
                    dialogChosen.setChosenItemCallback(this);
                    dialogChosen.showDialog();
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
                    RequestBody ho = RequestBody.create(MediaType.parse("multipart/form-data"), edtHo.getText().toString());
                    RequestBody ten = RequestBody.create(MediaType.parse("multipart/form-data"), edtTen.getText().toString());
                    RequestBody ngaysinh = RequestBody.create(MediaType.parse("multipart/form-data"), edtNgaySinh.getTag().toString());
                    RequestBody socmnd = RequestBody.create(MediaType.parse("multipart/form-data"), edtSocmnd.getText().toString());
                    RequestBody quequan = RequestBody.create(MediaType.parse("multipart/form-data"), edtQueQuan.getText().toString());
                    RequestBody trinhdo = RequestBody.create(MediaType.parse("multipart/form-data"), txtChonTrinhDo.getTag().toString());
                    RequestBody nghenghiep = RequestBody.create(MediaType.parse("multipart/form-data"), edtNgheNghiep.getText().toString());
                    RequestBody noilamviec = RequestBody.create(MediaType.parse("multipart/form-data"), edtNoiLamViec.getText().toString());
                    RequestBody phongtro_id = RequestBody.create(MediaType.parse("multipart/form-data"), txtChonPhongTro.getTag().toString());
                    RequestBody khutro_id = RequestBody.create(MediaType.parse("multipart/form-data"), txtChonKhuTro.getTag().toString());
                    RequestBody user_id = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(Common.currentUser.getId()));
                    RequestBody ghichu = RequestBody.create(MediaType.parse("multipart/form-data"), edtGhiChu.getText().toString());
                    RequestBody status = RequestBody.create(MediaType.parse("multipart/form-data"), txtTrangThai.getTag().toString());
                    RequestBody bo_hoten = RequestBody.create(MediaType.parse("multipart/form-data"), edtTenCha.getText().toString());
                    RequestBody bo_nghenghiep = RequestBody.create(MediaType.parse("multipart/form-data"), edtNgheNghiepCha.getText().toString());
                    RequestBody bo_namsinh = RequestBody.create(MediaType.parse("multipart/form-data"), edtNamSinhCha.getText().toString());
                    RequestBody bo_noicongtac = RequestBody.create(MediaType.parse("multipart/form-data"), edtNoiCongTacCha.getText().toString());
                    RequestBody bo_choohiennay = RequestBody.create(MediaType.parse("multipart/form-data"), edtChoOCha.getText().toString());
                    RequestBody me_hoten = RequestBody.create(MediaType.parse("multipart/form-data"), edtTenMe.getText().toString());
                    RequestBody me_nghenghiep = RequestBody.create(MediaType.parse("multipart/form-data"), edtNgheNghiepMe.getText().toString());
                    RequestBody me_noicongtac = RequestBody.create(MediaType.parse("multipart/form-data"), edtNoiCongTacMe.getText().toString());
                    RequestBody me_choohiennay = RequestBody.create(MediaType.parse("multipart/form-data"), edtChoOMe.getText().toString());
                    RequestBody me_namsinh = RequestBody.create(MediaType.parse("multipart/form-data"), edtNamSinhMe.getText().toString());
                    if (nguoitro == null) {
                        RequestBody nguoiTroId = RequestBody.create(MediaType.parse("multipart/form-data"), "-1");
                        RequestBody type = RequestBody.create(MediaType.parse("multipart/form-data"), "1");
                        addData(nguoiTroId,ho,ten,file,ngaysinh,socmnd,quequan,trinhdo,nghenghiep,noilamviec,phongtro_id,khutro_id,user_id,ghichu,status,type,
                                bo_hoten,bo_namsinh,bo_nghenghiep,bo_noicongtac,bo_choohiennay,me_hoten,me_nghenghiep,me_noicongtac,me_choohiennay,me_namsinh);
                    }
                    else {
                        RequestBody phongTroId = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(nguoitro.getId()));
                        RequestBody type = RequestBody.create(MediaType.parse("multipart/form-data"), "0");
                        addData(phongTroId,ho,ten,file,ngaysinh,socmnd,quequan,trinhdo,nghenghiep,noilamviec,phongtro_id,khutro_id,user_id,ghichu,status,type,
                                bo_hoten,bo_nghenghiep,bo_namsinh,bo_noicongtac,bo_choohiennay,me_hoten,me_nghenghiep,me_noicongtac,me_choohiennay,me_namsinh);
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

    @Override
    public void onResume() {
        super.onResume();
        Common.checkFormChange = checkFormChange;

    }

    @Override
    public void onReceiveItem(List<Item> item) {
        switch (typeChosenItem) {
            case 0: {
                if(Integer.parseInt(txtChonKhuTro.getTag().toString()) != item.get(0).getId()) {
                    checkFormChange = true;
                    Common.checkFormChange = true;
                }
                txtChonKhuTro.setText(item.get(0).getName());
                txtChonKhuTro.setTag(item.get(0).getId());
                sttKhuTro = item.get(0).getStt();
                phongtroList = khutroList.get(item.get(0).getStt()).getPhongtro();
                if(phongtroList.size() > 0) {
                    txtChonPhongTro.setTag(phongtroList.get(0).getId());
                    txtChonPhongTro.setText(String.valueOf(phongtroList.get(0).getTen()));
                }
                else {
                    txtChonPhongTro.setTag(-1);
                    txtChonPhongTro.setText("Không có phòng trọ");
                }
                break;
            }
            case 1: {
                if(Integer.parseInt(txtChonPhongTro.getTag().toString()) != item.get(0).getId()) {
                    Common.checkFormChange = true;
                    checkFormChange = true;
                }
                txtChonPhongTro.setText(item.get(0).getName());
                txtChonPhongTro.setTag(item.get(0).getId());
                sttPhongTro = item.get(0).getStt();
                break;
            }
            case 2: {
                if(Integer.parseInt(txtChonTrinhDo.getTag().toString()) != item.get(0).getId()) {
                    Common.checkFormChange = true;
                    checkFormChange = true;
                }
                txtChonTrinhDo.setText(item.get(0).getName());
                txtChonTrinhDo.setTag(item.get(0).getId());
                sttTrinhDo = item.get(0).getStt();
                break;
            }
            case 3: {
                if(Integer.parseInt(txtTrangThai.getTag().toString()) != item.get(0).getId()) {
                    Common.checkFormChange = true;
                    checkFormChange = true;
                }
                txtTrangThai.setText(item.get(0).getName());
                txtTrangThai.setTag(item.get(0).getId());
                break;
            }
        }
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        edtNgaySinh.setText(date);
        edtNgaySinh.setTag(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
    }
}