package ru.otus.java.basic.homeworks.hw7;

public class ATV implements Transport {
    private Person driver;
    private double fuel;
    private static final double FUEL_CONSUMPTION = 0.5;
    private static final double MAX_FUEL = 100;

    public ATV() {
        this.fuel = MAX_FUEL;
    }

    @Override
    public String getTransportName() {
        return "Вездеход";
    }

    @Override
    public void setDriver(Person person) {
        this.driver = person;
    }

    @Override
    public boolean move(double distance, Terrain terrain) {
        if (driver == null) {
            System.out.println("Нет водителя в вездеходе");
            return false;
        }

        double neededFuel = distance * FUEL_CONSUMPTION;
        if (fuel < neededFuel) {
            System.out.println("Недостаточно бензина. Нужно " + neededFuel + ", осталось " + fuel);
            return false;
        }

        fuel -= neededFuel;
        System.out.println(driver.getName() + " на вездеходе проехал " + distance +
                " км по " + terrain.toString() + ". Осталось бензина: " + fuel);
        return true;
    }

    public void getInfo() {
        System.out.println("Количество топлива у вездехода: " + fuel);
    }

}
