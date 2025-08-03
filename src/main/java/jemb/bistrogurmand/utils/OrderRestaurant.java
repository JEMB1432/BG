package jemb.bistrogurmand.utils;

import java.time.LocalDateTime;

public class OrderRestaurant {
    private int ID_Correction;
    private int ID_Employee;
    private int ID_Sale;
    private int ID_Product;
    private int Approved;
    private String EmployeeName;
    private String ProductName;
    private LocalDateTime SaleDate;

    public OrderRestaurant(int ID_Correction, int ID_Employee, int ID_Sale, int ID_Product, int Approved) {
        this.ID_Correction = ID_Correction;
        this.ID_Employee = ID_Employee;
        this.ID_Sale = ID_Sale;
        this.ID_Product = ID_Product;
        this.Approved=Approved;
    }

    public OrderRestaurant(int ID_Correction, int ID_Employee, String EmployeeName, int ID_Sale, int ID_Product, String ProductName, int State, LocalDateTime SaleDate) {
        this.ID_Correction = ID_Correction;
        this.ID_Employee = ID_Employee;
        this.EmployeeName=EmployeeName;
        this.ID_Sale = ID_Sale;
        this.ID_Product = ID_Product;
        this.ProductName=ProductName;
        this.Approved=State;
        this.SaleDate=SaleDate;
    }

    public int getID_Correction() {
        return ID_Correction;
    }

    public void setID_Correction(int ID_Correction) {
        this.ID_Correction = ID_Correction;
    }

    public int getID_Employee() {
        return ID_Employee;
    }

    public void setID_Employee(int ID_Employee) {
        this.ID_Employee = ID_Employee;
    }

    public int getID_Sale() {
        return ID_Sale;
    }

    public void setID_Sale(int ID_Sale) {
        this.ID_Sale = ID_Sale;
    }

    public int getID_Product() {
        return ID_Product;
    }

    public void setID_Product(int ID_Product) {
        this.ID_Product = ID_Product;

}

    public int getApproved() {
        return Approved;
    }

    public void setApproved(int approved) {
        Approved = approved;
    }

    public String getEmployeeName() {
        return EmployeeName;
    }

    public void setEmployeeName(String employeeName) {
        EmployeeName = employeeName;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public LocalDateTime getSaleDate() {
        return SaleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        SaleDate = saleDate;
    }
}
