package ru.otus.java.basic.homeworks.hw9;

import java.util.ArrayList;
import java.util.List;


public class Employee {
    private final String name;
    private final int age;

    public Employee(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "Employee{name='" + name + "', age=" + age + "}";
    }
}

 class ListTasks {

    // 1. Метод, создающий ArrayList с последовательными значениями от min до max
    public static ArrayList<Integer> createList(int min, int max) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = min; i <= max; i++) {
            list.add(i);
        }
        return list;
    }

    // 2. Метод, суммирующий элементы списка, которые больше 5
    public static int sumFive(List<Integer> numbers) {
        int sum = 0;
        for (int num : numbers) {
            if (num > 5) {
                sum += num;
            }
        }
        return sum;
    }

    // 3. Метод, переписывающий каждую ячейку списка указанным числом
    public static void fillAll(List<Integer> list, int number) {
        for (int i = 0; i < list.size(); i++) {
            list.set(i, number);
        }
    }

    // 4. Метод, увеличивающий каждый элемент списка на указанное число
    public static void addToAll(List<Integer> list, int delta) {
        for (int i = 0; i < list.size(); i++) {
            list.set(i, list.get(i) + delta);
        }
    }

    // 5. Метод, возвращающий список имён сотрудников
    public static List<String> getNames(List<Employee> employees) {
        List<String> names = new ArrayList<>();
        for (Employee emp : employees) {
            names.add(emp.getName());
        }
        return names;
    }

    // 6. Метод, фильтрующий сотрудников по минимальному возрасту
    public static List<Employee> filterMinAge(List<Employee> employees, int minAge) {
        List<Employee> result = new ArrayList<>();
        for (Employee emp : employees) {
            if (emp.getAge() >= minAge) {
                result.add(emp);
            }
        }
        return result;
    }

    // 7. Метод, проверяющий, превышает ли средний возраст заданный порог
    public static boolean filterMiddleAge(List<Employee> employees, double threshold) {
        if (employees == null || employees.isEmpty()) {
            return false;
        }
        int sum = 0;
        for (Employee emp : employees) {
            sum += emp.getAge();
        }
        double average = (double) sum / employees.size();
        return average > threshold;
    }

    // 8. Метод, возвращающий самого молодого сотрудника
    public static Employee filterYoungest(List<Employee> employees) {
        if (employees == null || employees.isEmpty()) {
            return null;
        }
        Employee youngest = employees.get(0);
        for (Employee emp : employees) {
            if (emp.getAge() < youngest.getAge()) {
                youngest = emp;
            }
        }
        return youngest;
    }


    public static void main(String[] args) {
        System.out.println("Проверка:");

        System.out.println("1. Диапазон значений (3, 7):  " + createList(3, 7));

        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        System.out.println("2. Сумма чисел, которые больше 5: (" + numbers + "):   " +
                sumFive(numbers));

        ArrayList<Integer> list1 = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        System.out.println("3. Список до изменений:(" + list1 + ", 9) ");
        fillAll(list1, 9);
        System.out.println("Список после изменений: " + list1);


        ArrayList<Integer> list2 = new ArrayList<>(List.of(1, 2, 3, 4));
        System.out.println("4. Список до изменений(" + list2 + ", 2)   ");
        addToAll(list2, 2);
        System.out.println(" Список после изменений: " + list2);


        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee("Иванов", 24));
        employees.add(new Employee("Сидорова", 25));
        employees.add(new Employee("Петров", 20));
        employees.add(new Employee("Данилова", 26));
        employees.add(new Employee("Кузнецов", 58));

        System.out.println("Проверка");
        System.out.println("Список до изменений:");
        for (Employee emp : employees) {
            System.out.println("  " + emp);
        }

        System.out.println("5. Список фамилий сотрудников " + getNames(employees));

        System.out.println("6. filterMinAge(>=25) ");
        List<Employee> filtered = filterMinAge(employees, 25);
        for (Employee emp : filtered) {
            System.out.println("   " + emp);
        }

        System.out.println("7. filterMiddleAge(24)  " +
                filterMiddleAge(employees, 24));
        System.out.println("   filterMiddleAge(26)  " +
                filterMiddleAge(employees, 26));


        System.out.println("8. Самый молодой сотрудник() " + filterYoungest(employees));
    }
}



