package com.example.ignasi94.backtrackingsimple;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ignasi94.backtrackingsimple.BBDD.DBAdapter;
import com.example.ignasi94.backtrackingsimple.Estructuras.Dog;
import com.example.ignasi94.backtrackingsimple.Estructuras.Volunteer;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerDog;
import com.example.ignasi94.backtrackingsimple.Utils.Constants;
import android.widget.AdapterView.OnItemLongClickListener;

import java.util.ArrayList;
import java.util.Dictionary;

public class EditCleanSolution extends Activity {

    Integer nPaseos;
    Integer nVolunteers;
    Integer gridColumns;
    Integer dogsUnassignedColumns;
    ArrayList<Volunteer> volunteers;
    ArrayList<ArrayList<VolunteerDog>> cleanSolution;
    DBAdapter dbAdapter;
    ArrayList<VolunteerDog> cleanGridArray;
    DogAdapter cleanDogAdapter;
    DogAdapter dogAdapterUnassigned;
    Integer draggedIndex;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_clean_solution);
        gridColumns = 5;
        this.ReadMakeListsParameters(getIntent());

        GridView dogGrid = (GridView) findViewById(R.id.grid_clean_dogs);
        dogGrid.setNumColumns(gridColumns);
        // Crear Adapter
        cleanDogAdapter = new DogAdapter(getApplicationContext(), this.cleanGridArray, false, gridColumns);
        // Relacionar el adapter a la grid
        dogGrid.setAdapter(cleanDogAdapter);
        dogGrid.setOnItemLongClickListener(new CleanSolutionTouchListener());

        // DOGGRIDUNASSIGNED
        GridView dogGridUnassigned = (GridView) findViewById(R.id.grid_dogs_notassigned);
        dogsUnassignedColumns = 5;
        dogGridUnassigned.setNumColumns(dogsUnassignedColumns);
        // Adapter
        dogAdapterUnassigned = new DogAdapter(getApplicationContext(), new ArrayList<VolunteerDog>(), true, dogsUnassignedColumns);
        dogGridUnassigned.setAdapter(dogAdapterUnassigned);
        //Events
        dogGridUnassigned.setOnItemLongClickListener(new UnassignedDogsTouchListener());
        dogGridUnassigned.setOnDragListener(new DragListener());

    }

    public void ReadMakeListsParameters(Intent intent) {
        dbAdapter = new DBAdapter(this);
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
        }
    }

    public class DogAdapter extends BaseAdapter {
        Context context;
        ArrayList<VolunteerDog> matrixList;
        Integer columns;
        boolean onlyDogs;

        public DogAdapter(Context context, ArrayList<VolunteerDog> matrixList, boolean onlyDogs, Integer columns) {
            this.context = context;
            this.onlyDogs = onlyDogs;
            this.columns = columns;
            if(onlyDogs && (matrixList.size() % dogsUnassignedColumns) == 0)
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
            View gridViewAndroid = view;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                gridViewAndroid = inflater.inflate(R.layout.griditem_griditem_dogs, null);
            }
            gridViewAndroid.setTag(position);
            TextView textViewAndroid = (TextView) gridViewAndroid.findViewById(R.id.android_gridview_text);
            ImageView imageViewAndroid = (ImageView) gridViewAndroid.findViewById(R.id.android_gridview_image);
            VolunteerDog volunteerDog = this.matrixList.get(position);
            if(!onlyDogs && (position % gridColumns) == 0)
            {
                if(volunteerDog.visibility) {
                    imageViewAndroid.setImageResource(R.mipmap.ic_doggrid_empty_image);
                    textViewAndroid.setText(volunteerDog.cleanRow.toString());
                }
                else
                {
                    imageViewAndroid.setImageResource(R.mipmap.ic_doggrid_empty_image);
                    textViewAndroid.setText(volunteerDog.cleanRow.toString());
                }
            }
            else {
                textViewAndroid.setText(volunteerDog.dog.name);
                imageViewAndroid.setImageResource(R.mipmap.ic_dog_default);
            }
            gridViewAndroid.setOnDragListener(new DragListener());

            return gridViewAndroid;
        }
    }

    public class CleanSolutionTouchListener implements OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long arg3) {
            if((position % (gridColumns)) == 0 || cleanDogAdapter.matrixList.get(position).dog.name.isEmpty()) {
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
                        //origenAdapter = dogAdapter;
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
                        //destinoAdapter = dogAdapter;
                    }
                    else if(gridDestinoName.contentEquals("grid_dogs_notassigned"))
                    {
                        destinoAdapter = dogAdapterUnassigned;
                    }
                    else
                    {
                        destinoAdapter = cleanDogAdapter;
                    }

                    boolean canBeDrag = true;
                    Integer indexDestino = -1;
                    if(destino != null) {
                        indexDestino = (Integer) destino.getTag();
                        if (gridDestinoName.contentEquals("grid_dogs") && (indexDestino % (nPaseos + 1)) == 0) {
                            canBeDrag = false;
                        }
                        if (gridDestinoName.contentEquals("grid_clean_dogs") && (indexDestino % gridColumns) == 0) {
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
                            destinoAdapter.removeAt(indexDestino);
                            destinoAdapter.addAt(indexDestino, volunteerDogOrigen);
                            if(gridDestinoName.contentEquals("grid_clean_dogs")) {
                                Integer newIndexDestino = destinoAdapter.reajustElement(indexDestino);
                                destinoAdapter.reajustEmptyElement(draggedIndex);
                                if(destinoAdapter.isFullRow(newIndexDestino))
                                {
                                    destinoAdapter.addRow(newIndexDestino);
                                }
                                if(destinoAdapter.isSecondaryEmptyRow(draggedIndex))
                                {
                                    destinoAdapter.removeRow(draggedIndex);
                                }
                            }
                            origenAdapter.notifyDataSetChanged();
                            gridViewOrigen.invalidateViews();
                        } else {
                            //Si GridViewOrigen == DOGGRIGCLEAN y GridViewDestino == DOGGRIDUNASSIGNED
                            if (gridOrigenName.contentEquals("grid_clean_dogs") && gridDestinoName.contentEquals("grid_dogs_notassigned")) {
                                //Sustituir perro de la GridOrigen por un espacio vacío y actualizar gridvieworigen
                                VolunteerDog volunteerDog = (VolunteerDog) origenAdapter.getItem(draggedIndex).clone();
                                if (onDragAllGrid) {
                                    origenAdapter.removeAt(draggedIndex);
                                    Dog empty = new Dog(Constants.DEFAULT_DOG_NAME);
                                    VolunteerDog emptyVolunteerDog = new VolunteerDog(empty, null);
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

