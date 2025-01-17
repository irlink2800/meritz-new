package com.irlink.meritz.util;

public class TextMessage {

    double date;
    public String from;
    public String body;

    public TextMessage(String from, double date, String body)
    {
        this.from = from;
        this.date = date;
        this.body = body;
    }

    public void append(String body)
    {
        this.body += body;
    }

}