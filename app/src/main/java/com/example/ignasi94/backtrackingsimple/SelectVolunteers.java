package com.example.ignasi94.backtrackingsimple;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Filter;
import android.widget.Filterable;

import com.example.ignasi94.backtrackingsimple.BBDD.DBAdapter;
import com.example.ignasi94.backtrackingsimple.Estructuras.Dog;
import com.example.ignasi94.backtrackingsimple.Estructuras.Volunteer;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerDog;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerWalks;
import com.example.ignasi94.backtrackingsimple.Utils.Constants;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

public class SelectVolunteers extends Activity {

    DBAdapter dbAdapter;
    VolunteerAdapter allVolunteersAdapter;
    VolunteerAdapter selectedVolunteersAdapter;
    GridView allVolunteersGrid;
    GridView selectedVolunteersGrid;
    List<Volunteer> listFullVolunteers;
    List<VolunteerWalks> listFullVolunteersWalks;
    List<VolunteerWalks> listSelectedVolunteersWalks;
    Button anyDay;
    Button saturday;
    Button sunday;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_volunteers);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        boolean newConfig= getIntent().getBooleanExtra("NEW", true);
        dbAdapter = new DBAdapter(this);
        listFullVolunteers = dbAdapter.getAllVolunteers();
        Dictionary<Integer,Volunteer> volunteersDict = dbAdapter.getAllVolunteersDictionary();

        this.InitializeGridArrays(newConfig);

        //AllVolunteersGRID
        allVolunteersGrid = (GridView) findViewById(R.id.grid_all_volunteers);
        allVolunteersGrid.setNumColumns(1);
        // Adapter
        allVolunteersAdapter = new VolunteerAdapter(getApplicationContext(), (ArrayList) listFullVolunteersWalks, true);
        allVolunteersGrid.setAdapter(allVolunteersAdapter);

        //SelectedVolunteersGRID
        selectedVolunteersGrid = (GridView) findViewById(R.id.grid_selected_volunteers);
        selectedVolunteersGrid.setNumColumns(1);
        // Adapter
        selectedVolunteersAdapter = new VolunteerAdapter(getApplicationContext(), (ArrayList) listSelectedVolunteersWalks, false);
        selectedVolunteersGrid.setAdapter(selectedVolunteersAdapter);

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
                allVolunteersAdapter.getFilter().filter(newText);

                return false;
            }
        });

        anyDay = (Button) findViewById(R.id.android_gridview_any_day_button);
        saturday = (Button) findViewById(R.id.android_gridview_saturday_button);
        sunday = (Button) findViewById(R.id.android_gridview_sunday_button);
        anyDay.setVisibility(View.VISIBLE);
        saturday.setVisibility(View.GONE);
        sunday.setVisibility(View.GONE);

        anyDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anyDay.setVisibility(View.GONE);
                saturday.setVisibility(View.VISIBLE);
                ArrayList<VolunteerWalks> saturdayVolunteers = new ArrayList<>();
                ArrayList<VolunteerWalks> allMatrixList = (ArrayList<VolunteerWalks>) allVolunteersAdapter.allmatrixList.clone();
                for(int i = 0; i < allMatrixList.size(); ++i)
                {
                    VolunteerWalks volunteer = allMatrixList.get(i);
                    if(volunteer.volunteerDay.equals(Constants.VOLUNTEER_DAY_SATURDAY))
                    {
                        saturdayVolunteers.add(volunteer);
                    }
                }
                allVolunteersAdapter.matrixList = saturdayVolunteers;
                allVolunteersAdapter.notifyDataSetChanged();
                allVolunteersGrid.invalidateViews();
            }
        });

        saturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saturday.setVisibility(View.GONE);
                sunday.setVisibility(View.VISIBLE);
                ArrayList<VolunteerWalks> sundayVolunteers = new ArrayList<>();
                ArrayList<VolunteerWalks> allMatrixList = (ArrayList<VolunteerWalks>) allVolunteersAdapter.allmatrixList.clone();
                for(int i = 0; i < allMatrixList.size(); ++i)
                {
                    VolunteerWalks volunteer = allMatrixList.get(i);
                    if(volunteer.volunteerDay.equals(Constants.VOLUNTEER_DAY_SUNDAY))
                    {
                        sundayVolunteers.add(volunteer);
                    }
                }
                allVolunteersAdapter.matrixList = sundayVolunteers;
                allVolunteersAdapter.notifyDataSetChanged();
                allVolunteersGrid.invalidateViews();
            }
        });

        sunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sunday.setVisibility(View.GONE);
                anyDay.setVisibility(View.VISIBLE);
                allVolunteersAdapter.matrixList = (ArrayList<VolunteerWalks>) allVolunteersAdapter.allmatrixList.clone();
                allVolunteersAdapter.notifyDataSetChanged();
                allVolunteersGrid.invalidateViews();
            }
        });

        Button configureWalksButton = (Button) findViewById(R.id.button_siguiente);
        configureWalksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbAdapter.SaveSelectedVolunteers(selectedVolunteersAdapter.matrixList);
                Intent launchactivity= new Intent(SelectVolunteers.this,ConfigureWalks.class);
                launchactivity.putExtra("NEW", newConfig);
                startActivity(launchactivity);
            }
        });
    }

    private void InitializeGridArrays(boolean newConfig)
    {
        if(newConfig) {
            dbAdapter.CleanSelectedVolunteers();
        }

        //always
        listFullVolunteersWalks = new ArrayList<VolunteerWalks>();
        listSelectedVolunteersWalks = new ArrayList<VolunteerWalks>();

        for (int i = 0; i < listFullVolunteers.size(); ++i) {
            Volunteer volunteer = listFullVolunteers.get(i);
            VolunteerWalks volunteerWalk = new VolunteerWalks(volunteer.id,volunteer.name,0,0,0,0,0,0,0);
            volunteerWalk.name = volunteer.name;
            volunteerWalk.volunteerDay = volunteer.volunteerDay;
            listFullVolunteersWalks.add(volunteerWalk);
        }

        if(!newConfig) {
            listSelectedVolunteersWalks = dbAdapter.getAllSelectedVolunteers();
            for (int i = 0; i < listSelectedVolunteersWalks.size(); ++i) {
                VolunteerWalks volunteer = listSelectedVolunteersWalks.get(i);
                for(int j = 0; j < listFullVolunteersWalks.size(); ++j)
                {
                    VolunteerWalks jVolunteer = listFullVolunteersWalks.get(j);
                    if(volunteer.id == jVolunteer.id)
                    {
                        listSelectedVolunteersWalks.get(i).name = jVolunteer.name;
                        listFullVolunteersWalks.remove(j);
                        break;
                    }

                }
            }
        }
    }

    public class VolunteerAdapter extends BaseAdapter implements Filterable {
            Context context;
            ArrayList<VolunteerWalks> matrixList;
            ArrayList<VolunteerWalks> allmatrixList;
            boolean allList;
            ValueFilter valueFilter;

            public VolunteerAdapter(Context context, ArrayList<VolunteerWalks> matrixList, boolean allList) {
            this.context = context;
            this.matrixList = matrixList;
            this.allmatrixList = (ArrayList<VolunteerWalks>) matrixList.clone();
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
                gridViewAndroid = inflater.inflate(R.layout.griditem_select_volunteers, null);
            }
            TextView textViewAndroid = (TextView) gridViewAndroid.findViewById(R.id.android_gridview_text);
            ImageView imageViewAndroid = (ImageView) gridViewAndroid.findViewById(R.id.android_gridview_image);
            ImageButton buttonViewAndroid = (ImageButton) gridViewAndroid.findViewById(R.id.android_gridview_button);

            textViewAndroid.setText(this.matrixList.get(position).name);
            imageViewAndroid.setImageResource(R.mipmap.ic_volunteer_default);

            if(this.allList) {
                buttonViewAndroid.setImageResource(R.mipmap.ic_flecha_arriba);
            }
            else {
                buttonViewAndroid.setImageResource(R.mipmap.ic_flecha_abajo);
            }
            buttonViewAndroid.setBackgroundColor(Color.parseColor("#FAFAFA"));
            buttonViewAndroid.setOnClickListener(new MoveVolunteersOnClickListener(position, this.allList));

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
                ArrayList<Volunteer> allMatrix = (ArrayList<Volunteer>) allmatrixList.clone();
                if (constraint != null && constraint.length() > 0) {
                    List<Volunteer> filterList = new ArrayList<>();
                    for (int i = 0; i < allMatrix.size(); i++) {
                        if ((allMatrix.get(i).name.toUpperCase()).contains(constraint.toString().toUpperCase()) && matrixList.contains(allMatrix.get(i))) {
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
                matrixList = (ArrayList<VolunteerWalks>) results.values;
                notifyDataSetChanged();
            }


        }
    }

    public class MoveVolunteersOnClickListener implements OnClickListener
    {
        int position;
        boolean allList;
        public MoveVolunteersOnClickListener(int position, boolean allList) {
            this.position = position;
            this.allList = allList;
        }

        @Override
        public void onClick(View v)
        {
            VolunteerWalks volunteer;
            if(allList)
            {
                volunteer = allVolunteersAdapter.matrixList.get(position);
                allVolunteersAdapter.matrixList.remove(position);
                for(int i = 0; i < allVolunteersAdapter.allmatrixList.size(); ++i)
                {
                    if(allVolunteersAdapter.allmatrixList.get(i).name == volunteer.name)
                    {
                        allVolunteersAdapter.allmatrixList.remove(i);
                    }
                }
                selectedVolunteersAdapter.matrixList.add(volunteer);
            }
            else
            {
                volunteer = selectedVolunteersAdapter.matrixList.get(position);
                selectedVolunteersAdapter.matrixList.remove(position);
                if(saturday.getVisibility() == View.VISIBLE && volunteer.volunteerDay.equals(Constants.VOLUNTEER_DAY_SATURDAY))
                {
                    allVolunteersAdapter.matrixList.add(volunteer);
                }
                else if(sunday.getVisibility() == View.VISIBLE && volunteer.volunteerDay.equals(Constants.VOLUNTEER_DAY_SUNDAY))
                {
                    allVolunteersAdapter.matrixList.add(volunteer);
                }
                else if(anyDay.getVisibility() == View.VISIBLE)
                {
                    allVolunteersAdapter.matrixList.add(volunteer);
                }
                allVolunteersAdapter.allmatrixList.add(volunteer);
            }

            allVolunteersAdapter.notifyDataSetChanged();
            selectedVolunteersAdapter.notifyDataSetChanged();
            allVolunteersGrid.invalidateViews();
            selectedVolunteersGrid.invalidateViews();
        }

    };
}
