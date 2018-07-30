package com.ngdroidapp.GameObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.DisplayMetrics;

import com.ngdroidapp.NgApp;
import com.ngdroidapp.OnScreenControls.HUD;
import com.ngdroidapp.OnScreenControls.TouchControl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import istanbul.gamelab.ngdroid.util.Utils;

public class Player{

    private Direction direction;
    private NgApp ngApp;
    private Rect source, destination;
    private HUD hud;
    private TouchControl touchControl;

    @SuppressWarnings("FieldCanBeLocal")
    private int destinationWidth, destinationHeight, sourceX, sourceY, destinationX, destinationY, i;

    //For the player to move continously on the screen.
    private int moveAmountX, moveAmountY;
    private double speed, velY;
    private final double gravity = 3;
    private double speedRatio;

    //Screen dimensions.
    private int screenWidth, screenHeight;

    private Background background;

    private List<Collideables> collideables;
    private List<nonCollideables> nonCollideables;

    private String lastPressedArrow = "";

    //variables for jump action for player
    private boolean enableGravity = true;
    private boolean jump;
    private double jumpCycle = 0.8;
    private boolean onSurface, stopPoint;
    private boolean isLanded = true;

    @SuppressWarnings("FieldCanBeLocal")
    private String collidedDirection = "";

    //variables for gliding action for player
    private boolean isGliding = false;
    private boolean isPressedLeftOrRight = false;
    private int dpadTouchLocationX, dpadTouchLocationY, actionTouchLocationX, actionTouchLocationY;

    //animation
    private Animations animation;
    private boolean isMoving;
    private boolean firstRun = true;
    private boolean isClimbing;
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


    public Player(NgApp root, Background background, Ground ground, Bridge bridge, AirObjects airObjects, Obstacles obstacles, HUD hud, TouchControl touchControl){

        source = new Rect();
        destination = new Rect();
        this.background = background;
        this.ngApp = root;
        this.hud = hud;
        this.touchControl = touchControl;

        collideables = new ArrayList<>();
        nonCollideables = new ArrayList<>();

        collideables.add(ground);
        collideables.add(bridge);
        collideables.add(airObjects);
        nonCollideables.add(obstacles);

        screenWidth = root.appManager.getScreenWidth();
        screenHeight = root.appManager.getScreenHeight();

        speed = 25;
        speedRatio = 0.8 * ((double) screenWidth / 1280.0);
        velY = -12;

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

        onSurface = false;

        //firsRun is boolean for skipping collision control at first update method in the game.
        //If it checks collisions before first draw, program gives NullPointerException.
        if(!firstRun){
            //GRAVITY FALL
            if(!isCollidingCollideable() && !stopPoint && !isClimbing){

                if(isGliding)
                    jumpCycle = 1;
                else
                    jumpCycle += 0.25;

                gravityFall(8);

            }else
                jumpCycle = 0.8;

            //GRAVITY PUSH
            if(isCollidingCollideable() && !onSurface && !stopPoint){
                gravityPush();
            }

            //NON-COLLIDEABLES
            if(isCollidingnonCollideable()){
                //Convert string to an appropriate format.
                collidedDirection = direction.name();
                collidedDirection = collidedDirection.substring(0, 1) + collidedDirection.substring(1, collidedDirection.length()).toLowerCase(Locale.ENGLISH);

                isMoving = !lastPressedArrow.contains(collidedDirection);
                stopPoint = true;
            }else{
                stopPoint = false;
                if(!lastPressedArrow.equals(direction.name().substring(0, 1) + direction.name().substring(1, direction.name().length()).toLowerCase(Locale.ENGLISH) + "Arrow")){
                    lastPressedArrow = direction.name().substring(0, 1) + direction.name().substring(1, direction.name().length()).toLowerCase(Locale.ENGLISH) + "Arrow";

                    if(lastPressedArrow.equals("RightArrow"))
                        moveAmountX = Math.abs(moveAmountX);
                    else
                        moveAmountX = Math.abs(moveAmountX) * -1;
                }
            }
        }else
            firstRun = false;

        //Player movement
        if(isMoving)
            movePlayer(moveAmountX, moveAmountY, lastPressedArrow);

        //Player jump
        if(jump && !isGliding)
            Jump();

        if(onSurface)
            isGliding = false;

        animation.update();
    }

    public void draw(Canvas canvas){

        source.set(0, 0, animation.getFrames().getWidth(), animation.getFrames().getHeight());
        canvas.drawBitmap(animation.getFrames(), source, destination, null);
    }

