package com.example.ignasi94.backtrackingsimple.Screens.DogsDistributionScreens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.ignasi94.backtrackingsimple.BBDD.DBAdapter;
import com.example.ignasi94.backtrackingsimple.R;
import com.example.ignasi94.backtrackingsimple.Screens.FirstScreen;
import com.example.ignasi94.backtrackingsimple.Screens.ListsScreens.ListsScreen;

public class DistributionOptionsScreen extends Activity{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.distribution_options_screen);

        Button goShowDistributionButton = (Button) findViewById(R.id.button_show_distribucion);
        goShowDistributionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchactivity= new Intent(DistributionOptionsScreen.this,ShowDistribution.class);
                startActivity(launchactivity);
            }
        });

        Button goEditDistributionButton = (Button) findViewById(R.id.button_edit_distribucion);
        goEditDistributionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchactivity= new Intent(DistributionOptionsScreen.this,EditDistribution.class);
                startActivity(launchactivity);
            }
        });
    }
}
