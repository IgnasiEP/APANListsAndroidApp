package com.example.ignasi94.backtrackingsimple.Estructuras.Grafo.DogGraf;

import com.example.ignasi94.backtrackingsimple.Estructuras.Dog;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class DogGraph {
    int nVertexs;
    LinkedList<EdgeDog>[] adjacencylist;
    Dog[] vertexs;

    public DogGraph(int nVertexs) {
        this.nVertexs = nVertexs;
        this.vertexs = new Dog[nVertexs];
        adjacencylist = new LinkedList[nVertexs];
        //initialize adjacency lists for all the vertices
        for (int i = 0; i < nVertexs; i++) {
            adjacencylist[i] = new LinkedList<>();
        }
    }

    public void addVertex(Dog dog) {
        for(int i = 0; i < this.vertexs.length; ++i)
        {
            if(vertexs[i] == null)
            {
                vertexs[i] = dog;
                break;
            }
        }
    }

    public void addEdge(Dog v1,Dog v2, EdgeDog edge) {
        boolean edge1 = false;
        boolean edge2 = false;
        for(int i = 0; i < this.vertexs.length; ++i) {
            if(this.vertexs[i].id == v1.id) {
                this.adjacencylist[i].add(edge);
                edge1 = true;
            }
            if(this.vertexs[i].id == v2.id) {
                this.adjacencylist[i].add(edge);
                edge2 = true;
            }
            if(edge1 && edge2)
            {
                return;
            }
        }
    }

    public Dog[] vertexList()
    {
        return this.vertexs;
    }

    public LinkedList<EdgeDog>[] edgeSet()
    {
        return this.adjacencylist;
    }

    //Returns a set of all edges touching the specified vertex.
    public LinkedList<EdgeDog> edgesOf(Dog vertex)
    {
        for(int i = 0; i < this.vertexs.length; ++i) {
            if(this.vertexs[i].id == vertex.id) {
                return this.adjacencylist[i];
            }
        }
        return null;
    }

    //Returns a set of all edges touching the specified vertex with the specified weight.
    public List<EdgeDog> edgesOfByWeight(Dog vertex, double weight)
    {
        LinkedList<EdgeDog> result = new LinkedList<EdgeDog>();
        LinkedList<EdgeDog> edges = new LinkedList<EdgeDog>();
        for(int i = 0; i < this.vertexs.length; ++i) {
            if(this.vertexs[i].id == vertex.id) {
                edges = this.adjacencylist[i];
            }
        }
        Iterator iter = edges.iterator();
        while(iter.hasNext()) {
            EdgeDog edge = (EdgeDog) iter.next();
            if(edge.weight == weight)
            {
                result.add(edge);
            }
        }
        return result;
    }

    public EdgeDog getEdge(Dog v1, Dog v2)
    {
        for(int i = 0; i < this.vertexs.length; ++i) {
            if(this.vertexs[i].id == v1.id || this.vertexs[i].id == v2.id) {
                Iterator iter = this.adjacencylist[i].iterator();
                while(iter.hasNext()) {
                    EdgeDog edge = (EdgeDog) iter.next();
                    if((edge.v1 == v1 && edge.v2 == v2) || (edge.v1 == v2 && edge.v2 == v1))
                    {
                        return edge;
                    }
                }
            }
        }
        return null;
    }

    public int Degree(Dog vertex) {
        for(int i = 0; i < this.vertexs.length; ++i) {
            if(this.vertexs[i].id == vertex.id) {
                return this.adjacencylist[i].size();
            }
        }
        return 0;
    }
}

