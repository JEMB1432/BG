package jemb.bistrogurmand.Controllers;

import jemb.bistrogurmand.DbConection.DatabaseConnection;
import jemb.bistrogurmand.utils.TableRestaurant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TableController {
    public List<TableRestaurant> getTablesRestaurants() {
        Connection conn = null;
        ResultSet response = null;
        List<TableRestaurant> tables = new ArrayList<>();

        try{
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM TableRestaurant ORDER BY ID_Table";
            response = conn.createStatement().executeQuery(sql);

            while (response.next()) {
                String ID_Table = response.getString("ID_Table");
                Integer NumberTable = response.getInt("NumberTable");
                Integer NumberSeats = response.getInt("NumberSeats");
                String State = response.getString("State");
                String Location  = response.getString("Location");

                tables.add(new TableRestaurant(ID_Table, NumberTable, NumberSeats, State, Location));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener la lista de Tablas: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (response != null) response.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar ResultSet: " + e.getMessage());
                e.printStackTrace();
            }
            DatabaseConnection.closeConnection(conn);
        }
        return tables;
    }

    public boolean updateTableRestaurant(TableRestaurant table) {
        String sql = "UPDATE TABLERESTAURANT SET NUMBERTABLE = ?, " +
                "NUMBERSEATS = ?, STATE = ?, LOCATION = ? WHERE ID_Table = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, table.getNumberTable());
            ps.setInt(2, table.getNumberSeats());
            ps.setString(3, table.getState());
            ps.setString(4, table.getLocation());
            ps.setString(5, table.getID_Table());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertTableRestaurant(TableRestaurant table) {
        String sql = "INSERT INTO TABLERESTAURANT (NUMBERTABLE, NUMBERSEATS, STATE, LOCATION)" +
                "VALUES (?, ?, ?, ?)";
        try{
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, table.getNumberTable());
            ps.setInt(2, table.getNumberSeats());
            ps.setString(3, table.getState());
            ps.setString(4, table.getLocation());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
