package com.example.ignasi94.backtrackingsimple.Estructuras;

public class Cage {
    //Private Variables
    public int id;
    public int numCage;
    public String zone;

    //empty constructor
    public Cage(){}
    //all parameters in constructor
    public Cage(int id, int numCage, String zone)
    {
        this.id = id;
        this.numCage = numCage;
        this.zone = zone;
    }

    public Cage(int numCage, String zone)
    {
        this(0,numCage,zone);
    }
}


