package com.example.managehouse.Fragment.ThongKe;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.example.managehouse.Activity.HomeActivity;
import com.example.managehouse.Callback.ChosenItemCallback;
import com.example.managehouse.Common.Common;
import com.example.managehouse.Fragment.KhuTro.FormFragment;
import com.example.managehouse.Model.Hoadon;
import com.example.managehouse.Model.Item;
import com.example.managehouse.Model.Khutro;
import com.example.managehouse.Model.Khutrokhoanthu;
import com.example.managehouse.R;
import com.example.managehouse.Retrofit.API;
import com.example.managehouse.Service.DialogChosenItem;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialog;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ThongKeDoanhThuFragment extends Fragment implements View.OnClickListener, ChosenItemCallback {

    private TextView txtDateFirst, txtDateLast, txtInfo, txtTongTien, txtKhuTro;
    private AnyChartView anyChartView;
    private LottieAnimationView lavLoading;
    private LinearLayout llKhuTro;

    private int monthFirst, yearFirst, monthLast, yearLast;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private API api;
    private HomeActivity homeActivity;
    private Cartesian cartesian;
    private List<Khutro> khutroList = new ArrayList<>();
    private  int sttKhuTro = 0;

    public ThongKeDoanhThuFragment() {
        // Required empty public constructor
    }

    public static ThongKeDoanhThuFragment newInstance(String param1, String param2) {
        ThongKeDoanhThuFragment fragment = new ThongKeDoanhThuFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_thong_ke_doanh_thu, container, false);
        api = Common.getAPI();
        mapping(view);
        initValue();
        thongKe();
        getKhutro();
        initChart(null);
        return view;
    }

    public void mapping(View view) {
        homeActivity.ivAction.setVisibility(View.GONE);
        anyChartView = view.findViewById(R.id.any_chart_view);
        txtDateFirst = view.findViewById(R.id.txtDateFirst);
        txtDateFirst.setOnClickListener(this);
        txtDateLast = view.findViewById(R.id.txtDateLast);
        txtDateLast.setOnClickListener(this);
        txtInfo = view.findViewById(R.id.txtInfo);
        txtTongTien = view.findViewById(R.id.txtTongTien);
        lavLoading = view.findViewById(R.id.lavLoading);
        llKhuTro = view.findViewById(R.id.llKhuTro);
        llKhuTro.setOnClickListener(this);
        txtKhuTro = view.findViewById(R.id.txtKhuTro);
    }

    public void initValue() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String dateLast = (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR);
        monthLast = (calendar.get(Calendar.MONTH) + 1);
        yearLast = calendar.get(Calendar.YEAR);
        calendar.add(Calendar.MONTH, -12);
        String dateFirst = (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR);
        monthFirst = (calendar.get(Calendar.MONTH) + 1);
        yearFirst = calendar.get(Calendar.YEAR);
        txtDateLast.setText(dateLast);
        txtDateFirst.setText(dateFirst);
    }

    public void initChart(List<DataEntry> seriesData) {
        cartesian = AnyChart.line();
        cartesian.animation(true);
        cartesian.padding(10d, 20d, 20d, 0d);
        cartesian.crosshair().enabled(true);
        cartesian.crosshair()
                .yLabel(true)
                // TODO ystroke
                .yStroke((Stroke) null, null, null, (String) null, (String) null);
        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

        //cartesian.title("Trend of Sales of the Most Popular Products of ACME Corp.");

        //cartesian.yAxis(0).title("Number of Bottles Sold (thousands)");
        //cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);
        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");

        Line series1 = cartesian.line(series1Mapping);
        series1.name("Doanh thu");
        series1.hovered().markers().enabled(true);
        series1.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(5d);
        series1.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        cartesian.legend().enabled(true);
        cartesian.legend().fontSize(16d);
        cartesian.legend().padding(0d, 0d, 10d, 0d);

        anyChartView.setChart(cartesian);

    }

    public void chosenMonthYear(TextView textView, long minDate, long maxDate) {
        String thangCurrent = textView.getText().toString();
        int monthCurrent = Integer.parseInt(thangCurrent.substring(0, thangCurrent.indexOf("/"))) - 1;
        int yearCurrent = Integer.parseInt(thangCurrent.substring(thangCurrent.indexOf("/") + 1));
        MonthYearPickerDialogFragment monthYearPickerDialogFragment = MonthYearPickerDialogFragment.getInstance(monthCurrent, yearCurrent, minDate, maxDate);
        monthYearPickerDialogFragment.setOnDateSetListener(new MonthYearPickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(int year, int monthOfYear) {
                if(textView == txtDateFirst) {
                    monthFirst = monthOfYear + 1;
                    yearFirst = year;
                }
                else {
                    monthLast = monthOfYear + 1;
                    yearLast = year;
                }
                String value = (monthOfYear + 1) + "/" + year;
                textView.setText(value);
                lavLoading.setVisibility(View.VISIBLE);
                thongKe();
            }
        });
        monthYearPickerDialogFragment.show(getFragmentManager(), null);
    }

    public void getKhutro() {
        String url = "khutro/1";
        compositeDisposable.add(api.getKhutroChosen(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Khutro>>() {
                    @Override
                    public void accept(List<Khutro> khutros) throws Exception {
                        khutroList = khutros;
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                }));
    }

    public void thongKe() {
        lavLoading.setVisibility(View.VISIBLE);
        compositeDisposable.add(api.thongKeChiTietDoanhThu(Integer.parseInt(txtKhuTro.getTag().toString()),monthFirst,yearFirst,monthLast,yearLast).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Hoadon>>() {
                    @Override
                    public void accept(List<Hoadon> hoadons) throws Exception {
                        List<DataEntry> seriesData  = new ArrayList<>();
                        if(hoadons.size() > 0) {
                            txtInfo.setText("Tổng doanh thu từ " + txtDateFirst.getText().toString() + " đến " + txtDateLast.getText().toString() + " là: ");
                            txtTongTien.setText(Common.formatNumber(hoadons.get(0).getTotal(),true));

                            for (Hoadon hoadon : hoadons) {
                                seriesData.add(new CustomDataEntry(hoadon.getThang() + "/" + hoadon.getNam(), hoadon.getTongtien()));
                            }

                        }
                        else {
                            txtInfo.setText("Tổng doanh thu từ " + txtDateFirst.getText().toString() + " đến " + txtDateLast.getText().toString() + " là: ");
                            txtTongTien.setText(Common.formatNumber(0,true));
                            seriesData.add(new CustomDataEntry("",0));
                        }
                        cartesian.data(seriesData);
                        lavLoading.setVisibility(View.GONE);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toasty.error(getContext(), "Gặp sự cố thống kê, thử lại sau.", 300, true).show();
                        lavLoading.setVisibility(View.GONE);
                        throwable.printStackTrace();
                    }
                }));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.txtDateFirst: {
                String dateLast = txtDateLast.getText().toString();
                Calendar calendar = Calendar.getInstance();
                int month = Integer.parseInt(dateLast.substring(0, dateLast.indexOf("/"))) - 1;
                int year = Integer.parseInt(dateLast.substring(dateLast.indexOf("/") + 1));
                calendar.clear();
                calendar.set(year, month, 1);
                long maxDate = calendar.getTimeInMillis();
                calendar.clear();
                calendar.set(1997, 0, 1);
                long minDate = calendar.getTimeInMillis();
                chosenMonthYear(txtDateFirst, minDate, maxDate);
                break;
            }
            case R.id.txtDateLast: {
                Date date = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                yearLast = calendar.get(Calendar.YEAR);
                monthLast = calendar.get(Calendar.MONTH) + 1;
                calendar.clear();
                calendar.set(yearLast, monthLast - 1, 1);
                long maxDate = calendar.getTimeInMillis();
                calendar.clear();
                calendar.set(1997, 0, 1);
                long minDate = calendar.getTimeInMillis();
                chosenMonthYear(txtDateLast, minDate, maxDate);
                break;
            }
            case R.id.llKhuTro : {
                if(khutroList.size() > 0) {
                    int id = Integer.parseInt(txtKhuTro.getTag().toString());
                    List<Item> items = new ArrayList<>();
                    int stt = 1;
                    if(sttKhuTro == 0) items.add(new Item(true, -1, 0, "Tất cả khu trọ"));
                    else items.add(new Item(false, -1, 0, "Tất cả khu trọ"));
                    for (Khutro khutro : khutroList) {
                        if (khutro.getId() == id || stt == sttKhuTro) items.add(new Item(true, khutro.getId(), stt, khutro.getTen()));
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
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        homeActivity.ivAction.setVisibility(View.VISIBLE);
        compositeDisposable.clear();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof HomeActivity) homeActivity = (HomeActivity) context;
    }

    @Override
    public void onReceiveItem(List<Item> item) {
        txtKhuTro.setTag(item.get(0).getId());
        txtKhuTro.setText(item.get(0).getName());
        sttKhuTro = item.get(0).getStt();
        thongKe();
    }

    private class CustomDataEntry extends ValueDataEntry {

        CustomDataEntry(String x, Number value) {
            super(x, value);
        }

    }

}