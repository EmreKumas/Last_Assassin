package com.ngdroidapp.GameObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.DisplayMetrics;

import com.ngdroidapp.NgApp;

import java.util.ArrayList;

import istanbul.gamelab.ngdroid.util.Utils;

public class Player{

    private Direction direction;
    private NgApp ngApp;
    private Rect source, destination;

    @SuppressWarnings("FieldCanBeLocal")
    private int destinationWidth, destinationHeight, sourceX, sourceY, destinationX, destinationY, i;

    //For the player to move continously on the screen.
    private int moveAmountX, moveAmountY;
    private int speed;
    private double speedRatio;

    //Screen dimensions.
    private int screenWidth;
    private int screenHeight;

    private Background background;
    private Ground ground;

    private String lastPressedArrow = "";

    //animation
    private Animations animation;
    private boolean isMoving;
    private static boolean isAttacked;
    private boolean isGliding;
    private boolean firstRun = true;
    private ArrayList<Bitmap[]> sprites;

    //animation actions
    private static final int RIGHT_IDLE = 0;
    private static final int RIGHT_GLIDING = 1;
    private static final int CLIMBING = 2;
    private static final int RIGHT_RUNNING = 3;
    private static final int RIGHT_ATTACK = 4;
    private static final int RIGHT_DEAD = 5;
    private static final int RIGHT_JUMPING = 6;
    private static final int LEFT_IDLE = 7;
    private static final int LEFT_GLIDING = 8;
    private static final int LEFT_RUNNING = 9;
    private static final int LEFT_ATTACK = 10;
    private static final int LEFT_DEAD = 11;
    private static final int LEFT_JUMPING = 12;


    public Player(NgApp root, Background background, Ground ground){

        source = new Rect();
        destination = new Rect();
        this.ground = ground;
        this.background = background;
        this.ngApp = root;

        screenWidth = root.appManager.getScreenWidth();
        screenHeight = root.appManager.getScreenHeight();

        speed = screenWidth / 65;
        speedRatio = 0.6;

        sourceX = 0;
        sourceY = 0;
        direction = Direction.RIGHT_IDLE;
        destinationWidth = screenWidth / 8;
        destinationHeight = screenHeight / 5;

        setupFrames();
        animation = new Animations();
        animation.setFrames(sprites.get(RIGHT_IDLE));
        animation.setDelay(75);
    }

    public void update(){

        if(!firstRun)
            gravityFall();

        if(firstRun)
            firstRun = false;

        animation.update();
    }

    public void draw(Canvas canvas){

        source.set(0, 0, animation.getFrames().getWidth(), animation.getFrames().getHeight());
        canvas.drawBitmap(animation.getFrames(), source, destination, null);
    }

    /**
     * This method simulates the gravity.
     */
    private void gravityFall(){

        for(i = 0; i < 7 && !ground.isColliding(destinationX + destinationWidth, destinationY + destinationHeight); i++)
            if(isMoving)
                movePlayer(moveAmountX, 6, lastPressedArrow);
            else
                movePlayer(0, 6, lastPressedArrow);

        while(ground.isColliding(destinationX + destinationWidth, destinationY + destinationHeight))
            if(isMoving)
                movePlayer(moveAmountX, -6, lastPressedArrow);
            else
                movePlayer(0, -6, lastPressedArrow);

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
    private void movePlayer(int x, int y, String pressedArrow){

        //If the background IS NOT moving, we move the player on the screen.
        if(!background.backgroundMovingHorizontally()){

            destinationX += x;

            //Now, we need to check if the user is not passing the screen limits.
            if((destinationX + destinationWidth) >= screenWidth && pressedArrow.equals("RightArrow"))
                destinationX -= x;
            else if(destinationX <= 0 && pressedArrow.equals("LeftArrow"))
                destinationX -= x;
        }

        if(!background.backgroundMovingVertically())
            destinationY += y;

        //Variables for continuous move.
        moveAmountX = x;
        moveAmountY = y;
        lastPressedArrow = pressedArrow;

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

                direction = Direction.LEFT;
                setAnimationType(LEFT_RUNNING);
                movePlayer((int) (-1 * speed * speedRatio), 0, "LeftArrow");


                //We move the player, also we shrink other dpad buttons.
                hud.setScaleDPad("LeftArrow", 1.2);
                break;

            //If the user is pressing to up arrow...
            case "UpArrow":

                setAnimationType(CLIMBING);
                movePlayer(0, -1 * speed, "UpArrow");

                //We move the player, also we shrink other dpad buttons.
                hud.setScaleDPad("UpArrow", 1.2);
                break;

            //If the user is pressing to right arrow...
            case "RightArrow":

                direction = Direction.RIGHT;
                setAnimationType(RIGHT_RUNNING);
                movePlayer((int) (speed * speedRatio), 0, "RightArrow");


                //We move the player, also we shrink other dpad buttons.
                hud.setScaleDPad("RightArrow", 1.2);
                break;

            //If the user is pressing to down arrow...
            case "DownArrow":

                setAnimationType(CLIMBING);
                movePlayer(0, speed, "DownArrow");

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

                if(direction == Direction.RIGHT){
                    setAnimationType(RIGHT_JUMPING);
                }else if(direction == Direction.LEFT){
                    System.out.println("inside direction left");
                    setAnimationType(LEFT_JUMPING);
                }

                //We make the action, also we shrink other action buttons.
                hud.setScaleActions("XButton", 1.2);
                break;

            //If the user is pressing to A Button...
            case "AButton":

                isAttacked = true;

                if(direction == Direction.RIGHT){
                    setAnimationType(RIGHT_ATTACK);
                }else if(direction == Direction.LEFT){
                    setAnimationType(LEFT_ATTACK);
                }

                //We make the action, also we shrink other action buttons.
                hud.setScaleActions("AButton", 1.2);
                break;

            //If the user is pressing to Y Button...
            case "YButton":

                isGliding = true;
                if(direction == Direction.RIGHT){
                    setAnimationType(RIGHT_GLIDING);
                }else if(direction == Direction.LEFT){
                    setAnimationType(LEFT_GLIDING);
                }

                //We make the action, also we shrink other action buttons.
                hud.setScaleActions("YButton", 1.2);
                break;
        }
    }

