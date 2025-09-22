package ru.yandex.praktikum.scooter;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.yandex.praktikum.scooter.api.*;

import static org.hamcrest.Matchers.equalTo;

public class CourierCreationTest {

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
    public void returnsTheCorrectBodyTest(){
        Response response = courier.creatingCourier();
        response.then().assertThat().body("ok", equalTo(true))
                .and()
                .statusCode(201);
    }

    @Test
    @DisplayName("Creating identical couriers")
    @Description("Должна вернуться ошибка \"409 conflict\" , тело ответа \"Этот логин уже используется. Попробуйте другой\"")
    public void identialCouriersTest(){
        courier.creatingCourier();
        Response secondCourier = courier.creatingCourier();
        secondCourier.then().assertThat().body("message", equalTo("Этот логин уже используется. Попробуйте другой."))
                .and()
                .statusCode(409);
    }

    @Test
    @DisplayName("Bad request, no one field")
    @Description("Возвращается ошибка \"400 Bad request\" при отсутствии одного из обязательных полей в теле запроса")
    public void courierTwoFieldsBodyTest(){
        Response response = courier.creatingTwoFieldsCourier();
        response.then().assertThat().body("message",equalTo("Недостаточно данных для создания учетной записи"))
                .and()
                .statusCode(400);
    }


    @After
    public void tearDown(){
        courier.deleteIdCourier();
    }
}
