package ru.otus.java.basic.homeworks.hw6;

public class MainApp {
public static void main(String[] args) {
    Plate plate = new Plate(100);

    // Создаём массив котов
    Cat[] cats = {
            new Cat("Степа", 60),
            new Cat("Ночь", 25),
            new Cat("Блонд", 40),
            new Cat("Феликс", 20)
    };


    for (Cat cat : cats) {
        cat.eat(plate);
    }


    System.out.println("Состояние котов после еды:");
    for (Cat cat : cats) {
        System.out.println(cat.getName() + "(аппетит: " + cat.getAppetite() + ") - " + (cat.isFull() ? "сыт" : "голоден"));
    }

    // Дополнительно: сколько еды осталось в тарелке
    System.out.println("Осталось еды в тарелке: " + plate.getCurrentFood() + " из " + plate.getMaxFood());
}

}


