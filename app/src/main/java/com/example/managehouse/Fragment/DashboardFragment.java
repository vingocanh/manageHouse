package com.example.managehouse.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.managehouse.Activity.HomeActivity;
import com.example.managehouse.Common.Common;
import com.example.managehouse.Fragment.ThongKe.ThongKeDoanhThuFragment;
import com.example.managehouse.Fragment.ThongKe.ThongKeKhuTroFragment;
import com.example.managehouse.Fragment.ThongKe.ThongKePhongTroFragment;
import com.example.managehouse.Model.Dashboard;
import com.example.managehouse.R;
import com.example.managehouse.Retrofit.API;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DashboardFragment extends Fragment implements View.OnClickListener {

    private TextView txtHoTen, txtDiaChi, txtYear, txtTotalMoney, txtTotalKhuTro, txtTotalNguoiTro, txtPhongTroDaThue, txtPhongTroTrong;
    private LinearLayout llLoading, llYear, llTongThu, llTkKhuTro, llTkPhongTro, llUser;
    private CardView cvDoanhThu, cvKhuTro, cvPhongTro, cvNguoiTro;
    private CircleImageView ivAvatar;
    private ImageView ivChangePassword;

    private FrameLayout flCreateBill;
    private HomeActivity homeActivity;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private API api;
    private MaterialNumberPicker numberPicker;
    private int year, maxYear;

    public DashboardFragment() {
        // Required empty public constructor
    }

    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
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
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        api = Common.getAPI();
        mapping(view);
        setValue();
        setAnimation();
        thongKe();
        return view;
    }

    public void mapping(View view) {
        flCreateBill = view.findViewById(R.id.flCreateBill);
        flCreateBill.setOnClickListener(this);
        txtHoTen = view.findViewById(R.id.txtHoten);
        txtDiaChi = view.findViewById(R.id.txtDiachi);
        txtYear = view.findViewById(R.id.txtYear);
        txtTotalMoney = view.findViewById(R.id.txtTotalMoney);
        txtTotalKhuTro = view.findViewById(R.id.txtTotalKhuTro);
        txtPhongTroDaThue = view.findViewById(R.id.txtPhongTroDaThue);
        txtPhongTroTrong = view.findViewById(R.id.txtPhongTroTrong);
        txtTotalNguoiTro = view.findViewById(R.id.txtTotalNguoiTro);
        llLoading = view.findViewById(R.id.llLoading);
        llYear = view.findViewById(R.id.llYear);
        llYear.setOnClickListener(this);
        llTongThu = view.findViewById(R.id.llTongThu);
        llTongThu.setOnClickListener(this);
        llTkKhuTro = view.findViewById(R.id.llTkKhuTro);
        llTkKhuTro.setOnClickListener(this);
        llTkPhongTro = view.findViewById(R.id.llTkPhongTro);
        llTkPhongTro.setOnClickListener(this);
        llUser = view.findViewById(R.id.llUser);
        llUser.setOnClickListener(this);

        cvDoanhThu = view.findViewById(R.id.cvDoanhThu);
        cvKhuTro = view.findViewById(R.id.cvKhuTro);
        cvNguoiTro = view.findViewById(R.id.cvNguoiTro);
        cvPhongTro = view.findViewById(R.id.cvPhongTro);
        ivAvatar = view.findViewById(R.id.ivAvatar);
        ivChangePassword = view.findViewById(R.id.ivChangePassword);
        ivChangePassword.setOnClickListener(this);
    }

    public void initNumberPicker() {
        numberPicker = new MaterialNumberPicker.Builder(getContext())
                .minValue(1997)
                .maxValue(maxYear)
                .defaultValue(year)
                .backgroundColor(Color.WHITE)
                .separatorColor(Color.TRANSPARENT)
                .textColor(R.color.colorPrimary)
                .textSize(20)
                .enableFocusability(false)
                .wrapSelectorWheel(true)
                .build();
    }

    public void setValue() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user",Context.MODE_PRIVATE);
        txtHoTen.setText(sharedPreferences.getString("name","Không xác định"));
        txtDiaChi.setText(sharedPreferences.getString("address","Không xác định"));
        String avatar = sharedPreferences.getString("avatar","");
        if( !avatar.equals("")) {
            Picasso.get().load(avatar).placeholder(R.drawable.ic_account).error(R.drawable.ic_account).into(ivAvatar);
        }
        else {
            ivAvatar.setImageResource(R.drawable.ic_account);
        }
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        year = maxYear = calendar.get(Calendar.YEAR);
        txtYear.setText(String.valueOf(year));
    }

    public void thongKe() {
        compositeDisposable.add(api.getDashboard(year).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Dashboard>() {
                    @Override
                    public void accept(Dashboard dashboard) throws Exception {
                        txtTotalMoney.setText(Common.formatNumber(dashboard.getTotal_money(),true));
                        txtTotalKhuTro.setText(Common.formatNumber(dashboard.getTotal_khutro(),false));
                        txtPhongTroDaThue.setText(Common.formatNumber(dashboard.getTotal_phongtro_thue(),false));
                        txtPhongTroTrong.setText(Common.formatNumber(dashboard.getTotal_phongtro_trong(),false));
                        txtTotalNguoiTro.setText(Common.formatNumber(dashboard.getTotal_nguoitro(),false));
                        llLoading.setVisibility(View.GONE);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        llLoading.setVisibility(View.GONE);
                        Toasty.error(getContext(), "Gặp sự cố thống kê, thử lại sau.", 300, true).show();
                        throwable.printStackTrace();
                    }
                }));
    }

    public void setAnimation() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.item_animation_top_in);
        animation.reset();
        animation.setDuration(700);
        cvDoanhThu.clearAnimation();
        cvDoanhThu.setAnimation(animation);
        cvKhuTro.clearAnimation();
        cvKhuTro.setAnimation(animation);

        animation = AnimationUtils.loadAnimation(getContext(), R.anim.item_animation_top_in);
        animation.reset();
        animation.setDuration(500);
        cvPhongTro.clearAnimation();
        cvPhongTro.setAnimation(animation);
        cvNguoiTro.clearAnimation();
        cvNguoiTro.setAnimation(animation);

        animation = AnimationUtils.loadAnimation(getContext(), R.anim.item_animation_bottom_in);
        animation.reset();
        animation.setDuration(500);
        flCreateBill.clearAnimation();
        flCreateBill.clearAnimation();
        flCreateBill.setAnimation(animation);

        animation = AnimationUtils.loadAnimation(getContext(), R.anim.item_animation_top_in);
        animation.reset();
        animation.setDuration(500);
        llUser.clearAnimation();
        llUser.setAnimation(animation);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof HomeActivity) homeActivity = (HomeActivity) context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        homeActivity = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        homeActivity.ivAction.setImageResource(R.drawable.ic_notifications_32dp);
        Common.posMenu = 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.flCreateBill : {
                CreateBillFragment createBillFragment = new CreateBillFragment();
                homeActivity.replaceFragment(createBillFragment,true);
                break;
            }
            case R.id.llYear : {
                initNumberPicker();
                new AlertDialog.Builder(getContext())
                        .setTitle("Chọn năm")
                        .setView(numberPicker)
                        .setPositiveButton(R.string.confirm_delete_button_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                year = numberPicker.getValue();
                                txtYear.setText(String.valueOf(year));
                                llLoading.setVisibility(View.VISIBLE);
                                thongKe();
                            }
                        })
                        .setNegativeButton(R.string.confirm_delete_button_no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).create().show();
                break;
            }
            case R.id.llTongThu : {
                homeActivity.replaceFragment(new ThongKeDoanhThuFragment(),true);
                break;
            }
            case R.id.llTkKhuTro : {
                homeActivity.replaceFragment(new ThongKeKhuTroFragment(),true);
                break;
            }
            case R.id.llTkPhongTro : {
                homeActivity.replaceFragment(new ThongKePhongTroFragment(),true);
                break;
            }
            case R.id.llUser : {
                homeActivity.replaceFragment(new UserFragment(),true);
                break;
            }
            case R.id.ivChangePassword : {
                homeActivity.replaceFragment(new ChangePasswordFragment(), true);
                break;
            }
        }
    }
}
