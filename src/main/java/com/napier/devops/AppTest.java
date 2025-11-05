package com.napier.devops;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

class AppTest {

    static App app;

    @BeforeAll
    static void init() {
        app = new App();
    }

    @Test
    void printSalariesTestNull() {
        app.printSalaries(null);  // Should print "No employees"
    }

    @Test
    void printSalariesTestEmpty() {
        ArrayList<Employee> employees = new ArrayList<>();
        app.printSalaries(employees);  // Should print "No employees"
    }

    @Test
    void printSalariesTestContainsNull() {
        ArrayList<Employee> employees = new ArrayList<>();
        employees.add(null);
        app.printSalaries(employees);  // Should print header only
    }

    @Test
    void printSalariesTestNormal() {
        ArrayList<Employee> employees = new ArrayList<>();
        Employee emp = new Employee();
        emp.emp_no = 1;
        emp.first_name = "Kevin";
        emp.last_name = "Chalmers";
        emp.salary = 55000;
        employees.add(emp);
        app.printSalaries(employees);  // Should print employee details
    }
}
