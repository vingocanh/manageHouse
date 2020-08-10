package com.example.managehouse.Model;

import java.io.Serializable;

public class Nguoitrogiadinh implements Serializable {

    private int id, nguotro_id, status;
    private String bo_hoten, bo_namsinh, bo_nghenghiep, bo_noicongtac, bo_choohiennay, me_hoten, me_nghenghiep, me_noicongtac, me_choohiennay, me_namsinh, created_at, updated_at;

    public Nguoitrogiadinh(int id, int nguotro_id, int status, String bo_hoten, String bo_namsinh, String bo_nghenghiep, String bo_noicongtac, String bo_choohiennay, String me_hoten, String me_nghenghiep, String me_noicongtac, String me_choohiennay, String me_namsinh, String created_at, String updated_at) {
        this.id = id;
        this.nguotro_id = nguotro_id;
        this.status = status;
        this.bo_hoten = bo_hoten;
        this.bo_namsinh = bo_namsinh;
        this.bo_nghenghiep = bo_nghenghiep;
        this.bo_noicongtac = bo_noicongtac;
        this.bo_choohiennay = bo_choohiennay;
        this.me_hoten = me_hoten;
        this.me_nghenghiep = me_nghenghiep;
        this.me_noicongtac = me_noicongtac;
        this.me_choohiennay = me_choohiennay;
        this.me_namsinh = me_namsinh;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNguotro_id() {
        return nguotro_id;
    }

    public void setNguotro_id(int nguotro_id) {
        this.nguotro_id = nguotro_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getBo_hoten() {
        return bo_hoten;
    }

    public void setBo_hoten(String bo_hoten) {
        this.bo_hoten = bo_hoten;
    }

    public String getBo_namsinh() {
        return bo_namsinh;
    }

    public void setBo_namsinh(String bo_namsinh) {
        this.bo_namsinh = bo_namsinh;
    }

    public String getBo_nghenghiep() {
        return bo_nghenghiep;
    }

    public void setBo_nghenghiep(String bo_nghenghiep) {
        this.bo_nghenghiep = bo_nghenghiep;
    }

    public String getBo_noicongtac() {
        return bo_noicongtac;
    }

    public void setBo_noicongtac(String bo_noicongtac) {
        this.bo_noicongtac = bo_noicongtac;
    }

    public String getBo_choohiennay() {
        return bo_choohiennay;
    }

    public void setBo_choohiennay(String bo_choohiennay) {
        this.bo_choohiennay = bo_choohiennay;
    }

    public String getMe_hoten() {
        return me_hoten;
    }

    public void setMe_hoten(String me_hoten) {
        this.me_hoten = me_hoten;
    }

    public String getMe_nghenghiep() {
        return me_nghenghiep;
    }

    public void setMe_nghenghiep(String me_nghenghiep) {
        this.me_nghenghiep = me_nghenghiep;
    }

    public String getMe_noicongtac() {
        return me_noicongtac;
    }

    public void setMe_noicongtac(String me_noicongtac) {
        this.me_noicongtac = me_noicongtac;
    }

    public String getMe_choohiennay() {
        return me_choohiennay;
    }

    public void setMe_choohiennay(String me_choohiennay) {
        this.me_choohiennay = me_choohiennay;
    }

    public String getMe_namsinh() {
        return me_namsinh;
    }

    public void setMe_namsinh(String me_namsinh) {
        this.me_namsinh = me_namsinh;
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
}
