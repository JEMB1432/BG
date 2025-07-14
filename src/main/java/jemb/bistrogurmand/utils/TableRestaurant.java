package jemb.bistrogurmand.utils;

public class TableRestaurant {
    private String ID_Table;
    private Integer NumberTable;
    private Integer NumberSeats;
    private String State;
    private String Location;

    public TableRestaurant(String ID_Table, Integer NumberTable, Integer NumberSeats, String State, String Location) {
        this.ID_Table = ID_Table;
        this.NumberTable = NumberTable;
        this.NumberSeats = NumberSeats;
        this.State = State;
        this.Location = Location;
    }

    public String getID_Table() {
        return ID_Table;
    }

    public void setID_Table(String ID_Table) {
        this.ID_Table = ID_Table;
    }

    public Integer getNumberTable() {
        return NumberTable;
    }

    public void setNumberTable(Integer numberTable) {
        NumberTable = numberTable;
    }

    public Integer getNumberSeats() {
        return NumberSeats;
    }

    public void setNumberSeats(Integer numberSeats) {
        NumberSeats = numberSeats;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }
}
