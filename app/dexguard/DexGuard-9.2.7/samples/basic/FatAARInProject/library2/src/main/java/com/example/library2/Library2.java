package com.example.library2;

import com.example.library.Library;

/**
 * Sample class that depends on an external dependency.
 */
public class Library2
{
    public void printMessage() {
        System.out.println(new Library().getMessage());
    }
}