    /**
     * This method checks if the player is colliding with the ground, bridge or airObjects.
     */
    private boolean isCollidingCollideable(){

        for(Collideables collideable : collideables)
            if(collideable.isColliding(destinationX + destinationWidth, destinationY + destinationHeight))
                return true;

        return false;
    }

    /**
     * This method checks if the player is colliding with nonCollideables.
     */
    private boolean isCollidingnonCollideable(){

        for(nonCollideables nonCollideable : nonCollideables)
            if(nonCollideable.isColliding(destinationX + destinationWidth, destinationY + destinationHeight))
                return true;

        return false;
    }

    /**
     * This method simulates the gravityFall.
     */
    private void gravityFall(@SuppressWarnings("SameParameterValue") int loopAmount){

        for(i = 0; i < loopAmount && !isCollidingCollideable(); i++){

            destinationY += (int) (jumpCycle * 1.75);
        }

        //To stabilize the player-screen ratio in which the player always needs to be closed to the mid of the screen.
        while(destinationY > screenHeight / 2 && background.getSourceY() + background.getSourceHeight() < 2160){

            destinationY--;
            background.addSourceY(1);
        }

        destination.set(destinationX, destinationY, destinationX + destinationWidth, destinationY + destinationHeight);
    }

    /**
     * This method simulates the gravityPush.
     */
    private void gravityPush(){

        while(isCollidingCollideable()){

            destinationY--;
        }

        destinationY++;

        //To stabilize the player-screen ratio in which the player always needs to be closed to the mid of the screen.
        while(destinationY < screenHeight / 2 && background.getSourceY() > 0){

            destinationY++;
            background.addSourceY(-1);
        }

        onSurface = true;

        destination.set(destinationX, destinationY, destinationX + destinationWidth, destinationY + destinationHeight);
    }

