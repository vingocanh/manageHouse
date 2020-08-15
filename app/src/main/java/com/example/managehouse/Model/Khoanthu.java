package com.example.managehouse.Model;

import java.io.Serializable;
import java.util.List;

public class Khoanthu implements Serializable {
    private int id, status, user_id, total;
    private String ten, created_at, updated_at, mota, avatar;
    private boolean checked;
    private List<Khutro> khutro;

    public Khoanthu(int id) {
        this.id = id;
    }

    public Khoanthu(int id, int status, int user_id, int total, String ten, String created_at, String updated_at, String mota, String avatar, boolean checked, List<Khutro> khutro) {
        this.id = id;
        this.status = status;
        this.user_id = user_id;
        this.total = total;
        this.ten = ten;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.mota = mota;
        this.avatar = avatar;
        this.checked = checked;
        this.khutro = khutro;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getMota() {
        return mota;
    }

    public void setMota(String mota) {
        this.mota = mota;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public List<Khutro> getKhutro() {
        return khutro;
    }

    public void setKhutro(List<Khutro> khutro) {
        this.khutro = khutro;
    }
}
