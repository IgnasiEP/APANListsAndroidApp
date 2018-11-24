package com.example.ignasi94.backtrackingsimple;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;
import android.view.View.OnTouchListener;
import android.view.View.DragShadowBuilder;
import android.view.View.OnLongClickListener;
import com.animoto.android.views.*;

import com.example.ignasi94.backtrackingsimple.BBDD.DBAdapter;
import com.example.ignasi94.backtrackingsimple.Estructuras.Dog;
import com.example.ignasi94.backtrackingsimple.Estructuras.Volunteer;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerDog;
import com.example.ignasi94.backtrackingsimple.Utils.Constants;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

public class EditSolution extends Activity {

    Integer nPaseos;
    Integer dogsUnassignedColumns;
    Integer nVolunteers;
    Integer draggedIndex;
    ArrayList<Volunteer> volunteers;
    Dog[][] walkSolution;
    ArrayList<VolunteerDog> walkSolutionArray;
    ArrayList<ArrayList<Integer>> cleanSolution;
    DBAdapter dbAdapter;
    DogAdapter dogAdapter;
    DogAdapter dogAdapterUnassigned;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_solution);
        this.ReadMakeListsParameters(getIntent());
        // DOGGRID
        GridView dogGrid = (GridView) findViewById(R.id.grid_dogs);
        dogGrid.setNumColumns(nPaseos+1);
        // Adapter
        dogAdapter = new DogAdapter(getApplicationContext(), walkSolutionArray, false);
        dogGrid.setAdapter(dogAdapter);
        // Events
        dogGrid.setOnItemLongClickListener(new WalkSolutionTouchListener());

        // DOGGRIDUNASSIGNED
        GridView dogGridUnassigned = (GridView) findViewById(R.id.grid_dogs_notassigned);
        dogsUnassignedColumns = 5;
        dogGridUnassigned.setNumColumns(dogsUnassignedColumns);
        // Adapter
        dogAdapterUnassigned = new DogAdapter(getApplicationContext(), new ArrayList<VolunteerDog>(), true);
        dogGridUnassigned.setAdapter(dogAdapterUnassigned);
        // Events
        dogGridUnassigned.setOnItemLongClickListener(new UnassignedDogsTouchListener());
        dogGridUnassigned.setOnDragListener(new DragListener());

        Button showEditClean = (Button) findViewById(R.id.button_limpieza);
        showEditClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchactivity= new Intent(EditSolution.this,EditCleanSolution   .class);
                launchactivity.putExtras(getIntent().getExtras());
                startActivity(launchactivity);
            }
        });

    }

    public void ReadMakeListsParameters(Intent intent) {
        dbAdapter = new DBAdapter(this);
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
        }

        cleanSolution = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < nPaseos; ++i) {
            ArrayList<Integer> iArray = intent.getIntegerArrayListExtra("CleanSolution" + i);
            cleanSolution.add(i, iArray);
        }
    }

    public class DogAdapter extends BaseAdapter {
        Context context;
        ArrayList<VolunteerDog> matrixList;
        boolean onlyDogs;

        public DogAdapter(Context context, ArrayList<VolunteerDog> matrixList, boolean onlyDogs) {
            this.context = context;
            this.onlyDogs = onlyDogs;
            if(onlyDogs && (matrixList.size() % dogsUnassignedColumns) == 0)
            {
                VolunteerDog volunteerDog = new VolunteerDog(new Dog(Constants.DEFAULT_DOG_NAME), null);
                matrixList.add(volunteerDog);
            }
            this.matrixList = matrixList;

        }

        @Override
        public int getCount() {
            return this.matrixList.size();
        }

        @Override
        public VolunteerDog getItem(int i) {
            return this.matrixList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public void removeAt(int i) {
            this.matrixList.remove(i);
        }

        public void add(VolunteerDog volunteerDog) {
            if(onlyDogs)
            {
                this.matrixList.add(this.matrixList.size()-1, volunteerDog);
            }
            else {
                this.matrixList.add(volunteerDog);
            }

        }

        public void addAt(int i, VolunteerDog volunteerDog) {
            if(onlyDogs) {
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

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            View gridViewAndroid = view;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                gridViewAndroid = inflater.inflate(R.layout.griditem_griditem_dogs, null);
            }

            gridViewAndroid.setTag(position);
            TextView textViewAndroid = (TextView) gridViewAndroid.findViewById(R.id.android_gridview_text);
            ImageView imageViewAndroid = (ImageView) gridViewAndroid.findViewById(R.id.android_gridview_image);

            if((position % (nPaseos+1)) == 0 && !onlyDogs)
            {
                VolunteerDog volunteerDog = matrixList.get(position);
                textViewAndroid.setText(volunteerDog.volunteer.name);
                imageViewAndroid.setImageResource(R.mipmap.ic_volunteer_default);
            }
            else {
                VolunteerDog volunteerDog = matrixList.get(position);
                textViewAndroid.setText(volunteerDog.dog.name);
                imageViewAndroid.setImageResource(R.mipmap.ic_dog_default);
                if(onlyDogs && volunteerDog.dog.name.isEmpty())
                {
                    imageViewAndroid.setImageResource(R.mipmap.ic_doggridunassigned_empty_image);
                }
                else if (!onlyDogs && volunteerDog.dog.name.isEmpty())
                {
                    imageViewAndroid.setImageResource(R.mipmap.ic_doggrid_empty_image);
                }
            }
            gridViewAndroid.setOnDragListener(new DragListener());

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

    public class DragListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    //v.setBackgroundColor(Color.parseColor("#aaaaaa"));
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    //v.setBackgroundColor(Color.parseColor("#aaaaaa"));
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
                    else
                    {
                        origenAdapter = dogAdapterUnassigned;
                    }

                    String gridDestinoName = gridViewDestino.getTag().toString();
                    if(gridDestinoName.contentEquals("grid_dogs"))
                    {
                        destinoAdapter = dogAdapter;
                    }
                    else
                    {
                        destinoAdapter = dogAdapterUnassigned;
                    }

                    boolean canBeDrag = true;
                    Integer indexDestino = -1;
                    if(destino != null) {
                        indexDestino = (Integer) destino.getTag();
                        if (gridDestinoName.contentEquals("grid_dogs") && (indexDestino % (nPaseos + 1)) == 0) {
                            canBeDrag = false;
                        }
                        if (gridOrigenName.contentEquals("grid_dogs_notassigned") && gridDestinoName.contentEquals("grid_dogs_notassigned")
                                && origenAdapter.getItem(indexDestino).dog.name.isEmpty()) {
                            canBeDrag = false;
                        }
                    }
                    if(canBeDrag) {
                        //Si gridview destino == destino inicial
                        if (gridViewOrigen == gridViewDestino) {
                            indexDestino = (Integer) destino.getTag();
                            VolunteerDog volunteerDogOrigen = (VolunteerDog) origenAdapter.getItem(draggedIndex).clone();
                            VolunteerDog volunteerDogDestino = (VolunteerDog) origenAdapter.getItem(indexDestino).clone();
                            origenAdapter.removeAt(draggedIndex);
                            origenAdapter.addAt(draggedIndex, volunteerDogDestino);
                            origenAdapter.removeAt(indexDestino);
                            origenAdapter.addAt(indexDestino, volunteerDogOrigen);
                            origenAdapter.notifyDataSetChanged();
                            gridViewOrigen.invalidateViews();
                        } else {
                            //Si GridViewOrigen == DOGGRIG y GridViewDestino == DOGGRIDUNASSIGNED
                            if (gridOrigenName.contentEquals("grid_dogs") && gridDestinoName.contentEquals("grid_dogs_notassigned")) {
                                //Sustituir perro de la GridOrigen por un espacio vacío y actualizar gridvieworigen
                                VolunteerDog volunteerDog = (VolunteerDog) dogAdapter.getItem(draggedIndex).clone();
                                if (onDragAllGrid) {
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
                                    origenAdapter.removeAt(draggedIndex);
                                    origenAdapter.addAt(draggedIndex, volunteerDogDestino);
                                    destinoAdapter.removeAt(indexDestino);
                                    destinoAdapter.addAt(indexDestino, volunteerDogOrigen);
                                    origenAdapter.notifyDataSetChanged();
                                    destinoAdapter.notifyDataSetChanged();
                                    gridViewOrigen.invalidateViews();
                                    gridViewDestino.invalidateViews();
                                }
                            } else {
                                indexDestino = (Integer) destino.getTag();
                                VolunteerDog volunteerDogOrigen = (VolunteerDog) origenAdapter.getItem(draggedIndex).clone();
                                VolunteerDog volunteerDogDestino = (VolunteerDog) destinoAdapter.getItem(indexDestino).clone();
                                origenAdapter.removeAt(draggedIndex);
                                if (!volunteerDogDestino.dog.name.isEmpty()) {
                                    origenAdapter.addAt(draggedIndex, volunteerDogDestino);
                                }

                                destinoAdapter.removeAt(indexDestino);
                                destinoAdapter.addAt(indexDestino, volunteerDogOrigen);
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
                    //v.setBackgroundColor(Color.GREEN);
                default:
                    break;
            }
            return true;
        }
    }
}

