package ru.otus.java.basic.homeworks.hw77;

public class Horse implements Transport {
    private Person driver;
    private double energy;
    private static final double MAX_ENERGY = 100;
    private static final double ENERGY_RATE = 2;

    public Horse() {
        this.energy = MAX_ENERGY;
    }

    @Override
    public String getTransportName() {
        return "Лошадь";
    }

    @Override
    public void setDriver(Person person) {
        this.driver = person;
    }

    @Override
    public boolean move(double distance, Terrain terrain) {
        if (driver == null) {
            System.out.println("Нет всадника на лошади");
            return false;
        }

        if (terrain == Terrain.MARSH) {
            System.out.println("Лошадь не может идти по болоту");
            return false;
        }

        double neededEnergy = distance * ENERGY_RATE;
        if (energy < neededEnergy) {
            System.out.println("У лошади недостаточно сил. Нужно " + neededEnergy + ", осталось " + energy);
            return false;
        }

        energy -= neededEnergy;
        System.out.println(driver.getName() + " на лошади проехал " + distance +
                " км по " + terrain.toString() + ". Осталось сил у лошади: " + energy);
        return true;
    }
    public void getInfo() {
        System.out.println("Силы лошади: " + energy);
    }
}
