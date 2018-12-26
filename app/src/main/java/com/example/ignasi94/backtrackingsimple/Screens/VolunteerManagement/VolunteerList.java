package com.example.ignasi94.backtrackingsimple.Screens.VolunteerManagement;

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
import com.example.ignasi94.backtrackingsimple.Estructuras.Volunteer;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerDog;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerWalks;
import com.example.ignasi94.backtrackingsimple.R;
import com.example.ignasi94.backtrackingsimple.Screens.DogManagement.DogList;
import com.example.ignasi94.backtrackingsimple.Screens.DogManagement.EditDog;
import com.example.ignasi94.backtrackingsimple.Screens.DogManagement.ShowDog;
import com.example.ignasi94.backtrackingsimple.Screens.DogsDistributionScreens.DistributionOptionsScreen;
import com.example.ignasi94.backtrackingsimple.Screens.DogsDistributionScreens.EditDistribution;
import com.example.ignasi94.backtrackingsimple.Utils.Constants;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class VolunteerList extends Activity {

    GridView gridView;
    ArrayList<Volunteer> volunteers;
    VolunteerAdapter volunteerAdapter;
    DBAdapter dbAdapter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.volunteer_management_volunteer_list);

        this.ReadMakeListsParameters(getIntent());

        gridView = (GridView) findViewById(R.id.grid_volunteer_lists);
        gridView.setNumColumns(1);
        // Adapter
        volunteerAdapter = new VolunteerAdapter(getApplicationContext(), volunteers);
        gridView.setAdapter(volunteerAdapter);
    }

    public void ReadMakeListsParameters(Intent intent)
    {
        dbAdapter = new DBAdapter(this);
        volunteers = (ArrayList<Volunteer>) dbAdapter.getAllVolunteers();
    }

    public class VolunteerAdapter extends BaseAdapter {
        Context context;
        ArrayList<Volunteer> matrixList;

        public VolunteerAdapter(Context context, ArrayList<Volunteer> matrixList) {
            this.context = context;
            matrixList.add(0, new Volunteer());
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
                gridViewAndroid = inflater.inflate(R.layout.volunteer_management_volunteer_item, null);
            }
            TextView textViewAndroid = (TextView) gridViewAndroid.findViewById(R.id.volunteer_name);
            ImageView imageViewAndroid = (ImageView) gridViewAndroid.findViewById(R.id.volunteer_image);
            if(position == 0)
            {
                //'Add element' element
                textViewAndroid.setText("Añadir voluntario/a");
                imageViewAndroid.setImageResource(R.mipmap.ic_cruz);
            }
            else {
                String dogName = matrixList.get(position).name;
                textViewAndroid.setText(dogName);
                imageViewAndroid.setImageResource(R.mipmap.ic_volunteer_default);
            }

            gridViewAndroid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent launchactivity;
                    if(position == 0)
                    {
                        launchactivity = new Intent(VolunteerList.this,EditVolunteer.class);
                        launchactivity.putExtra("NEW", true);
                    }
                    else
                    {
                        launchactivity = new Intent(VolunteerList.this,ShowVolunteer.class);
                        launchactivity.putExtra("VOLUNTEERID", matrixList.get(position).id);
                    }
                    startActivity(launchactivity);
                }
            });

            return gridViewAndroid;
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();

        volunteers = (ArrayList<Volunteer>) dbAdapter.getAllVolunteers();

        gridView = (GridView) findViewById(R.id.grid_volunteer_lists);
        gridView.setNumColumns(1);
        // Adapter
        volunteerAdapter = new VolunteerAdapter(getApplicationContext(), volunteers);
        gridView.setAdapter(volunteerAdapter);
    }
}
