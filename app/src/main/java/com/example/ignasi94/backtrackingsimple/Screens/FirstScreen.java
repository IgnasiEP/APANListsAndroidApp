package com.example.ignasi94.backtrackingsimple.Screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.ignasi94.backtrackingsimple.BBDD.DBAdapter;
import com.example.ignasi94.backtrackingsimple.R;
import com.example.ignasi94.backtrackingsimple.Screens.DogsDistributionScreens.XenilesDistribution;
import com.example.ignasi94.backtrackingsimple.Screens.ListsScreens.ListsScreen;

public class FirstScreen extends Activity {

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
                Intent launchactivity= new Intent(FirstScreen.this,XenilesDistribution.class);
                startActivity(launchactivity);
            }
        });
    }
}
