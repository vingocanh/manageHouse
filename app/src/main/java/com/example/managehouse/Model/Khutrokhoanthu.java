package com.example.managehouse.Model;

import java.util.List;

public class Khutrokhoanthu {
    private int id, khutro_id, khoanthu_id, gia, status, donvitinh_id;
    private String created_at, updated_at;
    private Khoanthu khoanthu;

    public Khutrokhoanthu(int id, int khutro_id, int khoanthu_id, int gia, int status, int donvitinh_id, String created_at, String updated_at, Khoanthu khoanthu) {
        this.id = id;
        this.khutro_id = khutro_id;
        this.khoanthu_id = khoanthu_id;
        this.gia = gia;
        this.status = status;
        this.donvitinh_id = donvitinh_id;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.khoanthu = khoanthu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getKhutro_id() {
        return khutro_id;
    }

    public void setKhutro_id(int khutro_id) {
        this.khutro_id = khutro_id;
    }

    public int getKhoanthu_id() {
        return khoanthu_id;
    }

    public void setKhoanthu_id(int khoanthu_id) {
        this.khoanthu_id = khoanthu_id;
    }

    public int getGia() {
        return gia;
    }

    public void setGia(int gia) {
        this.gia = gia;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDonvitinh_id() {
        return donvitinh_id;
    }

    public void setDonvitinh_id(int donvitinh_id) {
        this.donvitinh_id = donvitinh_id;
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

    public Khoanthu getKhoanthu() {
        return khoanthu;
    }

    public void setKhoanthu(Khoanthu khoanthu) {
        this.khoanthu = khoanthu;
    }
}
