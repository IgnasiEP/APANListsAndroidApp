package com.example.ignasi94.backtrackingsimple.Estructuras;

public class VolunteerWalks extends Volunteer {
    public int id;
    public String name;
    public int clean;
    public int walk1;
    public int walk2;
    public int walk3;
    public int walk4;
    public int walk5;
    public int nPaseos;

    public VolunteerWalks() {}

    public VolunteerWalks(int id, String name, int clean, int walk1, int walk2, int walk3, int walk4, int walk5, int nPaseos)
    {
        this.id = id;
        this.name = name;
        this.clean = clean;
        this.walk1 = walk1;
        this.walk2 = walk2;
        this.walk3 = walk3;
        this.walk4 = walk4;
        this.walk5 = walk5;
        this.nPaseos = nPaseos;
    }

}
