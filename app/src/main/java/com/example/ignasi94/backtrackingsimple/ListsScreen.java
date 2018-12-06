package com.example.ignasi94.backtrackingsimple;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.ignasi94.backtrackingsimple.BBDD.DBAdapter;
import com.example.ignasi94.backtrackingsimple.Estructuras.Cage;
import com.example.ignasi94.backtrackingsimple.Estructuras.Dog;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerWalks;
import com.example.ignasi94.backtrackingsimple.Utils.Constants;
import com.example.ignasi94.backtrackingsimple.Utils.RunnableThread;

import java.util.ArrayList;
import java.util.List;

public class ListsScreen extends Activity {

    ArrayList<ArrayList<Dog>>  clean;
    Dog[][] walks;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists_screen);

        DBAdapter dbAdapter = new DBAdapter(this);

        Button goConfigureListsButton = (Button) findViewById(R.id.button_configurar_lista);
        goConfigureListsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchactivity= new Intent(ListsScreen.this,SelectVolunteers.class);
                launchactivity.putExtra("NEW", true);
                startActivity(launchactivity);
            }
        });

        Button goEditConfigureListsButton = (Button) findViewById(R.id.button_editar_configuracion);
        goEditConfigureListsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchactivity= new Intent(ListsScreen.this,SelectVolunteers.class);
                launchactivity.putExtra("NEW", false);
                startActivity(launchactivity);
            }
        });

        Button goMakeListsButton = (Button) findViewById(R.id.button_crear_lista);
        goMakeListsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Dog> dogs = dbAdapter.getAllDogs();
                List<Cage> cages = GetCages();
                List<VolunteerWalks> volunteers = dbAdapter.getAllSelectedVolunteers();
                int npaseos = volunteers.get(0).nPaseos;
                RunnableThread rT = new RunnableThread("Test", dogs, cages, volunteers);
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
                dbAdapter.SaveWalkSolution(walks, volunteers);
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