package jemb.bistrogurmand.utils;

public class CorrectionRequest {
    private int correctionId;
    private int employeeId;
    private int saleId;
    private int productId;
    private String productName;
    private int newAmount;

    public CorrectionRequest(int correctionId, int employeeId, int saleId,
                             int productId, String productName, int newAmount) {
        this.correctionId = correctionId;
        this.employeeId = employeeId;
        this.saleId = saleId;
        this.productId = productId;
        this.productName = productName;
        this.newAmount = newAmount;
    }

    // Getters
    public int getCorrectionId() { return correctionId; }
    public int getEmployeeId() { return employeeId; }
    public int getSaleId() { return saleId; }
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getNewAmount() { return newAmount; }
}