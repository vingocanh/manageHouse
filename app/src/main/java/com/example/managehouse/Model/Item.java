package com.example.managehouse.Model;

public class Item {

    private boolean checked;
    private int id, stt;
    private String name;

    public Item(boolean checked, int id, int stt, String name) {
        this.checked = checked;
        this.id = id;
        this.stt = stt;
        this.name = name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStt() {
        return stt;
    }

    public void setStt(int stt) {
        this.stt = stt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
