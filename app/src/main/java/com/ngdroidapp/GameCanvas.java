package com.ngdroidapp;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;

import com.ngdroidapp.GameObjects.AirObjects;
import com.ngdroidapp.GameObjects.Background;
import com.ngdroidapp.GameObjects.Bridge;
import com.ngdroidapp.GameObjects.Direction;
import com.ngdroidapp.GameObjects.Foreground;
import com.ngdroidapp.GameObjects.Ground;
import com.ngdroidapp.OnScreenControls.HUD;
import com.ngdroidapp.GameObjects.Obstacles;
import com.ngdroidapp.GameObjects.Player;
import com.ngdroidapp.OnScreenControls.TouchControl;

import istanbul.gamelab.ngdroid.base.BaseCanvas;

public class GameCanvas extends BaseCanvas{

    private Background background;
    private Foreground foreground;
    private Ground ground;
    private Bridge bridge;
    private AirObjects airObjects;
    private Obstacles obstacles;
    private Player player;
    private HUD hud;

    private TouchControl touchControl;

    @SuppressWarnings("WeakerAccess")
    public GameCanvas(NgApp ngApp){
        super(ngApp);
    }

    public void setup(){
        touchControl = new TouchControl();

        background = new Background("Level_1_Background.jpg", root, touchControl);
        foreground = new Foreground("Level_1_Foreground.png", root, background);
        hud = new HUD(root);
        ground = new Ground(background, getWidth(), getHeight());
        bridge = new Bridge(background, getWidth(), getHeight());
        airObjects = new AirObjects(background, getWidth(), getHeight());
        obstacles = new Obstacles(background, getWidth(), getHeight());
        player = new Player(root, background, ground, bridge, airObjects, obstacles, hud, touchControl);

        //Setting the player's coordinates.
        player.setCoordinates((getWidth() / 4) - player.getDestinationWidth(), (int) (getHeight() / 1.7) - player.getDestinationHeight());
        ground.setPaint(Color.TRANSPARENT);
        bridge.setPaint(Color.TRANSPARENT);
        airObjects.setPaint(Color.TRANSPARENT);
        obstacles.setPaint(Color.TRANSPARENT);
//        ground.setPaint(Color.RED);
//        bridge.setPaint(Color.BLUE);
//        airObjects.setPaint(Color.GREEN);
//        obstacles.setPaint(Color.MAGENTA);
    }

    public void update(){

        background.update(player, hud, player.isMoving());
        player.update();
        foreground.update();

    }

    public void draw(Canvas canvas){

        background.draw(canvas);
        player.draw(canvas);
        foreground.draw(canvas);
        ground.draw(canvas);
        bridge.draw(canvas);
        airObjects.draw(canvas);
        obstacles.draw(canvas);
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

        player.checkButtonPressesForActions();
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

                    if(player.getLastDirection() == Direction.LEFT){
                        player.setAnimationType(7);
                    }else if(player.getLastDirection() == Direction.RIGHT){
                        player.setAnimationType(0);
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
            if (touchControl.isActionButtonPressed()) {
                if (touchControl.getActionButtonPressed(id)) {
                    player.setGliding(false);
                }
            }

            //If the user releases the DPAD...
            if(touchControl.getDpadPressed(id)){
                hud.revertScaleToOriginalDPad();
                player.setMoving(false);
            }
            //If the user releases the Action Buttons...
            else if(touchControl.getActionButtonPressed(id))
                hud.revertScaleToOriginalActions();

            //Removes the touch.
            touchControl.removeTouch(id);

            if(player.getLastDirection() == Direction.LEFT){
                player.setAnimationType(7);
            }else if(player.getLastDirection() == Direction.RIGHT){
                player.setAnimationType(0);
            }
        }
    }

    public boolean backPressed(){

        System.exit(0);
        return true;
    }
}
