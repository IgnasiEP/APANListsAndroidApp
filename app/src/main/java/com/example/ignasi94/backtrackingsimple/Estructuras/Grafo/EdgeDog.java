package com.example.ignasi94.backtrackingsimple.Estructuras.Grafo;

import com.example.ignasi94.backtrackingsimple.Estructuras.Dog;

public class EdgeDog {
    public Dog v1;
    public Dog v2;
    public double weight;

    public EdgeDog () {}

    public EdgeDog (Dog v1, Dog v2, double weight)
    {
        this.v1 = v1;
        this.v2 = v2;
        this.weight = weight;
    }
}
