package com.example.ignasi94.backtrackingsimple;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MakeLists extends AppCompatActivity {

    DBAdapter dbAdapter;
    Button doListButton = null;
    String CAGE_ZONE_XENILES = "XENILES";
    String CAGE_ZONE_CUARENTENAS = "CUARENTENAS";
    String CAGE_ZONE_PATIOS = "PATIOS";
    Short WT_NONE = 0;
    Short WT_INTERIOR = 1;
    Short WT_EXTERIOR = 2;
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
        //Solución paseos
        //   V1  V2  V3  V4
        //P1
        //P2
        //P3
        //P4
        Dog[][] walksTable = new Dog[nPaseos][volunteers.size()];
        //Dominios paseos
        //En walksDomains[i][j] tenemos el dominio para la posición[i][j] de la walkstable
        //El dominio se basa en: {Perro1,Perro2,...,PerroN}
        ArrayList<ArrayList<ArrayList<Dog>>> walksDomains = this.CreateWalkDomains(dogs,nPaseos,volunteers.size());


        //Solución limpeza
        ArrayList <ArrayList<Dog>> cleanTable = new ArrayList <ArrayList <Dog> >();
        //Dominios limpieza
        //En cleanDomains[i] tenemos el dominio para la posición
        //El dominio se basa en:{{PerroInterior1 (jaula1),PerroInterior2(jaula1)},...,{PerroInteriorX (jaulaN),PerroInteriorY(jaulaN)},...,
        //                       {PerroInterior1 (jaula1),PerroInterior2(jaula1),PerroExterior3(jaula1)},...,{PerroInteriorX (jaulaN),PerroInteriorY(jaulaN),PerroExteriorZ(jaulaN)}}
        ArrayList <ArrayList<ArrayList<Dog>>> cleanDomains = this.CreateCleanDomains(dogs,cages,nPaseos);

        listDogsPerCage = new ArrayList<ArrayList<Dog>>();
        this.CreateListDogsPerCage(dogs,cages);
        this.Backtracking(0,0,0,nPaseos,volunteers.size(),walksTable,walksDomains,cleanTable,cleanDomains);
    }

    public ArrayList<ArrayList<ArrayList<Dog>>> CreateWalkDomains(List<Dog> dogs, int nPaseos, int nVolunteers)
    {
        ArrayList<ArrayList<ArrayList<Dog>>> walksDomains = new ArrayList<ArrayList<ArrayList<Dog>>>();

        nWalkDomain = new ArrayList<Dog>();
        for(int k = 0; k < dogs.size(); k++) {
            Dog kDog = dogs.get(k);
            if(kDog.walktype == WT_EXTERIOR)
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
            if(cage.zone.contentEquals(CAGE_ZONE_XENILES))
            {
                for(int k = 0; k < dogs.size(); k++)
                {
                    Dog dog = dogs.get(k);
                    if(dog.idCage == cage.numCage)
                    {
                        if(dog.walktype == WT_INTERIOR)
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
                nWalkallCageDomain.add(allCageDomain);
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

    public void Backtracking(int iClean, int irow, int icolumn, int nPaseos, int nVolunteers, Dog[][] walksTable,ArrayList<ArrayList<ArrayList<Dog>>> walksDomains,ArrayList <ArrayList<Dog>> cleanTable,ArrayList <ArrayList<ArrayList<Dog>>> cleanDomains)
    {
        //inicio
        this.Selection(iClean,irow,icolumn,walksTable,walksDomains,cleanTable,cleanDomains);
        if(this.Validate(iClean, irow, icolumn, nVolunteers, walksTable, walksDomains, cleanTable, cleanDomains))
        {
            //Eliminar de tots els dominis
            this.UpdateDomain(iClean, irow, icolumn, walksTable, walksDomains, cleanTable, cleanDomains);
            if(iClean == nPaseos && irow == nPaseos-1 && icolumn == nVolunteers-1)
            {
                //Solución
                //no hacer nada
            }
            else
            {
                if(iClean == irow)
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
                }
            }
        }
        else
        {
            //Eliminar del domini de la posició actual
            //UpdateThisDomain
            if(!this.DomainEmpty(iClean, irow, icolumn, walksDomains, cleanDomains))
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
            }
        }

    }

    public void Selection(int iClean, int irow, int icolumn, Dog[][] walksTable,ArrayList<ArrayList<ArrayList<Dog>>> walksDomains,ArrayList <ArrayList<Dog>> cleanTable,ArrayList <ArrayList<ArrayList<Dog>>> cleanDomains) {
        if (iClean == irow) {
            ArrayList<Dog> dogsToAssignClean = (ArrayList<Dog>) cleanDomains.get(iClean).get(0).clone();
            cleanDomains.get(iClean).remove(dogsToAssignClean);
            cleanTable.add(iClean, dogsToAssignClean);
        } else {
            Dog dogToAssignWalk = walksDomains.get(irow).get(icolumn).get(0);
            walksDomains.get(irow).get(icolumn).remove(dogToAssignWalk);
            walksTable[irow][icolumn] = dogToAssignWalk;
        }
    }

    public void UpdateDomain(int iClean, int irow, int icolumn, Dog[][] walksTable,ArrayList<ArrayList<ArrayList<Dog>>> walksDomains,ArrayList <ArrayList<Dog>> cleanTable,ArrayList <ArrayList<ArrayList<Dog>>> cleanDomains)
    {
        if(iClean == irow)
        {
            for (int i = iClean+1; i < cleanDomains.size(); ++i) {
                cleanDomains.get(i).remove(cleanTable.get(iClean));
            }
        }
        else
        {
            boolean remove = false;
            for (int i = 0; i < walksDomains.size(); ++i) {
                for (int j = 0; j < walksDomains.get(0).size(); ++j) {
                    if(remove)
                    {
                        walksDomains.get(i).get(j).remove(walksTable[irow][icolumn]);
                    }
                    if(i == irow && j == icolumn)
                    {
                        remove = true;
                    }
                }
            }
        }
    }

    public boolean Validate(int iClean, int irow, int icolumn,  int nVolunteers, Dog[][] walksTable,ArrayList<ArrayList<ArrayList<Dog>>> walksDomains,ArrayList <ArrayList<Dog>> cleanTable,ArrayList <ArrayList<ArrayList<Dog>>> cleanDomains)
    {
        if( iClean == irow)
        {
            Dog assigned = cleanTable.get(irow).get(0);
            //Comprobar que el resto de perros de la jaula (que pueden salir) caben en el npaseo
            int dogsInCage = this.GetExteriorDogs(assigned.idCage);
            if(dogsInCage > nVolunteers )
            {
                return false;
            }
        }
        else
        {
            Dog assigned = walksTable[irow][icolumn];
            //Comprobar que los perros exteriores de su jaula caben en el npaseo
            int totalSpaces = 0;
            int dogsInCage = this.GetExteriorDogs(assigned.idCage);
            for(int i = 0; i < walksTable[0].length; i++)
            {
                if(listDogsPerCage.get(assigned.idCage).contains(walksTable[irow][i]))
                {
                    totalSpaces++;
                }
                if(walksTable[irow][i] == null)
                {
                    totalSpaces++;
                }
            }
            if(dogsInCage > totalSpaces)
            {
                return false;
            }
            //Comprobar que si hay perros interiores estos estan asignados, en caso que no return false
            for (int j = 0; j < listDogsPerCage.get(assigned.idCage).size(); ++j)
            {
                Dog cageDog = listDogsPerCage.get(assigned.idCage).get(j);
                if(cageDog.walktype == WT_INTERIOR && !cleanTable.get(irow).contains((cageDog)))
                {
                    return false;
                }
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
            if(listDogsPerCage.get(cageId).get(i).walktype == WT_EXTERIOR)
            {
                count++;
            }
        }
        return count;
    }
}
