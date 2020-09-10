package com.example.managehouse.Activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.managehouse.Common.Common;
import com.example.managehouse.Model.Hoadon;
import com.example.managehouse.Model.Khutrokhoanthu;
import com.example.managehouse.R;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class ShowBillActivity extends AppCompatActivity {

    private TextView txtNhaTro, txtDiaChi, txtDt, txtPhong, txtTenHoaDon, txtNgay, txtKhu, txtTongTien, txtGhiChu;
    private TableLayout tlBill;
    private LinearLayout llGhiChu;
    private FrameLayout flShareBill;

    private Hoadon hoadon = null;
    private boolean checkDonViNuoc = false; // false - đơn vị theo tháng | true - đơn vị theo khối

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_bill);
        mapping();
        setInfoBill();
        setTable();
        shareBill();
    }

    public void mapping() {
        txtNhaTro = findViewById(R.id.txtNhaTro);
        txtDiaChi = findViewById(R.id.txtDiaChi);
        txtDt = findViewById(R.id.txtDt);
        txtPhong = findViewById(R.id.txtPhong);
        txtTenHoaDon = findViewById(R.id.txtTenHoaDon);
        txtNgay = findViewById(R.id.txtNgay);
        txtKhu = findViewById(R.id.txtKhu);
        tlBill = findViewById(R.id.tlBill);
        txtTongTien = findViewById(R.id.txtTongTien);
        txtGhiChu = findViewById(R.id.txtGhiChu);
        llGhiChu = findViewById(R.id.llGhiChu);
        flShareBill = findViewById(R.id.flShareBill);
    }


    public void setInfoBill() {
        //get bill
        Intent intent = getIntent();
        String jsonHoaDon = intent.getStringExtra("hoadon");
        checkDonViNuoc = intent.getBooleanExtra("checkDonViNuoc", false);
        Gson gson = new Gson();
        hoadon = gson.fromJson(jsonHoaDon, Hoadon.class);

        //set info header
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        txtNhaTro.setText(sharedPreferences.getString("name", "Không xác định"));
        txtDiaChi.setText(sharedPreferences.getString("address", "Không xác định"));
        txtDt.setText(sharedPreferences.getString("username", "Không xác định"));

        // set info bill
        txtTenHoaDon.setText(hoadon.getTen());
        txtPhong.setText(hoadon.getPhongtro().getTen());
        txtKhu.setText(hoadon.getPhongtro().getKhutro().getTen());
        txtNgay.setText(hoadon.getCreated_at());

        // set tong tien
        txtTongTien.setText(Common.formatNumber(hoadon.getTongtien(),true));

        //check và set ghi chú
        if(hoadon.getGhichu() == null || hoadon.getGhichu().equals("")) {
            llGhiChu.setVisibility(View.GONE);
        }
        else {
            txtGhiChu.setText(hoadon.getGhichu());
        }
    }

    public void setTable() {
        TableRow tableRow = createRow();
        TextView txtTienPhong = createTextView();
        txtTienPhong.setBackgroundResource(R.drawable.border_left_top_table);
        txtTienPhong.setText("Tiền phòng");
        TextView txtSoLuong = createTextView();
        txtSoLuong.setBackgroundResource(R.drawable.border_left_top_right_table);
        txtSoLuong.setText("-");
        TextView txtDonGia = createTextView();
        txtDonGia.setBackgroundResource(R.drawable.border_top_right_table);
        txtDonGia.setText(Common.formatNumber(hoadon.getPhongtro().getGia(),false));
        TextView txtThanhTien = createTextView();
        txtThanhTien.setBackgroundResource(R.drawable.border_top_right_table);
        txtThanhTien.setText(Common.formatNumber(hoadon.getPhongtro().getGia(),false));
        tableRow.addView(txtTienPhong);
        tableRow.addView(txtSoLuong);
        tableRow.addView(txtDonGia);
        tableRow.addView(txtThanhTien);
        tlBill.addView(tableRow);
        TextView tvTen = createTextView();
        TextView tvSl = createTextView();
        TextView tvDonGia = createTextView();
        TextView tvThanhTien = createTextView();
        for (Khutrokhoanthu khutrokhoanthu : hoadon.getKhoanthu()) {
            TableRow row = createRow();
            tvTen = createTextView();
            tvTen.setBackgroundResource(R.drawable.border_left_top_table);
            tvSl = createTextView();
            tvSl.setBackgroundResource(R.drawable.border_left_top_right_table);
            tvDonGia = createTextView();
            tvDonGia.setBackgroundResource(R.drawable.border_top_right_table);
            tvThanhTien = createTextView();
            tvThanhTien.setBackgroundResource(R.drawable.border_top_right_table);
            int thanhTien = 0;
            tvTen.setText(khutrokhoanthu.getKhoanthu().getTen());
            row.addView(tvTen);
            if(khutrokhoanthu.getDonvitinh().getName().equals("Người")) {
                int soNguoi = hoadon.getPhongtro().getNguoitro().size();
                thanhTien = khutrokhoanthu.getGia() * soNguoi;
                tvSl.setText(soNguoi + " người");
            }
            else {
                if (khutrokhoanthu.getKhoanthu().getTen().equals("Điện")) {
                    int soDien = hoadon.getSodienmoi() - hoadon.getSodiencu();
                    if (soDien < 0) soDien = 0;
                    tvSl.setText(soDien + " số");
                    thanhTien = soDien * khutrokhoanthu.getGia();
                } else {
                    if (khutrokhoanthu.getKhoanthu().getTen().equals("Nước")) {
                        if (checkDonViNuoc) {
                            int soNuoc = hoadon.getSonuocmoi() - hoadon.getSonuoccu();
                            if (soNuoc < 0) soNuoc = 0;
                            tvSl.setText(String.valueOf(soNuoc));
                            thanhTien = soNuoc * khutrokhoanthu.getGia();
                        } else {
                            tvSl.setText("-");
                            thanhTien = khutrokhoanthu.getGia();
                        }
                    } else {
                        tvSl.setText("-");
                        thanhTien = khutrokhoanthu.getGia();
                    }
                }
            }

            row.addView(tvSl);
            tvThanhTien.setText(Common.formatNumber(thanhTien,false));
            tvDonGia.setText(Common.formatNumber(khutrokhoanthu.getGia(),false));
            row.addView(tvDonGia);
            row.addView(tvThanhTien);
            tlBill.addView(row);
        }
    }

    public void shareBill() {
        flShareBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flShareBill.setVisibility(View.GONE);
                View rootView = getWindow().getDecorView().findViewById(R.id.llThongTinNhaTro);
                Bitmap bitmap = getScreenShot(rootView);
                storeScreen(bitmap,hoadon.getPhongtro().getTen() + "_" + hoadon.getPhongtro().getKhutro().getTen() +".png");
            }
        });
    }

    public Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public void storeScreen(Bitmap bitmap, String fileName) {
        String dirPath = Environment.getExternalStorageDirectory()+"/managehouse/Pictures";
        File dir = new File(dirPath);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dirPath,fileName);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            shareImage(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shareImage(File file) {
        flShareBill.setVisibility(View.VISIBLE);
        Uri uri = null;
        if(Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(getApplicationContext(),getApplicationContext().getPackageName() + ".provider",file);
        }
        else {
            uri = Uri.fromFile(file);
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(Intent.EXTRA_SUBJECT,"def");
        intent.putExtra(Intent.EXTRA_TEXT,"abc");
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        try {
            startActivity(Intent.createChooser(intent, "Gửi hóa đơn"));
        } catch (ActivityNotFoundException e) {
            Toasty.error(getApplicationContext(), "Gặp sự cố, thử lại sau.", 300, true).show();
        }

    }

    public TextView createTextView() {
        TextView tv = new TextView(this);
        tv.setTextColor(getResources().getColor(R.color.colorText));
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(14);
        tv.setPadding(0, 20, 0, 20);
        return tv;
    }

    public TableRow createRow() {
        TableRow tableRow = new TableRow(this);
        return tableRow;
    }
}
