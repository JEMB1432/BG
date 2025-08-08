package jemb.bistrogurmand.utils;

import java.time.LocalDateTime;

public class SaleCorrectionSummary {
    private int saleId;
    private LocalDateTime saleDate;
    private int correctionCount;
    private double originalTotal;
    private double newTotal;
    private String status;

    public SaleCorrectionSummary(int saleId, LocalDateTime saleDate, int correctionCount,
                                 double originalTotal, double newTotal, String status) {
        this.saleId = saleId;
        this.saleDate = saleDate;
        this.correctionCount = correctionCount;
        this.originalTotal = originalTotal;
        this.newTotal = newTotal;
        this.status = status;
    }

    // Getters
    public int getSaleId() { return saleId; }
    public LocalDateTime getSaleDate() { return saleDate; }
    public int getCorrectionCount() { return correctionCount; }
    public double getOriginalTotal() { return originalTotal; }
    public double getNewTotal() { return newTotal; }
    public String getStatus() { return status; }
}