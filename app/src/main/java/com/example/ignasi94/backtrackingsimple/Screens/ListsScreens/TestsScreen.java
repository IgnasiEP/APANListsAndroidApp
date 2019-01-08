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
import com.example.ignasi94.backtrackingsimple.Utils.RunnableThread;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestsScreen extends Activity {

    ArrayList<ArrayList<Dog>>  clean;
    Dog[][] walks;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lists_activity_tests_screen);

        DBAdapter dbAdapter = new DBAdapter(this);
        dbAdapter.CleanTestTables();

        Button walksTestButton = (Button) findViewById(R.id.button_walks_test);
        walksTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Dog> dogs = dbAdapter.getAllDogs();
                List<Cage> cages = dbAdapter.getAllCages();
                List<Volunteer> allVolunteers = dbAdapter.getAllVolunteers();
                List<VolunteerWalks> volunteers = RandomSelectedVolunteers(allVolunteers, true);
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
                Intent launchactivity= new Intent(TestsScreen.this,ShowTestSolution.class);
                //SetOutputParameters(launchactivity, npaseos, volunteers.size(), rT);
                launchactivity.putExtra("nPaseos", npaseos);
                launchactivity.putExtra("Test", "WALKS");
                dbAdapter.SaveSelectedVolunteersTest(volunteerWalks);
                dbAdapter.CleanSolutionsTables();
                dbAdapter.SaveWalkSolution(walks, volunteerWalks);
                dbAdapter.SaveCleanSolution(clean);
                startActivity(launchactivity);
            }
        });

        Button favouritesTestButton = (Button) findViewById(R.id.button_favourites_test);
        favouritesTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Dog> dogs = dbAdapter.getAllDogs();
                List<Cage> cages = dbAdapter.getAllCages();
                List<Volunteer> allVolunteers = dbAdapter.getAllVolunteers();
                List<VolunteerWalks> volunteers = RandomSelectedVolunteers(allVolunteers, false);
                RandomFavourites(volunteers);
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
                Intent launchactivity= new Intent(TestsScreen.this,ShowTestSolution.class);
                //SetOutputParameters(launchactivity, npaseos, volunteers.size(), rT);
                launchactivity.putExtra("nPaseos", npaseos);
                launchactivity.putExtra("Test", "FAVOURITES");
                dbAdapter.SaveSelectedVolunteersTest(volunteerWalks);
                for(int i = 0; i < volunteerWalks.size(); ++i)
                {
                    dbAdapter.SaveOrUpdateVolunteerTest(volunteerWalks.get(i));
                }
                dbAdapter.CleanSolutionsTables();
                dbAdapter.SaveWalkSolution(walks, volunteerWalks);
                dbAdapter.SaveCleanSolution(clean);
                startActivity(launchactivity);
            }
        });

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

    public List<VolunteerWalks> RandomSelectedVolunteers(List<Volunteer> allVolunteers, boolean randomWalks)
    {
        List<VolunteerWalks> volunteers = new ArrayList<VolunteerWalks>();
        Random random = new Random();

        int npaseos = random.nextInt(3);

        if(npaseos == 0)
        {
            npaseos = 3;
        }
        else if(npaseos == 1)
        {
            npaseos = 4;
        }
        else
        {
            npaseos = 5;
        }

        for(int i = 0; i < allVolunteers.size(); ++i)
        {
            int x = random.nextInt(4);
            if(x == 0)
            {
                Volunteer volunteer = allVolunteers.get(i);
                VolunteerWalks volunteerWalk = new VolunteerWalks();
                if(randomWalks)
                {
                    volunteerWalk = new VolunteerWalks(volunteer.id, volunteer.name, 0,random.nextInt(2),random.nextInt(2),random.nextInt(2),random.nextInt(2),random.nextInt(2), npaseos);
                }
                else
                {
                    volunteerWalk = new VolunteerWalks(volunteer.id, volunteer.name, 0,1,1,1,1,1, npaseos);
                }
                volunteers.add(volunteerWalk);
            }
        }

        return volunteers;
    }

    public void RandomFavourites(List<VolunteerWalks> selectedVolunteers)
    {

    }

}
