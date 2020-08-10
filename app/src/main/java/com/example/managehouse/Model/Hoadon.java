package com.example.managehouse.Model;

import java.io.Serializable;
import java.util.List;

public class Hoadon implements Serializable {

    private int id, thang, nam, sodiencu, sodienmoi, tongtien, status, phongtro_id, sonuoccu,sonuocmoi, total;
    private Phongtro phongtro;
    private List<Khutrokhoanthu> khoanthu;
    private String ten, ghichu, created_at, updated_at;
    private String raw; // json hóa đơn để show bill activity
    private int check_water;

    public Hoadon(int id) {
        this.id = id;
    }

    public Hoadon(int id, int thang, int nam, int sodiencu, int sodienmoi, int tongtien, int status, int phongtro_id, int sonuoccu, int sonuocmoi, int total, Phongtro phongtro, List<Khutrokhoanthu> khoanthu, String ten, String ghichu, String created_at, String updated_at, String raw, int check_water) {
        this.id = id;
        this.thang = thang;
        this.nam = nam;
        this.sodiencu = sodiencu;
        this.sodienmoi = sodienmoi;
        this.tongtien = tongtien;
        this.status = status;
        this.phongtro_id = phongtro_id;
        this.sonuoccu = sonuoccu;
        this.sonuocmoi = sonuocmoi;
        this.total = total;
        this.phongtro = phongtro;
        this.khoanthu = khoanthu;
        this.ten = ten;
        this.ghichu = ghichu;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.raw = raw;
        this.check_water = check_water;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getThang() {
        return thang;
    }

    public void setThang(int thang) {
        this.thang = thang;
    }

    public int getNam() {
        return nam;
    }

    public void setNam(int nam) {
        this.nam = nam;
    }

    public int getSodiencu() {
        return sodiencu;
    }

    public void setSodiencu(int sodiencu) {
        this.sodiencu = sodiencu;
    }

    public int getSodienmoi() {
        return sodienmoi;
    }

    public void setSodienmoi(int sodienmoi) {
        this.sodienmoi = sodienmoi;
    }

    public int getTongtien() {
        return tongtien;
    }

    public void setTongtien(int tongtien) {
        this.tongtien = tongtien;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPhongtro_id() {
        return phongtro_id;
    }

    public void setPhongtro_id(int phongtro_id) {
        this.phongtro_id = phongtro_id;
    }

    public int getSonuoccu() {
        return sonuoccu;
    }

    public void setSonuoccu(int sonuoccu) {
        this.sonuoccu = sonuoccu;
    }

    public int getSonuocmoi() {
        return sonuocmoi;
    }

    public void setSonuocmoi(int sonuocmoi) {
        this.sonuocmoi = sonuocmoi;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Phongtro getPhongtro() {
        return phongtro;
    }

    public void setPhongtro(Phongtro phongtro) {
        this.phongtro = phongtro;
    }

    public List<Khutrokhoanthu> getKhoanthu() {
        return khoanthu;
    }

    public void setKhoanthu(List<Khutrokhoanthu> khoanthu) {
        this.khoanthu = khoanthu;
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

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public int getCheck_water() {
        return check_water;
    }

    public void setCheck_water(int check_water) {
        this.check_water = check_water;
    }
}
