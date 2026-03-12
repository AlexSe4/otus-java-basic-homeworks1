package ru.otus.java.basic.homeworks.HW4;

public class Box {
    private final int width, height, depth;
    private String color, item;
    private boolean isOpen;

    public Box(int width, int height, int length, String color) {
        this.width = width;
        this.height = height;
        this.depth = length;
        this.color = color;
        this.isOpen = false;
        this.item = null;
    }

    public void open() {
        if (isOpen == false) {
            isOpen = true;
            System.out.println("Коробка открыта.");
        } else {
            System.out.println("Коробка уже открыта.");
        }
    }

    public void close() {
        if (isOpen) {
            isOpen = false;
            System.out.println("Коробка закрыта.");
        } else {
            System.out.println("Коробка уже закрыта.");
        }
    }

    public void repaint(String newColor) {
        this.color = newColor;
        System.out.println("Коробка перекрашена в цвет: " + newColor);
    }


    public void printInfo() {
        System.out.println("Коробка: размеры [" + width + "x" + height + "x" + depth + "], цвет: " + color + ", состояние: " + (isOpen ? "открыта" : "закрыта") + ", внутри: " + (item == null ? "отсутствует" : item));
    }


    public void putItem(String newItem) {
        if (isOpen == false) {
            System.out.println("Нельзя положить предмет: коробка закрыта. Откройте её, чтобы положить предмет.");
        } else if (item != null) {
            System.out.println("Нельзя положить предмет: в коробке уже есть предмет " + item);
        } else {
            item = newItem;
            System.out.println("Предмет \"" + newItem + "\" помещён в коробку.");
        }
    }
    public void replaceItem(String newItem) {
        if (isOpen == false) {
            System.out.println("Нельзя заменить предмет");
        } else  {
            String oldItem = item;
            item = newItem;
            System.out.println("Предмет \"" + oldItem + "\" заменён на \"" + newItem + "\"");
        }
    }

    public void takeOutItem() {
        if (isOpen == false) {
            System.out.println("Нельзя взять предмет: коробка закрыта.");
        } else if (item == null) {
            System.out.println("Нельзя взять предмет: коробка пуста.");
        } else {
            System.out.println("Предмет \"" + item + "\" вынут из коробки.");
            item = null;
        }
    }
}
