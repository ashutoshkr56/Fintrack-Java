
package com.fintrack.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBConnectionTest {

    public static void main(String[] args) {

        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DATABASE()")) {

            if (rs.next()) {
                System.out.println("Database connected successfully!");
                System.out.println("Database name: " + rs.getString(1));
            }

        } catch (Exception e) {
            System.out.println("Database connection failed!");
            System.out.println(e.getMessage());
        }
    }
}
