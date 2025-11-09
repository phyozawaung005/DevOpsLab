package com.napier.devops;

public class Employee {
    public int emp_no;
    public String first_name;
    public String last_name;
    public String title;
    public double salary;

    public Employee() {}

    public Employee(int emp_no, String first_name, String last_name) {
        this.emp_no = emp_no;
        this.first_name = first_name;
        this.last_name = last_name;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "emp_no=" + emp_no +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", title='" + title + '\'' +
                ", salary=" + salary +
                '}';
    }
}
