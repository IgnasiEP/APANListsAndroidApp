package com.example.ignasi94.backtrackingsimple.Estructuras;

public class VolunteerDog implements Cloneable
{
    public Integer cleanRow;
    public boolean visibility;
    public Volunteer volunteer;
    public Dog dog;
    public boolean walksError;
    public boolean specialError;
    public boolean interiorError;

    public VolunteerDog() {}

    public VolunteerDog(Dog dog, Volunteer volunteer)
    {
        this.dog = dog;
        this.volunteer = volunteer;
    }

    public VolunteerDog(Dog dog, Integer cleanRow, boolean visibility)
    {
        this.dog = dog;
        this.visibility = visibility;
        this.cleanRow = cleanRow;
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