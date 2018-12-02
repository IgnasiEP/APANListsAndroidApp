package com.example.ignasi94.backtrackingsimple;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnClickListener;

import com.example.ignasi94.backtrackingsimple.BBDD.DBAdapter;
import com.example.ignasi94.backtrackingsimple.Estructuras.Volunteer;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerWalks;

import java.util.ArrayList;

public class ConfigureWalks extends Activity {

    DBAdapter dbAdapter;
    ArrayList<VolunteerWalks> volunteerWalks;
    VolunteerWalksAdapter volunteerWalksAdapter;
    GridView volunteersGrid;
    int nPaseos;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_walks);
        dbAdapter = new DBAdapter(this);
        volunteerWalks = dbAdapter.getAllSelectedVolunteers();

        volunteersGrid = (GridView) findViewById(R.id.grid_volunteers);
        volunteersGrid.setNumColumns(1);
        // Crear Adapter
        volunteerWalksAdapter = new VolunteerWalksAdapter(getApplicationContext(), volunteerWalks, 3);
        // Relacionar el adapter a la grid
        volunteersGrid.setAdapter(volunteerWalksAdapter);
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
                if(nPaseos == 3) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    gridViewAndroid = inflater.inflate(R.layout.griditem_configure_walks3, null);
                }
                else if(nPaseos == 7)
                {

                }
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
            Button buttonWalk4 = null;
            if(nPaseos >= 4) {
                buttonWalk4 = (Button) gridViewAndroid.findViewById(R.id.button_walk4);
                buttonWalk4.setOnClickListener(new ChangeVolunteerWalkOnClickListener(position));
            }
            Button buttonWalk5 = null;
            if(nPaseos >= 5) {
                buttonWalk5 = (Button) gridViewAndroid.findViewById(R.id.button_walk5);
                buttonWalk5.setOnClickListener(new ChangeVolunteerWalkOnClickListener(position));
            }
            Button buttonWalk6 = null;
            if(nPaseos >= 6) {
                buttonWalk6 = (Button) gridViewAndroid.findViewById(R.id.button_walk6);
                buttonWalk6.setOnClickListener(new ChangeVolunteerWalkOnClickListener(position));
            }
            Button buttonWalk7 = null;
            if(nPaseos >= 7) {
                buttonWalk7 = (Button) gridViewAndroid.findViewById(R.id.button_walk7);
                buttonWalk7.setOnClickListener(new ChangeVolunteerWalkOnClickListener(position));
            }
            Button buttonAll = (Button) gridViewAndroid.findViewById(R.id.button_all);
            buttonAll.setOnClickListener(new ChangeVolunteerWalkOnClickListener(position));

            textViewAndroid.setText(this.matrixList.get(position).name);
            imageViewAndroid.setImageResource(R.mipmap.ic_volunteer_default);

            if(allSelected == 0)
            {
                buttonWalk1.setBackgroundColor(Color.LTGRAY);
                buttonWalk2.setBackgroundColor(Color.LTGRAY);
                buttonWalk3.setBackgroundColor(Color.LTGRAY);
                if(nPaseos >= 4) {
                    buttonWalk4.setBackgroundColor(Color.LTGRAY);
                }
                if(nPaseos >= 5) {
                    buttonWalk5.setBackgroundColor(Color.LTGRAY);
                }
                if(nPaseos >= 6)
                {
                    buttonWalk6.setBackgroundColor(Color.LTGRAY);
                }
                if(nPaseos >= 7) {
                    buttonWalk7.setBackgroundColor(Color.LTGRAY);
                }
            }
            else if(allSelected == 1)
            {
                buttonWalk1.setBackgroundColor(Color.GREEN);
                buttonWalk2.setBackgroundColor(Color.GREEN);
                buttonWalk3.setBackgroundColor(Color.GREEN);
                if(nPaseos >= 4) {
                    buttonWalk4.setBackgroundColor(Color.GREEN);
                }
                if(nPaseos >= 5) {
                    buttonWalk5.setBackgroundColor(Color.GREEN);
                }
                if(nPaseos >= 6)
                {
                    buttonWalk6.setBackgroundColor(Color.GREEN);
                }
                if(nPaseos >= 7) {
                    buttonWalk7.setBackgroundColor(Color.GREEN);
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
            if(v.getTag().equals("button_clean"))
            {
                if(volunteer.clean == 0)
                {
                    volunteer.clean = 1;
                    v.setBackgroundColor(Color.GREEN);
                }
                else
                {
                    volunteer.clean = 0;
                    v.setBackgroundColor(Color.LTGRAY);
                }
            }
            else if(v.getTag().equals("button_walk1"))
            {
                if(volunteer.walk1 == 0)
                {
                    volunteer.walk1 = 1;
                    v.setBackgroundColor(Color.GREEN);
                }
                else
                {
                    volunteer.walk1 = 0;
                    v.setBackgroundColor(Color.LTGRAY);
                }
            }
            else if(v.getTag().equals("button_walk2"))
            {
                if(volunteer.walk2 == 0)
                {
                    volunteer.walk2 = 1;
                    v.setBackgroundColor(Color.GREEN);
                }
                else
                {
                    volunteer.walk2 = 0;
                    v.setBackgroundColor(Color.LTGRAY);
                }
            }
            else if(v.getTag().equals("button_walk3"))
            {
                if(volunteer.walk3 == 0)
                {
                    volunteer.walk3 = 1;
                    v.setBackgroundColor(Color.GREEN);
                }
                else
                {
                    volunteer.walk3 = 0;
                    v.setBackgroundColor(Color.LTGRAY);
                }
            }
            else if(v.getTag().equals("button_walk4"))
            {
                if(volunteer.walk4 == 0)
                {
                    volunteer.walk4 = 1;
                    v.setBackgroundColor(Color.GREEN);
                }
                else
                {
                    volunteer.walk4 = 0;
                    v.setBackgroundColor(Color.LTGRAY);
                }
            }
            else if(v.getTag().equals("button_walk5"))
            {
                if(volunteer.walk5 == 0)
                {
                    volunteer.walk5 = 1;
                    v.setBackgroundColor(Color.GREEN);
                }
                else
                {
                    volunteer.walk5 = 0;
                    v.setBackgroundColor(Color.LTGRAY);
                }
            }
            else if(v.getTag().equals("button_walk6"))
            {
                if(volunteer.walk6 == 0)
                {
                    volunteer.walk6 = 1;
                    v.setBackgroundColor(Color.GREEN);
                }
                else
                {
                    volunteer.walk6 = 0;
                    v.setBackgroundColor(Color.LTGRAY);
                }
            }
            else if(v.getTag().equals("button_walk7"))
            {
                if(volunteer.walk7 == 0)
                {
                    volunteer.walk7 = 1;
                    v.setBackgroundColor(Color.GREEN);
                }
                else
                {
                    volunteer.walk7 = 0;
                    v.setBackgroundColor(Color.LTGRAY);
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
                if(nPaseos >= 6 && volunteer.walk6 == 0)
                {
                    allClicked = false;
                }
                if(nPaseos >= 7 && volunteer.walk7 == 0)
                {
                    allClicked = false;
                }

                if(allClicked)
                {
                    volunteerWalksAdapter.getView(position, (View) v.getParent(), null, 0);
                    volunteer.walk1 = 0;
                    volunteer.walk2 = 0;
                    volunteer.walk3 = 0;
                    volunteer.walk4 = 0;
                    volunteer.walk5 = 0;
                    volunteer.walk6 = 0;
                    volunteer.walk7 = 0;
                }
                else
                {
                    volunteerWalksAdapter.getView(position, (View) v.getParent(), null, 1);
                    volunteer.walk1 = 1;
                    volunteer.walk2 = 1;
                    volunteer.walk3 = 1;
                    volunteer.walk4 = 1;
                    volunteer.walk5 = 1;
                    volunteer.walk6 = 1;
                    volunteer.walk7 = 1;
                }
            }
        }
    }
}
