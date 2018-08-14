package com.ngdroidapp.GameObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import com.ngdroidapp.GameObjects.Abstracts_Interfaces.GroundForEverything;
import com.ngdroidapp.NgApp;

import java.util.ArrayList;
import java.util.Random;

import istanbul.gamelab.ngdroid.util.Utils;

/**
 * Created by Hande OKTAY on 3.08.2018.
 */

public class Enemy{

    private Rect source;
    private RectF destination;
    private Direction direction;
    private NgApp ngApp;
    private Background background;
    private Animations animations;
    private Player player;

    private int destinationWidth, destinationHeight;
    private int sourceX, sourceY;
    private int xMin, xMax, x, y;
    private boolean isPlayerCloseToZombie = false;
    private boolean setDirection;
    private String zombieName;

    private double widthRatio, heightRatio;

    //arraylists for different zombie types
    private ArrayList<ArrayList<Bitmap[]>> allZombies; //arraylist contains all added diffrent zombie types and their action frames
    private ArrayList<Bitmap[]> zombie1;
    private ArrayList<Bitmap[]> zombie2;
    private ArrayList<Bitmap[]> zombie3;

    //enemy animation types
    private static final int RIGHT_ATTACK = 0;
    private static final int RIGHT_DEAD = 1;
    private static final int RIGHT_HURT = 2;
    private static final int RIGHT_IDLE = 3;
    private static final int RIGHT_RUN = 4;
    private static final int RIGHT_WALK = 5;
    private static final int LEFT_ATTACK = 6;
    private static final int LEFT_DEAD = 7;
    private static final int LEFT_HURT = 8;
    private static final int LEFT_IDLE = 9;
    private static final int LEFT_RUN = 10;
    private static final int LEFT_WALK = 11;


    public Enemy(NgApp root, Background background, Player player, String zombieName, int xMin, int xMax, int y){

        this.xMin = xMin;
        this.xMax = xMax;
        this.zombieName = zombieName;
        this.y = y;
        this.background = background;
        ngApp = root;
        this.player = player;

        widthRatio = (double) root.appManager.getScreenWidth() / 1280;
        heightRatio = (double) root.appManager.getScreenHeight() / 720;

        animations = new Animations();
        x = createRandomLocation();

        source = new Rect();
        destination = new RectF();

        sourceX = 0;
        sourceY = 0;
        direction = Direction.RIGHT;
        setDirection = true;
        destinationWidth = Player.destinationWidth;
        destinationHeight = Player.destinationHeight;

        allZombies = new ArrayList<>();
        zombie1 = new ArrayList<>();
        zombie2 = new ArrayList<>();
        zombie3 = new ArrayList<>();

        setupFrames(zombieName);
        setZombieAnimationType(RIGHT_WALK);
        setUpZombieCoordinates(x, y);
    }

    public void update(){

        moveEnemy(2); //default walk movement

     /*   //move the zombie faster if the player comes closer
        if (Math.abs(super.getDestinationX() - x) < (ngApp.appManager.getScreenWidth() / 10) || Math.abs(super.getDestinationY() - y) < ngApp.appManager.getScreenHeight() / 10 ) {

            isPlayerCloseToZombie = true;
            if (setDirection) {
                if (direction == Direction.RIGHT) {
                    setZombieAnimationType(RIGHT_RUN);
                } else if (direction == Direction.LEFT) {
                    setZombieAnimationType(LEFT_RUN);
                }
                setDirection = false;
            }
            moveEnemy(5);

        } else {
            isPlayerCloseToZombie = false;
        } */

        if(!isPlayerCloseToZombie){
            if(setDirection){
                if(direction == Direction.RIGHT){
                    setZombieAnimationType(RIGHT_WALK); //RIGHT WALK
                }else if(direction == Direction.LEFT){
                    setZombieAnimationType(LEFT_WALK); //LEFT WALK
                }
                setDirection = false;
            }
        }

        animations.update();
        System.out.println("xxx current frame: " + animations.getCurrentFrame());
        System.out.println("xxx current delay: " + animations.getDelay());

    }

    public void draw(Canvas canvas){

        destination.setEmpty();

        source.set(0, 0, animations.getFrames().getWidth(), animations.getFrames().getHeight());
        destination.set(x, y - (int) ((ngApp.appManager.getScreenHeight() / 5.5)), x + (int) ((ngApp.appManager.getScreenWidth() / 7.5)), y);

        destination.offset(-1 * background.getSourceX() / (1.2f / (float) widthRatio),
                -1 * (background.getSourceY() - (2160 - background.getSourceHeight())) / (1.2f / (float) heightRatio));

        canvas.drawBitmap(animations.getFrames(), source, destination, null);
    }


    //this method moves the enemy with given speed
    private void moveEnemy(int moveAmountX){

        //xMax xMin are the location range that the zombie will move
        if(x >= xMax){
            direction = Direction.LEFT;
            setDirection = true;
        }else if(x <= xMin){
            direction = Direction.RIGHT;
            setDirection = true;
        }

        if(direction == Direction.LEFT)
            x -= moveAmountX;
        else if(direction == Direction.RIGHT)
            x += moveAmountX;
    }


