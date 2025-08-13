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
            String sql = "SELECT ID_Table, NUMBERTABLE, NUMBERSEATS, STATE, LOCATION FROM TableRestaurant ORDER BY ID_Table";
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

    public int updateTableRestaurant(TableRestaurant table) {
        String sql = "UPDATE TABLERESTAURANT SET NUMBERTABLE = ?, " +
                "NUMBERSEATS = ?, STATE = ?, LOCATION = ? WHERE ID_Table = ?";

        String checkTable = "SELECT ID_Table FROM TableRestaurant WHERE NUMBERTABLE = ? AND ID_Table != ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            ps = conn.prepareStatement(checkTable);
            ps.setInt(1, table.getNumberTable());
            ps.setString(2, table.getID_Table());
            rs = ps.executeQuery();

            if (rs.next()) {
                return 2;
            }

            rs.close();
            ps.close();

            ps = conn.prepareStatement(sql);
            ps.setInt(1, table.getNumberTable());
            ps.setInt(2, table.getNumberSeats());
            ps.setString(3, table.getState());
            ps.setString(4, table.getLocation());
            ps.setString(5, table.getID_Table());

            int rowsAffected = ps.executeUpdate();
            conn.commit();

            return (rowsAffected > 0) ? 1 : 0;
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return 0;
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int insertTableRestaurant(TableRestaurant table) {
        String checkTableSql = "SELECT COUNT(*) FROM TABLERESTAURANT WHERE NUMBERTABLE = ?";

        String insertSql = "INSERT INTO TABLERESTAURANT (NUMBERTABLE, NUMBERSEATS, STATE, LOCATION) " +
                "VALUES (?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement checkPs = null;
        PreparedStatement insertPs = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            checkPs = conn.prepareStatement(checkTableSql);
            checkPs.setInt(1, table.getNumberTable());
            rs = checkPs.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return 2;
            }

            insertPs = conn.prepareStatement(insertSql);
            insertPs.setInt(1, table.getNumberTable());
            insertPs.setInt(2, table.getNumberSeats());
            insertPs.setString(3, table.getState());
            insertPs.setString(4, table.getLocation());

            int rowsAffected = insertPs.executeUpdate();
            conn.commit();

            return (rowsAffected > 0) ? 1 : 0;
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return 0;
        } finally {
            try {
                if (rs != null) rs.close();
                if (checkPs != null) checkPs.close();
                if (insertPs != null) insertPs.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
