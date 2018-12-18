package com.example.ignasi94.backtrackingsimple.Estructuras;

public class CageDog implements Cloneable
{
    public boolean visibility;
    public Cage cage;
    public Dog dog;

    public CageDog() {}

    public CageDog(Dog dog, Cage cage)
    {
        this.dog = dog;
        this.cage = cage;
    }

    public CageDog(Cage cage, boolean visibility)
    {
        this.cage = cage;
        this.visibility = visibility;
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