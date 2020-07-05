package com.example.managehouse.Model;

import java.util.List;

public class Phongtro {
    private int id, khutro_id, status, gia, chotsodien, chotsonuoc;
    private String ten, created_at, update_at;
    private Khutro khutro;

    public Phongtro(int id, int khutro_id, int status, int gia, int chotsodien, int chotsonuoc, String ten, String created_at, String update_at, Khutro khutro) {
        this.id = id;
        this.khutro_id = khutro_id;
        this.status = status;
        this.gia = gia;
        this.chotsodien = chotsodien;
        this.chotsonuoc = chotsonuoc;
        this.ten = ten;
        this.created_at = created_at;
        this.update_at = update_at;
        this.khutro = khutro;
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

    public Khutro getKhutro() {
        return khutro;
    }

    public void setKhutro(Khutro khutro) {
        this.khutro = khutro;
    }
}
