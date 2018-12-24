package com.example.ignasi94.backtrackingsimple.Screens.DogManagement;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ignasi94.backtrackingsimple.BBDD.DBAdapter;
import com.example.ignasi94.backtrackingsimple.Estructuras.Dog;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerDog;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerWalks;
import com.example.ignasi94.backtrackingsimple.R;
import com.example.ignasi94.backtrackingsimple.Screens.DogsDistributionScreens.DistributionOptionsScreen;
import com.example.ignasi94.backtrackingsimple.Screens.DogsDistributionScreens.EditDistribution;
import com.example.ignasi94.backtrackingsimple.Utils.Constants;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DogList extends Activity {

    GridView gridView;
    ArrayList<Dog> dogs;
    DogAdapter dogAdapter;
    DBAdapter dbAdapter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dog_management_dog_list);

        this.ReadMakeListsParameters(getIntent());

        // DOGGRID
        gridView = (GridView) findViewById(R.id.grid_dog_lists);
        gridView.setNumColumns(1);
        // Adapter
        dogAdapter = new DogAdapter(getApplicationContext(), dogs);
        gridView.setAdapter(dogAdapter);
    }

    public void ReadMakeListsParameters(Intent intent)
    {
        dbAdapter = new DBAdapter(this);
        dogs = (ArrayList<Dog>) dbAdapter.getAllDogs();
    }

    public class DogAdapter extends BaseAdapter {
        Context context;
        ArrayList<Dog> matrixList;

        public DogAdapter(Context context, ArrayList<Dog> matrixList) {
            this.context = context;
            matrixList.add(0, new Dog());
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
                gridViewAndroid = inflater.inflate(R.layout.dog_management_dog_item, null);
            }
            TextView textViewAndroid = (TextView) gridViewAndroid.findViewById(R.id.dog_name);
            ImageView imageViewAndroid = (ImageView) gridViewAndroid.findViewById(R.id.dog_image);
            if(position == 0)
            {
                //'Add element' element
                textViewAndroid.setText("NEW");
                imageViewAndroid.setImageResource(R.mipmap.ic_white_dog);
            }
            else {
                String dogName = matrixList.get(position).name;
                textViewAndroid.setText(dogName);
                imageViewAndroid.setImageResource(R.mipmap.ic_dog_default);
            }

            gridViewAndroid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent launchactivity;
                    if(position == 0)
                    {
                        launchactivity = new Intent(DogList.this,EditDog.class);
                        launchactivity.putExtra("NEW", true);
                    }
                    else
                    {
                        launchactivity = new Intent(DogList.this,ShowDog.class);
                        launchactivity.putExtra("DOGID", matrixList.get(position).id);
                    }
                    startActivity(launchactivity);
                }
            });

            return gridViewAndroid;
        }
    }
}
