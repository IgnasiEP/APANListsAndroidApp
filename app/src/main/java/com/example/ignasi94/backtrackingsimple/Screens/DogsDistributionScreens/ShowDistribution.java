package com.example.ignasi94.backtrackingsimple.Screens.DogsDistributionScreens;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnClickListener;

import com.example.ignasi94.backtrackingsimple.BBDD.DBAdapter;
import com.example.ignasi94.backtrackingsimple.Estructuras.CageDog;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerDog;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerWalks;
import com.example.ignasi94.backtrackingsimple.R;
import com.example.ignasi94.backtrackingsimple.Screens.DogManagement.DogList;
import com.example.ignasi94.backtrackingsimple.Screens.FirstScreen;
import com.example.ignasi94.backtrackingsimple.Utils.Constants;


import java.util.ArrayList;

public class ShowDistribution extends Activity {

    Integer gridColumns;
    DBAdapter dbAdapter;
    HorizontalScrollView xenilesGridView;
    GridView xenilesCageDogGrid = null;
    ArrayList<CageDog> xenilesCageDogsArray;

    HorizontalScrollView patiosGridView;
    GridView patiosCageDogGrid = null;
    ArrayList<CageDog> patiosCageDogsArray;

    HorizontalScrollView cuarentenasGridView;
    GridView cuarentenasCageDogGrid = null;
    ArrayList<CageDog> cuarentenasCageDogsArray;
    String visibleZone = null;
    Button buttonLeft = null;
    Button buttonRight = null;
    TextView title = null;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.distribution_activity_show);
        gridColumns = 5;
        this.ReadMakeListsParameters(getIntent());

        xenilesGridView = (HorizontalScrollView) findViewById(R.id.xeniles_cagesview);
        patiosGridView = (HorizontalScrollView) findViewById(R.id.patios_cagesview);
        cuarentenasGridView = (HorizontalScrollView) findViewById(R.id.cuarentenas_cagesview);

        //XENILES
        xenilesCageDogGrid = (GridView) findViewById(R.id.grid_xeniles_cage_dogs);
        xenilesCageDogGrid.setNumColumns(gridColumns);
        // Crear Adapter
        CageDogsAdapter xenilesCageDogsAdapter = new CageDogsAdapter(getApplicationContext(), this.xenilesCageDogsArray);
        // Relacionar el adapter a la grid
        xenilesCageDogGrid.setAdapter(xenilesCageDogsAdapter);

        //PATIOS
        patiosCageDogGrid = (GridView) findViewById(R.id.grid_patios_cage_dogs);
        patiosCageDogGrid.setNumColumns(gridColumns);
        // Crear Adapter
        CageDogsAdapter patiosCageDogsAdapter = new CageDogsAdapter(getApplicationContext(), this.patiosCageDogsArray);
        // Relacionar el adapter a la grid
        patiosCageDogGrid.setAdapter(patiosCageDogsAdapter);

        //Cuarentenas
        cuarentenasCageDogGrid = (GridView) findViewById(R.id.grid_cuarentenas_cage_dogs);
        cuarentenasCageDogGrid.setNumColumns(gridColumns);
        // Crear Adapter
        CageDogsAdapter cuarentenasCageDogsAdapter = new CageDogsAdapter(getApplicationContext(), this.cuarentenasCageDogsArray);
        // Relacionar el adapter a la grid
        cuarentenasCageDogGrid.setAdapter(cuarentenasCageDogsAdapter);

        title = (TextView) findViewById(R.id.textView_title);
        buttonLeft = (Button) findViewById(R.id.button_left);
        buttonRight = (Button) findViewById(R.id.button_right);
        buttonLeft.setOnClickListener(new ChangeScreenVisibilityClickListener(true));
        buttonRight.setOnClickListener(new ChangeScreenVisibilityClickListener(false));

        visibleZone = Constants.CAGE_ZONE_XENILES;
        buttonLeft.setText(Constants.CAGE_ZONE_PATIOS);
        buttonRight.setText(Constants.CAGE_ZONE_CUARENTENAS);
        patiosGridView.setVisibility(View.GONE);
        cuarentenasGridView.setVisibility(View.GONE);
        xenilesGridView.setVisibility(View.VISIBLE);
        title.setText("Jaulas xeniles");

        ImageButton button = (ImageButton) findViewById(R.id.image_cage_map);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchactivity= new Intent(ShowDistribution.this,ShowMap.class);
                startActivity(launchactivity);
            }
        });
    }

    public void ReadMakeListsParameters(Intent intent) {
        dbAdapter = new DBAdapter(this);
        xenilesCageDogsArray = dbAdapter.getDogsPerCage(gridColumns, Constants.CAGE_ZONE_XENILES);
        patiosCageDogsArray = dbAdapter.getDogsPerCage(gridColumns, Constants.CAGE_ZONE_PATIOS);
        cuarentenasCageDogsArray = dbAdapter.getDogsPerCage(gridColumns, Constants.CAGE_ZONE_CUARENTENAS);
    }

    public class CageDogsAdapter extends BaseAdapter {
        Context context;
        ArrayList<CageDog> matrixList;

        public CageDogsAdapter(Context context, ArrayList<CageDog> matrixList) {
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
            CageDog cageDog = matrixList.get(position);
            if((position % gridColumns) == 0)
            {
                if(cageDog.visibility) {
                    imageViewAndroid.setImageResource(R.mipmap.ic_doggrid_empty_image);
                    textViewAndroid.setText(String.valueOf(position/5 + 1));
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

            return gridViewAndroid;
        }
    }

    public class ChangeScreenVisibilityClickListener implements OnClickListener{

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
                title.setText("Jaulas xeniles");

            } else if (newVisibleZone.equals(Constants.CAGE_ZONE_PATIOS)) {
                buttonLeft.setText(Constants.CAGE_ZONE_CUARENTENAS);
                buttonRight.setText(Constants.CAGE_ZONE_XENILES);
                xenilesGridView.setVisibility(View.GONE);
                cuarentenasGridView.setVisibility(View.GONE);
                patiosGridView.setVisibility(View.VISIBLE);
                title.setText("Jaulas patios");
            } else {
                buttonLeft.setText(Constants.CAGE_ZONE_XENILES);
                buttonRight.setText(Constants.CAGE_ZONE_PATIOS);
                patiosGridView.setVisibility(View.GONE);
                xenilesGridView.setVisibility(View.GONE);
                cuarentenasGridView.setVisibility(View.VISIBLE);
                title.setText("Jaulas cuarentenas");
            }
            visibleZone = newVisibleZone;
        }

    }

}
