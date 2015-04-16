package edu.msu.wegschei.flocking;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;


public class WaitingActivity extends ActionBarActivity {

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

    private WaitingActivity waitingAct;

    volatile boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);

        waitingAct = this;
        //This class is where we check with the server to see if there is a player 2 found.
        //If there is, then this class takes in the name of the new player and exits to the
        //starting game screen, with both names passed into it.

        if(savedInstanceState != null) {
            this.loadInstanceState(savedInstanceState);
        }

        final View view = this.getWindow().getDecorView().getRootView();

        // Create a thread to load the catalog
        new Thread(new Runnable() {

            @Override
            public void run() {
                final FlockCloud cloud = new FlockCloud();

                while (flag) {
                    Log.d("Waiting Activity", "Inside Thread");

                    InputStream stream2 = cloud.checkIfPlayerWaiting(userId);
                    boolean failed = stream2 == null;
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
                            } else {
                                // Try matchmaking url again in waiting activity
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

                    final boolean fail1 = failed;
                    boolean post = view.post(new Runnable() {

                        @Override
                        public void run() {
                            if (!fail1) {

                                // if a game was found, set things up and send this player to it
                                if (playerOne == null) {

                                    Intent intent = new Intent(waitingAct, GameActivity.class);
                                    intent.putExtra(GameActivity.YOU_START, "NO");
                                    intent.putExtra(GameActivity.PLAYER_ONE, playerOne);
                                    intent.putExtra(GameActivity.PLAYER_TWO, playerTwo);

                                    flag = false;
                                    waitingAct.startActivity(intent);
                                }
                            }
                        }
                    });
                    try {
                        Thread.sleep(6000);
                    } catch (Exception e) {

                    }

                }

            }
        }).start();





    }

    void loadInstanceState(Bundle bundle) {
        userId = bundle.getString(USER);
    }
}

