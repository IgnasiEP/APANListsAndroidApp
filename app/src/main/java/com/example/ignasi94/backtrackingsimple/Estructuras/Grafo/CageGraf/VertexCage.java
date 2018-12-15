package com.example.ignasi94.backtrackingsimple.Estructuras.Grafo.CageGraf;

import com.example.ignasi94.backtrackingsimple.Estructuras.Cage;
import com.example.ignasi94.backtrackingsimple.Estructuras.Dog;

import java.util.List;

public class VertexCage {
    public int idCage;
    public Cage cage;
    public List<Dog> interiorDogsInCage;
    public List<Dog> friendDogs;

    public VertexCage() {}

    public VertexCage(Cage cage, List<Dog> interiorDogsInCage, List<Dog> friendDogs)
    {
        this.idCage = cage.id;
        this.cage = cage;
        this.interiorDogsInCage = interiorDogsInCage;
        this.friendDogs = friendDogs;
    }
}
