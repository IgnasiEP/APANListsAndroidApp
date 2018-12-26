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
import android.widget.TextView;

import com.example.ignasi94.backtrackingsimple.BBDD.DBAdapter;
import com.example.ignasi94.backtrackingsimple.Estructuras.Dog;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerDog;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerWalks;
import com.example.ignasi94.backtrackingsimple.R;

import java.util.ArrayList;
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
        setContentView(R.layout.lists_activity_show_solution);
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

    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
        this.ReadMakeListsParameters(getIntent());
        // Inicializar grid
        GridView dogGrid = (GridView) findViewById(R.id.grid_dogs);
        dogGrid.setNumColumns(nPaseos+1);
        // Crear Adapter
        DogAdapter dogAdapter = new DogAdapter(getApplicationContext(), walkSolutionArray);
        // Relacionar el adapter a la grid
        dogGrid.setAdapter(dogAdapter);
    }

    public void ReadMakeListsParameters(Intent intent)
    {
        dbAdapter = new DBAdapter(this);
        nPaseos = getIntent().getIntExtra("nPaseos", 0);
        ArrayList<VolunteerWalks> selectedVolunteers = (ArrayList) dbAdapter.getAllSelectedVolunteers();
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
}