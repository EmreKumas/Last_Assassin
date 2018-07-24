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

    public void update(){

        sourceX = background.getSourceX();
        source.set(sourceX, sourceY, sourceX + sourceWidth, sourceY + sourceHeight);
    }

    public void draw(Canvas canvas){

        canvas.drawBitmap(foreground, source, destination, null);
    }
}
