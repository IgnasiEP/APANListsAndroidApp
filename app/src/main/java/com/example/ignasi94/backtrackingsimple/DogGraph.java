package com.example.ignasi94.backtrackingsimple;


import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.alg.KruskalMinimumSpanningTree;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class DogGraph {
    private DefaultUndirectedWeightedGraph<VertexDog, EdgeDog> graph = new DefaultUndirectedWeightedGraph<VertexDog, EdgeDog>(EdgeDog.class);
    private double DEFAULT_EDGE_WEIGHT = 1;

    public DefaultUndirectedWeightedGraph<VertexDog, EdgeDog> DogGraph() {
        return graph;
    }

    public void addVertex(VertexDog dog) {
        graph.addVertex(dog);
    }

    public void addEdge(VertexDog v1,VertexDog v2) {
        graph.addEdge(v1, v2);
    }

    public void addEdge(VertexDog v1,VertexDog v2, EdgeDog edge) {
        graph.addEdge(v1, v2, edge);
    }

    public Set<VertexDog> vertexSet()
    {
        return graph.vertexSet();
    }

    public Set<EdgeDog> edgeSet()
    {
        return graph.edgeSet();
    }

    //Returns a set of all edges touching the specified vertex.
    public Set<EdgeDog> edgesOf(VertexDog vertex)
    {
        return graph.edgesOf(vertex);
    }

    //Returns a set of all edges touching the specified vertex with the specified weight.
    public Set<EdgeDog> edgesOfByWeight(VertexDog vertex, double weight)
    {
        Set<EdgeDog> result = new TreeSet<EdgeDog>();

        Set<EdgeDog> edges = graph.edgesOf(vertex);
        for(EdgeDog edge : edges)
        {
            if(edge.weight == weight)
            {
                result.add(edge);
            }
        }
        return result;
    }

    public EdgeDog getEdge(VertexDog v1, VertexDog v2)
    {
        return graph.getEdge(v1,v2);
    }

    public int Degree(VertexDog vertex) {
        return graph.inDegreeOf(vertex);
    }
}

