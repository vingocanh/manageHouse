package com.example.managehouse.Activity;

import android.app.AlertDialog;
import android.app.RemoteInput;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.managehouse.Common.Common;
import com.example.managehouse.Fragment.ContactFragment;
import com.example.managehouse.Fragment.CreateBillFragment;
import com.example.managehouse.Fragment.DashboardFragment;
import com.example.managehouse.Fragment.HoaDonFragment;
import com.example.managehouse.Fragment.KhoanThuFragment;
import com.example.managehouse.Fragment.KhuTroFragment;
import com.example.managehouse.Fragment.NguoiTroFragment;
import com.example.managehouse.Fragment.PhongTroFragment;
import com.example.managehouse.Fragment.SettingFragment;
import com.example.managehouse.Fragment.UserFragment;
import com.example.managehouse.Model.Message;
import com.example.managehouse.Model.User;
import com.example.managehouse.R;
import com.example.managehouse.Retrofit.API;
import com.example.managehouse.Retrofit.RetrofitClient;
import com.example.managehouse.Service.DialogNotification;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    public ImageView ivMenu, ivAction;
    private DrawerLayout drawerLayout;
    private NavigationView nvMenu;
    private Fragment fragment = null;
    private SharedPreferences sharedPreferences;
    private LinearLayout llDrawerHeader;
    private int type = -1; // type from notification;
    private Bundle dataFragment = new Bundle();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private API api;

    /* premission */
    private List<String> permissions = new ArrayList<>();

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        api = Common.getAPI();
        mapping();
        createPermission();
        backFragment();
        onNewIntent(getIntent());
        if (savedInstanceState == null) {
            sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
            String expiresAt = sharedPreferences.getString("expires_at", null);
            if (expiresAt != null) {
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Date expiresUser = formatter.parse(expiresAt);
                    Date currentDate = formatter.parse(formatter.format(new Date()));
                    if (currentDate.compareTo(expiresUser) < 0) {
                        if (this.type == -1) replaceFragment(new DashboardFragment(), false);
                        else {
                            switch (this.type) {
                                case 0: {
                                    Common.posMenu = 4;
                                    CreateBillFragment createBillFragment = new CreateBillFragment();
                                    createBillFragment.setArguments(dataFragment);
                                    replaceFragment(createBillFragment, false);
                                    break;
                                }
                                case 1: {
                                    replaceFragment(new DashboardFragment(), false);
                                    break;
                                }
                            }
                        }
                        Common.currentUser = new User(
                                Integer.parseInt(sharedPreferences.getString("id", "0")),
                                sharedPreferences.getString("name", ""),
                                sharedPreferences.getString("avatar", ""),
                                sharedPreferences.getString("phone", ""),
                                sharedPreferences.getString("username", ""),
                                sharedPreferences.getString("roles", ""),
                                sharedPreferences.getString("address", ""),
                                sharedPreferences.getString("email", ""),
                                null,
                                sharedPreferences.getString("created_at", ""),
                                sharedPreferences.getString("updated_at", ""),
                                sharedPreferences.getString("access_token", ""),
                                sharedPreferences.getString("token_type", ""),
                                sharedPreferences.getString("expires_at", "")
                        );
                        Common.token = sharedPreferences.getString("token_type", "") + " " + sharedPreferences.getString("access_token", "");
                        setValueHeader();
                    } else {
                        logout();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        nvMenu.getMenu().getItem(Common.posMenu).setChecked(true);
        receiveResultNotification();
    }

    public void mapping() {
        ivAction = findViewById(R.id.ivAction);
        ivMenu = findViewById(R.id.ivMenu);
        ivAction.setImageResource(R.drawable.ic_notifications_32dp);
        ivMenu.setOnClickListener(this);
        drawerLayout = findViewById(R.id.drawer_layout);
        nvMenu = findViewById(R.id.nav_view);
        nvMenu.getMenu().getItem(0).setChecked(true);
        nvMenu.setNavigationItemSelectedListener(this);
        llDrawerHeader = nvMenu.getHeaderView(0).findViewById(R.id.llDrawerHeader);
        llDrawerHeader.setOnClickListener(this);
    }

    public void setValueHeader() {
        View headerView = nvMenu.getHeaderView(0);
        TextView txtName = headerView.findViewById(R.id.txtName);
        CircleImageView ivAvatar = headerView.findViewById(R.id.ivAvatar);
        txtName.setText(Common.currentUser.getName());
        String avatar = sharedPreferences.getString("avatar", "");
        if( !avatar.equals("")) {
            Picasso.get().load(avatar).placeholder(R.drawable.ic_account).error(R.drawable.ic_account).into(ivAvatar);
        }
        else {
            ivAvatar.setImageResource(R.drawable.ic_account);
        }
    }

    public void replaceFragment(Fragment fragment, boolean backStack) {
        Common.currentFragment = fragment;
        this.fragment = fragment;
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.replace(R.id.flHome, fragment);
        if (backStack) {
            fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
        }
        fragmentTransaction.commit();
        if (fragment.getClass().getSimpleName().equals("DashboardFragment")) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public void createPermission() {
        permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        String[] arrPer = new String[permissions.size()];
        arrPer = permissions.toArray(arrPer);
        if (!checkPermissions()) {
            ActivityCompat.requestPermissions(HomeActivity.this, arrPer, 12);
        }
    }

    public boolean checkPermissions() {
        for (int i = 0; i < permissions.size(); i++) {
            if (ContextCompat.checkSelfPermission(this, permissions.get(i)) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    public void logout() {
        compositeDisposable.add(api.logout(Common.currentUser.getId(), Common.getDeviceToken(getApplicationContext())).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Message>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void accept(Message message) throws Exception {
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                }));
        sharedPreferences.edit().clear().commit();
        Common.currentUser = null;
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    public void backFragment() {
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (Common.posMenu >= 0) {
                    nvMenu.getMenu().getItem(Common.posMenu).setChecked(true);
                } else {
                    for (int i = 0; i < nvMenu.getMenu().size(); i++) {
                        nvMenu.getMenu().getItem(i).setChecked(false);
                    }
                }
            }
        });
    }

    public void receiveResultNotification() {
        if (getMessageText(getIntent()) != null) {
            int day = Integer.parseInt(getMessageText(getIntent()).toString());
            int phongtro_id = dataFragment.getInt("phongtro_id", -1);
            Log.d("cuong", phongtro_id + "");
            compositeDisposable.add(api.updateDateNotification(phongtro_id, Common.currentUser.getId(), day).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Message>() {
                        @Override
                        public void accept(Message message) throws Exception {
                            if(message.getStatus() == 201) {
                                Toasty.success(getApplicationContext(),message.getBody()[0], 500, true).show();
                            }
                            else {
                                Toasty.error(getApplicationContext(),message.getBody()[0], 300, true).show();
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                        }
                    }));
        }
    }

    public CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        return (remoteInput != null) ? remoteInput.getCharSequence("12121997") : null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            this.type = bundle.getInt("type");
            switch (type) {
                case 0: {
                    dataFragment.putInt("khutro_id", bundle.getInt("khutro_id"));
                    dataFragment.putInt("phongtro_id", bundle.getInt("phongtro_id"));
                    break;
                }
                case 1: {
                    dataFragment.putInt("phongtro_id", bundle.getInt("phongtro_id", -1));
                    break;
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivMenu: {
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            }

            case R.id.llDrawerHeader: {
                UserFragment userFragment = new UserFragment();
                replaceFragment(userFragment, true);
                drawerLayout.closeDrawer(GravityCompat.START);
                for (int i = 0; i < nvMenu.getMenu().size(); i++) {
                    nvMenu.getMenu().getItem(i).setChecked(false);
                }
                break;
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.flHome);
        String fragmentClass = "";
        if (fragment != null) fragmentClass = fragment.getClass().getSimpleName();
        switch (menuItem.getItemId()) {
            case R.id.navTongQuan: {
                if (!fragmentClass.equals("DashboardFragment")) {
                    replaceFragment(new DashboardFragment(), false);
                }
                break;
            }
            case R.id.navKhuTro: {
                if (!fragmentClass.equals("KhuTroFragment")) {
                    replaceFragment(new KhuTroFragment(), true);
                }
                break;
            }
            case R.id.navPhongTro: {
                if (!fragmentClass.equals("PhongTroFragment")) {
                    replaceFragment(new PhongTroFragment(), true);
                }
                break;
            }
            case R.id.navNguoiTro: {
                if (!fragmentClass.equals("NguoiTroFragment")) {
                    replaceFragment(new NguoiTroFragment(), true);
                }
                break;
            }
            case R.id.navHoaDon: {
                if (!fragmentClass.equals("HoaDonFragment")) {
                    replaceFragment(new HoaDonFragment(), true);
                }
                break;
            }
            case R.id.navKhoanThu: {
                if (!fragmentClass.equals("KhoanThuFragment")) {
                    replaceFragment(new KhoanThuFragment(), true);
                }
                break;
            }
            case R.id.navDangXuat: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Bạn chắc chắn muốn đăng xuất?")
                        .setPositiveButton(R.string.confirm_delete_button_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                logout();
                            }
                        })
                        .setNegativeButton(R.string.confirm_delete_button_no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create().show();

                break;
            }
            case R.id.navCaiDat: {
                if (!fragmentClass.equals("SettingFragment")) {
                    replaceFragment(new SettingFragment(), true);
                }
                break;
            }
            case R.id.navLienHe: {
                if (!fragmentClass.equals("ContactFragment")) {
                    replaceFragment(new ContactFragment(), true);
                }
                break;
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (fragment != null) {
                if (fragment.getView() != null) {
                    if (fragment.getView().getTag() != null) {
                        if (Common.checkFormChange) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setMessage("Dữ liệu bạn thay đổi sẽ bị mất, chắc chắn thoát?")
                                    .setPositiveButton(R.string.confirm_delete_button_ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            HomeActivity.super.onBackPressed();
                                        }
                                    })
                                    .setNegativeButton(R.string.confirm_delete_button_no, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).create().show();
                        } else HomeActivity.super.onBackPressed();
                        return;
                    }
                }
            }
            super.onBackPressed();
        }
    }
}
