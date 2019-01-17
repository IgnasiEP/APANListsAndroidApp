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
import java.util.Dictionary;
import java.util.List;
import java.util.Random;

public class TestsScreen extends Activity {

    ArrayList<ArrayList<Dog>>  clean;
    Dog[][] walks;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lists_activity_tests_screen);

        DBAdapter dbAdapter = new DBAdapter(this);

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
                Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
                    public void uncaughtException(Thread th, Throwable ex) {
                    }
                };
                t.setUncaughtExceptionHandler(h);
                t.start();
                try {
                    //Se para el algoritmo siempre a los 10 segundos
                    t.join(10000);
                    if(t.isAlive())
                    {
                        //Si el algoritmo no ha encontrado solución en 10 segundos devolvemos solución vacía
                        t.interrupt();
                        walks = new Dog[npaseos][volunteers.size()];
                        clean = new ArrayList <ArrayList <Dog> >();
                    }
                    else {
                        walks = rT.walksTable;
                        clean = rT.cleanTable;
                    }
                } catch (Exception e){
                }

                if(walks == null)
                {
                    walks = new Dog[npaseos][volunteers.size()];
                }
                if(clean == null)
                {
                    clean = new ArrayList <ArrayList <Dog> >();
                }

                //Pasamos la solución de paseos a matriz de id's
                Intent launchactivity= new Intent(TestsScreen.this,ShowTestSolution.class);
                //SetOutputParameters(launchactivity, npaseos, volunteers.size(), rT);
                launchactivity.putExtra("nPaseos", npaseos);
                launchactivity.putExtra("Test", "WALKS");
                dbAdapter.CleanTestTables();
                dbAdapter.SaveSelectedVolunteersTest(volunteerWalks);
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
                RandomFavourites(volunteers, dbAdapter.getAllDogsDictionary());
                int npaseos = volunteers.get(0).nPaseos;
                ArrayList<VolunteerWalks> volunteerWalks = new ArrayList<VolunteerWalks>();
                volunteerWalks = EraseCleaningVolunteers(volunteers);
                RunnableThread rT = new RunnableThread("Test", dogs, cages, volunteerWalks);
                ThreadGroup tg = new ThreadGroup("TestGroup1");
                Thread t = new Thread(tg,rT,rT.getName(), 128*1024*1024);
                Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
                    public void uncaughtException(Thread th, Throwable ex) {
                    }
                };
                t.setUncaughtExceptionHandler(h);
                t.start();
                try {
                    //Se para el algoritmo siempre a los 10 segundos
                    t.join(10000);
                    if(t.isAlive())
                    {
                        //Si el algoritmo no ha encontrado solución en 10 segundos devolvemos solución vacía
                        t.interrupt();
                        walks = new Dog[npaseos][volunteers.size()];
                        clean = new ArrayList <ArrayList <Dog> >();
                    }
                    else {
                        walks = rT.walksTable;
                        clean = rT.cleanTable;
                    }
                } catch (Exception e){
                }

                if(walks == null)
                {
                    walks = new Dog[npaseos][volunteers.size()];
                }
                if(clean == null)
                {
                    clean = new ArrayList <ArrayList <Dog> >();
                }

                //Pasamos la solución de paseos a matriz de id's
                Intent launchactivity= new Intent(TestsScreen.this,ShowTestSolution.class);
                //SetOutputParameters(launchactivity, npaseos, volunteers.size(), rT);
                launchactivity.putExtra("nPaseos", npaseos);
                launchactivity.putExtra("Test", "FAVOURITES");
                dbAdapter.CleanTestTables();
                dbAdapter.SaveSelectedVolunteersTest(volunteerWalks);
                for(int i = 0; i < volunteerWalks.size(); ++i)
                {
                    dbAdapter.SaveOrUpdateVolunteerTest(volunteerWalks.get(i));
                }
                dbAdapter.SaveWalkSolution(walks, volunteerWalks);
                dbAdapter.SaveCleanSolution(clean);
                startActivity(launchactivity);
            }
        });

        Button friendsTestButton = (Button) findViewById(R.id.button_friends_test);
        friendsTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Dog> dogs = dbAdapter.getAllDogs();
                List<Cage> cages = dbAdapter.getAllCages();
                List<Volunteer> allVolunteers = dbAdapter.getAllVolunteers();
                List<VolunteerWalks> volunteers = RandomSelectedVolunteers(allVolunteers, false);
                RandomFriendsFavourites(dogs);
                int npaseos = volunteers.get(0).nPaseos;
                ArrayList<VolunteerWalks> volunteerWalks = new ArrayList<VolunteerWalks>();
                volunteerWalks = EraseCleaningVolunteers(volunteers);
                RunnableThread rT = new RunnableThread("Test", dogs, cages, volunteerWalks);
                ThreadGroup tg = new ThreadGroup("TestGroup1");
                Thread t = new Thread(tg,rT,rT.getName(), 128*1024*1024);
                Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
                    public void uncaughtException(Thread th, Throwable ex) {
                    }
                };
                t.setUncaughtExceptionHandler(h);
                t.start();
                try {
                    //Se para el algoritmo siempre a los 10 segundos
                    t.join(10000);
                    if(t.isAlive())
                    {
                        //Si el algoritmo no ha encontrado solución en 10 segundos devolvemos solución vacía
                        t.interrupt();
                        walks = new Dog[npaseos][volunteers.size()];
                        clean = new ArrayList <ArrayList <Dog> >();
                    }
                    else {
                        walks = rT.walksTable;
                        clean = rT.cleanTable;
                    }
                } catch (Exception e){
                }

                if(walks == null)
                {
                    walks = new Dog[npaseos][volunteers.size()];
                }
                if(clean == null)
                {
                    clean = new ArrayList <ArrayList <Dog> >();
                }

                //Pasamos la solución de paseos a matriz de id's
                Intent launchactivity= new Intent(TestsScreen.this,ShowTestSolution.class);
                //SetOutputParameters(launchactivity, npaseos, volunteers.size(), rT);
                launchactivity.putExtra("nPaseos", npaseos);
                launchactivity.putExtra("Test", "FRIENDS");
                dbAdapter.CleanTestTables();
                dbAdapter.SaveDogsTest(dogs);
                dbAdapter.SaveSelectedVolunteersTest(volunteerWalks);
                dbAdapter.SaveWalkSolution(walks, volunteerWalks);
                dbAdapter.SaveCleanSolution(clean);
                startActivity(launchactivity);
            }
        });

        Button specialTestButton = (Button) findViewById(R.id.button_special_test);
        specialTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Dog> dogs = dbAdapter.getAllDogs();
                List<Cage> cages = dbAdapter.getAllCages();
                List<Volunteer> allVolunteers = dbAdapter.getAllVolunteers();
                List<VolunteerWalks> volunteers = RandomSelectedVolunteers(allVolunteers, false);
                RandomSpecial(dogs);
                RandomFavourites(volunteers, dbAdapter.getAllDogsDictionary());
                int npaseos = volunteers.get(0).nPaseos;
                ArrayList<VolunteerWalks> volunteerWalks = new ArrayList<VolunteerWalks>();
                volunteerWalks = EraseCleaningVolunteers(volunteers);
                RunnableThread rT = new RunnableThread("Test", dogs, cages, volunteerWalks);
                ThreadGroup tg = new ThreadGroup("TestGroup1");
                Thread t = new Thread(tg,rT,rT.getName(), 128*1024*1024);
                Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
                    public void uncaughtException(Thread th, Throwable ex) {
                    }
                };
                t.setUncaughtExceptionHandler(h);
                t.start();
                try {
                    //Se para el algoritmo siempre a los 10 segundos
                    t.join(10000);
                    if(t.isAlive())
                    {
                        //Si el algoritmo no ha encontrado solución en 10 segundos devolvemos solución vacía
                        t.interrupt();
                        walks = new Dog[npaseos][volunteers.size()];
                        clean = new ArrayList <ArrayList <Dog> >();
                    }
                    else {
                        walks = rT.walksTable;
                        clean = rT.cleanTable;
                    }
                } catch (Exception e){
                }

                if(walks == null)
                {
                    walks = new Dog[npaseos][volunteers.size()];
                }
                if(clean == null)
                {
                    clean = new ArrayList <ArrayList <Dog> >();
                }

                //Pasamos la solución de paseos a matriz de id's
                Intent launchactivity= new Intent(TestsScreen.this,ShowTestSolution3.class);
                //SetOutputParameters(launchactivity, npaseos, volunteers.size(), rT);
                launchactivity.putExtra("nPaseos", npaseos);
                launchactivity.putExtra("Test", "SPECIAL");
                dbAdapter.CleanTestTables();
                dbAdapter.SaveSelectedVolunteersTest(volunteerWalks);
                dbAdapter.SaveDogsTest(dogs);
                for(int i = 0; i < volunteerWalks.size(); ++i)
                {
                    dbAdapter.SaveOrUpdateVolunteerTest(volunteerWalks.get(i));
                }
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

    public void RandomFavourites(List<VolunteerWalks> selectedVolunteers, Dictionary<Integer,Dog> dogs)
    {
        Random random = new Random();
        for(int i = 0; i < selectedVolunteers.size(); ++i)
        {
            VolunteerWalks volunteerWalks = selectedVolunteers.get(i);

            volunteerWalks.favouriteDogs = new ArrayList<Dog>();

            while(volunteerWalks.favouriteDogs.size() < 5)
            {
                int id = random.nextInt(dogs.size());

                boolean contains = false;
                for(int j = 0; j < volunteerWalks.favouriteDogs.size(); ++j)
                {
                    Dog dog = volunteerWalks.favouriteDogs.get(j);

                    if(id == dog.id)
                    {
                        contains = true;
                        break;
                    }
                }

                if(!contains)
                {
                    Dog getDog = dogs.get(id);
                    if(getDog != null) {
                        volunteerWalks.favouriteDogs.add(getDog);
                    }
                }
            }
        }
    }

    public void RandomFriendsFavourites(List<Dog> dogs)
    {
        Random random = new Random();
        for(int i = 0; i < dogs.size(); ++i) {
            Dog idog = dogs.get(i);
            idog.friends = new ArrayList<Dog>();
        }

        for (int i = 0; i < dogs.size(); ++i) {
            Dog idog = dogs.get(i);

            if (idog.walktype == Constants.WT_INTERIOR) {

                for (int j = i + 1; j < dogs.size(); ++j) {
                    Dog jdog = dogs.get(j);

                    if (jdog.walktype == Constants.WT_INTERIOR) {
                        int add = random.nextInt(4);

                        if (add == 1) {
                            idog.friends.add(jdog);
                            jdog.friends.add(idog);
                        }
                    }
                }
            }
        }
    }

    public void RandomSpecial(List<Dog> dogs)
    {
        Random random = new Random();

        int added = 0;
        for(int i = 0; i < dogs.size(); ++i) {
            Dog idog = dogs.get(i);

            int add = random.nextInt(2);

            if (add > 0) {
                added++;
                idog.special = true;
            }
            else
            {
                idog.special = false;
            }

            if(added >= 6)
            {
                break;
            }
        }
    }

}
