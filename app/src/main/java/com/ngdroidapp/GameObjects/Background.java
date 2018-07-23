package com.ngdroidapp.GameObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.ngdroidapp.NgApp;
import com.ngdroidapp.TouchControl;

import istanbul.gamelab.ngdroid.util.Utils;

public class Background{

    //Level Background
    private Bitmap background;

    private Rect source, destination;

    @SuppressWarnings("FieldCanBeLocal")
    private int sourceWidth, sourceHeight, destinationWidth, destinationHeight, sourceX, sourceY, destinationX, destinationY;

    //These variables will be helpful when we move the player on the screen because the background needs to follow the player as well.
    @SuppressWarnings("FieldCanBeLocal")
    private int playerX, playerY, touchLocationX, touchLocationY;
    private int screenWidth, screenHeight;

    private TouchControl touchControl;
    private String pressedButton;

    private boolean backgroundNeedsToMove;

    public Background(String imagePath, NgApp root, TouchControl touchControl){

        source = new Rect();
        destination = new Rect();

        //Loading image and setting the touch control.
        this.background = Utils.loadImage(root, imagePath);
        this.touchControl = touchControl;

        //3840x2160
        sourceWidth = (int) (3840 / 2.5);
        sourceHeight = (int) (2160 / 2.5);
        sourceX = 0;
        sourceY = 2160 - sourceHeight;

        screenWidth = root.appManager.getScreenWidth();
        screenHeight = root.appManager.getScreenHeight();

        destinationWidth = screenWidth;
        destinationHeight = screenHeight;
        destinationX = 0;
        destinationY = 0;

        source.set(sourceX, sourceY, sourceX + sourceWidth, sourceY + sourceHeight);
        destination.set(destinationX, destinationY, destinationWidth, destinationHeight);
    }

    public void update(Player player, HUD hud, boolean isDpadPressing){

        //Getting players current positions.
        playerX = player.getDestinationX() + player.getDestinationWidth();
        playerY = player.getDestinationY() + player.getDestinationHeight();

        if(isDpadPressing){

            //If the user is pressing dpad, we need to check if the background needs to move or not.
            //First, lets check where the user is touching on the dpad.
            if(touchControl.getDpadPressed(0)){
                touchLocationX = touchControl.getTouch(0).x;
                touchLocationY = touchControl.getTouch(0).y;
            }else{
                touchLocationX = touchControl.getTouch(1).x;
                touchLocationY = touchControl.getTouch(1).y;
            }

            //Now, we know where the user touched.
            switch(hud.pressedButtonDPad(touchLocationX, touchLocationY)){

                //If the user touches to Right Arrow, if the players position is greater than the half of the screens width and if the background is not fully loaded on horizontally...
                case "RightArrow":

                    pressedButton = "RightArrow";

                    if((playerX + player.getDestinationWidth()) > screenWidth / 2 && sourceX + sourceWidth < 3840){

                        //When we move the background, if it won't be passing the limits of the source image, we will slide it.
                        if(sourceX + player.getSpeed() < 3840 - sourceWidth)
                            sourceX += player.getSpeed();
                            //Else, we will make it static. Otherwise, the right of the screen will be black because the source image is fully loaded.
                        else
                            sourceX = 3840 - sourceWidth;

                        source.set(sourceX, sourceY, sourceX + sourceWidth, sourceY + sourceHeight);

                        backgroundNeedsToMove = true;
                    }else
                        backgroundNeedsToMove = false;

                    break;

                //The same goes here. If the user touches to Left Arrow, if the players position is less than the half of the screens width and if the backgrounds beginning is not loaded yet...
                case "LeftArrow":

                    pressedButton = "LeftArrow";

                    if(playerX < screenWidth / 2 && sourceX > 0){

                        //When we move the background, if the image doesn't pass the limits on the left side, we will slide it.
                        if(sourceX - player.getSpeed() > 0)
                            sourceX -= player.getSpeed();
                        else
                            sourceX = 0;

                        source.set(sourceX, sourceY, sourceX + sourceWidth, sourceY + sourceHeight);

                        backgroundNeedsToMove = true;
                    }else
                        backgroundNeedsToMove = false;

                    break;
                case "UpArrow":

                    pressedButton = "UpArrow";

                    if(playerY < screenHeight / 2 && sourceY > 0){

                        if(sourceY - player.getSpeed() > 0)
                            sourceY -= player.getSpeed();
                        else
                            sourceY = 0;

                        source.set(sourceX, sourceY, sourceX + sourceWidth, sourceY + sourceHeight);

                        backgroundNeedsToMove = true;
                    }else
                        backgroundNeedsToMove = false;

                    break;
                case "DownArrow":

                    pressedButton = "DownArrow";

                    if(playerY + player.getDestinationHeight() > screenHeight / 2 && sourceY + sourceHeight < 2160){

                        if(sourceY + player.getSpeed() < 2160 - sourceHeight)
                            sourceY += player.getSpeed();
                        else
                            sourceY = 2160 - sourceHeight;

                        source.set(sourceX, sourceY, sourceX + sourceWidth, sourceY + sourceHeight);

                        backgroundNeedsToMove = true;
                    }else
                        backgroundNeedsToMove = false;

                    break;
            }
        }
    }

    //Is the image moving on horizontally... If its source is not zero and not the max limit...
    public boolean backgroundMovingHorizontally(){

        return sourceX != 0 && sourceX + sourceWidth < 3840;
    }

    //Is the image moving on vertically... If its source is not zero and not the max limit...
    public boolean backgroundMovingVertically(){

        return sourceY != 0 && sourceY + sourceHeight < 2160;
    }

    public void draw(Canvas canvas){

        canvas.drawBitmap(background, source, destination, null);
    }

    public int getSourceX(){
        return sourceX;
    }

    public int getSourceY(){
        return sourceY;
    }

    public int getSourceHeight(){
        return sourceHeight;
    }

    public String getPressedButton(){
        return pressedButton;
    }

    public boolean isBackgroundNeedsToMove(){
        return backgroundNeedsToMove;
    }

    public void setBackgroundNeedsToMove(boolean backgroundNeedsToMove){
        this.backgroundNeedsToMove = backgroundNeedsToMove;
    }
}
