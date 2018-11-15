package com.example.ignasi94.backtrackingsimple;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.ignasi94.backtrackingsimple.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

public class ShowSolution extends Activity {

    Integer nPaseos;
    Integer nVolunteers;
    Dog[][] walkSolution;
    ArrayList<Dog> walkSolutionArray;
    ArrayList<ArrayList<Integer>> cleanSolution;
    DBAdapter dbAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_solution);
        this.ReadMakeListsParameters(getIntent());
        // INITIALISE YOUR GRID
        GridView grid=(GridView)findViewById(R.id.grid);
        grid.setNumColumns(nPaseos);

        // CREATE AN ADAPTER  (MATRIX ADAPTER)
        MatricAdapter adapter=new MatricAdapter(getApplicationContext(),walkSolutionArray);

        // ATTACH THE ADAPTER TO GRID
        grid.setAdapter(adapter);

    }

    public void ReadMakeListsParameters(Intent intent)
    {
        dbAdapter = new DBAdapter(this);
        Dictionary<Integer,Dog>  dogs = dbAdapter.getAllDogsDictionary();
        nPaseos = intent.getIntExtra("nPaseos", 0);
        nVolunteers = intent.getIntExtra("nVolunteers", 0);
        walkSolution = new Dog[nVolunteers][nPaseos];
        walkSolutionArray = new ArrayList<Dog>();
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
            for(int j = 0; j < nPaseos; ++j)
            {
                walkSolutionArray.add(walkSolution[i][j]);
            }
        }

        cleanSolution = new ArrayList<ArrayList<Integer>>();
        for(int i = 0; i < nPaseos; ++i)
        {
            ArrayList<Integer> iArray = intent.getIntegerArrayListExtra("CleanSolution"+i);
            cleanSolution.add(i,iArray);
        }
    }

    public class MatricAdapter extends BaseAdapter {
        Context context;
        ArrayList<Dog> matrixList;

        public MatricAdapter(Context context, ArrayList<Dog> matrixList) {
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
                gridViewAndroid = inflater.inflate(R.layout.griditem_show_solution, null);
            }
            TextView textViewAndroid = (TextView) gridViewAndroid.findViewById(R.id.android_gridview_text);
            ImageView imageViewAndroid = (ImageView) gridViewAndroid.findViewById(R.id.android_gridview_image);
            textViewAndroid.setText(walkSolutionArray.get(position).name);
            imageViewAndroid.setImageResource(R.mipmap.ic_dog_default);

            return gridViewAndroid;
        }
    }


}