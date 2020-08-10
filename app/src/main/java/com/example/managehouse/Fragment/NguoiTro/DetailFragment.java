package com.example.managehouse.Fragment.NguoiTro;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.managehouse.Activity.HomeActivity;
import com.example.managehouse.Common.Common;
import com.example.managehouse.Model.Nguoitro;
import com.example.managehouse.Model.Phongtro;
import com.example.managehouse.R;
import com.squareup.picasso.Picasso;

public class DetailFragment extends Fragment {

    private TextView txtHoTen, txtPhongTro, txtNgaySinh, txtSoCMND, txtQueQuan, txtNgheNghiep, txtTrinhDo, txtNoiamViec, txtTinhTrang, txtGhiChu;
    private TextView txtTenCha, txtNamSinhCha, txtNgheNghiepCha, txtCongTacCha, txtChoOCha, txtTenMe, txtNamSinhMe, txtNgheNghiepMe, txtCongTacMe, txtChoOMe;
    private ImageView ivAvatar;

    private HomeActivity homeActivity;
    private Nguoitro nguoitro = null;

    public static DetailFragment newInstance(String param1, String param2) {
        DetailFragment fragment = new DetailFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            nguoitro = (Nguoitro) getArguments().getSerializable("nguoitro");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_nguoitro, container, false);
        mapping(view);
        setValue();
        return view;
    }

    public void mapping(View view) {
        txtHoTen = view.findViewById(R.id.txtHoTen);
        txtPhongTro = view.findViewById(R.id.txtPhongTro);
        txtNgaySinh = view.findViewById(R.id.txtNgaySinh);
        txtSoCMND = view.findViewById(R.id.txtSoCMND);
        txtQueQuan = view.findViewById(R.id.txtQueQuan);
        txtNgheNghiep = view.findViewById(R.id.txtNgheNghiep);
        txtTrinhDo = view.findViewById(R.id.txtTrinhDo);
        txtNoiamViec = view.findViewById(R.id.txtNoiamViec);
        txtTinhTrang = view.findViewById(R.id.txtTinhTrang);
        txtGhiChu = view.findViewById(R.id.txtGhiChu);
        txtTenCha = view.findViewById(R.id.txtTenCha);
        txtNamSinhCha = view.findViewById(R.id.txtNamSinhCha);
        txtNgheNghiepCha = view.findViewById(R.id.txtNgheNghiepCha);
        txtCongTacCha = view.findViewById(R.id.txtCongTacCha);
        txtChoOCha = view.findViewById(R.id.txtChoOCha);
        txtTenMe = view.findViewById(R.id.txtTenMe);
        txtNamSinhMe = view.findViewById(R.id.txtNamSinhMe);
        txtNgheNghiepMe = view.findViewById(R.id.txtNgheNghiepMe);
        txtCongTacMe = view.findViewById(R.id.txtCongTacMe);
        txtChoOMe = view.findViewById(R.id.txtChoOMe);
        ivAvatar = view.findViewById(R.id.ivAvatar);
        homeActivity.ivAction.setVisibility(View.GONE);
    }

    public void setValue() {
        if(nguoitro.getAvatar() != null) {
            Picasso.get().load(nguoitro.getAvatar()).placeholder(R.drawable.ic_person_outline_32dp).error(R.drawable.ic_person_outline_32dp).into(ivAvatar);
        }
        else {
            ivAvatar.setImageResource(R.drawable.ic_person_outline_32dp);
        }
        txtHoTen.setText(nguoitro.getHoten());
        txtPhongTro.setText(nguoitro.getPhongtro().getTen() + " | " + nguoitro.getKhutro().getTen());
        txtNgaySinh.setText(nguoitro.getNgaysinh2());
        txtSoCMND.setText(nguoitro.getSocmnd());
        txtQueQuan.setText(nguoitro.getQuequan());
        txtNgheNghiep.setText(nguoitro.getNghenghiep());
        txtTrinhDo.setText(nguoitro.getTrinhdo().getTen());
        txtNoiamViec.setText(nguoitro.getNoilamviec());
        txtGhiChu.setText(nguoitro.getGhichu());
        String trangThai = "Đang trọ";
        String color = "#27ae60";
        if(nguoitro.getStatus() == 0) {
            trangThai = "Đã thôi trọ";
            color = "#e74c3c";
        }
        txtTinhTrang.setText(trangThai);
        txtTinhTrang.setTextColor(Color.parseColor(color));
        txtTenCha.setText(nguoitro.getNguoitrogiadinh().getBo_hoten());
        txtNamSinhCha.setText(nguoitro.getNguoitrogiadinh().getBo_namsinh());
        txtNgheNghiepCha.setText(nguoitro.getNguoitrogiadinh().getBo_nghenghiep());
        txtCongTacCha.setText(nguoitro.getNguoitrogiadinh().getBo_noicongtac());
        txtChoOCha.setText(nguoitro.getNguoitrogiadinh().getBo_choohiennay());
        txtTenMe.setText(nguoitro.getNguoitrogiadinh().getMe_hoten());
        txtNamSinhMe.setText(nguoitro.getNguoitrogiadinh().getMe_namsinh());
        txtNgheNghiepMe.setText(nguoitro.getNguoitrogiadinh().getMe_nghenghiep());
        txtCongTacMe.setText(nguoitro.getNguoitrogiadinh().getMe_noicongtac());
        txtTenMe.setText(nguoitro.getNguoitrogiadinh().getMe_hoten());
        txtChoOMe.setText(nguoitro.getNguoitrogiadinh().getMe_choohiennay());
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
        homeActivity.ivAction.setVisibility(View.VISIBLE);
    }
}