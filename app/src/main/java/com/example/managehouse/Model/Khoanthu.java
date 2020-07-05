package com.example.managehouse.Model;

public class Khoanthu {
    private int id, status, user_id;
    private String ten, created_at, updated_at, mota;
    private boolean checked;

    public Khoanthu(int id, int status, int user_id, String ten, String created_at, String updated_at, String mota) {
        this.id = id;
        this.status = status;
        this.user_id = user_id;
        this.ten = ten;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.mota = mota;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
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
}
