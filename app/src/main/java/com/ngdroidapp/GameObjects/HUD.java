package com.ngdroidapp.GameObjects;

import android.graphics.Canvas;
import android.graphics.Point;

import com.ngdroidapp.NgApp;

public class HUD{

    private HUD_Elements hud_elements[];

    private HUD_Elements leftArrowHUD;
    private HUD_Elements upArrowHUD;
    private HUD_Elements rightArrowHUD;
    private HUD_Elements downArrowHUD;

    private HUD_Elements XButton;
    private HUD_Elements AButton;
    private HUD_Elements YButton;

    private int screenWidth, screenHeight;

    private Point touchPoint;

    public HUD(NgApp root){

        touchPoint = new Point();

        leftArrowHUD = new HUD_Elements("LeftArrow", "Left_Arrow_HUD.png", root);
        upArrowHUD = new HUD_Elements("UpArrow", "Up_Arrow_HUD.png", root);
        rightArrowHUD = new HUD_Elements("RightArrow", "Right_Arrow_HUD.png", root);
        downArrowHUD = new HUD_Elements("DownArrow", "Down_Arrow_HUD.png", root);

        XButton = new HUD_Elements("XButton", "X_Button_HUD.png", root);
        AButton = new HUD_Elements("AButton", "A_Button_HUD.png", root);
        YButton = new HUD_Elements("YButton", "Y_Button_HUD.png", root);

        screenWidth = root.appManager.getScreenWidth();
        screenHeight = root.appManager.getScreenHeight();

        //Will be used for loops.
        hud_elements = new HUD_Elements[7];
        hud_elements[0] = leftArrowHUD;
        hud_elements[1] = upArrowHUD;
        hud_elements[2] = rightArrowHUD;
        hud_elements[3] = downArrowHUD;
        hud_elements[4] = XButton;
        hud_elements[5] = AButton;
        hud_elements[6] = YButton;

        //PLACING HUD ELEMENTS INTO THE LEFT-BOTTOM CORNER OF THE SCREEN
        leftArrowHUD.setCoordinates(screenWidth / 50, screenHeight / 1.435);
        upArrowHUD.setCoordinates(screenWidth / 10.4, screenHeight / 1.8);
        rightArrowHUD.setCoordinates(screenWidth / 6.72, screenHeight / 1.435);
        downArrowHUD.setCoordinates(screenWidth / 10.4, screenHeight / 1.285);

        //PLACING THE X-A-Y BUTTONS INTO THE RIGHT-BOTTOM CORNER OF THE SCREEN
        XButton.setCoordinates(screenWidth / 1.29, screenHeight / 1.24);
        AButton.setCoordinates(screenWidth / 1.125, screenHeight / 1.24);
        YButton.setCoordinates(screenWidth / 1.125, screenHeight / 1.66);

        //Setting the edibility.
        upArrowHUD.setEditable(false);
        downArrowHUD.setEditable(false);
    }

    public void draw(Canvas canvas){

        leftArrowHUD.draw(canvas);
        upArrowHUD.draw(canvas);
        rightArrowHUD.draw(canvas);
        downArrowHUD.draw(canvas);

        XButton.draw(canvas);
        AButton.draw(canvas);
        YButton.draw(canvas);

        //DRAWING THE COLLISION DETECTORS FOR D-PAD BUTTONS.
        leftArrowHUD.drawLeftArrowShape(canvas, leftArrowHUD.getDestinationX(), leftArrowHUD.getDestinationY(), leftArrowHUD.getDestinationHeight(),
                (int) (leftArrowHUD.getDestinationWidth() / 1.71), (int) (leftArrowHUD.getDestinationWidth() / 2.4));
        upArrowHUD.drawUpArrowShape(canvas, upArrowHUD.getDestinationX(), upArrowHUD.getDestinationY(), upArrowHUD.getDestinationWidth(),
                (int) (upArrowHUD.getDestinationHeight() / 1.58), (int) (upArrowHUD.getDestinationHeight() / 2.725));
        rightArrowHUD.drawRightArrowShape(canvas, rightArrowHUD.getDestinationX(), rightArrowHUD.getDestinationY(), rightArrowHUD.getDestinationHeight(),
                (int) (rightArrowHUD.getDestinationWidth() / 1.71), (int) (rightArrowHUD.getDestinationWidth() / 2.4));
        downArrowHUD.drawDownArrowShape(canvas, downArrowHUD.getDestinationX(), downArrowHUD.getDestinationY(), downArrowHUD.getDestinationWidth(),
                (int) (downArrowHUD.getDestinationHeight() / 1.58), (int) (downArrowHUD.getDestinationHeight() / 2.725));

        //DRAWING THE COLLISION DETECTORS FOR ACTION BUTTONS.
        XButton.drawCircleShape(canvas, XButton.getDestinationX(), XButton.getDestinationY(), XButton.getDestinationHeight() / 2);
        AButton.drawCircleShape(canvas, AButton.getDestinationX(), AButton.getDestinationY(), AButton.getDestinationHeight() / 2);
        YButton.drawCircleShape(canvas, YButton.getDestinationX(), YButton.getDestinationY(), YButton.getDestinationHeight() / 2);
    }

