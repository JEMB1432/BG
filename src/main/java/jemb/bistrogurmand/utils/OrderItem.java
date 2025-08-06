package jemb.bistrogurmand.utils;

public class OrderItem {
    private final int productId;
    private final String productName;
    private final double unitPrice;
    private int quantity;

    public OrderItem(int productId, String productName, double unitPrice, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    // Getters
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public double getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }
    public double getTotalPrice() { return unitPrice * quantity; }

    // Métodos para modificar cantidad
    public void incrementQuantity() { quantity++; }
    public void decrementQuantity() { if (quantity > 1) quantity--; }
}