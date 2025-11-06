package com.napier.devops;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AppDisplayEmployeeTest
{
    static App app;

    @BeforeAll
    static void init()
    {
        app = new App();
    }

    @Test
    void displayEmployeeTestNull()
    {
        System.out.println("=== Test: Null Employee ===");
        app.displayEmployee(null);
    }

    @Test
    void displayEmployeeTestValid()
    {
        System.out.println("=== Test: Valid Employee ===");
        Employee emp = new Employee();
        emp.emp_no = 1;
        emp.first_name = "John";
        emp.last_name = "Doe";
        emp.salary = 50000;
        app.displayEmployee(emp);
    }

    @Test
    void displayEmployeeTestMissingFields()
    {
        System.out.println("=== Test: Missing Fields ===");
        Employee emp = new Employee();
        emp.emp_no = 2;
        emp.first_name = null;   // missing field
        emp.last_name = "Smith";
        emp.salary = 45000;
        app.displayEmployee(emp);
    }
}
