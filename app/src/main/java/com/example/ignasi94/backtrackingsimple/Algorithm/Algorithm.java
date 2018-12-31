package com.example.ignasi94.backtrackingsimple.Algorithm;

import android.view.View;
import android.widget.ArrayAdapter;

import com.example.ignasi94.backtrackingsimple.Estructuras.Cage;
import com.example.ignasi94.backtrackingsimple.Estructuras.Dog;
import com.example.ignasi94.backtrackingsimple.Estructuras.Grafo.CageGraf.CageGraph;
import com.example.ignasi94.backtrackingsimple.Estructuras.Grafo.CageGraf.EdgeCage;
import com.example.ignasi94.backtrackingsimple.Estructuras.Grafo.CageGraf.VertexCage;
import com.example.ignasi94.backtrackingsimple.Estructuras.Grafo.DogGraf.DogGraph;
import com.example.ignasi94.backtrackingsimple.Estructuras.Grafo.DogGraf.EdgeDog;
import com.example.ignasi94.backtrackingsimple.Estructuras.Grafo.DogGraf.TupleDog;
import com.example.ignasi94.backtrackingsimple.Estructuras.Volunteer;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerDog;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerWalks;
import com.example.ignasi94.backtrackingsimple.Estructuras.WalksInfo;
import com.example.ignasi94.backtrackingsimple.MergeUtils.MergeUtils;
import com.example.ignasi94.backtrackingsimple.Utils.Constants;

