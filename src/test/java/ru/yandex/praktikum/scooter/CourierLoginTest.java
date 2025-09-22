package ru.yandex.praktikum.scooter;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.yandex.praktikum.scooter.api.*;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CourierLoginTest {
    private final String BASE_URI = "https://qa-scooter.praktikum-services.ru/";
    private final String END_POINT_COURIER = "/api/v1/courier";
    private final String END_POINT_COURIER_LOGIN = "/api/v1/courier/login";

    Courier courier = new Courier(BASE_URI, END_POINT_COURIER, END_POINT_COURIER_LOGIN);

    @Before
    public void before(){
        courier.setUp();
    }

    @Test
    @DisplayName("Returns the correct body and statusCode")
    @Description("Возвращается корректное тело ответа и статус код при успешном запросе")
    public void returnsTheCorrectBody(){
        courier.creatingCourier();
        Response response = courier.authorizationCourier();
        response.then().assertThat().body("id", notNullValue())
                .and()
                .statusCode(200);
    }

    @Test
    @DisplayName("Authorization error with invalid login")
    @Description("Возвращается ошибка \"404 Not found\" при не верном логине")
    public void authorizationErrorLogin(){
        courier.creatingCourier();
        Response response = courier.authorizationCourierInvalidLogin();
        response.then().assertThat().body("message", equalTo("Учетная запись не найдена"))
                .and()
                .statusCode(404);
    }

    @Test
    @DisplayName("Authorization error with invalid password")
    @Description("Возвращается ошибка \"404 Not found\" при не верном пароле")
    public void authorizationErrorPassword(){
        courier.creatingCourier();
        Response response = courier.authorizationCourierInvalidPassword();
        response.then().assertThat().body("message", equalTo("Учетная запись не найдена"))
                .and()
                .statusCode(404);
    }

    @Test
    @DisplayName("Authorization error with no login")
    @Description("Возвращается ошибка \"400 Bad Request\" когда нет логина")
    public void authorizationErrorNoOneFieldLogin(){
        courier.creatingCourier();
        Response response = courier.authorizationCourierOneFieldLogin();
        response.then().assertThat().body("message", equalTo("Недостаточно данных для входа"))
                .and()
                .statusCode(400);
    }

    @Test
    @DisplayName("Authorization error with no password")
    @Description("Возвращается ошибка \"400 Bad Request\" когда нет пароля")
    public void authorizationErrorNoOneFieldPassword(){
        courier.creatingCourier();
        Response response = courier.authorizationCourierOneFieldPassword();
        response.then().assertThat().body("message", equalTo("Недостаточно данных для входа"))
                .and()
                .statusCode(400);
    }

    @After
    public void tearDown(){
        courier.deleteIdCourier();
    }
}
