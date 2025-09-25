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
import static org.hamcrest.Matchers.notNullValue;

public class CourierLoginTest {
    private final BaseURL baseURL = new BaseURL();
    private final String END_POINT_COURIER = "/api/v1/courier";
    private final String END_POINT_COURIER_LOGIN = "/api/v1/courier/login";

    private Courier courier;
    private CourierAuthorization courierData;
    private CourierLoginData validLoginData;
    private int createdCourierId;

    @Before
    public void setUp() {
        baseURL.setUp();
        courier = new Courier(RestAssured.baseURI, END_POINT_COURIER, END_POINT_COURIER_LOGIN);

        // Создаем курьера перед каждым тестом (кроме тестов с несуществующими данными)
        courierData = courier.createValidCourierData();
        Response createResponse = courier.createCourier(courierData);

        // Сохраняем валидные данные для авторизации
        validLoginData = new CourierLoginData(courierData.getLogin(), courierData.getPassword());

        // Получаем ID созданного курьера для последующего удаления
        if (createResponse.statusCode() == 201) {
            createdCourierId = courier.getCourierId(validLoginData);
        }
    }

    @Test
    @DisplayName("Returns the correct body and statusCode")
    @Description("Возвращается корректное тело ответа и статус код при успешном запросе")
    public void returnsTheCorrectBody() {
        Response response = courier.loginCourier(validLoginData);

        // Сначала проверяем статус код, затем тело ответа
        response.then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Authorization error with invalid login")
    @Description("Возвращается ошибка \"404 Not found\" при неверном логине")
    public void authorizationErrorLogin() {
        CourierLoginData invalidLoginData = courier.createLoginDataWithInvalidLogin();

        Response response = courier.loginCourier(invalidLoginData);

        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Authorization error with invalid password")
    @Description("Возвращается ошибка \"404 Not found\" при неверном пароле")
    public void authorizationErrorPassword() {
        CourierLoginData invalidPasswordData = courier.createLoginDataWithInvalidPassword();

        Response response = courier.loginCourier(invalidPasswordData);

        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Authorization error with no login")
    @Description("Возвращается ошибка \"400 Bad Request\" когда нет логина")
    public void authorizationErrorNoLogin() {
        CourierLoginData emptyLoginData = courier.createLoginDataWithEmptyLogin();

        Response response = courier.loginCourier(emptyLoginData);

        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Authorization error with no password")
    @Description("Возвращается ошибка \"400 Bad Request\" когда нет пароля")
    public void authorizationErrorNoPassword() {
        CourierLoginData emptyPasswordData = courier.createLoginDataWithEmptyPassword();

        Response response = courier.loginCourier(emptyPasswordData);

        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Authorization error with non-existent user")
    @Description("Возвращается ошибка \"404 Not found\" при попытке авторизации несуществующего пользователя")
    public void authorizationErrorNonExistentUser() {
        CourierLoginData nonExistentData = courier.createValidLoginData();

        Response response = courier.loginCourier(nonExistentData);

        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @After
    public void tearDown() {
        // Удаляем курьера только если он был успешно создан
        if (createdCourierId > 0) {
            try {
                Response deleteResponse = courier.deleteCourier(createdCourierId);
                // Можно добавить проверку успешного удаления если нужно
                // deleteResponse.then().statusCode(200);
            } catch (Exception e) {
                System.out.println("Failed to delete courier: " + e.getMessage());
            }
        }
    }
}