package ru.yandex.praktikum.scooter.api;

public class CourierAuthorizationWithoutField {
    private String login;
    private String firstName;

    public CourierAuthorizationWithoutField(String login, String password){
        this.login = login;
        this.firstName = password;
    }

    public CourierAuthorizationWithoutField(){}

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

}

