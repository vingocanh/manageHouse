package com.example.managehouse.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.managehouse.Callback.ChosenItemCallback;
import com.example.managehouse.Model.Item;
import com.example.managehouse.R;

import java.util.ArrayList;
import java.util.List;

public class ItemChosenAdapter extends RecyclerView.Adapter<ItemChosenAdapter.MyViewHolder> {

    private Context context;
    private List<Item> itemList;
    private List<Item> itemChecked = new ArrayList<>();
    private ChosenItemCallback chosenItemCallback;
    private String type;

    public ItemChosenAdapter(Context context, List<Item> itemList, String type) {
        this.context = context;
        this.itemList = itemList;
        this.type = type;
        for (Item item : itemList) {
            if(item.isChecked()) itemChecked.add(item);
        }
    }

    public void setChosenItemCallback(ChosenItemCallback chosenItemCallback) {
        this.chosenItemCallback = chosenItemCallback;
    }

    public void callChosenItemCallback(List<Item> item) {
        chosenItemCallback.onReceiveItem(item);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(context).inflate(R.layout.item_chosen, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final Item item = itemList.get(position);
        holder.cbItem.setChecked(item.isChecked());
        holder.cbItem.setTag(item.getId());
        holder.txtItem.setText(item.getName());

        holder.cbItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedItem(position);
            }
        });

        holder.txtItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedItem(position);
            }
        });
    }
    public void checkedItem(int position) {
        itemChecked.clear();
        if(type.equals("single")) {
            clearChecked();
            itemList.get(position).setChecked(true);
            itemChecked.add(itemList.get(position));
        }
        else {
            if(itemList.get(position).isChecked()) itemList.get(position).setChecked(false);
            else {
                itemList.get(position).setChecked(true);
            }
            for (Item item : itemList) {
                if(item.isChecked()) itemChecked.add(item);
            }
        }
        callChosenItemCallback(itemChecked);
    }
    public void clearChecked() {
        for (Item item : itemList) {
            item.setChecked(false);
        }
    }

    public void checkAllItem(boolean check) {
        itemChecked.clear();
        for (Item item : itemList) {
            item.setChecked(check);
            if(check) itemChecked.add(item);
        }
        callChosenItemCallback(itemChecked);
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CheckBox cbItem;
        private TextView txtItem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cbItem = itemView.findViewById(R.id.cbItem);
            txtItem = itemView.findViewById(R.id.txtItem);
        }
    }
}
