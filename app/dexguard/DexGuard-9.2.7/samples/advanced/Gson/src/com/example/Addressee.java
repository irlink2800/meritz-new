package com.example;

import com.google.gson.annotations.*;

public abstract class Addressee {

    @SerializedName("address")
    private Address address;

    protected Addressee(Address address) {
        this.address = address;
    }


    public Address getAddress() {
        return address;
    }
}
