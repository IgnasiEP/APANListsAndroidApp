package com.example.ignasi94.backtrackingsimple;

import java.util.List;

public class VertexDog extends Dog {
    public boolean assigned;
    public List<Integer> walkdomain;
    public List<Integer> cleandomain;

    //empty constructor
    public VertexDog(){}

    //all parameters in constructor
    public VertexDog(int id, String name, int idCage, int age, String link, Boolean special, Short walktype, String observations, int npaseos)
    {
        super(id, name, idCage, age, link, special, walktype, observations);
        for(int i = 0; i < npaseos; ++i)
        {
            walkdomain.add(i);
            cleandomain.add(i);
        }
    }

    public VertexDog(String name, int idCage, int age, String link, Boolean special, Short walktype, String observations, int npaseos)
    {
        this(0,name,idCage,age,link,special,walktype,observations, npaseos);
    }
}

