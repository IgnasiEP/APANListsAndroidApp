package com.example.ignasi94.backtrackingsimple.Estructuras;

import com.example.ignasi94.backtrackingsimple.Utils.Constants;

import java.util.List;

public class Dog {
    //Private Variables
    public int id;
    public String name;
    public int idCage;
    public int age;
    public String link;
    public Boolean special;
    public Short walktype;
    public String observations;
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

    public Dog(String name)
    {
        this.name = name;
    }

    public boolean HasInteriorPartner(List<Dog> dogs)
    {
        //Si el perro ya es interior nos ahorramos hacer el bucle
        if(this.walktype == Constants.WT_INTERIOR)
        {
            return true;
        }

        for(int i = 0; i < dogs.size(); ++i)
        {
            Dog iDog = dogs.get(i);
            if(this.idCage == iDog.idCage && this.id != iDog.id && iDog.walktype == Constants.WT_INTERIOR)
            {
                return true;
            }
        }
        return false;
    }
}