package com.example;

import com.google.gson.annotations.*;

public class Employee extends Person {

    @Expose
    private String jobTitle;

    @Expose
    private double monthlySalary;

    public Employee(Address address, Name name, Sex sex, String jobTitle, double monthlySalary) {
        super(address, name, sex);
        this.jobTitle = jobTitle;
        this.monthlySalary = monthlySalary;

    }


    public String getJobTitle() {
        return jobTitle;
    }


    public double getMonthlySalary() {
        return monthlySalary;
    }
}
