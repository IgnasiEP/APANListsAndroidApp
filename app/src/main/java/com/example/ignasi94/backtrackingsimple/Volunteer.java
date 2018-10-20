package com.example.ignasi94.backtrackingsimple;

public class Volunteer {
    int id;
    String name;
    String phone;
    String volunteerDay;
    String observations;

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
    }

    public Volunteer(String name, String phone, String volunteerDay, String observations)
    {
        this(0,name,phone,volunteerDay,observations);
    }

}

