package edu.msu.wegschei.flocking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Chris on 4/16/2015.
 */
public class NotYourTurnActivity  extends ActionBarActivity {

    public final static String PLAYER_ONE = "NotYourTurnActivity.playerOne";
    public final static String PLAYER_TWO = "NotYourTurnActivity.playerTwo";

    public ArrayList<Bird> birds = new ArrayList<>();

    /**
     * your username
     */
    private String userId;

    private NotYourTurnActivity nyta;

    volatile boolean flag = true;

    private Thread waitingThread;

    private boolean failed = false;

    private boolean onExit = false;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_not_your_turn);
        nyta = this;
        Bundle extras = getIntent().getExtras();
        if(extras.getString(PLAYER_ONE) != null) {
            userId = extras.getString(PLAYER_ONE);
        }else {
            userId = extras.getString(PLAYER_TWO);
        }

        //This class is where we check with the server to see if it is this player's turn after waiting
        //for the other player to finish their turn.

        final View view = findViewById(android.R.id.content);

        handler = new Handler();

        // Create a thread to load the catalog
        waitingThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    final FlockCloud cloud = new FlockCloud();

                    while (flag) {
                        Log.d("Not Your Turn Activity", "Inside Thread");

                        //change function call to checkifyourturn, that checks if it's your turn in the server
                        InputStream stream = cloud.checkIfPlayerWaiting(userId);
                        failed = stream == null;
                        if (!failed) {
                            try {
                                XmlPullParser xml2 = Xml.newPullParser();
                                xml2.setInput(stream, "UTF-8");

                                xml2.nextTag();      // Advance to first tag
                                xml2.require(XmlPullParser.START_TAG, null, "flocking");
                                String matchStatus = xml2.getAttributeValue(null, "status");

                                if (matchStatus.equals("yes")) {
                                    // it is now your turn, walk through all fo the returned birds and create the array
                                    // of birds from them

                                    while(true) {
                                        xml2.nextTag();      // Advance to next tag

                                        birds.clear();
                                        Bird bird = new Bird(nyta.getApplicationContext(),
                                                             Integer.parseInt(xml2.getAttributeValue(null, "birdId")));
                                        bird.setX(Integer.parseInt(xml2.getAttributeValue(null, "x")));
                                        bird.setY(Integer.parseInt(xml2.getAttributeValue(null, "y")));
                                        if(bird == null) { break; }
                                        birds.add(bird);
                                    }
                                    flag = false;
                                }
                            } catch (IOException ex) {
                                failed = true;
                            } catch (XmlPullParserException ex) {
                                failed = true;
                            } finally {
                                try {
                                    stream.close();
                                } catch (IOException ex) {
                                }
                            }
                        }

                        if (flag) Thread.sleep(6000);

                    }

                    waitingThread.interrupt();

                    if (Thread.interrupted() && !onExit) {

                        //If it's your turn now, send the rest of the tags to the game class to set up the
                        //placed birds
                        Intent intent = new Intent(nyta, GameActivity.class);

                        //send the created arrayList of birds into the game to be put on the game board at this point

                        nyta.startActivity(intent);
                    }
                } catch (InterruptedException e) {
                    Log.d("UH OH", "BRO");
                }

            }
        });

        waitingThread.start();

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
                        cloud.invalidateUser(userId);
                        Intent intent = new Intent(nyta, LoginActivity.class);
                        nyta.finish();
                    }
                }).start();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        if (waitingThread.isAlive()) {
            flag = false;
            onExit = true;
            Log.d("Killed thread", "Killed thread");
        }
        super.onBackPressed();
    }
}
