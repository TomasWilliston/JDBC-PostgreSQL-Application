package org.example;

import java.sql.*;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    String url = "jdbc:postgresql://localhost:5432/Assignment 3";
    String username = "postgres";
    String password = "postgres";
    public static void main(String[] args) {
        new Main().createTable();

        Scanner sc = new Scanner(System.in);
        String input = "";

        while (!Objects.equals(input, "Exit")) {
            System.out.println("Enter command (Add, Update, Delete, List, or Exit):");
            input = sc.nextLine();

            if (Objects.equals(input, "Exit")) {
                break;
            }

            if (Objects.equals(input, "Add")) {
                System.out.println("Enter first name:");
                String firstName = sc.nextLine();
                System.out.println("Enter last name:");
                String lastName = sc.nextLine();
                System.out.println("Enter email address:");
                String email = sc.nextLine();
                System.out.println("Enter enrollment date:");
                String enrollment = sc.nextLine();
                try {
                    Date date = java.sql.Date.valueOf(enrollment);
                    new Main().addStudent(firstName,lastName, email,date);
                }  catch (Exception e) {
                    System.out.println("Invalid enrollment date");
                }
            } else if (Objects.equals(input, "Delete")) {
                System.out.println("Enter student ID:");
                input = sc.nextLine();
                try {
                    int studentID = Integer.parseInt(input);
                            new Main().deleteStudent(studentID);
                } catch (Exception e) {
                    System.out.println("Invalid student ID");
                }
            } else if (Objects.equals(input, "Update")) {
                System.out.println("Enter student ID:");
                input = sc.nextLine();
                try {
                    int studentID = Integer.parseInt(input);
                    System.out.println("Enter new email address:");
                    String newEmail = sc.nextLine();
                    new Main().updateStudentEmail(studentID, newEmail);
                } catch (Exception e) {
                    System.out.println("Invalid student ID");
                }
            } else if (Objects.equals(input, "List")) {
                new Main().getAllStudents();
            } else {
                System.out.println("Invalid command");
            }
        }
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
            throw new RuntimeException(e);
        }
    }

    public void addStudent(String first_name, String last_name, String email, Date enrollment_date) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);
            PreparedStatement pstmt = conn.prepareStatement("insert into students (first_name, last_name, email, enrollment_date) values (?, ?, ?, ?)");
            pstmt.setString(1, first_name);
            pstmt.setString(2, last_name);
            pstmt.setString(3, email);
            pstmt.setDate(4, enrollment_date);
            pstmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateStudentEmail(int student_id, String email) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);
            PreparedStatement pstmt = conn.prepareStatement(
                    "update students set email=? where student_id=?");
            pstmt.setString(1, email);
            pstmt.setInt(2, student_id);
            pstmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteStudent(int student_id) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);
            PreparedStatement pstmt = conn.prepareStatement(
                    "delete from students where student_id=?");
            pstmt.setInt(1, student_id);
            pstmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void createTable() {
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);
            Statement stmt = conn.createStatement();


            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "students", null);
            boolean table_exists = tables.next();

            if (!table_exists) {
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS public.students ( " +
                        "student_id serial," +
                        "first_name text NOT NULL," +
                        "last_name text NOT NULL," +
                        "email text NOT NULL," +
                        "enrollment_date date," +
                        "PRIMARY KEY (student_id)," +
                        "UNIQUE (email) )");
                addStudent("John", "Doe", "john.doe@example.com", new Date(123,8,1));
                addStudent("Jane", "Smith", "jane.smith@example.com", new Date(123,8,1));
                addStudent("Jim", "Beam", "jim.beam@example.com", new Date(123,8,2));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
