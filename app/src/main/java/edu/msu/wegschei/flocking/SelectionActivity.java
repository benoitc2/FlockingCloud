package edu.msu.wegschei.flocking;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SelectionActivity extends ActionBarActivity {

    private String NAME = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        Intent intent = getIntent();
        if(intent != null){
            TextView textView = (TextView)findViewById(R.id.Message);
            NAME = intent.getStringExtra("PLAYER_NAME");
            String newMessage = NAME + ": Please select your bird";
            textView.setText(newMessage);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        NAME = savedInstanceState.getString("PLAYER_NAME");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("PLAYER_NAME", NAME);
        super.onSaveInstanceState(outState);

    }

    public void imageClicked(View view){
        //Convert the view to an ImageView
        ImageView imageView = (ImageView)view;
        //Get the contentDescription from the image
        String imageContentDescription = imageView.getContentDescription().toString();
        //Using that content description compare it to the strings.xml values

        //Create the intent
        Intent result = new Intent(this, GameActivity.class);
        if(imageContentDescription.equals(getString(R.string.robin))){
            result.putExtra("BirdImageID", R.drawable.robin);
        } else if(imageContentDescription.equals(getString(R.string.parrot))){
            result.putExtra("BirdImageID", R.drawable.parrot);
        } else if(imageContentDescription.equals(getString(R.string.swallow))){
            result.putExtra("BirdImageID", R.drawable.swallow);
        } else if(imageContentDescription.equals(getString(R.string.bananaquit))){
            result.putExtra("BirdImageID", R.drawable.bananaquit);
        } else if(imageContentDescription.equals(getString(R.string.ostrich))){
            result.putExtra("BirdImageID", R.drawable.ostrich);
        }
        //Fire it
        setResult(Activity.RESULT_OK, result);
        Toast.makeText(this, "Choice Received", Toast.LENGTH_SHORT).show();
        finish();
    }
}