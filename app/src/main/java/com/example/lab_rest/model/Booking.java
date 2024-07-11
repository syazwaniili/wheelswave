package com.example.lab_rest.model;

import java.util.Date;

public class Booking {

    private int bookingID;
    private String pickup_date;
    private String return_date;
    private String pickup_location;
    private String return_location;
    private String booking_status;
    private double totalPrice;
    private int user_id;
    private int admin_id;
    private int car_id;

    public Booking() {
    }

    public Booking(int bookingID, String pickup_date, String return_date, String pickup_location, String return_location, String booking_status, double totalPrice, int user_id , int admin_id, int car_id) {
        this.bookingID = bookingID;
        this.pickup_date = pickup_date;
        this.return_date = return_date;
        this.pickup_location = pickup_location;
        this.return_location = return_location;
        this.booking_status = booking_status;
        this.totalPrice = totalPrice;
        this.user_id = user_id;
        this.admin_id = admin_id;
        this.car_id = car_id;
    }

    public int getBookingID() {
        return bookingID;
    }

    public void setBookingID(int bookingID) {
        this.bookingID = bookingID;
    }

    public String getPickup_date() {
        return pickup_date;
    }

    public void setPickup_date(String pickup_date) {
        this.pickup_date = pickup_date;
    }

    public String getReturn_date() {
        return return_date;
    }

    public void setReturn_date(String return_date) {
        this.return_date = return_date;
    }

    public String getPickup_location() {
        return pickup_location;
    }

    public void setPickup_location(String pickup_location) {
        this.pickup_location = pickup_location;
    }

    public String getReturn_location() {
        return return_location;
    }

    public void setReturn_location(String return_location) {
        this.return_location = return_location;
    }

    public String getBooking_status() {
        return booking_status;
    }

    public void setBooking_status(String booking_status) {
        this.booking_status = booking_status;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(int admin_id) {
        this.admin_id = admin_id;
    }

    public int getCar_id() {
        return car_id;
    }

    public void setCar_id(int car_id) {
        this.car_id = car_id;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingID=" + bookingID +
                ", pickup_date=" + pickup_date +
                ", return_date=" + return_date +
                ", pickup_location='" + pickup_location + '\'' +
                ", return_location='" + return_location + '\'' +
                ", booking_status='" + booking_status + '\'' +
                ", totalPrice=" + totalPrice +
                ", user_id=" + user_id +
                ", admin_id=" + admin_id +
                ", car_id=" + car_id +
                '}';
    }
}
