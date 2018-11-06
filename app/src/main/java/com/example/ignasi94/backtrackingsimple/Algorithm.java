package com.example.ignasi94.backtrackingsimple;

import android.util.Log;

import org.apache.log4j.LogManager;
import org.w3c.dom.DOMImplementation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Algorithm {

    public Dog[][] walksTable;
    public ArrayList<ArrayList<ArrayList<Dog>>> walksDomains;
    public ArrayList <ArrayList<Dog>> cleanTable;
    public ArrayList <ArrayList<ArrayList<Dog>>> cleanDomains;
    public ArrayList<ArrayList<Dog>> listDogsPerCage;
    public ArrayList<ArrayList<Dog>> nCleanDomain;
    public ArrayList<Dog> nWalkDomain;
    public ArrayList<Cage> cages;
    public final Logger Log = Logger.getLogger("Logger");
    public FileHandler fh;

    public Algorithm() {}

    public Algorithm(List<Dog> dogs, List<Cage> cages, List<Volunteer> volunteers)
    {
        SimpleBacktracking(dogs,cages,volunteers);
    }

    public void SimpleBacktracking(List<Dog> dogs, List<Cage> cages, List<Volunteer> volunteers)
    {
        int nPaseos = 4;
        this.cages = (ArrayList) cages;
        listDogsPerCage = new ArrayList<ArrayList<Dog>>();
        this.CreateListDogsPerCage(dogs,cages);
        //Solución paseos
        //   V1  V2  V3  V4
        //P1
        //P2
        //P3
        //P4
        this.walksTable = new Dog[nPaseos][volunteers.size()];
        //Dominios paseos
        //En walksDomains[i][j] tenemos el dominio para la posición[i][j] de la walkstable
        //El dominio se basa en: {Perro1,Perro2,...,PerroN}
        this.walksDomains = this.CreateSimplifiedWalkDomains(cages, nPaseos,volunteers.size());
        //Solución limpeza
        this.cleanTable = new ArrayList <ArrayList <Dog> >();
        //Dominios limpieza
        //En cleanDomains[i] tenemos el dominio para la posición
        //El dominio se basa en:{{PerroInterior1 (jaula1),PerroInterior2(jaula1)},...,{PerroInteriorX (jaulaN),PerroInteriorY(jaulaN)},...,
        //                       {PerroInterior1 (jaula1),PerroInterior2(jaula1),PerroExterior3(jaula1)},...,{PerroInteriorX (jaulaN),PerroInteriorY(jaulaN),PerroExteriorZ(jaulaN)}}
        this.cleanDomains = this.CreateCleanDomains(dogs,cages,nPaseos);
        this.Backtracking(0,0,0,nPaseos,volunteers.size(),dogs.size());
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

    public ArrayList<ArrayList<ArrayList<Dog>>> CreateWalkDomains(List<Dog> dogs, int nPaseos, int nVolunteers)
    {
        ArrayList<ArrayList<ArrayList<Dog>>> walksDomains = new ArrayList<ArrayList<ArrayList<Dog>>>();
        nWalkDomain = new ArrayList<Dog>();
        for(int k = 0; k < dogs.size(); k++) {
            Dog kDog = dogs.get(k);
            if(kDog.walktype == Constants.WT_EXTERIOR)
            {
                nWalkDomain.add(kDog);
            }
        }
        for(int i = 0; i < nPaseos; i++)
        {
            ArrayList<ArrayList<Dog>> iDomains = new ArrayList<ArrayList<Dog>>();
            for(int j = 0; j < nVolunteers; j++)
            {
                ArrayList<Dog> nWalkDomainClone = (ArrayList<Dog>) nWalkDomain.clone();
                iDomains.add(j,nWalkDomainClone);
            }
            walksDomains.add(i,iDomains);
        }
        return walksDomains;
    }

    public ArrayList<ArrayList<ArrayList<Dog>>> CreateSimplifiedWalkDomains(List<Cage> cages , int nPaseos, int nVolunteers)
    {
        ArrayList<ArrayList<ArrayList<Dog>>> walksDomains = new ArrayList<ArrayList<ArrayList<Dog>>>();
        ArrayList<Dog> nXenilesWalkDomain = new ArrayList<Dog>();
        this.nWalkDomain = new ArrayList<Dog>();
        for (int i = 0; i < cages.size(); ++i)
        {
            Cage cage = cages.get(i);
            if(this.listDogsPerCage.get(cage.id).size() > 0) {
                Dog dog = GetFirstExterior(cage.id);
                if(dog != null) {
                    if (cage.zone == Constants.CAGE_ZONE_XENILES) {
                        nXenilesWalkDomain.add(dog);
                    } else {
                        this.nWalkDomain.add(dog);
                    }
                }
            }
        }
        this.nWalkDomain.addAll(0, nXenilesWalkDomain);

        for(int i = 0; i < nPaseos; i++)
        {
            ArrayList<ArrayList<Dog>> iDomains = new ArrayList<ArrayList<Dog>>();
            for(int j = 0; j < nVolunteers; j++)
            {
                ArrayList<Dog> nWalkDomainClone = (ArrayList<Dog>) nWalkDomain.clone();
                iDomains.add(j,nWalkDomainClone);
            }
            walksDomains.add(i,iDomains);
        }
        return walksDomains;
    }
    public ArrayList <ArrayList<ArrayList<Dog>>> CreateCleanDomains(List<Dog> dogs, List<Cage> cages, int nPaseos)
    {
        ArrayList <ArrayList<ArrayList<Dog>>> cleanDomains = new ArrayList <ArrayList <ArrayList<Dog>>>();
        nCleanDomain = this.CreateSingleCleanDomain(dogs,cages,nPaseos);
        for(int i = 0; i < nPaseos; i++)
        {
            ArrayList<ArrayList<Dog>> nCleanDomainClone = (ArrayList<ArrayList<Dog>>) nCleanDomain.clone();
            cleanDomains.add(nCleanDomainClone);
        }
        return cleanDomains;
    }
    public ArrayList<ArrayList<Dog>> CreateSingleCleanDomain(List<Dog> dogs, List<Cage> cages, int nPaseos)
    {
        ArrayList<ArrayList<Dog>> nWalkInteriorDomain = new ArrayList<ArrayList<Dog>>();
        ArrayList<ArrayList<Dog>> nWalkallCageDomain = new ArrayList<ArrayList<Dog>>();
        for(int j = 0; j < cages.size(); j++) {
            ArrayList<Dog> onlyInteriorDomain = new ArrayList<Dog>();
            ArrayList<Dog> allCageDomain = new ArrayList<Dog>();
            Cage cage = cages.get(j);
            if(cage.zone.contentEquals(Constants.CAGE_ZONE_XENILES))
            {
                for(int k = 0; k < dogs.size(); k++)
                {
                    Dog dog = dogs.get(k);
                    if(dog.idCage == cage.numCage)
                    {
                        if(dog.walktype == Constants.WT_INTERIOR)
                        {
                            onlyInteriorDomain.add(dog);
                        }
                        allCageDomain.add(dog);
                    }
                }
            }
            if(onlyInteriorDomain.size() > 0)
            {
                nWalkInteriorDomain.add(onlyInteriorDomain);
            }
            if(allCageDomain.size() > 0)
            {
                boolean add = false;
                for(int i = 0; i < allCageDomain.size(); ++i) {
                    Dog dog = allCageDomain.get(i);
                    if(dog.walktype == Constants.WT_EXTERIOR)
                    {
                        add = true;
                    }
                }
                if(add) {
                    nWalkallCageDomain.add(allCageDomain);
                }
            }
        }
        nWalkInteriorDomain.addAll(nWalkallCageDomain);
        return nWalkInteriorDomain;
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
    public void Backtracking(int iClean, int irow, int icolumn, int nPaseos, int nVolunteers, int ndogs)
    {
        Log.log(Level.INFO, String.format("Backtracking: iclean = %d --- irow = %d --- icolumn = %d", iClean, irow, icolumn));
        //inicio
        boolean emptyDomain = DomainEmpty(iClean, irow, icolumn);
        this.Selection(iClean, irow, icolumn, emptyDomain);
        if (this.Validate(iClean, irow, icolumn, nVolunteers, emptyDomain, ndogs, nPaseos)) {
            //Eliminar de tots els dominis
            this.AssignCageFriends(iClean, irow, icolumn, emptyDomain);
            this.UpdateDomain(iClean, irow, icolumn, emptyDomain);
            int added = 0;
            if(iClean > irow)
            {
                added = GetExteriorDogs(this.walksTable[irow][icolumn].idCage);
            }
            if (iClean == nPaseos && irow == nPaseos - 1 && icolumn + added >= nVolunteers) {
                //Solución
                //no hacer nada
            } else {
                if (iClean == irow) {
                    this.Backtracking(iClean + 1, irow, icolumn, nPaseos, nVolunteers, ndogs);
                } else {
                    if (icolumn + added >= nVolunteers) {
                        this.Backtracking(iClean, irow + 1, 0, nPaseos, nVolunteers, ndogs);
                    } else {
                        this.Backtracking(iClean, irow, icolumn + added, nPaseos, nVolunteers, ndogs);
                    }
                }
            }
        } else {
            //Eliminar del domini de la posició actual
            //UpdateThisDomain
            if (!this.DomainEmpty(iClean, irow, icolumn)) {
                this.Backtracking(iClean, irow, icolumn, nPaseos, nVolunteers, ndogs);
            } else {
                if (iClean == 0 && irow == 0 && icolumn == 0) {
                    return;
                } else {
                    if (ndogs < nPaseos * nVolunteers) {
                        this.walksTable[irow][icolumn] = null;
                        if (icolumn + 1 == nVolunteers) {
                            this.Backtracking(iClean, irow + 1, 0, nPaseos, nVolunteers, ndogs);
                        } else {
                            this.Backtracking(iClean, irow, icolumn + 1, nPaseos, nVolunteers, ndogs);
                        }
                    } else if (icolumn == 0 && iClean > irow) {
                        //cleandomains[iClean] = Totes les tuples de gossos - tuples de gossos ja al cleanTable + tupla de gossos de la cleanTable[iClean-1]
                        //tupla de gossos de la cleanTable[iClean-1] afegir a la resta de dominis on no hi ha gossos assignat
                        this.walksTable[irow][0] = null;
                        this.walksDomains.get(irow).get(0).addAll(this.walksDomains.get(irow).get(1));
                        this.cleanTable.remove(iClean-1);
                        //this.ReassignCleanDomain(iClean, nPaseos);
                        this.Backtracking(iClean - 1, irow, icolumn, nPaseos, nVolunteers, ndogs);
                    } else if (icolumn == 0) {
                        //Walkdomains[irow][icolumn] = Tots els gossos - gossos ja al walkTable + gos de la walkTable[irow-1][nVolunteers-1]
                        //gos de la walkTable[irow-1][nVolunteers-1] afegir a la resta de dominis on no hi ha gos assignat
                        //this.ReassignWalkDomain(irow - 1, nVolunteers - 1, irow, icolumn, nPaseos, nVolunteers);
                        this.ReassignCleanDomain(iClean, irow, icolumn, nPaseos, nVolunteers);
                        this.Backtracking(iClean, irow - 1, nVolunteers - 1, nPaseos, nVolunteers, ndogs);
                    } else {
                        //Walkdomains[irow][icolumn] = Tots els gossos - gossos ja al walkTable + gos de la walkTable[irow][icolumn-1]
                        //gos de la walkTable[irow][icolumn-1] afegir a la resta de dominis on no hi ha gos assignat
                        int dogsInCage = GetExteriorDogs(this.walksTable[irow][icolumn-1].idCage);
                        this.ReassignWalkDomain(irow, icolumn - 1, irow, icolumn, nPaseos, nVolunteers);
                        this.Backtracking(iClean, irow, icolumn - dogsInCage, nPaseos, nVolunteers, ndogs);
                    }
                }
            }
        }
    }

    public void Selection(int iClean, int irow, int icolumn, boolean emptyDomain) {
        if(!emptyDomain) {
            if (iClean == irow) {
                ArrayList<Dog> dogsToAssignClean = (ArrayList<Dog>) this.cleanDomains.get(iClean).get(0).clone();
                this.cleanDomains.get(iClean).remove(dogsToAssignClean);
                this.cleanTable.add(iClean, dogsToAssignClean);
            } else {

                Dog dogToAssignWalk = this.walksDomains.get(irow).get(icolumn).get(0);
                this.walksDomains.get(irow).get(icolumn).remove(dogToAssignWalk);
                this.walksTable[irow][icolumn] = dogToAssignWalk;
            }
        }
    }

    public boolean Validate(int iClean, int irow, int icolumn,  int nVolunteers, boolean emptyDomain, int ndogs, int nPaseos)
    {
        if(emptyDomain)
        {
            if(iClean == irow)
            {
                return false;
            }
            else if(iClean > irow && ndogs < nPaseos * nVolunteers)
            {
                   return true;
            }
            else {
                return false;
            }
        }
        if( iClean == irow)
        {
            Dog assigned = this.cleanTable.get(irow).get(0);
            //Comprobar que el resto de perros de la jaula (que pueden salir) caben en el npaseo
            int dogsInCage = this.GetExteriorDogs(assigned.idCage);
            if(dogsInCage > nVolunteers )
            {
                return false;
            }
        }
        else
        {
            ArrayList<Integer> cagesValidated = new ArrayList<Integer>();
            int totalDogsiRow = 0;
            for(int i = 0; i < this.walksTable[0].length; i++) {
                if (this.walksTable[irow][i] != null) {
                    Dog assigned = this.walksTable[irow][i];
                    if (!cagesValidated.contains(assigned.idCage)) {
                        cagesValidated.add(assigned.idCage);
                        int dogsInCage = this.GetExteriorDogs(assigned.idCage);
                        totalDogsiRow += dogsInCage;
                    }
                }
            }
            if(totalDogsiRow > nVolunteers)
            {
                return false;
            }
            Dog assigned = this.walksTable[irow][icolumn];
            //Comprobar que si hay perros interiores estos estan asignados, en caso que no return false
            for (int j = 0; j < this.listDogsPerCage.get(assigned.idCage).size(); ++j)
            {
                Dog cageDog = this.listDogsPerCage.get(assigned.idCage).get(j);
                if(cageDog.walktype == Constants.WT_INTERIOR && !this.cleanTable.get(irow).contains((cageDog)))
                {
                    return false;
                }
            }
        }
        return true;
    }

    public void UpdateDomain(int iClean, int irow, int icolumn, boolean emptyDomain)
    {
        boolean remove = false;
        if(!emptyDomain) {
            if (iClean == irow) {
                for (int i = iClean + 1; i < this.cleanDomains.size(); ++i) {
                    this.cleanDomains.get(i).remove(this.cleanTable.get(iClean));
                }
                for(int j = 0; j < this.cleanTable.get(iClean).size(); ++j)
                {
                    Dog dog = this.cleanTable.get(iClean).get(j);
                    if(dog.walktype == Constants.WT_EXTERIOR)
                    {
                        for (int i = 0; i < this.walksDomains.size(); ++i) {
                            for (int z = 0; z < this.walksDomains.get(0).size(); ++z) {
                                if (i == irow && z == icolumn) {
                                    remove = true;
                                }
                                if (remove) {
                                    if(this.walksDomains.get(i).get(z).contains(dog))
                                    {
                                        this.walksDomains.get(i).get(z).remove(dog);
                                    }
                                }

                            }
                        }
                    }
                }
            } else {
                int added = this.GetExteriorDogs(this.walksTable[irow][icolumn].idCage);
                for (int i = 0; i < this.walksDomains.size(); ++i) {
                    for (int j = 0; j < this.walksDomains.get(0).size(); ++j) {
                        if (remove) {
                            for (int z = 0; z < added; ++z) {
                                this.walksDomains.get(i).get(j).remove(this.walksTable[irow][icolumn+z]);
                            }
                        }
                        if (i == irow && j == icolumn) {
                            remove = true;
                        }
                    }
                }
                //Remove assigned dogs for CleanDomains
                ArrayList<Dog> dogsInCage = this.listDogsPerCage.get(this.walksTable[irow][icolumn].idCage);
                for (int i = iClean; i < this.cleanDomains.size(); ++i) {
                    if(this.cleanDomains.get(i).contains(dogsInCage))
                    {
                        this.cleanDomains.get(i).remove(dogsInCage);
                    }
                }
            }
        }
    }

    public void AssignCageFriends(int iClean, int irow, int icolumn, boolean emptyDomain)
    {
        if(iClean > irow && !emptyDomain)
        {
            Dog assigned = this.walksTable[irow][icolumn];
            int friendsAdded = 1;
            ArrayList<Dog> allDogsbyCage = this.listDogsPerCage.get(assigned.idCage);
            for (int i = 0; i < allDogsbyCage.size(); ++i) {
                Dog dog = allDogsbyCage.get(i);
                if (assigned != dog && dog.walktype == Constants.WT_EXTERIOR) {
                    this.walksTable[irow][icolumn + friendsAdded] = dog;
                    friendsAdded++;
                }
            }
        }
    }
    //cleandomains[iClean] = Totes les tuples de gossos - tuples de gossos ja al cleanTable + tupla de gossos de la cleanTable[iClean-1]
    //tupla de gossos de la cleanTable[iClean-1] afegir a la resta de dominis on no hi ha gossos assignat
    public void ReassignCleanDomain(int iClean, int irow, int icolumn, int nPaseos, int nVolunteers)
    {
        ArrayList <ArrayList<Dog>> cleanDomain = (ArrayList<ArrayList<Dog>>) this.nCleanDomain.clone();
        for (int i = 0; i < iClean; ++i)
        {
            if(cleanDomain.contains(this.cleanTable.get(i)))
            {
                cleanDomain.remove(this.cleanTable.get(i));
            }
        }
        for(int i = 0; i < nPaseos; ++i)
        {
            for(int j = 0; j < nVolunteers; ++j)
            {
                if(i == irow && i == icolumn)
                {
                    break;
                }
                Dog dog = this.walksTable[i][j];
                if(dog != null) {
                    ArrayList<Dog> dogsInCage = this.listDogsPerCage.get(dog.idCage);
                    if (cleanDomain.contains(dogsInCage)) {
                        cleanDomain.remove(dogsInCage);
                    }
                }
            }
        }

        for(int i = iClean; i < nPaseos; ++i)
        {
            this.cleanDomains.get(i).clear();
            this.cleanDomains.get(i).addAll(cleanDomain);
        }
    }
    //Walkdomains[irow][icolumn] = Tots els gossos - gossos ja al walkTable + gos de la walkTable[irow-1][nVolunteers-1]
    //gos de la walkTable[irow-1][nVolunteers-1] afegir a la resta de dominis on no hi ha gos assignat
    public void ReassignWalkDomain(int lastRow, int lastColumn, int irow,int icolumn, int nPaseos, int nVolunteers)
    {
        int dogsInCage = GetExteriorDogs(this.walksTable[lastRow][lastColumn - 1].idCage);
        ArrayList<Dog> walkDomain = (ArrayList<Dog>) this.nWalkDomain.clone();
        for (int i = 0; i <= lastRow; ++i) {
            for (int j = 0; j < nVolunteers; ++j) {
                if (i == lastRow && j == lastColumn - dogsInCage + 1) {
                    break;
                }
                if (walkDomain.contains(this.walksTable[i][j])) {
                    walkDomain.remove(this.walksTable[i][j]);
                }
            }
        }
        for(int i = 0; i <= irow; ++i)
        {
            for(int j = 0; j < this.cleanTable.get(i).size(); ++j)
            {
                if(walkDomain.contains(this.cleanTable.get(i).get(j)))
                {
                    walkDomain.remove(this.cleanTable.get(i).get(j));
                }
            }
        }

        for (int i = irow; i < nPaseos; ++i) {
            for (int j = 0; j < nVolunteers; ++j) {
                if (i == irow && j == 0) {
                    j = icolumn - dogsInCage + 1;
                }
                this.walksDomains.get(i).get(j).clear();
                this.walksDomains.get(i).get(j).addAll(walkDomain);
            }
        }

        //Reassign cleandomain
        Cage cage = this.GetCageById(this.walksTable[irow][lastColumn].idCage);
        ArrayList<Dog> dogsInCageList = this.listDogsPerCage.get(cage.id);
        if(cage.zone == Constants.CAGE_ZONE_XENILES)
        {
            for (int i = irow; i < this.cleanDomains.size(); ++i) {
                if(!this.cleanDomains.get(i).contains(dogsInCageList))
                {
                    this.cleanDomains.get(i).add(dogsInCageList);
                }
            }
        }

        this.walksTable[irow][icolumn] = null;
        for (int i = 0; i < dogsInCage; ++i) {
            this.walksTable[irow][lastColumn - i] = null;
        }
    }
    public boolean DomainEmpty(int iClean, int irow, int icolumn)
    {
        if(iClean==irow)
        {
            //Check values in domain[iClean]
            if(this.cleanDomains.get(iClean).size() == 0)
            {
                return true;
            }
        }
        else
        {
            //Check values in domain[irow][icolumn]
            if(this.walksDomains.get(irow).get(icolumn).size() == 0)
            {
                return true;
            }
        }
        return false;
    }
    public int GetExteriorDogs(int cageId)
    {
        int count = 0;
        for (int i = 0; i < this.listDogsPerCage.get(cageId).size(); ++i)
        {
            String dogName = this.listDogsPerCage.get(cageId).get(i).name;
            if(this.listDogsPerCage.get(cageId).get(i).walktype == Constants.WT_EXTERIOR)
            {
                count++;
            }
        }
        return count;
    }

    public Cage GetCageById(int cageId)
    {
        for(int i = 0; i < this.cages.size(); i++)
        {
            Cage cage = this.cages.get(i);
            if(cage.id == cageId)
            {
                return cage;
            }
        }
        return null;
    }

    public Dog GetFirstExterior(int idCage)
    {
        ArrayList<Dog> dogs = this.listDogsPerCage.get(idCage);
        for(int j = 0; j < dogs.size(); ++j)
        {
            Dog dog = dogs.get(j);
            if(dog.walktype == Constants.WT_EXTERIOR)
            {
                return dog;
            }
        }
        return null;
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
        if (vertexDog.walktype == Constants.WT_INTERIOR) {
            paseo = vertexDog.cleandomain.get(0);
            cleanSolution.get(paseo).add(vertexDog);
            vertexDog.cleandomain.remove(0);
            vertexDog.assigned = true;
        } else if (vertexDog.walktype == Constants.WT_EXTERIOR) {
            paseo = vertexDog.walkdomain.get(0);
            walksSolution.get(paseo).add(vertexDog);
            vertexDog.walkdomain.remove(0);
            vertexDog.assigned = true;
        }
        return paseo;
    }
}
