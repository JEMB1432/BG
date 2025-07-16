package jemb.bistrogurmand.utils;

public class OrderRestaurant {
    private int ID_Correction;
    private int ID_Employee;
    private int ID_Sale;
    private int ID_Product;

    public OrderRestaurant(int ID_Correction, int ID_Employee, int ID_Sale, int ID_Product) {
        this.ID_Correction = ID_Correction;
        this.ID_Employee = ID_Employee;
        this.ID_Sale = ID_Sale;
        this.ID_Product = ID_Product;
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
}
