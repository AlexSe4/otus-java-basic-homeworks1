package ru.otus.java.basic.homeworks.hw5;

public abstract class Animal {
    protected String name;
    protected double runSpeed;        // скорость бега, м/с
    protected double swimSpeed;       // скорость плавания, м/с
    protected double stamina;         // выносливость
    protected boolean tired;          // уровень усталости

    public Animal(String name, double runSpeed, double swimSpeed, double stamina) {
        this.name = name;
        this.runSpeed = runSpeed;
        this.swimSpeed = swimSpeed;
        this.stamina = stamina;
        this.tired = false;
    }

    // Бег (расход выносливости 1 ед/м для всех животных) - общий показатель
    public double run(int distance) {
        if (tired) {
            System.out.println(name + " уже устал и не может бежать.");
            return -1;
        }

        double requiredStamina = distance * 1.0; // 1 ед на метр
        if (stamina >= requiredStamina) {
            stamina -= requiredStamina;
            double time = distance / runSpeed;
            System.out.println(name + " пробежал " + distance + " м за " + time + " с. Остаток выносливости: " + stamina);
            return time;
        } else {
            tired = true;
            System.out.println(name + " не хватило выносливости на бег дистанцией " + distance + " м. Животное устало.");
            return -1;
        }
    }

    //Плавание - общий показатель
    public abstract double swim(int distance);
    // Информация о состоянии животного
    public void info() {
        System.out.println("Имя: " + name);
        System.out.println("Скорость бега: " + runSpeed + " м/с");
        System.out.println("Скорость плавания: " + swimSpeed + " м/с");
        System.out.println("Текущая выносливость: " + stamina);
        System.out.println("Усталость? " + (tired ? "Да" : "Нет"));
    }

    public String getName() { return name; }
    public double getRunSpeed() { return runSpeed; }
    public double getSwimSpeed() { return swimSpeed; }
    public double getStamina() { return stamina; }
    public boolean isTired() { return tired; }
}
