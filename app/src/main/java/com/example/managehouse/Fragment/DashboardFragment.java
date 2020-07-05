package com.example.managehouse.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.managehouse.Activity.HomeActivity;
import com.example.managehouse.Activity.ShowBillActivity;
import com.example.managehouse.R;

public class DashboardFragment extends Fragment implements View.OnClickListener {

    private TextView txtHoTen, txtDiaChi;

    private FrameLayout flCreateBill;
    private HomeActivity homeActivity = null;

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
        mapping(view);
        setValue();
        return view;
    }

    public void mapping(View view) {
        flCreateBill = view.findViewById(R.id.flCreateBill);
        flCreateBill.setOnClickListener(this);
        txtHoTen = view.findViewById(R.id.txtHoten);
        txtDiaChi = view.findViewById(R.id.txtDiachi);
    }

    public void setValue() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user",Context.MODE_PRIVATE);
        txtHoTen.setText(sharedPreferences.getString("name","Không xác định"));
        txtDiaChi.setText(sharedPreferences.getString("address","Không xác định"));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof HomeActivity) homeActivity = (HomeActivity) context;
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.flCreateBill : {
                CreateBillFragment createBillFragment = new CreateBillFragment();
                homeActivity.replaceFragment(createBillFragment,true);
                break;
            }
        }
    }
}
