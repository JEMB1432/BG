package jemb.bistrogurmand.Controllers;

import jemb.bistrogurmand.DbConection.DatabaseConnection;
import jemb.bistrogurmand.utils.PlanificationRestaurant;
import jemb.bistrogurmand.utils.TableRestaurant;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlanificationController {
    public List<PlanificationRestaurant> getPlanificationRestaurants() {
        Connection conn = null;
        ResultSet response = null;
        List<PlanificationRestaurant> tables = new ArrayList<>();

        try{
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM PlanificationRestaurant ORDER BY ID_Assignment";
            response = conn.createStatement().executeQuery(sql);

            while (response.next()) {
                Integer ID_Assignment = response.getInt("ID_Assignment");
                Integer ID_Employee = response.getInt("ID_Employee");
                Integer ID_Table = response.getInt("ID_Table");
                String StartTime = response.getString("StartTime");

                tables.add(new PlanificationRestaurant(ID_Assignment,ID_Employee,ID_Table,StartTime));
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
}