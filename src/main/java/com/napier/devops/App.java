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

        int retries = 20;
        for (int i = 0; i < retries; ++i) {
            System.out.println("Connecting to database...");
            try {
                // Wait for db to start
                Thread.sleep(delay);

                // Connect to database (new MySQL format)
                con = DriverManager.getConnection(
                        "jdbc:mysql://" + location + "/employees?allowPublicKeyRetrieval=true&useSSL=false",
                        "root",
                        "example"
                );
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

    // Get a department by name
    public Department getDepartment(String name) {
        Department dept = null;
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT dept_no, dept_name FROM departments WHERE dept_name = ?"
            );
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                dept = new Department();
                dept.setDept_no(rs.getString("dept_no"));
                dept.setDept_name(rs.getString("dept_name"));
            }
        } catch (SQLException e) {
            System.out.println("Error getting department: " + e.getMessage());
        }
        return dept;
    }

    // Get employees in a department
    public ArrayList<Employee> getSalariesByDepartment(Department dept) {
        ArrayList<Employee> employees = new ArrayList<>();
        if (dept == null) return employees;

        try {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT e.emp_no, e.first_name, e.last_name, s.salary, t.title " +
                            "FROM employees e " +
                            "JOIN dept_emp de ON e.emp_no = de.emp_no " +
                            "JOIN salaries s ON e.emp_no = s.emp_no " +
                            "JOIN titles t ON e.emp_no = t.emp_no " +
                            "WHERE de.dept_no = ? AND s.to_date = '9999-01-01' AND t.to_date = '9999-01-01'"
            );
            stmt.setString(1, dept.getDept_no());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Employee emp = new Employee();
                emp.emp_no = rs.getInt("emp_no");
                emp.first_name = rs.getString("first_name");
                emp.last_name = rs.getString("last_name");
                emp.salary = rs.getDouble("salary");
                emp.title = rs.getString("title");
                employees.add(emp);
            }
        } catch (SQLException e) {
            System.out.println("Error getting employees: " + e.getMessage());
        }

        return employees;
    }

    // Add employee
    public void addEmployee(Employee emp) {
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "INSERT INTO employees (emp_no, first_name, last_name, birth_date, gender, hire_date) " +
                            "VALUES (?, ?, ?, '9999-01-01', 'M', '9999-01-01')"
            );
            stmt.setInt(1, emp.emp_no);
            stmt.setString(2, emp.first_name);
            stmt.setString(3, emp.last_name);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to add employee: " + e.getMessage());
        }
    }

    // Get employee by emp_no
    public Employee getEmployee(int emp_no) {
        Employee emp = null;
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT emp_no, first_name, last_name FROM employees WHERE emp_no = ?"
            );
            stmt.setInt(1, emp_no);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                emp = new Employee();
                emp.emp_no = rs.getInt("emp_no");
                emp.first_name = rs.getString("first_name");
                emp.last_name = rs.getString("last_name");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching employee: " + e.getMessage());
        }
        return emp;
    }

    // Print salaries
    public void printSalaries(ArrayList<Employee> employees) {
        for (Employee e : employees) {
            System.out.println(e.first_name + " " + e.last_name + " | " + e.title + " | " + e.salary);
        }
    }

    // Main method
    public static void main(String[] args) {
        App a = new App();
        if (args.length < 1) {
            a.connect("localhost:33060", 10000);
        } else {
            a.connect(args[0], Integer.parseInt(args[1]));
        }

        Department dept = a.getDepartment("Development");
        ArrayList<Employee> employees = a.getSalariesByDepartment(dept);
        a.printSalaries(employees);
        a.disconnect();
    }
}
