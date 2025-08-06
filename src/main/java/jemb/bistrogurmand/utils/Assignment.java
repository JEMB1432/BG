package jemb.bistrogurmand.utils;

public class Assignment {
    private int id;
    private String tableAssign;
    private String employeeAssign;
    private String dateAssign;
    private String timeStartAssign;
    private String timeEndAssign;
    private String shiftAssign;

    public Assignment() {}

    public Assignment(int id,String tableAssign, String employeeAssign,
                      String dateAssign, String timeStartAssign,
                      String timeEndAssign, String shiftAssign) {
        this.id = id;
        this.tableAssign = tableAssign;
        this.employeeAssign = employeeAssign;
        this.dateAssign = dateAssign;
        this.timeStartAssign = timeStartAssign;
        this.timeEndAssign = timeEndAssign;
        this.shiftAssign = shiftAssign;
    }

    public String getTableAssign() {
        return tableAssign;
    }

    public void setTableAssign(String tableAssign) {
        this.tableAssign = tableAssign;
    }

    public String getEmployeeAssign() {
        return employeeAssign;
    }

    public void setEmployeeAssign(String employeeAssign) {
        this.employeeAssign = employeeAssign;
    }

    public String getDateAssign() {
        return dateAssign;
    }

    public void setDateAssign(String dateAssign) {
        this.dateAssign = dateAssign;
    }

    public String getTimeStartAssign() {
        return timeStartAssign;
    }

    public void setTimeStartAssign(String timeStartAssign) {
        this.timeStartAssign = timeStartAssign;
    }

    public String getTimeEndAssign() {
        return timeEndAssign;
    }

    public void setTimeEndAssign(String timeEndAssign) {
        this.timeEndAssign = timeEndAssign;
    }

    public String getShiftAssign() {
        return shiftAssign;
    }

    public void setShiftAssign(String shiftAssign) {
        this.shiftAssign = shiftAssign;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
