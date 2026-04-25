package ru.otus.java.basic.homeworks.hw23.app;

public class Item {
    private Long id;
    private String name;
    private int price;

    public Item(Long id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    @Override
    public String toString() {
        return "Item{id=" + id + ", name='" + name + "', price=" + price + "}";
    }
}
