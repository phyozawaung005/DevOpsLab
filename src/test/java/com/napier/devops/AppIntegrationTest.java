package com.napier.devops;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

/**
 * Integration tests for App class interacting with the database.
 * Tests connection, data retrieval, and Department entity behavior.
 */
public class AppIntegrationTest {

    static App app;

    /**
     * Runs once before all tests — establishes a connection.
     */
    @BeforeAll
    static void init() {
        app = new App();
        // Connect to local MySQL database
        app.connect("localhost:33060", 0);
    }

    /**
     * Runs once after all tests — closes the connection.
     */
    @AfterAll
    static void cleanup() {
        app.disconnect();
    }

    /**
     * Test 1 — Check if the application can retrieve a Department correctly.
     */
    @Test
    void testGetDepartment() {
        Department dept = app.getDepartment("Development");
        assertNotNull(dept, "Department object should not be null");
        assertEquals("Development", dept.getDept_name(), "Department name should match 'Development'");
    }

    /**
     * Test 2 — Ensure a Department object holds data properly.
     */
    @Test
    void testDepartmentSettersAndGetters() {
        Department dept = new Department();
        dept.setDept_no("d001");
        dept.setDept_name("Development");

        Employee manager = new Employee();
        manager.emp_no = 1001;
        manager.first_name = "John";
        manager.last_name = "Doe";
        dept.setManager(manager);

        assertEquals("d001", dept.getDept_no());
        assertEquals("Development", dept.getDept_name());
        assertEquals("John", dept.getManager().first_name);
    }

    /**
     * Test 3 — Check if employees in a department are retrieved correctly.
     */
    @Test
    void testGetEmployeesByDepartment() {
        Department dept = app.getDepartment("Development");
        ArrayList<Employee> employees = app.getSalariesByDepartment(dept);

        assertNotNull(employees, "Employees list should not be null");
        assertTrue(employees.size() > 0, "Department should have at least one employee");

        // Optional: check one employee's fields
        Employee emp = employees.get(0);
        assertNotNull(emp.first_name, "Employee should have a first name");
        assertNotNull(emp.last_name, "Employee should have a last name");
    }

    /**
     * Test 4 — Check the Department toString() output.
     */
    @Test
    void testDepartmentToString() {
        Department dept = new Department();
        dept.setDept_no("d001");
        dept.setDept_name("Development");

        String expected = "Department{dept_no='d001', dept_name='Development', manager=null}";
        assertEquals(expected, dept.toString());
    }
}
