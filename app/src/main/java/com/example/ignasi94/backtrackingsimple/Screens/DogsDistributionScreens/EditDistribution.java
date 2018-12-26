package com.example.ignasi94.backtrackingsimple.Screens.DogsDistributionScreens;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.example.ignasi94.backtrackingsimple.BBDD.DBAdapter;
import com.example.ignasi94.backtrackingsimple.Estructuras.Cage;
import com.example.ignasi94.backtrackingsimple.Estructuras.CageDog;
import com.example.ignasi94.backtrackingsimple.Estructuras.Dog;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerDog;
import com.example.ignasi94.backtrackingsimple.R;
import com.example.ignasi94.backtrackingsimple.Screens.ListsScreens.EditSolution;
import com.example.ignasi94.backtrackingsimple.Screens.ListsScreens.ShowSolution;
import com.example.ignasi94.backtrackingsimple.Utils.Constants;

import java.util.ArrayList;

public class EditDistribution extends Activity{

    Integer gridColumns;
    DBAdapter dbAdapter;
    HorizontalScrollView xenilesGridView;
    GridView xenilesCageDogGrid = null;
    ArrayList<CageDog> xenilesCageDogsArray;
    CageDogsAdapter xenilesCageDogsAdapter;

    HorizontalScrollView patiosGridView;
    GridView patiosCageDogGrid = null;
    ArrayList<CageDog> patiosCageDogsArray;
    CageDogsAdapter patiosCageDogsAdapter;

    HorizontalScrollView cuarentenasGridView;
    GridView cuarentenasCageDogGrid = null;
    ArrayList<CageDog> cuarentenasCageDogsArray;
    CageDogsAdapter cuarentenasCageDogsAdapter;

    HorizontalScrollView unassignedGridView;
    GridView unassignedCageDogGrid = null;
    ArrayList<CageDog> unassignedCageDogsArray;
    CageDogsAdapter unassignedCageDogsAdapter;

