package edu.msu.wegschei.flocking;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class GameActivity extends ActionBarActivity {

    /**
     * The game view in this activity's view
     */
    private GameView gameView;
    private TextView textView;
    private Button placeButton;

    public final static String PLAYER_ONE = "GameActivity.playerOne";
    public final static String PLAYER_TWO = "GameActivity.playerTwo";
    public final static String ORDER = "GameActivity.order";
    public final static String COUNTER = "GameActivity.counter";
    public final static String MESSAGE_TEXT = "GameActivity.messageText";
    public final static String BUTTON_TEXT = "GameActivity.buttonText";
    public final static String YOU_START = "GameActivity.youStart";

    private final static int BIRD_SELECTION = 1;
    private final static int RESPONSE_FROM_SERVER = 2;

    private String playerNameOne;
    private String playerNameTwo;
    private ArrayList<String> players = new ArrayList<>();
    private int counter = 0;

    private GameActivity ga;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_game);

        ga = this;
        gameView = (GameView)this.findViewById(R.id.gameView);
        placeButton = (Button) findViewById(R.id.buttonPlace);
        textView = (TextView)findViewById(R.id.textPlayer);

        //Gets the players' names that came from MainActivity, stores them in variables here
        Bundle extras = getIntent().getExtras();
        playerNameOne = extras.getString(PLAYER_ONE);
        players.add(playerNameOne);
        playerNameTwo = extras.getString(PLAYER_TWO);
        players.add(playerNameTwo);
        gameView.setNames(playerNameOne, playerNameTwo);

        if(bundle != null) {
            gameView.loadInstanceState(bundle);
        } else {
            gameView.advanceGame(-1);
        }
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
                        cloud.invalidateUser(playerNameOne);
                        finish();
                    }
                }).start();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void onPlace(View view) {
        Game.State originalState = gameView.getState();
        if(originalState == Game.State.PLAYER_ONE_WON || originalState == Game.State.PLAYER_TWO_WON) {
            gameView.end();
        } else {
            // post the bird's current location to the server
            gameView.onPlace();

            // Create a new thread to post a bird placement
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FlockCloud cloud = new FlockCloud();

                    // Get the last bird in the bird list (The bird player just placed!)
                    Bird pb = gameView.getGame().birds.get(gameView.getGame().birds.size()-1);

                    cloud.postBird(playerNameOne, pb.getX(), pb.getY(), pb.getId());

                    // Move to NotYourTurnActivity and wait for player 2!
                    Intent intent = new Intent(ga, NotYourTurnActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    ga.startActivityForResult(intent, 2);
                }
            }).start();
            Game.State newState = gameView.getState();
            if(newState ==  Game.State.PLAYER_ONE_WON) {
                textView.setText(playerNameOne + " wins!");
                placeButton.setText("Continue");
            } else if (newState ==  Game.State.PLAYER_TWO_WON) {
                textView.setText(playerNameTwo + " wins!");
                placeButton.setText("Continue");
            }
            /*
            counter--;
            gameView.onPlace();

            if (counter == 1) {
                textView.setText(players.get(counter) + ": Place your bird!");
                view.invalidate();
            }
            gameView.invalidate();

            if (counter == 0) {
                Collections.reverse(players);
            }

            Game.State newState = gameView.getState();
            if(newState ==  Game.State.PLAYER_ONE_WON) {
                textView.setText(playerNameOne + " wins!");
                placeButton.setText("Continue");
            } else if (newState ==  Game.State.PLAYER_TWO_WON) {
                textView.setText(playerNameTwo + " wins!");
                placeButton.setText("Continue");
            }
            */
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        bundle.putString(MESSAGE_TEXT, textView.getText().toString());
        bundle.putString(BUTTON_TEXT, placeButton.getText().toString());
        bundle.putStringArrayList(ORDER, players);
        bundle.putInt(COUNTER, counter);
        super.onSaveInstanceState(bundle);
        gameView.saveInstanceState(bundle);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        textView.setText(savedInstanceState.getString(MESSAGE_TEXT));
        placeButton.setText(savedInstanceState.getString(BUTTON_TEXT));
        players = savedInstanceState.getStringArrayList(ORDER);
        counter = savedInstanceState.getInt(COUNTER);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == BIRD_SELECTION && resultCode == Activity.RESULT_OK){
            Bundle extras = data.getExtras();
            int birdID = extras.getInt("BirdImageID");
            //counter++;
            /*
            if(counter == 2){
                textView.setText(players.get(0) + ": Place your bird!");
            }
            */
            textView.setText(players.get(0) + ": Place your bird!");
            gameView.advanceGame(birdID);
        }

        if(requestCode == RESPONSE_FROM_SERVER && resultCode == Activity.RESULT_OK) {
            // server returned a response with player 2 info
        }
    }
}