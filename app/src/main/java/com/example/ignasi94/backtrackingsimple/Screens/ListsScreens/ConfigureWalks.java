package com.example.ignasi94.backtrackingsimple.Screens.ListsScreens;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.example.ignasi94.backtrackingsimple.BBDD.DBAdapter;
import com.example.ignasi94.backtrackingsimple.Estructuras.Volunteer;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerWalks;
import com.example.ignasi94.backtrackingsimple.R;

import java.util.ArrayList;
import java.util.Dictionary;

public class ConfigureWalks extends Activity {

    DBAdapter dbAdapter;
    ArrayList<VolunteerWalks> volunteerWalks;
    VolunteerWalksAdapter volunteerWalksAdapter;
    GridView volunteersGrid;
    int nPaseos;
    Dictionary<Integer,Volunteer> volunteersDict;
    boolean wasNewConfig;
    boolean newConfig;
    ArrayList<Boolean> newConfigs;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lists_activity_configure_walks);
        newConfig= getIntent().getBooleanExtra("NEW", true);
        wasNewConfig = newConfig;
        dbAdapter = new DBAdapter(this);
        volunteersDict = dbAdapter.getAllVolunteersDictionary();
        volunteerWalks = dbAdapter.getAllSelectedVolunteers();

        newConfigs = new ArrayList<Boolean>();
        for(int i = 0; i < volunteerWalks.size(); ++i)
        {
            newConfigs.add(newConfig);
        }

        if(!newConfig)
        {
            nPaseos = volunteerWalks.get(0).nPaseos;
        }
        else
        {
            nPaseos = 4;
            this.SetAllSelected(volunteerWalks);
        }

        volunteersGrid = (GridView) findViewById(R.id.grid_volunteers);
        volunteersGrid.setNumColumns(1);
        // Crear Adapter
        volunteerWalksAdapter = new VolunteerWalksAdapter(getApplicationContext(), volunteerWalks, nPaseos);
        // Relacionar el adapter a la grid
        volunteersGrid.setAdapter(volunteerWalksAdapter);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        String[] letra = {"3","4","5"};
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra));
        spinner.setSelection(nPaseos-3);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id)
            {
                String selected = (String) adapterView.getItemAtPosition(pos);
                volunteerWalksAdapter = new VolunteerWalksAdapter(getApplicationContext(), volunteerWalks, Integer.parseInt(selected));
                volunteersGrid.setAdapter(volunteerWalksAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {    }
        });

        Button safeWalksButton = (Button) findViewById(R.id.button_save);
        safeWalksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbAdapter.SaveSelectedVolunteers(volunteerWalksAdapter.matrixList);
                Intent resultIntent = new Intent();
                resultIntent.putExtra("BUTTON", "BUTTON");
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        Button cancelWalksButton = (Button) findViewById(R.id.button_cancelar);
        cancelWalksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbAdapter.CleanSelectedVolunteers();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("BUTTON", "BUTTON");
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    public class VolunteerWalksAdapter extends BaseAdapter {
        Context context;
        ArrayList<VolunteerWalks> matrixList;
        int nPaseos;

        public VolunteerWalksAdapter(Context context, ArrayList<VolunteerWalks> matrixList, int nPaseos) {
            this.context = context;
            this.matrixList = matrixList;
            this.nPaseos = nPaseos;
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
            return this.getView(position,view,viewGroup, -1);
        }

        public View getView(int position, View view, ViewGroup viewGroup, int allSelected) {
            View gridViewAndroid = view;
            if (view == null) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    gridViewAndroid = inflater.inflate(R.layout.lists_griditem_configure_walks, null);
            }

            ImageView imageViewAndroid = (ImageView) gridViewAndroid.findViewById(R.id.android_gridview_image);
            TextView textViewAndroid = (TextView) gridViewAndroid.findViewById(R.id.android_gridview_text);
            Button buttonClean = (Button) gridViewAndroid.findViewById(R.id.button_clean);
            buttonClean.setOnClickListener(new ChangeVolunteerWalkOnClickListener(position));
            Button buttonWalk1 = (Button) gridViewAndroid.findViewById(R.id.button_walk1);
            buttonWalk1.setOnClickListener(new ChangeVolunteerWalkOnClickListener(position));
            Button buttonWalk2 = (Button) gridViewAndroid.findViewById(R.id.button_walk2);
            buttonWalk2.setOnClickListener(new ChangeVolunteerWalkOnClickListener(position));
            Button buttonWalk3 = (Button) gridViewAndroid.findViewById(R.id.button_walk3);
            buttonWalk3.setOnClickListener(new ChangeVolunteerWalkOnClickListener(position));
            Button buttonWalk4 = (Button) gridViewAndroid.findViewById(R.id.button_walk4);
            buttonWalk4.setOnClickListener(new ChangeVolunteerWalkOnClickListener(position));
            Button buttonWalk5 = (Button) gridViewAndroid.findViewById(R.id.button_walk5);
            buttonWalk5.setOnClickListener(new ChangeVolunteerWalkOnClickListener(position));
            Button buttonAll = (Button) gridViewAndroid.findViewById(R.id.button_all);
            buttonAll.setOnClickListener(new ChangeVolunteerWalkOnClickListener(position));

            String nameVolunteer = volunteersDict.get(this.matrixList.get(position).id).name;
            textViewAndroid.setText(nameVolunteer);
            textViewAndroid.setTextColor(Color.BLACK);
            imageViewAndroid.setImageResource(R.mipmap.ic_volunteer_default);

            if(this.nPaseos == 3)
            {
                buttonWalk1.setVisibility(View.VISIBLE);
                buttonWalk2.setVisibility(View.VISIBLE);
                buttonWalk3.setVisibility(View.VISIBLE);
                buttonWalk4.setVisibility(View.INVISIBLE);
                buttonWalk5.setVisibility(View.INVISIBLE);
                buttonAll.setVisibility(View.VISIBLE);
            }

            if(this.nPaseos == 4)
            {
                buttonWalk1.setVisibility(View.VISIBLE);
                buttonWalk2.setVisibility(View.VISIBLE);
                buttonWalk3.setVisibility(View.VISIBLE);
                buttonWalk4.setVisibility(View.VISIBLE);
                buttonWalk5.setVisibility(View.INVISIBLE);
                buttonAll.setVisibility(View.VISIBLE);
            }

            if(this.nPaseos == 5)
            {
                buttonWalk1.setVisibility(View.VISIBLE);
                buttonWalk2.setVisibility(View.VISIBLE);
                buttonWalk3.setVisibility(View.VISIBLE);
                buttonWalk4.setVisibility(View.VISIBLE);
                buttonWalk5.setVisibility(View.VISIBLE);
                buttonAll.setVisibility(View.VISIBLE);
            }

            VolunteerWalks volunteer = volunteerWalksAdapter.matrixList.get(position);
            volunteer.nPaseos = this.nPaseos;

            if(allSelected == 0)
            {
                //Deseleccionar todos
                buttonWalk1.setBackgroundResource(R.drawable.button_border_unclicked);
                buttonWalk1.setTextColor(Color.BLACK);
                buttonWalk2.setBackgroundResource(R.drawable.button_border_unclicked);
                buttonWalk2.setTextColor(Color.BLACK);
                buttonWalk3.setBackgroundResource(R.drawable.button_border_unclicked);
                buttonWalk3.setTextColor(Color.BLACK);
                if(nPaseos >= 4) {
                    buttonWalk4.setBackgroundResource(R.drawable.button_border_unclicked);
                    buttonWalk4.setTextColor(Color.BLACK);
                }
                if(nPaseos >= 5) {
                    buttonWalk5.setBackgroundResource(R.drawable.button_border_unclicked);
                    buttonWalk5.setTextColor(Color.BLACK);
                }
            }
            else if(allSelected == 1)
            {
                //Seleccionar todos
                buttonWalk1.setBackgroundResource(R.drawable.button_border);
                buttonWalk1.setTextColor(Color.WHITE);
                buttonWalk2.setBackgroundResource(R.drawable.button_border);
                buttonWalk2.setTextColor(Color.WHITE);
                buttonWalk3.setBackgroundResource(R.drawable.button_border);
                buttonWalk3.setTextColor(Color.WHITE);
                if(nPaseos >= 4) {
                    buttonWalk4.setBackgroundResource(R.drawable.button_border);
                    buttonWalk4.setTextColor(Color.WHITE);
                }
                if(nPaseos >= 5) {
                    buttonWalk5.setBackgroundResource(R.drawable.button_border);
                    buttonWalk5.setTextColor(Color.WHITE);
                }
            }
            else if(allSelected == 2)
            {
                //Borrar todos paseos
                buttonWalk1.setVisibility(View.INVISIBLE);
                buttonWalk2.setVisibility(View.INVISIBLE);
                buttonWalk3.setVisibility(View.INVISIBLE);
                if(nPaseos >= 4) {
                    buttonWalk4.setVisibility(View.INVISIBLE);
                }
                if(nPaseos >= 5) {
                    buttonWalk5.setVisibility(View.INVISIBLE);
                }
                buttonAll.setVisibility(View.INVISIBLE);
            }
            else if(allSelected == 3)
            {
                //Pintar todos paseos
                buttonWalk1.setVisibility(View.VISIBLE);
                buttonWalk2.setVisibility(View.VISIBLE);
                buttonWalk3.setVisibility(View.VISIBLE);
                if(nPaseos >= 4) {
                    buttonWalk4.setVisibility(View.VISIBLE);
                }
                if(nPaseos >= 5) {
                    buttonWalk5.setVisibility(View.VISIBLE);
                }
                buttonAll.setVisibility(View.VISIBLE);
            }
            else if (allSelected == 4)
            {
                //No hacer nada
            }
            else {
                if(newConfigs.get(position)) {
                    buttonWalk1.setBackgroundResource(R.drawable.button_border);
                    buttonWalk1.setTextColor(Color.WHITE);
                    buttonWalk2.setBackgroundResource(R.drawable.button_border);
                    buttonWalk2.setTextColor(Color.WHITE);
                    buttonWalk3.setBackgroundResource(R.drawable.button_border);
                    buttonWalk3.setTextColor(Color.WHITE);
                    if (nPaseos >= 4) {
                        buttonWalk4.setBackgroundResource(R.drawable.button_border);
                        buttonWalk4.setTextColor(Color.WHITE);
                    }
                    if (nPaseos >= 5) {
                        buttonWalk5.setBackgroundResource(R.drawable.button_border);
                        buttonWalk5.setTextColor(Color.WHITE);
                    }
                    buttonClean.setBackgroundResource(R.drawable.button_border_unclicked);
                    buttonClean.setTextColor(Color.BLACK);
                    buttonAll.setBackgroundResource(R.drawable.button_border);
                    buttonAll.setTextColor(Color.WHITE);
                    volunteer.walk1 = 1;
                    volunteer.walk2 = 1;
                    volunteer.walk3 = 1;
                    volunteer.walk4 = 1;
                    volunteer.walk5 = 1;
                    newConfigs.remove(position);
                    newConfigs.add(position,false);
                }
                else
                {
                    if(volunteer.clean == 1)
                    {
                        buttonClean.setBackgroundResource(R.drawable.button_border);
                        buttonClean.setTextColor(Color.WHITE);
                        buttonWalk1.setVisibility(View.INVISIBLE);
                        buttonWalk2.setVisibility(View.INVISIBLE);
                        buttonWalk3.setVisibility(View.INVISIBLE);
                        buttonWalk4.setVisibility(View.INVISIBLE);
                        buttonWalk5.setVisibility(View.INVISIBLE);
                        buttonAll.setVisibility(View.INVISIBLE);

                        buttonWalk1.setBackgroundResource(R.drawable.button_border_unclicked);
                        buttonWalk1.setTextColor(Color.BLACK);
                        buttonWalk2.setBackgroundResource(R.drawable.button_border_unclicked);
                        buttonWalk2.setTextColor(Color.BLACK);
                        buttonWalk3.setBackgroundResource(R.drawable.button_border_unclicked);
                        buttonWalk3.setTextColor(Color.BLACK);
                        buttonWalk4.setBackgroundResource(R.drawable.button_border_unclicked);
                        buttonWalk4.setTextColor(Color.BLACK);
                        buttonWalk5.setBackgroundResource(R.drawable.button_border_unclicked);
                        buttonWalk5.setTextColor(Color.BLACK);
                        buttonAll.setBackgroundResource(R.drawable.button_border_unclicked);
                        buttonAll.setTextColor(Color.BLACK);
                        volunteer.walk1 = 0;
                        volunteer.walk2 = 0;
                        volunteer.walk3 = 0;
                        volunteer.walk4 = 0;
                        volunteer.walk5 = 0;

                    }
                    else {
                        if (volunteer.walk1 == 1) {
                            buttonWalk1.setBackgroundResource(R.drawable.button_border);
                            buttonWalk1.setTextColor(Color.WHITE);
                        } else {
                            buttonWalk1.setBackgroundResource(R.drawable.button_border_unclicked);
                            buttonWalk1.setTextColor(Color.BLACK);
                        }

                        if (volunteer.walk2 == 1) {
                            buttonWalk2.setBackgroundResource(R.drawable.button_border);
                            buttonWalk2.setTextColor(Color.WHITE);
                        } else {
                            buttonWalk2.setBackgroundResource(R.drawable.button_border_unclicked);
                            buttonWalk2.setTextColor(Color.BLACK);
                        }

                        if (volunteer.walk3 == 1) {
                            buttonWalk3.setBackgroundResource(R.drawable.button_border);
                            buttonWalk3.setTextColor(Color.WHITE);
                        } else {
                            buttonWalk3.setBackgroundResource(R.drawable.button_border_unclicked);
                            buttonWalk3.setTextColor(Color.BLACK);
                        }

                        if (volunteer.walk4 == 1) {
                            buttonWalk4.setBackgroundResource(R.drawable.button_border);
                            buttonWalk4.setTextColor(Color.WHITE);
                        } else {
                            buttonWalk4.setBackgroundResource(R.drawable.button_border_unclicked);
                            buttonWalk4.setTextColor(Color.BLACK);
                        }

                        if (volunteer.walk5 == 1) {
                            buttonWalk5.setBackgroundResource(R.drawable.button_border);
                            buttonWalk5.setTextColor(Color.WHITE);
                        } else {
                            buttonWalk5.setBackgroundResource(R.drawable.button_border_unclicked);
                            buttonWalk5.setTextColor(Color.BLACK);
                        }

                        buttonClean.setBackgroundResource(R.drawable.button_border_unclicked);
                        buttonClean.setTextColor(Color.BLACK);
                        buttonAll.setBackgroundResource(R.drawable.button_border_unclicked);
                        buttonAll.setTextColor(Color.BLACK);
                    }
                }
            }

            if(volunteer.walk1 == 0 || volunteer.walk2 == 0 || volunteer.walk3 == 0 || (this.nPaseos >=4 && volunteer.walk4 == 0) || (this.nPaseos >= 5 && volunteer.walk5 == 0))
            {
                buttonAll.setBackgroundResource(R.drawable.button_border_unclicked);
                buttonAll.setTextColor(Color.BLACK);
            }

            if(volunteer.walk1 == 1 && volunteer.walk2 == 1 && volunteer.walk3 == 1)
            {
                if(this.nPaseos >=4 && volunteer.walk4 == 1)
                {
                    if(this.nPaseos >= 5 && volunteer.walk5 == 1)
                    {
                        buttonAll.setBackgroundResource(R.drawable.button_border);
                        buttonAll.setTextColor(Color.WHITE);
                    }

                    if(this.nPaseos < 5)
                    {
                        buttonAll.setBackgroundResource(R.drawable.button_border);
                        buttonAll.setTextColor(Color.WHITE);
                    }
                }

                if(this.nPaseos < 4) {
                    buttonAll.setBackgroundResource(R.drawable.button_border);
                    buttonAll.setTextColor(Color.WHITE);
                }
            }

            return gridViewAndroid;
        }
    }

    public class ChangeVolunteerWalkOnClickListener implements OnClickListener {
        int position;

        public ChangeVolunteerWalkOnClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            VolunteerWalks volunteer = volunteerWalksAdapter.matrixList.get(position);
            Object tag = v.getTag();
            Button button = (Button) v;
            if(v.getTag().equals("button_clean"))
            {
                if(volunteer.clean == 0)
                {
                    volunteer.clean = 1;
                    v.setBackgroundResource(R.drawable.button_border);
                    button.setTextColor(Color.WHITE);
                    volunteerWalksAdapter.getView(position, (View) v.getParent(), null, 2);
                }
                else
                {
                    volunteer.clean = 0;
                    v.setBackgroundResource(R.drawable.button_border_unclicked);
                    button.setTextColor(Color.BLACK);
                    volunteerWalksAdapter.getView(position, (View) v.getParent(), null, 3);
                }
            }
            else if(v.getTag().equals("button_walk1"))
            {
                if(volunteer.walk1 == 0)
                {
                    volunteer.walk1 = 1;
                    v.setBackgroundResource(R.drawable.button_border);
                    button.setTextColor(Color.WHITE);
                    volunteerWalksAdapter.getView(position, (View) v.getParent(), null, 4);
                }
                else
                {
                    volunteer.walk1 = 0;
                    v.setBackgroundResource(R.drawable.button_border_unclicked);
                    button.setTextColor(Color.BLACK);
                    volunteerWalksAdapter.getView(position, (View) v.getParent(), null, 4);
                }
            }
            else if(v.getTag().equals("button_walk2"))
            {
                if(volunteer.walk2 == 0)
                {
                    volunteer.walk2 = 1;
                    v.setBackgroundResource(R.drawable.button_border);
                    button.setTextColor(Color.WHITE);
                    volunteerWalksAdapter.getView(position, (View) v.getParent(), null, 4);
                }
                else
                {
                    volunteer.walk2 = 0;
                    v.setBackgroundResource(R.drawable.button_border_unclicked);
                    button.setTextColor(Color.BLACK);
                    volunteerWalksAdapter.getView(position, (View) v.getParent(), null, 4);
                }
            }
            else if(v.getTag().equals("button_walk3"))
            {
                if(volunteer.walk3 == 0)
                {
                    volunteer.walk3 = 1;
                    v.setBackgroundResource(R.drawable.button_border);
                    button.setTextColor(Color.WHITE);
                    volunteerWalksAdapter.getView(position, (View) v.getParent(), null, 4);
                }
                else
                {
                    volunteer.walk3 = 0;
                    v.setBackgroundResource(R.drawable.button_border_unclicked);
                    button.setTextColor(Color.BLACK);
                    volunteerWalksAdapter.getView(position, (View) v.getParent(), null, 4);
                }
            }
            else if(v.getTag().equals("button_walk4"))
            {
                if(volunteer.walk4 == 0)
                {
                    volunteer.walk4 = 1;
                    v.setBackgroundResource(R.drawable.button_border);
                    button.setTextColor(Color.WHITE);
                    volunteerWalksAdapter.getView(position, (View) v.getParent(), null, 4);
                }
                else
                {
                    volunteer.walk4 = 0;
                    v.setBackgroundResource(R.drawable.button_border_unclicked);
                    button.setTextColor(Color.BLACK);
                    volunteerWalksAdapter.getView(position, (View) v.getParent(), null, 4);
                }
            }
            else if(v.getTag().equals("button_walk5"))
            {
                if(volunteer.walk5 == 0)
                {
                    volunteer.walk5 = 1;
                    v.setBackgroundResource(R.drawable.button_border);
                    button.setTextColor(Color.WHITE);
                    volunteerWalksAdapter.getView(position, (View) v.getParent(), null, 4);
                }
                else
                {
                    volunteer.walk5 = 0;
                    v.setBackgroundResource(R.drawable.button_border_unclicked);
                    button.setTextColor(Color.BLACK);
                    volunteerWalksAdapter.getView(position, (View) v.getParent(), null, 4);
                }
            }
            else if(v.getTag().equals("button_all")) {
                boolean allClicked = true;
                if(volunteer.walk1 == 0)
                {
                    allClicked = false;
                }
                if(volunteer.walk2 == 0)
                {
                    allClicked = false;
                }
                if(volunteer.walk3 == 0)
                {
                    allClicked = false;
                }
                if(nPaseos >= 4 && volunteer.walk4 == 0)
                {
                    allClicked = false;
                }
                if(nPaseos >= 5 && volunteer.walk5 == 0)
                {
                    allClicked = false;
                }
                if(allClicked)
                {
                    volunteerWalksAdapter.getView(position, (View) v.getParent(), null, 0);
                    v.setBackgroundResource(R.drawable.button_border_unclicked);
                    button.setTextColor(Color.BLACK);
                    volunteer.walk1 = 0;
                    volunteer.walk2 = 0;
                    volunteer.walk3 = 0;
                    volunteer.walk4 = 0;
                    volunteer.walk5 = 0;
                }
                else
                {
                    volunteerWalksAdapter.getView(position, (View) v.getParent(), null, 1);
                    v.setBackgroundResource(R.drawable.button_border);
                    button.setTextColor(Color.WHITE);
                    volunteer.walk1 = 1;
                    volunteer.walk2 = 1;
                    volunteer.walk3 = 1;
                    volunteer.walk4 = 1;
                    volunteer.walk5 = 1;
                }
            }
        }
    }

    public void SetAllSelected(ArrayList<VolunteerWalks> volunteersList)
    {
        for(int i = 0; i < volunteersList.size(); ++i)
        {
            VolunteerWalks volunteer = volunteersList.get(i);
            volunteer.walk1 = 1;
            volunteer.walk2 = 1;
            volunteer.walk3 = 1;
            volunteer.walk4 = 1;
            volunteer.walk5 = 1;
            volunteer.nPaseos = 4;
        }
    }
}
