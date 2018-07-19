package com.ngdroidapp.GameObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.ngdroidapp.NgApp;

import istanbul.gamelab.ngdroid.util.Utils;

public class Player{

    private Bitmap player;

    private Rect source, destination;

    @SuppressWarnings("FieldCanBeLocal")
    private int sourceWidth, sourceHeight, destinationWidth, destinationHeight, sourceX, sourceY, destinationX, destinationY;

    //For the player to move continously on the screen.
    private int moveAmountX, moveAmountY;
    private int speed;

    private Background background;

    private boolean isMoving;

    public Player(String imagePath, NgApp root, Background background){

        source = new Rect();
        destination = new Rect();

        this.player = Utils.loadImage(root, imagePath);
        this.background = background;

        speed = 15;

        //232x439
        sourceWidth = 232;
        sourceHeight = 439;
        sourceX = 0;
        sourceY = 0;

        destinationWidth = 93;
        destinationHeight = 176;

        source.set(sourceX, sourceY, sourceWidth, sourceHeight);
    }

    public void update(){

        if(isMoving)
            movePlayer(moveAmountX, moveAmountY);
    }

    public void draw(Canvas canvas){

        canvas.drawBitmap(player, source, destination, null);
    }

    public Rect getDestination(){
        return destination;
    }

    public int getDestinationX(){
        return destinationX;
    }

    public int getDestinationY(){
        return destinationY;
    }

    public int getSpeed(){
        return speed;
    }

    public boolean isMoving(){
        return isMoving;
    }

    public void setMoving(boolean moving){
        isMoving = moving;
    }

    public int getDestinationHeight(){
        return destinationHeight;
    }

    public int getDestinationWidth(){
        return destinationWidth;
    }

    /**
     * This method sets the players initial coordinates.
     */
    public void setCoordinates(int x, int y){

        destinationX = x;
        destinationY = y;

        destination.set(destinationX, destinationY, destinationX + destinationWidth, destinationY + destinationHeight);
    }

    /**
     * This method moves the player on the screen.
     */
    private void movePlayer(int x, int y){

        //If the background IS NOT moving, we move the player on the screen.
        if(!background.backgroundMovingHorizantally())
            destinationX += x;

        if(!background.backgroundMovingVertically())
            destinationY += y;

        //Variables for continous move.
        moveAmountX = x;
        moveAmountY = y;

        destination.set(destinationX, destinationY, destinationX + destinationWidth, destinationY + destinationHeight);
    }

    /**
     * This method defines the reaction of the player when a dpad button is pressed.
     */
    public void dpadPressed(String pressedArrow, HUD hud){

        //If the dpad is pressed, it means the player is moving.
        isMoving = true;

        switch(pressedArrow){

            //If the user is pressing to left arrow...
            case "LeftArrow":

                movePlayer(-1 * speed, 0);

                //We move the player, also we shrink other dpad buttons.
                hud.setScaleDPad("LeftArrow", 1.2);
                break;

            //If the user is pressing to up arrow...
            case "UpArrow":

                movePlayer(0, -1 * speed);

                //We move the player, also we shrink other dpad buttons.
                hud.setScaleDPad("UpArrow", 1.2);
                break;

            //If the user is pressing to right arrow...
            case "RightArrow":

                movePlayer(speed, 0);

                //We move the player, also we shrink other dpad buttons.
                hud.setScaleDPad("RightArrow", 1.2);
                break;

            //If the user is pressing to down arrow...
            case "DownArrow":

                movePlayer(0, speed);

                //We move the player, also we shrink other dpad buttons.
                hud.setScaleDPad("DownArrow", 1.2);
                break;
        }
    }

    /**
     * This method defines the reaction of the player when an action button is pressed.
     */
    public void actionsPressed(String pressedActionButton, HUD hud){

        switch(pressedActionButton){

            //If the user is pressing to X Button...
            case "XButton":



                //We make the action, also we shrink other action buttons.
                hud.setScaleActions("XButton", 1.2);
                break;

            //If the user is pressing to A Button...
            case "AButton":



                //We make the action, also we shrink other action buttons.
                hud.setScaleActions("AButton", 1.2);
                break;

            //If the user is pressing to Y Button...
            case "YButton":



                //We make the action, also we shrink other action buttons.
                hud.setScaleActions("YButton", 1.2);
                break;
        }
    }
}
