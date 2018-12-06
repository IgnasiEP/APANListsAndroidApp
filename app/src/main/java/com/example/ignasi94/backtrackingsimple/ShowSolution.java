package com.example.ignasi94.backtrackingsimple;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup.LayoutParams;

import com.example.ignasi94.backtrackingsimple.BBDD.DBAdapter;
import com.example.ignasi94.backtrackingsimple.Estructuras.Dog;
import com.example.ignasi94.backtrackingsimple.Estructuras.Volunteer;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerDog;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerWalks;
import com.example.ignasi94.backtrackingsimple.R;
import com.example.ignasi94.backtrackingsimple.Utils.RunnableThread;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

public class ShowSolution extends Activity {

    Integer nPaseos;
    Integer nVolunteers;
    ArrayList<VolunteerWalks> volunteers;
    Dog[][] walkSolution;
    ArrayList<VolunteerDog> walkSolutionArray;
    ArrayList<ArrayList<Integer>> cleanSolution;
    DBAdapter dbAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_solution);
        this.ReadMakeListsParameters(getIntent());
        // Inicializar grid
        GridView dogGrid = (GridView) findViewById(R.id.grid_dogs);
        dogGrid.setNumColumns(nPaseos+1);
        // Crear Adapter
        DogAdapter dogAdapter = new DogAdapter(getApplicationContext(), walkSolutionArray);
        // Relacionar el adapter a la grid
        dogGrid.setAdapter(dogAdapter);

        Button editButton = (Button) findViewById(R.id.button_editar_lista);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchactivity= new Intent(ShowSolution.this,EditSolution.class);
                launchactivity.putExtras(getIntent().getExtras());
                startActivity(launchactivity);
            }
        });

        Button showClean = (Button) findViewById(R.id.button_limpieza);
        showClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchactivity= new Intent(ShowSolution.this,ShowCleanSolution.class);
                launchactivity.putExtras(getIntent().getExtras());
                startActivity(launchactivity);
            }
        });
    }

    public void ReadMakeListsParameters(Intent intent)
    {
        dbAdapter = new DBAdapter(this);
        nPaseos = getIntent().getIntExtra("nPaseos", 0);
        volunteers = (ArrayList) dbAdapter.getAllSelectedVolunteers();
        nVolunteers = volunteers.size();
        walkSolutionArray = dbAdapter.GetWalkSolution(nVolunteers,nPaseos+1);

        /*dbAdapter = new DBAdapter(this);
        Dictionary<Integer,Dog>  dogs = dbAdapter.getAllDogsDictionary();
        volunteers = (ArrayList) dbAdapter.getAllVolunteers();
        nPaseos = intent.getIntExtra("nPaseos", 0);
        nVolunteers = intent.getIntExtra("nVolunteers", 0);
        walkSolution = new Dog[nVolunteers][nPaseos];
        walkSolutionArray = new ArrayList<VolunteerDog>();
        for(int i = 0; i < nPaseos; ++i)
        {
            ArrayList<Integer> iArray = intent.getIntegerArrayListExtra("WalkSolution"+i);
            for(int j = 0; j < iArray.size(); ++j)
            {
                walkSolution[j][i] = dogs.get(iArray.get(j));
            }

        }

        for(int i = 0; i < nVolunteers; ++i)
        {
            for(int j = 0; j < nPaseos + 1; ++j)
            {
                if(j == 0)
                {
                    walkSolutionArray.add(new VolunteerDog(null, volunteers.get(i)));
                }
                else {
                    walkSolutionArray.add(new VolunteerDog(walkSolution[i][j-1], null));
                }
            }
        }

        cleanSolution = new ArrayList<ArrayList<Integer>>();
        for(int i = 0; i < nPaseos; ++i)
        {
            ArrayList<Integer> iArray = intent.getIntegerArrayListExtra("CleanSolution"+i);
            cleanSolution.add(i,iArray);
        }*/
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
                gridViewAndroid = inflater.inflate(R.layout.griditem_dogs, null);
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
}