    String visibleZone = null;
    String lastVisibleZone = null;
    Button buttonLeft = null;
    Button buttonRight = null;
    TextView title = null;
    int draggedIndex;
    int dogsUnassignedColumns;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.distribution_activity_edit);
        gridColumns = 5;
        dogsUnassignedColumns = gridColumns;
        this.ReadMakeListsParameters(getIntent());

        xenilesGridView = (HorizontalScrollView) findViewById(R.id.xeniles_cagesview);
        patiosGridView = (HorizontalScrollView) findViewById(R.id.patios_cagesview);
        cuarentenasGridView = (HorizontalScrollView) findViewById(R.id.cuarentenas_cagesview);

        //XENILES
        xenilesCageDogGrid = (GridView) findViewById(R.id.grid_xeniles_cage_dogs);
        xenilesCageDogGrid.setNumColumns(gridColumns);
        // Crear Adapter
        xenilesCageDogsAdapter = new CageDogsAdapter(getApplicationContext(), this.xenilesCageDogsArray, Constants.GRID_CAGE_DOGS, gridColumns, true);
        // Relacionar el adapter a la grid
        xenilesCageDogGrid.setAdapter(xenilesCageDogsAdapter);
        xenilesCageDogGrid.setOnItemLongClickListener(new XenilesTouchListener());

        //PATIOS
        patiosCageDogGrid = (GridView) findViewById(R.id.grid_patios_cage_dogs);
        patiosCageDogGrid.setNumColumns(gridColumns);
        // Crear Adapter
        patiosCageDogsAdapter = new CageDogsAdapter(getApplicationContext(), this.patiosCageDogsArray, Constants.GRID_CAGE_DOGS, gridColumns, true);
        // Relacionar el adapter a la grid
        patiosCageDogGrid.setAdapter(patiosCageDogsAdapter);
        patiosCageDogGrid.setOnItemLongClickListener(new PatiosTouchListener());

        //CUARENTENAS
        cuarentenasCageDogGrid = (GridView) findViewById(R.id.grid_cuarentenas_cage_dogs);
        cuarentenasCageDogGrid.setNumColumns(gridColumns);
        // Crear Adapter
        cuarentenasCageDogsAdapter = new CageDogsAdapter(getApplicationContext(), this.cuarentenasCageDogsArray, Constants.GRID_CAGE_DOGS, gridColumns, true);
        // Relacionar el adapter a la grid
        cuarentenasCageDogGrid.setAdapter(cuarentenasCageDogsAdapter);
        cuarentenasCageDogGrid.setOnItemLongClickListener(new CuarentenasTouchListener());

        this.addEmptyRows();

        //UNASSIGNED
        unassignedCageDogGrid = (GridView) findViewById(R.id.grid_unassigned_cage_dogs);
        unassignedCageDogGrid.setNumColumns(gridColumns);
        // Crear Adapter
        unassignedCageDogsAdapter = new CageDogsAdapter(getApplicationContext(), this.unassignedCageDogsArray, Constants.GRID_CAGE_DOGS_UNASSIGNED, gridColumns,true);
        // Relacionar el adapter a la grid
        unassignedCageDogGrid.setAdapter(unassignedCageDogsAdapter);
        unassignedCageDogGrid.setOnItemLongClickListener(new UnassignedDogsTouchListener());
        unassignedCageDogGrid.setOnDragListener(new DragListener());

        //title = (TextView) findViewById(R.id.textView_title);
        buttonLeft = (Button) findViewById(R.id.button_left);
        buttonRight = (Button) findViewById(R.id.button_right);
        buttonLeft.setOnClickListener(new ChangeScreenVisibilityClickListener(true));
        buttonRight.setOnClickListener(new ChangeScreenVisibilityClickListener(false));

        lastVisibleZone = null;
        visibleZone = Constants.CAGE_ZONE_XENILES;
        buttonLeft.setText(Constants.CAGE_ZONE_PATIOS);
        buttonLeft.setOnDragListener(new DragListener());
        buttonRight.setText(Constants.CAGE_ZONE_CUARENTENAS);
        buttonRight.setOnDragListener(new DragListener());

        Button buttonSafe = (Button) findViewById(R.id.button_guardar);
        buttonSafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbAdapter.updateDogCages(xenilesCageDogsAdapter.matrixList, true);
                dbAdapter.updateDogCages(cuarentenasCageDogsAdapter.matrixList, true);
                dbAdapter.updateDogCages(patiosCageDogsAdapter.matrixList, true);
                dbAdapter.updateDogCages(unassignedCageDogsAdapter.matrixList, false);
                finish();
            }
        });
    }

    public void ReadMakeListsParameters(Intent intent) {
        dbAdapter = new DBAdapter(this);
        xenilesCageDogsArray = dbAdapter.getDogsPerCage(gridColumns, Constants.CAGE_ZONE_XENILES);
        patiosCageDogsArray = dbAdapter.getDogsPerCage(gridColumns, Constants.CAGE_ZONE_PATIOS);
        cuarentenasCageDogsArray = dbAdapter.getDogsPerCage(gridColumns, Constants.CAGE_ZONE_CUARENTENAS);
        unassignedCageDogsArray = dbAdapter.getUnassignedDogs();
    }

    public class CageDogsAdapter extends BaseAdapter {
        Context context;
        ArrayList<CageDog> matrixList;
        Integer gridType;
        Integer columns;
        boolean events;

        public CageDogsAdapter(Context context, ArrayList<CageDog> matrixList, Integer gridType, Integer columns, boolean events) {
            this.context = context;
            this.gridType = gridType;
            this.columns = columns;
            this.events = events;
            if(gridType.equals(Constants.GRID_CAGE_DOGS_UNASSIGNED) && (matrixList.size() % dogsUnassignedColumns) == 0)
            {
                CageDog cageDog = new CageDog(new Dog(Constants.DEFAULT_DOG_NAME), null);
                matrixList.add(cageDog);
            }
            this.matrixList = matrixList;
        }

        @Override
        public int getCount() {
            return matrixList.size();
        }

        @Override
        public CageDog getItem(int i) {
            return matrixList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public void removeAt(int i) {
            this.matrixList.remove(i);
        }

        public void add(CageDog cageDog) {
            if(gridType == Constants.GRID_CAGE_DOGS_UNASSIGNED)
            {
                this.matrixList.add(this.matrixList.size()-1, cageDog);
            }
            else {
                this.matrixList.add(cageDog);
            }

        }

        public void addAt(int i, CageDog cageDog) {
            if(gridType == Constants.GRID_CAGE_DOGS_UNASSIGNED) {
                if(i == this.matrixList.size()) {
                    this.matrixList.add(i, cageDog);
                    CageDog emptyCDog = new CageDog(new Dog(Constants.DEFAULT_DOG_NAME), null);
                    this.matrixList.add(emptyCDog);
                }
                else {
                    this.matrixList.add(i, cageDog);
                }
            }
            else
            {
                this.matrixList.add(i, cageDog);
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
            ArrayList<CageDog> clone = (ArrayList<CageDog>) this.matrixList.clone();
            for(int i = 0; i < this.columns; ++i)
            {
                clone.remove((int)startIndex);
            }
            this.matrixList = clone;
        }

        public Integer reajustElement(Integer position)
        {
            Integer tmpPosition = position -1;
            CageDog cageDog = new CageDog();
            boolean reajust = false;
            Integer lastEmptyPosition = 0;
            while(true)
            {
                cageDog = this.matrixList.get(tmpPosition);
                if(cageDog.dog == null && !cageDog.visibility)
                {
                    tmpPosition = tmpPosition - 1;
                }
                else if(cageDog.dog != null && cageDog.dog.name.isEmpty())
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
                cageDog = this.matrixList.get(position);
                CageDog tmpCageDog = this.matrixList.get(tmpPosition);
                this.matrixList.remove((int)position);
                this.matrixList.add(position, tmpCageDog);
                this.matrixList.remove((int)tmpPosition);
                this.matrixList.add(tmpPosition, tmpCageDog);
            }
            return tmpPosition;
        }

        public void reajustEmptyElement(Integer position)
        {
            Integer tmpPosition = position;
            CageDog draggedCageDog = this.matrixList.get(position);
            if(draggedCageDog.dog.name.isEmpty()) {
                this.matrixList.remove((int) position);

                while (true) {
                    CageDog cageDog = this.matrixList.get(tmpPosition);
                    if(tmpPosition == this.matrixList.size() - 1)
                    {
                        this.matrixList.add(new CageDog(new Dog(Constants.DEFAULT_DOG_NAME), null));
                        break;
                    }
                    if(cageDog.dog == null && cageDog.visibility)
                    {
                        this.matrixList.add(tmpPosition, new CageDog(new Dog(Constants.DEFAULT_DOG_NAME), null));
                        break;
                    }
                    if (cageDog.dog == null && !cageDog.visibility) {
                        CageDog tmpCageDog = this.matrixList.get(tmpPosition);
                        this.matrixList.remove((int) tmpPosition);
                        this.matrixList.add(tmpPosition + 1, tmpCageDog);
                        tmpPosition = tmpPosition + 1;
                    } else if (cageDog.dog != null && cageDog.dog.name.isEmpty()) {
                        this.matrixList.add(tmpPosition, new CageDog(new Dog(Constants.DEFAULT_DOG_NAME), null));
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
            if(nextRowIndex < this.matrixList.size() && this.matrixList.get(startIndex).row == this.matrixList.get(nextRowIndex).row)
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
                numberPaseo = this.matrixList.get(lastRow).row;
            }
            for(int i = 0; i < this.columns; ++i)
            {
                if(i == 0)
                {
                    this.matrixList.add(startIndex+i, new CageDog(null,numberPaseo, false));
                }
                else
                {
                    this.matrixList.add( startIndex + i, new CageDog(new Dog(Constants.DEFAULT_DOG_NAME), null));
                }
            }
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            View gridViewAndroid = view;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                gridViewAndroid = inflater.inflate(R.layout.lists_griditem_dogs, null);
            }
            gridViewAndroid.setTag(position);
            TextView textViewAndroid = (TextView) gridViewAndroid.findViewById(R.id.android_gridview_text);
            ImageView imageViewAndroid = (ImageView) gridViewAndroid.findViewById(R.id.android_gridview_image);
            CageDog cageDog = matrixList.get(position);
            if(gridType == Constants.GRID_CAGE_DOGS && (position % gridColumns) == 0)
            {
                if(cageDog.visibility) {
                    imageViewAndroid.setImageResource(R.mipmap.ic_doggrid_empty_image);
                    textViewAndroid.setText(String.valueOf(cageDog.row));
                }
                else
                {
                    imageViewAndroid.setImageResource(R.mipmap.ic_white_dog);
                    textViewAndroid.setText("");
                }
            }
            else {
                String dogName = cageDog.dog.name;
                textViewAndroid.setText(dogName);
                if(dogName.isEmpty())
                {
                    imageViewAndroid.setImageResource(R.mipmap.ic_white_dog);
                }
                else {
                    imageViewAndroid.setImageResource(R.mipmap.ic_dog_default);
                }
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

    public class ChangeScreenVisibilityClickListener implements View.OnClickListener {

        boolean isLeftButton;
        public ChangeScreenVisibilityClickListener(boolean isLeftButton)
        {
            this.isLeftButton = isLeftButton;
        }

        @Override
        public void onClick(View v) {
            String newVisibleZone = null;
            if(isLeftButton)
            {
                newVisibleZone = buttonLeft.getText().toString();
            }
            else
            {
                newVisibleZone = buttonRight.getText().toString();
            }
            if (newVisibleZone.equals(Constants.CAGE_ZONE_XENILES)) {
                buttonLeft.setText(Constants.CAGE_ZONE_PATIOS);
                buttonRight.setText(Constants.CAGE_ZONE_CUARENTENAS);
                patiosGridView.setVisibility(View.GONE);
                cuarentenasGridView.setVisibility(View.GONE);
                xenilesGridView.setVisibility(View.VISIBLE);
                //title.setText("Jaulas xeniles");

            } else if (newVisibleZone.equals(Constants.CAGE_ZONE_PATIOS)) {
                buttonLeft.setText(Constants.CAGE_ZONE_CUARENTENAS);
                buttonRight.setText(Constants.CAGE_ZONE_XENILES);
                xenilesGridView.setVisibility(View.GONE);
                cuarentenasGridView.setVisibility(View.GONE);
                patiosGridView.setVisibility(View.VISIBLE);
                //title.setText("Jaulas patios");
            } else {
                buttonLeft.setText(Constants.CAGE_ZONE_XENILES);
                buttonRight.setText(Constants.CAGE_ZONE_PATIOS);
                patiosGridView.setVisibility(View.GONE);
                xenilesGridView.setVisibility(View.GONE);
                cuarentenasGridView.setVisibility(View.VISIBLE);
                //title.setText("Jaulas cuarentenas");
            }
            lastVisibleZone = visibleZone;
            visibleZone = newVisibleZone;
        }

    }

    public class XenilesTouchListener implements OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long arg3) {
            if((position % (gridColumns)) == 0 || xenilesCageDogsAdapter.matrixList.get(position).dog.name.isEmpty()) {
                return false;
            }
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            view.startDrag(data, shadowBuilder, view, 0);
            draggedIndex = position;
            return true;
        }
    }

    public class PatiosTouchListener implements OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long arg3) {
            if((position % (gridColumns)) == 0 || patiosCageDogsAdapter.matrixList.get(position).dog.name.isEmpty()) {
                return false;
            }
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            view.startDrag(data, shadowBuilder, view, 0);
            draggedIndex = position;
            return true;
        }
    }

    public class CuarentenasTouchListener implements OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long arg3) {
            if((position % (gridColumns)) == 0 || cuarentenasCageDogsAdapter.matrixList.get(position).dog.name.isEmpty()) {
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
            if(unassignedCageDogsAdapter.matrixList.get(position).dog.name.isEmpty()) {
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
                    buttonLeft.setBackgroundColor(Color.GREEN);
                    buttonRight.setBackgroundColor(Color.GREEN);
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    //v.setBackgroundColor(Color.BLUE)
                    if(v.getTag().toString().contentEquals("button_left")) {
                        buttonLeft.callOnClick();
                    }
                    else if(v.getTag().toString().contentEquals("button_right")) {
                        buttonRight.callOnClick();
                    }
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    //v.setBackgroundColor(Color.RED);
                    if(v.getTag().toString().contentEquals("button_left") ||
                       v.getTag().toString().contentEquals("button_right"))
                    {
                        patiosGridView.setVisibility(View.GONE);
                        cuarentenasGridView.setVisibility(View.GONE);
                        cuarentenasGridView.setVisibility(View.VISIBLE);

                        if(visibleZone.equals(Constants.CAGE_ZONE_PATIOS) || visibleZone.equals(Constants.CAGE_ZONE_XENILES)) {
                            patiosGridView.setVisibility(View.VISIBLE);
                        }
                    }

                    break;
                case DragEvent.ACTION_DROP:
                    //Elemento arrastrado
                    View view = (View) event.getLocalState();
                    //Dejar de ver la vista donde estaba inicialmente
                    view.setVisibility(View.INVISIBLE);
                    boolean onDragAllGrid = false;

                    GridView gridViewOrigen = null;
                    CageDogsAdapter origenAdapter = null;
                    LinearLayout origen = null;

                    GridView gridViewDestino = null;
                    CageDogsAdapter destinoAdapter = null;
                    LinearLayout destino = null;

                    if(v.getTag().toString().contentEquals("button_left") || v.getTag().toString().contentEquals("button_right"))
                    {
                        view.setVisibility(View.VISIBLE);
                        return true;
                    }
                    if(v.getTag().toString().contentEquals("grid_unassigned_cage_dogs")) {
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
                    if(gridOrigenName.contentEquals("grid_xeniles_cage_dogs"))
                    {
                        origenAdapter = xenilesCageDogsAdapter;
                    }
                    else if(gridOrigenName.contentEquals("grid_patios_cage_dogs"))
                    {
                        origenAdapter = patiosCageDogsAdapter;
                    }
                    else if(gridOrigenName.contentEquals("grid_cuarentenas_cage_dogs"))
                    {
                        origenAdapter = cuarentenasCageDogsAdapter;
                    }
                    else
                    {
                        origenAdapter = unassignedCageDogsAdapter;
                    }

                    String gridDestinoName = gridViewDestino.getTag().toString();
                    if(gridDestinoName.contentEquals("grid_xeniles_cage_dogs"))
                    {
                        destinoAdapter = xenilesCageDogsAdapter;
                    }
                    else if(gridDestinoName.contentEquals("grid_patios_cage_dogs"))
                    {
                        destinoAdapter = patiosCageDogsAdapter;
                    }
                    else if(gridDestinoName.contentEquals("grid_cuarentenas_cage_dogs"))
                    {
                        destinoAdapter = cuarentenasCageDogsAdapter;
                    }
                    else
                    {
                        destinoAdapter = unassignedCageDogsAdapter;
                    }
                    String xasd = visibleZone;
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
                        if (!gridDestinoName.contentEquals("grid_unassigned_cage_dogs") && (indexDestino % gridColumns) == 0) {
                            canBeDrag = false;
                        }
                        if (gridOrigenName.contentEquals("grid_unassigned_cage_dogs") && gridDestinoName.contentEquals("grid_unassigned_cage_dogs")
                                && origenAdapter.getItem(indexDestino).dog.name.isEmpty()) {
                            canBeDrag = false;
                        }
                    }
                    if(canBeDrag) {
                        //Si gridview destino == destino inicial
                        if (gridViewOrigen == gridViewDestino) {
                            if(destino != null) {
                                indexDestino = (Integer) destino.getTag();
                                CageDog cageDogOrigen = (CageDog) origenAdapter.getItem(draggedIndex).clone();
                                CageDog cageDogDestino = (CageDog) origenAdapter.getItem(indexDestino).clone();
                                origenAdapter.removeAt(draggedIndex);
                                origenAdapter.addAt(draggedIndex, cageDogDestino);
                                destinoAdapter.removeAt(indexDestino);
                                destinoAdapter.addAt(indexDestino, cageDogOrigen);
                                if (!gridDestinoName.contentEquals("grid_unassigned_cage_dogs")) {
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
                                CageDog cageDogOrigen = (CageDog) origenAdapter.getItem(draggedIndex).clone();
                                origenAdapter.removeAt(draggedIndex);
                                origenAdapter.add(cageDogOrigen);

                                origenAdapter.notifyDataSetChanged();
                                gridViewOrigen.invalidateViews();
                            }
                        } else {
                            if (/*gridOrigenName.contentEquals("grid_clean_dogs") &&*/ gridDestinoName.contentEquals("grid_unassigned_cage_dogs")) {
                                //Sustituir perro de la GridOrigen por un espacio vacío y actualizar gridvieworigen
                                CageDog cageDog = (CageDog) origenAdapter.getItem(draggedIndex).clone();
                                if (onDragAllGrid) {
                                    origenAdapter.removeAt(draggedIndex);
                                    Dog empty = new Dog(Constants.DEFAULT_DOG_NAME);
                                    CageDog emptyCageDog = new CageDog(empty, null);
                                    origenAdapter.addAt(draggedIndex, emptyCageDog);
                                    origenAdapter.reajustEmptyElement(draggedIndex);
                                    if(origenAdapter.isSecondaryEmptyRow(draggedIndex))
                                    {
                                        origenAdapter.removeRow(draggedIndex);
                                    }
                                    origenAdapter.notifyDataSetChanged();
                                    gridViewOrigen.invalidateViews();

                                    //Añadir a la gridviewDestino el elemento arrastrado
                                    try {
                                        destinoAdapter.add(cageDog);
                                        int count = destinoAdapter.getCount();
                                        destinoAdapter.getView(count - 2, null, null);
                                        destinoAdapter.notifyDataSetChanged();
                                        gridViewDestino.invalidateViews();
                                    } catch (Exception e) {
                                        System.out.println(e);
                                    }
                                } else {
                                    indexDestino = (Integer) destino.getTag();
                                    CageDog cageDogOrigen = (CageDog) origenAdapter.getItem(draggedIndex).clone();
                                    CageDog cageDogDestino = (CageDog) destinoAdapter.getItem(indexDestino).clone();
                                    origenAdapter.removeAt(draggedIndex);
                                    origenAdapter.addAt(draggedIndex, cageDogDestino);
                                    destinoAdapter.removeAt(indexDestino);
                                    destinoAdapter.addAt(indexDestino, cageDogOrigen);
                                    origenAdapter.notifyDataSetChanged();
                                    destinoAdapter.notifyDataSetChanged();
                                    gridViewOrigen.invalidateViews();
                                    gridViewDestino.invalidateViews();
                                }
                            }
                            else if (gridOrigenName.contentEquals("grid_unassigned_cage_dogs")) {
                                indexDestino = (Integer) destino.getTag();
                                CageDog cageDogOrigen = (CageDog) origenAdapter.getItem(draggedIndex).clone();
                                CageDog cageDogDestino = (CageDog) destinoAdapter.getItem(indexDestino).clone();
                                origenAdapter.removeAt(draggedIndex);
                                if (!cageDogDestino.dog.name.isEmpty()) {
                                    origenAdapter.addAt(draggedIndex, cageDogDestino);
                                }
                                destinoAdapter.removeAt(indexDestino);
                                destinoAdapter.addAt(indexDestino, cageDogOrigen);

                                Integer newIndexDestino = destinoAdapter.reajustElement(indexDestino);
                                if(destinoAdapter.isFullRow(newIndexDestino)) {
                                    destinoAdapter.addRow(newIndexDestino);
                                }

                                destinoAdapter.notifyDataSetChanged();
                                origenAdapter.notifyDataSetChanged();
                                gridViewOrigen.invalidateViews();
                                gridViewDestino.invalidateViews();
                            }
                            else
                            {
                                indexDestino = (Integer) destino.getTag();
                                CageDog cageDogOrigen = (CageDog) origenAdapter.getItem(draggedIndex).clone();
                                CageDog cageDogDestino = (CageDog) destinoAdapter.getItem(indexDestino).clone();
                                origenAdapter.removeAt(draggedIndex);
                                origenAdapter.addAt(draggedIndex, cageDogDestino);
                                destinoAdapter.removeAt(indexDestino);
                                destinoAdapter.addAt(indexDestino, cageDogOrigen);


                                origenAdapter.reajustEmptyElement(draggedIndex);
                                if(origenAdapter.isSecondaryEmptyRow(draggedIndex))
                                {
                                    origenAdapter.removeRow(draggedIndex);
                                }
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
                            /*
                            else if(gridOrigenName.contentEquals("grid_dogs") && gridDestinoName.contentEquals("grid_clean_dogs"))
                            {
                                indexDestino = (Integer) destino.getTag();
                                VolunteerDog volunteerDogOrigen = (VolunteerDog) origenAdapter.getItem(draggedIndex).clone();
                                VolunteerDog volunteerDogDestino = (VolunteerDog) destinoAdapter.getItem(indexDestino).clone();
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
                            }*/
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
                    buttonLeft.setBackgroundColor(Color.LTGRAY);
                    buttonRight.setBackgroundColor(Color.LTGRAY);
                default:
                    break;
            }
            return true;
        }
    }

    public void addEmptyRows()
    {
        for(int i = 0; i < xenilesCageDogsAdapter.matrixList.size(); i = i + gridColumns)
        {
            if(xenilesCageDogsAdapter.isFullRow(i))
            {
                xenilesCageDogsAdapter.addRow(i);
            }
        }

        for(int i = 0; i < patiosCageDogsAdapter.matrixList.size(); i = i + gridColumns)
        {
            if(patiosCageDogsAdapter.isFullRow(i))
            {
                patiosCageDogsAdapter.addRow(i);
            }
        }

        for(int i = 0; i < cuarentenasCageDogsAdapter.matrixList.size(); i = i + gridColumns)
        {
            if(cuarentenasCageDogsAdapter.isFullRow(i))
            {
                cuarentenasCageDogsAdapter.addRow(i);
            }
        }
    }

}

