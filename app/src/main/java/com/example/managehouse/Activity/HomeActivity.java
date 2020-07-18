package com.example.managehouse.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.managehouse.Common.Common;
import com.example.managehouse.Fragment.DashboardFragment;
import com.example.managehouse.Fragment.KhuTroFragment;
import com.example.managehouse.Fragment.NguoiTroFragment;
import com.example.managehouse.Fragment.PhongTroFragment;
import com.example.managehouse.Model.User;
import com.example.managehouse.R;
import com.google.android.material.navigation.NavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    public ImageView ivMenu, ivAction;
    private DrawerLayout drawerLayout;
    private NavigationView nvMenu;
    private Fragment fragment = null;

    /* premission */
    private List<String> permissions = new ArrayList<>();

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mapping();
        createPermission();
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        String expiresAt = sharedPreferences.getString("expires_at", null);
        if (expiresAt != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            try {
                Date expiresUser = formatter.parse(expiresAt);
                Date currentDate = formatter.parse(formatter.format(new Date()));
                if (currentDate.compareTo(expiresUser) < 0) {
                    fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.add(R.id.flHome, new DashboardFragment());
                    fragmentTransaction.commit();
                    Common.currentUser = new User(
                            Integer.parseInt(sharedPreferences.getString("id", "0")),
                            sharedPreferences.getString("name", ""),
                            sharedPreferences.getString("username", ""),
                            sharedPreferences.getString("roles", ""),
                            sharedPreferences.getString("address", ""),
                            null,
                            null,
                            sharedPreferences.getString("created_at", ""),
                            sharedPreferences.getString("updated_at", ""),
                            sharedPreferences.getString("access_token", ""),
                            sharedPreferences.getString("token_type", ""),
                            sharedPreferences.getString("expires_at", "")
                    );
                    Common.token = sharedPreferences.getString("token_type", "") + " " + sharedPreferences.getString("access_token", "");
                } else {
                    sharedPreferences.edit().clear().commit();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

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
    }

    public void replaceFragment(Fragment fragment, boolean backStack) {
        this.fragment = fragment;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.replace(R.id.flHome, fragment);
        if (backStack) {
            fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
        }
        fragmentTransaction.commit();

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivMenu: {
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.flHome);
        String fragmentClass = "";
        if(fragment != null) fragmentClass = fragment.getClass().getSimpleName();
        switch (menuItem.getItemId()) {
            case R.id.navKhuTro: {
                if(!fragmentClass.equals("KhuTroFragment")) {
                    replaceFragment(new KhuTroFragment(), true);
                }
                break;
            }
            case R.id.navPhongTro : {
                if(!fragmentClass.equals("PhongTroFragment")) {
                    replaceFragment(new PhongTroFragment(), true);
                }
                break;
            }
            case R.id.navNguoiTro : {
                if(!fragmentClass.equals("NguoiTroFragment")) {
                    replaceFragment(new NguoiTroFragment(), true);
                }
                break;
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
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
