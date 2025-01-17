package com.example;

import com.google.gson.annotations.*;

public class SoftwareDeveloper extends Employee {

    @Expose
    private String favoriteLanguage;

    public SoftwareDeveloper(Address address, Name name, Sex sex, String jobTitle, double monthlySalary, String favoriteLanguage) {
        super(address, name, sex, jobTitle, monthlySalary);
        this.favoriteLanguage = favoriteLanguage;
    }

    public String getFavoriteLanguage() {
        return favoriteLanguage;
    }
}
