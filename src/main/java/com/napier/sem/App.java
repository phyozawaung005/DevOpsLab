package com.napier.sem;

import java.sql.*;
import java.util.ArrayList;

public class App
{
    /**
     * Connection to MySQL database.
     */
    private Connection con = null;

    /**
     * Connect to the MySQL database.
     */
    public void connect()
    {
        try
        {
            // Load Database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Connect to database
            con = DriverManager.getConnection(
                    "jdbc:mysql://db:3306/employees?useSSL=false&allowPublicKeyRetrieval=true",
                    "root",
                    "example");
        }
        catch (Exception e)
        {
            System.out.println("Error connecting to database: " + e.getMessage());
        }
    }

    /**
     * Disconnect from the MySQL database.
     */
    public void disconnect()
    {
        if (con != null)
        {
            try
            {
                con.close();
            }
            catch (Exception e)
            {
                System.out.println("Error disconnecting: " + e.getMessage());
            }
        }
    }

    /**
     * Gets all employees’ salaries for a given role
     * @param role The job title to filter on
     * @return List of employees with that role
     */
    public ArrayList<Employee> getSalariesByRole(String role)
    {
        ArrayList<Employee> employees = new ArrayList<>();
        try
        {
            // Use PreparedStatement to prevent SQL injection
            String strSelect =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, " +
                            "salaries.salary, titles.title " +
                            "FROM employees, salaries, titles " +
                            "WHERE employees.emp_no = salaries.emp_no " +
                            "AND employees.emp_no = titles.emp_no " +
                            "AND salaries.to_date = '9999-01-01' " +
                            "AND titles.to_date = '9999-01-01' " +
                            "AND titles.title = ? " +
                            "ORDER BY employees.emp_no ASC";

            PreparedStatement pstmt = con.prepareStatement(strSelect);
            pstmt.setString(1, role);

            ResultSet rset = pstmt.executeQuery();

            // Loop through results
            while (rset.next())
            {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
                emp.salary = rset.getInt("salary");
                emp.title = rset.getString("title");
                employees.add(emp);
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salaries by role");
        }
        return employees;
    }

    /**
     * Displays a list of employees’ salaries by role
     */
    public void displaySalariesByRole(ArrayList<Employee> employees)
    {
        if (employees == null || employees.isEmpty())
        {
            System.out.println("No employees found for this role.");
            return;
        }

        for (Employee emp : employees)
        {
            System.out.println(
                    "ID: " + emp.emp_no + " | " +
                            "Name: " + emp.first_name + " " + emp.last_name + " | " +
                            "Title: " + emp.title + " | " +
                            "Salary: " + emp.salary
            );
        }
    }

    /**
     * Main method for testing
     */
    public static void main(String[] args)
    {
        App app = new App();
        app.connect();

        // Example: Get all Engineers
        ArrayList<Employee> engineers = app.getSalariesByRole("Engineer");
        app.displaySalariesByRole(engineers);

        app.disconnect();
    }
}
