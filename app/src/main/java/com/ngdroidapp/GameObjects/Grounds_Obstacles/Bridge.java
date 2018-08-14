package com.ngdroidapp.GameObjects.Grounds_Obstacles;

import android.graphics.Canvas;
import android.graphics.Region;

import com.ngdroidapp.GameObjects.Abstracts_Interfaces.GroundForEverything;
import com.ngdroidapp.GameObjects.Background;

public class Bridge extends GroundForEverything{

    public Bridge(Background background, int screenWidth, int screenHeight){

        super(background, screenWidth, screenHeight);
    }

    public void draw(Canvas canvas){

        ground.reset();

        ground.moveTo((int) (1875  * widthRatio), (int) (480 * heightRatio));
        ground.lineTo((int) (2375 * widthRatio), (int) (175 * heightRatio));
        ground.lineTo((int) (2400 * widthRatio), (int) (350 * heightRatio));
        ground.lineTo((int) (2050 * widthRatio), (int) (480 * heightRatio));
        ground.lineTo((int) (1850 * widthRatio), (int) (480 * heightRatio));
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
