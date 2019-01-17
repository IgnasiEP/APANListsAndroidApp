package com.example.ignasi94.backtrackingsimple.Screens.ListsScreens;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.support.constraint.Guideline;


import com.example.ignasi94.backtrackingsimple.BBDD.DBAdapter;
import com.example.ignasi94.backtrackingsimple.Estructuras.Cage;
import com.example.ignasi94.backtrackingsimple.Estructuras.Dog;
import com.example.ignasi94.backtrackingsimple.Estructuras.Volunteer;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerDog;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerWalks;
import com.example.ignasi94.backtrackingsimple.R;
import com.example.ignasi94.backtrackingsimple.Utils.Constants;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

public class EditSolution extends Activity {

    Integer nPaseos;
    Integer dogsUnassignedColumns;
    Integer nVolunteers;
    Integer draggedIndex;
    Integer cleanGridColumns;
    ArrayList<VolunteerWalks> volunteers;
    Dog[][] walkSolution;
    ArrayList<VolunteerDog> walkSolutionArray;
    ArrayList<ArrayList<VolunteerDog>> cleanSolution;
    ArrayList<VolunteerDog> cleanGridArray;
    ArrayList<VolunteerDog> noAssignedDogsArray;
    DBAdapter dbAdapter;
    DogAdapter dogAdapter;
    DogAdapter dogAdapterUnassigned;
    DogAdapter cleanDogAdapter;
    Button buttonShowPaseos;
    Button buttonShowClean;
    HorizontalScrollView gridView;
    HorizontalScrollView cleanGridView;
    View full;
    GridView dogGrid;
    GridView cleanGrid;
    Boolean lastSolution;

