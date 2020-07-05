package com.example.managehouse.Callback;

import com.example.managehouse.Model.Item;

import java.util.List;

public interface ChosenItemCallback {

    void onReceiveItem(List<Item> item);

}
