package ru.yandex.praktikum.scooter;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex.praktikum.scooter.api.*;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderCreationTest {

    private final BaseURL baseURL = new BaseURL();
    private final String END_POINT_ORDER = "/api/v1/orders";
    private final String END_POINT_ORDER_CANCEL = "/api/v1/orders/cancel";

    private String[] color;
    private OrderData orderData;
    private int trackNumber;
    private Order orderApiClient;

    public OrderCreationTest(String[] color) {
        this.color = color;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {new String[]{"BLACK"}},
                {new String[]{"GREY"}},
                {new String[]{"BLACK", "GREY"}},
                {new String[]{}} // Тест без выбора цвета
        });
    }

    @Before
    public void setUp() {
        baseURL.setUp();
        // Инициализируем API клиент для работы с заказами
        orderApiClient = new Order(RestAssured.baseURI, END_POINT_ORDER, END_POINT_ORDER_CANCEL);
        // Создаем тестовые данные заказа
        orderData = createOrderData();
    }

    private OrderData createOrderData() {
        OrderData order = new OrderData();
        order.setFirstName("Naruto");
        order.setLastName("Uchiha");
        order.setAddress("Konoha, 142 apt.");
        order.setMetroStation(4);
        order.setPhone("+7 800 355 35 35");
        order.setRentTime(5);
        order.setDeliveryDate("2020-06-06");
        order.setComment("Saske, come back to Konoha");
        order.setColor(color);
        return order;
    }

    @Test
    @DisplayName("Successful order creation when using different colors")
    @Description("Возвращается статус код \"201 Created\" и правильное тело ответа содержащее \"track\"")
    public void testCreateOrderWithDifferentColors() {
        // Создаем заказ через API клиент
        Response response = orderApiClient.createOrder(orderData);

        // Сначала проверяем статус код, затем тело ответа
        response.then()
                .statusCode(201)
                .body("track", notNullValue());

        // Сохраняем track number для последующего удаления заказа
        trackNumber = response.then().extract().path("track");
    }

    @After
    public void tearDown() {
        // Отменяем заказ только если он был успешно создан
        if (trackNumber > 0) {
            try {
                orderApiClient.cancelOrder(trackNumber);
            } catch (Exception e) {
                System.out.println("Failed to cancel order with track: " + trackNumber + ". Error: " + e.getMessage());
            }
        }
    }
}