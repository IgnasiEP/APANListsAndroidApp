package com.example.ignasi94.backtrackingsimple.Screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.ignasi94.backtrackingsimple.BuildConfig;

import com.example.ignasi94.backtrackingsimple.BBDD.DBAdapter;
import com.example.ignasi94.backtrackingsimple.R;
import com.example.ignasi94.backtrackingsimple.Screens.DogManagement.DogList;
import com.example.ignasi94.backtrackingsimple.Screens.DogsDistributionScreens.DistributionOptionsScreen;
import com.example.ignasi94.backtrackingsimple.Screens.DogsDistributionScreens.ShowDistribution;
import com.example.ignasi94.backtrackingsimple.Screens.ListsScreens.ListsScreen;
import com.example.ignasi94.backtrackingsimple.Screens.VolunteerManagement.VolunteerList;

public class FirstScreen extends Activity {

    int versionCode = BuildConfig.VERSION_CODE;
    String versionName = BuildConfig.VERSION_NAME;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);

        DBAdapter dbAdapter = new DBAdapter(this);
        dbAdapter.onUpgrade();

        Button goListsButton = (Button) findViewById(R.id.button_listas);
        goListsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchactivity= new Intent(FirstScreen.this,ListsScreen.class);
                startActivity(launchactivity);
            }
        });

        Button goDistributionButton = (Button) findViewById(R.id.button_distribucion);
        goDistributionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchactivity= new Intent(FirstScreen.this,DistributionOptionsScreen.class);
                startActivity(launchactivity);
            }
        });

        Button goDogsManagementButton = (Button) findViewById(R.id.button_gestion_perros);
        goDogsManagementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchactivity= new Intent(FirstScreen.this,DogList.class);
                startActivity(launchactivity);
            }
        });

        Button goVolunteersManagementButton = (Button) findViewById(R.id.button_gestion_voluntarios);
        goVolunteersManagementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchactivity= new Intent(FirstScreen.this,VolunteerList.class);
                startActivity(launchactivity);
            }
        });
    }
}
