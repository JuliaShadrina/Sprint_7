package ru.yandex.praktikum.scooter;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.scooter.api.*;

import static org.hamcrest.Matchers.equalTo;

public class CourierCreationTest {

    private final BaseURL baseURL = new BaseURL();
    private final String END_POINT_COURIER = "/api/v1/courier";
    private final String END_POINT_COURIER_LOGIN = "/api/v1/courier/login";

    private Courier courier;
    private CourierAuthorization courierData;
    private CourierLoginData loginData;

    @Before
    public void setUp() {
        baseURL.setUp();
        courier = new Courier(RestAssured.baseURI, END_POINT_COURIER, END_POINT_COURIER_LOGIN);
    }

    @Test
    @DisplayName("Returns the correct body and statusCode")
    @Description("Возвращается корректное тело ответа и статус код при успешном запросе")
    public void returnsTheCorrectBodyTest() {
        // Создаем данные для курьера
        courierData = courier.createValidCourierData();
        loginData = new CourierLoginData(courierData.getLogin(), courierData.getPassword());

        Response response = courier.createCourier(courierData);

        // Сначала проверяем статус код, затем тело ответа
        response.then()
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Creating identical couriers")
    @Description("Должна вернуться ошибка \"409 conflict\" , тело ответа \"Этот логин уже используется. Попробуйте другой\"")
    public void identicalCouriersTest() {
        // Создаем и регистрируем первого курьера
        courierData = courier.createValidCourierData();
        loginData = new CourierLoginData(courierData.getLogin(), courierData.getPassword());

        courier.createCourier(courierData);

        // Пытаемся создать второго курьера с такими же данными
        Response secondCourier = courier.createCourier(courierData);

        // Сначала проверяем статус код, затем тело ответа
        secondCourier.then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("Bad request - missing login field")
    @Description("Возвращается ошибка \"400 Bad request\" при отсутствии логина в теле запроса")
    public void courierWithoutLoginTest() {
        // Создаем данные курьера без логина
        courierData = new CourierAuthorization(null, "password123", "FirstName");

        Response response = courier.createCourier(courierData);

        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Bad request - missing password field")
    @Description("Возвращается ошибка \"400 Bad request\" при отсутствии пароля в теле запроса")
    public void courierWithoutPasswordTest() {
        // Создаем данные курьера без пароля
        courierData = new CourierAuthorization("testlogin", null, "FirstName");

        Response response = courier.createCourier(courierData);

        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Bad request - only two fields instead of three")
    @Description("Возвращается ошибка \"400 Bad request\" при наличии только двух полей вместо трёх обязательных")
    public void courierWithTwoFieldsTest() {
        Response response = courier.createCourierWithTwoFields();

        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @After
    public void tearDown() {
        // Удаляем курьера только если он был создан и у нас есть данные для авторизации
        if (loginData != null) {
            try {
                int courierId = courier.getCourierId(loginData);
                if (courierId > 0) {
                    courier.deleteCourier(courierId);
                }
            } catch (Exception e) {
                // Игнорируем ошибки удаления (например, если курьер не был создан)
                System.out.println("Courier deletion failed (possibly not created): " + e.getMessage());
            }
        }
    }
}
