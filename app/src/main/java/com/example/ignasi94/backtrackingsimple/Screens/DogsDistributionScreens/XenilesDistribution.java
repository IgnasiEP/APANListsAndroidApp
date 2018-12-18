package com.example.ignasi94.backtrackingsimple.Screens.DogsDistributionScreens;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ignasi94.backtrackingsimple.BBDD.DBAdapter;
import com.example.ignasi94.backtrackingsimple.Estructuras.CageDog;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerDog;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerWalks;
import com.example.ignasi94.backtrackingsimple.R;
import com.example.ignasi94.backtrackingsimple.Utils.Constants;

import java.util.ArrayList;

public class XenilesDistribution extends Activity {

    Integer gridColumns;
    DBAdapter dbAdapter;
    ArrayList<CageDog> cageDogsArray;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xeniles_activity_show_distribution);
        gridColumns = 5;
        this.ReadMakeListsParameters(getIntent());

        GridView cageDogGrid = (GridView) findViewById(R.id.grid_cage_dogs);
        cageDogGrid.setNumColumns(gridColumns);
        // Crear Adapter
        CageDogsAdapter cageDogsAdapter = new CageDogsAdapter(getApplicationContext(), this.cageDogsArray);
        // Relacionar el adapter a la grid
        cageDogGrid.setAdapter(cageDogsAdapter);
    }

    public void ReadMakeListsParameters(Intent intent) {
        dbAdapter = new DBAdapter(this);
        cageDogsArray = dbAdapter.GetDogsPerCage(gridColumns, Constants.CAGE_ZONE_XENILES);
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
            CageDog cageDog = cageDogsArray.get(position);
            if((position % gridColumns) == 0)
            {
                if(cageDog.visibility) {
                    imageViewAndroid.setImageResource(R.mipmap.ic_doggrid_empty_image);
                    textViewAndroid.setText(String.valueOf(cageDog.cage.id));
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
}
