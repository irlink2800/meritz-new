package com.example;

import com.google.gson.annotations.*;

public class Person extends Addressee {

    @Expose
    private Name    name;

    @Expose
    private Sex     sex;

    public Person(Address address,
                  Name name,
                  Sex sex) {
        super(address);
        this.name    = name;
        this.sex     = sex;
    }


    public Name getName() {
        return name;
    }


    public Sex getSex() {
        return sex;
    }

}
