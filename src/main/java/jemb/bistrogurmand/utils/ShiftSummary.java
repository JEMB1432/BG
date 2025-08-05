package jemb.bistrogurmand.utils;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ShiftSummary {
    private final SimpleStringProperty shiftName;
    private final SimpleIntegerProperty activeWaiters;
    private final SimpleIntegerProperty serviceWaiters;
    private final SimpleIntegerProperty pendingOrders;

    public ShiftSummary(String shiftName, int serviceWaiters, int activeWaiters, int pendingOrders) {
        this.shiftName = new SimpleStringProperty(shiftName);
        this.activeWaiters = new SimpleIntegerProperty(activeWaiters);
        this.serviceWaiters = new SimpleIntegerProperty(serviceWaiters);
        this.pendingOrders = new SimpleIntegerProperty(pendingOrders);
    }

    // Getters para las propiedades (necesarios para TableView)
    public String getShiftName() {
        return shiftName.get();
    }
    public SimpleStringProperty shiftNameProperty() {
        return shiftName;
    }

    public int getActiveWaiters() {
        return activeWaiters.get();
    }
    public SimpleIntegerProperty activeWaitersProperty() {
        return activeWaiters;
    }

    public int getServiceWaiters() {
        return serviceWaiters.get();
    }
    public SimpleIntegerProperty serviceWaitersProperty() {
        return serviceWaiters;
    }

    public int getPendingOrders() {
        return pendingOrders.get();
    }
    public SimpleIntegerProperty pendingOrdersProperty() {
        return pendingOrders;
    }
}