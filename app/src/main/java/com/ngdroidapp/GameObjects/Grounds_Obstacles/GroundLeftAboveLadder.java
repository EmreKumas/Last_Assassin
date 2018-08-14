package com.ngdroidapp.GameObjects.Grounds_Obstacles;

import android.graphics.Canvas;
import android.graphics.Region;

import com.ngdroidapp.GameObjects.Abstracts_Interfaces.AirThings;
import com.ngdroidapp.GameObjects.Abstracts_Interfaces.GroundForEverything;
import com.ngdroidapp.GameObjects.Background;

public class GroundLeftAboveLadder extends GroundForEverything implements AirThings{

    public GroundLeftAboveLadder(Background background, int screenWidth, int screenHeight){
        super(background, screenWidth, screenHeight);
    }

    @Override
    public void draw(Canvas canvas){

        ground.reset();

        ground.moveTo((int) (990  * widthRatio), (int) (-560 * heightRatio));
        ground.lineTo((int) (990  * widthRatio), (int) (-620 * heightRatio));
        ground.lineTo((int) (2070  * widthRatio), (int) (-620 * heightRatio));
        ground.lineTo((int) (2070  * widthRatio), (int) (-560 * heightRatio));
        ground.lineTo((int) (990  * widthRatio), (int) (-560 * heightRatio));
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
