package com.napier.devops;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

public class AppIntegrationTest {

    static App app;

    @BeforeAll
    static void init() {
        app = new App();
        app.connect("localhost:33060", 0);
    }

    @AfterAll
    static void cleanup() {
        app.disconnect();
    }

    @Test
    void testGetDepartment() {
        Department dept = app.getDepartment("Development");
        assertNotNull(dept);
        assertEquals("Development", dept.getDept_name());
    }

    @Test
    void testDepartmentSettersAndGetters() {
        Department dept = new Department();
        dept.setDept_no("d001");
        dept.setDept_name("Development");

        Employee manager = new Employee(1001, "John", "Doe");
        dept.setManager(manager);

        assertEquals("d001", dept.getDept_no());
        assertEquals("Development", dept.getDept_name());
        assertEquals("John", dept.getManager().first_name);
    }

    @Test
    void testGetEmployeesByDepartment() {
        Department dept = app.getDepartment("Development");
        ArrayList<Employee> employees = app.getSalariesByDepartment(dept);

        assertNotNull(employees);
        assertTrue(employees.size() > 0);

        Employee emp = employees.get(0);
        assertNotNull(emp.first_name);
        assertNotNull(emp.last_name);
    }

    @Test
    void testDepartmentToString() {
        Department dept = new Department();
        dept.setDept_no("d001");
        dept.setDept_name("Development");

        String expected = "Department{dept_no='d001', dept_name='Development', manager=null}";
        assertEquals(expected, dept.toString());
    }

    @Test
    void testAddEmployee() {
        Employee emp = new Employee(500000, "Kevin", "Chalmers");
        app.addEmployee(emp);

        Employee fetched = app.getEmployee(500000);
        assertNotNull(fetched);
        assertEquals(500000, fetched.emp_no);
        assertEquals("Kevin", fetched.first_name);
        assertEquals("Chalmers", fetched.last_name);
    }
}
