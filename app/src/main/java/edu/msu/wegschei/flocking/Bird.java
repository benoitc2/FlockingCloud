package edu.msu.wegschei.flocking;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Class to describe a Bird object
 */
public class Bird {
    /**
     * The image for the actual bird.
     */
    private Bitmap bird;

    /**
     * Rectangle that is where our bird is in pixels
     */
    private Rect rect;

    /**
     * Rectangle we will use for intersection testing
     */
    private Rect overlap = new Rect();

    /**
     * x location in pixels
     */
    private float x = 0.5f;

    /**
     * y location in pixels
     */
    private float y = 0.5f;

    /**
     * dimensions from the canvas for collision testing
     */
    private int marginX = 0;
    private int marginY = 0;
    private int gameSize = 0;
    private float scaleFactor = 1f;

    /**
     * Id for the bitmap
     */
    private int id;

    public Bird(Context context, int bitmapId) {
        id = bitmapId;
        bird = BitmapFactory.decodeResource(context.getResources(), bitmapId);
        rect = new Rect();
        setRect();
    }

    public void move(float dx, float dy) {
        x += dx;
        y += dy;
        setRect();
    }

    public Rect getRect() {
        return rect;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getId() {
        return id;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getPixelLeft() {
        return x*gameSize+marginX-bird.getWidth()/2*scaleFactor;
    }

    public float getPixelTop() {
        return y*gameSize+marginY-bird.getHeight()/2*scaleFactor;
    }

    private void setRect() {
        float left = getPixelLeft();
        float top = getPixelTop();

        rect.set((int)(left),
                (int)(top),
                (int)(left+bird.getWidth()*scaleFactor),
                (int)(top+bird.getHeight()*scaleFactor));
    }

    /**
     * Collision detection between two birds. This object is
     * compared to the one referenced by other
     * @param other Bird to compare to.
     * @return True if there is any overlap between the two birds.
     */
    public boolean collisionTest(Bird other) {
        // Do the rectangles overlap?
        if(!Rect.intersects(rect, other.rect)) {
            return false;
        }

        // Determine where the two images overlap
        overlap.set(rect);
        overlap.intersect(other.rect);

        // We have overlap. Now see if we have any pixels in common
        for(int r=overlap.top; r<overlap.bottom;  r++) {
            int aY = (int)(Math.abs(r - getPixelTop())/scaleFactor);
            int bY = (int)(Math.abs(r - other.getPixelTop())/scaleFactor);

            for(int c=overlap.left;  c<overlap.right;  c++) {

                int aX = (int)(Math.abs(c - getPixelLeft())/scaleFactor);
                int bX = (int)(Math.abs(c - other.getPixelLeft())/scaleFactor);

                if( (bird.getPixel(aX, aY) & 0x80000000) != 0 &&
                        (other.bird.getPixel(bX, bY) & 0x80000000) != 0) {
                    //Log.i("collision", "Overlap " + r + "," + c);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Draw the bird
     * @param canvas Canvas we are drawing on
     * @param mX Margin x value in pixels
     * @param mY Margin y value in pixels
     * @param gSize Size we draw the game in pixels
     * @param sFactor Amount we scale the birds when we draw them
     */
    public void draw(Canvas canvas, int mX, int mY, int gSize, float sFactor) {
        marginX = mX;
        marginY = mY;
        gameSize = gSize;
        scaleFactor = sFactor;

        setRect();
        canvas.save();

        canvas.translate(marginX + x * gameSize, marginY + y * gameSize);
        canvas.scale(scaleFactor, scaleFactor);
        canvas.translate(-bird.getWidth() / 2, -bird.getHeight() / 2);

        // Draw the bitmap
        canvas.drawBitmap(bird, 0, 0, null);
        canvas.restore();
    }
}