    /**
     * This method is used when we click a dpad button, other dpad buttons, except the one the user clicked, will shrink.
     */
    public void setScaleDPad(String pressedArrow, double scaleRatio){

        //Firstly, all dpad buttons will be reset to original sizes.
        revertScaleToOriginalDPad();

        //All buttons, except the one the user clicked, shrinks.
        for(int i = 0; i < 4; i++){

            if(hud_elements[i].getName().equals(pressedArrow))
                continue;
            hud_elements[i].setScale(screenWidth, screenHeight, scaleRatio);
        }

        switch(pressedArrow){

            //If the user clicked to left arrow...
            case "LeftArrow":

                //All buttons coordinates will change depending on the new size of theirs.
                leftArrowHUD.setCoordinates(screenWidth / 50, screenHeight / 1.435);
                upArrowHUD.setCoordinates(screenWidth / 9.44,screenHeight / 1.71);
                rightArrowHUD.setCoordinates(screenWidth / 6.5,screenHeight / 1.41);
                downArrowHUD.setCoordinates(screenWidth / 9.44,screenHeight / 1.28);

                break;
            //If the user clicked to up arrow...
            case "UpArrow":

                //All buttons coordinates will change depending on the new size of theirs.
                upArrowHUD.setCoordinates(screenWidth / 10.4, screenHeight / 1.8);
                leftArrowHUD.setCoordinates(screenWidth / 28.3,screenHeight / 1.41);
                rightArrowHUD.setCoordinates(screenWidth / 6.5,screenHeight / 1.41);
                downArrowHUD.setCoordinates(screenWidth / 9.44,screenHeight / 1.28);

                break;
            //If the user clicked to right arrow...
            case "RightArrow":

                //All buttons coordinates will change depending on the new size of theirs.
                rightArrowHUD.setCoordinates(screenWidth / 6.72, screenHeight / 1.435);
                leftArrowHUD.setCoordinates(screenWidth / 28.3,screenHeight / 1.41);
                upArrowHUD.setCoordinates(screenWidth / 9.44,screenHeight / 1.71);
                downArrowHUD.setCoordinates(screenWidth / 9.44,screenHeight / 1.28);

                break;
            //If the user clicked to down arrow...
            case "DownArrow":

                //All buttons coordinates will change depending on the new size of theirs.
                downArrowHUD.setCoordinates(screenWidth / 10.4, screenHeight / 1.285);
                leftArrowHUD.setCoordinates(screenWidth / 28.3,screenHeight / 1.41);
                upArrowHUD.setCoordinates(screenWidth / 9.44,screenHeight / 1.71);
                rightArrowHUD.setCoordinates(screenWidth / 6.5,screenHeight / 1.41);

                break;
        }
    }

    /**
     * This method is used when we click an action button, other action buttons, except the one the user clicked, will shrink.
     */
    public void setScaleActions(String pressedActionButton, double scaleRatio){

        //Firstly, all action buttons will be reset to original sizes.
        revertScaleToOriginalActions();

        //All buttons, except the one the user clicked, shrinks.
        for(int i = 4; i < 7; i++){

            if(hud_elements[i].getName().equals(pressedActionButton))
                continue;
            hud_elements[i].setScale(screenWidth, screenHeight, scaleRatio);
        }

        switch(pressedActionButton){

            //If the user clicked to X Button...
            case "XButton":

                //All buttons coordinates will change depending on the new size of theirs.
                XButton.setCoordinates(screenWidth / 1.29, screenHeight / 1.24);
                AButton.setCoordinates(screenWidth / 1.116, screenHeight / 1.215);
                YButton.setCoordinates(screenWidth / 1.116, screenHeight / 1.59);

                break;
            //If the user clicked to A Button...
            case "AButton":

                //All buttons coordinates will change depending on the new size of theirs.
                AButton.setCoordinates(screenWidth / 1.125, screenHeight / 1.24);
                XButton.setCoordinates(screenWidth / 1.27, screenHeight / 1.215);
                YButton.setCoordinates(screenWidth / 1.116, screenHeight / 1.59);

                break;
            //If the user clicked to Y Button...
            case "YButton":

                //All buttons coordinates will change depending on the new size of theirs.
                YButton.setCoordinates(screenWidth / 1.125, screenHeight / 1.66);
                XButton.setCoordinates(screenWidth / 1.27, screenHeight / 1.215);
                AButton.setCoordinates(screenWidth / 1.116, screenHeight / 1.215);

                break;
        }
    }

