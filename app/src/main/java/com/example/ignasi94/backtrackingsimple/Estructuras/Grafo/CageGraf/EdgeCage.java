package com.example.ignasi94.backtrackingsimple.Estructuras.Grafo.CageGraf;

import com.example.ignasi94.backtrackingsimple.Estructuras.Cage;
import com.example.ignasi94.backtrackingsimple.Estructuras.Dog;

public class EdgeCage {
    public VertexCage v1;
    public VertexCage v2;
    public double weight;

    public EdgeCage() {}

    public EdgeCage(VertexCage v1, VertexCage v2, double weight)
    {
        this.v1 = v1;
        this.v2 = v2;
        this.weight = weight;
    }
}
