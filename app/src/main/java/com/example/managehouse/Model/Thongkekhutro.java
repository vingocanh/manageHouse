package com.example.managehouse.Model;

import java.io.Serializable;

public class Thongkekhutro implements Serializable {

    private int total_price, number_room_full, number_room_empty, number_people;

    public Thongkekhutro(int total_price, int number_room_full, int number_room_empty, int number_people) {
        this.total_price = total_price;
        this.number_room_full = number_room_full;
        this.number_room_empty = number_room_empty;
        this.number_people = number_people;
    }

    public int getTotal_price() {
        return total_price;
    }

    public void setTotal_price(int total_price) {
        this.total_price = total_price;
    }

    public int getNumber_room_full() {
        return number_room_full;
    }

    public void setNumber_room_full(int number_room_full) {
        this.number_room_full = number_room_full;
    }

    public int getNumber_room_empty() {
        return number_room_empty;
    }

    public void setNumber_room_empty(int number_room_empty) {
        this.number_room_empty = number_room_empty;
    }

    public int getNumber_people() {
        return number_people;
    }

    public void setNumber_people(int number_people) {
        this.number_people = number_people;
    }
}
