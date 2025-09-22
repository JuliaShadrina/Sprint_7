package ru.yandex.praktikum.scooter;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import ru.yandex.praktikum.scooter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderCreationTest {

    private String color;
    String requestBody = String.format("{"
            + "\"firstName\": \"Naruto\","
            + "\"lastName\": \"Uchiha\","
            + "\"address\": \"Konoha, 142 apt.\","
            + "\"metroStation\": 4,"
            + "\"phone\": \"+7 800 355 35 35\","
            + "\"rentTime\": 5,"
            + "\"deliveryDate\": \"2020-06-06\","
            + "\"comment\": \"Saske, come back to Konoha\","
            + "\"color\": [\"%s\"]"
            + "}", color);

    public OrderCreationTest(String color) {
        this.color = color;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"BLACK"},
                {"GREY"},
        });
    }

    @Before
    public void setUp(){
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    @Test
    @DisplayName("Successful order creation when using different colors")
    @Description("Возвращается статус код \"201 Created\" и правильное тело ответа содержащее \"track\"")
    public void testCreateOrderWithDifferentColors() {
        Response response = createOrder();
        response.then().assertThat().body("track", notNullValue())
                .and()
                .statusCode(201);
    }

    @Step("Создание заказа")
    public Response createOrder(){
        Response response = given()
                .header("Content-type", "application/json")
                .body(requestBody)
                .when()
                .post("/api/v1/orders");
        return response;
    }

    @Step("Получение трек номера заказа")
    public int trackOrder(){
        TrackOrder track = given()
                .header("Content-type", "application/json")
                .body(requestBody)
                .post("/api/v1/orders")
                .body()
                .as(TrackOrder.class);
        return track.getTrack();
    }

    @Step("Отмена заказа")
    public void orderRemoval(){
        Response response = given()
                .header("Content-type", "application/json")
                .when()
                .put("/api/v1/orders/cancel" + "/" + trackOrder());
    }

    @After
    public void tearDown(){
        orderRemoval();
    }
}
