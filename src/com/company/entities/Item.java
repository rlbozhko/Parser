package com.company.entities;


import java.util.Objects;

public class Item implements Comparable {
   private String name;
   private String price;


    public Item()   {
    }

    public Item(String name, String price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Item{" +
                " name='" + name + '\'' +
                ", price='" + price + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Item other = (Item) obj;
        return Objects.equals(this.name, other.name)
                && Objects.equals(this.price, other.price);
    }


    @Override
    public int compareTo(Object obj) {

        if (this == obj) {
            return 0;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return 1;
        }
        final Item other = (Item) obj;
        int last = this.name.compareTo(other.name);
        return last == 0 ? this.price.compareTo(other.price) : last;

    }
}
