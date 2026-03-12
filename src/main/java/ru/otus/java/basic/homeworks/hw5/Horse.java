package ru.otus.java.basic.homeworks.hw5;

class Horse extends Animal {
    public Horse(String name, double runSpeed, double swimSpeed, double stamina) {
        super(name, runSpeed, swimSpeed, stamina);
    }


    @Override
    public double swim(int distance) {
        if (tired) {
            System.out.println(name + " уже устал и не может плыть.");
            return -1;
        }
        double requiredStamina = distance * 4.0;
        {
            if (stamina >= requiredStamina) {
                stamina -= requiredStamina;
                double time = distance / swimSpeed;
                System.out.println(name + " проплыл " + distance + " м за " + time + " с. Остаток выносливости: " + stamina);
                return time;
            } else {
                tired = true;
                System.out.println(name + " не хватило выносливости на заплыв дистанцией " + distance + " м. Животное устало.");
                return -1;
            }
        }
    }
}