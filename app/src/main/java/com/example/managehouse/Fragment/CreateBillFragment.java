package com.example.managehouse.Fragment;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.custom.SimpleCustomValidation;
import com.example.managehouse.Activity.HomeActivity;
import com.example.managehouse.Activity.ShowBillActivity;
import com.example.managehouse.Callback.ChosenItemCallback;
import com.example.managehouse.Common.Common;
import com.example.managehouse.Fragment.KhuTro.FormFragment;
import com.example.managehouse.Model.Hoadon;
import com.example.managehouse.Model.Item;
import com.example.managehouse.Model.Khutro;
import com.example.managehouse.Model.Khutrokhoanthu;
import com.example.managehouse.Model.Message;
import com.example.managehouse.Model.Nguoitro;
import com.example.managehouse.Model.Phongtro;
import com.example.managehouse.R;
import com.example.managehouse.Retrofit.API;
import com.example.managehouse.Service.DialogChosenItem;
import com.example.managehouse.Service.DialogLoading;
import com.example.managehouse.Service.DialogNotification;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialog;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
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

public class CreateBillFragment extends Fragment implements View.OnClickListener, ChosenItemCallback {

    private EditText edtSoDienCu, edtSoDienMoi, edtTen, edtThang, edtTienPhong, edtSoDien, edtGhiChu, edtSoNuocCu, edtSoNuocMoi, edtSoNuoc;
    private TextView txtChonKhuTro, txtChonPhongTro, txtCacKhoanThu, txtTongTien, txtChonTrangThai;
    private LinearLayout llNhapSoNuoc, llSoNuoc;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private API api;
    private List<Khutro> khutroList = new ArrayList<>();
    private List<Phongtro> phongtroList = new ArrayList<>();
    private DialogLoading dialogLoading;
    private HomeActivity homeActivity;
    private AwesomeValidation awesomeValidation;
    private DialogChosenItem dialogChosen = null;
    private int typeChosenItem = -1;
    private int sttKhuTro = 0, sttPhongTro = 0, userId = 10;
    private boolean checkDonViNuoc = false; // false - đơn vị theo tháng | true - đơn vị theo khối
    private boolean checkLoadForm = true, checkFormChange = false;
    private String tenHoaDon = "", thangHoaDon = "", ghiChu = "";
    private int tienPhong = 0, soDienCu = 0, soNuocCu =0, soDienMoi = 0, soNuocMoi = 0, soDien = 0, soNuoc = 0;
    private Hoadon hoadon = null;
    private int khutroId = 0, phongtroId = 0;
    private String tenKhuTro = "", tenPhongTro = "";

    public CreateBillFragment() {
        // Required empty public constructor
    }

