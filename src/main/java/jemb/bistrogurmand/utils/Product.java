package jemb.bistrogurmand.utils;

public class Product {
    private String name;
    private double price;
    private String available;
    private String urlImage;
    private String description;

    public Product(String name, double price, String available, String urlImage, String description) {
        this.name = name;
        this.price = price;
        this.available = available;
        this.urlImage = urlImage;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