    /**
     * This method reverts the scale of the dpad buttons to their original sizes and sets their coordinates.
     */
    public void revertScaleToOriginalDPad(){

        for(int i = 0; i < 4; i++)
            hud_elements[i].setScale(screenWidth, screenHeight, 1);

        leftArrowHUD.setCoordinates(screenWidth / 50, screenHeight / 1.435);
        upArrowHUD.setCoordinates(screenWidth / 10.4, screenHeight / 1.8);
        rightArrowHUD.setCoordinates(screenWidth / 6.72, screenHeight / 1.435);
        downArrowHUD.setCoordinates(screenWidth / 10.4, screenHeight / 1.285);
    }

    /**
     * This method reverts the scale of the action buttons to their original sizes and sets their coordinates.
     */
    public void revertScaleToOriginalActions(){

        for(int i = 4; i < 7; i++)
            hud_elements[i].setScale(screenWidth, screenHeight, 1);

        XButton.setCoordinates(screenWidth / 1.29, screenHeight / 1.24);
        AButton.setCoordinates(screenWidth / 1.125, screenHeight / 1.24);
        YButton.setCoordinates(screenWidth / 1.125, screenHeight / 1.66);
    }

    /**
     * This method scales every dpad buttons sizes to small.
     */
    public void scaleEverythingToSmallDPad(double scaleRatio){

        for(int i = 0; i < 4; i++)
            hud_elements[i].setScale(screenWidth, screenHeight, scaleRatio);

        leftArrowHUD.setCoordinates(screenWidth / 28.3,screenHeight / 1.41);
        upArrowHUD.setCoordinates(screenWidth / 9.44,screenHeight / 1.71);
        rightArrowHUD.setCoordinates(screenWidth / 6.5,screenHeight / 1.41);
        downArrowHUD.setCoordinates(screenWidth / 9.44,screenHeight / 1.28);
    }

    /**
     * This method checks if parameters x and y is in the range of any dpad buttons.
     */
    public boolean checkAnyCollisionDPad(int x, int y){

        touchPoint.x = x;
        touchPoint.y = y;

        return leftArrowHUD.isPressing(touchPoint) || upArrowHUD.isPressing(touchPoint) || rightArrowHUD.isPressing(touchPoint) || downArrowHUD.isPressing(touchPoint);
    }

    /**
     * This methods checks if the parameter is touching to the corresponding DPad button.
     */
    public boolean checkCollisionDPad(String currentPressedDpad, int x, int y){

        touchPoint.x = x;
        touchPoint.y = y;

        boolean returnValue = false;

        switch(currentPressedDpad){

            case "LeftArrow":

                returnValue = leftArrowHUD.isPressing(touchPoint);
                break;
            case "UpArrow":

                returnValue = upArrowHUD.isPressing(touchPoint);
                break;
            case "RightArrow":

                returnValue = rightArrowHUD.isPressing(touchPoint);
                break;
            case "DownArrow":

                returnValue = downArrowHUD.isPressing(touchPoint);
                break;
        }

        return returnValue;
    }

    /**
     * This method checks if parameters x and y is in the range of any action buttons.
     */
    public boolean checkAnyCollisionActions(int x, int y){

        touchPoint.x = x;
        touchPoint.y = y;

        return XButton.isPressing(touchPoint) || AButton.isPressing(touchPoint) || YButton.isPressing(touchPoint);
    }

    /**
     * This method returns the name of the pressed dpad button depending on the x and y coordinates.
     */
    public String pressedButtonDPad(int x, int y){

        touchPoint.x = x;
        touchPoint.y = y;

        if(leftArrowHUD.isPressing(touchPoint))
            return "LeftArrow";
        else if(upArrowHUD.isPressing(touchPoint))
            return "UpArrow";
        else if(rightArrowHUD.isPressing(touchPoint))
            return "RightArrow";
        else if(downArrowHUD.isPressing(touchPoint))
            return "DownArrow";

        return "";
    }

    /**
     * This method returns the name of the pressed action button depending on the x and y coordinates.
     */
    public String pressedButtonActions(int x, int y){

        touchPoint.x = x;
        touchPoint.y = y;

        if(XButton.isPressing(touchPoint))
            return "XButton";
        else if(AButton.isPressing(touchPoint))
            return "AButton";

        return "YButton";
    }

    public HUD_Elements returnButton(String name){

        switch(name){

            case "LeftArrow":
                return leftArrowHUD;
            case "UpArrow":
                return upArrowHUD;
            case "RightArrow":
                return rightArrowHUD;
            case "DownArrow":
                return downArrowHUD;
            case "XButton":
                return XButton;
            case "AButton":
                return AButton;
            case "YButton":
                return YButton;
        }

        return null;
    }

    public HUD_Elements getLeftArrowHUD(){
        return leftArrowHUD;
    }

    public HUD_Elements getUpArrowHUD(){
        return upArrowHUD;
    }

    public HUD_Elements getRightArrowHUD(){
        return rightArrowHUD;
    }

    public HUD_Elements getDownArrowHUD(){
        return downArrowHUD;
    }
}
