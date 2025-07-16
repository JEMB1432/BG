package jemb.bistrogurmand.utils;

import jemb.bistrogurmand.Controllers.TableDAO;

import java.time.LocalDate;
import java.time.LocalTime;

public class PlanificationRestaurant {

    private Integer ID_Assignment;
    private Integer ID_Employee;
    private String employeeName; // Nuevo campo
    private Integer ID_Table;
    private int tableNumber;
    private LocalTime startTime;      // Hora de inicio
    private LocalTime endTime;        // Hora de fin
    private LocalDate dateAssig;      // Fecha de asignación

    private boolean favorite;         // 0 ó 1 en DB → boolean en Java
    private String shift;             // Turno
    private TableDAO.EmployeeDAO employee;
    private TableDAO table;

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public PlanificationRestaurant(){

    }
    // Constructor
    public PlanificationRestaurant(Integer ID_Assignment, Integer ID_Employee, Integer ID_Table,
                                   LocalTime startTime, LocalTime endTime, LocalDate dateAssig,
                                   boolean favorite, String shift) {
        this.ID_Assignment = ID_Assignment;
        this.ID_Employee = ID_Employee;
        this.ID_Table = ID_Table;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dateAssig = dateAssig;
        this.favorite = favorite;
        this.shift = shift;
    }


    public Integer getID_Assignment() {
        return ID_Assignment;
    }

    public void setID_Assignment(Integer ID_Assignment) {
        this.ID_Assignment = ID_Assignment;
    }

    public Integer getID_Employee() {
        return ID_Employee;
    }

    public void setID_Employee(Integer ID_Employee) {
        this.ID_Employee = ID_Employee;
    }

    public Integer getID_Table() {
        return ID_Table;
    }

    public void setID_Table(Integer ID_Table) {
        this.ID_Table = ID_Table;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public LocalDate getDateAssig() {
        return dateAssig;
    }

    public void setDateAssig(LocalDate dateAssig) {
        this.dateAssig = dateAssig;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public TableDAO.EmployeeDAO getEmployee() {
        return employee;
    }

    public void setEmployee(TableDAO.EmployeeDAO employee) {
        this.employee = employee;
    }

    public TableDAO getTable() {
        return table;
    }

    public void setTable(TableDAO table) {
        this.table = table;
    }
}