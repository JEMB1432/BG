package jemb.bistrogurmand.utils.Modals;


import java.time.LocalDate;
import java.time.LocalTime;

public class AssignedTable {
    private int tableNumber;
    private String location;
    private String shift;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate date;
    private boolean favorite;

    // Constructor
    public AssignedTable(int tableNumber, String location, String shift,
                         LocalTime startTime, LocalTime endTime, LocalDate date, boolean favorite) {
        this.tableNumber = tableNumber;
        this.location = location;
        this.shift = shift;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        this.favorite = favorite;
    }

    // Getters
    public int getTableNumber() { return tableNumber; }
    public String getLocation() { return location; }
    public String getShift() { return shift; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public LocalDate getDate() { return date; }
    public boolean isFavorite() { return favorite; }
}

