package com.example.ignasi94.backtrackingsimple;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
//import org.jgraph.JGraph;
import org.jgrapht.generate.BarabasiAlbertGraphGenerator;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MakeLists extends AppCompatActivity {

    DBAdapter dbAdapter;
    Button doListButton = null;
    ArrayList<ArrayList<Dog>> listDogsPerCage = null;
    ArrayList<ArrayList<Dog>> nCleanDomain = null;
    ArrayList<Dog> nWalkDomain = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_lists);

        dbAdapter = new DBAdapter(this);
        doListButton = (Button)findViewById(R.id.button);
        doListButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                List<Dog> dogs = dbAdapter.getAllDogs();
                List<Cage> cages = dbAdapter.getAllCages();
                List<Volunteer> volunteers = dbAdapter.getAllVolunteers();
                SimpleBacktracking(dogs,cages,volunteers);
            }
        });
    }

    public void SimpleBacktracking(List<Dog> dogs, List<Cage> cages, List<Volunteer> volunteers)
    {
        int nPaseos = 4;

        DogGraph dogGraph = this.CreateDogsGraph(dogs, cages);
        dogGraph.vertexSet();

        ArrayList<ArrayList<Dog>> walksSolution = new ArrayList<ArrayList<Dog>>();
        ArrayList<ArrayList<Dog>> cleanSolution = new ArrayList<ArrayList<Dog>>();

        this.Backtracking(null, dogGraph, cages, volunteers.size(), walksSolution, cleanSolution);
    }

    public DogGraph CreateDogsGraph(List<Dog> dogs, List<Cage> cages) {
        DogGraph dogGraph = new DogGraph();

        //Vertices
        for (int i = 0; i < dogs.size(); ++i) {
            dogGraph.addVertex((VertexDog) dogs.get(i));
        }

        //Aristas
        for (int i = 0; i < dogs.size(); ++i) {
            VertexDog iDog = (VertexDog) dogs.get(i);
            boolean hasInterioriDog = iDog.HasInteriorPartner(dogs);
            for (int j = i + 1; j < dogs.size(); ++i) {
                VertexDog jDog = (VertexDog) dogs.get(j);
                if (iDog.idCage == jDog.idCage) {
                    EdgeDog edge = new EdgeDog(iDog,jDog,Constants.EDGE_SAME_CAGE_VALUE);
                    dogGraph.addEdge(iDog, jDog, edge);
                } else {
                    if (hasInterioriDog && jDog.HasInteriorPartner(dogs)) {
                        EdgeDog edge = new EdgeDog(iDog,jDog,Constants.EDGE_INCOMPATIBLE_VALUE);
                        dogGraph.addEdge(iDog, jDog, edge);
                    } else {
                        EdgeDog edge = new EdgeDog(iDog,jDog,Constants.EDGE_COMPATIBLE_VALUE);
                        dogGraph.addEdge(iDog, jDog, edge);
                    }
                }
            }
        }
        return dogGraph;
    }



    public void CreateListDogsPerCage(List<Dog> dogs, List<Cage> cages)
    {
        listDogsPerCage.add(new ArrayList<Dog>());
        for(int i = 0; i < cages.size(); ++i)
        {
            ArrayList<Dog> listDogs = new ArrayList<Dog>();
            for(int j = 0; j < dogs.size(); ++j)
            {
                Dog dog = dogs.get(j);
                if(dog.idCage == cages.get(i).id)
                {
                    listDogs.add(dog);
                }
            }
            listDogsPerCage.add(listDogs);
        }
    }

    public void Backtracking(VertexDog vertexDog, DogGraph dogGraph, List<Cage> cages, int nVolunteers, ArrayList<ArrayList<Dog>> walksSolution, ArrayList<ArrayList<Dog>> cleanSolution)
    {
        //inicio
        int paseo = this.Selection(vertexDog, dogGraph,cages,walksSolution,cleanSolution);
        if(this.Validate(paseo, vertexDog, dogGraph,cages, nVolunteers, walksSolution,cleanSolution))
        {
            //Eliminar de tots els dominis
            if(walksSolution.get(paseo).size() == nVolunteers)
            {
                this.UpdateDomain(paseo, dogGraph);
            }
            //if(iClean == nPaseos && irow == nPaseos-1 && icolumn == nVolunteers-1)
            {
                //Solución
                //no hacer nada
            }
            //else
            {
                /*if(iClean == irow)
                {
                    this.Backtracking(iClean+1, irow,icolumn,nPaseos,nVolunteers,walksTable,walksDomains,cleanTable,cleanDomains);
                }
                if(icolumn+1 == nVolunteers)
                {
                    this.Backtracking(iClean, irow+1,0,nPaseos,nVolunteers,walksTable,walksDomains,cleanTable,cleanDomains);
                }
                else
                {
                    this.Backtracking(iClean, irow,icolumn+1,nPaseos,nVolunteers,walksTable,walksDomains,cleanTable,cleanDomains);
                }*/
            }
        }
        else
        {
            //Eliminar del domini de la posició actual
            //UpdateThisDomain
            /*if(!this.DomainEmpty(iClean, irow, icolumn, walksDomains, cleanDomains))
            {
                this.Backtracking(iClean, irow, icolumn, nPaseos, nVolunteers, walksTable, walksDomains, cleanTable, cleanDomains);
            }
            else
            {
                if(iClean == 0 && irow == 0 && icolumn == 0)
                {
                    return;
                }
                else
                {
                    if(icolumn == 0 && iClean > irow)
                    {
                        //cleandomains[iClean] = Totes les tuples de gossos - tuples de gossos ja al cleanTable + tupla de gossos de la cleanTable[iClean-1]
                        //tupla de gossos de la cleanTable[iClean-1] afegir a la resta de dominis on no hi ha gossos assignat
                        this.ReassignCleanDomain(iClean,nPaseos, cleanTable, cleanDomains);
                        this.Backtracking(iClean-1, irow, icolumn, nPaseos, nVolunteers, walksTable, walksDomains, cleanTable, cleanDomains);
                    }
                    else if(icolumn == 0)
                    {
                        //Walkdomains[irow][icolumn] = Tots els gossos - gossos ja al walkTable + gos de la walkTable[irow-1][nVolunteers-1]
                        //gos de la walkTable[irow-1][nVolunteers-1] afegir a la resta de dominis on no hi ha gos assignat
                        this.ReassignWalkDomain(irow-1,nVolunteers-1, irow, icolumn, nPaseos,nVolunteers, walksTable, walksDomains);
                        this.Backtracking(iClean, irow-1, nVolunteers-1, nPaseos, nVolunteers, walksTable, walksDomains, cleanTable, cleanDomains);
                    }
                    else
                    {
                        //Walkdomains[irow][icolumn] = Tots els gossos - gossos ja al walkTable + gos de la walkTable[irow][icolumn-1]
                        //gos de la walkTable[irow][icolumn-1] afegir a la resta de dominis on no hi ha gos assignat
                        this.ReassignWalkDomain(irow,icolumn-1, irow, icolumn, nPaseos,nVolunteers, walksTable, walksDomains);
                        this.Backtracking(iClean, irow, icolumn-1, nPaseos, nVolunteers, walksTable, walksDomains, cleanTable, cleanDomains);
                    }
                }
            }*/
        }

    }

    public int Selection(VertexDog vertexDog, DogGraph dogGraph, List<Cage> cages, ArrayList<ArrayList<Dog>> walksSolution, ArrayList<ArrayList<Dog>> cleanSolution) {
        //Buscamos uno de los perros que se encuentran en una de las jaula de xeniles que comparten
        /*Set<VertexDog> vertexs = dogGraph.vertexSet();
        Set<Integer> sameCageWeight = new TreeSet<Integer>();
        sameCageWeight.add(3);

        VertexDog maxDegreeVertex = new VertexDog();
        int maxDegree = 0;
        for(VertexDog dog : vertexs)
        {
            if(!dog.assigned)
            {
                Cage cage = new Cage();
                for(int i = 0; i < 0; ++i)
                {
                    cage = cages.get(i);
                    if(cage.id == dog.idCage)
                    {
                        break;
                    }
                }
                if(XenilesAssigned() || (!XenilesAssigned() && cage.zone == Constants.CAGE_ZONE_XENILES))
                {
                    Set<Double> edges = dogGraph.edgesOf(dog);
                    edges.retainAll(new HashSet<Integer>());
                    int degree3 = edges.size();
                    if (degree3 > maxDegree) {
                        maxDegree = degree3;
                        maxDegreeVertex = dog;
                    }
                }
            }
        }
        return maxDegreeVertex;*/
        int paseo = 0;
        if(vertexDog.walktype == Constants.WT_INTERIOR)
        {
            paseo = vertexDog.cleandomain.get(0);
            cleanSolution.get(paseo).add(vertexDog);
            vertexDog.cleandomain.remove(0);
            vertexDog.assigned = true;
        }
        else if (vertexDog.walktype == Constants.WT_EXTERIOR)
        {
            paseo = vertexDog.walkdomain.get(0);
            walksSolution.get(paseo).add(vertexDog);
            vertexDog.walkdomain.remove(0);
            vertexDog.assigned = true;
        }
        return paseo;
    }

    public void UpdateDomain(int paseo, DogGraph dogGraph)
    {
        Set<VertexDog> vertexs = dogGraph.vertexSet();
        for(VertexDog dog : vertexs)
        {
            if (!dog.assigned)
            {
                dog.walkdomain.remove(paseo);
            }
        }
    }

    public boolean Validate(int paseo, VertexDog vertexDog, DogGraph dogGraph, List<Cage> cages, int nVolunteers, ArrayList<ArrayList<Dog>> walksSolution, ArrayList<ArrayList<Dog>> cleanSolution)
    {
        if(vertexDog.walktype == Constants.WT_INTERIOR)
        {
            //Comprobar que el resto de perros interiores del paseo son compatibles
            if(!this.ValidateCompatibilities(paseo,vertexDog,dogGraph,cleanSolution))
            {
                return false;
            }
        }
        else
        {
            //No puede haber más perros en un paseo que voluntarios
            if(walksSolution.get(paseo).size() > nVolunteers)
            {
                return false;
            }

            //Validar que no existan incompatibilidades en los perros del paseo
            if(!this.ValidateCompatibilities(paseo,vertexDog,dogGraph,walksSolution))
            {
                return false;
            }
        }
        //Comprobamos que el resto de perros de la jaula (que pueden salir) caben en el paseo
        Set<EdgeDog> edges = dogGraph.edgesOfByWeight(vertexDog, Constants.EDGE_SAME_CAGE_VALUE);
        int exteriorDogsInCage = 0;
        int assignedExteriorDogsInCage = 0;
        for(EdgeDog edge : edges)
        {
            VertexDog other = new VertexDog();
            if(edge.v1 == vertexDog)
            {
                other = edge.v2;
            }
            else
            {
                other = edge.v1;
            }
            if(other.walktype == Constants.WT_EXTERIOR)
            {
                exteriorDogsInCage++;
            }
            if(other.assigned && other.walktype == Constants.WT_EXTERIOR)
            {
                assignedExteriorDogsInCage++;
            }
            //Comprobamos que los perros ya asignados de la misma jaula se encuentren asignados
            //al mismo paseo
            if(other.assigned && !walksSolution.get(paseo).contains(other))
            {
                return false;
            }
        }
        //Si los perros que quedan por asignar de la jaula es mayor que
        //los perros que quedan por assignar al paseo, como es obvio no es posible.
        if(exteriorDogsInCage - assignedExteriorDogsInCage > nVolunteers - walksSolution.get(paseo).size())
        {
            return false;
        }
        return true;
    }

    public boolean ValidateCompatibilities(int paseo, VertexDog vertexDog, DogGraph dogGraph, ArrayList<ArrayList<Dog>> solution)
    {
        List<Dog> dogs = solution.get(paseo);
        for(int i = 0; i < dogs.size(); ++i)
        {
            VertexDog iDog = (VertexDog) dogs.get(i);
            if(iDog.id != vertexDog.id && dogGraph.getEdge(vertexDog,iDog).weight == Constants.EDGE_INCOMPATIBLE_VALUE)
            {
                return false;
            }
        }
        return true;
    }





    //cleandomains[iClean] = Totes les tuples de gossos - tuples de gossos ja al cleanTable + tupla de gossos de la cleanTable[iClean-1]
    //tupla de gossos de la cleanTable[iClean-1] afegir a la resta de dominis on no hi ha gossos assignat
    public void ReassignCleanDomain(int iClean,int nPaseos, ArrayList <ArrayList<Dog>> cleanTable, ArrayList <ArrayList<ArrayList<Dog>>> cleanDomains)
    {
        ArrayList <ArrayList<Dog>> cleanDomain = nCleanDomain;

        for (int i = 0; i < iClean-1; ++i)
        {
            if(cleanDomain.contains(cleanTable.get(i)))
            {
                cleanDomain.remove(cleanTable.get(i));
            }
        }

        for(int i = iClean; i < nPaseos; ++i)
        {
            cleanDomains.get(i).clear();
            cleanDomains.get(i).addAll(cleanDomain);
        }
    }

    //Walkdomains[irow][icolumn] = Tots els gossos - gossos ja al walkTable + gos de la walkTable[irow-1][nVolunteers-1]
    //gos de la walkTable[irow-1][nVolunteers-1] afegir a la resta de dominis on no hi ha gos assignat
    public void ReassignWalkDomain(int lastRow, int lastColumn, int irow,int icolumn, int nPaseos, int nVolunteers, Dog[][] walksTable,ArrayList<ArrayList<ArrayList<Dog>>> walksDomains)
    {
        ArrayList<Dog> walkDomain = nWalkDomain;
        for(int i = 0; i < lastRow; ++i)
        {
            for(int j = 0; j < lastColumn; ++j)
            {
                if(walkDomain.contains(walksTable[i][j]))
                {
                    walkDomain.remove(walksTable[i][j]);
                }
            }
        }

        for(int i = irow; i < nPaseos; ++i)
        {
            for(int j = icolumn; j < nVolunteers; ++j)
            {
                walksDomains.get(i).get(j).clear();
                walksDomains.get(i).get(j).addAll(walkDomain);
            }
        }

    }

    public boolean DomainEmpty(int iClean, int irow, int icolumn, ArrayList<ArrayList<ArrayList<Dog>>> walksDomains,ArrayList <ArrayList<ArrayList<Dog>>> cleanDomains)
    {
        if(iClean==irow)
        {
            //Check values in domain[iClean]
            if(cleanDomains.get(iClean).size() == 0)
            {
                return true;
            }
        }
        else
        {
            //Check values in domain[irow][icolumn]
            if(walksDomains.get(irow).get(icolumn).size() == 0)
            {
                return true;
            }
        }
        return false;
    }

    public int GetExteriorDogs(int cageId)
    {
        int count = 0;
        for (int i = 0; i < listDogsPerCage.get(cageId).size(); ++i)
        {
            String dogName = listDogsPerCage.get(cageId).get(i).name;
            if(listDogsPerCage.get(cageId).get(i).walktype == Constants.WT_EXTERIOR)
            {
                count++;
            }
        }
        return count;
    }
}
