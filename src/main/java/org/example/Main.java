package org.example;

import java.sql.*;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    String url = "jdbc:postgresql://localhost:5432/Assignment 3";
    String username = "postgres";
    String password = "postgres";
    public static void main(String[] args) {
        //make sure the table exists
        new Main().createTable();

        //scanner for console input
        Scanner sc = new Scanner(System.in);
        String input = "";

        //main input loop
        while (!Objects.equals(input, "Exit")) {
            System.out.println("Enter command (Add, Update, Delete, List, or Exit):");
            input = sc.nextLine();

            if (Objects.equals(input, "Exit")) {
                break;
            }

            //determine what action to take
            if (Objects.equals(input, "Add")) {
                //getting values to add
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
                    //add new entry
                    new Main().addStudent(firstName,lastName, email,date);
                }  catch (Exception e) {
                    System.out.println("Invalid enrollment date");
                }
            } else if (Objects.equals(input, "Delete")) {
                //getting value for deletion
                System.out.println("Enter student ID:");
                input = sc.nextLine();
                try {
                    int studentID = Integer.parseInt(input);
                    //delete student
                    new Main().deleteStudent(studentID);
                } catch (Exception e) {
                    System.out.println("Invalid student ID");
                }
            } else if (Objects.equals(input, "Update")) {
                //getting value for updating
                System.out.println("Enter student ID:");
                input = sc.nextLine();
                try {
                    int studentID = Integer.parseInt(input);
                    System.out.println("Enter new email address:");
                    String newEmail = sc.nextLine();
                    //update email
                    new Main().updateStudentEmail(studentID, newEmail);
                } catch (Exception e) {
                    System.out.println("Invalid student ID");
                }
            } else if (Objects.equals(input, "List")) {
                //list all entries
                new Main().getAllStudents();
            } else {
                //for all other inputs
                System.out.println("Invalid command");
            }
        }
    }

    /**
     * Prints the information about every student to the console.
     */
    public void getAllStudents() {
        //header
        System.out.println("ID\tName\t\tEmail\t\t\t\t\tEnrollment");
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);
            Statement stmt = conn.createStatement();
            //run query
            ResultSet rs = stmt.executeQuery("select * from students");
            //loop through results and print their info
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

    /**
     * Adds a new student to the table.
     * @param first_name First name of the student.
     * @param last_name Last name of the student.
     * @param email Email address of the student.
     * @param enrollment_date Date the student enrolled.
     */
    public void addStudent(String first_name, String last_name, String email, Date enrollment_date) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);
            //prepared statement formatted to insert the appropriate values
            PreparedStatement pstmt = conn.prepareStatement("insert into students (first_name, last_name, email, enrollment_date) values (?, ?, ?, ?)");
            //set values
            pstmt.setString(1, first_name);
            pstmt.setString(2, last_name);
            pstmt.setString(3, email);
            pstmt.setDate(4, enrollment_date);
            //execute statement
            pstmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates the email of a specific student in the table.
     * @param student_id ID of the student.
     * @param email New email for the student.
     */
    public void updateStudentEmail(int student_id, String email) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);
            //prepared statement formatted to update the email for a certain student ID
            PreparedStatement pstmt = conn.prepareStatement(
                    "update students set email=? where student_id=?");
            //set values
            pstmt.setString(1, email);
            pstmt.setInt(2, student_id);
            //execute statement
            pstmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes a student from the table.
     * @param student_id ID of the student to be deleted.
     */
    public void deleteStudent(int student_id) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);
            //prepared statement formatted for deleting a student
            PreparedStatement pstmt = conn.prepareStatement(
                    "delete from students where student_id=?");
            //set value
            pstmt.setInt(1, student_id);
            //execute statement
            pstmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Ensures that the table exists, and creates the table as well as
     * populates it if it does not yet exist.
     */
    public void createTable() {
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);
            Statement stmt = conn.createStatement();

            //check if the table exists by searching the database for it.
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "students", null);
            boolean table_exists = tables.next();

            if (!table_exists) {
                //create the table
                stmt.executeUpdate("CREATE TABLE public.students ( " +
                        "student_id serial," +
                        "first_name text NOT NULL," +
                        "last_name text NOT NULL," +
                        "email text NOT NULL," +
                        "enrollment_date date," +
                        "PRIMARY KEY (student_id)," +
                        "UNIQUE (email) )");
                //populate the table with students
                addStudent("John", "Doe", "john.doe@example.com", new Date(123,8,1));
                addStudent("Jane", "Smith", "jane.smith@example.com", new Date(123,8,1));
                addStudent("Jim", "Beam", "jim.beam@example.com", new Date(123,8,2));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
