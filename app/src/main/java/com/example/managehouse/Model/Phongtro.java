package com.example.managehouse.Model;

import java.io.Serializable;
import java.util.List;

public class Phongtro implements Serializable {
    private int id, khutro_id, status, gia, chotsodien, chotsonuoc, total, user_id;
    private String ten, created_at, update_at, img, ghichu, ngaythanhtoan, ngaythanhtoan2;
    private Khutro khutro;
    private List<Nguoitro> nguoitro;

    public Phongtro(int id) {
        this.id = id;
    }

    public Phongtro(int id, int khutro_id, int status, int gia, int chotsodien, int chotsonuoc, int total, int user_id, String ten, String created_at, String update_at, String img, String ghichu, String ngaythanhtoan, String ngaythanhtoan2, Khutro khutro, List<Nguoitro> nguoitro) {
        this.id = id;
        this.khutro_id = khutro_id;
        this.status = status;
        this.gia = gia;
        this.chotsodien = chotsodien;
        this.chotsonuoc = chotsonuoc;
        this.total = total;
        this.user_id = user_id;
        this.ten = ten;
        this.created_at = created_at;
        this.update_at = update_at;
        this.img = img;
        this.ghichu = ghichu;
        this.ngaythanhtoan = ngaythanhtoan;
        this.ngaythanhtoan2 = ngaythanhtoan2;
        this.khutro = khutro;
        this.nguoitro = nguoitro;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getGia() {
        return gia;
    }

    public void setGia(int gia) {
        this.gia = gia;
    }

    public int getChotsodien() {
        return chotsodien;
    }

    public void setChotsodien(int chotsodien) {
        this.chotsodien = chotsodien;
    }

    public int getChotsonuoc() {
        return chotsonuoc;
    }

    public void setChotsonuoc(int chotsonuoc) {
        this.chotsonuoc = chotsonuoc;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
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

    public String getUpdate_at() {
        return update_at;
    }

    public void setUpdate_at(String update_at) {
        this.update_at = update_at;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getGhichu() {
        return ghichu;
    }

    public void setGhichu(String ghichu) {
        this.ghichu = ghichu;
    }

    public String getNgaythanhtoan() {
        return ngaythanhtoan;
    }

    public void setNgaythanhtoan(String ngaythanhtoan) {
        this.ngaythanhtoan = ngaythanhtoan;
    }

    public String getNgaythanhtoan2() {
        return ngaythanhtoan2;
    }

    public void setNgaythanhtoan2(String ngaythanhtoan2) {
        this.ngaythanhtoan2 = ngaythanhtoan2;
    }

    public Khutro getKhutro() {
        return khutro;
    }

    public void setKhutro(Khutro khutro) {
        this.khutro = khutro;
    }

    public List<Nguoitro> getNguoitro() {
        return nguoitro;
    }

    public void setNguoitro(List<Nguoitro> nguoitro) {
        this.nguoitro = nguoitro;
    }
}
