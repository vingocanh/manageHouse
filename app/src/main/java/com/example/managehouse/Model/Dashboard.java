package com.example.managehouse.Model;

public class Dashboard {
    private int total_money, total_khutro, total_phongtro_thue, total_phongtro_trong, total_nguoitro;

    public Dashboard(int total_money, int total_khutro, int total_phongtro_thue, int total_phongtro_trong, int total_nguoitro) {
        this.total_money = total_money;
        this.total_khutro = total_khutro;
        this.total_phongtro_thue = total_phongtro_thue;
        this.total_phongtro_trong = total_phongtro_trong;
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

    public int getTotal_phongtro_thue() {
        return total_phongtro_thue;
    }

    public void setTotal_phongtro_thue(int total_phongtro_thue) {
        this.total_phongtro_thue = total_phongtro_thue;
    }

    public int getTotal_phongtro_trong() {
        return total_phongtro_trong;
    }

    public void setTotal_phongtro_trong(int total_phongtro_trong) {
        this.total_phongtro_trong = total_phongtro_trong;
    }

    public int getTotal_nguoitro() {
        return total_nguoitro;
    }

    public void setTotal_nguoitro(int total_nguoitro) {
        this.total_nguoitro = total_nguoitro;
    }
}
