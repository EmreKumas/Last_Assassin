package com.ngdroidapp.GameObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

import com.ngdroidapp.NgApp;

import istanbul.gamelab.ngdroid.util.Utils;

public class HUD_Elements{

    private String name;

    private Bitmap bitmap;
    private Rect destination;

    private Path shapeToDraw;
    private Paint paint;
    private Region region;
    private RectF recognizableArea;

    public int color;

    @SuppressWarnings("FieldCanBeLocal")
    private int destinationX, destinationY, destinationWidth, destinationHeight;

    private boolean editable = true;

    HUD_Elements(String name, String imagePath, NgApp root){

        //Hud elements name.
        this.name = name;

        //Loading the image
        bitmap = Utils.loadImage(root, imagePath);

        destination = new Rect();
        shapeToDraw = new Path();

        //Paint is the fill we use for the shapes will be drawen.
        paint = new Paint();

        //Region is the area where we fill the paint. It will be used for collision detection.
        region = new Region();

        //And RectF is the area where we click with our mouse. It will also be used for collision detection.
        recognizableArea = new RectF();

        paint.setColor(Color.TRANSPARENT);

        //SCALING FOR DIFFERENT RESOLUTIONS (FOR DIFFERENT PHONES)
        destinationWidth = (int) (bitmap.getWidth() / (1280.0 / root.appManager.getScreenWidth()));
        destinationHeight = (int) (bitmap.getHeight() / (720.0 / root.appManager.getScreenHeight()));

        destination.set(destinationX, destinationY, destinationX + destinationWidth, destinationY + destinationHeight);
    }

    public void draw(Canvas canvas){

        canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), destination, null);

    }

    public void setCoordinates(double x, double y){

        destinationX = (int) x;
        destinationY = (int) y;

        destination.set(destinationX, destinationY, destinationX + destinationWidth, destinationY + destinationHeight);
    }

    public void setScale(int screenWidth, int screenHeight, double scaleRatio){

        destinationWidth = (int) (bitmap.getWidth() / (1280.0 / screenWidth) / scaleRatio);
        destinationHeight = (int) (bitmap.getHeight() / (720.0 / screenHeight) / scaleRatio);

        destination.set(destinationX, destinationY, destinationX + destinationWidth, destinationY + destinationHeight);
    }

    public int getDestinationX(){
        return destinationX;
    }

    public int getDestinationY(){
        return destinationY;
    }

    public int getDestinationWidth(){
        return destinationWidth;
    }

    public int getDestinationHeight(){
        return destinationHeight;
    }

    public String getName(){
        return name;
    }

    public boolean isPressing(Point clickPoint){

        return region.contains(clickPoint.x, clickPoint.y);
    }

    /**
     * Following five methods will be used to draw the collision detectors for hud elements.
     */
    public void drawLeftArrowShape(Canvas canvas, int x, int y, int height, int bigWidth, int smallWidth){

        //We need to reset the path, otherwise old things will remain and corrupt the new shape.
        shapeToDraw.reset();

        shapeToDraw.moveTo(x, y);
        shapeToDraw.lineTo(x + bigWidth, y);
        shapeToDraw.lineTo(x + bigWidth + smallWidth, y + (height / 2));
        shapeToDraw.lineTo(x + bigWidth, y + height);
        shapeToDraw.lineTo(x, y + height);
        shapeToDraw.lineTo(x, y);
        shapeToDraw.close();

        drawAndSetCollisions(canvas);
    }

    public void drawUpArrowShape(Canvas canvas, int x, int y, int height, int bigWidth, int smallWidth){

        shapeToDraw.reset();

        shapeToDraw.moveTo(x, y);
        shapeToDraw.lineTo(x + height, y);
        shapeToDraw.lineTo(x + height, y + bigWidth);
        shapeToDraw.lineTo(x + (height / 2), y + bigWidth + smallWidth);
        shapeToDraw.lineTo(x, y + bigWidth);
        shapeToDraw.lineTo(x, y);
        shapeToDraw.close();

        drawAndSetCollisions(canvas);
    }

    public void drawRightArrowShape(Canvas canvas, int x, int y, int height, int bigWidth, int smallWidth){

        shapeToDraw.reset();

        x += bigWidth + smallWidth;

        shapeToDraw.moveTo(x, y);
        shapeToDraw.lineTo(x, y + height);
        shapeToDraw.lineTo(x - bigWidth, y + height);
        shapeToDraw.lineTo(x - bigWidth - smallWidth, y + (height / 2));
        shapeToDraw.lineTo(x - bigWidth, y);
        shapeToDraw.lineTo(x, y);
        shapeToDraw.close();

        drawAndSetCollisions(canvas);
    }

    public void drawDownArrowShape(Canvas canvas, int x, int y, int height, int bigWidth, int smallWidth){

        shapeToDraw.reset();

        y += bigWidth + smallWidth;

        shapeToDraw.moveTo(x, y);
        shapeToDraw.lineTo(x + height, y);
        shapeToDraw.lineTo(x + height, y - bigWidth);
        shapeToDraw.lineTo(x + (height / 2), y - bigWidth - smallWidth);
        shapeToDraw.lineTo(x, y - bigWidth);
        shapeToDraw.lineTo(x, y);
        shapeToDraw.close();

        drawAndSetCollisions(canvas);
    }

    public void drawCircleShape(Canvas canvas, int x, int y, float radius){

        shapeToDraw.reset();

        shapeToDraw.addCircle(x + radius, y + radius, radius, Path.Direction.CW);
        shapeToDraw.close();

        drawAndSetCollisions(canvas);
    }

    private void drawAndSetCollisions(Canvas canvas){

        //Drawing the path on the canvas.
        canvas.drawPath(shapeToDraw, paint);

        //Computing the bounds and sending them to RectF.
        shapeToDraw.computeBounds(recognizableArea, true);

        //Setting the region for collision detection.
        region.setPath(shapeToDraw, new Region((int) recognizableArea.left, (int) recognizableArea.top,
                (int) recognizableArea.right, (int) recognizableArea.bottom));
    }

    public boolean isEditable(){
        return editable;
    }

    public void setEditable(boolean editable){
        this.editable = editable;
    }
}