    public static CreateBillFragment newInstance(String param1, String param2) {
        CreateBillFragment fragment = new CreateBillFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            hoadon = (Hoadon) getArguments().getSerializable("hoadon");
            khutroId = getArguments().getInt("khutro_id");
            phongtroId = getArguments().getInt("phongtro_id");
        }
        if(khutroId != 0 && phongtroId != 0) {
            Common.checkFormChange = true;
        }
        else Common.checkFormChange = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_bill, container, false);
        mapping(view);
        textChange();
        // init api
        api = Common.getAPI();
        dialogLoading = new DialogLoading(getActivity(), "Đang khởi tạo, đợi chút...");
        dialogLoading.showDialog();
        if(checkLoadForm) initForm();
        getKhutro();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        validator();

    }

    public void mapping(View view) {
        homeActivity.ivAction.setImageResource(R.drawable.ic_save_32dp);
        txtChonKhuTro = view.findViewById(R.id.txtChonKhuTro);
        txtChonPhongTro = view.findViewById(R.id.txtChonPhongTro);
        edtSoDienCu = view.findViewById(R.id.edtSoDienCu);
        edtSoDienMoi = view.findViewById(R.id.edtSoDienMoi);
        edtTen = view.findViewById(R.id.edtTen);
        edtThang = view.findViewById(R.id.edtThang);
        edtTienPhong = view.findViewById(R.id.edtTienPhong);
        edtSoDien = view.findViewById(R.id.edtSoDien);
        txtCacKhoanThu = view.findViewById(R.id.txtCacKhoanThu);
        txtTongTien = view.findViewById(R.id.txtTongTien);
        edtGhiChu = view.findViewById(R.id.edtGhiChu);
        txtChonKhuTro.setOnClickListener(this);
        txtChonPhongTro.setOnClickListener(this);
        txtCacKhoanThu.setOnClickListener(this);
        llNhapSoNuoc = view.findViewById(R.id.llNhapSoNuoc);
        llSoNuoc = view.findViewById(R.id.llSoNuoc);
        edtSoNuocCu = view.findViewById(R.id.edtSoNuocCu);
        edtSoNuocMoi = view.findViewById(R.id.edtSoNuocMoi);
        edtSoNuoc = view.findViewById(R.id.edtSoNuoc);
        edtTienPhong.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    String value = edtTienPhong.getText().toString();
                    edtTienPhong.setText(String.valueOf(Common.clearMoney(value)));
                } else {
                    edtTienPhong.setTag(Common.clearMoney(edtTienPhong.getText().toString()));
                    edtTienPhong.setText(Common.formatNumber(Integer.parseInt(edtTienPhong.getText().toString()),true));
                }
            }
        });
        edtThang.setCursorVisible(false);
        edtThang.setFocusableInTouchMode(false);
        edtThang.setFocusable(false);
        edtThang.setOnClickListener(this);
        homeActivity.ivAction.setOnClickListener(this);
        txtChonTrangThai = view.findViewById(R.id.txtChonTrangThai);
        txtChonTrangThai.setOnClickListener(this);
    }

    public void textChange() {
        edtSoDienMoi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    int sdc = Integer.parseInt(edtSoDienCu.getText().toString());
                    int sdm = Integer.parseInt(s.toString());
                    if(sdm != soDienMoi) {
                        Common.checkFormChange = true;
                        checkFormChange = true;
                    }
                    soDien = sdm - sdc;
                    if (soDien > 0) {
                        edtSoDien.setText(String.valueOf(soDien));
                        totalMoney();
                    } else {
                        soDien = 0;
                        edtSoDien.setText("0");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtSoDienCu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    if(Integer.parseInt(s.toString()) != soDienCu) {
                        Common.checkFormChange = true;
                        checkFormChange = true;
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtSoNuocMoi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    int snc = Integer.parseInt(edtSoNuocCu.getText().toString());
                    int snm = Integer.parseInt(s.toString());
                    if(snm != soNuocMoi) {
                        Common.checkFormChange = true;
                        checkFormChange = true;
                    }

                    soNuoc = snm - snc;
                    if (soNuoc > 0) {
                        edtSoNuoc.setText(String.valueOf(soNuoc));
                        totalMoney();
                    } else {
                        edtSoNuoc.setText("0");
                        soNuoc = 0;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtSoNuocCu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    if(Integer.parseInt(s.toString()) != soNuocCu) {
                        Common.checkFormChange = true;
                        checkFormChange = true;
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
                if (!s.toString().equals("")) {
                    if(!s.toString().equals(tenHoaDon)) {
                        Common.checkFormChange = true;
                        checkFormChange = true;
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtThang.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    if(!s.toString().equals(thangHoaDon)) {
                        Common.checkFormChange = true;
                        checkFormChange = true;
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtTienPhong.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    if(tienPhong != Integer.parseInt(edtTienPhong.getTag().toString())) {
                        Common.checkFormChange = true;
                        checkFormChange = true;
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtSoDien.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")) {
                    if(!s.toString().equals(String.valueOf(soDien))) {
                        Common.checkFormChange = true;
                        checkFormChange = true;
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtSoNuoc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")) {
                    if(!s.toString().equals(String.valueOf(soNuoc))) {
                        Common.checkFormChange = true;
                        checkFormChange = true;
                    }

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
                    if(!s.toString().equals(ghiChu)) {
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

    public void initForm() {
        String color = "#f39c12", text = "Chờ thu tiền";
        int status = 1;
        if(hoadon != null) {
            soDienCu = hoadon.getSodiencu();
            soNuocCu = hoadon.getSonuoccu();
            soDienMoi = hoadon.getSodienmoi();
            soNuocMoi = hoadon.getSonuocmoi();
            soDien = soDienMoi - soDienCu;
            soNuoc = soNuocMoi - soNuocCu;
            tenHoaDon = hoadon.getTen();
            thangHoaDon = hoadon.getThang() + "/" + hoadon.getNam();
            txtChonPhongTro.setTag(hoadon.getPhongtro_id());
            txtChonPhongTro.setText(hoadon.getPhongtro().getTen());
            tienPhong = hoadon.getPhongtro().getGia();
            edtTienPhong.setTag(tienPhong);
            edtTienPhong.setText(Common.formatNumber(tienPhong,true));
            txtTongTien.setTag(hoadon.getTongtien());
            txtTongTien.setText(Common.formatNumber(hoadon.getTongtien(),true));
            ghiChu = hoadon.getGhichu();
            edtGhiChu.setText(ghiChu);
            status = hoadon.getStatus();
            if(hoadon.getStatus() == 2) {
                color = "#27ae60";
                text = "Đã thu tiền";
            }
            else {
                if(hoadon.getStatus() == 0) {
                    color = "#e74c3c";
                    text = "Đã hủy";
                }
            }
        }
        else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();
            String currentDate = simpleDateFormat.format(date);
            tenHoaDon = "HĐ-" + currentDate;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            thangHoaDon = (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR);
        }
        edtSoDienCu.setText(String.valueOf(soDienCu));
        edtSoDienMoi.setText(String.valueOf(soDienMoi));
        edtSoNuocCu.setText(String.valueOf(soNuocCu));
        edtSoNuocMoi.setText(String.valueOf(soNuocMoi));
        edtSoDien.setText(String.valueOf(soDien));
        edtSoNuoc.setText(String.valueOf(soNuoc));
        edtTen.setText(tenHoaDon);
        edtThang.setText(thangHoaDon);
        txtChonTrangThai.setTag(status);
        txtChonTrangThai.setText(text);
        txtChonTrangThai.setTextColor(Color.parseColor(color));
        txtChonKhuTro.setTag(0);
        txtChonPhongTro.setTag(0);
    }

    public void validator() {
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(getActivity(), R.id.edtSoDienCu, new SimpleCustomValidation() {
            @Override
            public boolean compare(String s) {
                if (s.length() > 0) return true;
                return false;
            }
        }, R.string.form_null_value);
        awesomeValidation.addValidation(getActivity(), R.id.edtSoDienMoi, new SimpleCustomValidation() {
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
        awesomeValidation.addValidation(getActivity(), R.id.edtThang, new SimpleCustomValidation() {
            @Override
            public boolean compare(String s) {
                if (s.length() > 0) return true;
                return false;
            }
        }, R.string.form_null_value);
        awesomeValidation.addValidation(getActivity(), R.id.edtTienPhong, new SimpleCustomValidation() {
            @Override
            public boolean compare(String s) {
                if (s.length() > 0) return true;
                return false;
            }
        }, R.string.form_null_value);
        awesomeValidation.addValidation(getActivity(), R.id.edtSoDien, new SimpleCustomValidation() {
            @Override
            public boolean compare(String s) {
                if (s.length() > 0) return true;
                return false;
            }
        }, R.string.form_null_value);
    }

    public void createHoaDon(int hoadon_id, String ten, int thang, int nam, int phongtro_id, int soDienCu, int soDienMoi, int soNuocCu, int soNuocMoi, String cacKhoanThu, int tongTien, int checkNuoc, String ghiChu, int status, int type) {
        compositeDisposable.add(api.createHoadon(hoadon_id, ten, thang, nam, phongtro_id, khutroList.get(sttKhuTro).getId(),soDienCu, soDienMoi, soNuocCu,soNuocMoi, cacKhoanThu, tongTien,checkNuoc,ghiChu,status,type).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Message>() {
                    @Override
                    public void accept(Message message) throws Exception {
                        dialogLoading.hideDialog();
                        if (message.getStatus() == 401) {
                            new DialogNotification(getActivity(), message.getBody(), "error").showDialog();
                        } else {
                            if (message.getStatus() == 402) {
                                Toasty.error(getContext(), message.getBody()[0], 300, true).show();
                            } else {
                                checkLoadForm = false;
                                Common.checkFormChange = false;
                                Toasty.success(getContext(), message.getBody()[0], 300, true).show();
                                if(type == 1) {
                                    Intent intent = new Intent(getContext(), ShowBillActivity.class);
                                    Log.d("cuong",message.getData());
                                    intent.putExtra("hoadon", message.getData());
                                    intent.putExtra("checkDonViNuoc", checkDonViNuoc);
                                    startActivity(intent);
                                }
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toasty.error(getContext(), "Gặp sự cố, thử lại sau.", 300, true).show();
                        dialogLoading.hideDialog();
                        throwable.printStackTrace();
                    }
                }));
    }

    public void getKhutro() {
        String url = "khutro/1";
        compositeDisposable.add(api.getKhutroChosen(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Khutro>>() {
                    @Override
                    public void accept(List<Khutro> khutros) throws Exception {
                        khutroList = khutros;
                        if (khutroList.size() > 0) {
                            if(khutroId != 0 && phongtroId != 0) {
                                for (Khutro khutro: khutroList) {
                                    if(khutro.getId() == khutroId) {
                                        phongtroList = khutro.getPhongtro();
                                        Item item = new Item(false,khutroId,sttKhuTro,khutro.getTen());
                                        chosenKhuTro(item,true);
                                        break;
                                    }
                                    sttKhuTro++;
                                }
                                for (Phongtro phongtro : phongtroList) {
                                    if(phongtro.getId() == phongtroId) {
                                        Item item = new Item(false,phongtroId,sttPhongTro,phongtro.getTen());
                                        chosenPhongTro(item);
                                        break;
                                    }
                                    sttPhongTro++;
                                }
                            }
                            else {
                                Khutro khutro = null;
                                if(hoadon == null) {
                                    khutro = khutroList.get(0);
                                    phongtroList = khutro.getPhongtro();
                                    if(phongtroList.size() > 0) {
                                        txtChonPhongTro.setTag(phongtroList.get(0).getId());
                                        txtChonPhongTro.setText(String.valueOf(phongtroList.get(0).getTen()));
                                        soDienCu = phongtroList.get(0).getChotsodien();
                                        edtSoDienCu.setText(String.valueOf(phongtroList.get(0).getChotsodien()));
                                        soNuocCu = phongtroList.get(0).getChotsonuoc();
                                        edtSoNuocCu.setText(String.valueOf(phongtroList.get(0).getChotsonuoc()));
                                        tienPhong = phongtroList.get(0).getGia();
                                        edtTienPhong.setTag(phongtroList.get(0).getGia());
                                        edtTienPhong.setText(Common.formatNumber(phongtroList.get(0).getGia(),true));
                                        totalMoney();
                                    }
                                    else {
                                        txtChonPhongTro.setTag(-1);
                                        txtChonPhongTro.setText("Không có phòng trọ");
                                    }
                                }
                                else {
                                    khutro = hoadon.getPhongtro().getKhutro();
                                    for (Khutro kt : khutroList) {
                                        if(kt.getId() == khutro.getId()) {
                                            phongtroList = kt.getPhongtro();
                                            break;
                                        }
                                    }
                                }
                                txtChonKhuTro.setText(khutro.getTen());
                                txtChonKhuTro.setTag(khutro.getId());
                                checkInputWater(khutro);
                                int i = 0;
                                String khoanThu = "", idKhoanThu = "";
                                for (Khutrokhoanthu khutrokhoanthu : khutro.getKhutrokhoanthu()) {
                                    if(i == khutro.getKhutrokhoanthu().size() - 1) {
                                        khoanThu += khutrokhoanthu.getKhoanthu().getTen();
                                        idKhoanThu += khutrokhoanthu.getKhoanthu_id();
                                    }
                                    else {
                                        khoanThu += khutrokhoanthu.getKhoanthu().getTen() + ", ";
                                        idKhoanThu += khutrokhoanthu.getKhoanthu_id() + ",";
                                    }
                                    i++;
                                }
                                txtCacKhoanThu.setText(khoanThu);
                                txtCacKhoanThu.setTag(idKhoanThu);
                            }

                        }
                        else {
                            txtChonKhuTro.setText("Không có khu trọ");
                            txtChonKhuTro.setTag("-1");
                            txtChonPhongTro.setTag(-1);
                            txtChonPhongTro.setText("Không có phòng trọ");
                            dialogLoading.hideDialog();
                        }
                        dialogLoading.hideDialog();

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toasty.error(getContext(), "Gặp sự cố, thử lại sau.", 300, true).show();
                        dialogLoading.hideDialog();
                        throwable.printStackTrace();
                    }
                }));
    }

    public void totalMoney() {
        if(khutroList.size() > 0){
            int tienPhong = (edtTienPhong.getTag() == null ? 0 : Integer.parseInt(edtTienPhong.getTag().toString()));
            int giaDien = 0;
            int giaNuoc = 0;
            int tienKhoanThu = 0;
            int total = 0;
            Object idList = txtCacKhoanThu.getTag();
            String[] idChosen = null;
            if (idList != null) {
                idChosen = idList.toString().split(",");
            }
            Khutro khutro = khutroList.get(sttKhuTro);
            for (Khutrokhoanthu khutrokhoanthu : khutro.getKhutrokhoanthu()) {
                if (khutrokhoanthu.getKhoanthu_id() == 5) {
                    if(idChosen == null) giaDien = khutrokhoanthu.getGia();
                    else {
                        if(!Arrays.asList(idChosen).contains("5")) {
                            giaDien = 0;
                        }
                        else giaDien = khutrokhoanthu.getGia();
                    }
                }
                else {
                    if(khutrokhoanthu.getKhoanthu_id() == 1) {
                        if(idChosen == null) giaNuoc = khutrokhoanthu.getGia();
                        else {
                            if(!Arrays.asList(idChosen).contains("1")) {
                                giaNuoc = 0;
                            }
                            else giaNuoc = khutrokhoanthu.getGia();
                        }

                    }
                    else {
                        if(idChosen != null) {
                            if (Arrays.asList(idChosen).contains(String.valueOf(khutrokhoanthu.getKhoanthu_id()))) {
                                tienKhoanThu += khutrokhoanthu.getGia();
                            }
                        }
                    }
                }
            }
            int soDien = Integer.parseInt(edtSoDien.getText().toString());
            int soNuoc = Integer.parseInt(edtSoNuoc.getText().toString());
            total = tienPhong + tienKhoanThu + soDien * giaDien;
            if(checkDonViNuoc) {
                total += soNuoc * giaNuoc;
            }
            else total += giaNuoc;
            txtTongTien.setText(Common.formatNumber(total,true));
            txtTongTien.setTag(total);
        }

    }

    public void chosenMonthYear() {
        String thangCurrent = edtThang.getText().toString();
        int monthCurrent = Integer.parseInt(thangCurrent.substring(0, thangCurrent.indexOf("/"))) - 1;
        int yearCurrent = Integer.parseInt(thangCurrent.substring(thangCurrent.indexOf("/") + 1));
        MonthYearPickerDialogFragment monthYearPickerDialogFragment = MonthYearPickerDialogFragment.getInstance(monthCurrent, yearCurrent);
        monthYearPickerDialogFragment.setOnDateSetListener(new MonthYearPickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(int year, int monthOfYear) {
                String value = (monthOfYear + 1) + "/" + year;
                edtThang.setText(value);
            }
        });
        monthYearPickerDialogFragment.show(getFragmentManager(), null);
    }

    public void checkInputWater(Khutro khutro) {
        for (Khutrokhoanthu khutrokhoanthu : khutro.getKhutrokhoanthu()) {
            if (khutrokhoanthu.getKhoanthu().getTen().equals("Nước")) {
                if(khutrokhoanthu.getDonvitinh_id() != 3) {
                    llNhapSoNuoc.setVisibility(View.GONE);
                    llSoNuoc.setVisibility(View.GONE);
                    checkDonViNuoc = false;
                }
                else {
                    llNhapSoNuoc.setVisibility(View.VISIBLE);
                    llSoNuoc.setVisibility(View.VISIBLE);
                    checkDonViNuoc = true;
                }
            }
        }
    }

    public void chosenKhuTro(Item item, boolean notifi) {
        if(item.getId() != Integer.parseInt(txtChonKhuTro.getTag().toString())) {
            Common.checkFormChange = true;
            checkFormChange = true;
        }
        txtChonKhuTro.setText(item.getName());
        txtChonKhuTro.setTag(item.getId());
        sttKhuTro = item.getStt();
        checkInputWater(khutroList.get(sttKhuTro));
        int i = 0;
        String khoanThu = "", idKhoanThu = "";
        for (Khutrokhoanthu khutrokhoanthu : khutroList.get(sttKhuTro).getKhutrokhoanthu()) {
            if(i == khutroList.get(sttKhuTro).getKhutrokhoanthu().size() - 1) {
                khoanThu += khutrokhoanthu.getKhoanthu().getTen();
                idKhoanThu += khutrokhoanthu.getKhoanthu_id();
            }
            else {
                khoanThu += khutrokhoanthu.getKhoanthu().getTen() + ", ";
                idKhoanThu += khutrokhoanthu.getKhoanthu_id() + ",";
            }
            i++;
        }
        txtCacKhoanThu.setText(khoanThu);
        txtCacKhoanThu.setTag(idKhoanThu);
        if(!notifi) {
            phongtroList = khutroList.get(item.getStt()).getPhongtro();
            if(phongtroList.size() > 0) {
                txtChonPhongTro.setTag(phongtroList.get(0).getId());
                txtChonPhongTro.setText(String.valueOf(phongtroList.get(0).getTen()));
                soDienCu = phongtroList.get(0).getChotsodien();
                edtSoDienCu.setText(String.valueOf(phongtroList.get(0).getChotsodien()));
                soNuocCu = phongtroList.get(0).getChotsonuoc();
                edtSoNuocCu.setText(String.valueOf(phongtroList.get(0).getChotsonuoc()));
                tienPhong = phongtroList.get(0).getGia();
                edtTienPhong.setTag(phongtroList.get(0).getGia());
                edtTienPhong.setText(Common.formatNumber(phongtroList.get(0).getGia(),true));
                tienPhong = phongtroList.get(0).getGia();
            }
            else {
                txtChonPhongTro.setTag(-1);
                txtChonPhongTro.setText("Không có phòng trọ");
            }
        }
        totalMoney();
    }

    public void chosenPhongTro(Item item) {
        if(item.getId() != Integer.parseInt(txtChonPhongTro.getTag().toString())) {
            Common.checkFormChange = true;
            checkFormChange = true;
        }
        txtChonPhongTro.setText(item.getName());
        txtChonPhongTro.setTag(item.getId());
        sttPhongTro = item.getStt();
        Phongtro phongtro = phongtroList.get(sttPhongTro);
        tienPhong = phongtro.getGia();
        edtTienPhong.setTag(phongtro.getGia());
        edtTienPhong.setText(Common.formatNumber(phongtro.getGia(),true));
        tienPhong = phongtro.getGia();
        soNuocCu =phongtro.getChotsonuoc();
        soDienCu = phongtro.getChotsodien();
        edtSoDienCu.setText(String.valueOf(phongtro.getChotsodien()));
        edtSoNuocCu.setText(String.valueOf(phongtro.getChotsonuoc()));
        totalMoney();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof HomeActivity) {
            homeActivity = (HomeActivity) context;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivAction : {
                if (awesomeValidation.validate()) {
                    boolean check = false;
                    String ten = edtTen.getText().toString();
                    String date = edtThang.getText().toString();
                    int thang = Integer.parseInt(date.substring(0, date.indexOf("/")));
                    int nam = Integer.parseInt(date.substring(date.indexOf("/") + 1));
                    int phongtro_id = Integer.parseInt(txtChonPhongTro.getTag().toString());
                    int soDienCu = Integer.parseInt(edtSoDienCu.getText().toString());
                    int soDienMoi = Integer.parseInt(edtSoDienMoi.getText().toString());
                    int soNuocCu = Integer.parseInt(edtSoNuocCu.getText().toString());
                    int soNuocMoi = Integer.parseInt(edtSoNuocMoi.getText().toString());
                    String ghiChu = edtGhiChu.getText().toString();
                    int status = Integer.parseInt(txtChonTrangThai.getTag().toString());
                    int type = 1, hoadon_id = 0;
                    if(hoadon != null) {
                        type = 0;
                        hoadon_id = hoadon.getId();
                    }
                    List<String> erros = new ArrayList<>();
                    if(soDienMoi < soDienCu) {
                        check = true;
                        erros.add("Số điện mới không được nhỏ hơn số điện cũ");
                    }
                    if(soNuocMoi < soNuocCu) {
                        check = true;
                        erros.add("Số nước mới không được nhỏ hơn số nước cũ");
                    }
                    if(check) {
                        String[] body =  new String[erros.size()];
                        body = erros.toArray(body);
                        DialogNotification dialogNotification = new DialogNotification(getActivity(),body,"error");
                        dialogNotification.showDialog();
                    }
                    else {
                        String body = "Đang tạo hóa đơn...";
                        if(type != 1) {
                            body = "Đang sửa hóa đơn...";
                        }
                        dialogLoading = new DialogLoading(getActivity(), body);
                        dialogLoading.showDialog();
                        String cacKhoanThu = null;
                        if (txtCacKhoanThu.getTag() != null)
                            cacKhoanThu = txtCacKhoanThu.getTag().toString();
                        int tongTien = Integer.parseInt(txtTongTien.getTag().toString());
                        createHoaDon(hoadon_id,ten, thang, nam, phongtro_id, soDienCu, soDienMoi, soNuocCu,soNuocMoi, cacKhoanThu, tongTien,(checkDonViNuoc) ? 1 : 0,ghiChu,status,type);
                    }

                }
                break;
            }
            case R.id.txtChonKhuTro: {
                typeChosenItem = 0;
                if(khutroList.size() > 0) {

                    int id = Integer.parseInt(txtChonKhuTro.getTag().toString());
                    List<Item> items = new ArrayList<>();
                    int stt = 0;
                    for (Khutro khutro : khutroList) {
                        if (khutro.getId() == id) items.add(new Item(true, khutro.getId(), stt, khutro.getTen()));
                        else items.add(new Item(false, khutro.getId(), stt, khutro.getTen()));
                        stt++;
                    }
                    dialogChosen = new DialogChosenItem(getActivity(), items, "Chọn khu trọ", "single",sttKhuTro, true);
                    dialogChosen.setChosenItemCallback(this);
                    dialogChosen.showDialog();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Không có khu trọ nào, hãy thêm khu trọ mới.")
                            .setPositiveButton(R.string.confirm_delete_button_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FormFragment formFragment = new FormFragment();
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
            case R.id.txtChonPhongTro: {
                typeChosenItem = 1;
                if(phongtroList.size() > 0) {

                    int id = Integer.parseInt(txtChonPhongTro.getTag().toString());
                    List<Item> items = new ArrayList<>();
                    int stt = 0;
                    for (Phongtro phongtro : phongtroList) {
                        if (phongtro.getId() == id)
                            items.add(new Item(true, phongtro.getId(), stt, phongtro.getTen()));
                        else items.add(new Item(false, phongtro.getId(), stt, phongtro.getTen()));
                        stt++;
                    }
                    dialogChosen = new DialogChosenItem(getActivity(), items, "Chọn phòng trọ", "single", sttPhongTro, true);
                    dialogChosen.setChosenItemCallback(this);
                    dialogChosen.showDialog();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Không có phòng trọ nào trong khu, hãy thêm phòng trọ mới.")
                            .setPositiveButton(R.string.confirm_delete_button_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

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
            case R.id.txtCacKhoanThu: {
                typeChosenItem = 2;
                Khutro khutro = khutroList.get(sttKhuTro);
                List<Item> items = new ArrayList<>();
                Object idList = txtCacKhoanThu.getTag();
                String[] idChosen = null;
                if (idList != null) {
                    idChosen = idList.toString().split(",");
                }
                int stt = 0;
                int checkedItem = 0;
                for (Khutrokhoanthu khutrokhoanthu : khutro.getKhutrokhoanthu()) {
                    boolean checked = false;
                    if(idChosen == null) {
                        if(khutrokhoanthu.getKhoanthu().getTen().equals("Nước") || khutrokhoanthu.getKhoanthu().getTen().equals("Điện")) {
                            checked = true;
                        }
                    }
                    else {
                        if(Arrays.asList(idChosen).contains(String.valueOf(khutrokhoanthu.getKhoanthu_id()))) {
                            checked = true;
                        }
                    }
                    if(checked) checkedItem++;
                    items.add(new Item(checked, khutrokhoanthu.getKhoanthu_id(), stt, khutrokhoanthu.getKhoanthu().getTen() + " - " + Common.formatNumber(khutrokhoanthu.getGia(),true)));
                    stt++;
                }

                dialogChosen = new DialogChosenItem(getActivity(), items, "Chọn các khoản thu", "multi",0, false);
                dialogChosen.setChosenItemCallback(this);
                dialogChosen.showDialog();
                if(checkedItem == khutro.getKhutrokhoanthu().size()) {
                    dialogChosen.getCbAllItem().setChecked(true);
                }
                break;
            }
            case R.id.edtThang: {
                chosenMonthYear();
                break;
            }
            case R.id.txtChonTrangThai : {
                typeChosenItem = 3;
                List<Item> items = new ArrayList<>();
                int trangThai = Integer.parseInt(txtChonTrangThai.getTag().toString());
                items.add(new Item(false, 1, 0, "Chờ thu tiền"));
                items.add(new Item(false, 2, 1, "Đã thu tiền"));
                items.add(new Item(false, 0, 1, "Đã hủy"));
                for (Item item : items) {
                    if (item.getId() == trangThai) item.setChecked(true);
                }
                DialogChosenItem dialogChosenItem = new DialogChosenItem(getActivity(),items,"Chọn tình trạng","single",0, false);
                dialogChosenItem.setChosenItemCallback(this);
                dialogChosenItem.showDialog();
                break;
            }
        }
    }

    @Override
    public void onReceiveItem(List<Item> item) {
        switch (typeChosenItem) {
            case 0: {
                chosenKhuTro(item.get(0),false);
                break;
            }
            case 1: {
                chosenPhongTro(item.get(0));
                break;
            }
            case 2: {

                String text = "", id = "";
                int stt = 0;
                for (Item i : item) {
                    String name = i.getName();
                    String value = name.substring(0, name.indexOf("-") - 1);
                    if (stt != item.size() - 1) {
                        text += value + ", ";
                        id += i.getId() + ",";
                    } else {
                        text += value;
                        id += i.getId();
                    }
                    stt++;
                }
                if(id.equals(txtCacKhoanThu.getTag().toString())) {
                    Common.checkFormChange = true;
                    checkFormChange = true;
                }
                txtCacKhoanThu.setText(text);
                txtCacKhoanThu.setTag(id);
                totalMoney();
                break;
            }
            case 3: {
                if(Integer.parseInt(txtChonTrangThai.getTag().toString()) != item.get(0).getId()) Common.checkFormChange = true;
                String color = "#f39c12";
                if(item.get(0).getId() == 2) {
                    color = "#27ae60";
                }
                else {
                    if(item.get(0).getId() == 0) {
                        color = "#e74c3c";
                    }
                }
                txtChonTrangThai.setText(item.get(0).getName());
                txtChonTrangThai.setTag(item.get(0).getId());
                txtChonTrangThai.setTextColor(Color.parseColor(color));
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
    public void onStop() {
        super.onStop();
        checkLoadForm = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        Common.checkFormChange = checkFormChange;
    }
}
