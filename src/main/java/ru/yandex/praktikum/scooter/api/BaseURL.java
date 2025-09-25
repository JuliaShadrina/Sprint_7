package ru.yandex.praktikum.scooter.api;

import io.restassured.RestAssured;

public class BaseURL {
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }
}
