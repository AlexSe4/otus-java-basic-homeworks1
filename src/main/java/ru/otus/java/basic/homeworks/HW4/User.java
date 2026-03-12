package ru.otus.java.basic.homeworks.HW4;

public class User {
    //основные характеритистики
    String surname;
    String name;
    String patronymic;
    int birthYear;
    String email;


    public User(String surname, String name, String patronymic, int birthYear, String email) {
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
        this.birthYear = birthYear;
        this.email = email;
    }


    public void printInfo() {
        System.out.println("ФИО: " + surname + " " + name + " " + patronymic);
        System.out.println("Год рождения: " + birthYear);
        System.out.println("e-mail: " + email);
    }

    public boolean isOlderThan40() {
        int currentYear = 2025;
        int age = currentYear - birthYear;
        return age > 40;
    }
}
