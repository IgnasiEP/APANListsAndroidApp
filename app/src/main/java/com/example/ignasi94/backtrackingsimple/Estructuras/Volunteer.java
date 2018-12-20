package com.example.ignasi94.backtrackingsimple.Estructuras;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Volunteer {
    public int id;
    public String name;
    public String phone;
    public String volunteerDay;
    public String observations;
    public ArrayList<Dog> favouriteDogs;

    //empty constructor
    public Volunteer(){}
    //all parameters in constructor
    public Volunteer(int id, String name, String phone, String volunteerDay, String observations)
    {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.volunteerDay = volunteerDay;
        this.observations = observations;
        this.favouriteDogs = new ArrayList<Dog>();
    }

    public Volunteer(String name, String phone, String volunteerDay, String observations)
    {
        this(0,name,phone,volunteerDay,observations);
    }

    public Volunteer(String name)
    {
        this.name = name;
    }

}

