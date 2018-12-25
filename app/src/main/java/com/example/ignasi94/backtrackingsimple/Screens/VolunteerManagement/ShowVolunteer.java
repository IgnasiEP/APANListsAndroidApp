package com.example.ignasi94.backtrackingsimple.Screens.VolunteerManagement;

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
import com.example.ignasi94.backtrackingsimple.Estructuras.Volunteer;
import com.example.ignasi94.backtrackingsimple.R;
import com.example.ignasi94.backtrackingsimple.Screens.VolunteerManagement.EditVolunteer;
import com.example.ignasi94.backtrackingsimple.Screens.VolunteerManagement.ShowVolunteer;
import com.example.ignasi94.backtrackingsimple.Utils.Constants;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

public class ShowVolunteer extends Activity {

    public DBAdapter dbAdapter;
    public Volunteer volunteer;
    public Integer maxId;
    public Integer minId;
    public TextView volunteerNameText;
    public TextView phoneText;
    public TextView observationsText;
    public Spinner spinner;
    public Dictionary<Integer,Volunteer> volunteers;
    public Integer volunteerId;
    public TextView tipoPaseoText;
    public TextView volunteerObservations;
    public GridView gridView;
    public DogAdapter dogAdapter;
    public Button editButton;
    public View divider;
    public ImageView volunteerImage;
    public Integer visibilityInfo = 0;
    public ImageButton lastVolunteerButton;
    public ImageButton nextVolunteerButton;
    public Button datosButton;
    public Button jaulaButton;
    public Button showVolunteerFriendsButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.volunteer_management_volunteer_show);

        volunteerId = getIntent().getIntExtra("VOLUNTEERID", -1);

        dbAdapter = new DBAdapter(this);

        volunteers = dbAdapter.getAllVolunteersDictionary();
        maxId = dbAdapter.GetMaxIdVolunteersTable();
        minId = dbAdapter.GetMinIdVolunteersTable();

        volunteerNameText = (TextView) findViewById(R.id.volunteer_name);
        phoneText = (TextView) findViewById(R.id.volunteer_tlf);
        observationsText = (TextView) findViewById(R.id.volunteer_observations);
        spinner = (Spinner) findViewById(R.id.day_spinner);
        String[] letra = {"Sábado","Domingo"};
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra));
        spinner.setEnabled(false);
        volunteerObservations = (TextView) findViewById(R.id.textView9);
        divider = (View) findViewById(R.id.view2);
        volunteerImage = (ImageView) findViewById(R.id.volunteer_image);

        UpdateView();

        lastVolunteerButton = (ImageButton) findViewById(R.id.last_volunteer_button);
        lastVolunteerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newVolunteerId = volunteerId - 1;
                while(true)
                {
                    volunteer = volunteers.get(newVolunteerId);
                    if(volunteer != null)
                    {
                        volunteerId = newVolunteerId;
                        break;
                    }

                    if(newVolunteerId - 1 < minId) {
                        newVolunteerId = maxId;
                    }
                    else
                    {
                        newVolunteerId--;
                    }

                }

                UpdateView();
            }
        });

        nextVolunteerButton = (ImageButton) findViewById(R.id.next_volunteer_button);
        nextVolunteerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newVolunteerId = volunteerId + 1;
                while(true)
                {
                    volunteer = volunteers.get(newVolunteerId);
                    if(volunteer != null)
                    {
                        volunteerId = newVolunteerId;
                        break;
                    }

                    if(newVolunteerId + 1 > maxId) {
                        newVolunteerId = minId;
                    }
                    else
                    {
                        newVolunteerId++;
                    }
                }

                UpdateView();
            }
        });


        editButton = (Button) findViewById(R.id.button_edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchactivity= new Intent(ShowVolunteer.this,EditVolunteer.class);
                launchactivity.putExtra("VOLUNTEERID", volunteerId);
                startActivity(launchactivity);
            }
        });


    }

    public void UpdateView()
    {
        volunteer = volunteers.get(volunteerId);

        if(volunteer.name == null) {
            this.volunteerNameText.setText("*Nombre voluntario*");
            this.volunteerNameText.setTextColor(Color.BLACK);
        }
        else {
            this.volunteerNameText.setText(volunteer.name.toString());
            this.volunteerNameText.setTextColor(Color.BLACK);
        }

        this.phoneText.setText(volunteer.phone);

        if(volunteer.volunteerDay.equals(Constants.VOLUNTEER_DAY_SATURDAY))
        {
            this.spinner.setSelection(0);
        }
        else
        {
            this.spinner.setSelection(1);
        }

        gridView = (GridView) findViewById(R.id.grid_dog_lists);
        gridView.setNumColumns(5);
        // Adapter
        dogAdapter = new DogAdapter(getApplicationContext(), volunteer.favouriteDogs);
        gridView.setAdapter(dogAdapter);

        gridView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
        volunteers = dbAdapter.getAllVolunteersDictionary();
        if(volunteers.get(volunteerId) == null)
        {
            finish();
        }
        else
        {
            UpdateView();
        }
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

