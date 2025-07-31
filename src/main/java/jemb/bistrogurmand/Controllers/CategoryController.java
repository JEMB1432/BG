package jemb.bistrogurmand.Controllers;

import jemb.bistrogurmand.DbConection.DatabaseConnection;
import jemb.bistrogurmand.utils.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryController {

    public List<Category> getCategories(){
        List<Category> categories = new ArrayList<Category>();
        String sql = "SELECT ID_CATEGORY, NAME, STATE_CATEGORY FROM CATEGORY WHERE STATE_CATEGORY = 1";
        try{
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                categories.add(new Category(rs.getInt("ID_CATEGORY"), rs.getString("NAME"), rs.getString("STATE_CATEGORY")));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return categories;
    }

    public boolean addCategory(String name, String state) {
        String sql = "INSERT INTO CATEGORY (NAME, STATE_CATEGORY) VALUES (?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, name);
            ps.setString(2, state);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static void main(String[] args) {
        CategoryController c = new CategoryController();
        List<Category> categories = c.getCategories();
        for (Category cat : categories) {
            System.out.println(cat.getName());
        }
    }
}
