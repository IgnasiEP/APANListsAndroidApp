package com.example.ignasi94.backtrackingsimple.Screens.ListsScreens;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ignasi94.backtrackingsimple.BBDD.DBAdapter;
import com.example.ignasi94.backtrackingsimple.Estructuras.Dog;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerDog;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerWalks;
import com.example.ignasi94.backtrackingsimple.R;

import java.util.ArrayList;
import java.util.List;

public class ShowTestSolution3 extends Activity {

    Integer nPaseos;
    Integer nVolunteers;
    ArrayList<VolunteerWalks> volunteers;
    Dog[][] walkSolution;
    ArrayList<VolunteerDog> walkSolutionArray;
    ArrayList<ArrayList<Integer>> cleanSolution;
    ArrayList<Dog> specialDogs;
    DBAdapter dbAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lists_activity_show_test_solution3);
        this.ReadMakeListsParameters(getIntent());
        // Inicializar grid
        GridView dogGrid = (GridView) findViewById(R.id.grid_dogs);
        dogGrid.setNumColumns(nPaseos+1);
        // Crear Adapter
        DogAdapter dogAdapter = new DogAdapter(getApplicationContext(), walkSolutionArray);
        // Relacionar el adapter a la grid
        dogGrid.setAdapter(dogAdapter);


        for(int i = 0; i < volunteers.size(); ++i)
        {
            VolunteerWalks volunteer = volunteers.get(i);
            dbAdapter.getDogFavouritesTest(volunteer);
        }
        GridView favoritosGrid = (GridView) findViewById(R.id.grid_test_info);
        favoritosGrid.setNumColumns(6);
        FavouritesAdapter favouritesAdapter = new FavouritesAdapter(getApplicationContext(), volunteers);
        favoritosGrid.setAdapter(favouritesAdapter);


        GridView specialGrid = (GridView) findViewById(R.id.grid_special_dogs);
        specialGrid.setNumColumns(6);
        SpecialAdapter specialAdapter = new SpecialAdapter(getApplicationContext(), specialDogs);
        specialGrid.setAdapter(specialAdapter);

        Button showClean = (Button) findViewById(R.id.button_limpieza);
        showClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchactivity= new Intent(ShowTestSolution3.this,ShowCleanSolution.class);
                launchactivity.putExtras(getIntent().getExtras());
                startActivity(launchactivity);
            }
        });
    }

    public void ReadMakeListsParameters(Intent intent)
    {
        dbAdapter = new DBAdapter(this);
        nPaseos = getIntent().getIntExtra("nPaseos", 0);
        ArrayList<VolunteerWalks> selectedVolunteers = (ArrayList) dbAdapter.getAllSelectedVolunteersTest();
        ArrayList<Dog> dogs = (ArrayList) dbAdapter.getAllDogsTest();
        specialDogs = new ArrayList<Dog>();
        for(int i = 0; i < dogs.size(); ++i)
        {
            if(dogs.get(i).special)
            {
                specialDogs.add(dogs.get(i));
            }
        }
        volunteers = this.EraseCleaningVolunteers(selectedVolunteers);
        nVolunteers = volunteers.size();
        walkSolutionArray = dbAdapter.GetWalkSolution(nVolunteers,nPaseos+1);
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

    public class DogAdapter extends BaseAdapter {
        Context context;
        ArrayList<VolunteerDog> matrixList;

        public DogAdapter(Context context, ArrayList<VolunteerDog> matrixList) {
            this.context = context;
            this.matrixList = matrixList;
        }

        @Override
        public int getCount() {
            return matrixList.size();
        }

        @Override
        public Object getItem(int i) {
            return matrixList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            View gridViewAndroid = view;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                gridViewAndroid = inflater.inflate(R.layout.lists_griditem_dogs, null);
            }
            TextView textViewAndroid = (TextView) gridViewAndroid.findViewById(R.id.android_gridview_text);
            ImageView imageViewAndroid = (ImageView) gridViewAndroid.findViewById(R.id.android_gridview_image);
            if((position % (nPaseos+1)) == 0)
            {
                textViewAndroid.setText(walkSolutionArray.get(position).volunteer.name);
                imageViewAndroid.setImageResource(R.mipmap.ic_volunteer_default);
            }
            else {
                String dogName = walkSolutionArray.get(position).dog.name;
                textViewAndroid.setText(dogName);
                if(dogName.isEmpty())
                {
                    imageViewAndroid.setImageResource(R.mipmap.ic_doggrid_empty_image);
                }
                else {
                    imageViewAndroid.setImageResource(R.mipmap.ic_dog_default);
                }
            }

            return gridViewAndroid;
        }
    }

    public class FavouritesAdapter extends BaseAdapter {
        Context context;
        ArrayList<VolunteerWalks> matrixList;

        public FavouritesAdapter(Context context, ArrayList<VolunteerWalks> matrixList) {
            this.context = context;
            ArrayList<VolunteerWalks> tmp = new ArrayList<VolunteerWalks>();
            for(int i = 0; i < matrixList.size(); ++i)
            {
                tmp.add(matrixList.get(i));
                tmp.add(new VolunteerWalks());
                tmp.add(new VolunteerWalks());
                tmp.add(new VolunteerWalks());
                tmp.add(new VolunteerWalks());
                tmp.add(new VolunteerWalks());
            }
            this.matrixList = tmp;
        }

        @Override
        public int getCount() {
            return matrixList.size();
        }

        @Override
        public Object getItem(int i) {
            return matrixList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            View gridViewAndroid = view;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                gridViewAndroid = inflater.inflate(R.layout.lists_griditem_textview, null);
            }
            TextView textViewAndroid = (TextView) gridViewAndroid.findViewById(R.id.android_textview);
            if((position % (6)) == 0)
            {
                textViewAndroid.setText(matrixList.get(position).name);
            }
            if((position % (6)) == 1)
            {
                textViewAndroid.setText(matrixList.get(position-1).favouriteDogs.get(0).name);
            }
            if((position % (6)) == 2)
            {
                textViewAndroid.setText(matrixList.get(position-2).favouriteDogs.get(1).name);
            }
            if((position % (6)) == 3)
            {
                textViewAndroid.setText(matrixList.get(position-3).favouriteDogs.get(2).name);
            }
            if((position % (6)) == 4)
            {
                textViewAndroid.setText(matrixList.get(position-4).favouriteDogs.get(3).name);
            }
            if((position % (6)) == 5)
            {
                textViewAndroid.setText(matrixList.get(position-5).favouriteDogs.get(4).name);
            }


            return gridViewAndroid;
        }
    }

    public class SpecialAdapter extends BaseAdapter {
        Context context;
        ArrayList<Dog> matrixList;

        public SpecialAdapter(Context context, ArrayList<Dog> matrixList) {
            this.context = context;
            this.matrixList = matrixList;
        }

        @Override
        public int getCount() {
            return matrixList.size();
        }

        @Override
        public Object getItem(int i) {
            return matrixList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            View gridViewAndroid = view;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                gridViewAndroid = inflater.inflate(R.layout.lists_griditem_textview, null);
            }
            TextView textViewAndroid = (TextView) gridViewAndroid.findViewById(R.id.android_textview);
            textViewAndroid.setText(matrixList.get(position).name);

            return gridViewAndroid;
        }
    }
}