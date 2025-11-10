package com.napier.devops;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class App {

    /**
     * Connection to MySQL database.
     */
    private Connection con = null;

    /**
     * Connect to the MySQL database.
     */
    public void connect(String location, int delay) {
        try {
            // Load Database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e) {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 15;
        for (int i = 0; i < retries; ++i) {
            System.out.println("Connecting to database...");
            try {
                // Wait a bit for db to start
                Thread.sleep(10000);
                // Connect to database
                //con = DriverManager.getConnection("jdbc:mysql://db:3306/employees?useSSL=false&allowPublicKeyRetrieval=true", "root", "example");
                con = DriverManager.getConnection("jdbc:mysql://" + location + "/employees?useSSL=false&allowPublicKeyRetrieval=true", "root", "example");
                System.out.println("Successfully connected");
                break;
            }
            catch (SQLException sqle) {
                System.out.println("Failed to connect to database attempt " + Integer.toString(i));
                System.out.println(sqle.getMessage());
            }
            catch (InterruptedException ie) {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    /**
     * Disconnect from the MySQL database.
     */
    public void disconnect() {
        if (con != null) {
            try {
                // Close connection
                con.close();
            }
            catch (Exception e) {
                System.out.println("Error closing connection to database");
            }
        }
    }

    public Employee getEmployee(int ID) {
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect = "SELECT emp_no, first_name, last_name FROM employees WHERE emp_no = " + ID;
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Return new employee if valid.
            // Check one is returned
            if (rset.next()) {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
                return emp;
            }
            else
                return null;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee details");
            return null;
        }
    }

    public void displayEmployee(Employee emp) {
        if (emp != null) {
            System.out.println(
                    emp.emp_no + " " + emp.first_name + " " + emp.last_name + "\n" + emp.title + "\n"
                            + "Salary:" + emp.salary + "\n" + emp.dept_name + "\n" + "Manager: "
                            + emp.manager + "\n");
        }
    }

    /**
     * Gets all the current employees and salaries.
     * @return A list of all employees and salaries, or null if there is an error.
     */
    public ArrayList<Employee> getAllSalaries() {
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary "
                            + "FROM employees, salaries "
                            + "WHERE employees.emp_no = salaries.emp_no AND salaries.to_date = '9999-01-01' "
                            + "ORDER BY employees.emp_no ASC";
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Extract employee information
            ArrayList<Employee> employees = new ArrayList<Employee>();
            while (rset.next()) {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("employees.emp_no");
                emp.first_name = rset.getString("employees.first_name");
                emp.last_name = rset.getString("employees.last_name");
                emp.salary = rset.getInt("salaries.salary");
                employees.add(emp);
            }
            return employees;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salary details");
            return null;
        }
    }

    /**
     * Prints a list of employees.
     * @param employees The list of employees to print.
     */
    public void printSalaries(ArrayList<Employee> employees) {
        // Check employees is not null
        if (employees == null) {
            System.out.println("No employees");
            return;
        }

        // Print header
        System.out.println(String.format("%-10s %-15s %-20s %-8s", "Emp No", "First Name", "Last Name", "Salary"));
        // Loop over all employees in the list
        for (Employee emp : employees) {
            if (emp == null)
                continue;

            String emp_string =
                    String.format("%-10s %-15s %-20s %-8s",
                            emp.emp_no, emp.first_name, emp.last_name, emp.salary);
            System.out.println(emp_string);
        }
    }

    public Department getDepartment(String dept_name) {
        try {
            PreparedStatement pstmt = con.prepareStatement("SELECT dept_no, dept_name " +
                    "FROM departments WHERE dept_name = ?");
            pstmt.setString(1, dept_name);
            ResultSet rset = pstmt.executeQuery();
            Department dept = null;
            if (rset.next()) {
                dept = new Department(rset.getString(1), rset.getString(2));
            }
            rset.close();
            pstmt.close();
            return dept;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get Department");
            return null;
        }
    }

    public ArrayList<Employee> getSalariesByDepartment(Department dept) {
        try {
            PreparedStatement pstmt = con.prepareStatement("SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary "
                    + "FROM employees, salaries, dept_emp "
                    + "WHERE employees.emp_no = salaries.emp_no AND salaries.to_date = '9999-01-01' "
                    + "AND employees.emp_no = dept_emp.emp_no AND dept_emp.dept_no = ? "
                    + "ORDER BY employees.emp_no ASC");
            pstmt.setString(1, dept.getDept_no());
            // Execute SQL statement
            ResultSet rset = pstmt.executeQuery();
            // Extract employee information
            ArrayList<Employee> employees = new ArrayList<Employee>();
            while (rset.next()) {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("employees.emp_no");
                emp.first_name = rset.getString("employees.first_name");
                emp.last_name = rset.getString("employees.last_name");
                emp.salary = rset.getInt("salaries.salary");
                employees.add(emp);
            }
            return employees;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salary details");
            return null;
        }
    }

    public void addEmployee(Employee emp) {
        try {
            String strUpdate = "INSERT INTO employees (emp_no, first_name, last_name, " +
                    "birth_date, gender, hire_date) VALUES (?, ?, ?, '9999-01-01', 'M', '9999-01-01')";
            PreparedStatement pstmt = con.prepareStatement(strUpdate);
            pstmt.setInt(1, emp.emp_no);
            pstmt.setString(2, emp.first_name);
            pstmt.setString(3, emp.last_name);
            pstmt.execute();
            pstmt.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to add employee");
        }
    }

    public ArrayList<Employee> getSalariesByRole(String role) {
        try {
            PreparedStatement pstmt = con.prepareStatement("SELECT employees.emp_no, employees.first_name, " +
                    "employees.last_name, titles.title, salaries.salary, departments.dept_name, dept_manager.emp_no " +
                    "FROM employees, salaries, titles, departments, dept_emp, dept_manager " +
                    "WHERE employees.emp_no = salaries.emp_no " +
                    "AND salaries.to_date = '9999-01-01' " +
                    "AND titles.emp_no = employees.emp_no " +
                    "AND titles.to_date = '9999-01-01' " +
                    "AND dept_emp.emp_no = employees.emp_no " +
                    "AND dept_emp.to_date = '9999-01-01' " +
                    "AND departments.dept_no = dept_emp.dept_no " +
                    "AND dept_manager.dept_no = dept_emp.dept_no " +
                    "AND dept_manager.to_date = '9999-01-01' " +
                    "AND titles.title = ?");
            pstmt.setString(1, role);
            // Execute SQL statement
            ResultSet rset = pstmt.executeQuery();
            // Extract employee information
            ArrayList<Employee> employees = new ArrayList<Employee>();
            while (rset.next()) {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("employees.emp_no");
                emp.first_name = rset.getString("employees.first_name");
                emp.last_name = rset.getString("employees.last_name");
                emp.title = rset.getString("titles.title");
                emp.salary = rset.getInt("salaries.salary");
                emp.dept_name = rset.getString("departments.dept_name");
                emp.manager = rset.getString("dept_manager.emp_no");
                employees.add(emp);
            }
            return employees;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salary details");
            return null;
        }
    }

    public void outputEmployees(ArrayList<Employee> employees, String filename) {
        // Check employees is not null
        if (employees == null) {
            System.out.println("No employees");
            return;
        }

        StringBuilder sb = new StringBuilder();
        // Print header
        sb.append("| Emp No | First Name | Last Name | Title | Salary | Department | Manager |\r\n");
        sb.append("| --- | --- | --- | --- | --- | --- | --- |\r\n");
        // Loop over all employees in the list
        for (Employee emp : employees) {
            if (emp == null) continue;
            sb.append("| " + emp.emp_no + " | " +
                    emp.first_name + " | " + emp.last_name + " | " +
                    emp.title + " | " + emp.salary + " | "
                    + emp.dept_name + " | " + emp.manager + " |\r\n");
        }
        try {
            new File("./reports/").mkdir();
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File("./reports/" + filename)));
            writer.write(sb.toString());
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to print salary details");
        }
    }

    public static void main(String[] args) {
        // Create new Application
        App a = new App();

        // Connect to database
        if(args.length < 1){
            a.connect("localhost:33060", 0);
        }else{
//            m.connect("db:3306", 10000);
            a.connect(args[0], Integer.parseInt(args[1]));
        }

//        Employee emp = m.getEmployee(255530);
        // Display results
//        m.displayEmployee(emp);

//        ArrayList<Employee> employees = m.getAllSalaries();

//        Department dept = m.getDepartment("Development");
        // Extract employee salary information
//        ArrayList<Employee> employees = m.getSalariesByDepartment(dept);

        ArrayList<Employee> employees = a.getSalariesByRole("Manager");
        a.outputEmployees(employees, "ManagerSalaries.md");

        // Test the size of the returned data - should be 240124
//        System.out.println(employees.size());
//        m.printSalaries(employees);

        // Disconnect from database
        a.disconnect();
    }
}