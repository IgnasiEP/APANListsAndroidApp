package com.example.ignasi94.backtrackingsimple.Estructuras.Grafo.DogGraf;

import com.example.ignasi94.backtrackingsimple.Estructuras.Dog;

public class TupleDog {
    public Dog dog;
    public int dogsInCage;

    public TupleDog() {}

    public TupleDog(Dog dog, int dogsInCage)
    {
        this.dog = dog;
        this.dogsInCage = dogsInCage;
    }
}
