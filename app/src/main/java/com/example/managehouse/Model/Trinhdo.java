package com.example.managehouse.Model;

import java.io.Serializable;

public class Trinhdo implements Serializable {

    private int id, status;
    private String ten, ghichu;

    public Trinhdo(int id, int status, String ten, String ghichu) {
        this.id = id;
        this.status = status;
        this.ten = ten;
        this.ghichu = ghichu;
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

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getGhichu() {
        return ghichu;
    }

    public void setGhichu(String ghichu) {
        this.ghichu = ghichu;
    }
}