    /**
     * This method creates jump movement for the player.
     */
    private void Jump(){

        isLanded = false;  //checks whether the player landed on surface or not.
        isMoving = false;

        destinationY += velY;  //first part of the jump movement towards Y axis
        velY += gravity;

        /*if (!background.backgroundMovingHorizontally()) {
            if (direction == Direction.LEFT) {
                destinationX -= 25;
            } else if (direction == Direction.RIGHT) {
                destinationX += 25;
            }
        }*/

        //falling part of the jump movement.velocity towards Y axis becomes 0 if the player at hMax and free falling starts with gravity
        if(velY == 0){
            enableGravity = true;
            if(!isGliding){
                gravityFall(10);
            }
            onSurface = false;
        }

        //If the player touches the ground after jump move
        if(isCollidingCollideable() && enableGravity){
            isLanded = true;
            velY = -12; //set default velocity on Y axis again for another jump action
            onSurface = true;
            enableGravity = false; //checks the collision with ground before and after jump.
            jump = false;
        }
        destination.set(destinationX, destinationY, destinationX + destinationWidth, destinationY + destinationHeight);
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

        if(!background.backgroundMovingVertically()){
            destinationY += y;
        }

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

        //If the dpad is pressed, it means the player is moving ONLY IF THE CONDITIONS ARE TRUE.
        isMoving = true;

        switch(pressedArrow){

            //If the user is pressing to left arrow...
            case "LeftArrow":

                isPressedLeftOrRight = true;
                direction = Direction.LEFT;
                setAnimationType(LEFT_RUNNING);
                if(!isCollidingnonCollideable()){
                    movePlayer((int) (-1 * speed * speedRatio), 0, "LeftArrow");
                }else
                    isMoving = false;

                if(isLanded)
                    velY = -30;

                if(!isGliding){
                    setAnimationType(LEFT_RUNNING);
                }

                //We move the player, also we shrink other dpad buttons.
                hud.setScaleDPad("LeftArrow", 1.2);
                break;

            //If the user is pressing to up arrow...
            case "UpArrow":

                isPressedLeftOrRight = false;
                isGliding = false;
                isClimbing = true;
                setAnimationType(CLIMBING);
                movePlayer(0, (int) (-1 * speed * speedRatio), "UpArrow");

                //We move the player, also we shrink other dpad buttons.
                hud.setScaleDPad("UpArrow", 1.2);
                break;

            //If the user is pressing to right arrow...
            case "RightArrow":

                isPressedLeftOrRight = true;
                direction = Direction.RIGHT;
                setAnimationType(RIGHT_RUNNING);

                if(!isCollidingnonCollideable())
                    movePlayer((int) (speed * speedRatio), 0, "RightArrow");
                else
                    isMoving = false;

                if(isLanded)
                    velY = -30;

                if(!isGliding)
                    setAnimationType(RIGHT_RUNNING);

                //We move the player, also we shrink other dpad buttons.
                hud.setScaleDPad("RightArrow", 1.2);
                break;

            //If the user is pressing to down arrow...
            case "DownArrow":

                isPressedLeftOrRight = false;
                isGliding = false;
                setAnimationType(CLIMBING);
                movePlayer(0, (int) (speed * speedRatio), "DownArrow");

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

            //If the user is pressing to X Button, Jump
            case "XButton":

                isGliding = false;
                if(isLanded){

                    velY = -60;
                    jump = true;
                    if(direction == Direction.RIGHT){
                        setAnimationType(RIGHT_JUMPING);
                    }else if(direction == Direction.LEFT){
                        setAnimationType(LEFT_JUMPING);
                    }
                    //We make the action, also we shrink other action buttons.
                    hud.setScaleActions("XButton", 1.2);
                }
                break;

            //If the user is pressing to A Button, Attack.
            case "AButton":

                isGliding = false;

                if(direction == Direction.RIGHT){
                    setAnimationType(RIGHT_ATTACK);
                }else if(direction == Direction.LEFT){
                    setAnimationType(LEFT_ATTACK);
                }

                //We make the action, also we shrink other action buttons.
                hud.setScaleActions("AButton", 1.2);
                break;

            //If the user is pressing to Y Button, Glide.
            case "YButton":

                isGliding = true;
                if(!onSurface){
                    if(direction == Direction.RIGHT){
                        setAnimationType(RIGHT_GLIDING);
                    }else if(direction == Direction.LEFT){
                        setAnimationType(LEFT_GLIDING);
                    }
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
                right_idle[j] = Bitmap.createBitmap(spriteSheet, j * 140, 0, 140, 120);
            }

            sprites.add(right_idle);


            //RIGHT_GLIDE frames
            Bitmap[] right_glide = new Bitmap[10];
            for(int j = 0; j < 10; j++){
                right_glide[j] = Bitmap.createBitmap(spriteSheet, j * 140, 132, 140, 120);

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

    public int getDestinationX(){
        return destinationX;
    }

    public int getDestinationY(){
        return destinationY;
    }

    public int getSpeed(){
        return (int) speed;
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

    public void setClimbing(boolean climbing){
        isClimbing = climbing;
    }

    public void setGliding(boolean gliding) {
        isGliding = gliding;
    }

    public void checkButtonPressesForActions(){

        if(touchControl.getActionButtonPressed(0)){

            //action button id is 0

            if(touchControl.isOtherTouchDpad(1)){

                //dpad button id is 1

                dpadTouchLocationX = touchControl.getTouch(1).x;
                dpadTouchLocationY = touchControl.getTouch(1).y;

                actionTouchLocationX = touchControl.getTouch(0).x;
                actionTouchLocationY = touchControl.getTouch(0).y;

                if(hud.pressedButtonActions(actionTouchLocationX, actionTouchLocationY).equals("YButton")){  //if the pressed button is glide button

                    switch(hud.pressedButtonDPad(dpadTouchLocationX, dpadTouchLocationY)){

                        case "LeftArrow":
                            setAnimationType(LEFT_GLIDING);
                            break;
                        case "RightArrow":
                            setAnimationType(RIGHT_GLIDING);
                            break;
                        case "UpArrow":
                            break;
                        case "DownArrow":
                            break;
                        default:
                            break;

                    }

                 /*   if (hud.pressedButtonDPad(dpadTouchLocationX, dpadTouchLocationY) == "LeftArrow") {
                        //do something
                    } else if (hud.pressedButtonDPad(dpadTouchLocationX, dpadTouchLocationY) == "RightArrow") {
                        //do something
                    } else if (hud.pressedButtonDPad(dpadTouchLocationX, dpadTouchLocationY) == "UpArrow") {
                        //do something
                    } else if (hud.pressedButtonDPad(dpadTouchLocationX, dpadTouchLocationY) == "DownArrow") {
                        //do something
                    } */
                }

            }

        }else if(touchControl.getDpadPressed(0)){

            //dpad id is 0

            if(touchControl.isOtherTouchAction(1)){

                //dpad button id is 0

                dpadTouchLocationX = touchControl.getTouch(0).x;
                dpadTouchLocationY = touchControl.getTouch(0).y;

                //action button id is 1

                actionTouchLocationX = touchControl.getTouch(1).x;
                actionTouchLocationY = touchControl.getTouch(1).y;

                if(hud.pressedButtonActions(actionTouchLocationX, actionTouchLocationY).equals("YButton")){

                    switch(hud.pressedButtonDPad(dpadTouchLocationX, dpadTouchLocationY)){

                        case "LeftArrow":
                            break;
                        case "RightArrow":
                            break;
                        case "UpArrow":
                            break;
                        case "DownArrow":
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }
}
