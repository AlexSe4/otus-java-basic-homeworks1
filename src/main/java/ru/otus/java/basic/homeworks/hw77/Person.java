package ru.otus.java.basic.homeworks.hw77;

public class Person {
    private String name;
    private Transport currentTransport;

    public Person(String name) {
        this.name = name;
        this.currentTransport = null;
    }

    public String getName() {
        return name;
    }

    public void sitOn(Transport transport) {
        this.currentTransport = transport;
        transport.setDriver(this);
        System.out.println(name + " сел на " + transport.getTransportName());
    }

    public void standUp() {
        if (currentTransport != null) {
            currentTransport.setDriver(null);
            System.out.println(name + " встал с " + currentTransport.getTransportName());
            currentTransport = null;
        } else {
            System.out.println(name + " и так не на транспорте");
        }
    }

    public boolean move(double distance, Terrain terrain) {
        if (currentTransport != null) {
            return currentTransport.move(distance, terrain);
        } else {

            System.out.println(name + " идет пешком " + distance + " км по " + terrain.toString());
            return true;
        }
    }
}
