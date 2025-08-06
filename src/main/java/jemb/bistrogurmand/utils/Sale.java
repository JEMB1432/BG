package jemb.bistrogurmand.utils;

import java.util.Date;

public class Sale {
    int idSale;
    int idAssignment;
    int idEmployee;
    float total;
    Date saleDate;
    float rating;
    int status;

    public Sale(){}

    public Sale(int idSale, int idAssignment, int idEmployee, float total, Date saleDate, float rating, int status) {
        this.idSale = idSale;
        this.idAssignment = idAssignment;
        this.idEmployee = idEmployee;
        this.total = total;
        this.saleDate = saleDate;
        this.rating = rating;
        this.status = status;
    }

    public int getIdSale() {
        return idSale;
    }

    public void setIdSale(int idSale) {
        this.idSale = idSale;
    }

    public int getIdAssignment() {
        return idAssignment;
    }

    public void setIdAssignment(int idAssignment) {
        this.idAssignment = idAssignment;
    }

    public int getIdEmployee() {
        return idEmployee;
    }

    public void setIdEmployee(int idEmployee) {
        this.idEmployee = idEmployee;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
