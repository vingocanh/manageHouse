package com.example.managehouse.Model;

public class Dashboard {
    private int total_money, total_khutro, total_phongtro, total_nguoitro;

    public Dashboard(int total_money, int total_khutro, int total_phongtro, int total_nguoitro) {
        this.total_money = total_money;
        this.total_khutro = total_khutro;
        this.total_phongtro = total_phongtro;
        this.total_nguoitro = total_nguoitro;
    }

    public int getTotal_money() {
        return total_money;
    }

    public void setTotal_money(int total_money) {
        this.total_money = total_money;
    }

    public int getTotal_khutro() {
        return total_khutro;
    }

    public void setTotal_khutro(int total_khutro) {
        this.total_khutro = total_khutro;
    }

    public int getTotal_phongtro() {
        return total_phongtro;
    }

    public void setTotal_phongtro(int total_phongtro) {
        this.total_phongtro = total_phongtro;
    }

    public int getTotal_nguoitro() {
        return total_nguoitro;
    }

    public void setTotal_nguoitro(int total_nguoitro) {
        this.total_nguoitro = total_nguoitro;
    }
}
