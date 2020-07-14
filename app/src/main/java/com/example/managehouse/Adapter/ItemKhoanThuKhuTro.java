package com.example.managehouse.Adapter;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.managehouse.Callback.ChosenItemCallback;
import com.example.managehouse.Callback.ChosenKhoanThuKhuTroCallback;
import com.example.managehouse.Common.Common;
import com.example.managehouse.Model.Donvitinh;
import com.example.managehouse.Model.Item;
import com.example.managehouse.Model.Khoanthu;
import com.example.managehouse.R;
import com.example.managehouse.Service.DialogChosenItem;

import java.util.ArrayList;
import java.util.List;

public class ItemKhoanThuKhuTro extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ChosenItemCallback {

    private Activity activity;
    private List<Khoanthu> khoanthus;
    private List<Donvitinh> donvitinhs = new ArrayList<>();
    private ChosenKhoanThuKhuTroCallback chosenKhoanThuKhuTroCallback;
    private int[] giaKhoanThu, donViTinhKhoanThu, idKhoanThu;
    private String[] tenDonViTinh = null;
    private int posChecked = 0;
    private TextView txtDonViTinh;

    public ItemKhoanThuKhuTro(Activity activity, List<Khoanthu> khoanthus) {
        this.activity = activity;
        this.khoanthus = khoanthus;
        giaKhoanThu = donViTinhKhoanThu =idKhoanThu = new int[khoanthus.size()];
    }

    public List<Donvitinh> getDonvitinhs() {
        return donvitinhs;
    }

    public void setDonvitinhs(List<Donvitinh> donvitinhs) {
        this.donvitinhs = donvitinhs;
    }

    public int[] getGiaKhoanThu() {
        return giaKhoanThu;
    }

    public void setGiaKhoanThu(int[] giaKhoanThu) {
        this.giaKhoanThu = giaKhoanThu;
    }

    public int[] getDonViTinhKhoanThu() {
        return donViTinhKhoanThu;
    }

    public int[] getIdKhoanThu() {
        return idKhoanThu;
    }

    public void setIdKhoanThu(int[] idKhoanThu) {
        this.idKhoanThu = idKhoanThu;
    }

    public String[] getTenDonViTinh() {
        return tenDonViTinh;
    }

    public void setTenDonViTinh(String[] tenDonViTinh) {
        this.tenDonViTinh = tenDonViTinh;
    }

    public void setDonViTinhKhoanThu(int[] donViTinhKhoanThu) {
        this.donViTinhKhoanThu = donViTinhKhoanThu;
    }

    public void setChosenKhoanThuKhuTroCallback(ChosenKhoanThuKhuTroCallback chosenKhoanThuKhuTroCallback) {
        this.chosenKhoanThuKhuTroCallback = chosenKhoanThuKhuTroCallback;
    }

