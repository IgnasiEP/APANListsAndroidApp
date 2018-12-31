package com.example.ignasi94.backtrackingsimple.Screens.DogManagement;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.constraint.Guideline;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import android.widget.Filter;
import android.widget.SearchView;
import android.widget.Filterable;

import com.example.ignasi94.backtrackingsimple.BBDD.DBAdapter;
import com.example.ignasi94.backtrackingsimple.Estructuras.Dog;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerWalks;
import com.example.ignasi94.backtrackingsimple.R;
import com.example.ignasi94.backtrackingsimple.Utils.Constants;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

public class EditDog extends Activity {

    public DBAdapter dbAdapter;
    public Dog dog;
    public Integer maxId;
    public TextView titleDogNameText;
    public TextView titleAgeText;
    public TextView titleLinkText;
    public EditText dogNameText;
    public EditText ageText;
    public EditText linkText;
    public CheckBox specialDogCheck;
    public TextView observationsText;
    public Spinner spinner;
    public Dictionary<Integer,Dog> dogs;
    public Integer dogId;
    public TextView tipoPaseoText;
    public TextView dogObservations;
    public GridView selectedDogsGridView;
    public GridView allDogsGridView;
    public DogAdapter selectedDogAdapter;
    public DogAdapter allDogAdapter;
    public Button removeButton;
    public View divider;
    public ImageView dogImage;
    public Integer visibilityInfo = 0;
    public Button datosButton;
    public Button jaulaButton;
    public Button showDogFriendsButton;
    public Button saveButton;
    public Button cancelButton;
    public boolean newDog;
    public ImageView cageView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dog_management_dog_edit);

        newDog = getIntent().getBooleanExtra("NEW", false);
        dogId = getIntent().getIntExtra("DOGID", -1);

        dbAdapter = new DBAdapter(this);

        dogs = dbAdapter.getAllDogsDictionary();
        maxId = dbAdapter.GetMaxIdDogsTable();

        titleDogNameText = (TextView) findViewById(R.id.textView4);
        titleAgeText = (TextView) findViewById(R.id.textView5);
        titleLinkText = (TextView) findViewById(R.id.textView6);
        dogNameText = (EditText) findViewById(R.id.dog_name);
        ageText = (EditText) findViewById(R.id.dog_age);
        linkText = (EditText) findViewById(R.id.dog_link);
        specialDogCheck = (CheckBox) findViewById(R.id.special_dog_check);
        observationsText = (TextView) findViewById(R.id.dog_observations);
        showDogFriendsButton = (Button) findViewById(R.id.show_dog_friends_button);
        spinner = (Spinner) findViewById(R.id.walktype_spinner);
        String[] letra = {"No pasea","Interior","Exterior"};
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra));
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id)
            {
                String selected = (String) adapterView.getItemAtPosition(pos);
                if(selected.equals("No pasea"))
                {
                    dog.walktype = Constants.WT_NONE;
                }
                else if(selected.equals("Interior"))
                {
                    dog.walktype = Constants.WT_INTERIOR;
                }
                else
                {
                    dog.walktype = Constants.WT_EXTERIOR;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {    }
        });
        tipoPaseoText = (TextView) findViewById(R.id.textView7);
        dogObservations = (TextView) findViewById(R.id.textView9);
        divider = (View) findViewById(R.id.view2);
        dogImage = (ImageView) findViewById(R.id.dog_image);
        cageView = (ImageView) findViewById(R.id.cage_image);

        UpdateView();

        datosButton = (Button) findViewById(R.id.datos_button);
        jaulaButton = (Button) findViewById(R.id.jaula_button);
        datosButton.setVisibility(View.INVISIBLE);

        SearchView search = (SearchView) findViewById(R.id.android_search);
        search.setActivated(true);
        search.setQueryHint("Nombre");
        search.onActionViewExpanded();
        search.setIconified(false);
        search.clearFocus();

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                allDogAdapter.getFilter().filter(newText);

                return false;
            }
        });

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
                showDogFriendsButton.setVisibility(View.VISIBLE);
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

                Guideline guideLine30 = (Guideline) findViewById(R.id.guideline30);
                params = (ConstraintLayout.LayoutParams) guideLine30.getLayoutParams();
                params.guidePercent = 0.5f;
                guideLine30.setLayoutParams(params);

                dogImage.setVisibility(View.INVISIBLE);
                titleDogNameText.setVisibility(View.INVISIBLE);
                titleAgeText.setVisibility(View.INVISIBLE);
                titleLinkText.setVisibility(View.INVISIBLE);
                dogNameText.setVisibility(View.INVISIBLE);
                ageText.setVisibility(View.INVISIBLE);
                linkText.setVisibility(View.INVISIBLE);
                removeButton.setVisibility(View.INVISIBLE);

                divider.setVisibility(View.INVISIBLE);
                datosButton.setVisibility(View.INVISIBLE);
                jaulaButton.setVisibility(View.INVISIBLE);

                specialDogCheck.setVisibility(View.INVISIBLE);
                tipoPaseoText.setVisibility(View.INVISIBLE);
                showDogFriendsButton.setVisibility(View.INVISIBLE);
                dogObservations.setVisibility(View.INVISIBLE);
                observationsText.setVisibility(View.INVISIBLE);
                spinner.setVisibility(View.INVISIBLE);

                cancelButton.setVisibility(View.INVISIBLE);
                saveButton.setVisibility(View.INVISIBLE);

                selectedDogsGridView = (GridView) findViewById(R.id.grid_selected_dogs);
                selectedDogsGridView.setNumColumns(1);
                // Adapter
                selectedDogAdapter = new DogAdapter(getApplicationContext(), dog.friends, false);
                selectedDogsGridView.setAdapter(selectedDogAdapter);
                selectedDogsGridView.setVisibility(View.VISIBLE);

                List<Dog> allDogs = dbAdapter.getAllDogs();
                if(!newDog)
                {
                    for(int i = 0; i < allDogs.size(); ++i)
                    {
                        if(allDogs.get(i).id == dogId)
                        {
                            allDogs.remove(i);
                        }
                    }

                    for(int i = 0; i < allDogs.size(); ++i)
                    {
                        for(int j = 0; j < dog.friends.size(); ++j)
                        {
                            if(allDogs.get(i).id == dog.friends.get(j).id)
                            {
                                allDogs.remove(i);
                            }
                        }
                    }
                }
                allDogsGridView = (GridView) findViewById(R.id.grid_all_dogs);
                allDogsGridView.setNumColumns(1);
                // Adapter
                allDogAdapter = new DogAdapter(getApplicationContext(), allDogs, true);
                allDogsGridView.setAdapter(allDogAdapter);
                allDogsGridView.setVisibility(View.VISIBLE);
            }
        });


        removeButton = (Button) findViewById(R.id.button_remove);
        if(newDog)
        {
            removeButton.setVisibility(View.INVISIBLE);
        }
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbAdapter.DeleteDog(dog);
                finish();
            }
        });

        saveButton = (Button) findViewById(R.id.button_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dog.name = dogNameText.getText().toString();
                dog.age = Integer.parseInt(ageText.getText().toString());
                dog.link = linkText.getText().toString();
                dog.observations = observationsText.getText().toString();
                dog.special = specialDogCheck.isChecked();

                if(selectedDogAdapter != null) {
                    dog.friends = selectedDogAdapter.matrixList;
                }
                dbAdapter.SaveOrUpdateDog(dog);
                finish();
            }
        });

        cancelButton = (Button) findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void UpdateView()
    {
        if(newDog) {
            dog = new Dog("", -1,0,null,false,Constants.WT_EXTERIOR,null);
        }
        else {
            dog = dogs.get(dogId);
        }

        if (dog.name == null) {
            this.dogNameText.setText("*Nombre perro*");
            this.dogNameText.setTextColor(Color.BLACK);
        } else {
            this.dogNameText.setText(dog.name.toString());
            this.dogNameText.setTextColor(Color.BLACK);
        }

        this.ageText.setText(Integer.toString(dog.age));

        if (dog.link == null || dog.link.isEmpty()) {
            this.linkText.setText("");
        } else {
            this.linkText.setText(dog.link);
        }

        if (dog.special) {
            this.specialDogCheck.setChecked(true);
        } else {
            this.specialDogCheck.setChecked(false);
        }

        this.observationsText.setText(dog.observations);

        if (dog.walktype == Constants.WT_NONE) {
            this.spinner.setSelection(0);
        } else if (dog.walktype == Constants.WT_INTERIOR) {
            this.spinner.setSelection(1);


        } else {
            this.spinner.setSelection(2);
        }

        this.SetCageImage();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && dogImage.getVisibility() != View.VISIBLE) {
            Guideline guideLine2 = (Guideline) findViewById(R.id.guideline2);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) guideLine2.getLayoutParams();
            params.guidePercent = 0.997f;
            guideLine2.setLayoutParams(params);

            Guideline guideLine27 = (Guideline) findViewById(R.id.guideline27);
            params = (ConstraintLayout.LayoutParams) guideLine27.getLayoutParams();
            params.guidePercent = 0.998f;
            guideLine27.setLayoutParams(params);

            Guideline guideLine30 = (Guideline) findViewById(R.id.guideline30);
            params = (ConstraintLayout.LayoutParams) guideLine30.getLayoutParams();
            params.guidePercent = 0.999f;
            guideLine30.setLayoutParams(params);

            dogImage.setVisibility(View.VISIBLE);
            if(!newDog) {
                removeButton.setVisibility(View.VISIBLE);
            }
            titleDogNameText.setVisibility(View.VISIBLE);
            titleAgeText.setVisibility(View.VISIBLE);
            titleLinkText.setVisibility(View.VISIBLE);
            dogNameText.setVisibility(View.VISIBLE);
            ageText.setVisibility(View.VISIBLE);
            linkText.setVisibility(View.VISIBLE);

            specialDogCheck.setVisibility(View.VISIBLE);
            tipoPaseoText.setVisibility(View.VISIBLE);
            dogObservations.setVisibility(View.VISIBLE);
            observationsText.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);

            cancelButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);

            if(visibilityInfo == 0)
            {
                jaulaButton.setVisibility(View.VISIBLE);
                showDogFriendsButton.setVisibility(View.VISIBLE);
            }
            else
            {
                datosButton.setVisibility(View.VISIBLE);
            }

            selectedDogsGridView.setVisibility(View.INVISIBLE);
            allDogsGridView.setVisibility(View.INVISIBLE);

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public class DogAdapter extends BaseAdapter implements Filterable{
        Context context;
        List<Dog> matrixList;
        List<Dog> allmatrixList;
        List<Dog> allmatrixOptionList;
        boolean allList;
        ValueFilter valueFilter;

        public DogAdapter(Context context, List<Dog> matrixList, boolean allList) {
            this.context = context;
            this.matrixList = matrixList;
            this.allmatrixList = (List<Dog>) ((ArrayList<Dog>) matrixList).clone();
            this.allmatrixOptionList = (List<Dog>) ((ArrayList<Dog>) matrixList).clone();
            this.allList = allList;
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
                gridViewAndroid = inflater.inflate(R.layout.lists_griditem_select_volunteers, null);
            }
            TextView textViewAndroid = (TextView) gridViewAndroid.findViewById(R.id.android_gridview_text);
            ImageView imageViewAndroid = (ImageView) gridViewAndroid.findViewById(R.id.android_gridview_image);
            ImageButton buttonViewAndroid = (ImageButton) gridViewAndroid.findViewById(R.id.android_gridview_button);

            textViewAndroid.setText(this.matrixList.get(position).name);
            imageViewAndroid.setImageResource(R.mipmap.ic_dog_default);

            if(this.allList) {
                buttonViewAndroid.setImageResource(R.mipmap.ic_flecha_arriba);
            }
            else {
                buttonViewAndroid.setImageResource(R.mipmap.ic_flecha_abajo);
            }
            buttonViewAndroid.setBackgroundColor(Color.parseColor("#FAFAFA"));
            buttonViewAndroid.setOnClickListener(new MoveDogsOnClickListener(position, this.allList));

            return gridViewAndroid;
        }

        @Override
        public Filter getFilter() {
            if (valueFilter == null) {
                valueFilter = new ValueFilter();
            }
            return valueFilter;
        }

        private class ValueFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<Dog> allMatrix = (ArrayList<Dog>) ((ArrayList<Dog>) allmatrixList).clone();
                if (constraint != null && constraint.length() > 0) {
                    List<Dog> filterList = new ArrayList<>();
                    for (int i = 0; i < allMatrix.size(); i++) {
                        if ((allMatrix.get(i).name.toUpperCase()).contains(constraint.toString().toUpperCase()) && allmatrixOptionList.contains(allMatrix.get(i))) {
                            filterList.add(allMatrix.get(i));
                        }
                    }
                    results.count = filterList.size();
                    results.values = filterList;
                } else {
                    results.count = allMatrix.size();
                    results.values = allMatrix;
                }
                return results;

            }

            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                matrixList = (ArrayList<Dog>) results.values;
                notifyDataSetChanged();
            }


        }
    }

    public class MoveDogsOnClickListener implements OnClickListener
    {
        int position;
        boolean allList;
        public MoveDogsOnClickListener(int position, boolean allList) {
            this.position = position;
            this.allList = allList;
        }

        @Override
        public void onClick(View v)
        {
            Dog dog;
            if(allList)
            {
                dog = allDogAdapter.matrixList.get(position);
                allDogAdapter.matrixList.remove(position);
                for(int i = 0; i < allDogAdapter.allmatrixList.size(); ++i)
                {
                    if(allDogAdapter.allmatrixList.get(i).name == dog.name)
                    {
                        allDogAdapter.allmatrixList.remove(i);
                    }
                }
                selectedDogAdapter.matrixList.add(dog);
            }
            else
            {
                dog = selectedDogAdapter.matrixList.get(position);
                selectedDogAdapter.matrixList.remove(position);
                allDogAdapter.matrixList.add(dog);
                allDogAdapter.allmatrixList.add(dog);
            }

            allDogAdapter.notifyDataSetChanged();
            selectedDogAdapter.notifyDataSetChanged();
            allDogsGridView.invalidateViews();
            selectedDogsGridView.invalidateViews();
        }

    };

    public void SetCageImage()
    {
        if(dog != null)
        {
            if(dog.idCage == 1)
            {
                cageView.setImageResource(R.drawable.ic_xeniles1);
            }
            else if(dog.idCage == 2)
            {
                cageView.setImageResource(R.drawable.ic_xeniles2);
            }
            else if(dog.idCage == 3)
            {
                cageView.setImageResource(R.drawable.ic_xeniles3);
            }
            else if(dog.idCage == 4)
            {
                cageView.setImageResource(R.drawable.ic_xeniles4);
            }
            else if(dog.idCage == 5)
            {
                cageView.setImageResource(R.drawable.ic_xeniles5);
            }
            else if(dog.idCage == 6)
            {
                cageView.setImageResource(R.drawable.ic_xeniles6);
            }
            else if(dog.idCage == 7)
            {
                cageView.setImageResource(R.drawable.ic_xeniles7);
            }
            else if(dog.idCage == 8)
            {
                cageView.setImageResource(R.drawable.ic_xeniles8);
            }
            else if(dog.idCage == 9)
            {
                cageView.setImageResource(R.drawable.ic_xeniles9);
            }
            else if(dog.idCage == 10)
            {
                cageView.setImageResource(R.drawable.ic_xeniles10);
            }
            else if(dog.idCage == 11)
            {
                cageView.setImageResource(R.drawable.ic_xeniles11);
            }
            else if(dog.idCage == 12)
            {
                cageView.setImageResource(R.drawable.ic_xeniles12);
            }
            else if(dog.idCage == 13)
            {
                cageView.setImageResource(R.drawable.ic_xeniles13);
            }
            else if(dog.idCage == 14)
            {
                cageView.setImageResource(R.drawable.ic_xeniles14);
            }
            else if(dog.idCage == 15)
            {
                cageView.setImageResource(R.drawable.ic_xeniles15);
            }
            else if(dog.idCage == 16)
            {
                cageView.setImageResource(R.drawable.ic_xeniles16);
            }
            else if(dog.idCage == 17)
            {
                cageView.setImageResource(R.drawable.ic_xeniles17);
            }
            else if(dog.idCage == 18)
            {
                cageView.setImageResource(R.drawable.ic_xeniles18);
            }
            else if(dog.idCage == 19)
            {
                cageView.setImageResource(R.drawable.ic_xeniles19);
            }
            else if(dog.idCage == 20)
            {
                cageView.setImageResource(R.drawable.ic_xeniles20);
            }
            else if(dog.idCage == 21)
            {
                cageView.setImageResource(R.drawable.ic_xeniles21);
            }
            else if(dog.idCage == 22)
            {
                cageView.setImageResource(R.drawable.ic_xeniles22);
            }
            else if(dog.idCage == 23)
            {
                cageView.setImageResource(R.drawable.ic_xeniles23);
            }
            else if(dog.idCage == 24)
            {
                cageView.setImageResource(R.drawable.ic_patios1);
            }
            else if(dog.idCage == 25)
            {
                cageView.setImageResource(R.drawable.ic_patios2);
            }
            else if(dog.idCage == 26)
            {
                cageView.setImageResource(R.drawable.ic_patios3);
            }
            else if(dog.idCage == 27)
            {
                cageView.setImageResource(R.drawable.ic_patios4);
            }
            else if(dog.idCage == 28)
            {
                cageView.setImageResource(R.drawable.ic_patios5);
            }
            else if(dog.idCage == 29)
            {
                cageView.setImageResource(R.drawable.ic_patios6);
            }
            else if(dog.idCage == 30)
            {
                cageView.setImageResource(R.drawable.ic_patios7);
            }
            else if(dog.idCage == 31)
            {
                cageView.setImageResource(R.drawable.ic_patios8);
            }
            else if(dog.idCage == 32)
            {
                cageView.setImageResource(R.drawable.ic_quarentenes1);
            }
            else if(dog.idCage == 33)
            {
                cageView.setImageResource(R.drawable.ic_quarentenes2);
            }
            else if(dog.idCage == 34)
            {
                cageView.setImageResource(R.drawable.ic_quarentenes3);
            }
            else if(dog.idCage == 35)
            {
                cageView.setImageResource(R.drawable.ic_quarentenes4);
            }
            else if(dog.idCage == 36)
            {
                cageView.setImageResource(R.drawable.ic_quarentenes5);
            }
            else if(dog.idCage == 37)
            {
                cageView.setImageResource(R.drawable.ic_quarentenes6);
            }
            else if(dog.idCage == 38)
            {
                cageView.setImageResource(R.drawable.ic_quarentenes7);
            }
            else if(dog.idCage == 39)
            {
                cageView.setImageResource(R.drawable.ic_quarentenes8);
            }
            else if(dog.idCage == 40)
            {
                cageView.setImageResource(R.drawable.ic_quarentenes9);
            }
            else if(dog.idCage == 41)
            {
                cageView.setImageResource(R.drawable.ic_quarentenes10);
            }
            else if(dog.idCage == 42)
            {
                cageView.setImageResource(R.drawable.ic_quarentenes11);
            }
            else if(dog.idCage == 43)
            {
                cageView.setImageResource(R.drawable.ic_quarentenes12);
            }
            else
            {
                cageView.setImageResource(R.color.colorGray);
            }
        }
        else
        {
            cageView.setImageResource(R.color.colorGray);
        }
    }
}
