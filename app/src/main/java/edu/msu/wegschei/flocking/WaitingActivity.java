package edu.msu.wegschei.flocking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class WaitingActivity extends ActionBarActivity {

    private String playerOne;
    public final static String PLAYER_ONE = "WaitingActivity.playerOne";

    WaitingActivity wa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);
        wa = this;
        Bundle extras = getIntent().getExtras();
        playerOne = extras.getString(PLAYER_ONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.quit:
                // Create a new thread to invalidate a user
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FlockCloud cloud = new FlockCloud();
                        cloud.invalidateUser(playerOne);
                        Intent intent = new Intent(wa, LoginActivity.class);
                        wa.startActivity(intent);
                        wa.finish();
                    }
                }).start();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    //This class is where we check with the server to see if there is a player 2 found.
    //If there is, then this class takes in the name of the new player and exits to the
    //starting game screen, with both names passed into it.
}