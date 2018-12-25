package com.example.ignasi94.backtrackingsimple.Screens.DogManagement;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.constraint.Guideline;

import com.example.ignasi94.backtrackingsimple.BBDD.DBAdapter;
import com.example.ignasi94.backtrackingsimple.Estructuras.Dog;
import com.example.ignasi94.backtrackingsimple.R;
import com.example.ignasi94.backtrackingsimple.Utils.Constants;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

public class ShowDog extends Activity {

    public DBAdapter dbAdapter;
    public Dog dog;
    public Integer maxId;
    public Integer minId;
    public TextView dogNameText;
    public TextView ageText;
    public TextView linkText;
    public CheckBox specialDogCheck;
    public TextView observationsText;
    public Spinner spinner;
    public Dictionary<Integer,Dog> dogs;
    public Integer dogId;
    public TextView tipoPaseoText;
    public TextView dogObservations;
    public GridView gridView;
    public DogAdapter dogAdapter;
    public Button editButton;
    public View divider;
    public ImageView dogImage;
    public Integer visibilityInfo = 0;
    public ImageButton lastDogButton;
    public ImageButton nextDogButton;
    public Button datosButton;
    public Button jaulaButton;
    public Button showDogFriendsButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dog_management_dog_show);

        dogId = getIntent().getIntExtra("DOGID", -1);

        dbAdapter = new DBAdapter(this);

        dogs = dbAdapter.getAllDogsDictionary();
        maxId = dbAdapter.GetMaxIdDogsTable();
        minId = dbAdapter.GetMinIdDogsTable();

        dogNameText = (TextView) findViewById(R.id.dog_name);
        ageText = (TextView) findViewById(R.id.dog_age);
        linkText = (TextView) findViewById(R.id.dog_link);
        specialDogCheck = (CheckBox) findViewById(R.id.special_dog_check);
        observationsText = (TextView) findViewById(R.id.dog_observations);
        spinner = (Spinner) findViewById(R.id.walktype_spinner);
        spinner = (Spinner) findViewById(R.id.walktype_spinner);
        String[] letra = {"No pasea","Interior","Exterior"};
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra));
        spinner.setEnabled(false);
        tipoPaseoText = (TextView) findViewById(R.id.textView7);
        showDogFriendsButton = (Button) findViewById(R.id.show_dog_friends_button);
        dogObservations = (TextView) findViewById(R.id.textView9);
        divider = (View) findViewById(R.id.view2);
        dogImage = (ImageView) findViewById(R.id.dog_image);

        UpdateView();

        datosButton = (Button) findViewById(R.id.datos_button);
        jaulaButton = (Button) findViewById(R.id.jaula_button);
        datosButton.setVisibility(View.INVISIBLE);

        datosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visibilityInfo = 0;
                datosButton.setVisibility(View.INVISIBLE);
                jaulaButton.setVisibility(View.VISIBLE);
                ConstraintLayout.LayoutParams params = null;

                Guideline guideLine15 = (Guideline) findViewById(R.id.guideline15);
                params = (ConstraintLayout.LayoutParams) guideLine15.getLayoutParams();
                params.guidePercent = 0.88f;
                guideLine15.setLayoutParams(params);

                specialDogCheck.setVisibility(View.VISIBLE);
                tipoPaseoText.setVisibility(View.VISIBLE);
                dogObservations.setVisibility(View.VISIBLE);
                observationsText.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.VISIBLE);
                if(dog.walktype == Constants.WT_INTERIOR && dog.friends.size() > 0)
                {
                    showDogFriendsButton.setVisibility(View.VISIBLE);
                }
            }
        });

        jaulaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visibilityInfo = 1;
                datosButton.setVisibility(View.VISIBLE);
                jaulaButton.setVisibility(View.INVISIBLE);
                Guideline guideLine15 = (Guideline) findViewById(R.id.guideline15);
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) guideLine15.getLayoutParams();
                params.guidePercent = 0.40f;
                guideLine15.setLayoutParams(params);

                specialDogCheck.setVisibility(View.INVISIBLE);
                tipoPaseoText.setVisibility(View.INVISIBLE);
                showDogFriendsButton.setVisibility(View.INVISIBLE);
                dogObservations.setVisibility(View.INVISIBLE);
                observationsText.setVisibility(View.INVISIBLE);
                spinner.setVisibility(View.INVISIBLE);
            }
        });

        lastDogButton = (ImageButton) findViewById(R.id.last_dog_button);
        lastDogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newDogId = dogId - 1;
                while(true)
                {
                    dog = dogs.get(newDogId);
                    if(dog != null)
                    {
                        dogId = newDogId;
                        break;
                    }

                    if(newDogId - 1 < minId) {
                        newDogId = maxId;
                    }
                    else
                    {
                        newDogId--;
                    }

                }

                UpdateView();
            }
        });

        nextDogButton = (ImageButton) findViewById(R.id.next_dog_button);
        nextDogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newDogId = dogId + 1;
                while(true)
                {
                    dog = dogs.get(newDogId);
                    if(dog != null)
                    {
                        dogId = newDogId;
                        break;
                    }

                    if(newDogId + 1 > maxId) {
                        newDogId = minId;
                    }
                    else
                    {
                        newDogId++;
                    }
                }

                UpdateView();
            }
        });

        showDogFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Guideline guideLine2 = (Guideline) findViewById(R.id.guideline2);
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) guideLine2.getLayoutParams();
                params.guidePercent = 0.00f;
                guideLine2.setLayoutParams(params);

                Guideline guideLine27 = (Guideline) findViewById(R.id.guideline27);
                params = (ConstraintLayout.LayoutParams) guideLine27.getLayoutParams();
                params.guidePercent = 0.10f;
                guideLine27.setLayoutParams(params);

                dogImage.setVisibility(View.INVISIBLE);
                dogNameText.setVisibility(View.INVISIBLE);
                ageText.setVisibility(View.INVISIBLE);
                linkText.setVisibility(View.INVISIBLE);
                editButton.setVisibility(View.INVISIBLE);

                divider.setVisibility(View.INVISIBLE);
                datosButton.setVisibility(View.INVISIBLE);
                jaulaButton.setVisibility(View.INVISIBLE);

                specialDogCheck.setVisibility(View.INVISIBLE);
                tipoPaseoText.setVisibility(View.INVISIBLE);
                showDogFriendsButton.setVisibility(View.INVISIBLE);
                dogObservations.setVisibility(View.INVISIBLE);
                observationsText.setVisibility(View.INVISIBLE);
                spinner.setVisibility(View.INVISIBLE);

                nextDogButton.setVisibility(View.INVISIBLE);
                lastDogButton.setVisibility(View.INVISIBLE);

                // DOGGRID
                List<Dog> friendDogs = new ArrayList<Dog>();
                friendDogs.add(dog);
                gridView = (GridView) findViewById(R.id.grid_dog_lists);
                gridView.setNumColumns(5);
                // Adapter
                dogAdapter = new DogAdapter(getApplicationContext(), friendDogs.get(0).friends);
                gridView.setAdapter(dogAdapter);

                gridView.setVisibility(View.VISIBLE);
            }
        });


        editButton = (Button) findViewById(R.id.button_edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchactivity= new Intent(ShowDog.this,EditDog.class);
                launchactivity.putExtra("DOGID", dogId);
                startActivity(launchactivity);
            }
        });


    }

    public void UpdateView()
    {
        dog = dogs.get(dogId);

        if(dog.name == null) {
            this.dogNameText.setText("*Nombre perro*");
            this.dogNameText.setTextColor(Color.BLACK);
        }
        else {
            this.dogNameText.setText(dog.name.toString());
            this.dogNameText.setTextColor(Color.BLACK);
        }

        this.ageText.setText(Integer.toString(dog.age));

        if(dog.link == null || dog.link.isEmpty())
        {
            this.linkText.setText("");
        }
        else {
            this.linkText.setText(dog.link);
        }

        if(dog.special)
        {
            this.specialDogCheck.setChecked(true);
        }
        else
        {
            this.specialDogCheck.setChecked(false);
        }

        this.observationsText.setText(dog.observations);

        if(dog.walktype == Constants.WT_NONE)
        {
            this.spinner.setSelection(0);
            showDogFriendsButton.setVisibility(View.INVISIBLE);
        }
        else if(dog.walktype == Constants.WT_INTERIOR)
        {
            this.spinner.setSelection(1);
            int i = this.spinner.getVisibility();
            if(this.spinner.getVisibility() == View.VISIBLE && dog.friends.size() > 0) {
                showDogFriendsButton.setVisibility(View.VISIBLE);
            }
            else
            {
                showDogFriendsButton.setVisibility(View.INVISIBLE);
            }
        }
        else
        {
            this.spinner.setSelection(2);
            showDogFriendsButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
        dogs = dbAdapter.getAllDogsDictionary();
        if(dogs.get(dogId) == null)
        {
            finish();
        }
        else
        {
            UpdateView();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && dogImage.getVisibility() != View.VISIBLE) {
            Guideline guideLine2 = (Guideline) findViewById(R.id.guideline2);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) guideLine2.getLayoutParams();
            params.guidePercent = 0.99f;
            guideLine2.setLayoutParams(params);

            Guideline guideLine27 = (Guideline) findViewById(R.id.guideline27);
            params = (ConstraintLayout.LayoutParams) guideLine27.getLayoutParams();
            params.guidePercent = 0.995f;
            guideLine27.setLayoutParams(params);

            dogImage.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.VISIBLE);
            dogNameText.setVisibility(View.VISIBLE);
            ageText.setVisibility(View.VISIBLE);
            linkText.setVisibility(View.VISIBLE);

            specialDogCheck.setVisibility(View.VISIBLE);
            tipoPaseoText.setVisibility(View.VISIBLE);
            dogObservations.setVisibility(View.VISIBLE);
            observationsText.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);

            nextDogButton.setVisibility(View.VISIBLE);
            lastDogButton.setVisibility(View.VISIBLE);

            if(visibilityInfo == 0)
            {
                jaulaButton.setVisibility(View.VISIBLE);
                if(dog.walktype == Constants.WT_INTERIOR && dog.friends.size() > 0)
                {
                    showDogFriendsButton.setVisibility(View.VISIBLE);
                }
            }
            else
            {
                datosButton.setVisibility(View.VISIBLE);
            }

            gridView.setVisibility(View.INVISIBLE);

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public class DogAdapter extends BaseAdapter {
        Context context;
        List<Dog> matrixList;

        public DogAdapter(Context context, List<Dog> matrixList) {
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

            String dogName = matrixList.get(position).name;
            textViewAndroid.setText(dogName);
            imageViewAndroid.setImageResource(R.mipmap.ic_dog_default);

            return gridViewAndroid;
        }
    }
}