    private void setupFrames(){

        try{

            sprites = new ArrayList<>();

            Bitmap spriteSheet = Utils.loadImage(ngApp, "spritesheet.png");

            //RIGHT_IDLE frames
            Bitmap[] right_idle = new Bitmap[10];
            for(int j = 0; j < 10; j++){
                //noinspection ConstantConditions
                right_idle[j] = Bitmap.createBitmap(spriteSheet, j * 140, 0, 140, 135);
            }

            sprites.add(right_idle);


            //RIGHT_GLIDE frames
            Bitmap[] right_glide = new Bitmap[10];
            for(int j = 0; j < 10; j++){
                right_glide[j] = Bitmap.createBitmap(spriteSheet, j * 140, 132, 140, 135);

            }
            sprites.add(right_glide);


            //CLIMB frames
            Bitmap[] climb = new Bitmap[8];
            for(int j = 0; j < 8; j++){
                climb[j] = Bitmap.createBitmap(spriteSheet, j * 140, 270, 140, 135);
            }
            sprites.add(climb);


            //RIGHT_RUN frames
            Bitmap[] right_run = new Bitmap[10];
            for(int j = 0; j < 10; j++){
                right_run[j] = Bitmap.createBitmap(spriteSheet, j * 140, 405, 140, 135);
            }
            sprites.add(right_run);


            //RIGHT_ATTACK frames
            Bitmap[] attack = new Bitmap[10];
            for(int j = 0; j < 10; j++){
                attack[j] = Bitmap.createBitmap(spriteSheet, j * 140, 540, 140, 135);
            }
            sprites.add(attack);


            //RIGHT_DEAD frames
            Bitmap[] right_dead = new Bitmap[10];
            for(int j = 0; j < 10; j++){
                right_dead[j] = Bitmap.createBitmap(spriteSheet, j * 140, 675, 140, 150);
            }
            sprites.add(right_dead);

            //RIGHT_JUMP frames
            Bitmap[] right_jump = new Bitmap[10];
            for(int j = 0; j < 10; j++){
                right_jump[j] = Bitmap.createBitmap(spriteSheet, j * 140, 830, 140, 137);
            }
            sprites.add(right_jump);


            //LEFT_IDLE frames
            Bitmap[] left_idle = new Bitmap[10];
            for(int j = 0; j < 10; j++){
                left_idle[j] = flipBitmap(Bitmap.createBitmap(spriteSheet, j * 140, 0, 140, 135));
            }
            sprites.add(left_idle);

            //LEFT_GLIDE frames
            Bitmap[] left_glide = new Bitmap[10];
            for(int j = 0; j < 10; j++){
                left_glide[j] = flipBitmap(Bitmap.createBitmap(spriteSheet, j * 140, 132, 140, 135));
            }
            sprites.add(left_glide);
            source.set(sourceX, sourceY, 450, 500);


            //LEFT_ RUN frames
            Bitmap[] left_run = new Bitmap[10];
            for(int j = 0; j < 10; j++){
                left_run[j] = flipBitmap(Bitmap.createBitmap(spriteSheet, j * 140, 405, 140, 135));
            }
            sprites.add(left_run);


            //LEFT_ATTACK frames
            Bitmap[] left_attack = new Bitmap[10];
            for(int j = 0; j < 10; j++){
                left_attack[j] = flipBitmap(Bitmap.createBitmap(spriteSheet, j * 140, 540, 140, 135));
            }
            sprites.add(left_attack);


            //LEFT_DEAD frames
            Bitmap[] left_dead = new Bitmap[10];
            for(int j = 0; j < 10; j++){
                left_dead[j] = flipBitmap(Bitmap.createBitmap(spriteSheet, j * 140, 675, 140, 150));
            }
            sprites.add(left_dead);


            //LEFT_JUMP frames
            Bitmap[] left_jump = new Bitmap[10];
            for(int j = 0; j < 10; j++){
                left_jump[j] = flipBitmap(Bitmap.createBitmap(spriteSheet, j * 140, 830, 140, 137));
            }
            sprites.add(left_jump);


        }catch(Exception e){
            e.printStackTrace();
        }


    }

    public void setAnimationType(final int type){
        animation.setFrames(sprites.get(type));
        System.out.println("size of sprite array arrayList : " + sprites.size());

        if(type == RIGHT_ATTACK || type == LEFT_ATTACK){
            animation.setDelay(30);
        }else
            animation.setDelay(75);
    }


    private Bitmap flipBitmap(Bitmap d){

        Matrix m = new Matrix();
        m.preScale(-1, 1);
        Bitmap dst = Bitmap.createBitmap(d, 0, 0, d.getWidth(), d.getHeight(), m, false);
        dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);

        return dst;
    }

    public Direction getLastDirection(){
        return direction;
    }

    public void setIsAttacked(Boolean b){
        isAttacked = b;
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
}
