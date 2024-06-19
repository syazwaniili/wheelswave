package com.example.lab_rest.model;

public class Car {
    private int id;
    private String category;
    private String seats;
    private double price;
    private int mileage;
    private String manufacturer;
    private String model;
    private String year;
    private String image;
    private String status;

    public Car() {
    }

    public Car(int id, String category, String seats, double price, int mileage, String manufacturer, String model, String year, String image, String status) {
        this.id = id;
        this.category = category;
        this.seats = seats;
        this.price = price;
        this.mileage = mileage;
        this.manufacturer = manufacturer;
        this.model = model;
        this.year = year;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", seats='" + seats + '\'' +
                ", price='" + price + '\'' +
                ", mileage='" + mileage + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", model='" + model + '\'' +
                ", year='" + year + '\'' +
                ", image='" + image + '\'' +
                ", status'" + status + '\'' +
                '}';
    }
}