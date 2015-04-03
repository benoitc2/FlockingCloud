package edu.msu.wegschei.flocking;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class ScoreActivity extends ActionBarActivity {

    private final static String SCORE = "ScoreActivity.score";
    private final static String WINNER = "ScoreActivity.winner";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int birdsPlaced;
        String winner;

        TextView textWinner;
        TextView textBirdsPlaced;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        Bundle extras = getIntent().getExtras();
        winner = extras.getString(WINNER);
        birdsPlaced = extras.getInt(SCORE);

        textWinner = (TextView)findViewById(R.id.textWinner);
        textWinner.setText(winner);

        textBirdsPlaced = (TextView)findViewById(R.id.textBirdsPlaced);
        textBirdsPlaced.setText("Birds placed: " + birdsPlaced);
    }

    public void onReset(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
