package com.example.ignasi94.backtrackingsimple;

public class EdgeDog {
    public VertexDog v1;
    public VertexDog v2;
    public double weight;

    public EdgeDog () {}

    public EdgeDog (VertexDog v1, VertexDog v2, double weight)
    {
        this.v1 = v1;
        this.v2 = v2;
        this.weight = weight;
    }
}
