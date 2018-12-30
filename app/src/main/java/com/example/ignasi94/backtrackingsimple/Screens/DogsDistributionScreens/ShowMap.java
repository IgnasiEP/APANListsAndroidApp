package com.example.ignasi94.backtrackingsimple.Screens.DogsDistributionScreens;

import android.app.Activity;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.ignasi94.backtrackingsimple.R;
import com.example.ignasi94.backtrackingsimple.Utils.Constants;

public class ShowMap extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.distribution_show_map);

        String zone = getIntent().getStringExtra("ZONE");
        ConstraintLayout mainLayout = (ConstraintLayout ) findViewById(R.id.main_layout);

        if(zone != null && zone.equals(Constants.CAGE_ZONE_XENILES))
        {
            mainLayout.setBackgroundResource(R.drawable.ic_xeniles);
        }
        else if(zone != null && zone.equals(Constants.CAGE_ZONE_PATIOS))
        {
            mainLayout.setBackgroundResource(R.drawable.ic_patios);
        }
        else if(zone != null && zone.equals(Constants.CAGE_ZONE_CUARENTENAS))
        {
            mainLayout.setBackgroundResource(R.drawable.ic_quarentenes);
        }
    }
}
