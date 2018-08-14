package com.ngdroidapp.GameObjects;

import android.graphics.Canvas;
import android.graphics.Region;

import com.ngdroidapp.GameObjects.Abstracts_Interfaces.AirThings;
import com.ngdroidapp.GameObjects.Abstracts_Interfaces.GroundForEverything;

public class GroundAboveLadder extends GroundForEverything implements AirThings{

    public GroundAboveLadder(Background background, int screenWidth, int screenHeight){
        super(background, screenWidth, screenHeight);
    }

    @Override
    public void draw(Canvas canvas){

        ground.reset();

        ground.moveTo((int) (2050  * widthRatio), (int) (-490 * heightRatio));
        ground.lineTo((int) (2720  * widthRatio), (int) (-490 * heightRatio));
        ground.lineTo((int) (2720  * widthRatio), (int) (-400 * heightRatio));
        ground.lineTo((int) (2050  * widthRatio), (int) (-400 * heightRatio));
        ground.lineTo((int) (2050  * widthRatio), (int) (-490 * heightRatio));
        ground.close();

        //Offsetting the path.
        ground.offset(-1 * background.getSourceX() / (1.2f / (float) widthRatio),
                -1 * (background.getSourceY() - (2160 - background.getSourceHeight())) / (1.2f / (float) heightRatio));

        canvas.drawPath(ground, paint);

        //Computing the bounds and sending them to RectF.
        ground.computeBounds(collisionDetector, true);

        //Setting the region for collision detection.
        region.setPath(ground, new Region((int) collisionDetector.left, (int) collisionDetector.top,
                (int) collisionDetector.right, (int) collisionDetector.bottom));
    }
}
