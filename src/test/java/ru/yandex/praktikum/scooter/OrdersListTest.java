package ru.yandex.praktikum.scooter;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import ru.yandex.praktikum.scooter.api.*;

import static org.hamcrest.Matchers.notNullValue;

public class OrdersListTest {
    private final BaseURL baseURL = new BaseURL();;
    private final String ORDER_ENDPOINT = "/api/v1/orders";
    private final String CANCEL_ORDER_ENDPOINT = "/api/v1/orders/cancel";

    Order order = new Order(RestAssured.baseURI, ORDER_ENDPOINT, CANCEL_ORDER_ENDPOINT);

    @Before
    public void setUp() {
        baseURL.setUp(); // настройка базового URI
    }

    @Test
    @DisplayName("Получение списка заказов")
    @Description("Проверка, что список заказов не пустой и возвращается статус 200")
    public void getOrderListTest() {
        Response response = order.getOrderList();
        response.then().assertThat()
                .body("orders", notNullValue())
                .and()
                .statusCode(200);
    }
}
