package com.ngdroidapp.GameObjects;

import android.graphics.Bitmap;


/**
 * Created by Hande OKTAY on 16.07.2018.
 */

public class Animations{

    private Bitmap[] frames;
    private int currentFrame;

    private long startTime;
    private long delay;

    private boolean playedOnce;

    Animations(){
        playedOnce = false;
    }

    public void setFrames(Bitmap[] bitmaps){
        this.frames = bitmaps;
        currentFrame = 0;
        startTime = System.nanoTime();
        playedOnce = false;
    }


    public void update(){
        if(delay == -1) return;

        long elapsed = (System.nanoTime() - startTime) / 1000000;
        if(elapsed > delay){
            currentFrame++;
            startTime = System.nanoTime();
        }

        if(currentFrame == frames.length){
            currentFrame = 0;
            playedOnce = true;
        }
    }

    public void setCurrentFrame(int currentFrame){
        this.currentFrame = currentFrame;
    }

    public void setDelay(long delay){
        this.delay = delay;
    }

    public Bitmap getFrames(){
        return frames[currentFrame];
    }

    public int getCurrentFrame(){
        return currentFrame;
    }

    public boolean isPlayedOnce(){
        return playedOnce;
    }
}
