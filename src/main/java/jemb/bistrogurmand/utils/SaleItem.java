package jemb.bistrogurmand.utils;

public class SaleItem {
    private final long productId;
    private final String productName;
    private final double price;
    private final int quantity;
    private final String observation;

    public SaleItem(long productId, String productName, double price, int quantity, String observation) {
        this.productId     = productId;
        this.productName   = productName;
        this.price         = price;
        this.quantity      = quantity;
        this.observation   = observation;
    }

    public long getProductId()     { return productId; }
    public String getProductName() { return productName; }
    public double getPrice()       { return price; }
    public int getQuantity()       { return quantity; }
    public String getObservation() { return observation; }
}
