package com.ngdroidapp.OnScreenControls;

import android.graphics.Point;

/**
 * This class checks the user touches.
 */
public class TouchControl{

    private Point touches[];
    private boolean dpadPressed[];
    private boolean actionButtonPressed[];

    public TouchControl(){

        touches = new Point[2];
        dpadPressed = new boolean[2];
        actionButtonPressed = new boolean[2];
    }

    /**
     * This method adds a touch based on where the user touched.
     */
    public void addTouch(int touchID, Point point, boolean dpadPressed, boolean actionButtonPressed){

        touches[touchID] = point;
        this.dpadPressed[touchID] = dpadPressed;
        this.actionButtonPressed[touchID] = actionButtonPressed;

    }

    /**
     * This method removes the given touchID.
     */
    public void removeTouch(int touchID){

        touches[touchID] = null;
        dpadPressed[touchID] = actionButtonPressed[touchID] = false;
    }

    /**
     * This method checks if the given touchID exists.
     */
    public boolean doesExist(int touchID){

        return touches[touchID] != null;
    }

    /**
     * This method takes a parameter ID and checks if the other ID is touching to DPAD.
     */
    public boolean isOtherTouchDpad(int id){

        if(id == 0)
            id = 1;
        else
            id = 0;

        return dpadPressed[id];
    }
    /**
     * This method takes a parameter ID and checks if the other ID is touching to Action Buttons.
     */
    public boolean isOtherTouchAction(int id){

        if(id == 0)
            id = 1;
        else
            id = 0;

        return actionButtonPressed[id];
    }

    /**
     * This method checks if the given ID is touching to DPAD.
     */
    public boolean getDpadPressed(int id){
        return dpadPressed[id];
    }

    /**
     * This method checks if any of the touches is touching to DPAD.
     */
    public boolean isDpadPressing(){

        for(boolean id : dpadPressed){

            if(id)
                return true;
        }

        return false;
    }

    /**
     * This method checks if any of the touches is touching to Action Buttons.
     */
    public boolean isActionButtonPressed(){

        for(boolean id : actionButtonPressed){

            if(id)
                return true;
        }

        return false;
    }

    /**
     * This method checks if the given ID is touching to Action Buttons.
     */
    public boolean getActionButtonPressed(int id){
        return actionButtonPressed[id];
    }

    /**
     * This method returns the given touchID's point.
     */
    public Point getTouch(int id){

        return touches[id];
    }


    /**
     * This method updates the touch given as ID.
     */
    public void updateTouch(int id, int x, int y){

        touches[id].x = x;
        touches[id].y = y;

    }
}
