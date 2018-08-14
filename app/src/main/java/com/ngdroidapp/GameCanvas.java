package com.ngdroidapp;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;

import com.ngdroidapp.GameObjects.Abstracts_Interfaces.GroundForEverything;
import com.ngdroidapp.GameObjects.Abstracts_Interfaces.Obstacles;
import com.ngdroidapp.GameObjects.Enemy;
import com.ngdroidapp.GameObjects.GroundAboveLadder;
import com.ngdroidapp.GameObjects.GroundAfterBridge;
import com.ngdroidapp.GameObjects.Background;
import com.ngdroidapp.GameObjects.Bridge;
import com.ngdroidapp.GameObjects.Direction;
import com.ngdroidapp.GameObjects.Foreground;
import com.ngdroidapp.GameObjects.Ground;
import com.ngdroidapp.GameObjects.GroundLeftAboveLadder;
import com.ngdroidapp.GameObjects.Ladder;
import com.ngdroidapp.GameObjects.Obstacle_AfterBridge;
import com.ngdroidapp.GameObjects.Obstacle_RightMost;
import com.ngdroidapp.GameObjects.Player;
import com.ngdroidapp.GameObjects.ThingToHoldPlayer;
import com.ngdroidapp.OnScreenControls.HUD;
import com.ngdroidapp.OnScreenControls.TouchControl;

import java.util.Arrays;

import istanbul.gamelab.ngdroid.base.BaseCanvas;

public class GameCanvas extends BaseCanvas{

    private Background background;
    private Foreground foreground;

    private GroundForEverything ground;
    private GroundForEverything bridge;
    private GroundForEverything groundAfterBridge;
    private GroundForEverything groundAboveLadder;
    private GroundForEverything groundLeftAboveLadder;

    private Obstacles obstacle_afterBridge;
    private Obstacles obstacle_rightMost;

    private Ladder ladder;
    private ThingToHoldPlayer thingToHoldPlayer;

    private Player player;
    private Enemy enemy1;

    private HUD hud;

    private TouchControl touchControl;

    private double widthRatio, heightRatio;

    @SuppressWarnings("WeakerAccess")
    public GameCanvas(NgApp ngApp){
        super(ngApp);
    }

    public void setup(){

        widthRatio = (double) getWidth() / 1280;
        heightRatio = (double) getHeight() / 720;

        touchControl = new TouchControl();

        background = new Background("Level_1_Background.jpg", root, touchControl);
        foreground = new Foreground("Level_1_Foreground.png", root, background);
        hud = new HUD(root);

        ground = new Ground(background, getWidth(), getHeight());
        bridge = new Bridge(background, getWidth(), getHeight());
        groundAfterBridge = new GroundAfterBridge(background, getWidth(), getHeight());
        groundAboveLadder = new GroundAboveLadder(background, getWidth(), getHeight());
        groundLeftAboveLadder = new GroundLeftAboveLadder(background, getWidth(), getHeight());
        ladder = new Ladder(background, getWidth(), getHeight());
        thingToHoldPlayer = new ThingToHoldPlayer(background, getWidth(), getHeight());

        obstacle_afterBridge = new Obstacle_AfterBridge(background, getWidth(), getHeight());
        obstacle_rightMost = new Obstacle_RightMost(background, getWidth(), getHeight());

        player = new Player(root, background, hud, touchControl,
                Arrays.asList(ground, bridge, thingToHoldPlayer, groundAfterBridge, groundAboveLadder, groundLeftAboveLadder),
                Arrays.asList(obstacle_afterBridge, obstacle_rightMost),
                ladder);
        enemy1 = new Enemy(root, background, player, "OrangeShirtBoy", (int) (2775 * widthRatio),
                (int) (3350 * widthRatio), (int) (70 * heightRatio));

        //Setting the player's coordinates.
        player.setCoordinates((getWidth() / 4) - player.getDestinationWidth(), (int) (getHeight() / 1.6) - player.getDestinationHeight());

        ground.setPaint(Color.TRANSPARENT);
        bridge.setPaint(Color.TRANSPARENT);
        thingToHoldPlayer.setPaint(Color.TRANSPARENT);
        groundAfterBridge.setPaint(Color.TRANSPARENT);
        groundAboveLadder.setPaint(Color.TRANSPARENT);
        groundLeftAboveLadder.setPaint(Color.TRANSPARENT);
        ladder.setPaint(Color.TRANSPARENT);
        obstacle_afterBridge.setPaint(Color.TRANSPARENT);
        obstacle_rightMost.setPaint(Color.TRANSPARENT);
//
//        ground.setPaint(Color.RED);
//        bridge.setPaint(Color.BLUE);
//        thingToHoldPlayer.setPaint(Color.WHITE);
//        groundAfterBridge.setPaint(Color.GREEN);
//        groundAboveLadder.setPaint(Color.RED);
//        groundLeftAboveLadder.setPaint(Color.BLUE);
//        ladder.setPaint(Color.CYAN);
//        obstacle_afterBridge.setPaint(Color.MAGENTA);
//        obstacle_rightMost.setPaint(Color.LTGRAY);
    }

    public void update(){

        background.update(player, hud, player.isMoving());
        player.update();
        enemy1.update();
        foreground.update();

    }

