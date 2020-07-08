package com.example.managehouse.Service;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.managehouse.Activity.HomeActivity;
import com.example.managehouse.Adapter.ItemChosenAdapter;
import com.example.managehouse.Callback.ChosenItemCallback;
import com.example.managehouse.Fragment.KhuTro.FormFragment;
import com.example.managehouse.Model.Item;
import com.example.managehouse.R;

import java.util.ArrayList;
import java.util.List;

public class DialogChosenItem implements View.OnClickListener, ChosenItemCallback {

    private TextView txtHuy, txtDongY;
    private CheckBox cbAllItem;
    private RecyclerView rvChosenItem;
    private EditText edtSearch;

    private Activity activity;
    private int scrollPos = 0;
    public  List<Item> itemList, copyItems = new ArrayList<>();
    private String title;
    private AlertDialog alertDialog;
    private ItemChosenAdapter adapter;
    private ChosenItemCallback chosenItemCallback;
    private List<Item> itemChecked = new ArrayList<>();
    private String type;

    public DialogChosenItem(Activity activity, List<Item> itemList, String title, String type, int scrollPos) {
        this.activity = activity;
        this.itemList = itemList;
        this.title = title;
        this.type = type;
        this.scrollPos = scrollPos;
        for (Item item : itemList) {
            if(item.isChecked()) itemChecked.add(item);
        }
    }

    public CheckBox getCbAllItem() {
        return cbAllItem;
    }

    public void setCbAllItem(CheckBox cbAllItem) {
        this.cbAllItem = cbAllItem;
    }

    public void setChosenItemCallback(ChosenItemCallback chosenItemCallback) {
        this.chosenItemCallback = chosenItemCallback;
    }

    public void callChosenItemCallback(List<Item> item) {
        chosenItemCallback.onReceiveItem(item);
    }

    public void showDialog() {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_chosen_item, null);
        mapping(view);
        checkAllItem();
        searchItem();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void mapping(View view) {
        TextView txtTitle = view.findViewById(R.id.txtTitle);
        txtHuy = view.findViewById(R.id.txtHuy);
        txtHuy.setOnClickListener(this);
        txtDongY = view.findViewById(R.id.txtDongY);
        txtDongY.setOnClickListener(this);
        txtTitle.setText(title);
        rvChosenItem = view.findViewById(R.id.rcvChosenItem);
        adapter = new ItemChosenAdapter(activity.getApplicationContext(),itemList, type);
        adapter.setChosenItemCallback(this);
        rvChosenItem.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity.getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvChosenItem.setLayoutManager(layoutManager);
        rvChosenItem.setAdapter(adapter);
        layoutManager.scrollToPosition(scrollPos);
        cbAllItem = view.findViewById(R.id.cbAllItem);
        if(type.equals("single")) {
            cbAllItem.setVisibility(View.GONE);
        }
        else {
            cbAllItem.setVisibility(View.VISIBLE);
        }

        // set height recleview
        rvChosenItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        if(rvChosenItem.getMeasuredHeight() > 450) {
            ViewGroup.LayoutParams params = rvChosenItem.getLayoutParams();
            params.height = 450;
            rvChosenItem.setLayoutParams(params);
        }
        ImageView ivSearch = view.findViewById(R.id.ivSearch);
        ImageView ivAdd = view.findViewById(R.id.ivAdd);
        if(itemList.size() < 10) {
            ivSearch.setVisibility(View.GONE);
        }
        final LinearLayout llSearch = view.findViewById(R.id.llSearch);
        final LinearLayout llBtnSearch = view.findViewById(R.id.llBtnSearch);
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(llSearch.getVisibility() == View.VISIBLE) {
                    llSearch.setVisibility(View.GONE);
                    llBtnSearch.setVisibility(View.VISIBLE);
                }
                else {
                    llSearch.setVisibility(View.VISIBLE);
                    llBtnSearch.setVisibility(View.GONE);
                }
            }
        });
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) activity;
                homeActivity.replaceFragment(new FormFragment(), true);
                hideDialog();
            }
        });
        edtSearch = view.findViewById(R.id.edtTimKiem);
    }

    public void checkAllItem() {
        cbAllItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.checkAllItem(cbAllItem.isChecked());
            }
        });
    }

    public void searchItem() {
        copyItems.addAll(itemList);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                itemList.clear();
                itemList.addAll(copyItems);
                List<Item> newItems = new ArrayList<>();
                for (Item item : itemList) {
                    if (!item.getName().toUpperCase().contains(formatString(s.toString())) ) {
                        if(!item.getName().toLowerCase().contains(formatString(s.toString()))) newItems.add(item);
                    }
                }
                itemList.removeAll(newItems);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public String formatString(String s) {
        String value = s.trim();
        while (value.contains("  ")) {
            value = value.replace("  ", " ");
        }
        return value;
    }

    public void hideDialog() {
        alertDialog.dismiss();
    }

    public boolean isShow() {
        return alertDialog.isShowing();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtHuy : {
                hideDialog();
                break;
            }
            case R.id.txtDongY : {
                callChosenItemCallback(itemChecked);
                hideDialog();
                break;
            }
        }
    }

    @Override
    public void onReceiveItem(List<Item> item) {
        itemChecked = item;
        adapter.notifyDataSetChanged();
    }

}
