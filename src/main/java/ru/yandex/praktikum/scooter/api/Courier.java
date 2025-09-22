package ru.yandex.praktikum.scooter.api;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import com.github.javafaker.Faker;

import static io.restassured.RestAssured.given;

public class Courier {

    private final String BASE_URI;
    private final String END_POINT_COURIER;
    private final String END_POINT_COURIER_LOGIN;

    Faker faker = new Faker();
    String username = faker.name().username();
    String firstName = faker.name().firstName();
    String password = faker.internet().password(4, 8, false, false);
    String invalid_username = "snikers";
    String null_username = "";
    String invalid_password = "01010101010";
    String null_password = "";


    public Courier(String BASE_URI, String END_POINT_COURIER, String END_POINT_COURIER_LOGIN){
        this.BASE_URI = BASE_URI;
        this.END_POINT_COURIER = END_POINT_COURIER;
        this.END_POINT_COURIER_LOGIN = END_POINT_COURIER_LOGIN;
    }

    public void setUp(){
        RestAssured.baseURI = BASE_URI;
    }

    @Step("Два поля в теле запроса вместо трёх обязательных")
    public Response creatingTwoFieldsCourier(){
        CourierAuthorizationWithoutField json = new CourierAuthorizationWithoutField(username, password);
        Response twoFieldsResponse = given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post(END_POINT_COURIER);
        return twoFieldsResponse;
    }

    @Step("Создание курьера")
    public Response creatingCourier(){
        CourierAuthorization json = new CourierAuthorization(username, password, firstName);
        Response response = given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post(END_POINT_COURIER);
        return response;
    }

    @Step("Получение по Id")
    public int getIdCourier(){
        CourierLoginData json = new CourierLoginData(username,password);
        CourierLogin loginID = given()
                .header("Content-type", "application/json")
                .body(json)
                .post(END_POINT_COURIER_LOGIN)
                .as(CourierLogin.class);
        return loginID.getId();
    }

    @Step("Удаление по Id")
    public void deleteIdCourier(){
        Response response = given()
                .header("Conrent-type", "application/json")
                .when()
                .delete(END_POINT_COURIER + "/" + getIdCourier());
    }

    @Step("Авторизация")
    public Response authorizationCourier(){
        CourierLoginData json = new CourierLoginData(username, password);
        Response response = given()
                .header("Content-type","application/json")
                .body(json)
                .when()
                .post(END_POINT_COURIER_LOGIN);
        return response;
    }

    @Step("Неверный логин")
    public Response authorizationCourierInvalidLogin(){
        CourierLoginData json = new CourierLoginData(invalid_username, password);
        Response response = given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post(END_POINT_COURIER_LOGIN);
        return response;
    }

    @Step("Пустая строка в логине")
    public Response authorizationCourierOneFieldLogin(){
        CourierLoginData json = new CourierLoginData(null_username, password);
        Response response = given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post(END_POINT_COURIER_LOGIN);
        return response;
    }

    @Step("Пустая строка в пароле")
    public Response authorizationCourierOneFieldPassword(){
        CourierLoginData json = new CourierLoginData(username, null_password);
        Response response = given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post(END_POINT_COURIER_LOGIN);
        return response;
    }

    @Step("Неверный пароль")
    public Response authorizationCourierInvalidPassword(){
        CourierLoginData json = new CourierLoginData(username, invalid_password);
        Response response = given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post(END_POINT_COURIER_LOGIN);
        return response;
    }


}
