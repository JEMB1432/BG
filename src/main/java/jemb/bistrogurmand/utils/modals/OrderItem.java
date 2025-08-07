package jemb.bistrogurmand.utils.Modals;

import jemb.bistrogurmand.utils.Product;
import javafx.beans.property.*;

public class OrderItem {
    private final int productId;
    private final String productName;
    private final double unitPrice;
    private final SimpleIntegerProperty quantity;

    public OrderItem(int productId, String productName, double unitPrice, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = new SimpleIntegerProperty(quantity);
    }

    // Getters
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public double getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity.get(); }
    public double getTotalPrice() { return unitPrice * quantity.get(); }

    // Property accessors
    public SimpleIntegerProperty quantityProperty() { return quantity; }

    // Métodos para modificar cantidad
    public void incrementQuantity() { quantity.set(quantity.get() + 1); }
    public void decrementQuantity() {
        if (quantity.get() > 1) quantity.set(quantity.get() - 1);
    }
}