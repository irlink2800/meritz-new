package com.example;

import com.google.gson.annotations.*;

public class Company extends Addressee {

    @Expose
    private String              name;

    @Expose
    private SoftwareDeveloper[] employees;


    public Company(String name, SoftwareDeveloper[] employees, Address officeAddress) {
        super(officeAddress);
        this.name = name;
        this.employees = employees;
    }
}
