package ru.otus.java.basic.homeworks.hw10;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.List;

public class PhoneBook {
    private Map<String, Set<String>> catalog = new HashMap<>();

    public void add(String name, String phoneNumber) {
        Set<String> numbers = catalog.get(name);
        if (numbers == null) {
            numbers = new HashSet<>();
            numbers.add(phoneNumber);
            catalog.put(name, numbers);
        } else {
            numbers.add(phoneNumber);
        }
    }
    public List<String> find(String name) {
        Set<String> numbers = catalog.get(name);
        if (numbers == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(numbers);
    }

    public String findPerson(String name) {
        Set<String> numbers = catalog.get(name);
        if (numbers == null) {
            return "в списке отсутствует   " + name;
        } else {
            return "найден, телефоны:" + numbers;
        }
    }

    public boolean containsPhoneNumber(String phoneNumber) {
        for (Set<String> numbers : catalog.values()) {
            if (numbers.contains(phoneNumber)) {
                return true;
            }
        }
        return false;
    }
    public void printAll() {
        for (Map.Entry<String, Set<String>> entry : catalog.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
    public String getPhoneStatus (String phoneNumber){
        if (containsPhoneNumber(phoneNumber)) {
            return "Номер  " + phoneNumber + "  найден в справочнике";
        } else {
            return "Номер  " + phoneNumber + "  отсутствует в спраочнике";
        }
    }



    public static void main(String[] args) {
        PhoneBook book = new PhoneBook();
        book.add("Холодный Сергей Иванович", "987-651-43-11");
        book.add("Хитров Ян Иванович", "987-641-42-22");
        book.add("Мечтательный Илья Иванович", "999-888-77-55");
        book.add("Занудный Нил Иванович", "123-23-34");
        book.add("Великий Ив Иванович", "222-333-11-22");
        book.add("Удача Александр Иванович", "444-333-22-11");
        book.add("Одинокий Станислав Иванович", "456-567-22-11");
        book.add("Покинутый Иван Иванович", "123-234-77-77");
        book.add("Покинутый Илья Иванович", "987-567-33-33");


        System.out.println("Номер Холодного: " + book.find("Холодный Сергей Иванович"));
        System.out.println("Номер Занудного: " + book.find("Занудный Нил Иванович"));
        System.out.println("Номер Покинутого: " + book.findPerson("Покинутый Геннадий Иванович"));

        System.out.println("Есть ли номер 222-333-11-22? " + book.getPhoneStatus("222-333-11-22"));
        System.out.println("Есть ли номер 123-111-77-77? " + book.getPhoneStatus("123-111-77-77"));

        System.out.println("Содержимое справочника (HashMap):");
        book.printAll();
    }
}
