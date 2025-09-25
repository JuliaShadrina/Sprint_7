package ru.yandex.praktikum.scooter.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import com.github.javafaker.Faker;

import static io.restassured.RestAssured.given;

public class Courier {
    private final String baseUri;
    private final String courierEndpoint;
    private final String courierLoginEndpoint;

    private final Faker faker = new Faker();

    // Константы для тестовых данных
    private static final String INVALID_USERNAME = "snikers";
    private static final String INVALID_PASSWORD = "01010101010";
    private static final String EMPTY_STRING = "";

    public Courier(String baseUri, String courierEndpoint, String courierLoginEndpoint) {
        this.baseUri = baseUri;
        this.courierEndpoint = courierEndpoint;
        this.courierLoginEndpoint = courierLoginEndpoint;
    }

    // Методы для генерации тестовых данных
    public CourierAuthorization createValidCourierData() {
        return new CourierAuthorization(
                faker.name().username(),
                faker.internet().password(4, 8, false, false),
                faker.name().firstName()
        );
    }

    public CourierAuthorization createCourierDataWithoutPassword() {
        return new CourierAuthorization(
                faker.name().username(),
                null,
                faker.name().firstName()
        );
    }

    public CourierLoginData createValidLoginData() {
        return new CourierLoginData(
                faker.name().username(),
                faker.internet().password(4, 8, false, false)
        );
    }

    public CourierLoginData createLoginDataWithInvalidLogin() {
        return new CourierLoginData(INVALID_USERNAME, faker.internet().password(4, 8, false, false));
    }

    public CourierLoginData createLoginDataWithInvalidPassword() {
        return new CourierLoginData(faker.name().username(), INVALID_PASSWORD);
    }

    public CourierLoginData createLoginDataWithEmptyLogin() {
        return new CourierLoginData(EMPTY_STRING, faker.internet().password(4, 8, false, false));
    }

    public CourierLoginData createLoginDataWithEmptyPassword() {
        return new CourierLoginData(faker.name().username(), EMPTY_STRING);
    }

    // Универсальный метод для выполнения API запросов
    private Response executeApiRequest(String endpoint, Object body, String method) {
        var request = given()
                .header("Content-type", "application/json");

        if (body != null) {
            request.body(body);
        }

        switch (method.toUpperCase()) {
            case "POST":
                return request.post(endpoint);
            case "GET":
                return request.get(endpoint);
            case "DELETE":
                return request.delete(endpoint);
            case "PUT":
                return request.put(endpoint);
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }
    }

    // API методы
    @Step("Создание курьера")
    public Response createCourier(CourierAuthorization courierData) {
        return executeApiRequest(courierEndpoint, courierData, "POST");
    }

    @Step("Создание курьера с двумя полями вместо трёх")
    public Response createCourierWithTwoFields() {
        CourierAuthorizationWithoutField requestBody = new CourierAuthorizationWithoutField(
                faker.name().username(),
                faker.name().firstName()
        );
        return executeApiRequest(courierEndpoint, requestBody, "POST");
    }

    @Step("Авторизация курьера")
    public Response loginCourier(CourierLoginData loginData) {
        return executeApiRequest(courierLoginEndpoint, loginData, "POST");
    }

    @Step("Получение ID курьера")
    public int getCourierId(CourierLoginData loginData) {
        Response response = loginCourier(loginData);
        return response.then().extract().path("id");
    }

    @Step("Удаление курьера по ID")
    public Response deleteCourier(int courierId) {
        String deleteEndpoint = courierEndpoint + "/" + courierId;
        return executeApiRequest(deleteEndpoint, null, "DELETE");
    }

    // Специализированные методы для различных сценариев авторизации
    @Step("Авторизация с неверным логином")
    public Response loginWithInvalidLogin() {
        return loginCourier(createLoginDataWithInvalidLogin());
    }

    @Step("Авторизация с неверным паролем")
    public Response loginWithInvalidPassword() {
        return loginCourier(createLoginDataWithInvalidPassword());
    }

    @Step("Авторизация с пустым логином")
    public Response loginWithEmptyLogin() {
        return loginCourier(createLoginDataWithEmptyLogin());
    }

    @Step("Авторизация с пустым паролем")
    public Response loginWithEmptyPassword() {
        return loginCourier(createLoginDataWithEmptyPassword());
    }
}