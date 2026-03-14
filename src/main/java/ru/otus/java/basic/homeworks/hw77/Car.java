package ru.otus.java.basic.homeworks.hw77;

public class Car implements Transport {
    private Person driver;
    private double fuel;
    private static final double FUEL_RATE = 0.06;

    public Car(double fuel) {
        this.fuel = fuel;
    }
    @Override
    public String getTransportName() {
        return "Машина";
    }

    @Override
    public void setDriver(Person person) {
        this.driver = person;
    }

    @Override
    public boolean move(double distance, Terrain terrain) {
        if (driver == null) {
            System.out.println("Водителя нет в машине");
            return false;
        }

        if (fuel <= 0) {
            System.out.println("Недостаточно бензина.");
            return false;
        }

        if (terrain == Terrain.DENSE_FOREST) {
            System.out.println("Машина не может ехать по густому лесу");
            return false;
        }

        if (terrain == Terrain.MARSH) {
            System.out.println("Машина не может ехать по болоту");
            return false;
        }

        double neededFuel = distance * FUEL_RATE;
        if (fuel < neededFuel) {
            System.out.println("Недостаточно бензина для поездки. Нужно: " + neededFuel + ", осталось: " + fuel);
            return false;
        }

        fuel -= neededFuel;
        System.out.println(driver.getName() + " на машине проехал " + distance +
                " километров по " + terrain.toString());
        return true;
    }

    public void getInfo() {
        System.out.println("Количество топлива у машины: " + fuel);
    }
}
