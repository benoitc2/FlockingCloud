package edu.msu.wegschei.flocking;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
    private EditText playerOne;
    private EditText playerTwo;

    private final static String PLAYER_ONE = "MainActivity.playerOne";
    private final static String PLAYER_TWO = "MainActivity.playerTwo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.playerOne = (EditText)findViewById(R.id.playerOne);
        this.playerTwo = (EditText)findViewById(R.id.playerTwo);

        if(savedInstanceState != null) {
            // We have saved state
            loadInstanceState(savedInstanceState);
        }
    }

    public void loadInstanceState(Bundle bundle) {
        String nameOne = bundle.getString(PLAYER_ONE);
        String nameTwo = bundle.getString(PLAYER_TWO);

        playerOne.setText(nameOne, TextView.BufferType.EDITABLE);
        playerTwo.setText(nameTwo, TextView.BufferType.EDITABLE);

    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        String nameOne = playerOne.getText().toString();
        String nameTwo = playerTwo.getText().toString();

        bundle.putString(PLAYER_ONE, nameOne);
        bundle.putString(PLAYER_TWO, nameTwo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_rules:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                // Parameterize the builder
                builder.setTitle(R.string.rules);
                builder.setMessage(R.string.rules_text);
                builder.setPositiveButton(android.R.string.ok, null);

                // Create the dialog box and show it
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onStartGame(View view){
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(GameActivity.PLAYER_ONE, playerOne.getText().toString());
        intent.putExtra(GameActivity.PLAYER_TWO, playerTwo.getText().toString());

        startActivity(intent);
    }
}