    //this method sets the default x location of the enemy between given minimum and maximum x values when the enemy is first created.
    private int createRandomLocation(){

        Random random = new Random();
        return random.nextInt(xMax - xMin) + xMin;
    }


    //creates the frames according to given zombie type
    private void setupFrames(String zombieName){

        try{
            switch(zombieName){

                case "ArmlessBoy":
                    break;
                case "BlackHairGirl":
                    break;
                case "OrangeShirtBoy":
                    createOrangeShirtBoySprite();
                    break;
                default:
                    break;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    //sets the desired animation type for the enemy
    private void setZombieAnimationType(final int actionType){

        animations.setFrames(zombie1.get(actionType));

        if(actionType == RIGHT_ATTACK || actionType == LEFT_ATTACK){
            animations.setDelay(30);
        }else
            animations.setDelay(75);
    }


    private void createOrangeShirtBoySprite(){

        try{
            Bitmap spritesheet = Utils.loadImage(ngApp, "zombie_1.png");

            //RIGHT ATTACK frames
            Bitmap[] right_attack = new Bitmap[6];
            for(int j = 0; j < 6; j++){
                right_attack[j] = Bitmap.createBitmap(spritesheet, j * 200, 0, 200, 160);
            }
            zombie1.add(right_attack);


            //RIGHT DEAD frames
            Bitmap[] right_dead = new Bitmap[7];
            for(int j = 0; j < 7; j++){
                right_dead[j] = Bitmap.createBitmap(spritesheet, j * 200, 160, 200, 160);
            }
            zombie1.add(right_dead);

            //RIGHT HURT frames
            Bitmap[] right_hurt = new Bitmap[5];
            for(int j = 0; j < 5; j++){
                right_hurt[j] = Bitmap.createBitmap(spritesheet, j * 200, 320, 200, 160);
            }
            zombie1.add(right_hurt);

            //RIGHT_IDLE FRAMES
            Bitmap[] right_idle = new Bitmap[4];
            for(int j = 0; j < 4; j++){
                right_idle[j] = Bitmap.createBitmap(spritesheet, j * 200, 480, 200, 160);
            }
            zombie1.add(right_idle);

            //RIGHT RUN frames
            Bitmap[] right_run = new Bitmap[9];
            for(int j = 0; j < 9; j++){
                right_run[j] = Bitmap.createBitmap(spritesheet, j * 170, 640, 170, 160);
            }
            zombie1.add(right_run);


            //RIGHT WALK frames
            Bitmap[] right_walk = new Bitmap[6];
            for(int j = 0; j < 6; j++){
                right_walk[j] = Bitmap.createBitmap(spritesheet, j * 200, 805, 200, 155);
            }
            zombie1.add(right_walk);


            //LEFT ATTACK frames
            Bitmap[] left_attack = new Bitmap[6];
            for(int j = 0; j < 6; j++){
                left_attack[j] = Player.flipBitmap(Bitmap.createBitmap(spritesheet, j * 200, 0, 200, 160));
            }
            zombie1.add(left_attack);

            //LEFT DEAD frames
            Bitmap[] left_dead = new Bitmap[7];
            for(int j = 0; j < 7; j++){
                left_dead[j] = Player.flipBitmap(Bitmap.createBitmap(spritesheet, j * 200, 160, 200, 160));
            }
            zombie1.add(left_dead);

            //LEFT HURT frames
            Bitmap[] left_hurt = new Bitmap[5];
            for(int j = 0; j < 5; j++){
                left_hurt[j] = Player.flipBitmap(Bitmap.createBitmap(spritesheet, j * 200, 320, 200, 160));
            }
            zombie1.add(left_hurt);

            //LEFT_IDLE frames
            Bitmap[] left_idle = new Bitmap[5];
            for(int j = 0; j < 5; j++){
                left_idle[j] = Player.flipBitmap(Bitmap.createBitmap(spritesheet, j * 200, 480, 200, 160));
            }
            zombie1.add(left_idle);

            //LEFT RUN frames
            Bitmap[] left_run = new Bitmap[9];
            for(int j = 0; j < 9; j++){
                left_run[j] = Player.flipBitmap(Bitmap.createBitmap(spritesheet, j * 170, 640, 170, 160));
            }
            zombie1.add(left_run);

            //LEFT WALK frames
            Bitmap[] left_walk = new Bitmap[6];
            for(int j = 0; j < 6; j++){
                left_walk[j] = Player.flipBitmap(Bitmap.createBitmap(spritesheet, j * 200, 805, 200, 155));
            }
            zombie1.add(left_walk);


            System.out.println("");
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    //sets the enemy's default location when its first created.
    private void setUpZombieCoordinates(int x, int y){
        this.x = x;
        this.y = y;

        destination.set(x, y - ngApp.appManager.getScreenHeight() / 12, x + (ngApp.appManager.getScreenWidth() / 20), y);
    }
}