    public void draw(Canvas canvas){

        background.draw(canvas);
        player.draw(canvas);
        enemy1.draw(canvas);
        foreground.draw(canvas);
        ground.draw(canvas);
        bridge.draw(canvas);
        groundAfterBridge.draw(canvas);
        groundAboveLadder.draw(canvas);
        groundLeftAboveLadder.draw(canvas);
        thingToHoldPlayer.draw(canvas);
        ladder.draw(canvas);
        obstacle_afterBridge.draw(canvas);
        obstacle_rightMost.draw(canvas);
        hud.draw(canvas);

        //FPS
        root.gui.drawText(canvas, "FPS: " + root.appManager.getFrameRate() + " / " + root.appManager.getFrameRateTarget(),
                getWidth() - (int) (getWidth() / 5.5), getHeight() / 16, 0);
    }

    public void touchDown(int x, int y, int id){

        //Accepts two fingers at most.
        if(id < 2){

            //If the user touches to DPAD and is not touching to DPAD with the other ID.
            if(hud.checkAnyCollisionDPad(x, y) && !touchControl.isOtherTouchDpad(id)){

                //Lastly, we need to check if the button is editable.
                if(hud.returnButton(hud.pressedButtonDPad(x, y)).isEditable()){
                    player.dpadPressed(hud.pressedButtonDPad(x, y), hud);
                    touchControl.addTouch(id, new Point(x, y), true, false);
                }
            }
            //If the user touches to Action Buttons and is not touching to Action Buttons with the other ID.
            else if(hud.checkAnyCollisionActions(x, y) && !touchControl.isOtherTouchAction(id)){

                if(hud.returnButton(hud.pressedButtonActions(x, y)).isEditable()){
                    player.actionsPressed(hud.pressedButtonActions(x, y), hud);
                    touchControl.addTouch(id, new Point(x, y), false, true);
                }
            }
        }
    }

    public void touchMove(int x, int y, int id){

        //Accepts two fingers at most. And if that ID exists...
        if(id < 2 && touchControl.doesExist(id)){

            //If player is not hovering over the D-Pad button...
            if(touchControl.getDpadPressed(id) && !touchControl.getActionButtonPressed(id))
                if(!hud.checkCollisionDPad(hud.pressedButtonDPad(touchControl.getTouch(id).x, touchControl.getTouch(id).y), x, y)){

                    player.setMoving(false);
                    hud.scaleEverythingToSmallDPad(1.2);
                    touchControl.updateTouch(id, -100, -100);

                    if(!player.isClimbing()){
                        if(player.getLastDirection() == Direction.LEFT){
                            player.setAnimationType(Player.LEFT_IDLE);
                        }else if(player.getLastDirection() == Direction.RIGHT){
                            player.setAnimationType(Player.RIGHT_IDLE);
                        }
                    }
                }

            //Continue movement if user is hovering over on DPad and getDpadPressed is true and player is not moving.
            if(touchControl.getDpadPressed(id) && !player.isMoving())
                if(hud.checkAnyCollisionDPad(x, y) && hud.returnButton(hud.pressedButtonDPad(x, y)).isEditable()){

                    player.dpadPressed(hud.pressedButtonDPad(x, y), hud);
                    touchControl.updateTouch(id, x, y);
                }
        }
    }

    public void touchUp(int x, int y, int id){

        if(id < 2 && touchControl.doesExist(id)){

            //if the released button is an action button, disable gliding action.
            if(touchControl.isActionButtonPressed()){
                if(touchControl.getActionButtonPressed(id) && hud.pressedButtonActions(touchControl.getTouch(id).x, touchControl.getTouch(id).y).equals("YButton")){

                    player.setGliding(false);

                    if(player.isMoving()){
                        if(player.getLastDirection() == Direction.LEFT)
                            player.setAnimationType(Player.LEFT_RUNNING);
                        else if(player.getLastDirection() == Direction.RIGHT)
                            player.setAnimationType(Player.RIGHT_RUNNING);
                    }else{
                        if(player.getLastDirection() == Direction.LEFT)
                            player.setAnimationType(Player.LEFT_IDLE);
                        else if(player.getLastDirection() == Direction.RIGHT)
                            player.setAnimationType(Player.RIGHT_IDLE);
                    }
                }
            }

            //If the user releases the DPAD...
            if(touchControl.getDpadPressed(id)){
                hud.revertScaleToOriginalDPad();
                player.setMoving(false);

                //If player is not gliding or climbing, set it back to idle pose.
                if(!player.isGliding() && !player.isClimbing()){

                    if(player.getLastDirection() == Direction.LEFT){
                        player.setAnimationType(Player.LEFT_IDLE);
                    }else if(player.getLastDirection() == Direction.RIGHT){
                        player.setAnimationType(Player.RIGHT_IDLE);
                    }
                }
            }

            //If the user releases the Action Buttons...
            else if(touchControl.getActionButtonPressed(id))
                hud.revertScaleToOriginalActions();

            //Removes the touch.
            touchControl.removeTouch(id);
        }
    }

    public boolean backPressed(){

        System.exit(0);
        return true;
    }
}
