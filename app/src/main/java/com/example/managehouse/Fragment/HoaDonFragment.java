package com.example.managehouse.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.managehouse.Activity.HomeActivity;
import com.example.managehouse.Activity.ShowBillActivity;
import com.example.managehouse.Adapter.ItemHoaDonAdapter;
import com.example.managehouse.Callback.ChosenItemCallback;
import com.example.managehouse.Common.Common;
import com.example.managehouse.Helper.ButtonThaoTacClickListener;
import com.example.managehouse.Helper.MySwipeHelper;
import com.example.managehouse.Model.Hoadon;
import com.example.managehouse.Model.Item;
import com.example.managehouse.Model.Message;
import com.example.managehouse.R;
import com.example.managehouse.Retrofit.API;
import com.example.managehouse.Service.DialogChosenItem;
import com.example.managehouse.Service.DialogLoading;
import com.example.managehouse.Service.DialogNotification;
import com.example.managehouse.Service.SpacesItemDecoration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class HoaDonFragment extends Fragment implements View.OnClickListener, ChosenItemCallback {

    private RecyclerView rvData;
    private EditText edtTimKiem;
    private LottieAnimationView lavLoading;
    private ImageView ivSort, ivLayout, ivFilter;

    private ItemHoaDonAdapter itemHoaDonAdapter;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private API api;
    private HomeActivity homeActivity;
    private List<Hoadon> hoadonList = new ArrayList<>();
    private boolean isLoading = false, layout = true, checkSpace = true; // load more hoadon
    private int limit = 10, sort = 1, filter = -1, checkCallBack = 0;
    private String search = null;
    private Timer timerSearch = null;
    private MySwipeHelper mySwipeHelper = null;

    public HoaDonFragment() {
        // Required empty public constructor
    }

    public static HoaDonFragment newInstance(String param1, String param2) {
        HoaDonFragment fragment = new HoaDonFragment();
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
        View view = inflater.inflate(R.layout.fragment_hoa_don, container, false);
        api = Common.getAPI();
        mapping(view);
        inputTimKiem();
        scrollListener();
        itemHoaDonAdapter = new ItemHoaDonAdapter(getActivity(), hoadonList, layout);
        rvData.setAdapter(itemHoaDonAdapter);
        getData("init", 0);
        swipe();
        return view;
    }

    public void mapping(View view) {
        homeActivity.ivAction.setImageResource(R.drawable.ic_baseline_add_32);
        homeActivity.ivAction.setOnClickListener(this);
        rvData = view.findViewById(R.id.rvData);
        rvData.setHasFixedSize(true);
        changeLayout();
        edtTimKiem = view.findViewById(R.id.edtTimKiem);
        lavLoading = view.findViewById(R.id.lavLoading);
        ivSort = view.findViewById(R.id.ivSort);
        ivSort.setOnClickListener(this);
        ivLayout = view.findViewById(R.id.ivLayout);
        ivLayout.setOnClickListener(this);
        ivFilter = view.findViewById(R.id.ivFilter);
        ivFilter.setOnClickListener(this);
    }

    public void changeLayout() {
        if (!layout) {
            if (checkSpace) {
                rvData.addItemDecoration(new SpacesItemDecoration(10));
                checkSpace = false;
            }
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
            rvData.setLayoutManager(gridLayoutManager);
        } else {
            if (!checkSpace) rvData.addItemDecoration(new SpacesItemDecoration(-10));
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            rvData.setLayoutManager(linearLayoutManager);
            checkSpace = true;
        }
    }

    public void inputTimKiem() {
        edtTimKiem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (timerSearch != null) {
                    timerSearch.cancel();
                }
            }

            @Override
            public void afterTextChanged(final Editable s) {
                if(!s.toString().equals("")) {
                    timerSearch = new Timer();
                    timerSearch.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            homeActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    search = s.toString();
                                    timKiemData(search, "init", 0);
                                }
                            });
                        }
                    }, 300);
                }
                else {
                    getData("init", 0);
                }
            }
        });
    }

    public void timKiemData(final String search, final String type, int offset) {
        if (type.equals("init")) lavLoading.setVisibility(View.VISIBLE);
        compositeDisposable.add(api.timKiemHoaDon(search, limit, offset, sort, filter).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Hoadon>>() {
                    @Override
                    public void accept(List<Hoadon> hoadons) throws Exception {
                        if (type.equals("init")) {
                            hoadonList.clear();
                            if (hoadons.size() > 0) {
                                hoadonList.addAll(hoadons);
                                if (hoadonList.get(0).getTotal() - hoadonList.size() > 0)
                                    isLoading = false;
                                else isLoading = true;
                            } else {
                                isLoading = true;
                                hoadonList.add(new Hoadon(-1));
                            }
                            itemHoaDonAdapter.notifyDataSetChanged();
                            lavLoading.setVisibility(View.GONE);

                        } else {
                            if (type.equals("more")) {
                                hoadonList.remove(hoadonList.size() - 1);
                                itemHoaDonAdapter.notifyItemRemoved(hoadonList.size());
                                hoadonList.addAll(hoadons);
                                if (hoadonList.size() == hoadonList.get(0).getTotal())
                                    isLoading = true;
                                else isLoading = false;
                            }
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toasty.warning(getContext(), "Bạn nhập nhanh quá rồi, hãy thử lại.", 300, true).show();
                        lavLoading.setVisibility(View.GONE);
                        throwable.printStackTrace();
                    }
                }));
    }

    public void scrollListener() {
        rvData.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) rvData.getLayoutManager();
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == hoadonList.size() - 1) {
                        isLoading = true;
                        String type = "all";
                        if (search != null) type = "search";
                        loadMore(type);
                    }
                }
            }
        });
    }

    private void loadMore(String type) {
        hoadonList.add(null);
        itemHoaDonAdapter.notifyItemInserted(hoadonList.size() - 1);
        int offset = hoadonList.size() - 1;
        if (type.equals("all")) {
            getData("more", offset);
        } else if (type.equals("search")) {

            timKiemData(search, "more", offset);
        }
    }

    public void delete(int id, final int pos) {
        String url = "hoadon/" + id;
        lavLoading.setVisibility(View.VISIBLE);
        compositeDisposable.add(api.deleteKhuTro(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Message>() {
                    @Override
                    public void accept(Message message) throws Exception {
                        if (message.getStatus() == 402) {
                            Toasty.error(getContext(), message.getBody()[0], 300, true).show();
                        } else {
                            Toasty.success(getContext(), message.getBody()[0], 300, true).show();
                            hoadonList.remove(pos);
                            if (hoadonList.size() > 0)
                                hoadonList.get(0).setTotal(hoadonList.get(0).getTotal() - 1);
                            else hoadonList.add(new Hoadon(-2));
                            itemHoaDonAdapter.notifyDataSetChanged();
                        }
                        lavLoading.setVisibility(View.GONE);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toasty.error(getContext(), "Gặp sự cố, thử lại sau.", 300, true).show();
                        throwable.printStackTrace();
                    }
                }));
    }

    public void getData(final String type, int offset) {
        if (type.equals("init")) lavLoading.setVisibility(View.VISIBLE);
        compositeDisposable.add(api.getHoaDon(limit, offset, sort, filter).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Hoadon>>() {
                    @Override
                    public void accept(List<Hoadon> hoadons) throws Exception {
                        if (type.equals("init")) {
                            hoadonList.clear();
                            if (hoadons.size() > 0) {
                                hoadonList.addAll(hoadons);
                                Common.hoadonList = hoadonList;
                                if (hoadonList.get(0).getTotal() - hoadonList.size() > 0)
                                    isLoading = false;
                                else isLoading = true;
                            } else {
                                isLoading = true;
                                hoadonList.add(new Hoadon(-2));
                            }
                            itemHoaDonAdapter.notifyDataSetChanged();
                            lavLoading.setVisibility(View.GONE);

                        } else {
                            if (type.equals("more")) {
                                hoadonList.remove(hoadonList.size() - 1);
                                itemHoaDonAdapter.notifyItemRemoved(hoadonList.size());
                                hoadonList.addAll(hoadons);
                                if (hoadonList.size() == hoadonList.get(0).getTotal())
                                    isLoading = true;
                                else isLoading = false;
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toasty.error(getContext(), "Gặp sự cố, thử lại sau.", 300, true).show();
                        lavLoading.setVisibility(View.GONE);
                        throwable.printStackTrace();
                    }
                }));
    }

    public void swipe() {
        mySwipeHelper = new MySwipeHelper(getContext(), rvData) {
            @Override
            public void instantiateButtonThaoTac(RecyclerView.ViewHolder viewHolder, final List<MySwipeHelper.ButtonThaoTac> buffer) {
                mySwipeHelper.buttonWidth = viewHolder.itemView.getWidth() / 6;
                buffer.add(new ButtonThaoTac(getContext(),
                        "Xóa",
                        0,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics()),
                        Color.parseColor("#e74c3c"),
                        new ButtonThaoTacClickListener() {
                            @Override
                            public void onClick(final int pos) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage(R.string.confirm_delete)
                                        .setPositiveButton(R.string.confirm_delete_button_ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                delete(hoadonList.get(pos).getId(), pos);
                                            }
                                        })
                                        .setNegativeButton(R.string.confirm_delete_button_no, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        }).create().show();

                            }
                        }));
                buffer.add(new ButtonThaoTac(getContext(),
                        "Sửa",
                        0,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics()),
                        Color.parseColor("#3498db"),
                        new ButtonThaoTacClickListener() {
                            @Override
                            public void onClick(int pos) {
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("hoadon", (Serializable) hoadonList.get(pos));
                                CreateBillFragment createBillFragment = new CreateBillFragment();
                                createBillFragment.setArguments(bundle);
                                homeActivity.replaceFragment(createBillFragment, true);
                            }
                        }));
                buffer.add(new ButtonThaoTac(getContext(),
                        "Tình trạng",
                        0,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics()),
                        Color.parseColor("#3498db"),
                        new ButtonThaoTacClickListener() {
                            @Override
                            public void onClick(int pos) {
                                updateStatus(pos);
                            }
                        }));

            }
        };

    }

    public void updateStatus(int pos) {
        DialogLoading dialogLoading = new DialogLoading(getActivity(), "Đang sửa tình trạng...");
        dialogLoading.showDialog();
        compositeDisposable.add(api.updateStatus(hoadonList.get(pos).getId()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Message>() {
                    @Override
                    public void accept(Message message) throws Exception {
                        if (message.getStatus() == 402) {
                            Toasty.error(getContext(), message.getBody()[0], 300, true).show();
                        } else {
                            Toasty.success(getContext(), message.getBody()[0], 300, true).show();
                            hoadonList.get(pos).setStatus(Integer.parseInt(message.getData()));
                            itemHoaDonAdapter.notifyDataSetChanged();
                        }
                        dialogLoading.hideDialog();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toasty.error(getContext(), "Gặp sự cố, thử lại sau.", 300, true).show();
                        dialogLoading.hideDialog();
                        throwable.printStackTrace();
                    }
                }));
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
        compositeDisposable.clear();
    }

    @Override
    public void onStop() {
        super.onStop();
        checkSpace = true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivAction: {
                CreateBillFragment createBillFragment = new CreateBillFragment();
                homeActivity.replaceFragment(createBillFragment, true);
                break;
            }
            case R.id.ivSort: {
                checkCallBack = 1;
                List<Item> items = new ArrayList<>();
                items.add(new Item(false, 1, 0, "Mới nhất"));
                items.add(new Item(false, 0, 1, "Cũ nhất"));
                items.add(new Item(false, 2, 2, "Tổng tiền tăng dần"));
                items.add(new Item(false, 3, 3, "Tổng tiền giảm dần"));
                for (Item item : items) {
                    if (item.getId() == sort) item.setChecked(true);
                }
                DialogChosenItem dialogChosenItem = new DialogChosenItem(getActivity(), items, "Sắp xếp", "single", 0, false);
                dialogChosenItem.setChosenItemCallback(this);
                dialogChosenItem.showDialog();
                break;
            }
            case R.id.ivLayout: {
                layout = !layout;
                changeLayout();
                itemHoaDonAdapter = new ItemHoaDonAdapter(getActivity(), hoadonList, layout);
                rvData.setAdapter(itemHoaDonAdapter);
                break;
            }
            case R.id.ivFilter: {
                checkCallBack = 0;
                List<Item> items = new ArrayList<>();
                items.add(new Item(false, -1, 0, "Tất cả"));
                items.add(new Item(false, 1, 1, "Chờ thu tiền"));
                items.add(new Item(false, 2, 2, "Đã thu tiền"));
                items.add(new Item(false, 0, 2, "Đã hủy"));
                for (Item item : items) {
                    if (item.getId() == filter) item.setChecked(true);
                }
                DialogChosenItem dialogChosenItem = new DialogChosenItem(getActivity(), items, "Hiển thị hóa đơn", "single", 0, false);
                dialogChosenItem.setChosenItemCallback(this);
                dialogChosenItem.showDialog();
                break;
            }
        }
    }

    @Override
    public void onReceiveItem(List<Item> item) {
        if (checkCallBack == 1) sort = item.get(0).getId();
        else if (checkCallBack == 0) filter = item.get(0).getId();
        int offset = hoadonList.size() - 1;
        if (search != null) {
            timKiemData(search, "init", 0);
        } else {
            getData("init", 0);
        }
    }
}