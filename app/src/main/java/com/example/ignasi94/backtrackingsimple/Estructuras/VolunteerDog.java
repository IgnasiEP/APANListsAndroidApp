package com.example.ignasi94.backtrackingsimple.Estructuras;

public class VolunteerDog implements Cloneable
{
    public Dog dog;
    public Volunteer volunteer;

    public VolunteerDog() {}

    public VolunteerDog(Dog dog, Volunteer volunteer)
    {
        this.dog = dog;
        this.volunteer = volunteer;
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