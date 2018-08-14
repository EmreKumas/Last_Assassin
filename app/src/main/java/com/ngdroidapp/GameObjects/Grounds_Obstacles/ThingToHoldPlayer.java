package com.ngdroidapp.GameObjects.Grounds_Obstacles;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.ngdroidapp.GameObjects.Abstracts_Interfaces.GroundForEverything;
import com.ngdroidapp.GameObjects.Background;
import com.ngdroidapp.GameObjects.Player;

public class ThingToHoldPlayer extends GroundForEverything{

    private RectF thingToHold;

    public ThingToHoldPlayer(Background background, int screenWidth, int screenHeight){

        super(background, screenWidth, screenHeight);

        thingToHold = new RectF();
    }

    @Override
    public void draw(Canvas canvas){

        thingToHold.setEmpty();

        thingToHold.set((float)(2290 * widthRatio), (float)(180 * heightRatio), (float)(2370 * widthRatio), (float)(230 * heightRatio));

        //Offsetting the path.
        thingToHold.offset(-1 * background.getSourceX() / (1.2f / (float) widthRatio),
                -1 * (background.getSourceY() - (2160 - background.getSourceHeight())) / (1.2f / (float) heightRatio));

        canvas.drawRect(thingToHold, paint);
    }

    @Override
    public void deleteEverything(){
        thingToHold.setEmpty();
    }

    @Override
    public boolean isColliding(int x, int y){

        return thingToHold.intersect(new RectF(x - Player.destinationWidth, y - Player.destinationHeight, x, y));
    }
}
