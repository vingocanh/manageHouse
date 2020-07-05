package com.example.managehouse.Fragment;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private TextView txtChonKhuTro, txtChonPhongTro, txtCacKhoanThu, txtTongTien;
    private LinearLayout llNhapSoNuoc, llSoNuoc;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private API api;
    private List<Khutro> khutroList;
    private List<Phongtro> phongtroList;
    private DialogLoading dialogLoading;
    private HomeActivity homeActivity;
    private AwesomeValidation awesomeValidation;
    private DialogChosenItem dialogChosen = null;
    private int typeChosenItem = -1;
    private int sttKhuTro = 0, sttPhongTro = 0, userId = 10;
    private boolean checkDonViNuoc = false; // false - đơn vị theo tháng | true - đơn vị theo khối

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_bill, container, false);
        mapping(view);
        createHoaDon();
        dialogLoading = new DialogLoading(getActivity(), "Đang khởi tạo, đợi chút...");
        dialogLoading.showDialog();
        // init api
        api = Common.getAPI();
        initForm();
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
        edtSoDienMoi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    int soDienCu = Integer.parseInt(edtSoDienCu.getText().toString());
                    int soDienMoi = Integer.parseInt(s.toString());
                    int soDien = soDienMoi - soDienCu;
                    if (soDien > 0) {
                        edtSoDien.setText(String.valueOf(soDien));
                        totalMoney();
                    } else edtSoDien.setText("0");
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
                    int soNuocCu = Integer.parseInt(edtSoNuocCu.getText().toString());
                    int soNuocMoi = Integer.parseInt(s.toString());
                    int soNuoc = soNuocMoi - soNuocCu;
                    if (soNuoc > 0) {
                        edtSoNuoc.setText(String.valueOf(soNuoc));
                        totalMoney();
                    } else edtSoNuoc.setText("0");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtTienPhong.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    String value = edtTienPhong.getText().toString();
                    edtTienPhong.setText(String.valueOf(Common.clearMoney(value)));
                } else {
                    edtTienPhong.setText(Common.formatMoney(Integer.parseInt(edtTienPhong.getText().toString())));
                    edtTienPhong.setTag(Common.clearMoney(edtTienPhong.getText().toString()));
                }
            }
        });
        edtThang.setCursorVisible(false);
        edtThang.setFocusableInTouchMode(false);
        edtThang.setFocusable(false);
        edtThang.setOnClickListener(this);
    }

    public void initForm() {
        edtSoDienCu.setText("0");
        edtSoDienMoi.setText("0");
        edtSoNuocCu.setText("0");
        edtSoNuocMoi.setText("0");
        edtSoDien.setText("0");
        edtSoNuoc.setText("0");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String currentDate = simpleDateFormat.format(date);
        edtTen.setText("HĐ-" + currentDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        edtThang.setText((calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR));
        txtCacKhoanThu.setText("Nước, Điện");
        txtCacKhoanThu.setTag("1,5");
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

    public void createHoaDon() {
        homeActivity.ivAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (awesomeValidation.validate()) {
                    dialogLoading = new DialogLoading(getActivity(), "Đang tạo hóa đơn...");
                    dialogLoading.showDialog();
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
                    String cacKhoanThu = null;
                    if (txtCacKhoanThu.getTag() != null)
                        cacKhoanThu = txtCacKhoanThu.getTag().toString();
                    int tongTien = Integer.parseInt(txtTongTien.getTag().toString());
                    compositeDisposable.add(api.createHoadon(ten, thang, nam, phongtro_id, khutroList.get(sttKhuTro).getId(),soDienCu, soDienMoi, soNuocCu,soNuocMoi, cacKhoanThu, tongTien,ghiChu).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
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
                                            Toasty.success(getContext(), message.getBody()[0], 300, true).show();
                                            Intent intent = new Intent(getContext(), ShowBillActivity.class);
                                            intent.putExtra("hoadon", message.getData());
                                            intent.putExtra("checkDonViNuoc", checkDonViNuoc);
                                            startActivity(intent);
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
            }
        });
    }

    public void getKhutro() {
        String url = "khutro/1";
        compositeDisposable.add(api.getKhutroBill(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Khutro>>() {
                    @Override
                    public void accept(List<Khutro> khutros) throws Exception {
                        khutroList = khutros;
                        if (khutroList.size() > 0) {
                            txtChonKhuTro.setText(khutroList.get(0).getTen());
                            txtChonKhuTro.setTag(khutroList.get(0).getId());
                            checkInputWater(khutroList.get(sttKhuTro));
                            getPhongtro(String.valueOf(khutroList.get(0).getId()), dialogLoading, null);
                        }
                        else {
                            txtChonKhuTro.setText("Không có khu trọ");
                            txtChonKhuTro.setTag("-1");
                            txtChonPhongTro.setTag(-1);
                            txtChonPhongTro.setText("Không có phòng trọ");
                            dialogLoading.hideDialog();
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

    public void getPhongtro(String id, final DialogLoading dl, final String type) {
        compositeDisposable.add(api.getPhongtro(id).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Phongtro>>() {
                    @Override
                    public void accept(List<Phongtro> phongtros) throws Exception {
                        phongtroList = phongtros;
                        if(phongtroList.size() > 0) {
                            txtChonPhongTro.setTag(phongtroList.get(0).getId());
                            txtChonPhongTro.setText(String.valueOf(phongtroList.get(0).getTen()));
                            edtSoDienCu.setText(String.valueOf(phongtroList.get(0).getChotsodien()));
                            edtSoNuocCu.setText(String.valueOf(phongtroList.get(0).getChotsonuoc()));
                            edtTienPhong.setText(Common.formatMoney(phongtroList.get(0).getGia()));
                            edtTienPhong.setTag(phongtroList.get(0).getGia());
                            totalMoney();
                        }
                        else {
                            txtChonPhongTro.setTag(-1);
                            txtChonPhongTro.setText("Không có phòng trọ");
                        }
                        dl.hideDialog();

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toasty.error(getContext(), "Gặp sự cố, thử lại sau.", 300, true).show();
                        dl.hideDialog();
                        throwable.printStackTrace();
                    }
                }));
    }

    public void totalMoney() {
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
        txtTongTien.setText(Common.formatMoney(total));
        txtTongTien.setTag(total);
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
                    dialogChosen = new DialogChosenItem(getActivity(), items, "Chọn khu trọ", "single",sttKhuTro);
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
                if(phongtroList.size() > 0) {
                    typeChosenItem = 1;
                    int id = Integer.parseInt(txtChonPhongTro.getTag().toString());
                    List<Item> items = new ArrayList<>();
                    int stt = 0;
                    for (Phongtro phongtro : phongtroList) {
                        if (phongtro.getId() == id)
                            items.add(new Item(true, phongtro.getId(), stt, phongtro.getTen()));
                        else items.add(new Item(false, phongtro.getId(), stt, phongtro.getTen()));
                        stt++;
                    }
                    dialogChosen = new DialogChosenItem(getActivity(), items, "Chọn phòng trọ", "single", sttKhuTro);
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
                    items.add(new Item(checked, khutrokhoanthu.getKhoanthu_id(), stt, khutrokhoanthu.getKhoanthu().getTen() + " - " + Common.formatMoney(khutrokhoanthu.getGia())));
                    stt++;
                }

                dialogChosen = new DialogChosenItem(getActivity(), items, "Chọn các khoản thu", "multi",0);
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
        }
    }

    @Override
    public void onReceiveItem(List<Item> item) {
        switch (typeChosenItem) {
            case 0: {
                txtChonKhuTro.setText(item.get(0).getName());
                txtChonKhuTro.setTag(item.get(0).getId());
                sttKhuTro = item.get(0).getStt();
                checkInputWater(khutroList.get(sttKhuTro));
                DialogLoading dialogLoading = new DialogLoading(getActivity(), "Đang tải phòng trọ trong khu...");
                dialogLoading.showDialog();
                getPhongtro(String.valueOf(item.get(0).getId()), dialogLoading, "chosen_kt");
                txtCacKhoanThu.setText("Nước, Điện");
                txtCacKhoanThu.setTag("1,5");
                totalMoney();
                break;
            }
            case 1: {
                txtChonPhongTro.setText(item.get(0).getName());
                txtChonPhongTro.setTag(item.get(0).getId());
                sttPhongTro = item.get(0).getStt();
                Phongtro phongtro = phongtroList.get(sttPhongTro);
                edtTienPhong.setText(Common.formatMoney(phongtro.getGia()));
                edtTienPhong.setTag(phongtro.getGia());
                edtSoDienCu.setText(String.valueOf(phongtro.getChotsodien()));
                edtSoNuocCu.setText(String.valueOf(phongtro.getChotsonuoc()));
                totalMoney();
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
                txtCacKhoanThu.setText(text);
                txtCacKhoanThu.setTag(id);
                totalMoney();
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
