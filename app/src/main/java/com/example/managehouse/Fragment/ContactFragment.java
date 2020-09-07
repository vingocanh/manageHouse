package com.example.managehouse.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.managehouse.Common.Common;
import com.example.managehouse.R;

public class ContactFragment extends Fragment implements View.OnClickListener {

    private TextView txtPhone, txtFacebook;

    public ContactFragment() {
        // Required empty public constructor
    }

    public static ContactFragment newInstance(String param1, String param2) {
        ContactFragment fragment = new ContactFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        mapping(view);
        return view;
    }

    public void mapping(View view) {
        txtPhone = view.findViewById(R.id.txtPhone);
        txtPhone.setOnClickListener(this);
        txtFacebook = view.findViewById(R.id.txtFacebook);
        txtFacebook.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtPhone : {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:0348714195"));
                startActivity(callIntent);
                break;
            }
            case R.id.txtFacebook : {
                break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Common.posMenu = 6;
    }
}