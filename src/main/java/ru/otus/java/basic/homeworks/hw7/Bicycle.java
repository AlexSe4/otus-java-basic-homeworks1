package ru.otus.java.basic.homeworks.hw7;

public class Bicycle implements Transport {
    private Person driver;
    private int riderEnergy;
    private static final int MAX_RIDER_ENERGY = 100;
    private static final double ENERGY_CONSUMPTION = 1.5;

    public Bicycle() {
        this.riderEnergy = MAX_RIDER_ENERGY;
    }

    @Override
    public String getTransportName() {
        return "Велосипед";
    }

    @Override
    public void setDriver(Person person) {
        this.driver = person;
    }

    @Override
    public boolean move(double distance, Terrain terrain) {
        if (driver == null) {
            System.out.println("Нет водителя на велосипеде");
            return false;
        }

        if (terrain == Terrain.MARSH) {
            System.out.println("Велосипед не может ехать по болоту");
            return false;
        }

        double neededEnergy = distance * ENERGY_CONSUMPTION;
        if (riderEnergy < neededEnergy) {
            System.out.println("У водителя недостаточно сил. Нужно " + neededEnergy + ", осталось " + riderEnergy);
            return false;
        }

        riderEnergy -= neededEnergy;
        // Используем toString() вместо getTitle()
        System.out.println(driver.getName() + " на велосипеде проехал " + distance +
                " км по " + terrain.toString() + ". У водителя осталось сил: " + riderEnergy);
        return true;
    }

    public void getInfo() {
        System.out.println("Энергия водителя велосипеда: " + riderEnergy);
    }
}