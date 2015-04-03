package edu.msu.wegschei.flocking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * class to describe a Game
 */
public class  Game {

    /**
     * Paint for filling the area the game is in
     */
    private Paint fillPaint;

    /**
     * Paint for outlining the area the game is in
     */
    private Paint outlinePaint;

    /**
     * Collection of birds
     */
    public ArrayList<Bird> birds = new ArrayList<>();

    /**
     * The size of the game in pixels
     */
    private int gameSize;

    /**
     * Left margin in pixels
     */
    private int marginX;

    /**
     * Top margin in pixels
     */
    private int marginY;

    /**
     * Most recent relative X touch when dragging
     */
    private float lastRelX;

    /**
     * Most recent relative Y touch when dragging
     */
    private float lastRelY;

    /**
     * This variable is set to a bird we are dragging. If
     * we are not dragging, the variable is null.
     */
    private Bird dragging = null;

    private Bird next = null;

    /**
     * The view of the game
     */
    private Context parentContext = null;

    /**
     * An ostrich bitmap for scaling
     */
    private Bitmap ostrich;

    /**
     * A rectangle of the boundary of the play area
     */
    private Rect boundary;

    /**
     * Width of outline
     */
    final static int BRUSH_WIDTH = 6;

    /**
     * Percentage of the display width or height that is occupied by the game
     */
    final static float SCALE_IN_VIEW = 0.9f;

    /**
     * Ratio of board height to ostrich height
     */
    final static float OSTRICH_RATIO = 1.5f;

    /**
     * The name of the bundle keys to save the puzzle
     */
    private final static String LOCATIONS = "Game.locations";
    private final static String IDS = "Game.ids";
    private final static String DRAGGING_INDEX = "Game.draggingIndex";
    private final static String NEXT_ID = "Game.nextId";
    private final static String PLAYER_ONE = "Game.playerOne";
    private final static String PLAYER_TWO = "Game.playerTwo";
    private final static String STATE = "Game.state";
    private final static String ORDER = "Game.order";

    public enum State {
        START,
        PLAYER_ONE_SELECTING, PLAYER_TWO_SELECTING,
        PLAYER_ONE_PLACING, PLAYER_TWO_PLACING,
        PLAYER_ONE_WON, PLAYER_TWO_WON,
    }

    private State state = State.START;
    private boolean player1First = true;

    /**
     * The names of the players in the game.
     */
    private String playerOne;
    private String playerTwo;

    public Game(Context context, View parent) {
        parentContext = context;

        // Create paint for filling the area the puzzle will
        // be solved in.
        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setColor(0xffcccccc);

        outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outlinePaint.setColor(0xff008000);
        outlinePaint.setStrokeWidth(BRUSH_WIDTH);

        boundary = new Rect();

        // Load the solved puzzle image
        ostrich = BitmapFactory.decodeResource(context.getResources(), R.drawable.ostrich);
    }

    public void draw(Canvas canvas) {
        float scaleFactor;

        int wid = canvas.getWidth();
        int hit = canvas.getHeight();

        // Determine the minimum of the two dimensions
        int minDim = wid < hit ? wid : hit;

        gameSize = (int)(minDim * SCALE_IN_VIEW);

        // Compute the margins so we center the puzzle
        marginX = (wid - gameSize) / 2;
        marginY = (hit - gameSize) / 2;

        // Draw the outline of the puzzle
        int startX = marginX - BRUSH_WIDTH;
        int startY = marginY - BRUSH_WIDTH;
        int endX = marginX + gameSize + BRUSH_WIDTH;
        int endY = marginY + gameSize + BRUSH_WIDTH;
        canvas.drawRect(startX, startY, endX, endY, outlinePaint);

        boundary.set(marginX, marginY, marginX + gameSize, marginY + gameSize);
        canvas.drawRect(boundary, fillPaint);

        scaleFactor = (float)gameSize / (float)ostrich.getHeight() / OSTRICH_RATIO;

        for(Bird bird : birds) {
            bird.draw(canvas, marginX, marginY, gameSize, scaleFactor);
        }
    }

    /**
     * Handle a touch event from the view.
     * @param view The view that is the source of the touch
     * @param event The motion event describing the touch
     * @return true if the touch is handled.
     */
    public boolean onTouchEvent(View view, MotionEvent event) {
        //
        // Convert an x,y location to a relative location in the game.
        //

        float relX = (event.getX() - marginX) / gameSize;
        float relY = (event.getY() - marginY) / gameSize;

        switch(event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastRelX = relX;
                lastRelY = relY;
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                return true;

            case MotionEvent.ACTION_MOVE:
                // If we are dragging, move the piece and force a redraw
                if(dragging != null) {
                    dragging.move(relX - lastRelX, relY - lastRelY);
                    lastRelX = relX;
                    lastRelY = relY;
                    view.invalidate();
                    return true;
                }
        }

        return false;
    }