    public void callReceiveKhoanThuKhuTro() {
        chosenKhoanThuKhuTroCallback.onReceiveKhoanThu();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_khoanthu_khutro, parent, false);
            return new MyItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_loading, parent, false);
            return new MyLoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyItemViewHolder) {
            ((MyItemViewHolder) holder).cbItem.setChecked(khoanthus.get(position).isChecked());
            ((MyItemViewHolder) holder).txtItem.setText(khoanthus.get(position).getTen());
            if(giaKhoanThu[position] != 0) {
                ((MyItemViewHolder) holder).edtPrice.setText(Common.formatMoney(giaKhoanThu[position]));
                ((MyItemViewHolder) holder).edtPrice.setTag(giaKhoanThu[position]);
            }
            if(tenDonViTinh != null && donViTinhKhoanThu[position] != 0) {
                ((MyItemViewHolder) holder).txtDonViTinh.setText("Đơn vị: " + tenDonViTinh[position]);
                ((MyItemViewHolder) holder).txtDonViTinh.setTag(donViTinhKhoanThu[position]);
            }
            if(khoanthus.get(position).isChecked()) {
                ((MyItemViewHolder) holder).llNhap.setVisibility(View.VISIBLE);
            }
            ((MyItemViewHolder) holder).txtItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(khoanthus.get(position).isChecked()) {
                        khoanthus.get(position).setChecked(false);
                        idKhoanThu[position] = 0;
                        ((MyItemViewHolder) holder).llNhap.setVisibility(View.GONE);
                    }
                    else {
                        khoanthus.get(position).setChecked(true);
                        idKhoanThu[position] = khoanthus.get(position).getId();
                        ((MyItemViewHolder) holder).llNhap.setVisibility(View.VISIBLE);
                    }
                    callReceiveKhoanThuKhuTro();
                }
            });
            ((MyItemViewHolder) holder).cbItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(khoanthus.get(position).isChecked()) {
                        khoanthus.get(position).setChecked(false);
                        idKhoanThu[position] = 0;
                        ((MyItemViewHolder) holder).llNhap.setVisibility(View.GONE);
                    }
                    else {
                        khoanthus.get(position).setChecked(true);
                        idKhoanThu[position] = khoanthus.get(position).getId();
                        ((MyItemViewHolder) holder).llNhap.setVisibility(View.VISIBLE);
                    }
                    callReceiveKhoanThuKhuTro();
                }
            });
            ((MyItemViewHolder) holder).edtPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    String value = ((MyItemViewHolder) holder).edtPrice.getText().toString();
                    if(!value.equals("")) {
                        if (hasFocus) {

                            ((MyItemViewHolder) holder).edtPrice.setText(String.valueOf(Common.clearMoney(value)));
                        } else {
                            ((MyItemViewHolder) holder).edtPrice.setText(Common.formatMoney(Integer.parseInt(((MyItemViewHolder) holder).edtPrice.getText().toString())));
                            ((MyItemViewHolder) holder).edtPrice.setTag(Common.clearMoney(((MyItemViewHolder) holder).edtPrice.getText().toString()));
                        }
                    }
                }
            });
            ((MyItemViewHolder) holder).txtDonViTinh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    posChecked = position;
                    txtDonViTinh = ((MyItemViewHolder) holder).txtDonViTinh;
                    List<Item> items = new ArrayList<>();
                    int id = 0, stt = 0;
                    if(((MyItemViewHolder) holder).txtDonViTinh.getTag() != null) id = Integer.parseInt(((MyItemViewHolder) holder).txtDonViTinh.getTag().toString());
                    for (Donvitinh donvitinh : donvitinhs) {
                        if (donvitinh.getId() == id) items.add(new Item(true, donvitinh.getId(), stt, donvitinh.getName()));
                        else {
                            items.add(new Item(false, donvitinh.getId(), stt, donvitinh.getName()));
                        }
                        stt++;
                    }
                    DialogChosenItem dialogChosenItem = new DialogChosenItem(activity, items, "Chọn đơn vị tính", "single",0, true);
                    dialogChosenItem.setChosenItemCallback(ItemKhoanThuKhuTro.this);
                    dialogChosenItem.showDialog();
                }
            });
            ((MyItemViewHolder) holder).edtPrice.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!s.toString().equals("")) {
                        if(s.toString().indexOf(".") == -1)  giaKhoanThu[position] = Integer.parseInt(s.toString());
                    }
                    else {
                        giaKhoanThu[position] = 0;
                    }
                }
            });
        } else {
            if (holder instanceof MyLoadingViewHolder) {

            }
        }
    }

    @Override
    public int getItemCount() {
        return khoanthus.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (khoanthus.get(position) == null) return 0;
        return 1;
    }

    @Override
    public void onReceiveItem(List<Item> item) {
        txtDonViTinh.setText("Đơn vị: " + item.get(0).getName());
        txtDonViTinh.setTag(item.get(0).getId());
        donViTinhKhoanThu[posChecked] = item.get(0).getId();
    }

    public class MyItemViewHolder extends RecyclerView.ViewHolder{

        private CheckBox cbItem;
        private TextView txtItem, txtDonViTinh;
        private EditText edtPrice;
        private LinearLayout llNhap;

        public MyItemViewHolder(@NonNull View itemView) {
            super(itemView);
            llNhap = itemView.findViewById(R.id.llNhap);
            cbItem = itemView.findViewById(R.id.cbItem);
            txtItem = itemView.findViewById(R.id.txtItem);
            txtDonViTinh = itemView.findViewById(R.id.txtDonViTinh);
            edtPrice = itemView.findViewById(R.id.edtPrice);
        }
    }

    public class MyLoadingViewHolder extends RecyclerView.ViewHolder {

        public MyLoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
