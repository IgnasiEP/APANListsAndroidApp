package com.example.ignasi94.backtrackingsimple;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.ignasi94.backtrackingsimple.BBDD.DBAdapter;
import com.example.ignasi94.backtrackingsimple.Estructuras.Cage;
import com.example.ignasi94.backtrackingsimple.Estructuras.Dog;
import com.example.ignasi94.backtrackingsimple.Estructuras.Volunteer;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerWalks;
import com.example.ignasi94.backtrackingsimple.Utils.Constants;
import com.example.ignasi94.backtrackingsimple.Utils.RunnableThread;

import java.util.ArrayList;
import java.util.List;

public class MakeLists extends AppCompatActivity {

    DBAdapter dbAdapter;
    Button doListButtonTest1 = null;
    Button doListButtonTest2 = null;
    Button doListButtonTest3 = null;
    Button doListButtonTest4 = null;
    ArrayList<ArrayList<Dog>>  clean;
    Dog[][] walks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_lists);

        dbAdapter = new DBAdapter(this);
        List<Dog> dogs = dbAdapter.getAllDogs();
        List<Cage> cages = GetCages();
        List<VolunteerWalks> volunteers = dbAdapter.getAllSelectedVolunteers();
        int npaseos = volunteers.get(0).nPaseos;
        ArrayList<VolunteerWalks> volunteerWalks = new ArrayList<VolunteerWalks>();
        volunteerWalks = this.EraseCleaningVolunteers(volunteers);
        RunnableThread rT = new RunnableThread("Test", dogs, cages, volunteerWalks);
        ThreadGroup tg = new ThreadGroup("TestGroup1");
        Thread t = new Thread(tg,rT,rT.getName(), 128*1024*1024);
        t.start();
        try {
            t.join();
            walks = rT.walksTable;
            clean = rT.cleanTable;
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //Pasamos la solución de paseos a matriz de id's
        Intent launchactivity= new Intent(MakeLists.this,ShowSolution.class);
        //SetOutputParameters(launchactivity, npaseos, volunteers.size(), rT);
        launchactivity.putExtra("nPaseos", npaseos);
        dbAdapter.SaveWalkSolution(walks, volunteerWalks);
        dbAdapter.SaveCleanSolution(clean);
        startActivity(launchactivity);
    }

    public void SetOutputParameters(Intent launchactivity, int nPaseos, int nVolunteers, RunnableThread rT)
    {
        ArrayList<ArrayList<Integer>> walksArray = new ArrayList<ArrayList<Integer>>();
        for(int i = 0; i < nPaseos; ++i)
        {
            ArrayList<Integer> nArray = new ArrayList<Integer>();
            for(int j = 0; j < nVolunteers; ++j)
            {
                if(rT.walksTable[i][j] != null) {
                    nArray.add(rT.walksTable[i][j].id);
                }
                else
                {
                    nArray.add(0);
                }
            }
            walksArray.add(nArray);
        }
        //Añadimos la matriz de paseos fila por fila al Intent
        launchactivity.putExtra("nPaseos", nPaseos);
        for(int i = 0; i < walksArray.size(); ++i)
        {
            launchactivity.putExtra("WalkSolution" + i, walksArray.get(i));
        }

        ArrayList<ArrayList<Integer>> cleanArray = new ArrayList<ArrayList<Integer>>();
        for(int i = 0; i < nPaseos; ++i)
        {
            ArrayList<Integer> nArray = new ArrayList<Integer>();
            for(int j = 0; j < rT.cleanTable.get(i).size(); ++j)
            {
                nArray.add(rT.cleanTable.get(i).get(j).id);
            }
            cleanArray.add(nArray);
        }
        //Añadimos la solución de limpieza al Intent
        launchactivity.putExtra("nVolunteers", nVolunteers);
        for(int i = 0; i < rT.cleanTable.size(); ++i)
        {
            launchactivity.putExtra("CleanSolution" + i, cleanArray.get(i));
        }
    }

    public ArrayList<VolunteerWalks> EraseCleaningVolunteers(List<VolunteerWalks> volunteers)
    {
        ArrayList<VolunteerWalks> volunteerWalks = new ArrayList<VolunteerWalks>();
        for(int i = 0; i < volunteers.size(); ++i)
        {
            if(volunteers.get(i).clean == 0)
            {
                volunteerWalks.add(volunteers.get(i));
            }
        }
        return volunteerWalks;
    }

    //Dog(int id, String name, int idCage, int age, String link, Boolean special, Short walktype, String observations)
    public List<Dog> GetDogs(int i)
    {
        List<Dog> list = new ArrayList<Dog>();
        Dog dog = new Dog(1,"Puyol",1,0,null,false, Constants.WT_EXTERIOR,null);
        Dog dog2 = new Dog(2,"Vida",2,0,null,false,Constants.WT_EXTERIOR,null);
        Dog dog3 = new Dog(3,"Trixie",3,0,null,false,Constants.WT_EXTERIOR,null);
        Dog dog4 = new Dog(4,"Thor",4,0,null,false,Constants.WT_EXTERIOR,null);
        Dog dog5 = new Dog(5,"Looney",5,0,null,false,Constants.WT_EXTERIOR,null);
        Dog dog6 = new Dog(6,"Atenea",6,0,null,false,Constants.WT_INTERIOR,null);
        Dog dog7 = new Dog(7,"Quim",7,0,null,false,Constants.WT_EXTERIOR,null);
        Dog dog8 = new Dog(8,"Kratos",7,0,null,false,Constants.WT_EXTERIOR,null);
        Dog dog9 = new Dog(9,"Rista",8,0,null,false,Constants.WT_EXTERIOR,null);
        Dog dog10 = new Dog(10,"Milady",9,0,null,false,Constants.WT_EXTERIOR,null);
        Dog dog11 = new Dog(11,"Maxi",10,0,null,false,Constants.WT_EXTERIOR,null);
        Dog dog12 = new Dog(12,"Nika",11,0,null,false,Constants.WT_EXTERIOR,null);
        Dog dog13 = new Dog(13,"Mara",12,0,null,false,Constants.WT_EXTERIOR,null);
        Dog dog14 = new Dog(14,"Geralt",13,0,null,false,Constants.WT_INTERIOR,null);
        Dog dog15 = new Dog(15,"Ralts",14,0,null,false,Constants.WT_EXTERIOR,null);
        Dog dog16 = new Dog(16,"Luc",15,0,null,false,Constants.WT_EXTERIOR,null);
        Dog dog17 = new Dog(17,"Pontos",16,0,null,false,Constants.WT_EXTERIOR,null);
        Dog dog18 = new Dog(18,"Chelin",17,0,null,false,Constants.WT_EXTERIOR,null);
        Dog dog19 = new Dog(19,"Dogos",18,0,null,false,Constants.WT_EXTERIOR,null);
        Dog dog20 = new Dog(20,"Chelsea",19,0,null,false,Constants.WT_EXTERIOR,null);
        Dog dog21 = new Dog(21,"Argus",20,0,null,false,Constants.WT_INTERIOR,null);
        Dog dog22 = new Dog(22,"Jess",20,0,null,false,Constants.WT_INTERIOR,null);
        Dog dog23 = new Dog(23,"Max",21,0,null,false,Constants.WT_EXTERIOR,null);
        Dog dog24 = new Dog(24,"Canela",21,0,null,false,Constants.WT_EXTERIOR,null);
        Dog dog25 = new Dog(25,"Blacky",21,0,null,false,Constants.WT_INTERIOR,null);
        Dog dog26 = new Dog(26,"Titus",22,0,null,false,Constants.WT_EXTERIOR,null);
        Dog dog27 = new Dog(27,"Amiguets",22,0,null,false,Constants.WT_EXTERIOR,null);
        Dog dog28 = new Dog(28,"Miam",23,0,null,false,Constants.WT_NONE,null);
        Dog dog29 = new Dog(29,"Dardo",23,0,null,false,Constants.WT_NONE,null);

        //Patis
        Dog dog30 = new Dog(30,"Vito",24,0,null,false,Constants.WT_EXTERIOR,null);
        Dog dog31 = new Dog(31,"Corleone",24,0,null,false,Constants.WT_EXTERIOR,null);
        Dog dog32 = new Dog(32,"Ter",24,0,null,false,Constants.WT_NONE,null);
        Dog dog33 = new Dog(33,"Perla",24,0,null,false,Constants.WT_EXTERIOR,null);

        Dog dog34 = new Dog(34,"Canelo",26,0,null,false,Constants.WT_EXTERIOR,null);
        Dog dog35 = new Dog(35,"Saga",26,0,null,false,Constants.WT_EXTERIOR,null);
        Dog dog36 = new Dog(36,"Tunes",26,0,null,false,Constants.WT_EXTERIOR,null);
        Dog dog37 = new Dog(37,"Sira",26,0,null,false,Constants.WT_NONE,null);

        Dog dog38 = new Dog(38,"Pésol",29,0,null,false,Constants.WT_EXTERIOR,null);
        Dog dog39 = new Dog(39,"Cristal",29,0,null,false,Constants.WT_EXTERIOR,null);
        Dog dog40 = new Dog(40,"Bull",30,0,null,false,Constants.WT_EXTERIOR,null);
        Dog dog41 = new Dog(41,"Maya",30,0,null,false,Constants.WT_EXTERIOR,null);
        Dog dog42 = new Dog(42,"Stracciatela",31,0,null,false,Constants.WT_EXTERIOR,null);

        Dog dog43 = new Dog(43,"Mar",32,0,null,false,Constants.WT_EXTERIOR,null);
        Dog dog44 = new Dog(44,"Roc",32,0,null,false,Constants.WT_EXTERIOR,null);

        list.add(dog);list.add(dog2);list.add(dog3);list.add(dog4);list.add(dog5);list.add(dog6);list.add(dog7);list.add(dog8);list.add(dog9);list.add(dog10);
        list.add(dog11);list.add(dog12);list.add(dog13);list.add(dog14);list.add(dog15);list.add(dog16);list.add(dog17);list.add(dog18);list.add(dog19);list.add(dog20);
        list.add(dog21);list.add(dog22);list.add(dog23);list.add(dog24);list.add(dog25);list.add(dog26);list.add(dog27);list.add(dog28);list.add(dog29);list.add(dog30);
        list.add(dog31);list.add(dog32);list.add(dog33);list.add(dog34);list.add(dog35);list.add(dog36);list.add(dog37);list.add(dog38);list.add(dog39);list.add(dog40);
        list.add(dog41);list.add(dog42);list.add(dog43);list.add(dog44);

        return list;
    }

    //public Cage(int id, int numCage, String zone)
    public List<Cage> GetCages()
    {
        List<Cage> list = new ArrayList<Cage>();
        Cage cage1 = new Cage(1,1,Constants.CAGE_ZONE_XENILES);
        Cage cage2 = new Cage(2,2,Constants.CAGE_ZONE_XENILES);
        Cage cage3 = new Cage(3,3,Constants.CAGE_ZONE_XENILES);
        Cage cage4 = new Cage(4,4,Constants.CAGE_ZONE_XENILES);
        Cage cage5 = new Cage(5,5,Constants.CAGE_ZONE_XENILES);
        Cage cage6 = new Cage(6,6,Constants.CAGE_ZONE_XENILES);
        Cage cage7 = new Cage(7,7,Constants.CAGE_ZONE_XENILES);
        Cage cage8 = new Cage(8,8,Constants.CAGE_ZONE_XENILES);
        Cage cage9 = new Cage(9,9,Constants.CAGE_ZONE_XENILES);
        Cage cage10 = new Cage(10,10,Constants.CAGE_ZONE_XENILES);
        Cage cage11 = new Cage(11,11,Constants.CAGE_ZONE_XENILES);
        Cage cage12 = new Cage(12,12,Constants.CAGE_ZONE_XENILES);
        Cage cage13 = new Cage(13,13,Constants.CAGE_ZONE_XENILES);
        Cage cage14 = new Cage(14,14,Constants.CAGE_ZONE_XENILES);
        Cage cage15 = new Cage(15,15,Constants.CAGE_ZONE_XENILES);
        Cage cage16 = new Cage(16,16,Constants.CAGE_ZONE_XENILES);
        Cage cage17 = new Cage(17,17,Constants.CAGE_ZONE_XENILES);
        Cage cage18 = new Cage(18,18,Constants.CAGE_ZONE_XENILES);
        Cage cage19 = new Cage(19,19,Constants.CAGE_ZONE_XENILES);
        Cage cage20 = new Cage(20,20,Constants.CAGE_ZONE_XENILES);
        Cage cage21 = new Cage(21,21,Constants.CAGE_ZONE_XENILES);
        Cage cage22 = new Cage(22,22,Constants.CAGE_ZONE_XENILES);
        Cage cage23 = new Cage(23,23,Constants.CAGE_ZONE_XENILES);

        Cage cage24 = new Cage(24,24,Constants.CAGE_ZONE_PATIOS);
        Cage cage25 = new Cage(25,25,Constants.CAGE_ZONE_PATIOS);
        Cage cage26 = new Cage(26,26,Constants.CAGE_ZONE_PATIOS);
        Cage cage27 = new Cage(27,27,Constants.CAGE_ZONE_PATIOS);
        Cage cage28 = new Cage(28,28,Constants.CAGE_ZONE_PATIOS);

        Cage cage29 = new Cage(29,29,Constants.CAGE_ZONE_PATIOS);
        Cage cage30 = new Cage(30,30,Constants.CAGE_ZONE_PATIOS);
        Cage cage31 = new Cage(31,31,Constants.CAGE_ZONE_PATIOS);

        Cage cage32 = new Cage(32,32,Constants.CAGE_ZONE_CUARENTENAS);


        list.add(cage1);list.add(cage2);list.add(cage3);list.add(cage4);list.add(cage5);list.add(cage6);list.add(cage7);list.add(cage8);list.add(cage9);list.add(cage10);
        list.add(cage11);list.add(cage12);list.add(cage13);list.add(cage14);list.add(cage15);list.add(cage16);list.add(cage17);list.add(cage18);list.add(cage19);list.add(cage20);
        list.add(cage21);list.add(cage22);list.add(cage23);list.add(cage24);list.add(cage25);list.add(cage26);list.add(cage27);list.add(cage28);list.add(cage29);list.add(cage30);
        list.add(cage31);list.add(cage32);

        return list;
    }

    //public Volunteer(int id, String name, String phone, String volunteerDay, String observations)
    public List<Volunteer> GetVolunteers(int i)
    {
        List<Volunteer> list = new ArrayList<Volunteer>();
        Volunteer ignasi = new Volunteer(1,"Ignasi", null, Constants.VOLUNTEER_DAY_SATURDAY,null);
        Volunteer esther = new Volunteer(2,"Esther", null, Constants.VOLUNTEER_DAY_SATURDAY,null);
        Volunteer sonia = new Volunteer(3,"Sònia", null, Constants.VOLUNTEER_DAY_SATURDAY,null);
        Volunteer alex = new Volunteer(4,"Àlex", null, Constants.VOLUNTEER_DAY_SATURDAY,null);
        Volunteer guillem = new Volunteer(5,"Guillem", null, Constants.VOLUNTEER_DAY_SATURDAY,null);
        Volunteer lidia = new Volunteer(6,"Lídia", null, Constants.VOLUNTEER_DAY_SATURDAY,null);
        Volunteer alba1 = new Volunteer(7,"Alba1", null, Constants.VOLUNTEER_DAY_SATURDAY,null);
        Volunteer alba2 = new Volunteer(8,"Alba2", null, Constants.VOLUNTEER_DAY_SATURDAY,null);

        Volunteer alex2 = new Volunteer(9,"Alex", null, Constants.VOLUNTEER_DAY_SUNDAY,null);
        Volunteer andrea = new Volunteer(10,"Andrea", null, Constants.VOLUNTEER_DAY_SUNDAY,null);
        Volunteer marga = new Volunteer(11,"Marga", null, Constants.VOLUNTEER_DAY_SUNDAY,null);
        Volunteer cris = new Volunteer(12,"Cris", null, Constants.VOLUNTEER_DAY_SUNDAY,null);
        Volunteer asun = new Volunteer(13,"Asun", null, Constants.VOLUNTEER_DAY_SUNDAY,null);
        Volunteer elena = new Volunteer(14,"Elena", null, Constants.VOLUNTEER_DAY_SUNDAY,null);
        Volunteer guio = new Volunteer(15,"Guio", null, Constants.VOLUNTEER_DAY_SUNDAY,null);
        Volunteer munsita = new Volunteer(16,"Munsita", null, Constants.VOLUNTEER_DAY_SUNDAY,null);
        Volunteer montse = new Volunteer(17,"Montse", null, Constants.VOLUNTEER_DAY_SUNDAY,null);

        if(i == 1)
        {
            list.add(ignasi);list.add(esther);list.add(sonia);list.add(alex);list.add(guillem);list.add(lidia);list.add(alba1);list.add(alba2);
        }
        else if(i == 2)
        {
            list.add(ignasi);list.add(esther);list.add(sonia);list.add(alex);list.add(guillem);list.add(lidia);list.add(alba1);list.add(alba2);
        }
        else
        {
            list.add(alex2);list.add(andrea);list.add(marga);list.add(cris);list.add(asun);
        }

        return list;
    }
}

