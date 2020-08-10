package com.example.managehouse.Model;

import java.io.Serializable;

public class Nguoitro implements Serializable {

    private int id, phongtro_id, khutro_id, status, total, user_id;
    private String hoten, ho, ten, avatar, ngaysinh, ngaysinh2, socmnd, quequan, nghenghiep, noilamviec, ghichu, created_at, updated_at;
    private Phongtro phongtro;
    private Trinhdo trinhdo;
    private Khutro khutro;
    private Nguoitrogiadinh nguoitrogiadinh;

    public Nguoitro(int id) {
        this.id = id;
    }

    public Nguoitro(int id, int phongtro_id, int khutro_id, int status, int total, int user_id, String hoten, String ho, String ten, String avatar, String ngaysinh, String ngaysinh2, String socmnd, String quequan, String nghenghiep, String noilamviec, String ghichu, String created_at, String updated_at, Phongtro phongtro, Trinhdo trinhdo, Khutro khutro, Nguoitrogiadinh nguoitrogiadinh) {
        this.id = id;
        this.phongtro_id = phongtro_id;
        this.khutro_id = khutro_id;
        this.status = status;
        this.total = total;
        this.user_id = user_id;
        this.hoten = hoten;
        this.ho = ho;
        this.ten = ten;
        this.avatar = avatar;
        this.ngaysinh = ngaysinh;
        this.ngaysinh2 = ngaysinh2;
        this.socmnd = socmnd;
        this.quequan = quequan;
        this.nghenghiep = nghenghiep;
        this.noilamviec = noilamviec;
        this.ghichu = ghichu;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.phongtro = phongtro;
        this.trinhdo = trinhdo;
        this.khutro = khutro;
        this.nguoitrogiadinh = nguoitrogiadinh;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPhongtro_id() {
        return phongtro_id;
    }

    public void setPhongtro_id(int phongtro_id) {
        this.phongtro_id = phongtro_id;
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

    public String getHoten() {
        return hoten;
    }

    public void setHoten(String hoten) {
        this.hoten = hoten;
    }

    public String getHo() {
        return ho;
    }

    public void setHo(String ho) {
        this.ho = ho;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNgaysinh() {
        return ngaysinh;
    }

    public void setNgaysinh(String ngaysinh) {
        this.ngaysinh = ngaysinh;
    }

    public String getNgaysinh2() {
        return ngaysinh2;
    }

    public void setNgaysinh2(String ngaysinh2) {
        this.ngaysinh2 = ngaysinh2;
    }

    public String getSocmnd() {
        return socmnd;
    }

    public void setSocmnd(String socmnd) {
        this.socmnd = socmnd;
    }

    public String getQuequan() {
        return quequan;
    }

    public void setQuequan(String quequan) {
        this.quequan = quequan;
    }

    public String getNghenghiep() {
        return nghenghiep;
    }

    public void setNghenghiep(String nghenghiep) {
        this.nghenghiep = nghenghiep;
    }

    public String getNoilamviec() {
        return noilamviec;
    }

    public void setNoilamviec(String noilamviec) {
        this.noilamviec = noilamviec;
    }

    public String getGhichu() {
        return ghichu;
    }

    public void setGhichu(String ghichu) {
        this.ghichu = ghichu;
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

    public Phongtro getPhongtro() {
        return phongtro;
    }

    public void setPhongtro(Phongtro phongtro) {
        this.phongtro = phongtro;
    }

    public Trinhdo getTrinhdo() {
        return trinhdo;
    }

    public void setTrinhdo(Trinhdo trinhdo) {
        this.trinhdo = trinhdo;
    }

    public Khutro getKhutro() {
        return khutro;
    }

    public void setKhutro(Khutro khutro) {
        this.khutro = khutro;
    }

    public Nguoitrogiadinh getNguoitrogiadinh() {
        return nguoitrogiadinh;
    }

    public void setNguoitrogiadinh(Nguoitrogiadinh nguoitrogiadinh) {
        this.nguoitrogiadinh = nguoitrogiadinh;
    }
}
