package jemb.bistrogurmand.utils.Modals;

import javafx.beans.property.*;

public class OrderItem {
    private final Product product;
    private final SimpleIntegerProperty quantity;
    private final SimpleStringProperty productName;
    private final SimpleDoubleProperty price;
    private final SimpleStringProperty observation = new SimpleStringProperty("");

    public OrderItem(Product product, int initialQty) {
        this.product     = product;
        this.quantity    = new SimpleIntegerProperty(initialQty);
        this.productName = new SimpleStringProperty(product.getName());
        this.price       = new SimpleDoubleProperty(product.getPrice());
    }

    public Product getProduct()               { return product; }
    public int getQuantity()                  { return quantity.get(); }
    public void setQuantity(int qty)          { quantity.set(qty); }
    public SimpleIntegerProperty quantityProperty() { return quantity; }

    public String getProductName()            { return productName.get(); }
    public SimpleStringProperty productNameProperty() { return productName; }

    public double getPrice()                  { return price.get(); }
    public SimpleDoubleProperty priceProperty() { return price; }

    public String getObservation()            { return observation.get(); }
    public void setObservation(String obs)    { observation.set(obs); }
    public SimpleStringProperty observationProperty() { return observation; }
}