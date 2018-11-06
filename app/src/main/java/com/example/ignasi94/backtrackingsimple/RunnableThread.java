package com.example.ignasi94.backtrackingsimple;

import java.util.ArrayList;
import java.util.List;

public class RunnableThread implements Runnable {
    private String name;
    private List<Dog> listDogs;
    private List<Cage> listCages;
    private List<Volunteer> listVolunteers;
    public Dog[][] walksTable;
    public ArrayList<ArrayList<Dog>> cleanTable;

    public String getName()
    {
        return this.name;
    }
    public RunnableThread(String n, List<Dog> listDogs, List<Cage> listCages, List<Volunteer> listVolunteers) {
        this.name = n;
        this.listDogs = listDogs;
        this.listCages = listCages;
        this.listVolunteers = listVolunteers;
    }

    @Override
    public void run() {
        Algorithm alg = new Algorithm(this.listDogs,this.listCages,this.listVolunteers);
        this.walksTable = alg.walksTable;
        this.cleanTable = alg.cleanTable;
    }
}
