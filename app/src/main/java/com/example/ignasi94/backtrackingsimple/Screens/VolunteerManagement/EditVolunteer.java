package com.example.ignasi94.backtrackingsimple.Screens.VolunteerManagement;


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
import com.example.ignasi94.backtrackingsimple.Estructuras.Volunteer;
import com.example.ignasi94.backtrackingsimple.Estructuras.Volunteer;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerWalks;
import com.example.ignasi94.backtrackingsimple.R;
import com.example.ignasi94.backtrackingsimple.Utils.Constants;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
public class EditVolunteer extends Activity {

    public DBAdapter dbAdapter;
    public Volunteer volunteer;
    public Integer maxId;
    public TextView titlevolunteerNameText;
    public TextView titlePhoneText;
    public TextView titleDayText;
    public EditText volunteerNameText;
    public EditText phoneText;
    public TextView observationsText;
    public Spinner spinner;
    public Dictionary<Integer,Volunteer> volunteers;
    public Integer volunteerId;
    public TextView volunteerObservations;
    public GridView selectedDogsGridView;
    public GridView allDogsGridView;
    public DogAdapter selectedDogsAdapter;
    public DogAdapter allDogsAdapter;
    public Button removeButton;
    public View divider;
    public ImageView volunteerImage;
    public Integer visibilityInfo = 0;
    public Button saveButton;
    public Button cancelButton;
    public Button showFavouriteDogsButton;
    public boolean newvolunteer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.volunteer_management_volunteer_edit);

        newvolunteer = getIntent().getBooleanExtra("NEW", false);
        volunteerId = getIntent().getIntExtra("VOLUNTEERID", -1);

        dbAdapter = new DBAdapter(this);

        volunteers = dbAdapter.getAllVolunteersDictionary();
        maxId = dbAdapter.GetMaxIdVolunteersTable();

        titlevolunteerNameText = (TextView) findViewById(R.id.textView4);
        titlePhoneText = (TextView) findViewById(R.id.textView5);
        titleDayText = (TextView) findViewById(R.id.textView6);
        volunteerNameText = (EditText) findViewById(R.id.volunteer_name);
        phoneText = (EditText) findViewById(R.id.volunteer_tlf);
        
        observationsText = (TextView) findViewById(R.id.volunteer_observations);
        spinner = (Spinner) findViewById(R.id.day_spinner2);
        String[] letra = {"Sábado","Domingo"};
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra));
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id)
            {
                String selected = (String) adapterView.getItemAtPosition(pos);
                if(selected.equals("Sábado"))
                {
                    volunteer.volunteerDay = Constants.VOLUNTEER_DAY_SATURDAY;
                }
                else
                {
                    volunteer.volunteerDay = Constants.VOLUNTEER_DAY_SUNDAY;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {    }
        });
        volunteerObservations = (TextView) findViewById(R.id.textView9);
        divider = (View) findViewById(R.id.view2);
        volunteerImage = (ImageView) findViewById(R.id.volunteer_image);

        UpdateView();

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
                allDogsAdapter.getFilter().filter(newText);

                return false;
            }
        });

        showFavouriteDogsButton = (Button) findViewById(R.id.show_dog_friends_button);
        showFavouriteDogsButton.setOnClickListener(new View.OnClickListener() {
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

                volunteerImage.setVisibility(View.INVISIBLE);
                titlevolunteerNameText.setVisibility(View.INVISIBLE);
                titlePhoneText.setVisibility(View.INVISIBLE);
                titleDayText.setVisibility(View.INVISIBLE);
                volunteerNameText.setVisibility(View.INVISIBLE);
                phoneText.setVisibility(View.INVISIBLE);
                spinner.setVisibility(View.INVISIBLE);
                removeButton.setVisibility(View.INVISIBLE);

                divider.setVisibility(View.INVISIBLE);

                showFavouriteDogsButton.setVisibility(View.INVISIBLE);
                volunteerObservations.setVisibility(View.INVISIBLE);
                observationsText.setVisibility(View.INVISIBLE);
                spinner.setVisibility(View.INVISIBLE);

                cancelButton.setVisibility(View.INVISIBLE);
                saveButton.setVisibility(View.INVISIBLE);

                selectedDogsGridView = (GridView) findViewById(R.id.grid_selected_dogs);
                selectedDogsGridView.setNumColumns(1);
                // Adapter
                selectedDogsAdapter = new DogAdapter(getApplicationContext(), volunteer.favouriteDogs, false);
                selectedDogsGridView.setAdapter(selectedDogsAdapter);
                selectedDogsGridView.setVisibility(View.VISIBLE);

                List<Dog> allDogs = dbAdapter.getAllDogs();
                if(!newvolunteer)
                {
                    for(int i = 0; i < allDogs.size(); ++i)
                    {
                        for(int j = 0; j < volunteer.favouriteDogs.size(); ++j)
                        {
                            if(allDogs.get(i).id == volunteer.favouriteDogs.get(j).id)
                            {
                                allDogs.remove(i);
                            }
                        }
                    }
                }
                allDogsGridView = (GridView) findViewById(R.id.grid_all_dogs);
                allDogsGridView.setNumColumns(1);
                // Adapter
                allDogsAdapter = new DogAdapter(getApplicationContext(), allDogs, true);
                allDogsGridView.setAdapter(allDogsAdapter);
                allDogsGridView.setVisibility(View.VISIBLE);
            }
        });


        removeButton = (Button) findViewById(R.id.button_remove);
        if(newvolunteer)
        {
            removeButton.setVisibility(View.INVISIBLE);
        }
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbAdapter.DeleteVolunteer(volunteer);
                finish();
            }
        });

        saveButton = (Button) findViewById(R.id.button_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volunteer.name = volunteerNameText.getText().toString();
                volunteer.phone = phoneText.getText().toString();
                volunteer.observations = observationsText.getText().toString();

                if(selectedDogsAdapter != null) {
                    volunteer.favouriteDogs = (ArrayList<Dog>) selectedDogsAdapter.matrixList;
                }
                dbAdapter.SaveOrUpdateVolunteer(volunteer);
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
        if(newvolunteer) {
            volunteer = new Volunteer("", null,"S",null);
        }
        else {
            volunteer = volunteers.get(volunteerId);
        }

        if (volunteer.name == null) {
            this.volunteerNameText.setText("*Nombre perro*");
            this.volunteerNameText.setTextColor(Color.BLACK);
        } else {
            this.volunteerNameText.setText(volunteer.name.toString());
            this.volunteerNameText.setTextColor(Color.BLACK);
        }

        this.phoneText.setText(volunteer.phone);

        this.observationsText.setText(volunteer.observations);

        if (volunteer.volunteerDay == Constants.VOLUNTEER_DAY_SATURDAY) {
            this.spinner.setSelection(0);
        } else if (volunteer.volunteerDay == Constants.VOLUNTEER_DAY_SUNDAY) {
            this.spinner.setSelection(1);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && volunteerImage.getVisibility() != View.VISIBLE) {
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

            volunteerImage.setVisibility(View.VISIBLE);
            if(!newvolunteer) {
                removeButton.setVisibility(View.VISIBLE);
            }
            titlevolunteerNameText.setVisibility(View.VISIBLE);
            titlePhoneText.setVisibility(View.VISIBLE);
            titleDayText.setVisibility(View.VISIBLE);
            volunteerNameText.setVisibility(View.VISIBLE);
            phoneText.setVisibility(View.VISIBLE);

            showFavouriteDogsButton.setVisibility(View.VISIBLE);
            volunteerObservations.setVisibility(View.VISIBLE);
            observationsText.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);

            cancelButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);

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
                dog = allDogsAdapter.matrixList.get(position);
                allDogsAdapter.matrixList.remove(position);
                for(int i = 0; i < allDogsAdapter.allmatrixList.size(); ++i)
                {
                    if(allDogsAdapter.allmatrixList.get(i).name == dog.name)
                    {
                        allDogsAdapter.allmatrixList.remove(i);
                    }
                }
                selectedDogsAdapter.matrixList.add(dog);
            }
            else
            {
                dog = selectedDogsAdapter.matrixList.get(position);
                selectedDogsAdapter.matrixList.remove(position);
                allDogsAdapter.matrixList.add(dog);
                allDogsAdapter.allmatrixList.add(dog);
            }

            allDogsAdapter.notifyDataSetChanged();
            selectedDogsAdapter.notifyDataSetChanged();
            allDogsGridView.invalidateViews();
            selectedDogsGridView.invalidateViews();
        }

    };
}