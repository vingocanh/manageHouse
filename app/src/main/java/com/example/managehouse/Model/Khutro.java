package com.example.managehouse.Model;

import java.io.Serializable;
import java.util.List;

public class Khutro implements Serializable {
    private int id, user_id, status, phongtro_count, total;
    private String ten, diachi, created_at, updated_at, img, nam_xd;
    private List<Khutrokhoanthu> khutrokhoanthu;
    private List<Phongtro> phongtro;

    public Khutro(int id) {
        this.id = id;
    }

    public Khutro(int id, int user_id, int status, int phongtro_count, int total, String ten, String diachi, String created_at, String updated_at, String img, String nam_xd, List<Khutrokhoanthu> khutrokhoanthu, List<Phongtro> phongtro) {
        this.id = id;
        this.user_id = user_id;
        this.status = status;
        this.phongtro_count = phongtro_count;
        this.total = total;
        this.ten = ten;
        this.diachi = diachi;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.img = img;
        this.nam_xd = nam_xd;
        this.khutrokhoanthu = khutrokhoanthu;
        this.phongtro = phongtro;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPhongtro_count() {
        return phongtro_count;
    }

    public void setPhongtro_count(int phongtro_count) {
        this.phongtro_count = phongtro_count;
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

    public String getDiachi() {
        return diachi;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getNam_xd() {
        return nam_xd;
    }

    public void setNam_xd(String nam_xd) {
        this.nam_xd = nam_xd;
    }

    public List<Khutrokhoanthu> getKhutrokhoanthu() {
        return khutrokhoanthu;
    }

    public void setKhutrokhoanthu(List<Khutrokhoanthu> khutrokhoanthu) {
        this.khutrokhoanthu = khutrokhoanthu;
    }

    public List<Phongtro> getPhongtro() {
        return phongtro;
    }

    public void setPhongtro(List<Phongtro> phongtro) {
        this.phongtro = phongtro;
    }
}
