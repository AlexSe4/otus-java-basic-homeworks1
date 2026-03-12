package ru.otus.java.basic.homeworks.HW4;

public class MainApp {
    public static void main(String[] args) {
        // Создание массива из 10 пользователей
        User[] users = new User[10];


        users[0] = new User("Холодный", "Павел", "Петрович", 1945, "holod@mail.ru");
        users[1] = new User("Потный", "Петр", "Геннадьевич", 1985, "genna85@mail.ru");
        users[2] = new User("Сидорова", "Анна", "Сергеевна", 1993, "sidorova93@mail.ru");
        users[3] = new User("Веселый", "Алексей", "Сергеевич", 1987, "sm872@mail.ru");
        users[4] = new User("Ночная", "Лариса", "Сергеевна", 2001, "nn2001@mail.ru");
        users[5] = new User("Великая", "Любовь", "Ивановна", 1988, "love88@mail.ru");
        users[6] = new User("Светлова", "Татьяна", "Александровна", 1999, "vasileva@mail.ru");
        users[7] = new User("Быков", "Денис", "Петрович", 1980, "newuser99@mail.ru");
        users[8] = new User("Денисова", "Ольга", "Олеговна", 2005, "denisova@mail.ru");
        users[9] = new User("Петров", "Владимир", "Петрович", 1966, "petrov@mail.ru");


        System.out.println("Пользователи старше 40 лет:");
        for (int i = 0; i < users.length; i++) {
            if (users[i].isOlderThan40()) {
                users[i].printInfo();
            }
        }

    System.out.println("Работа с коробкой");
        Box myBox = new Box(30, 20, 10, "красный");
        myBox.printInfo(); //Информация о коробке
        myBox.putItem("мяч"); // попытка положить предмет в закрытую коробку
        myBox.open();  //Открываем коробку
        myBox.putItem("мяч"); // положили предмет
        myBox.close();
        myBox.open();
        myBox.putItem("мяч");  //
        myBox.replaceItem("кольцо"); //поменять предмет
        myBox.close();      // Закрыть коробку
        myBox.repaint("синий"); //Перекрасить коробку
        myBox.close();      // Закрыть коробку
        myBox.takeOutItem(); // Попытка вытянуть предмет из закрытой коробки
        myBox.open();       // Открыть коробку
        myBox.takeOutItem(); // Забрать предмет
        myBox.close();      // Закрыть коробку
        myBox.printInfo(); //Финальная информация о коробке
    }
}

