package com.ngdroidapp.GameObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.DisplayMetrics;

import com.ngdroidapp.GameObjects.Abstracts_Interfaces.AirThings;
import com.ngdroidapp.GameObjects.Abstracts_Interfaces.Collideables;
import com.ngdroidapp.GameObjects.Abstracts_Interfaces.GroundForEverything;
import com.ngdroidapp.GameObjects.Abstracts_Interfaces.Obstacles;
import com.ngdroidapp.GameObjects.Abstracts_Interfaces.nonCollideables;
import com.ngdroidapp.GameObjects.Grounds_Obstacles.GroundAfterBridge;
import com.ngdroidapp.GameObjects.Grounds_Obstacles.ThingToHoldPlayer;
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
    private Background background;
    private Ladder ladder;

    private List<Collideables> collideables;
    private List<nonCollideables> nonCollideables;

    private int sourceX, sourceY, destinationX, destinationY;
    public static int destinationWidth, destinationHeight;

    @SuppressWarnings("FieldCanBeLocal")
    private int i;

    //For the player to move continously on the screen.
    private int moveAmountX, moveAmountY;
    private double speed, speedRatio, glidingSpeed;
    private double gravity, velY;

    //Screen dimensions.
    private int screenWidth, screenHeight;

    private String lastPressedArrow = "";

    //variables for jump action for player
    private boolean jump, onSurface, stopPoint, jumpCamera;
    private boolean gravityEnabled = true;
    private double jumpCycle = 0.8;

    //variables for gliding action for player
    private boolean isGliding;
    private int dpadTouchLocationX, dpadTouchLocationY, actionTouchLocationX, actionTouchLocationY;

    //animation
    private Animations animation;
    private ArrayList<Bitmap[]> sprites;
    private boolean isMoving, isClimbing, climbingFinished, isAttacking;
    private boolean firstRun = true;
    private int currentAnimation;

    //animation actions
    public static final int RIGHT_IDLE = 0;
    private static final int RIGHT_GLIDING = 1;
    private static final int CLIMBING = 2;
    public static final int RIGHT_RUNNING = 3;
    private static final int RIGHT_ATTACK = 4;
    private static final int RIGHT_DEAD = 5;
    private static final int RIGHT_JUMPING = 6;
    public static final int LEFT_IDLE = 7;
    private static final int LEFT_GLIDING = 8;
    public static final int LEFT_RUNNING = 9;
    private static final int LEFT_ATTACK = 10;
    private static final int LEFT_DEAD = 11;
    private static final int LEFT_JUMPING = 12;

    public Player(NgApp root, Background background, HUD hud, TouchControl touchControl, List<GroundForEverything> groundForEverything, List<Obstacles> obstacles, Ladder ladder){

        source = new Rect();
        destination = new Rect();
        this.background = background;
        this.ngApp = root;
        this.hud = hud;
        this.touchControl = touchControl;
        this.ladder = ladder;

        collideables = new ArrayList<>();
        nonCollideables = new ArrayList<>();

        collideables.addAll(groundForEverything);
        nonCollideables.addAll(obstacles);

        screenWidth = root.appManager.getScreenWidth();
        screenHeight = root.appManager.getScreenHeight();
        gravity = screenHeight / 180;

        speed = 25;
        glidingSpeed = speed / 2;
        speedRatio = 0.8 * ((double) screenWidth / 1280.0);

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

        //firsRun is boolean for skipping collision control at first update method in the game.
        //If it checks collisions before first draw,  program gives NullPointerException.
        if(!firstRun){

            //If the player is not touching to ground, then onSurface is false.
            if(!isCollidingCollideable())
                onSurface = false;

            //GRAVITY FALL
            if(!isCollidingCollideable() && gravityEnabled && !isClimbing){
                if(isGliding)
                    jumpCycle = 1;
                else
                    jumpCycle += 0.25;

                gravityFall(8);
            }else
                jumpCycle = 0.8;

            //GRAVITY PUSH
            if(isCollidingCollideable() && gravityEnabled)
                gravityPush();

            //NON-COLLIDEABLES
            if(isCollidingnonCollideable()){

                if(direction.name().equals("RIGHT")){
                    stopPoint = true;
                    isMoving = false;
                }else
                    stopPoint = false;

                //Will be enabled later.
//                if(collidedDirection.isEmpty() || (!stopPoint && lastPressedArrow.equals(collidedDirection.concat("Arrow")))){
//                    //Convert string to an appropriate format.
//                    collidedDirection = direction.name();
//                    collidedDirection = collidedDirection.substring(0, 1) + collidedDirection.substring(1, collidedDirection.length()).toLowerCase(Locale.ENGLISH);
//
//                    isMoving = !lastPressedArrow.contains(collidedDirection.concat("Arrow"));
//
//                    stopPoint = true;
//                }
            }else
                stopPoint = false;

            //LADDER
            if(isCollidingLadder()){

                //If the player is also colliding with the groundAfterBridge we only enable upArrow.
                if(collideables.get(3).isColliding(destinationX + destinationWidth, destinationY + destinationHeight)){
                    hud.returnButton("UpArrow").setEditable(true);

                    climbingFinished = lastPressedArrow.equals("DownArrow");

                    //Also we disable isClimbing if lastPressedArrow not equals to upArrow.
                    if(!lastPressedArrow.equals("UpArrow") && isClimbing){
                        isClimbing = false;
                    }
                }
                //If the player is also colliding with the groundAboveLadder we only enable downArrow.
                else if(collideables.get(4).isColliding(destinationX + destinationWidth, destinationY + destinationHeight)){
                    hud.returnButton("DownArrow").setEditable(true);

                    climbingFinished = lastPressedArrow.equals("UpArrow");

                }else{
                    climbingFinished = false;
                    hud.returnButton("UpArrow").setEditable(true);
                    hud.returnButton("DownArrow").setEditable(true);
                }

                if(climbingFinished){
                    moveAmountY = 0;
                    isMoving = false;
                }

                if(isClimbing && !onSurface){

                    moveAmountX = 0;
                    hud.returnButton("LeftArrow").setEditable(false);
                    hud.returnButton("RightArrow").setEditable(false);
                }else if(onSurface){

                    hud.returnButton("LeftArrow").setEditable(true);
                    hud.returnButton("RightArrow").setEditable(true);
                }
            }else{

                isClimbing = false;
                hud.returnButton("UpArrow").setEditable(false);
                hud.returnButton("DownArrow").setEditable(false);
                hud.returnButton("LeftArrow").setEditable(true);
                hud.returnButton("RightArrow").setEditable(true);
            }

            //TO GET RID OF OBSTACLES
            if(isMoving && stopPoint && !lastPressedArrow.equals(direction.name().substring(0, 1) + direction.name().substring(1, direction.name().length()).toLowerCase(Locale.ENGLISH) + "Arrow")){
                lastPressedArrow = direction.name().substring(0, 1) + direction.name().substring(1, direction.name().length()).toLowerCase(Locale.ENGLISH) + "Arrow";

                if(lastPressedArrow.equals("RightArrow"))
                    if(!isGliding)
                        moveAmountX = Math.abs(moveAmountX);
                    else
                        moveAmountX = Math.abs((int) glidingSpeed);
                else{
                    if(!isGliding)
                        moveAmountX = Math.abs(moveAmountX) * -1;
                    else
                        moveAmountX = Math.abs((int) glidingSpeed) * -1;
                }

                stopPoint = false;
                isMoving = true;
            }
        }else
            firstRun = false;

        //Player jump
        if(jump)
            Jump();

        //Player movement
        if(isMoving)
            movePlayer(moveAmountX, moveAmountY, lastPressedArrow);

        if(onSurface)
            isGliding = false;

        //To revert gliding effects back to normal
        if(!isGliding && isMoving && !isClimbing){

            if(lastPressedArrow.equals("RightArrow"))
                moveAmountX = Math.abs((int) (speed * speedRatio));
            else if(lastPressedArrow.equals("LeftArrow"))
                moveAmountX = Math.abs((int) (speed * speedRatio)) * -1;
        }

        //ATTACKING
        if(isAttacking && animation.isPlayedOnce()){

            isAttacking = false;

            if(isMoving){
                if(direction == Direction.LEFT)
                    setAnimationType(LEFT_RUNNING);
                else if(direction == Direction.RIGHT)
                    setAnimationType(RIGHT_RUNNING);
            }else{
                if(direction == Direction.LEFT)
                    setAnimationType(LEFT_IDLE);
                else if(direction == Direction.RIGHT)
                    setAnimationType(RIGHT_IDLE);
            }
        }

        //GLIDING
        if(!isGliding && onSurface && (getAnimationType() == LEFT_GLIDING || getAnimationType() == RIGHT_GLIDING)){

            if(isMoving){
                if(direction == Direction.LEFT)
                    setAnimationType(LEFT_RUNNING);
                else if(direction == Direction.RIGHT)
                    setAnimationType(RIGHT_RUNNING);
            }else{
                if(direction == Direction.LEFT)
                    setAnimationType(LEFT_IDLE);
                else if(direction == Direction.RIGHT)
                    setAnimationType(RIGHT_IDLE);
            }
        }

        //CLIMBING
        if(climbingFinished && getAnimationType() == CLIMBING){

            if(direction == Direction.LEFT)
                setAnimationType(LEFT_IDLE);
            else if(direction == Direction.RIGHT)
                setAnimationType(RIGHT_IDLE);

            jumpCycle += 0.75;
            gravityFall(8);
        }

        //CLIMBING
        if(isClimbing && !isMoving){

            setAnimationType(CLIMBING);
            animation.setDelay(10000000);
        }

        //ANIMATIONS
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

        for(Collideables collideable : collideables){

            if(collideable instanceof GroundAfterBridge || collideable instanceof ThingToHoldPlayer){

                if(lastPressedArrow.equals("RightArrow") && collideable.isColliding((int) (destinationX + destinationWidth * 0.7), destinationY + destinationHeight))
                    return true;
                else if(lastPressedArrow.equals("LeftArrow") && collideable.isColliding((int) (destinationX + destinationWidth / 1.4), destinationY + destinationHeight))
                    return true;
                else if(lastPressedArrow.equals("DownArrow") && collideable.isColliding(destinationX + destinationWidth, destinationY + destinationHeight) && !collideables.get(4).isColliding(destinationX + destinationWidth, destinationY + destinationHeight))
                    return true;

            }else if(collideable instanceof AirThings){

                if(lastPressedArrow.equals("DownArrow") && isCollidingLadder())
                    return false;
                else if(lastPressedArrow.equals("UpArrow") && collideable.isColliding(destinationX + destinationWidth, destinationY + destinationHeight))
                    return true;
                else if(lastPressedArrow.equals("DownArrow") && collideable.isColliding(destinationX + destinationWidth, destinationY + destinationHeight) && !collideables.get(4).isColliding(destinationX + destinationWidth, destinationY + destinationHeight))
                    return true;
                else if(collideable.isColliding(destinationX + destinationWidth, destinationY + destinationHeight))
                    return true;

            }else if(collideable.isColliding(destinationX + destinationWidth, destinationY + destinationHeight))
                return true;
        }

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
     * This method checks if the player is colliding with the ladder.
     */
    private boolean isCollidingLadder(){

        return ladder.isColliding(destinationX + destinationWidth / 2, destinationY + destinationHeight);

    }

    /**
     * This method simulates the gravityFall.
     */
    private void gravityFall(@SuppressWarnings("SameParameterValue") int loopAmount){

        for(i = 0; i < loopAmount && !isCollidingCollideable(); i++){

            destinationY += (int) (jumpCycle * 1.8);
        }

        //To stabilize the player-screen ratio in which the player always needs to be closed to the mid of the screen.
        while(destinationY > screenHeight / 1.8 && background.backgroundStopPoint()){

            destinationY--;
            background.addSourceY(1);
        }

        while(isCollidingCollideable()){

            destinationY--;
        }

        destinationY++;

        while(destinationY <= screenHeight / 2.2 && background.getSourceY() > 0 && !jumpCamera){

            destinationY++;
            background.addSourceY(-1);
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

        if(stopPoint && !isGliding){

            if(direction == Direction.LEFT)
                setAnimationType(LEFT_IDLE);
            else if(direction == Direction.RIGHT)
                setAnimationType(RIGHT_IDLE);
        }

        if(velY < 0 && !gravityEnabled){
            destinationY += velY;  //first part of the jump movement towards Y axis
            velY += gravity;
            onSurface = false;
            jumpCamera = true;
        }

        if(velY >= 0)
            gravityEnabled = true;

        //If the player touches the ground after jump move
        if(onSurface){
            jump = false;
            jumpCamera = false;

            if(isMoving){
                if(direction == Direction.LEFT)
                    setAnimationType(LEFT_RUNNING);
                else if(direction == Direction.RIGHT)
                    setAnimationType(RIGHT_RUNNING);
            }else{
                if(direction == Direction.LEFT)
                    setAnimationType(LEFT_IDLE);
                else if(direction == Direction.RIGHT)
                    setAnimationType(RIGHT_IDLE);
            }
        }

        destination.set(destinationX, destinationY, destinationX + destinationWidth, destinationY + destinationHeight);
    }

    private void attack(){

        if(getAnimationType() != RIGHT_ATTACK && getAnimationType() != LEFT_ATTACK){

            isAttacking = true;

            if(direction == Direction.RIGHT){
                setAnimationType(RIGHT_ATTACK);
            }else if(direction == Direction.LEFT){
                setAnimationType(LEFT_ATTACK);
            }
        }
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
    private void movePlayer(Integer x, Integer y, String pressedArrow){

        //If the background IS NOT moving, we move the player on the screen.
        if(!background.backgroundMovingHorizontally() && x != null){

            destinationX += x;

            //Now, we need to check if the user is not passing the screen limits.
            if((destinationX + destinationWidth) >= screenWidth && pressedArrow.equals("RightArrow"))
                destinationX -= x;
            else if(destinationX <= 0 && pressedArrow.equals("LeftArrow"))
                destinationX -= x;
        }

        if(pressedArrow.equals("UpArrow") && (!background.backgroundMovingVertically() || destinationY > screenHeight / 2) && y != null)
            destinationY += y;
        else if(pressedArrow.equals("DownArrow") && (!background.backgroundMovingVertically() || destinationY < screenHeight / 2) && y != null)
            destinationY += y;

        if(x != null)
            moveAmountX = x;
        if(y != null)
            moveAmountY = y;

        lastPressedArrow = pressedArrow;
        destination.set(destinationX, destinationY, destinationX + destinationWidth, destinationY + destinationHeight);
    }

    /**
     * This method defines the reaction of the player when a dpad button is pressed.
     */
    public void dpadPressed(String pressedArrow, HUD hud){

        //If the dpad is pressed, it means the player is moving ONLY IF THE CONDITIONS ARE TRUE.
        switch(pressedArrow){

            //If the user is pressing to left arrow...
            case "LeftArrow":

                direction = Direction.LEFT;
                moveAmountY = 0;

                if(!stopPoint || !lastPressedArrow.equals(direction.name().substring(0, 1) + direction.name().substring(1, direction.name().length()).toLowerCase(Locale.ENGLISH) + "Arrow")){

                    if(!isGliding)
                        movePlayer((int) (-1 * speed * speedRatio), moveAmountY, "LeftArrow");
                    else
                        movePlayer((int) (-1 * glidingSpeed * speedRatio), moveAmountY, "LeftArrow");
                    isMoving = true;
                    if(!isGliding)
                        setAnimationType(LEFT_RUNNING);
                    else
                        setAnimationType(LEFT_GLIDING);
                }else
                    isMoving = false;

                if(!isGliding && isMoving){
                    setAnimationType(LEFT_RUNNING);
                }

                //We move the player, also we shrink other dpad buttons.
                hud.setScaleDPad("LeftArrow", 1.2);
                break;

            //If the user is pressing to up arrow...
            case "UpArrow":

                isGliding = false;
                isClimbing = true;
                isMoving = true;
                moveAmountX = 0;

                setAnimationType(CLIMBING);
                movePlayer(moveAmountX, (int) (-1 * speed * speedRatio), "UpArrow");

                //We move the player, also we shrink other dpad buttons.
                hud.setScaleDPad("UpArrow", 1.2);
                break;

            //If the user is pressing to right arrow...
            case "RightArrow":

                direction = Direction.RIGHT;
                moveAmountY = 0;

                if(!isCollidingnonCollideable() || !lastPressedArrow.equals(direction.name().substring(0, 1) + direction.name().substring(1, direction.name().length()).toLowerCase(Locale.ENGLISH) + "Arrow")){

                    if(!isGliding)
                        movePlayer((int) (speed * speedRatio), moveAmountY, "RightArrow");
                    else
                        movePlayer((int) (glidingSpeed * speedRatio), moveAmountY, "RightArrow");

                    isMoving = true;
                    if(!isGliding)
                        setAnimationType(RIGHT_RUNNING);
                    else
                        setAnimationType(RIGHT_GLIDING);
                }else
                    isMoving = false;

                if(!isGliding && isMoving)
                    setAnimationType(RIGHT_RUNNING);

                //We move the player, also we shrink other dpad buttons.
                hud.setScaleDPad("RightArrow", 1.2);
                break;

            //If the user is pressing to down arrow...
            case "DownArrow":

                isGliding = false;
                isClimbing = true;
                isMoving = true;
                moveAmountX = 0;

                setAnimationType(CLIMBING);
                movePlayer(moveAmountX, (int) (speed * speedRatio), "DownArrow");

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

                if(!isClimbing){
                    isGliding = false;
                    attack();

                    //We make the action, also we shrink other action buttons.
                    hud.setScaleActions("XButton", 1.2);
                }
                break;

            //If the user is pressing to A Button, Attack.
            case "AButton":

                if(!isClimbing){
                    isGliding = false;

                    if(onSurface){

                        velY = -1 * screenHeight / 18;
                        jump = true;
                        gravityEnabled = false;

                        if(direction == Direction.RIGHT){
                            setAnimationType(RIGHT_JUMPING);
                        }else if(direction == Direction.LEFT){
                            setAnimationType(LEFT_JUMPING);
                        }

                        //We make the action, also we shrink other action buttons.
                        hud.setScaleActions("AButton", 1.2);
                    }
                }
                break;

            //If the user is pressing to Y Button, Glide.
            case "YButton":

                if(!isClimbing){
                    if(!onSurface){
                        isGliding = true;
                    }

                    if(!onSurface){
                        if(direction == Direction.RIGHT){
                            setAnimationType(RIGHT_GLIDING);
                        }else if(direction == Direction.LEFT){
                            setAnimationType(LEFT_GLIDING);
                        }
                    }else{

                        if(isMoving){
                            if(direction == Direction.LEFT)
                                setAnimationType(LEFT_RUNNING);
                            else if(direction == Direction.RIGHT)
                                setAnimationType(RIGHT_RUNNING);
                        }else{
                            if(direction == Direction.LEFT)
                                setAnimationType(LEFT_IDLE);
                            else if(direction == Direction.RIGHT)
                                setAnimationType(RIGHT_IDLE);
                        }
                    }

                    if(isMoving){

                        if(direction.name().equals("RIGHT"))
                            moveAmountX = Math.abs((int) glidingSpeed);
                        else
                            moveAmountX = Math.abs((int) glidingSpeed) * -1;
                    }

                    //We make the action, also we shrink other action buttons.
                    hud.setScaleActions("YButton", 1.2);
                }
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
                attack[j] = Bitmap.createBitmap(spriteSheet, j * 140, 550, 140, 120);
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
                left_idle[j] = flipBitmap(Bitmap.createBitmap(spriteSheet, j * 140, 0, 140, 120));
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

        currentAnimation = type;
    }

    private int getAnimationType(){

        return currentAnimation;
    }


    public static Bitmap flipBitmap(Bitmap d){

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

    public int getGlidingSpeed(){
        return (int) glidingSpeed;
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

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isClimbing(){
        return isClimbing;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isGliding(){
        return isGliding;
    }

    public void setGliding(boolean gliding){
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