    ArrayList<ArrayList<Dog>> listDogsPerCage;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lists_activity_edit_solution);

        Button buttonShowNotAssigned = (Button) findViewById(R.id.button_show_no_assigned);
        Button buttonHideNotAssigned = (Button) findViewById(R.id.button_hide_notassigned);
        LinearLayout linearLayoutNotAssigned = (LinearLayout) findViewById(R.id.layoutnotassigned);
        LinearLayout linearLayoutAssigned = (LinearLayout) findViewById(R.id.layoutdogsassigned);
        TextView topText = (TextView) findViewById(R.id.textView_title);

        cleanGridColumns = 5;
        dogsUnassignedColumns = 5;

        this.ReadMakeListsParameters(getIntent());

        buttonShowPaseos = (Button) findViewById(R.id.button_paseos);
        buttonShowClean = (Button) findViewById(R.id.button_limpieza);
        gridView = findViewById(R.id.dogsview);
        cleanGridView = findViewById(R.id.dogscleanview);

        // DOGGRID
        dogGrid = (GridView) findViewById(R.id.grid_dogs);
        dogGrid.setNumColumns(nPaseos+1);
        // Adapter
        dogAdapter = new DogAdapter(getApplicationContext(), walkSolutionArray, Constants.GRID_DOGS, nPaseos+1);
        dogGrid.setAdapter(dogAdapter);
        // Events
        dogGrid.setOnItemLongClickListener(new WalkSolutionTouchListener());

        // DOGGRIDUNASSIGNED
        GridView dogGridUnassigned = (GridView) findViewById(R.id.grid_dogs_notassigned);
        dogGridUnassigned.setNumColumns(dogsUnassignedColumns);
        // Adapter
        dogAdapterUnassigned = new DogAdapter(getApplicationContext(), noAssignedDogsArray, Constants.GRID_DOGS_UNASSIGNED, dogsUnassignedColumns);
        dogGridUnassigned.setAdapter(dogAdapterUnassigned);
        // Events
        dogGridUnassigned.setOnItemLongClickListener(new UnassignedDogsTouchListener());
        dogGridUnassigned.setOnDragListener(new DragListener());

        //CLEANGRID
        cleanGrid = (GridView) findViewById(R.id.grid_clean_dogs);
        cleanGrid.setNumColumns(cleanGridColumns);
        // Crear Adapter
        cleanDogAdapter = new DogAdapter(getApplicationContext(), this.cleanGridArray, Constants.GRID_CLEAN, cleanGridColumns);
        //Si una fila está llena se añade una línea vacía para poder
        //seguir añadiendo elementos
        this.addEmptyRows();
        // Relacionar el adapter a la grid
        cleanGrid.setAdapter(cleanDogAdapter);
        cleanGrid.setOnItemLongClickListener(new CleanSolutionTouchListener());

        List<Dog> dogs = dbAdapter.getAllDogs();
        List<Cage> cages = dbAdapter.getAllCages();
        CreateListDogsPerCage(dogs, cages);

        buttonShowClean.setOnDragListener(new DragListener());
        buttonShowClean.setTag("button_limpieza");
        buttonShowClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridView.setVisibility(View.GONE);

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) cleanGridView.getLayoutParams();
                params.weight = 13;
                cleanGridView.setLayoutParams(params);

                buttonShowClean.setVisibility(View.GONE);
                buttonShowPaseos.setVisibility(View.VISIBLE);
            }
        });

        buttonShowPaseos.setOnDragListener(new DragListener());
        buttonShowPaseos.setTag("button_paseos");
        buttonShowPaseos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridView.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) gridView.getLayoutParams();
                if(buttonShowNotAssigned.getVisibility() == View.VISIBLE)
                {
                    params.weight = 16;
                }
                else
                {
                    params.weight = 23;
                }

                gridView.setLayoutParams(params);

                params = (LinearLayout.LayoutParams) cleanGridView.getLayoutParams();
                params.weight = 13;
                cleanGridView.setLayoutParams(params);

                buttonShowClean.setVisibility(View.VISIBLE);
                buttonShowPaseos.setVisibility(View.GONE);
            }
        });

        buttonHideNotAssigned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dogGridUnassigned.setVisibility(View.GONE);
                buttonHideNotAssigned.setVisibility(View.GONE);
                topText.setVisibility(View.GONE);
                buttonShowNotAssigned.setVisibility(View.VISIBLE);
                linearLayoutNotAssigned.setVisibility(View.GONE);

                Guideline guideLine12 = (Guideline) findViewById(R.id.guideline12);
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) guideLine12.getLayoutParams();
                params.guidePercent = 0.1f;
                guideLine12.setLayoutParams(params);

                float scale = getResources().getDisplayMetrics().density;
                int dpAsPixels = (int) (65*scale + 0.5f);
                dogGrid.setPadding(0,0,0, dpAsPixels);
                cleanGrid.setPadding(0,0,0, dpAsPixels);

                if(buttonShowClean.getVisibility() == View.VISIBLE) {
                    buttonShowClean.callOnClick();
                    buttonShowPaseos.callOnClick();
                }
            }
        });

        buttonShowNotAssigned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dogGridUnassigned.setVisibility(View.VISIBLE);
                buttonHideNotAssigned.setVisibility(View.VISIBLE);
                topText.setVisibility(View.VISIBLE);
                buttonShowNotAssigned.setVisibility(View.GONE);
                linearLayoutNotAssigned.setVisibility(View.VISIBLE);

                Guideline guideLine12 = (Guideline) findViewById(R.id.guideline12);
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) guideLine12.getLayoutParams();
                params.guidePercent = 0.4f;
                guideLine12.setLayoutParams(params);

                float scale = getResources().getDisplayMetrics().density;
                int dpAsPixels = (int) (30*scale + 0.5f);
                dogGrid.setPadding(0,0,0, dpAsPixels);
                cleanGrid.setPadding(0,0,0, dpAsPixels);

                if(buttonShowClean.getVisibility() == View.VISIBLE) {
                    buttonShowClean.callOnClick();
                    buttonShowPaseos.callOnClick();
                }

            }
        });

        Button saveButton = (Button) findViewById(R.id.button_guardar);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!lastSolution) {
                    dbAdapter.CleanSolutionsTables();
                    dbAdapter.SaveWalkSolution(dogAdapter.matrixList);
                    dbAdapter.SaveCleanSolution(cleanDogAdapter.matrixList);
                }
                dbAdapter.CleanSolutionsTablesLast();
                dbAdapter.SaveWalkSolutionLast(dogAdapter.matrixList);
                dbAdapter.SaveCleanSolutionLast(cleanDogAdapter.matrixList);
                finish();
            }
        });


        buttonShowClean.callOnClick();
        buttonHideNotAssigned.callOnClick();
        buttonShowPaseos.callOnClick();
    }

    public void ReadMakeListsParameters(Intent intent) {
        dbAdapter = new DBAdapter(this);
        nPaseos = getIntent().getIntExtra("nPaseos", 0);
        lastSolution = getIntent().getBooleanExtra("LAST", false);

        List<Dog> allDogs = new ArrayList<Dog>();
        noAssignedDogsArray = new ArrayList<VolunteerDog>();
        if(lastSolution)
        {
            volunteers = (ArrayList) dbAdapter.getAllSelectedVolunteersLast();
            nVolunteers = volunteers.size();
            walkSolutionArray = dbAdapter.GetWalkSolutionLast(nVolunteers, nPaseos + 1);
            cleanGridArray = dbAdapter.GetCleanSolutionLast(nPaseos, cleanGridColumns);
            allDogs = dbAdapter.getAllDogsLast();
        }
        else {
            volunteers = (ArrayList) dbAdapter.getAllSelectedVolunteers();
            nVolunteers = volunteers.size();
            walkSolutionArray = dbAdapter.GetWalkSolution(nVolunteers, nPaseos + 1);
            cleanGridArray = dbAdapter.GetCleanSolution(nPaseos, cleanGridColumns);
            allDogs = dbAdapter.getAllDogs();
        }

        for(int i = 0; i < allDogs.size(); ++i)
        {
            Dog dog = allDogs.get(i);
            boolean add = true;
            for(int j = 0; j < walkSolutionArray.size(); ++j)
            {
                VolunteerDog vDog = walkSolutionArray.get(j);
                if(vDog.dog != null && vDog.dog.id == dog.id)
                {
                    add = false;
                    break;
                }
            }
            for(int j = 0; j < cleanGridArray.size(); ++j)
            {
                VolunteerDog vDog = cleanGridArray.get(j);
                if(vDog.dog != null && vDog.dog.id == dog.id)
                {
                    add = false;
                    break;
                }
            }
            if(add) {
                noAssignedDogsArray.add(new VolunteerDog(dog, null));
            }
        }

        int rows = cleanGridArray.size()/cleanGridColumns;

        /*dbAdapter = new DBAdapter(this);
        Dictionary<Integer, Dog> dogs = dbAdapter.getAllDogsDictionary();
        volunteers = (ArrayList) dbAdapter.getAllVolunteers();
        nPaseos = intent.getIntExtra("nPaseos", 0);
        nVolunteers = intent.getIntExtra("nVolunteers", 0);
        walkSolution = new Dog[nVolunteers][nPaseos];
        walkSolutionArray = new ArrayList<VolunteerDog>();
        for (int i = 0; i < nPaseos; ++i) {
            ArrayList<Integer> iArray = intent.getIntegerArrayListExtra("WalkSolution" + i);
            for (int j = 0; j < iArray.size(); ++j) {
                walkSolution[j][i] = dogs.get(iArray.get(j));
            }

        }

        for (int i = 0; i < nVolunteers; ++i) {
            for (int j = 0; j < nPaseos + 1; ++j) {
                if (j == 0) {
                    walkSolutionArray.add(new VolunteerDog(null, volunteers.get(i)));
                } else {
                    walkSolutionArray.add(new VolunteerDog(walkSolution[i][j - 1], null));
                }
            }
        }*/
    }

    public void MakeCleanLists(Intent intent) {
        dbAdapter = new DBAdapter(this);
        Dictionary<Integer, Dog> dogs = dbAdapter.getAllDogsDictionary();
        volunteers = (ArrayList) dbAdapter.getAllSelectedVolunteers();
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
                if ((dogsToAdd % cleanGridColumns) == 0 && j == 0) {
                    cleanGridArray.add(new VolunteerDog(null, i + 1, true));
                    ++dogsToAdd;
                } else if ((dogsToAdd % cleanGridColumns) == 0) {
                    cleanGridArray.add(new VolunteerDog(null, i + 1, false));
                    ++dogsToAdd;
                }
                cleanGridArray.add(cleanSolution.get(i).get(j));
                ++dogsToAdd;
            }
            while((dogsToAdd % cleanGridColumns) != 0)
            {
                cleanGridArray.add(new VolunteerDog(new Dog(Constants.DEFAULT_DOG_NAME), null));
                ++dogsToAdd;
            }
        }
    }


    public class DogAdapter extends BaseAdapter {
        Context context;
        ArrayList<VolunteerDog> matrixList;
        Integer gridType;
        Integer columns;

        public DogAdapter(Context context, ArrayList<VolunteerDog> matrixList, Integer gridType, Integer columns) {
            this.context = context;
            this.gridType = gridType;
            this.columns = columns;
            if(gridType == Constants.GRID_DOGS_UNASSIGNED /*&& (matrixList.size() % dogsUnassignedColumns) == 0*/)
            {
                VolunteerDog volunteerDog = new VolunteerDog(new Dog(Constants.DEFAULT_DOG_NAME), null);
                matrixList.add(volunteerDog);
            }
            this.matrixList = matrixList;
        }

        @Override
        public int getCount() {
            return matrixList.size();
        }

        @Override
        public VolunteerDog getItem(int i) {
            return matrixList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public void removeAt(int i) {
            this.matrixList.remove(i);
        }

        public void add(VolunteerDog volunteerDog) {
            if(gridType == Constants.GRID_DOGS_UNASSIGNED)
            {
                this.matrixList.add(this.matrixList.size()-1, volunteerDog);
            }
            else {
                this.matrixList.add(volunteerDog);
            }

        }

        public void addAt(int i, VolunteerDog volunteerDog) {
            if(gridType == Constants.GRID_DOGS_UNASSIGNED) {
                if(i == this.matrixList.size()) {
                    this.matrixList.add(i, volunteerDog);
                    VolunteerDog emptyVDog = new VolunteerDog(new Dog(Constants.DEFAULT_DOG_NAME), null);
                    this.matrixList.add(emptyVDog);
                }
                else {
                    this.matrixList.add(i, volunteerDog);
                }
            }
            else
            {
                this.matrixList.add(i, volunteerDog);
            }
        }

        public boolean isSecondaryEmptyRow(Integer position)
        {
            Integer row = position/this.columns;
            Integer startIndex = (row+1)*this.columns;
            //No es la primera row del iPaseo -> se puede borrar
            //Todos los elementos de la row estan empty y el último de la row anterior
            //tambien
            if(startIndex >= this.matrixList.size())
            {
                return false;
            }
            if(this.matrixList.get(startIndex).visibility)
            {
                return false;
            }
            if(!this.matrixList.get(startIndex-1).dog.name.isEmpty())
            {
                return false;
            }
            for(int i = 1; i < this.columns; ++i)
            {
                if(!this.matrixList.get(startIndex+i).dog.name.isEmpty())
                {
                    return false;
                }
            }
            return true;
        }

        public void removeRow(Integer position)
        {
            Integer row = position/this.columns;
            Integer startIndex = (row+1)*this.columns;
            ArrayList<VolunteerDog> clone = (ArrayList<VolunteerDog>) this.matrixList.clone();
            for(int i = 0; i < this.columns; ++i)
            {
                clone.remove((int)startIndex);
            }
            this.matrixList = clone;
        }

        public Integer reajustElement(Integer position)
        {
            Integer tmpPosition = position -1;
            VolunteerDog volunteerDog = new VolunteerDog();
            boolean reajust = false;
            Integer lastEmptyPosition = 0;
            while(true)
            {
                volunteerDog = this.matrixList.get(tmpPosition);
                if(volunteerDog.dog == null && !volunteerDog.visibility)
                {
                    tmpPosition = tmpPosition - 1;
                }
                else if(volunteerDog.dog != null && volunteerDog.dog.name.isEmpty())
                {
                    lastEmptyPosition = tmpPosition;
                    tmpPosition = tmpPosition - 1;
                    reajust = true;
                }
                else
                {
                    tmpPosition = tmpPosition + 1;
                    if((tmpPosition % this.columns) == 0)
                    {
                        tmpPosition = tmpPosition + 1;
                    }
                    break;
                }
            }
            if(reajust)
            {
                volunteerDog = this.matrixList.get(position);
                VolunteerDog tmpVolunteerDog = this.matrixList.get(tmpPosition);
                this.matrixList.remove((int)position);
                this.matrixList.add(position, tmpVolunteerDog);
                this.matrixList.remove((int)tmpPosition);
                this.matrixList.add(tmpPosition, volunteerDog);
            }
            return tmpPosition;
        }

        public void reajustEmptyElement(Integer position)
        {
            Integer tmpPosition = position;
            VolunteerDog draggedVolunteerDog = this.matrixList.get(position);
            if(draggedVolunteerDog.dog.name.isEmpty()) {
                this.matrixList.remove((int) position);

                while (true) {
                    VolunteerDog volunteerDog = this.matrixList.get(tmpPosition);
                    if(tmpPosition == this.matrixList.size() - 1)
                    {
                        this.matrixList.add(new VolunteerDog(new Dog(Constants.DEFAULT_DOG_NAME), null));
                        break;
                    }
                    if(volunteerDog.dog == null && volunteerDog.visibility)
                    {
                        this.matrixList.add(tmpPosition, new VolunteerDog(new Dog(Constants.DEFAULT_DOG_NAME), null));
                        break;
                    }
                    if (volunteerDog.dog == null && !volunteerDog.visibility) {
                        VolunteerDog tmpVolunteerDog = this.matrixList.get(tmpPosition);
                        this.matrixList.remove((int) tmpPosition);
                        this.matrixList.add(tmpPosition + 1, tmpVolunteerDog);
                        tmpPosition = tmpPosition + 1;
                    } else if (volunteerDog.dog != null && volunteerDog.dog.name.isEmpty()) {
                        this.matrixList.add(tmpPosition, new VolunteerDog(new Dog(Constants.DEFAULT_DOG_NAME), null));
                        break;
                    }
                    tmpPosition = tmpPosition + 1;
                }
            }
        }

        public boolean isFullRow(Integer index)
        {
            Integer row = index/this.columns;
            Integer startIndex = row*this.columns;
            Integer nextRowIndex = (row+1)*this.columns;
            for(int i = 1; i < this.columns; ++i)
            {
                if(this.matrixList.get(startIndex+i).dog.name.isEmpty())
                {
                    return false;
                }
            }
            if(nextRowIndex < this.matrixList.size() && this.matrixList.get(startIndex).cleanRow == this.matrixList.get(nextRowIndex).cleanRow)
            {
                return false;
            }

            return true;
        }

        public void addRow(Integer index)
        {
            Integer row = index/this.columns;
            Integer startIndex = (row+1)*this.columns;
            Integer lastRow = row*this.columns;
            Integer numberPaseo = -1;
            if(lastRow < 0)
            {
                numberPaseo = 1;
            }
            else
            {
                numberPaseo = this.matrixList.get(lastRow).cleanRow;
            }
            for(int i = 0; i < this.columns; ++i)
            {
                if(i == 0)
                {
                    this.matrixList.add(startIndex+i, new VolunteerDog(null,numberPaseo, false));
                }
                else
                {
                    this.matrixList.add( startIndex + i, new VolunteerDog(new Dog(Constants.DEFAULT_DOG_NAME), null));
                }
            }
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            return this.getView(position, view, viewGroup, true);
        }

        public View getView(int position, View view, ViewGroup viewGroup, boolean events){
            View gridViewAndroid = view;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                gridViewAndroid = inflater.inflate(R.layout.lists_griditem_edit_dogs, null);
            }

            gridViewAndroid.setTag(position);
            AppCompatTextView textViewAndroid = (AppCompatTextView) gridViewAndroid.findViewById(R.id.android_gridview_text);
            //TextViewCompat.setAutoSizeTextTypeWithDefaults(textViewAndroid, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            textViewAndroid.setMaxLines(1);
            ImageView imageViewAndroid = (ImageView) gridViewAndroid.findViewById(R.id.android_gridview_image);
            ImageView imageViewAndroid2 = (ImageView) gridViewAndroid.findViewById(R.id.android_gridview_image2);

            VolunteerDog volunteerDog = this.matrixList.get(position);
            if(gridType == Constants.GRID_DOGS && (position % this.columns) == 0)
            {
                textViewAndroid.setText(volunteerDog.volunteer.name);
                imageViewAndroid.setImageResource(R.mipmap.ic_volunteer_default);
            }
            else if(gridType == Constants.GRID_CLEAN && (position % this.columns) == 0)
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
                textViewAndroid.setText(volunteerDog.dog.name);
                imageViewAndroid.setImageResource(R.mipmap.ic_dog_default);
                if(gridType == Constants.GRID_DOGS_UNASSIGNED && volunteerDog.dog.name.isEmpty())
                {
                    imageViewAndroid.setImageResource(R.mipmap.ic_white_dog);
                }
                else if (gridType == Constants.GRID_DOGS && volunteerDog.dog.name.isEmpty())
                {
                    imageViewAndroid.setImageResource(R.mipmap.ic_doggrid_empty_image);
                }
                else if (gridType == Constants.GRID_CLEAN && volunteerDog.dog.name.isEmpty())
                {
                    imageViewAndroid.setImageResource(R.mipmap.ic_white_dog);
                }
            }

            if(gridType != Constants.GRID_CAGE_DOGS_UNASSIGNED && (position % this.columns) != 0 && !volunteerDog.dog.name.isEmpty())
            {
                LinearLayout.LayoutParams loparams = (LinearLayout.LayoutParams) textViewAndroid.getLayoutParams();
                // Set only target params:
                loparams.weight = 7;
                textViewAndroid.setLayoutParams(loparams);
                textViewAndroid.setTextSize(13);

                if(volunteerDog.walksError && volunteerDog.specialError && volunteerDog.interiorError)
                {
                    imageViewAndroid2.setImageResource(R.mipmap.ic_error_triple);
                }
                else if(volunteerDog.walksError && volunteerDog.specialError)
                {
                    imageViewAndroid2.setImageResource(R.mipmap.ic_error_red_blue);
                }
                else if(volunteerDog.walksError && volunteerDog.interiorError)
                {
                    imageViewAndroid2.setImageResource(R.mipmap.ic_error_red_orange);
                }
                else if(volunteerDog.specialError && volunteerDog.interiorError)
                {
                    imageViewAndroid2.setImageResource(R.mipmap.ic_error_blue_orange);
                }
                else if(volunteerDog.walksError)
                {
                    imageViewAndroid2.setImageResource(R.mipmap.ic_error_red);
                }
                else if(volunteerDog.specialError)
                {
                    imageViewAndroid2.setImageResource(R.mipmap.ic_error_blue);
                }
                else if(volunteerDog.interiorError)
                {
                    imageViewAndroid2.setImageResource(R.mipmap.ic_error_orange);
                }
                else
                {
                    imageViewAndroid2.setImageResource(R.mipmap.ic_noerror);
                }
                imageViewAndroid2.setVisibility(View.VISIBLE);
            }
            else
            {
                LinearLayout.LayoutParams loparams = (LinearLayout.LayoutParams) textViewAndroid.getLayoutParams();
                // Set only target params:
                loparams.weight = 10;
                textViewAndroid.setLayoutParams(loparams);
                textViewAndroid.setTextSize(15);

                imageViewAndroid2.setVisibility(View.GONE);
            }

            if(events) {
                gridViewAndroid.setOnDragListener(new DragListener());
            }
            else
            {
                gridViewAndroid.setOnDragListener(null);
            }

            return gridViewAndroid;
        }
    }

    public class WalkSolutionTouchListener implements OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long arg3) {
            if((position % (nPaseos+1)) == 0 || dogAdapter.matrixList.get(position).dog.name.isEmpty()) {
               return false;
            }
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            view.startDrag(data, shadowBuilder, view, 0);
            draggedIndex = position;
            return true;
        }
    }

    public class UnassignedDogsTouchListener implements OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long arg3) {
            if(dogAdapterUnassigned.matrixList.get(position).dog.name.isEmpty()) {
                return false;
            }
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            view.startDrag(data, shadowBuilder, view, 0);
            draggedIndex = position;
            return true;
        }
    }

    public class CleanSolutionTouchListener implements OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long arg3) {
            if((position % (cleanGridColumns)) == 0 || cleanDogAdapter.matrixList.get(position).dog.name.isEmpty()) {
                return false;
            }
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            view.startDrag(data, shadowBuilder, view, 0);
            draggedIndex = position;
            return true;
        }
    }

    public class DragListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    if(buttonShowPaseos.getVisibility() == View.VISIBLE)
                    {
                        buttonShowPaseos.setBackgroundColor(Color.GREEN);
                    }
                    if(buttonShowClean.getVisibility() == View.VISIBLE)
                    {
                        buttonShowClean.setBackgroundColor(Color.GREEN);
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    if(v.getTag().toString().contentEquals("button_limpieza")) {
                        buttonShowClean.callOnClick();
                    }
                    else if(v.getTag().toString().contentEquals("button_paseos")) {
                        buttonShowPaseos.callOnClick();
                    }
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    //v.setBackgroundColor(Color.RED);
                    cleanGridView.setVisibility(View.GONE);
                    cleanGridView.setVisibility(View.VISIBLE);
                    /*View tmpView = (View) event.getLocalState();
                    //Elemento arrastrado
                    LinearLayout tmpOrigen = (LinearLayout) tmpView;
                    //GridView origen
                    GridView tmpGridViewOrigen = (GridView) tmpOrigen.getParent();
                    String tmpGridOrigenName = tmpGridViewOrigen.getTag().toString();

                    if(tmpGridOrigenName.contentEquals("grid_dogs") && gridView.getVisibility() == View.VISIBLE && !v.getTag().toString().contentEquals("button_limpieza"))
                    {
                        buttonShowPaseos.callOnClick();
                    }*/
                    break;
                case DragEvent.ACTION_DROP:
                    //Elemento arrastrado
                    View view = (View) event.getLocalState();
                    //Dejar de ver la vista donde estaba inicialmente
                    view.setVisibility(View.INVISIBLE);
                    boolean onDragAllGrid = false;

                    GridView gridViewOrigen = null;
                    DogAdapter origenAdapter = null;
                    LinearLayout origen = null;

                    GridView gridViewDestino = null;
                    DogAdapter destinoAdapter = null;
                    LinearLayout destino = null;

                    if(v.getTag().toString().contentEquals("button_limpieza") || v.getTag().toString().contentEquals("button_paseos"))
                    {
                        view.setVisibility(View.VISIBLE);
                        return true;
                    }
                    if(v.getTag().toString().contentEquals("grid_dogs_notassigned")) {
                        onDragAllGrid = true;
                        //GridViewDestino
                        gridViewDestino = (GridView) v;

                        //Elemento arrastrado
                        origen = (LinearLayout) view;
                        //GridView origen
                        gridViewOrigen = (GridView) origen.getParent();

                    }
                    else
                    {
                        //Elemento destino
                        destino = (LinearLayout) v;
                        //GridViewDestino
                        gridViewDestino = (GridView) destino.getParent();

                        //Elemento arrastrado
                        origen = (LinearLayout) view;
                        //GridView origen
                        gridViewOrigen = (GridView) origen.getParent();
                    }

                    String gridOrigenName = gridViewOrigen.getTag().toString();
                    if(gridOrigenName.contentEquals("grid_dogs"))
                    {
                        origenAdapter = dogAdapter;
                    }
                    else if(gridOrigenName.contentEquals("grid_dogs_notassigned"))
                    {
                        origenAdapter = dogAdapterUnassigned;
                    }
                    else
                    {
                        origenAdapter = cleanDogAdapter;
                    }

                    String gridDestinoName = gridViewDestino.getTag().toString();
                    if(gridDestinoName.contentEquals("grid_dogs"))
                    {
                        destinoAdapter = dogAdapter;
                    }
                    else if(gridDestinoName.contentEquals("grid_dogs_notassigned"))
                    {
                        destinoAdapter = dogAdapterUnassigned;
                    }
                    else
                    {
                        destinoAdapter = cleanDogAdapter;
                    }
                    boolean visible = gridViewOrigen.getVisibility() == View.VISIBLE;
                    boolean gone = gridViewOrigen.getVisibility() == View.GONE;
                    //if(origenAdapter == destinoAdapter && gridViewOrigen.getVisibility() == View.GONE)
                    //{
                    //    if(gridOrigenName.contentEquals("grid_dogs"))
                    //    {
                    //        gridViewDestino = (GridView) cleanGridView;
                    //        gridDestinoName = "grid_clean_dogs";
                    //        destinoAdapter = cleanDogAdapter;
                    //    }
                    //    else
                    //    {
                    //        gridViewDestino = (GridView) gridView;
                    //        gridDestinoName = "grid_dogs";
                    //        destinoAdapter = dogAdapter;
                    //    }
                    //}

                    boolean canBeDrag = true;
                    Integer indexDestino = -1;
                    if(destino != null) {
                        indexDestino = (Integer) destino.getTag();
                        if (gridDestinoName.contentEquals("grid_dogs") && (indexDestino % (nPaseos + 1)) == 0) {
                            canBeDrag = false;
                        }
                        if (gridDestinoName.contentEquals("grid_clean_dogs") && (indexDestino % cleanGridColumns) == 0) {
                            canBeDrag = false;
                        }
                        if (gridOrigenName.contentEquals("grid_dogs_notassigned") && gridDestinoName.contentEquals("grid_dogs_notassigned")
                                && origenAdapter.getItem(indexDestino).dog.name.isEmpty()) {
                            canBeDrag = false;
                        }
                    }
                    VolunteerDog dogDragged = null;
                    VolunteerDog dogMoved = null;
                    if(canBeDrag) {
                        //Si gridview destino == destino inicial
                        if (gridViewOrigen == gridViewDestino) {
                            if(destino != null) {
                                indexDestino = (Integer) destino.getTag();
                                VolunteerDog volunteerDogOrigen = (VolunteerDog) origenAdapter.getItem(draggedIndex).clone();
                                VolunteerDog volunteerDogDestino = (VolunteerDog) origenAdapter.getItem(indexDestino).clone();
                                dogDragged = origenAdapter.matrixList.get(draggedIndex);
                                origenAdapter.removeAt(draggedIndex);
                                origenAdapter.addAt(draggedIndex, volunteerDogDestino);
                                dogMoved = destinoAdapter.matrixList.get(indexDestino);
                                destinoAdapter.removeAt(indexDestino);
                                destinoAdapter.addAt(indexDestino, volunteerDogOrigen);
                                if (gridDestinoName.contentEquals("grid_clean_dogs")) {
                                    Integer newIndexDestino = destinoAdapter.reajustElement(indexDestino);
                                    destinoAdapter.reajustEmptyElement(draggedIndex);
                                    if (destinoAdapter.isFullRow(newIndexDestino)) {
                                        destinoAdapter.addRow(newIndexDestino);
                                    }
                                    if (destinoAdapter.isSecondaryEmptyRow(draggedIndex)) {
                                        destinoAdapter.removeRow(draggedIndex);
                                    }
                                }
                                origenAdapter.notifyDataSetChanged();
                                gridViewOrigen.invalidateViews();
                            }
                            else
                            {
                                VolunteerDog volunteerDogOrigen = (VolunteerDog) origenAdapter.getItem(draggedIndex).clone();
                                dogDragged = origenAdapter.matrixList.get(draggedIndex);
                                origenAdapter.removeAt(draggedIndex);
                                origenAdapter.add(volunteerDogOrigen);

                                origenAdapter.notifyDataSetChanged();
                                gridViewOrigen.invalidateViews();
                            }
                        } else {
                            //Si GridViewOrigen == DOGGRIG y GridViewDestino == DOGGRIDUNASSIGNED
                            if (gridOrigenName.contentEquals("grid_dogs") && gridDestinoName.contentEquals("grid_dogs_notassigned")) {
                                //Sustituir perro de la GridOrigen por un espacio vacío y actualizar gridvieworigen
                                VolunteerDog volunteerDog = (VolunteerDog) dogAdapter.getItem(draggedIndex).clone();
                                if (onDragAllGrid) {
                                    dogDragged = dogAdapter.matrixList.get(draggedIndex);
                                    dogAdapter.removeAt(draggedIndex);
                                    Dog empty = new Dog(Constants.DEFAULT_DOG_NAME);
                                    VolunteerDog emptyVolunteerDog = new VolunteerDog(empty, null);
                                    dogAdapter.addAt(draggedIndex, emptyVolunteerDog);
                                    dogAdapter.notifyDataSetChanged();
                                    gridViewOrigen.invalidateViews();

                                    //Añadir a la gridviewDestino el elemento arrastrado
                                    try {
                                        dogAdapterUnassigned.add(volunteerDog);
                                        int count = dogAdapterUnassigned.getCount();
                                        dogAdapterUnassigned.getView(count - 1, null, null);
                                        destinoAdapter.notifyDataSetChanged();
                                        gridViewDestino.invalidateViews();
                                    } catch (Exception e) {
                                        System.out.println(e);
                                    }
                                } else {
                                    indexDestino = (Integer) destino.getTag();
                                    VolunteerDog volunteerDogOrigen = (VolunteerDog) origenAdapter.getItem(draggedIndex).clone();
                                    VolunteerDog volunteerDogDestino = (VolunteerDog) destinoAdapter.getItem(indexDestino).clone();
                                    dogDragged = origenAdapter.matrixList.get(draggedIndex);
                                    origenAdapter.removeAt(draggedIndex);
                                    origenAdapter.addAt(draggedIndex, volunteerDogDestino);
                                    dogMoved = destinoAdapter.matrixList.get(indexDestino);
                                    destinoAdapter.removeAt(indexDestino);
                                    destinoAdapter.addAt(indexDestino, volunteerDogOrigen);
                                    origenAdapter.notifyDataSetChanged();
                                    destinoAdapter.notifyDataSetChanged();
                                    gridViewOrigen.invalidateViews();
                                    gridViewDestino.invalidateViews();
                                }
                            }
                            else if (gridOrigenName.contentEquals("grid_clean_dogs") && gridDestinoName.contentEquals("grid_dogs_notassigned")) {
                                //Sustituir perro de la GridOrigen por un espacio vacío y actualizar gridvieworigen
                                VolunteerDog volunteerDog = (VolunteerDog) origenAdapter.getItem(draggedIndex).clone();
                                if (onDragAllGrid) {
                                    origenAdapter.removeAt(draggedIndex);
                                    Dog empty = new Dog(Constants.DEFAULT_DOG_NAME);
                                    VolunteerDog emptyVolunteerDog = new VolunteerDog(empty, null);
                                    dogDragged = origenAdapter.matrixList.get(draggedIndex);
                                    origenAdapter.addAt(draggedIndex, emptyVolunteerDog);
                                    origenAdapter.reajustEmptyElement(draggedIndex);
                                    if(origenAdapter.isSecondaryEmptyRow(draggedIndex))
                                    {
                                        origenAdapter.removeRow(draggedIndex);
                                    }
                                    origenAdapter.notifyDataSetChanged();
                                    gridViewOrigen.invalidateViews();

                                    //Añadir a la gridviewDestino el elemento arrastrado
                                    try {
                                        destinoAdapter.add(volunteerDog);
                                        int count = destinoAdapter.getCount();
                                        destinoAdapter.getView(count - 1, null, null);
                                        destinoAdapter.notifyDataSetChanged();
                                        gridViewDestino.invalidateViews();
                                    } catch (Exception e) {
                                        System.out.println(e);
                                    }
                                } else {
                                    indexDestino = (Integer) destino.getTag();
                                    VolunteerDog volunteerDogOrigen = (VolunteerDog) origenAdapter.getItem(draggedIndex).clone();
                                    VolunteerDog volunteerDogDestino = (VolunteerDog) destinoAdapter.getItem(indexDestino).clone();
                                    dogDragged = origenAdapter.matrixList.get(draggedIndex);
                                    origenAdapter.removeAt(draggedIndex);
                                    origenAdapter.addAt(draggedIndex, volunteerDogDestino);
                                    dogMoved = destinoAdapter.matrixList.get(indexDestino);
                                    destinoAdapter.removeAt(indexDestino);
                                    destinoAdapter.addAt(indexDestino, volunteerDogOrigen);
                                    origenAdapter.notifyDataSetChanged();
                                    destinoAdapter.notifyDataSetChanged();
                                    gridViewOrigen.invalidateViews();
                                    gridViewDestino.invalidateViews();
                                }
                            }
                            else if(gridOrigenName.contentEquals("grid_dogs") && gridDestinoName.contentEquals("grid_clean_dogs"))
                            {
                                indexDestino = (Integer) destino.getTag();
                                VolunteerDog volunteerDogOrigen = (VolunteerDog) origenAdapter.getItem(draggedIndex).clone();
                                VolunteerDog volunteerDogDestino = (VolunteerDog) destinoAdapter.getItem(indexDestino).clone();
                                dogDragged = (VolunteerDog) volunteerDogOrigen.clone();
                                dogMoved = (VolunteerDog) volunteerDogDestino.clone();
                                origenAdapter.removeAt(draggedIndex);
                                origenAdapter.addAt(draggedIndex, volunteerDogDestino);
                                destinoAdapter.removeAt(indexDestino);
                                destinoAdapter.addAt(indexDestino, volunteerDogOrigen);
                                Integer newIndexDestino = destinoAdapter.reajustElement(indexDestino);
                                if(destinoAdapter.isFullRow(newIndexDestino))
                                {
                                    destinoAdapter.addRow(newIndexDestino);
                                }
                                origenAdapter.notifyDataSetChanged();
                                destinoAdapter.notifyDataSetChanged();
                                gridViewOrigen.invalidateViews();
                                gridViewDestino.invalidateViews();
                            }
                            else if(gridOrigenName.contentEquals("grid_clean_dogs") && gridDestinoName.contentEquals("grid_dogs"))
                            {
                                indexDestino = (Integer) destino.getTag();
                                VolunteerDog volunteerDogOrigen = (VolunteerDog) origenAdapter.getItem(draggedIndex).clone();
                                VolunteerDog volunteerDogDestino = (VolunteerDog) destinoAdapter.getItem(indexDestino).clone();
                                dogDragged = (VolunteerDog) volunteerDogOrigen.clone();
                                dogMoved = (VolunteerDog) volunteerDogDestino.clone();
                                origenAdapter.removeAt(draggedIndex);
                                origenAdapter.addAt(draggedIndex, volunteerDogDestino);
                                destinoAdapter.removeAt(indexDestino);
                                destinoAdapter.addAt(indexDestino, volunteerDogOrigen);
                                origenAdapter.reajustEmptyElement(draggedIndex);
                                if(origenAdapter.isSecondaryEmptyRow(draggedIndex))
                                {
                                    origenAdapter.removeRow(draggedIndex);
                                }
                                origenAdapter.notifyDataSetChanged();
                                destinoAdapter.notifyDataSetChanged();
                                gridViewOrigen.invalidateViews();
                                gridViewDestino.invalidateViews();
                            }
                            else {
                                indexDestino = (Integer) destino.getTag();
                                VolunteerDog volunteerDogOrigen = (VolunteerDog) origenAdapter.getItem(draggedIndex).clone();
                                VolunteerDog volunteerDogDestino = (VolunteerDog) destinoAdapter.getItem(indexDestino).clone();
                                dogDragged = (VolunteerDog) volunteerDogOrigen.clone();
                                dogMoved = (VolunteerDog) volunteerDogDestino.clone();
                                origenAdapter.removeAt(draggedIndex);
                                if (!volunteerDogDestino.dog.name.isEmpty()) {
                                    origenAdapter.addAt(draggedIndex, volunteerDogDestino);
                                }
                                destinoAdapter.removeAt(indexDestino);
                                destinoAdapter.addAt(indexDestino, volunteerDogOrigen);
                                if(gridDestinoName.contentEquals("grid_clean_dogs")) {
                                    Integer newIndexDestino = destinoAdapter.reajustElement(indexDestino);
                                    if(destinoAdapter.isFullRow(newIndexDestino))
                                    {
                                        destinoAdapter.addRow(newIndexDestino);
                                    }
                                }
                                destinoAdapter.notifyDataSetChanged();
                                origenAdapter.notifyDataSetChanged();
                                gridViewOrigen.invalidateViews();
                                gridViewDestino.invalidateViews();
                            }
                        }
                    }
                    origen.setVisibility(View.VISIBLE);
                    gridViewDestino.invalidate();
                    // if an item has already been dropped here, there will be a tag
                    Object tag = gridViewDestino.getTag();
                    CheckErrors(draggedIndex, indexDestino, dogDragged, dogMoved, origenAdapter, gridOrigenName, gridViewOrigen, destinoAdapter, gridDestinoName, gridViewDestino);
                    /*
                     * //if there is already an item here, set it back visible in
                     * its original place if(tag!=null) { //the tag is the view id
                     * already dropped here int existingID = (Integer)tag; //set the
                     * original view visible again
                     * findViewById(existingID).setVisibility(View.VISIBLE); } //set
                     * the tag in the target view being dropped on - to the ID of
                     * the view being dropped dropTarget.setTag(dropped.getId());
                     */
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    buttonShowClean.setBackgroundColor(Color.LTGRAY);
                    buttonShowPaseos.setBackgroundColor(Color.LTGRAY);
                default:
                    break;
            }
            return true;
        }
    }

    public void addEmptyRows()
    {
        for(int i = 0; i < cleanDogAdapter.matrixList.size(); i = i + cleanGridColumns)
        {
            if(cleanDogAdapter.isFullRow(i))
            {
                cleanDogAdapter.addRow(i);
            }
        }
    }

    public void CheckErrors(int origenPosition, int destinoPosition, VolunteerDog dogDragged, VolunteerDog dogMoved, DogAdapter origenAdapter, String gridOrigenName,
                            GridView gridViewOrigen, DogAdapter destinoAdapter, String gridDestinoName, GridView gridViewDestino)
    {
        if(gridOrigenName.equals("grid_dogs") && dogMoved != null && dogMoved.dog.id > 0)
        {
            if(dogMoved.dog.special)
            {
                VolunteerDog volunteer = origenAdapter.matrixList.get((origenPosition/origenAdapter.columns)* origenAdapter.columns);
                boolean containsDog = false;

                for(int i = 0; i < volunteer.volunteer.favouriteDogs.size(); ++i)
                {
                    if(volunteer.volunteer.favouriteDogs.get(i).name.equals(dogMoved.dog.name))
                    {
                        containsDog = true;
                        break;
                    }
                }

                for(int i = 0; i < dogAdapter.matrixList.size(); ++i)
                {
                    VolunteerDog volunteerDog = dogAdapter.matrixList.get(i);
                    if(volunteerDog.dog != null && volunteerDog.dog.id == dogMoved.dog.id)
                    {
                        if(containsDog) {
                            volunteerDog.specialError = false;
                        }
                        else
                        {
                            volunteerDog.specialError = true;
                        }

                    }
                }
            }

            if(dogMoved.dog.walktype != Constants.WT_EXTERIOR)
            {
                for(int i = 0; i < dogAdapter.matrixList.size(); ++i)
                {
                    VolunteerDog volunteerDog = dogAdapter.matrixList.get(i);
                    if(volunteerDog.dog != null && volunteerDog.dog.id == dogMoved.dog.id)
                    {
                        volunteerDog.interiorError = true;
                    }
                }
            }

            CheckIfCageDogsSameWalk(dogMoved.dog.idCage);
        }

        if(gridDestinoName.equals("grid_dogs"))
        {
            if(dogDragged.dog.special)
            {
                VolunteerDog volunteer = destinoAdapter.matrixList.get((destinoPosition/destinoAdapter.columns)*destinoAdapter.columns);
                boolean containsDog = false;

                for(int i = 0; i < volunteer.volunteer.favouriteDogs.size(); ++i)
                {
                    if(volunteer.volunteer.favouriteDogs.get(i).name.equals(dogDragged.dog.name))
                    {
                        containsDog = true;
                        break;
                    }
                }

                for(int i = 0; i < dogAdapter.matrixList.size(); ++i)
                {
                    VolunteerDog volunteerDog = dogAdapter.matrixList.get(i);
                    if(volunteerDog.dog != null && volunteerDog.dog.id == dogDragged.dog.id)
                    {
                        if(containsDog) {
                            volunteerDog.specialError = false;
                        }
                        else
                        {
                            volunteerDog.specialError = true;
                        }

                    }
                }
            }

            if(dogDragged.dog.walktype != Constants.WT_EXTERIOR)
            {
                for(int i = 0; i < dogAdapter.matrixList.size(); ++i)
                {
                    VolunteerDog volunteerDog = dogAdapter.matrixList.get(i);
                    if(volunteerDog.dog != null && volunteerDog.dog.id == dogDragged.dog.id)
                    {
                        volunteerDog.interiorError = true;
                    }
                }
            }

            CheckIfCageDogsSameWalk(dogDragged.dog.idCage);
        }

        if(gridOrigenName.equals("grid_clean_dogs") && dogMoved != null && dogMoved.dog.id > 0)
        {
            if(dogMoved.interiorError)
            {
                for(int i = 0; i < cleanDogAdapter.matrixList.size(); ++i)
                {
                    VolunteerDog volunteerDog = cleanDogAdapter.matrixList.get(i);
                    if(volunteerDog.dog != null && volunteerDog.dog.id == dogMoved.dog.id)
                    {
                        volunteerDog.interiorError = false;
                    }
                }
            }
            CheckIfCageDogsSameWalk(dogMoved.dog.idCage);
        }

        if(gridDestinoName.equals("grid_clean_dogs"))
        {
            if(dogDragged.interiorError)
            {
                for(int i = 0; i < cleanDogAdapter.matrixList.size(); ++i)
                {
                    VolunteerDog volunteerDog = cleanDogAdapter.matrixList.get(i);
                    if(volunteerDog.dog != null && volunteerDog.dog.id == dogDragged.dog.id)
                    {
                        volunteerDog.interiorError = false;
                    }
                }
            }
            CheckIfCageDogsSameWalk(dogDragged.dog.idCage);
        }

        if(gridDestinoName.equals("grid_dogs_notassigned")) {
            CheckIfCageDogsSameWalk(dogDragged.dog.idCage);
        }

        if(gridOrigenName.equals("grid_dogs_notassigned") && dogMoved != null && dogMoved.dog.id > 0) {
            CheckIfCageDogsSameWalk(dogMoved.dog.idCage);
        }

        destinoAdapter.notifyDataSetChanged();
        origenAdapter.notifyDataSetChanged();
        gridViewOrigen.invalidateViews();
        gridViewDestino.invalidateViews();
    }

    public void CheckIfCageDogsSameWalk(int idCage)
    {
        ArrayList<Dog> cageDogs = listDogsPerCage.get(idCage);

        ArrayList<Dog> exteriorCageDogs = GetExteriorDogs(idCage);

        ArrayList<Integer> walks = new ArrayList<Integer>();

        ArrayList<Integer> positions = new ArrayList<Integer>();
        ArrayList<Dog> dogsInTable = new ArrayList<Dog>();

        ArrayList<Integer> cleanPositions = new ArrayList<Integer>();
        ArrayList<Dog> dogsInCleanTable = new ArrayList<Dog>();
        int iPaseo = -1;

        for(int i = 0; i < dogAdapter.matrixList.size(); ++i)
        {
            VolunteerDog volunteerDog = dogAdapter.matrixList.get(i);
            if(volunteerDog.volunteer != null)
            {
                iPaseo = 0;
            }
            if(volunteerDog.dog != null && volunteerDog.dog.idCage == idCage)
            {
                dogsInTable.add(volunteerDog.dog);
                positions.add(i);
                if(!walks.contains(iPaseo))
                {
                    walks.add(iPaseo);
                }
            }
            iPaseo++;
        }

        iPaseo = 0;
        for(int i = 0; i < cleanDogAdapter.matrixList.size(); ++i)
        {
            VolunteerDog volunteerDog = cleanDogAdapter.matrixList.get(i);
            if((i % cleanDogAdapter.columns) == 0)
            {
                iPaseo++;
            }
            if(volunteerDog.dog != null && volunteerDog.dog.idCage == idCage)
            {
                dogsInCleanTable.add(volunteerDog.dog);
                cleanPositions.add(i);
                if(!walks.contains(iPaseo))
                {
                    walks.add(iPaseo);
                }
            }
        }

        boolean dogsInCageUnassigned = false;
        for(int i = 0; i < dogAdapterUnassigned.matrixList.size(); ++i)
        {
            VolunteerDog volunteerDog = dogAdapterUnassigned.matrixList.get(i);
            if(volunteerDog.dog != null && volunteerDog.dog.idCage == idCage && volunteerDog.dog.walktype != Constants.WT_NONE)
            {
                dogsInCageUnassigned = true;
            }
        }

        if(walks.size() > 1 || dogsInCageUnassigned)
        {
            for(int i = 0; i < positions.size(); ++i)
            {
                VolunteerDog volunteerDog = dogAdapter.matrixList.get(positions.get(i));
                if(volunteerDog.dog != null && volunteerDog.dog.idCage == idCage)
                {
                    volunteerDog.walksError = true;
                }
                dogAdapter.notifyDataSetChanged();
                dogGrid.invalidateViews();
            }

            for(int i = 0; i < cleanPositions.size(); ++i)
            {
                VolunteerDog volunteerDog = cleanDogAdapter.matrixList.get(cleanPositions.get(i));
                if(volunteerDog.dog != null && volunteerDog.dog.idCage == idCage)
                {
                    volunteerDog.walksError = true;
                }
                cleanDogAdapter.notifyDataSetChanged();
                cleanGrid.invalidateViews();
            }
        }
        else if(walks.size() == 1 || !dogsInCageUnassigned)
        {
            for(int i = 0; i < positions.size(); ++i)
            {
                VolunteerDog volunteerDog = dogAdapter.matrixList.get(positions.get(i));
                if(volunteerDog.dog != null && volunteerDog.dog.idCage == idCage)
                {
                    volunteerDog.walksError = false;
                }

                dogAdapter.notifyDataSetChanged();
                dogGrid.invalidateViews();
            }

            for(int i = 0; i < cleanPositions.size(); ++i)
            {
                VolunteerDog volunteerDog = cleanDogAdapter.matrixList.get(cleanPositions.get(i));
                if(volunteerDog.dog != null && volunteerDog.dog.idCage == idCage)
                {
                    volunteerDog.walksError = false;
                }
                cleanDogAdapter.notifyDataSetChanged();
                cleanGrid.invalidateViews();
            }
        }
    }

    public void CreateListDogsPerCage(List<Dog> dogs, List<Cage> cages)
    {
        listDogsPerCage = new ArrayList<ArrayList<Dog>>();
        listDogsPerCage.add(new ArrayList<Dog>());
        for(int i = 0; i < cages.size(); ++i)
        {
            ArrayList<Dog> listDogs = new ArrayList<Dog>();
            for(int j = 0; j < dogs.size(); ++j)
            {
                Dog dog = dogs.get(j);
                if(dog.idCage == cages.get(i).id)
                {
                    listDogs.add(dog);
                }
            }
            listDogsPerCage.add(listDogs);
        }
    }

    public ArrayList<Dog> GetExteriorDogs(int idCage)
    {
        ArrayList<Dog> dogs = new ArrayList<Dog>();
        int count = 0;
        for (int i = 0; i < this.listDogsPerCage.get(idCage).size(); ++i)
        {
            Dog dog = this.listDogsPerCage.get(idCage).get(i);
            if(dog.walktype == Constants.WT_EXTERIOR)
            {
                dogs.add(dog);
            }
        }
        return dogs;
    }
}


