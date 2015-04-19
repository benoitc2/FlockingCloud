package edu.msu.wegschei.flocking;

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


public class WaitingActivity extends ActionBarActivity {

    public final static String PLAYER_ONE = "WaitingActivity.playerOne";

    public final static String USER = "WaitingActivity.user";

    /**
     * your username
     */
    private String userId;

    /**
     * player one name
     */
    private String playerOne;

    /**
     * player two name
     */
    private String playerTwo;

    private WaitingActivity wa;

    volatile boolean flag = true;

    private Thread matchmakingThread;

    private boolean failed = false;

    private boolean onExit = false;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_waiting);
        wa = this;
        Bundle extras = getIntent().getExtras();
        playerOne = extras.getString(PLAYER_ONE);

        //This class is where we check with the server to see if there is a player 2 found.
        //If there is, then this class takes in the name of the new player and exits to the
        //starting game screen, with both names passed into it.

        if(savedInstanceState != null) {
            this.loadInstanceState(savedInstanceState);
        }

        final View view = findViewById(android.R.id.content);

        handler = new Handler();

        // Create a thread to load the catalog
        matchmakingThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    final FlockCloud cloud = new FlockCloud();

                    while (flag) {
                        Log.d("Waiting Activity", "Inside Thread");

                        InputStream stream2 = cloud.checkIfPlayerWaiting(playerOne);
                        failed = stream2 == null;
                        if (!failed) {
                            try {
                                XmlPullParser xml2 = Xml.newPullParser();
                                xml2.setInput(stream2, "UTF-8");

                                xml2.nextTag();      // Advance to first tag
                                xml2.require(XmlPullParser.START_TAG, null, "flocking");
                                String matchStatus = xml2.getAttributeValue(null, "status");

                                if (matchStatus.equals("found")) {
                                    // player 1 and player 2 are returned here
                                    playerOne = xml2.getAttributeValue(null, "p1");
                                    playerTwo = xml2.getAttributeValue(null, "p2");
                                    flag = false;
                                }
                            } catch (IOException ex) {
                                failed = true;
                            } catch (XmlPullParserException ex) {
                                failed = true;
                            } finally {
                                try {
                                    stream2.close();
                                } catch (IOException ex) {
                                }
                            }
                        }

                        if (flag) Thread.sleep(6000);

                    }

                    matchmakingThread.interrupt();

                    if (Thread.interrupted() && !onExit) {

                        // if a game was found, set things up and send this player to it
                        if (playerOne != null) {

                            Intent intent = new Intent(wa, GameActivity.class);
                            intent.putExtra(GameActivity.YOU_START, "NO");
                            intent.putExtra(GameActivity.PLAYER_ONE, playerOne);
                            intent.putExtra(GameActivity.PLAYER_TWO, playerTwo);

                            wa.startActivity(intent);

                        }
                    }
                } catch (InterruptedException e) {
                    Log.d("UH OH", "BRO");
                }

            }
        });

        matchmakingThread.start();

    }

    void loadInstanceState(Bundle bundle) {
        userId = bundle.getString(USER);
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

    @Override
    public void onBackPressed() {
        if (matchmakingThread.isAlive()) {
            flag = false;
            onExit = true;
            Log.d("Killed thread", "Killed thread");
        }
        super.onBackPressed();
    }

    //This class is where we check with the server to see if there is a player 2 found.
    //If there is, then this class takes in the name of the new player and exits to the
    //starting game screen, with both names passed into it.
}