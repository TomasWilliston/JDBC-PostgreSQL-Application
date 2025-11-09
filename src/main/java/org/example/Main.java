package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {
    String url = "jdbc:postgresql://localhost:5432/Assignment 3";
    String username = "postgres";
    String password = "postgres";
    public static void main(String[] args) {
        new Main().getAllStudents();
    }

    public void getAllStudents() {

        System.out.println("ID\tName\t\tEmail\t\t\t\t\tEnrollment");
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from students");
            while (rs.next()) {
                System.out.print(rs.getString("student_id") + "\t");
                System.out.print(rs.getString("first_name") + " ");
                System.out.print(rs.getString("last_name") + "\t");
                System.out.print(rs.getString("email") + "\t");
                System.out.println(rs.getString("enrollment_date"));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