import org.jgrapht.Graph;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Algorithm {

    //WalkTable & Domains
    public Dog[][] walksTable;
    public ArrayList<ArrayList<ArrayList<Dog>>> walksDomains;
    public ArrayList<Dog> nWalkDomain;

    //Clean Table & Domains
    public ArrayList <ArrayList<Dog>> cleanTable;
    public ArrayList <ArrayList<ArrayList<Dog>>> cleanDomains;
    public ArrayList<ArrayList<Dog>> nCleanDomain;
    public ArrayList<ArrayList<Dog>> interiorFriendGroups;
    public Dictionary<Dog,Integer> cleanDogsAssigned;

    //Utils
    public ArrayList<ArrayList<Dog>> listDogsPerCage;
    public ArrayList<Cage> cages;
    public ArrayList<ArrayList<Integer>> walksConfig;
    public ArrayList<WalksInfo> walksMapping;

    //Graphs
    public DogGraph dogGraph;
    public CageGraph cageGraph;

    //Important Values
    int nPaseos;
    int totalWalks;
    int exteriorDogs;

    //Others
    public final Logger Log = Logger.getLogger("Logger");
    public FileHandler fh;
    List<VolunteerWalks> volunteers;

    public Algorithm() {}

    public Algorithm(List<Dog> dogs, List<Cage> cages, List<VolunteerWalks> volunteers)
    {
        SimpleBacktracking(dogs,cages,volunteers);
    }

    public void SimpleBacktracking(List<Dog> dogs, List<Cage> cages, List<VolunteerWalks> volunteers)
    {
        nPaseos = volunteers.get(0).nPaseos;
        this.cages = (ArrayList) cages;
        //Lista de perros por jaula
        listDogsPerCage = new ArrayList<ArrayList<Dog>>();
        cleanDogsAssigned = new Hashtable<Dog,Integer>();
        interiorFriendGroups = new ArrayList<ArrayList<Dog>>();
        this.volunteers = volunteers;
        this.CreateListDogsPerCage(dogs,cages);

        //Grafo
        this.dogGraph = CreateDogsGraph(dogs);
        this.cageGraph = CreateCagesGraph(cages);

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

        //Para cada paseos sabemos los voluntarios que realizaran ese ipaseo y cuales no
        this.walksConfig = this.WalksConfig((ArrayList) volunteers);
        //Calculamos los paseos totales que se pueden hacer
        this.totalWalks = this.TotalWalks();
        //Reordenamos los paseos de más paseos a menos
        this.OrderWalksTableByWalksCount();

        exteriorDogs = 0;
        for(int i = 0; i < dogs.size(); ++i)
        {
            if(dogs.get(i).walktype == Constants.WT_EXTERIOR)
            {
                ++exteriorDogs;
            }
        }

        this.Backtracking(0,0,0,nPaseos,volunteers.size(),dogs.size());

        //Modificar solución
        this.ReOrderWalksTableSolution(volunteers.size());
    }

    //Creación del grafo.
    //Este grafo relaciona los distintos perros. Estas relacionas tienen un peso:
    //Relación incompatible
    //Relación compatible
    //Relación con un perro de su misma jaula
    public DogGraph CreateDogsGraph(List<Dog> dogs) {
        DogGraph dogGraph = new DogGraph(dogs.size());

        //Vertices
        for (int i = 0; i < dogs.size(); ++i) {
            dogGraph.addVertex(dogs.get(i));
        }

        //Aristas
        for (int i = 0; i < dogs.size(); ++i) {
            Dog iDog = dogs.get(i);
            boolean hasInterioriDog = iDog.HasInteriorPartner(dogs);
            for (int j = i + 1; j < dogs.size(); ++j) {
                Dog jDog = dogs.get(j);
                if (iDog.idCage == jDog.idCage) {
                    EdgeDog edge = new EdgeDog(iDog,jDog, Constants.DOG_EDGE_SAME_CAGE_VALUE);
                    dogGraph.addEdge(iDog, jDog, edge);
                } else {
                    if (hasInterioriDog && jDog.HasInteriorPartner(dogs) && !InteriorAreFriends(iDog.idCage, jDog.idCage)) {
                        EdgeDog edge = new EdgeDog(iDog, jDog, Constants.DOG_EDGE_INCOMPATIBLE_VALUE);
                        dogGraph.addEdge(iDog, jDog, edge);
                    }
                    else if (hasInterioriDog && jDog.HasInteriorPartner(dogs) && InteriorAreFriends(iDog.idCage, jDog.idCage))
                    {
                        if(iDog.walktype == Constants.WT_INTERIOR && jDog.walktype == Constants.WT_INTERIOR)
                        {
                            EdgeDog edge = new EdgeDog(iDog, jDog, Constants.DOG_EDGE_INTERIOR_FRIENDS_VALUE);
                            dogGraph.addEdge(iDog, jDog, edge);
                        }
                        else
                        {
                            EdgeDog edge = new EdgeDog(iDog,jDog,Constants.DOG_EDGE_COMPATIBLE_VALUE);
                            dogGraph.addEdge(iDog, jDog, edge);
                        }
                    }
                    else {
                        EdgeDog edge = new EdgeDog(iDog,jDog,Constants.DOG_EDGE_COMPATIBLE_VALUE);
                        dogGraph.addEdge(iDog, jDog, edge);
                    }
                }
            }
        }
        return dogGraph;
    }

    //Creación del grafo.
    //Este grafo relaciona las distintas jaulas. Estas relacionas tienen un peso:
    //Relación incompatible (2 jaulas tienen perros interiores no amigos)
    //Relación compatible (0 o 1 jaula con perros interiores)
    //Relación compatible con interiores (2 jaulas tienen perros interiores y son amigos)
    public CageGraph CreateCagesGraph(List<Cage> cages) {
        CageGraph cagesGraph = new CageGraph(cages.size());

        //Vertices
        for (int i = 0; i < cages.size(); ++i) {
            Cage cage = cages.get(i);
            ArrayList<Dog> interiorDogsInCage = this.GetInteriorDogs(cage.id);
            ArrayList<Dog> friendDogs = this.GetInteriorCommonFriends(cage.id);
            cagesGraph.addVertex(new VertexCage(cage,interiorDogsInCage,friendDogs));
        }

        //Aristas
        for (int i = 0; i < cages.size(); ++i) {
            Cage iCage = cages.get(i);
            VertexCage iVertex = cagesGraph.getVertex(iCage.id);
            for (int j = i + 1; j < cages.size(); ++j) {
                Cage jCage = cages.get(j);
                VertexCage jVertex = cagesGraph.getVertex(jCage.id);
                if (iVertex.interiorDogsInCage.size() > 0 && jVertex.interiorDogsInCage.size() > 0) {
                    if(jVertex.friendDogs.containsAll(iVertex.interiorDogsInCage) &&
                       iVertex.friendDogs.containsAll(jVertex.interiorDogsInCage))
                    {
                        EdgeCage edge = new EdgeCage(iVertex,jVertex, Constants.CAGE_EDGE_INTERIOR_FRIENDS_VALUE);
                        cagesGraph.addEdge(iVertex, jVertex, edge);
                    }
                    else
                    {
                        EdgeCage edge = new EdgeCage(iVertex,jVertex, Constants.CAGE_EDGE_INCOMPATIBLE_VALUE);
                        cagesGraph.addEdge(iVertex, jVertex, edge);
                    }
                } else {
                    EdgeCage edge = new EdgeCage(iVertex,jVertex, Constants.CAGE_EDGE_COMPATIBLE_VALUE);
                    cagesGraph.addEdge(iVertex, jVertex, edge);
                }
            }
        }
        return cagesGraph;
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

        //Calculo Dominio ordenados por zona y tamaño de perros exteriores decreciente
        this.Create2OrderArray();
        /*this.nWalkDomain = new ArrayList<Dog>();
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
        this.nWalkDomain.addAll(0, nXenilesWalkDomain);*/

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
        ArrayList<ArrayList<Dog>> interiorFriendsGroups = new ArrayList<ArrayList<Dog>>();
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



        //Añadimos al principio del dominio los grupos de perros interiores
        //de distintas jaulas
        interiorFriendsGroups = this.GetInteriorFriendsGroupsDomain();
        interiorFriendsGroups.addAll(nWalkInteriorDomain);
        interiorFriendsGroups.addAll(nWalkallCageDomain);
        return interiorFriendsGroups;
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
            if (iClean == nPaseos && irow == nPaseos - 1 && icolumn + added >= this.iWalks(irow)) {
                //Solución
                //no hacer nada
            } else {
                if (iClean == irow) {
                    this.Backtracking(iClean + 1, irow, icolumn, nPaseos, nVolunteers, ndogs);
                } else {
                    if (icolumn + added >= this.iWalks(irow)) {
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
                if(iClean == irow)
                {
                    this.cleanTable.remove(iClean);
                }
                this.Backtracking(iClean, irow, icolumn, nPaseos, nVolunteers, ndogs);
            } else {
                if (iClean == 0 && irow == 0 && icolumn == 0) {
                    return;
                } else {
                    if (exteriorDogs < this.totalWalks) {
                        this.walksTable[irow][icolumn] = null;
                        if (iClean == nPaseos && irow == nPaseos - 1 && icolumn + 1 >= this.iWalks(irow)) {
                            //Solución
                            //no hacer nada
                        }
                        else if (icolumn + 1 == nVolunteers) {
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
                        this.Backtracking(iClean, irow - 1, this.iWalks(irow-1) - 1, nPaseos, nVolunteers, ndogs);
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
            else if(iClean > irow && ndogs < this.totalWalks)
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
            int dogsToAssign = 0;
            ArrayList<Dog> specialDogsInWalkRow = this.GetSpecialDogs(new ArrayList<Integer>(),irow);

            if(cleanTable.get(irow).size() > 0 && this.MoreThanOneCageClean(irow)) {
                dogsToAssign = GetDogsToAssign(irow);
            }
            else {
                //Comprobar que el resto de perros de la jaula (que pueden salir) caben en el npaseo
                dogsToAssign = this.GetExteriorDogs(assigned.idCage);
            }

            if(!this.AllSpecialDogsHaveAUniqueFavouriteVolunteer(specialDogsInWalkRow,irow))
            {
                return false;
            }
            else if (dogsToAssign > this.iWalks(irow)) {
                return false;
            }
        }
        else
        {
            //Comprobamos si caben todos los perros de la jaula en el paseo
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
            //Perros especialies (y exteriores) no asignados a la cleanTable. O sea, perros especiales
            //asignados o por asignar
            ArrayList<Dog> specialDogsInWalkRow = this.GetSpecialDogs(cagesValidated,irow);

            //Si se ha asignado a limpieza más de una jaula
            if(!this.AllSpecialDogsHaveAUniqueFavouriteVolunteer(specialDogsInWalkRow,irow))
            {
                return false;
            }
            else if(cleanTable.get(irow).size() > 0 && this.MoreThanOneCageClean(irow))
            {
                int dogsToAssign = GetDogsUnassignedToAssign(cagesValidated, irow);
                if(totalDogsiRow + dogsToAssign > this.iWalks(irow)) {
                    return false;
                }
            }
            //Si los perros exteriores que comparten jaula con el interior asignado a limpieza ya estan asignados
            else if(cleanTable.get(irow).size() > 0 && cagesValidated.contains(cleanTable.get(irow).get(0).idCage) && totalDogsiRow > this.iWalks(irow))
            {
                return false;
            }
            //Si estan asignados los interiores pero no los exteriores de la jaula en limpieza
            else if(cleanTable.get(irow).size() > 0 && this.CleanOnlyContainsInteriors(irow) && !cagesValidated.contains(cleanTable.get(irow).get(0).idCage)
                    &&  totalDogsiRow + this.GetExteriorDogs(cleanTable.get(irow).get(0).idCage) > this.iWalks(irow))
            {
                return false;
            }
            //Si estan asignados a limpieza todos los perros de la jaula(interior + exterior)
            else if(cleanTable.get(irow).size() > 0 && !this.CleanOnlyContainsInteriors(irow) && totalDogsiRow > this.iWalks(irow))
            {
                return false;
            }
            //Si no hay perros asignados a limpieza
            else if(cleanTable.get(irow).size() == 0 && totalDogsiRow > this.iWalks(irow))
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
                //Añadimos los perros asignado a la lista de perros asignados a limpieza
                for (int i = 0; i < this.cleanTable.get(iClean).size(); ++i) {
                    Dog dog = this.cleanTable.get(iClean).get(i);
                    this.cleanDogsAssigned.put(dog,1);
                }
                //Eliminamos de los dominios de limpieza los perros asignados
                for (int i = iClean + 1; i < this.cleanDomains.size(); ++i) {
                    this.cleanDomains.get(i).remove(this.cleanTable.get(iClean));
                }
                for(int j = 0; j < this.cleanTable.get(iClean).size(); ++j)
                {
                    Dog dog = this.cleanTable.get(iClean).get(j);
                    //Si el perro asignado a la limpieza es exterior si elimina de los dominios de paseo
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

                //Eliminamos todos los conjuntos con perros ya asignados a limpieza
                if(iClean+1 < this.cleanDomains.size())
                {

                    ArrayList<ArrayList<Dog>> newCleanDomain = (ArrayList<ArrayList<Dog>>) this.cleanDomains.get(iClean + 1).clone();
                    for (int i = 0; i < this.cleanDomains.get(iClean+1).size(); ++i) {
                        boolean eraseGroup = false;
                        //Per cada group de gossos mirem si s'ha d'eliminar
                        for(int j = 0; j < this.cleanDomains.get(iClean+1).get(i).size(); ++j)
                        {
                            Dog dog = this.cleanDomains.get(iClean+1).get(i).get(j);
                            Object value = cleanDogsAssigned.get(dog);
                            if(value != null)
                            {
                                eraseGroup = true;
                            }
                        }
                        if(eraseGroup)
                        {
                            newCleanDomain.remove(this.cleanDomains.get(iClean+1).get(i));
                        }
                    }

                    for (int i = iClean + 1; i < this.cleanDomains.size(); ++i) {
                        this.cleanDomains.get(i).clear();
                        this.cleanDomains.get(i).addAll(newCleanDomain);
                    }

                }
            } else {
                //Eliminamos de los posteriores dominios de paseo todos los perros de la jaula asignada
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
                //Se quita la jaula asignada de los dominios de limpieza posteriores
                ArrayList<Dog> dogsInCage = this.listDogsPerCage.get(this.walksTable[irow][icolumn].idCage);
                for (int i = iClean; i < this.cleanDomains.size(); ++i) {
                    if(this.cleanDomains.get(i).contains(dogsInCage))
                    {
                        this.cleanDomains.get(i).remove(dogsInCage);
                    }
                }
                //Eliminamos de los dominios de este paseo todos los perros incompatibles al asignado
                Dog assigned = this.walksTable[irow][icolumn];
                LinkedList<EdgeDog> edges = dogGraph.edgesOf(assigned);
                for(int i = icolumn; i < this.walksDomains.get(irow).size(); i++)
                {
                    for (EdgeDog edge : edges) {
                        if(edge.weight == Constants.DOG_EDGE_INCOMPATIBLE_VALUE)
                        {
                            if(edge.v1.id == assigned.id)
                            {
                                if(walksDomains.get(irow).get(icolumn).contains(edge.v2))
                                {
                                    walksDomains.get(irow).get(icolumn).remove(edge.v2);
                                }
                            }
                            else
                            {
                                if(walksDomains.get(irow).get(icolumn).contains(edge.v1))
                                {
                                    walksDomains.get(irow).get(icolumn).remove(edge.v1);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void AssignCageFriends(int iClean, int irow, int icolumn, boolean emptyDomain)
    {
        //Una vez asignado un perro y validado correctamente se asignan a las siguientes posiciones del paseo
        //sus compañeros exteriores
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

        //Eliminamos todos los conjuntos con perros ya asignados a limpieza
        ArrayList<ArrayList<Dog>> newCleanDomain = (ArrayList<ArrayList<Dog>>) cleanDomain.clone();
        for (int i = 0; i < cleanDomain.size(); ++i) {
            boolean eraseGroup = false;
            //Per cada group de gossos mirem si s'ha d'eliminar
            for(int j = 0; j < cleanDomain.get(i).size(); ++j)
            {
                Dog dog = cleanDomain.get(i).get(j);
                Object value = cleanDogsAssigned.get(dog);
                if(value != null)
                {
                    eraseGroup = true;
                }
            }
            if(eraseGroup)
            {
                newCleanDomain.remove(cleanDomain.get(i));
            }
        }

        for (int i = iClean + 1; i < this.cleanDomains.size(); ++i) {
            this.cleanDomains.get(i).clear();
            this.cleanDomains.get(i).addAll(newCleanDomain);
        }

        for(int i = iClean; i < nPaseos; ++i)
        {
            this.cleanDomains.get(i).clear();
            this.cleanDomains.get(i).addAll(cleanDomain);
        }
    }

    public void ReassignWalkDomain(int lastRow, int lastColumn, int irow,int icolumn, int nPaseos, int nVolunteers)
    {
        int dogsInCage = GetExteriorDogs(this.walksTable[lastRow][lastColumn].idCage);
        //walkDomain seran todos los perros no asignados (sólo se eliminan los asignados no tenemos que tratar
        //los incompatibles que hemos eliminado en UpdateDomain)
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

    public ArrayList<Dog> GetInteriorDogs(int cageId)
    {
        ArrayList<Dog> dogs = new ArrayList<Dog>();
        int count = 0;
        for (int i = 0; i < this.listDogsPerCage.get(cageId).size(); ++i)
        {
            Dog dog = this.listDogsPerCage.get(cageId).get(i);
            if(dog.walktype == Constants.WT_INTERIOR)
            {
                dogs.add(dog);
            }
        }
        return dogs;
    }

    public ArrayList<Dog> GetSpecialDogs(ArrayList<Integer> cagesInRow, int iRow)
    {
        ArrayList<Dog> specialDogs = new ArrayList<Dog>();
        ArrayList<Dog> exteriorSpecialDogsInCleanTable = new ArrayList<Dog>();
        for(int i = 0; i < cleanTable.get(iRow).size(); ++i)
        {
            Dog dog = cleanTable.get(iRow).get(i);
            if(dog.walktype == Constants.WT_EXTERIOR && dog.special)
            {
                exteriorSpecialDogsInCleanTable.add(dog);
            }

            if(!cagesInRow.contains(dog.idCage))
            {
                cagesInRow.add(dog.idCage);
            }
        }
        for(int i = 0; i < cagesInRow.size(); ++i)
        {
            ArrayList<Dog> allCagesInRowDogs = listDogsPerCage.get(cagesInRow.get(i));
            for(int j = 0; j < allCagesInRowDogs.size(); ++j)
            {
                Dog dog = allCagesInRowDogs.get(j);
                if(dog.special && dog.walktype == Constants.WT_EXTERIOR)
                {
                    specialDogs.add(dog);
                }
            }
        }
        specialDogs.removeAll(exteriorSpecialDogsInCleanTable);
        return specialDogs;
    }

    public boolean AllSpecialDogsHaveAUniqueFavouriteVolunteer(ArrayList<Dog> specialDogsInWalkRow, int iRow)
    {
        ArrayList<VolunteerWalks> volunteersAssignediRow = this.GetVolunteersAssignedIRow(iRow, volunteers);
        ArrayList<Dog> specialDogsAsFavourite = new ArrayList<Dog>();
        for(int j = 0; j < specialDogsInWalkRow.size(); ++j)
        {
            Dog dog = specialDogsInWalkRow.get(j);
            for(int i = 0; i < volunteersAssignediRow.size(); ++i)
            {
                Volunteer volunteer = volunteersAssignediRow.get(i);
                if(!specialDogsAsFavourite.contains(dog))
                {
                    for(int z = 0; z < volunteer.favouriteDogs.size(); ++z)
                    {
                        Dog favourite = volunteer.favouriteDogs.get(z);
                        if(dog.id == favourite.id) {
                            specialDogsAsFavourite.add(dog);
                            break;
                        }
                    }
                }
            }
        }
        return specialDogsInWalkRow.size() == specialDogsAsFavourite.size();
    }

    public ArrayList<Dog> GetInteriorCommonFriends(int cageId)
    {
        ArrayList<Dog> friendsDogs = new ArrayList<Dog>();
        boolean firstInterior = true;
        for (int i = 0; i < this.listDogsPerCage.get(cageId).size(); ++i)
        {
            Dog dog = this.listDogsPerCage.get(cageId).get(i);
            if(dog.walktype == Constants.WT_INTERIOR) {
                ArrayList<Dog> newFriendsDogs = new ArrayList<Dog>();
                if (firstInterior) {
                    friendsDogs.addAll(dog.friends);
                    firstInterior = false;
                } else {
                    for (int j = 0; j < dog.friends.size(); ++j) {
                        if (friendsDogs.contains(dog.friends.get(j))) {
                            newFriendsDogs.add(dog.friends.get(j));
                        }
                    }
                    friendsDogs = newFriendsDogs;
                }
            }
        }
        return friendsDogs;
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

    public Dog GetFirstExterior(int idCage) {
        ArrayList<Dog> dogs = this.listDogsPerCage.get(idCage);
        for (int j = 0; j < dogs.size(); ++j) {
            Dog dog = dogs.get(j);
            if (dog.walktype == Constants.WT_EXTERIOR) {
                return dog;
            }
        }
        return null;
    }



    private boolean CleanOnlyContainsInteriors (int irow)
    {
        ArrayList<Dog> dogs = this.cleanTable.get(irow);
        for(int i = 0; i < dogs.size(); ++i)
        {
            Dog dog  = dogs.get(i);
            if(dog.walktype == Constants.WT_EXTERIOR)
            {
                return false;
            }
        }
        return true;
    }

    //Creamos 3 listas de perros (una por zona) <idPerro, cantidad perros exteriores de su jaula>
    //Cada lista la ordenamos por cantidad de perros(exteriores) utilizando el grafo de más a menos
    //Juntamos las 3 listas: Xeniles -> Patios -> Cuarentenas
    //El resultado es el dominio que se tiene que asignar a cada posición de la matriz
    public void Create2OrderArray()
    {
        ArrayList<TupleDog> xeniles = new ArrayList<TupleDog>();
        ArrayList<TupleDog> patios = new ArrayList<TupleDog>();
        ArrayList<TupleDog> cuarentenas = new ArrayList<TupleDog>();

        for(int i = 0; i < this.cages.size(); ++i)
        {
            Cage cage = this.cages.get(i);
            Dog dog = this.GetFirstExterior(cage.id);
            if(dog != null) {
                List<EdgeDog> dogsInCage = dogGraph.edgesOfByWeight(dog, Constants.DOG_EDGE_SAME_CAGE_VALUE);
                int exteriorDogsInCage = dogsInCage.size() + 1;
                if (cage.zone == Constants.CAGE_ZONE_XENILES) {
                    for (int j = 0; j < dogsInCage.size(); ++j) {
                        EdgeDog edgeDog = dogsInCage.get(j);
                        if (edgeDog.v1.id == dog.id && edgeDog.v2.walktype != Constants.WT_EXTERIOR) {
                            exteriorDogsInCage--;
                        } else if (edgeDog.v2.id == dog.id && edgeDog.v1.walktype != Constants.WT_EXTERIOR) {
                            exteriorDogsInCage--;
                        }
                    }
                }
                if (cage.zone == Constants.CAGE_ZONE_XENILES) {
                    xeniles.add(new TupleDog(dog, exteriorDogsInCage));
                } else if (cage.zone == Constants.CAGE_ZONE_PATIOS) {
                    patios.add(new TupleDog(dog, exteriorDogsInCage));
                } else {
                    cuarentenas.add(new TupleDog(dog, exteriorDogsInCage));
                }
            }
        }

        TupleDog[] xenilesArray = new TupleDog[xeniles.size()];
        TupleDog[] patiosArray = new TupleDog[patios.size()];
        TupleDog[] cuarentenasArray = new TupleDog[cuarentenas.size()];
        this.MergeSortByDogsInCage(xeniles.toArray(xenilesArray));
        this.MergeSortByDogsInCage(patios.toArray(patiosArray));
        this.MergeSortByDogsInCage(cuarentenas.toArray(cuarentenasArray));

        List<TupleDog> concatMerge = new ArrayList<TupleDog>();
        List<TupleDog> xenilesList = new ArrayList<TupleDog>();
        for(int i = 0; i < xenilesArray.length; ++i)
        {
            xenilesList.add(xenilesArray[i]);
        }
        List<TupleDog> patiosList = new ArrayList<TupleDog>();
        for(int i = 0; i < patiosArray.length; ++i)
        {
            patiosList.add(patiosArray[i]);
        }
        List<TupleDog> cuarentenasList = new ArrayList<TupleDog>();
        for(int i = 0; i < cuarentenasArray.length; ++i)
        {
            cuarentenasList.add(cuarentenasArray[i]);
        }

        concatMerge = xenilesList;
        concatMerge.addAll(patiosList);
        concatMerge.addAll(cuarentenasList);

        ArrayList<Dog> solution = new ArrayList<Dog>();
        for(int i = 0; i < concatMerge.size(); ++i)
        {
            TupleDog tupleDog = concatMerge.get(i);
            solution.add(tupleDog.dog);
        }

        this.nWalkDomain = solution;
    }

    public void MergeSortByDogsInCage(TupleDog[] data)
    {
        if(data.length <= 1) return;               // Base case: just 1 elt

        TupleDog[] a = new TupleDog[data.length / 2];        // Split array into two
        TupleDog[] b = new TupleDog[data.length - a.length]; //   halves, a and b
        for(int i = 0; i < data.length; i++) {
            if(i < a.length) a[i] = data[i];
            else             b[i - a.length] = data[i];
        }

        MergeSortByDogsInCage(a);                              // Recursively sort first
        MergeSortByDogsInCage(b);                              //   and second half.

        int ai = 0;                                // Merge halves: ai, bi
        int bi = 0;                                //   track position in
        while(ai + bi < data.length) {             //   in each half.
            if(bi >= b.length || (ai < a.length && a[ai].dogsInCage > b[bi].dogsInCage)) {
                data[ai + bi] = a[ai]; // (copy element of first array over)
                ai++;
            } else {
                data[ai + bi] = b[bi]; // (copy element of second array over)
                bi++;
            }
        }
    }

    public void MergeSortByWalks(WalksInfo[] data)
    {
        if(data.length <= 1) return;               // Base case: just 1 elt

        WalksInfo[] a = new WalksInfo[data.length / 2];        // Split array into two
        WalksInfo[] b = new WalksInfo[data.length - a.length]; //   halves, a and b
        for(int i = 0; i < data.length; i++) {
            if(i < a.length) a[i] = data[i];
            else             b[i - a.length] = data[i];
        }

        MergeSortByWalks(a);                              // Recursively sort first
        MergeSortByWalks(b);                              //   and second half.

        int ai = 0;                                // Merge halves: ai, bi
        int bi = 0;                                //   track position in
        while(ai + bi < data.length) {             //   in each half.
            if(bi >= b.length || (ai < a.length && a[ai].iWalkCount > b[bi].iWalkCount)) {
                data[ai + bi] = a[ai]; // (copy element of first array over)
                ai++;
            } else {
                data[ai + bi] = b[bi]; // (copy element of second array over)
                bi++;
            }
        }
    }

    public ArrayList<ArrayList<Integer>> WalksConfig(ArrayList<VolunteerWalks> volunteerWalks)
    {
        ArrayList<ArrayList<Integer>> walksConfig = new ArrayList<ArrayList<Integer>>();
        for(int i = 0; i < nPaseos; ++i) {
            ArrayList<Integer> iWalkConfig = new ArrayList<Integer>();
            for (int j = 0; j < volunteerWalks.size(); ++j) {
                VolunteerWalks volunteerWalk = volunteerWalks.get(j);
                if(i == 0) {
                    iWalkConfig.add(volunteerWalk.walk1);
                }
                else if(i == 1)
                {
                    iWalkConfig.add(volunteerWalk.walk2);
                }
                else if(i == 2)
                {
                    iWalkConfig.add(volunteerWalk.walk3);
                }
                else if(i == 3)
                {
                    iWalkConfig.add(volunteerWalk.walk4);
                }
                else if(i == 4)
                {
                    iWalkConfig.add(volunteerWalk.walk5);
                }
            }
            walksConfig.add(iWalkConfig);
        }
        return walksConfig;
    }

    public int iWalks(int iPaseo)
    {
        int iWalks = 0;
        for(int i = 0; i < walksConfig.get(iPaseo).size(); ++i)
        {
            if(walksConfig.get(iPaseo).get(i) == 1)
            {
                iWalks++;
            }
        }
        return iWalks;
    }

    public int TotalWalks()
    {
        int totalWalks = 0;
        for(int i = 0; i < nPaseos; ++i) {
            for (int j = 0; j < walksConfig.get(i).size(); ++j) {
                if (walksConfig.get(i).get(j) == 1) {
                    totalWalks++;
                }
            }
        }
        return totalWalks;
    }

    public void ReOrderWalksTableSolution(int nVolunteers)
    {
        for(int i = 0; i < nPaseos; ++i)
        {
            ArrayList<Dog> idogs = new ArrayList<Dog>();
            ArrayList<Dog> specials = new ArrayList<Dog>();
            //Recuperamos los perros asignados al paseo i
            for(int j = 0; j < walksTable[i].length; ++j) {
                if(walksTable[i][j] != null) {
                    if (walksTable[i][j].special) {
                        specials.add(walksTable[i][j]);
                    } else {
                        idogs.add(walksTable[i][j]);
                    }
                    walksTable[i][j] = null;
                }
            }
            ArrayList<VolunteerWalks> volunteersAssigned = this.GetVolunteersAssignedIRow(i,volunteers);
            ArrayList<VolunteerDog> newAssing = new ArrayList<VolunteerDog>();
            Dictionary<Integer,Dog> newAssingDictionary = new Hashtable<Integer, Dog>();
            //Crear lista de voluntarios de orden creciente segun la cantidad de perros especiales
            //asignados en comparación con los del paseo
            ArrayList<VolunteerWalks> volunteersWithSpecialDogs = this.GetSortedVolunteersWithDogs(i,specials, volunteersAssigned, true, volunteersAssigned);
            //Asignar especiales
            newAssing.addAll(this.AssignDogsByFavourites(volunteersWithSpecialDogs, specials,true));

            //Crear lista de voluntarios de orden creciente segun la cantidad de perros especiales
            //asignados en comparación con los del paseo

            ArrayList<VolunteerWalks> volunteersStillToAssign = (ArrayList<VolunteerWalks>) volunteersAssigned.clone();
            for(int j = 0; j < newAssing.size(); ++j) {
                int id = newAssing.get(j).volunteer.id;
                for(int z = 0; z < volunteersStillToAssign.size(); ++z)
                {
                    int id2 = volunteersStillToAssign.get(z).id;
                    if(id == id2)
                    {
                        volunteersStillToAssign.remove(z);
                    }
                }
            }
            ArrayList<VolunteerWalks> volunteersWithFavouriteDogs = this.GetSortedVolunteersWithDogs(i, idogs, volunteersStillToAssign, false, volunteersAssigned);
            //Asignar favoritos
            ArrayList<VolunteerDog> favouritesAssigned = this.AssignDogsByFavourites(volunteersWithFavouriteDogs, idogs, true);
            newAssing.addAll(favouritesAssigned);

            for(int z = 0; z < favouritesAssigned.size(); ++z)
            {
                idogs.remove(favouritesAssigned.get(z).dog);
            }

            //Asignar resto
            ArrayList<VolunteerWalks> volunteersStillToAssign2 = (ArrayList<VolunteerWalks>) volunteersStillToAssign.clone();
            for(int j = 0; j < volunteersWithFavouriteDogs.size(); ++j) {
                int id = volunteersWithFavouriteDogs.get(j).id;
                for(int z = 0; z < volunteersStillToAssign2.size(); ++z)
                {
                    int id2 = volunteersStillToAssign2.get(z).id;
                    if(id == id2)
                    {
                        volunteersStillToAssign2.remove(z);
                    }
                }
            }
            newAssing.addAll(this.AssignDogsByFavourites(volunteersStillToAssign2, idogs, false));

            for(int j = 0; j < newAssing.size(); ++j)
            {
                VolunteerDog volunteerDog = newAssing.get(j);
                int id = volunteerDog.volunteer.id;
                newAssingDictionary.put(id,volunteerDog.dog);
            }

            for(int j = 0; j < volunteers.size(); ++j)
            {
                Volunteer volunteer = volunteers.get(j);
                Dog assigned = newAssingDictionary.get(volunteer.id);
                if(assigned != null)
                {
                    walksTable[i][j] = assigned;
                }
            }
            //Reasignamos estos perros a los voluntarios que hacen el paseo i
            /*for(int j = 0; j < walksTable[i].length; ++j)
            {
                if(this.walksConfig.get(i).get(j) == 1)
                {
                    walksTable[i][j] = idogs.get(0);
                    idogs.remove(0);
                }
            }*/
        }

        //Volvemos a ordenar los paseos en el orden original
        Dog[][] newWalksTable = new Dog[nPaseos][nVolunteers];
        ArrayList<ArrayList<Dog>> newCleanTable = new ArrayList<ArrayList<Dog>>();
        for(int i = 0; i < nPaseos; ++i)
        {
            newCleanTable.add(new ArrayList<Dog>());
        }

        for(int i = 0; i < nPaseos; ++i)
        {
            int assigniRow = this.walksMapping.get(i).iWalk;
            for(int j = 0; j < walksTable[assigniRow].length; ++j)
            {
                newWalksTable[i][j] = walksTable[assigniRow][j];
            }
            newCleanTable.remove(i);
            newCleanTable.add(i,cleanTable.get(assigniRow));
        }

        this.walksTable = newWalksTable;
        this.cleanTable = newCleanTable;
    }

    public boolean InteriorAreFriends(int idCageX, int idCageY)
    {
        ArrayList<Dog> dogsInteriorCageX = this.GetInteriorDogs(idCageX);
        ArrayList<Dog> dogsInteriorCageY = this.GetInteriorDogs(idCageY);
        for(int i = 0; i < dogsInteriorCageX.size(); ++i)
        {
            Dog iDog = dogsInteriorCageX.get(i);
            for(int j = 0; j < dogsInteriorCageY.size(); ++j)
            {
                Dog jDog = dogsInteriorCageY.get(j);
                if(!iDog.friends.contains(jDog))
                {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean MoreThanOneCageClean(int irow)
    {
        int cageId = cleanTable.get(irow).get(0).idCage;
        for(int i = 1; i < cleanTable.get(irow).size(); ++i)
        {
            if(cleanTable.get(irow).get(i).idCage != cageId)
            {
                return true;
            }
        }
        return false;
    }

    //Si hay más de una jaula asignada a limpieza calculamos cuandos perros de estas jaulas
    // quedan para asignar al paseo iRow
    public int GetDogsUnassignedToAssign(ArrayList<Integer> cagesValidated, int irow)
    {
        ArrayList<Integer> cleanCagesValidated = new ArrayList<Integer>();
        int dogsToAssign = 0;
        for(int i = 0; i < cleanTable.get(irow).size(); ++i)
        {
            Dog dog = cleanTable.get(irow).get(i);
            if(!cleanCagesValidated.contains(dog.idCage))
            {
                if(!cagesValidated.contains(dog.idCage))
                {
                    dogsToAssign = dogsToAssign + GetExteriorDogs(dog.idCage);
                }
                cleanCagesValidated.add(dog.idCage);
            }
        }
        return dogsToAssign;
    }

    public int GetDogsToAssign(int irow)
    {
        int dogsToAssign = 0;
        ArrayList<Integer> cagesInCleanRow = new ArrayList<Integer>();
        for(int i = 0; i < this.cleanTable.get(irow).size(); ++i)
        {
            Dog dog = this.cleanTable.get(irow).get(i);
            if(!cagesInCleanRow.contains(dog.idCage))
            {
                dogsToAssign = dogsToAssign + this.GetExteriorDogs(dog.idCage);
                cagesInCleanRow.add(dog.idCage);
            }
        }
        return dogsToAssign;
    }

    public void OrderWalksTableByWalksCount()
    {
        ArrayList<WalksInfo> walksMapping = new ArrayList<WalksInfo>();
        for(int i = 0; i < nPaseos; i++)
        {
            int count = 0;
            for(int j = 0; j < this.walksConfig.get(i).size(); ++j)
            {
                if(this.walksConfig.get(i).get(j) == 1)
                {
                    count++;
                }
            }
            walksMapping.add(new WalksInfo(i,count));
        }

        WalksInfo[] walksCountArray = new WalksInfo[walksMapping.size()];
        this.MergeSortByWalks(walksMapping.toArray(walksCountArray));

        List<WalksInfo> walksCountList = new ArrayList<WalksInfo>();
        for(int i = 0; i < walksCountArray.length; ++i)
        {
            walksCountList.add(walksCountArray[i]);
        }

        ArrayList<ArrayList<Integer>> newWalksConfig = new ArrayList<ArrayList<Integer>>();
        for(int i = 0; i < nPaseos; ++i)
        {
            ArrayList<Integer> iwalkConfig = (ArrayList<Integer>) this.walksConfig.get(walksCountList.get(i).iWalk).clone();
            newWalksConfig.add(iwalkConfig);
        }
        this.walksConfig = newWalksConfig;
        this.walksMapping = (ArrayList<WalksInfo>) walksCountList;
    }

    public ArrayList<ArrayList<Dog>> GetInteriorFriendsGroupsDomain()
    {
        VertexCage[] vertexs = this.cageGraph.vertexList();
        Dictionary<VertexCage,Boolean> visitedVertexs = new Hashtable<VertexCage,Boolean>();
        ArrayList<VertexCage> cagesGroup = new ArrayList<VertexCage>();
        ArrayList<Dog> dogGroup = new ArrayList<Dog>();

        for(int i = 0; i < vertexs.length; ++i) {
            if(vertexs[i].friendDogs.size() > 0) {
                visitedVertexs = new Hashtable<VertexCage, Boolean>();
                for (int j = 0; j < vertexs.length; ++j) {
                    visitedVertexs.put(vertexs[j], false);
                }

                cagesGroup.add(vertexs[i]);
                dogGroup.addAll(vertexs[i].interiorDogsInCage);
                this.RecursiveGetInteriorFriendsGroupsDomain(vertexs[i], vertexs, visitedVertexs, cagesGroup, dogGroup);
            }
        }

        ArrayList<ArrayList<Dog>> newInteriorFriendGroups = (ArrayList<ArrayList<Dog>>) interiorFriendGroups.clone();
        for(int i = 0; i < interiorFriendGroups.size(); ++i)
        {
            for(int j = i+1; j < interiorFriendGroups.size(); ++j)
            {
                if(interiorFriendGroups.get(i).size() == interiorFriendGroups.get(j).size() &&
                   interiorFriendGroups.get(i).containsAll(interiorFriendGroups.get(j)))
                {
                    newInteriorFriendGroups.remove(interiorFriendGroups.get(j));
                }

                if(interiorFriendGroups.get(j).size() == interiorFriendGroups.get(i).size() &&
                   interiorFriendGroups.get(j).containsAll(interiorFriendGroups.get(i)))
                {
                    newInteriorFriendGroups.remove(interiorFriendGroups.get(i));
                }
            }
        }

        MergeUtils.MergeBySizeElements(newInteriorFriendGroups);
        this.interiorFriendGroups = newInteriorFriendGroups;
        return (ArrayList<ArrayList<Dog>>)this.interiorFriendGroups.clone();
    }

    public void RecursiveGetInteriorFriendsGroupsDomain(VertexCage vertex, VertexCage[] vertexs, Dictionary<VertexCage,Boolean> visitedVertexs,  ArrayList<VertexCage> cagesGroup, ArrayList<Dog> dogGroup)
    {
        //Marcar vertex com a visitat i afegir a la llista de gàbies
        visitedVertexs.remove(vertex);
        visitedVertexs.put(vertex, true);

        //Buscar pel vertex i totes les arestes de pes = Compatible x interiors
        List<EdgeCage> edges = this.cageGraph.edgesOfByWeight(vertex, Constants.CAGE_EDGE_INTERIOR_FRIENDS_VALUE);

        boolean endRoad = true;
        boolean oneCompatible = false;
        //Per cada aresta
        for (int i = 0; i < edges.size(); ++i) {
            //Obtenir vertex2
            EdgeCage edge = edges.get(i);
            VertexCage vertex2 = null;
            if (edge.v1.idCage == vertex.idCage) {
                vertex2 = edge.v2;
            } else {
                vertex2 = edge.v1;
            }

            if (!visitedVertexs.get(vertex2)) {
                endRoad = false;
                //Mirar si el vertex 2 és compatible amb les gàbies del grup actual (per cada aresta mirar si existeixen totes les gàbies de la llista)
                List<EdgeCage> edges2 = this.cageGraph.edgesOfByWeight(vertex2, Constants.CAGE_EDGE_INTERIOR_FRIENDS_VALUE);
                ArrayList<VertexCage> cagesGroupClone = (ArrayList<VertexCage>) cagesGroup.clone();
                for (int j = 0; j < edges2.size(); ++j) {
                    EdgeCage edge2 = edges2.get(j);
                    VertexCage notVertex2 = null;
                    if (edge2.v1.idCage == vertex2.idCage) {
                        notVertex2 = edge2.v2;
                    } else {
                        notVertex2 = edge2.v1;
                    }
                    if (cagesGroupClone.contains(notVertex2)) {
                        cagesGroupClone.remove(notVertex2);
                    }
                }

                ArrayList<Dog> dogGroupTest = (ArrayList<Dog>) dogGroup.clone();
                dogGroupTest.addAll(vertex2.interiorDogsInCage);
                boolean existsGroup = false;
                for(int j = 0; j < interiorFriendGroups.size(); ++j)
                {
                    if(interiorFriendGroups.get(j).size() == dogGroupTest.size() &&
                       interiorFriendGroups.get(j).containsAll(dogGroupTest))
                    {
                        existsGroup = true;
                    }
                }

                //Si es compatible amb totes
                if (cagesGroupClone.size() == 0 && !existsGroup) {
                    oneCompatible = true;
                    cagesGroup.add(vertex2);
                    //Afegir gossos de la gàbia a la llista
                    dogGroup.addAll(vertex2.interiorDogsInCage);

                    interiorFriendGroups.add((ArrayList<Dog>)dogGroup.clone());
                    //Crida recursiva
                    this.RecursiveGetInteriorFriendsGroupsDomain(vertex2, vertexs, visitedVertexs, cagesGroup, dogGroup);
                }
            }
        }
        //Si no hi ha més vèrtexs a visitar o tots els possibles a visitar són imconpatibles
        //afegim la llista de gossos actuals
        //if(cagesGroup.size() > 1 && (endRoad || !oneCompatible))
        {
            //interiorFriendGroups.add((ArrayList<Dog>)dogGroup.clone());
        }
        dogGroup.removeAll(vertex.interiorDogsInCage);
        cagesGroup.remove(vertex);
    }

    public ArrayList<VolunteerWalks> GetSortedVolunteersWithDogs(int iRow, ArrayList<Dog> idogs, ArrayList<VolunteerWalks> volunteers, boolean special, ArrayList<VolunteerWalks> allAssignedVolunteers)
    {
        ArrayList<VolunteerWalks> volunteersRow = new ArrayList<VolunteerWalks>();
        int ivolunteer = -1;
        for(int i = 0; i < walksTable[iRow].length; ++i)
        {
            if(this.walksConfig.get(iRow).get(i) == 1)
            {
                ivolunteer++;
                VolunteerWalks volunteer = allAssignedVolunteers.get(ivolunteer);
                for(int j = 0; j < volunteers.size(); ++j)
                {
                    if(volunteer.id == volunteers.get(j).id) {
                        boolean addvolunteer = false;
                        ArrayList<Dog> tmpSpecialDogs = new ArrayList<Dog>();
                        for (int z = 0; z < idogs.size(); ++z) {
                            Dog dog = idogs.get(z);
                            for(int k = 0; k < volunteer.favouriteDogs.size(); ++k)
                            {
                                Dog iFavourite = volunteer.favouriteDogs.get(k);
                                if(dog.id == iFavourite.id)
                                {
                                    addvolunteer = true;
                                    tmpSpecialDogs.add(dog);
                                    break;
                                }
                            }
                        }
                        if (addvolunteer) {
                            //Creamos un voluntario temporal que tenga como favoritos los perros favoritos/especiales que tiene el voluntario
                            //real como favoritos y que estan asignados en el paseo iRow
                            VolunteerWalks tmpVolunteer = new VolunteerWalks(volunteer.id, volunteer.name, volunteer.clean, volunteer.walk1, volunteer.walk2, volunteer.walk3, volunteer.walk4, volunteer.walk5, volunteer.nPaseos);
                            tmpVolunteer.favouriteDogs = tmpSpecialDogs;
                            volunteersRow.add(tmpVolunteer);
                        }
                    }
                }
            }
        }

        MergeUtils.MergeByFavouriteDogsSize(volunteersRow);

        return volunteersRow;
    }

    public ArrayList<VolunteerDog> AssignDogsByFavourites(ArrayList<VolunteerWalks> volunteers, ArrayList<Dog> iDogs, boolean favourites)
    {
        ArrayList<VolunteerDog> assignRow = new ArrayList<VolunteerDog>();
        ArrayList<VolunteerWalks> assignedVolunteers = new ArrayList<VolunteerWalks>();
        ArrayList<Dog> dogsUnassigned = (ArrayList<Dog>) iDogs.clone();

        for(int i = 0; i < iDogs.size(); ++i)
        {
            Dog dog = iDogs.get(i);
            for (int j = 0; j < volunteers.size(); ++j) {
                VolunteerWalks volunteer = volunteers.get(j);
                if (favourites && !assignedVolunteers.contains(volunteer) && volunteer.favouriteDogs.contains(dog)) {
                    assignRow.add(new VolunteerDog(dog, volunteer));
                    assignedVolunteers.add(volunteer);
                    break;
                }
                else if(!favourites && !assignedVolunteers.contains(volunteer))
                {
                    assignRow.add(new VolunteerDog(dog, volunteer));
                    assignedVolunteers.add(volunteer);
                    break;
                }
            }
        }
        volunteers = assignedVolunteers;

        return assignRow;
    }

    public ArrayList<VolunteerWalks> GetVolunteersAssignedIRow(int iRow, List<VolunteerWalks> volunteers) {
        ArrayList<VolunteerWalks> volunteersAssigned = new ArrayList<VolunteerWalks>();
        for (int j = 0; j < walksTable[walksMapping.get(iRow).iWalk].length; ++j) {
            VolunteerWalks volunteer = volunteers.get(j);
            if (walksConfig.get(iRow).get(j) == 1) {
                volunteersAssigned.add(volunteer);
            }
        }
        return  volunteersAssigned;
    }
}

/*
Crida inicial: Backtracking(0,0,0);

procediment Backtracking(Posició taula neteja , Fila taula passejos, Columna taula passejos) ; si Posició taula neteja == Fila taula passejos s'asignen gossos a la taula de neteja
                                                                                                si Posició taula neteja > Fila taula passejos s'asigna gos a la taula de passejos
inici
dominiBuit = DominiBuit(Posició taula neteja , Fila taula passejos, Columna taula passejos) ; En el cas que hi hagi menys gossos que passejos i es permetin assignacions buides hem de consultar si el domini es buit
Selecció(Posició taula neteja , Fila taula passejos, Columna taula passejos, dominiBuit) ; Assigna un gos a la taula de passejos o una llista de gossos a la taula de neteja
si Validació(Posició taula neteja , Fila taula passejos, Columna taula passejos, dominiBuit)
    AsignarRestaDeLaGàbia(Posició taula neteja , Fila taula passejos, Columna taula passejos, dominiBuit) ;
    ActualitzarDomini(Posició taula neteja , Fila taula passejos, Columna taula passejos, dominiBuit) ; S'elimina del domini de passejos o del de neteja el/els gos/gossos asignat/s de
                                                                                                        les següents posicions a assignar
    si (S'ha asignat un gos/gossos a la taula de passejos)
        gossosAfegits = ObtenirNumeroGossosAfegits();
    si (Posició taula neteja == Quantitat passejos && Fila taula passejos == Quantitat passejos - 1 && Columna taula passejos + gossosAfegits >= Quantitat de passejos del passeig(Fila taula passejos))
        No fer res -> Tant la taula de neteja com la de passejos han asignat un valor a cada una de les posicions -> Es una SOLUCIÓ
    si no
        si (Asignat gos/gossos a taula neteja)
            Backtracking(Posició taula neteja + 1, Fila taula passejos, Columna taula passejos)
        si no
            si(Columna taula passejos + gossosAfegits >= Quantitat de passejos del passeig(Fila taula passejos))
                Backtracking(Posició taula neteja, Fila taula passejos +1, 0)
            si no
                Backtracking(Posició taula neteja, Fila taula passejos, Columna taula passejos + gossosAfegits)
            fi si
        fi si
    fi si
si no
    si !DominiBuit(Posició taula neteja, Fila taula passejos, Columna taula passejos) ; Encara queden valor al domini. S'asigna un nou valor a la mateixa posició de la taula
        Backtracking(Posició taula neteja, Fila taula passejos, Columna taula passejos)
    si no
        si (Posició taula neteja == 0 &&  Fila taula passejos == 0 && Columna taula passejos == 0) //Posició inicial de les 2 taules
            return; //No existeix solució
        si no
            si (Quantitat de gossos < Total passejos a assignar) ; Si hi ha mmenys gossos que passejos a assignar s'han de permetre els valors buits a la taula de passejos;
                si(Fila de la taula de passejos plena)
                    Backtracking(Posició taula neteja, Fila taula passejos + 1, Columna taula passejos)
                si no
                    Backtracking(Posició taula neteja, Fila taula passejos, Columna taula passejos + 1)
                fi si
            si (Columna taula passejos == 0 && Posició taula neteja > Fila taula passejos) ; Al passeig i no s'ha pogut asignar cap gos vàlid a la taula de passejos. Fem backtracking d'aquests gos.
                                                                                             S'ha de modificar el/s gos/gossos assignats a la taula de neteja
                Desassignem el gos que s'acaba d'asignar de la taula de passejos.
                Actualitzem el domini de la taula de passejos en aquesta posició           ; de tal manera que hi hagi de nou tots els gossos no assignats a la taula de passejos
                Eliminem de la taula de neteja la llista de l'última posició ja que s'han d'assignar gossos de nou
                Backtracking(Posició taula neteja - 1, Fila taula passejos, Columna taula passejos)
            si (Columna taula passejos == 0)                                               ; Al passeig i no s'ha pogut asignar cap gos/gossos vàlids a la taula de neteja. Fem backtracking d'aquest/s gos/gosos.
                                                                                             S'ha de modificar els gossos de l'última gàbia assignada al passeig i-1
                Actualitzem el domini de la taula de neteja
                Backtracking(Posició taula neteja, Fila taula passejos - 1, Quantitat passejos(Fila taula passejos) - 1)
            si no                                                                          ; Eliminem els gossos de l'última gàbia assignada i assignem una gàbia al mateix passeig
                gossosÚltimaGàbiaAssignada = ObtenirNumeroGossosÚltimaGàbiaAssignada();
                Actualitzem el domini de la taula de passejos de les posicions on hem desassignat gossos i de les posicions sense gossos assignats
                Backtracking(Posició taula neteja, Fila taula passejos, Columna taula passejos - gossosÚltimaGàbiaAssignada)
            fi si
       fi si
   fi si
fi si
fi



procediment Validació(Posició taula neteja , Fila taula passejos, Columna taula passejos, dominiBuit)

inici
si dominiBuit
    si(Posició taula neteja == Fila taula passejos) ; A la taula de neteja sempre s'ha d'assignar una subllista de gossos
        retorna fals
    si(Posició taula neteja == Fila taula passejos && Quantitat gossos < Quantitat passejos) ; Si hi ha més passejos que gossos hem de permetre les assignacions sense gossos a la taula de passejos
        retorna cert
    si no
        retorna fals
si(Posició taula neteja == Fila taula passejos) ; S'han assignat gossos a la taula de neteja
    gossosPerAssignar = Obtenir gossos que s'hauran d'assignar a la taula de passejos ; Si sabem els gossos que s'han assignat a la taula de neteja podem obtenir quins gossos dels que surten a passejar s'hauran
                                                                                       d'assignar obligatoriament al passeig 'Fila taula passejos'
    gossosEspecialsPerAssignar = Obtenir gossos especials que s'hauran d'assignar a la taula de passejos

    si(EsPodenAssignarEspecials(gossosEspecialsPerAssignar)) ; Un gos especial només es pot assignar si hi ha algún voluntari que el té com a preferit.
        retorna fals
    si(gossosPerAssignar > Quantitat passejos del passeig(Fila taula passejos)) ; No es poden assignar tots els gossos al passeig
        retorna fals
    fi si
si no ; S'ha assignat un gos a la taula de passejos
    gàbiesAssignades = ObtenirGàbiesJaAssignades()
    quantitatGossosAssignats = ObtenirGossosJaAssignats() ; S'inclouen tots els gossos que surten a passejar de la gàbia de l'últim gos assignat
    gossosEspecialsPerAssignar = Obtenir gossos especials que s'hauran d'assignar a la taula de passejos

    si(EsPodenAssignarEspecials(gssosEspecialsPerAssignar)) ; Un gos especial només es pot assignar si hi ha algún voluntari que el té com a preferit.
        retorna fals
    si(Hi ha més d'una gàbia assignada a la taula de neteja al passeig(Fila taula passejos))
        gossosPerAssignar = Obtenir gossos que s'hauran d'assignar a la taula de passejos
        si(cuantitatGossosAssignats + gossosPerAssignar > Quantitat passejos del passeig(Fila taula passejos))
            retorna fals
        fi si
    si(Si només hi ha una gàbia assignada a la taula de neteja i els gossos que surten a passejar que comparteixen gàbia amb el gos/gossos asignats a la taula de neteja ja estan assignats
    && quantitatGossosAssignats > Quantitat passejos del passeig(Fila taula passejos))
       retorna fals
    si(Si només hi ha una gàbia assignada a la taula de neteja i els gossos que surten a passejar que comparteixen gàbia amb el gos/gossos asignats a la taula de neteja NO estan assignats
    && quantitatGossosAssignats + gossos que surten a passejar que comparteixen gàbia amb el gos/gossos asignats a la taula de neteja > Quantitat passejos del passeig(Fila taula passejos))
       retorna fals
    si(Si només hi ha una gàbia assignada a la taula de neteja i s'hi han assignats tots els gossos (els que surten a passejar i els que no
    && quantitatGossosAssignats > Quantitat passejos del passeig(Fila taula passejos))
       retorna fals
    si(Els gossos que surten per la zona interior de la gàbia del gos que s'acaba d'assignar no estan assignats a la taula de neteja)
        retorna fals
    fi si
    retorna cert
fi


procediment EsPodenAssignarEspecials(gossosEspecialsPerAssignar)
inici
    si (per cada gos especial de gossosEspecialsPerAssignar hi ha un voluntari que el té com a preferit i
    el tamany de l'unió de voluntaris > Quantitat gossos en gossosEspecialsPerAssignar)
        retorna cert
    sino
        retorna fals
fi


Per cada vèrtex del graf es fa la crida: ObtenirGrupGabiesDomini(iVertex, vertexs, vertexsVisitats, gàbiesGrupActual, gossosGrupActual)

global GrafJaulas
global GrupsJaTrobats
procediment ObtenirGrupGabiesDomini(vertex, vertexs, vertexsVisitats, gàbiesGrupActual, gossosGrupActual)

inici
    vertexsVisitats = Marcar 'vertex' com a visitat

    arestes = ObtenerAristasPorPeso(GrafJaulas, pes == 3)

    Per cada aresta
        segonVertex = aresta.ObtenirVertexDestí(vertex)
        si (vertexsVisitats no conté segonVertex)
            //Mirar si el segonVertex no es incompatible amb cap gabia de la llista 'gàbiesGrupActual'
            arestesSegonVertex = ObtenerAristasPorPeso(GrafJaulas, pes == 3)
            Comprovar que segonVertex tingui arestes a totes les gàbies de 'gàbiesGrupActual'
            jaExisteixGrup = Comprovar si GrupsJaTrobats té un grup igual a gàbiesGrupActual

            si (segonVertex és compatible amb les gàbies de vertexsVisitats i !jaExisteixGrup)
                Afegir a gàbiesGrupActual la gàbia del vertex actual
                Afegir a gossosGrupActual tots els gossos que s'han d'asignar a la zona interior de la gàbia del vertex actual
                Afegir a GrupsJaTrobats la llista gossosGrupActual
                ObtenirGrupGabiesDomini(segonVertex, vertexs, vertexsVisitats, gàbiesGrupActual, gossosGrupActual)
            fi si
        fi si
    fi bucle
    Treure de gàbiesGrupActual la gàbia del vertex 'iVertex'
    Treure de gossosGrupActual tots els gossos que s'han d'asignar a la zona interior de la gàbia del vertex 'iVertex
fi
*
 */