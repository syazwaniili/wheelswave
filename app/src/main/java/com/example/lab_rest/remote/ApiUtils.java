package com.example.lab_rest.remote;

public class ApiUtils {

    // REST API server URL
    public static final String BASE_URL = "http://178.128.220.20/2022798613/api/";

    // return UserService instance
    public static UserService getUserService() {
        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }

    // return CarService instance
    public static CarService getCarService() {
        return RetrofitClient.getClient(BASE_URL).create(CarService.class);
    }

}