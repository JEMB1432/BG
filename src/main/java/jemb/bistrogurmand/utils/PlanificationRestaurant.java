package jemb.bistrogurmand.utils;

public class PlanificationRestaurant {
    private Integer ID_Assignment;
    private Integer ID_Employee;
    private Integer ID_Table;
    private String StartTime;

    public PlanificationRestaurant(Integer ID_Assignment, Integer ID_Employee,Integer ID_Table, String startTime) {
        this.ID_Assignment = ID_Assignment;
        this.ID_Employee = ID_Employee;
        ID_Table = ID_Table;
        StartTime = startTime;
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

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }
}