    public boolean canPlace() {
        boundary.set(marginX, marginY, marginX + gameSize, marginY + gameSize);
        if(boundary.contains(dragging.getRect())) {
            for(Bird bird : birds) {
                if(bird != dragging && dragging.collisionTest(bird)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public void loadInstanceState(Bundle bundle, Context context) {
        float [] locations = bundle.getFloatArray(LOCATIONS);
        int [] ids = bundle.getIntArray(IDS);
        int draggingIndex = bundle.getInt(DRAGGING_INDEX);
        playerOne = bundle.getString(PLAYER_ONE);
        playerTwo = bundle.getString(PLAYER_TWO);
        state = (State) bundle.getSerializable(STATE);
        player1First = bundle.getBoolean(ORDER);
        int nextId = bundle.getInt(NEXT_ID);

        birds.clear();
        dragging = null;

        for(int i=0; i<ids.length; i++) {
            Bird bird = new Bird(context, ids[i]);
            bird.setX(locations[2*i]);
            bird.setY(locations[2*i+1]);
            birds.add(bird);

            if(i == draggingIndex) {
                dragging = bird;
            }
        }

        next = null;
        if(nextId != -1) {
            next = new Bird(context, nextId);
        }

        if(dragging != null) {
            birds.remove(dragging);
            birds.add(dragging);
        }
    }

    public void saveInstanceState(Bundle bundle) {
        float [] locations = new float[birds.size() * 2];
        int [] ids = new int[birds.size()];
        int draggingIndex = -1;

        int nextId = -1;
        if(next != null) {
            nextId = next.getId();
        }

        for(int i=0;  i<birds.size(); i++) {
            Bird bird = birds.get(i);
            if(bird == dragging) {
                draggingIndex = i;
            }
            locations[i*2] = bird.getX();
            locations[i*2+1] = bird.getY();
            ids[i] = bird.getId();
        }

        bundle.putFloatArray(LOCATIONS, locations);
        bundle.putIntArray(IDS,  ids);
        bundle.putInt(DRAGGING_INDEX, draggingIndex);
        bundle.putInt(NEXT_ID, nextId);
        bundle.putString(PLAYER_ONE, playerOne);
        bundle.putString(PLAYER_TWO, playerTwo);
        bundle.putSerializable(STATE, state);
        bundle.putBoolean(ORDER, player1First);
    }

    private void startSelectionActivity(String name){
        Intent intent = new Intent(parentContext, SelectionActivity.class);
        intent.putExtra("PLAYER_NAME", name);
        ((Activity)parentContext).startActivityForResult(intent, 1);
    }

    public void advanceGame(int birdID) {
        switch (state) {
            case START:
                state = State.PLAYER_ONE_SELECTING;
                startSelectionActivity(playerOne);
                break;

            case PLAYER_ONE_SELECTING:
                if(player1First) {
                    state = State.PLAYER_TWO_SELECTING;
                    next = new Bird(parentContext, birdID);
                    startSelectionActivity(playerTwo);
                } else {
                    state = State.PLAYER_TWO_PLACING;
                    dragging = next;
                    birds.add(dragging);
                    next = new Bird(parentContext, birdID);
                }
                break;

            case PLAYER_TWO_SELECTING:
                if(player1First) {
                    state = State.PLAYER_ONE_PLACING;
                    dragging = next;
                    birds.add(dragging);
                    next = new Bird(parentContext, birdID);
                } else {
                    state = State.PLAYER_ONE_SELECTING;
                    next = new Bird(parentContext, birdID);
                    startSelectionActivity(playerOne);
                }
                break;

            case PLAYER_ONE_PLACING:
                if(player1First) {
                    state = State.PLAYER_TWO_PLACING;
                    dragging = next;
                    birds.add(dragging);
                    next = null;
                } else {
                    dragging = null;
                    player1First = !player1First;
                    state = State.PLAYER_ONE_SELECTING;
                    startSelectionActivity(playerOne);
                }
                break;

            case PLAYER_TWO_PLACING:
                if(player1First) {
                    dragging = null;
                    player1First = !player1First;
                    state = State.PLAYER_TWO_SELECTING;
                    startSelectionActivity(playerTwo);
                } else {
                    state = State.PLAYER_ONE_PLACING;
                    dragging = next;
                    birds.add(dragging);
                    next = null;
                }
                break;
        }
    }

    public void setNames(String p1, String p2) {
        playerOne = p1;
        playerTwo = p2;
    }

    public void end() {
        Intent intent = new Intent(parentContext, ScoreActivity.class);
        intent.putExtra("ScoreActivity.score", birds.size()-1);

        switch (state) {
            case PLAYER_ONE_PLACING:
                state = State.PLAYER_TWO_WON;
            case PLAYER_TWO_WON:
                intent.putExtra("ScoreActivity.winner", playerTwo + " wins!");
                break;

            case PLAYER_TWO_PLACING:
                state = State.PLAYER_ONE_WON;
            case PLAYER_ONE_WON:
                intent.putExtra("ScoreActivity.winner", playerOne + " wins!");
                break;
        }
        parentContext.startActivity(intent);

        dragging = null;
        next = null;
    }

    public State getState() {
        return state;
    }
}