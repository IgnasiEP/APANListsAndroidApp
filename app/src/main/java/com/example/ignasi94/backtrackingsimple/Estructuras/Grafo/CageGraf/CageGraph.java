package com.example.ignasi94.backtrackingsimple.Estructuras.Grafo.CageGraf;

import com.example.ignasi94.backtrackingsimple.Estructuras.Cage;
import com.example.ignasi94.backtrackingsimple.Estructuras.Dog;
import com.example.ignasi94.backtrackingsimple.Estructuras.Grafo.DogGraf.EdgeDog;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class CageGraph {
    int nVertexs;
    LinkedList<EdgeCage>[] adjacencylist;
    VertexCage[] vertexs;

    public CageGraph(int nVertexs) {
        this.nVertexs = nVertexs;
        this.vertexs = new VertexCage[nVertexs];
        adjacencylist = new LinkedList[nVertexs];
        //initialize adjacency lists for all the vertices
        for (int i = 0; i < nVertexs; i++) {
            adjacencylist[i] = new LinkedList<>();
        }
    }

    public void addVertex(VertexCage cage) {
        for(int i = 0; i < this.vertexs.length; ++i)
        {
            if(vertexs[i] == null)
            {
                vertexs[i] = cage;
                break;
            }
        }
    }

    public VertexCage getVertex(int idCage) {
        for(int i = 0; i < this.vertexs.length; ++i)
        {
            if(vertexs[i] != null && vertexs[i].idCage == idCage)
            {
                return vertexs[i];
            }
        }
        return null;
    }

    public void addEdge(Cage v1,Cage v2, EdgeCage edge) {
        boolean edge1 = false;
        boolean edge2 = false;
        for(int i = 0; i < this.vertexs.length; ++i) {
            if(this.vertexs[i].idCage == v1.id) {
                this.adjacencylist[i].add(edge);
                edge1 = true;
            }
            if(this.vertexs[i].idCage == v2.id) {
                this.adjacencylist[i].add(edge);
                edge2 = true;
            }
            if(edge1 && edge2)
            {
                return;
            }
        }
    }

    public VertexCage[] vertexList()
    {
        return this.vertexs;
    }

    public LinkedList<EdgeCage>[] edgeSet()
    {
        return this.adjacencylist;
    }

    //Returns a set of all edges touching the specified vertex.
    public LinkedList<EdgeCage> edgesOf(Cage vertex)
    {
        for(int i = 0; i < this.vertexs.length; ++i) {
            if(this.vertexs[i].idCage == vertex.id) {
                return this.adjacencylist[i];
            }
        }
        return null;
    }

    //Returns a set of all edges touching the specified vertex with the specified weight.
    public List<EdgeCage> edgesOfByWeight(Cage vertex, double weight)
    {
        LinkedList<EdgeCage> result = new LinkedList<EdgeCage>();
        LinkedList<EdgeCage> edges = new LinkedList<EdgeCage>();
        for(int i = 0; i < this.vertexs.length; ++i) {
            if(this.vertexs[i].idCage == vertex.id) {
                edges = this.adjacencylist[i];
            }
        }
        Iterator iter = edges.iterator();
        while(iter.hasNext()) {
            EdgeCage edge = (EdgeCage) iter.next();
            if(edge.weight == weight)
            {
                result.add(edge);
            }
        }
        return result;
    }

    public EdgeCage getEdge(Cage v1, Cage v2)
    {
        for(int i = 0; i < this.vertexs.length; ++i) {
            if(this.vertexs[i].idCage == v1.id || this.vertexs[i].idCage == v2.id) {
                Iterator iter = this.adjacencylist[i].iterator();
                while(iter.hasNext()) {
                    EdgeCage edge = (EdgeCage) iter.next();
                    if((edge.v1 == v1 && edge.v2 == v2) || (edge.v1 == v2 && edge.v2 == v1))
                    {
                        return edge;
                    }
                }
            }
        }
        return null;
    }

    public int Degree(Cage vertex) {
        for(int i = 0; i < this.vertexs.length; ++i) {
            if(this.vertexs[i].idCage == vertex.id) {
                return this.adjacencylist[i].size();
            }
        }
        return 0;
    }
}

