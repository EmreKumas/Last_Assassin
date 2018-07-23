package com.ngdroidapp.GameObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.ngdroidapp.NgApp;

import istanbul.gamelab.ngdroid.util.Utils;

public class Foreground{

    //Level Foreground
    private Bitmap foreground;

    private Rect source, destination;

    @SuppressWarnings("FieldCanBeLocal")
    private int sourceWidth, sourceHeight, destinationWidth, destinationHeight, sourceX, sourceY, destinationX, destinationY;

    private Background background;

    public Foreground(String imagePath, NgApp root, Background background){

        source = new Rect();
        destination = new Rect();

        //Loading image and setting the touch control.
        this.foreground = Utils.loadImage(root, imagePath);
        this.background = background;

        //3840x2160
        sourceWidth = (int) (3840 / 2.5);
        sourceHeight = (int) (2160 / 2.5);
        sourceX = 0;
        sourceY = 2160 - sourceHeight;

        destinationWidth = root.appManager.getScreenWidth();
        destinationHeight = root.appManager.getScreenHeight();
        destinationX = 0;
        destinationY = 0;

        source.set(sourceX, sourceY, sourceX + sourceWidth, sourceY + sourceHeight);
        destination.set(destinationX, destinationY, destinationWidth, destinationHeight);
    }

    public void update(double playerSpeed, boolean foregroundNeedsToMove){

        if((background.backgroundMovingHorizontally() || background.backgroundMovingVertically()) && foregroundNeedsToMove){

            switch(background.getPressedButton()){

                case "LeftArrow":

                    //When we move the background, if the image doesn't pass the limits on the left side, we will slide it.
                    if(sourceX - playerSpeed > 0)
                        sourceX -= playerSpeed;
                    else
                        sourceX = 0;

                    source.set(sourceX, sourceY, sourceX + sourceWidth, sourceY + sourceHeight);

                    break;
                case "UpArrow":

                    if(sourceY - playerSpeed > 0)
                        sourceY -= playerSpeed;
                    else
                        sourceY = 0;

                    source.set(sourceX, sourceY, sourceX + sourceWidth, sourceY + sourceHeight);

                    break;
                case "RightArrow":

                    //When we move the background, if it won't be passing the limits of the source image, we will slide it.
                    if(sourceX + playerSpeed < 3840 - sourceWidth)
                        sourceX += playerSpeed;
                        //Else, we will make it static. Otherwise, the right of the screen will be black because the source image is fully loaded.
                    else
                        sourceX = 3840 - sourceWidth;

                    source.set(sourceX, sourceY, sourceX + sourceWidth, sourceY + sourceHeight);

                    break;
                case "DownArrow":

                    if(sourceY + playerSpeed < 2160 - sourceHeight)
                        sourceY += playerSpeed;
                    else
                        sourceY = 2160 - sourceHeight;

                    source.set(sourceX, sourceY, sourceX + sourceWidth, sourceY + sourceHeight);

                    break;
            }
        }
    }

    public void draw(Canvas canvas){

        canvas.drawBitmap(foreground, source, destination, null);
    }
}
