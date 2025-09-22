package ru.yandex.praktikum.scooter.api;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class Order {
    private final String BASE_URI;
    private final String END_POINT_ORDER;
    private final String END_POINT_ORDER_CANCEL;

    public Order(String BASE_URI, String END_POINT_ORDER, String END_POINT_ORDER_CANCEL){

        this.BASE_URI = BASE_URI;
        this.END_POINT_ORDER = END_POINT_ORDER;
        this.END_POINT_ORDER_CANCEL = END_POINT_ORDER_CANCEL;

    }


    public void setUp(){
        RestAssured.baseURI = BASE_URI;
    }

    @Step("Получение списка заказов")
    public Response getOrderList(){
        Response response = given()
                .header("Content-type", "application/json")
                .when()
                .get(END_POINT_ORDER);
        return response;
    }

}

