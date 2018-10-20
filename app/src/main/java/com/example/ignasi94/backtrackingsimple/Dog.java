package com.example.ignasi94.backtrackingsimple;

public class Dog {
    //Private Variables
    int id;
    String name;
    int idCage;
    int age;
    String link;
    Boolean special;
    Short walktype;
    String observations;
    //How to safe images


    //empty constructor
    public Dog(){}

    //all parameters in constructor
    public Dog(int id, String name, int idCage, int age, String link, Boolean special, Short walktype, String observations)
    {
        this.id = id;
        this.name = name;
        this.idCage = idCage;
        this.age = age;
        this.link = link;
        this.special = special;
        this.walktype = walktype;
        this.observations = observations;
    }

    public Dog(String name, int idCage, int age, String link, Boolean special, Short walktype, String observations)
    {
        this(0,name,idCage,age,link,special,walktype,observations);
    }
}