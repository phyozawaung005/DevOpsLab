package com.napier.devops;

import java.sql.*;
import java.util.ArrayList;

public class App {

    private Connection con = null;

    public void connect(String location, int delay) {
        try {
            // Load Database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i = 0; i < retries; ++i) {
            System.out.println("Connecting to database...");
            try {
                // Wait for db to start
                Thread.sleep(delay);

                // Connect to database (new MySQL format)
                con = DriverManager.getConnection(
                        "jdbc:mysql://" + location + "/employees?allowPublicKeyRetrieval=true&useSSL=false",
                        "root", "example");
                System.out.println("Successfully connected");
                break;
            } catch (SQLException sqle) {
                System.out.println("Failed to connect to database attempt " + i);
                System.out.println(sqle.getMessage());
            } catch (InterruptedException ie) {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    public void disconnect() {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public Department getDepartment(String name) {
        // Dummy example for integration setup
        Department dept = new Department();
        dept.dept_name = name;
        return dept;
    }

    public ArrayList<Employee> getSalariesByDepartment(Department dept) {
        // Dummy method for testing
        ArrayList<Employee> employees = new ArrayList<>();
        return employees;
    }

    public void printSalaries(ArrayList<Employee> employees) {
        System.out.println("Printing employee salaries...");
    }

    public static void main(String[] args) {
        App a = new App();

        if (args.length < 1) {
            a.connect("localhost:33060", 30000);
        } else {
            a.connect(args[0], Integer.parseInt(args[1]));
        }

        Department dept = a.getDepartment("Development");
        ArrayList<Employee> employees = a.getSalariesByDepartment(dept);

        a.printSalaries(employees);
        a.disconnect();
    }
}
