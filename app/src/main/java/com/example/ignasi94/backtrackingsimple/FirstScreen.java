package com.example.ignasi94.backtrackingsimple;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import com.example.ignasi94.backtrackingsimple.BBDD.DBAdapter;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerWalks;

import java.util.ArrayList;

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
    }
}
