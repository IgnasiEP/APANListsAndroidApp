package com.example.ignasi94.backtrackingsimple.Screens.ListsScreens;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.ignasi94.backtrackingsimple.BBDD.DBAdapter;
import com.example.ignasi94.backtrackingsimple.Estructuras.Dog;
import com.example.ignasi94.backtrackingsimple.Estructuras.Volunteer;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerDog;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerWalks;
import com.example.ignasi94.backtrackingsimple.R;
import com.example.ignasi94.backtrackingsimple.Utils.Constants;

import java.util.ArrayList;
import java.util.Dictionary;

public class ShowCleanSolution extends Activity {

    Integer nPaseos;
    Integer nVolunteers;
    Integer gridColumns;
    ArrayList<VolunteerWalks> volunteers;
    ArrayList<ArrayList<VolunteerDog>> cleanSolution;
    DBAdapter dbAdapter;
    ArrayList<VolunteerDog> cleanGridArray;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lists_activity_show_clean_solution);
        gridColumns = 5;
        this.ReadMakeListsParameters(getIntent());

        GridView dogGrid = (GridView) findViewById(R.id.grid_clean_dogs);
        dogGrid.setNumColumns(gridColumns);
        // Crear Adapter
        CleanDogAdapter cleanDogAdapter = new CleanDogAdapter(getApplicationContext(), this.cleanGridArray);
        // Relacionar el adapter a la grid
        dogGrid.setAdapter(cleanDogAdapter);


    }

    public void ReadMakeListsParameters(Intent intent) {
        dbAdapter = new DBAdapter(this);
        nPaseos = getIntent().getIntExtra("nPaseos", 0);
        volunteers = (ArrayList) dbAdapter.getAllSelectedVolunteers();
        nVolunteers = volunteers.size();
        cleanGridArray = dbAdapter.GetCleanSolution(nPaseos, gridColumns);


        /*dbAdapter = new DBAdapter(this);
        Dictionary<Integer, Dog> dogs = dbAdapter.getAllDogsDictionary();
        volunteers = (ArrayList) dbAdapter.getAllVolunteers();
        nPaseos = intent.getIntExtra("nPaseos", 0);
        nVolunteers = intent.getIntExtra("nVolunteers", 0);

        cleanSolution = new ArrayList<ArrayList<VolunteerDog>>();
        for (int i = 0; i < nPaseos; ++i) {
            ArrayList<Integer> iArray = intent.getIntegerArrayListExtra("CleanSolution" + i);
            ArrayList<VolunteerDog> iArraySolution = new ArrayList<VolunteerDog>();
            for(int j = 0; j < iArray.size(); ++j)
            {
                Dog dog = dogs.get(iArray.get(j));
                if(i == 0)
                {
                    iArraySolution.add(new VolunteerDog(dog, null));
                    iArraySolution.add(new VolunteerDog(dog, null));
                    iArraySolution.add(new VolunteerDog(dog, null));
                    iArraySolution.add(new VolunteerDog(dog, null));
                    iArraySolution.add(new VolunteerDog(dog, null));
                }
                iArraySolution.add(new VolunteerDog(dog, null));
            }
            cleanSolution.add(iArraySolution);
        }

        cleanGridArray = new ArrayList<VolunteerDog>();
        for(int i = 0; i < cleanSolution.size();++i)
        {
            int dogsToAdd = 0;
            for(int j = 0; j < cleanSolution.get(i).size(); ++j) {
                if ((dogsToAdd % gridColumns) == 0 && j == 0) {
                    cleanGridArray.add(new VolunteerDog(null, i + 1, true));
                    ++dogsToAdd;
                } else if ((dogsToAdd % gridColumns) == 0) {
                    cleanGridArray.add(new VolunteerDog(null, i + 1, false));
                    ++dogsToAdd;
                }
                cleanGridArray.add(cleanSolution.get(i).get(j));
                ++dogsToAdd;
            }
            while((dogsToAdd % gridColumns) != 0)
            {
                cleanGridArray.add(new VolunteerDog(new Dog(Constants.DEFAULT_DOG_NAME), null));
                ++dogsToAdd;
            }
        }*/
    }

    public class CleanDogAdapter extends BaseAdapter {
        Context context;
        ArrayList<VolunteerDog> matrixList;

        public CleanDogAdapter(Context context, ArrayList<VolunteerDog> matrixList) {
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
            VolunteerDog volunteerDog = cleanGridArray.get(position);
            if((position % gridColumns) == 0)
            {
                if(volunteerDog.visibility) {
                    imageViewAndroid.setImageResource(R.mipmap.ic_doggrid_empty_image);
                    textViewAndroid.setText(volunteerDog.cleanRow.toString());
                }
                else
                {
                    imageViewAndroid.setImageResource(R.mipmap.ic_white_dog);
                    textViewAndroid.setText("");
                }
            }
            else {
                String dogName = volunteerDog.dog.name;
                textViewAndroid.setText(dogName);
                if(dogName.isEmpty())
                {
                    imageViewAndroid.setImageResource(R.mipmap.ic_white_dog);
                }
                else {
                    imageViewAndroid.setImageResource(R.mipmap.ic_dog_default);
                }
            }

            return gridViewAndroid;
        }
    }
}

