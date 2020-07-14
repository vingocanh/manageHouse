package com.example.managehouse.Model;

public class Thongkekhutro {

    private int total_price, number_room, number_people;

    public Thongkekhutro(int total_price, int number_room, int number_people) {
        this.total_price = total_price;
        this.number_room = number_room;
        this.number_people = number_people;
    }

    public int getTotal_price() {
        return total_price;
    }

    public void setTotal_price(int total_price) {
        this.total_price = total_price;
    }

    public int getNumber_room() {
        return number_room;
    }

    public void setNumber_room(int number_room) {
        this.number_room = number_room;
    }

    public int getNumber_people() {
        return number_people;
    }

    public void setNumber_people(int number_people) {
        this.number_people = number_people;
    }
}
