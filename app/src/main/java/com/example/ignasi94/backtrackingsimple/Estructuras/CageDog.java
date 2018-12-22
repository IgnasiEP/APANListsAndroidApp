package com.example.ignasi94.backtrackingsimple.Estructuras;

public class CageDog implements Cloneable
{
    public Integer row;
    public boolean visibility;
    public Cage cage;
    public Dog dog;

    public CageDog() {}

    public CageDog(Dog dog, Cage cage)
    {
        this.dog = dog;
        this.cage = cage;
    }

    public CageDog(Cage cage, Integer row,boolean visibility)
    {
        this.cage = cage;
        this.visibility = visibility;
        this.row = row;
    }

    public Object clone() {
        try
        {
            return super.clone();
        }
        catch( CloneNotSupportedException e )
        {
            return null;
        }
    }
}