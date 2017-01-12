package uk.ac.uea.roomfinder.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import uk.ac.uea.roomfinder.R;

public class DetailsActivity extends AppCompatActivity {

   TextView roomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_details);
        roomName = (TextView)findViewById(R.id.name_tv);

        /* Get intent and use passed data */
        long id = (long)getIntent().getExtras().get("id");
        roomName.setText("Location " + id);
    }

    public void mapActivity(View view) {
        Intent i = new Intent(this, MapActivity.class);
        startActivity(i);
    }

}