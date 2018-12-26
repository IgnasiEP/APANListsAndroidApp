package com.example.ignasi94.backtrackingsimple.Screens.ListsScreens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.ignasi94.backtrackingsimple.BBDD.DBAdapter;
import com.example.ignasi94.backtrackingsimple.Estructuras.Cage;
import com.example.ignasi94.backtrackingsimple.Estructuras.Dog;
import com.example.ignasi94.backtrackingsimple.Estructuras.Volunteer;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerWalks;
import com.example.ignasi94.backtrackingsimple.R;
import com.example.ignasi94.backtrackingsimple.Utils.Constants;
import com.example.ignasi94.backtrackingsimple.Utils.RunnableThread;

import java.util.ArrayList;
import java.util.List;

public class ListsScreen extends Activity {

    ArrayList<ArrayList<Dog>>  clean;
    Dog[][] walks;
    DBAdapter dbAdapter;
    Button goConfigureListsButton;
    Button goEditConfigureListsButton;
    Button goMakeListsButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lists_activity_lists_screen);

        dbAdapter = new DBAdapter(this);

        goConfigureListsButton = (Button) findViewById(R.id.button_configurar_lista);
        goConfigureListsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchactivity= new Intent(ListsScreen.this,SelectVolunteers.class);
                launchactivity.putExtra("NEW", true);
                startActivity(launchactivity);
            }
        });

        goEditConfigureListsButton = (Button) findViewById(R.id.button_editar_configuracion);
        goEditConfigureListsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchactivity= new Intent(ListsScreen.this,SelectVolunteers.class);
                launchactivity.putExtra("NEW", false);
                startActivity(launchactivity);
            }
        });

        goMakeListsButton = (Button) findViewById(R.id.button_crear_lista);
        goMakeListsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Dog> dogs = dbAdapter.getAllDogs();
                //TESTINTERIORFRIENDS(dogs, dbAdapter);
                List<Cage> cages = GetCages();
                List<VolunteerWalks> volunteers = dbAdapter.getAllSelectedVolunteers();
                //TESTINTERIORSANDSPECIALS1(volunteers,dogs,dbAdapter);
                int npaseos = volunteers.get(0).nPaseos;
                ArrayList<VolunteerWalks> volunteerWalks = new ArrayList<VolunteerWalks>();
                volunteerWalks = EraseCleaningVolunteers(volunteers);
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
                Intent launchactivity= new Intent(ListsScreen.this,ShowSolution.class);
                //SetOutputParameters(launchactivity, npaseos, volunteers.size(), rT);
                launchactivity.putExtra("nPaseos", npaseos);
                dbAdapter.SaveWalkSolution(walks, volunteerWalks);
                dbAdapter.SaveCleanSolution(clean);
                startActivity(launchactivity);
            }
        });

        List<VolunteerWalks> volunteers = dbAdapter.getAllSelectedVolunteers();
        if(volunteers.size() == 0)
        {
            goEditConfigureListsButton.setVisibility(View.INVISIBLE);
            goMakeListsButton.setVisibility(View.INVISIBLE);
        }
        else
        {
            goEditConfigureListsButton.setVisibility(View.VISIBLE);
            goMakeListsButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
        List<VolunteerWalks> volunteers = dbAdapter.getAllSelectedVolunteers();
        if(volunteers.size() == 0)
        {
            goEditConfigureListsButton.setVisibility(View.INVISIBLE);
            goMakeListsButton.setVisibility(View.INVISIBLE);
        }
        else
        {
            goEditConfigureListsButton.setVisibility(View.VISIBLE);
            goMakeListsButton.setVisibility(View.VISIBLE);
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


    //
    //
    // |-------- 2 (21+22) --------
    // |                          |
    // |                          |
    // 1 ----- 5 ----- 6          3
    // | \                      / |
    // |  \                   /   |
    // |   --------- 4 -----/     |
    // |--------------------------|
    public void TESTINTERIORFRIENDS(List<Dog> dogs, DBAdapter dbAdapter)
    {
        for(int i = 0; i < dogs.size(); ++i)
        {
            if(dogs.get(i).walktype == Constants.WT_INTERIOR)
            {
                dogs.get(i).walktype = Constants.WT_NONE;
            }
        }

        //public Dog(int id, String name, int idCage, int age, String link, Boolean special, Short walktype, String observations)
        int lastId = dogs.get(dogs.size()-1).id;
        Dog dog1 = new Dog(lastId+1,"NODE 1", 1, 0, null, false, Constants.WT_INTERIOR, null);
        lastId++;

        Dog dog21 = new Dog(lastId+1,"NODE 2-1", 2, 0, null, false, Constants.WT_INTERIOR, null);
        lastId++;

        Dog dog22 = new Dog(lastId+1,"NODE 2-2", 2, 0, null, false, Constants.WT_INTERIOR, null);
        lastId++;

        Dog dog3 = new Dog(lastId+1,"NODE 3", 3, 0, null, false, Constants.WT_INTERIOR, null);
        lastId++;

        Dog dog4 = new Dog(lastId+1,"NODE 4", 4, 0, null, false, Constants.WT_INTERIOR, null);
        lastId++;

        Dog dog5 = new Dog(lastId+1,"NODE 5", 5, 0, null, false, Constants.WT_INTERIOR, null);
        lastId++;

        Dog dog6 = new Dog(lastId+1,"NODE 6", 6, 0, null, false, Constants.WT_INTERIOR, null);
        lastId++;

        dog1.friends.add(dog21);
        dog1.friends.add(dog22);
        dog1.friends.add(dog3);
        dog1.friends.add(dog4);
        dog1.friends.add(dog5);

        dog21.friends.add(dog1);
        dog21.friends.add(dog3);

        dog22.friends.add(dog1);
        dog22.friends.add(dog3);

        dog3.friends.add(dog1);
        dog3.friends.add(dog21);
        dog3.friends.add(dog22);
        dog3.friends.add(dog4);

        dog4.friends.add(dog1);
        dog4.friends.add(dog3);

        dog5.friends.add(dog1);
        dog5.friends.add(dog6);

        dog6.friends.add(dog5);

        dogs.add(dog1);
        dogs.add(dog21);
        dogs.add(dog22);
        dogs.add(dog3);
        dogs.add(dog4);
        dogs.add(dog5);
        dogs.add(dog6);

        dbAdapter.SaveDogs(dogs);
    }

    public void TESTINTERIORSANDSPECIALS1(List<VolunteerWalks> volunteers, List<Dog> dogs, DBAdapter dbAdapter)
    {
        volunteers.get(0).favouriteDogs = new ArrayList<Dog>();
        volunteers.get(0).favouriteDogs.add(dogs.get(0));
        volunteers.get(0).favouriteDogs.add(dogs.get(1));
        volunteers.get(0).favouriteDogs.add(dogs.get(2));

        volunteers.get(1).favouriteDogs = new ArrayList<Dog>();
        volunteers.get(1).favouriteDogs.add(dogs.get(1));
        volunteers.get(1).favouriteDogs.add(dogs.get(3));

        volunteers.get(2).favouriteDogs = new ArrayList<Dog>();
        volunteers.get(2).favouriteDogs.add(dogs.get(0));

        dogs.get(0).special = true;
        dogs.get(1).special = true;
    }

    public void TESTINTERIORSANDSPECIALS2(List<VolunteerWalks> volunteers, List<Dog> dogs, DBAdapter dbAdapter)
    {
        volunteers.get(0).favouriteDogs = new ArrayList<Dog>();
        volunteers.get(0).favouriteDogs.add(dogs.get(0));
        volunteers.get(0).favouriteDogs.add(dogs.get(1));
        volunteers.get(0).favouriteDogs.add(dogs.get(2));

        volunteers.get(1).favouriteDogs = new ArrayList<Dog>();
        volunteers.get(1).favouriteDogs.add(dogs.get(3));
        volunteers.get(1).favouriteDogs.add(dogs.get(4));
        volunteers.get(1).favouriteDogs.add(dogs.get(5));

        volunteers.get(2).favouriteDogs = new ArrayList<Dog>();
        volunteers.get(2).favouriteDogs.add(dogs.get(0));
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
        Cage cage12 = new Cage(12,12, Constants.CAGE_ZONE_XENILES);
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
}