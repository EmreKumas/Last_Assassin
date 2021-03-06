package com.ngdroidapp.GameObjects.Grounds_Obstacles;

import android.graphics.Canvas;
import android.graphics.Region;

import com.ngdroidapp.GameObjects.Abstracts_Interfaces.Obstacles;
import com.ngdroidapp.GameObjects.Background;

public class Obstacle_AfterBridge extends Obstacles{

    public Obstacle_AfterBridge(Background background, int screenWidth, int screenHeight){

        super(background, screenWidth, screenHeight);
    }

    public void draw(Canvas canvas){

        ground.reset();

        ground.moveTo((int) (2360  * widthRatio), (int) (250 * heightRatio));
        ground.lineTo((int) (2360 * widthRatio), (int) (130 * heightRatio));
        ground.lineTo((int) (2500 * widthRatio), (int) (130 * heightRatio));
        ground.lineTo((int) (2500 * widthRatio), (int) (250 * heightRatio));
        ground.lineTo((int) (2360 * widthRatio), (int) (250 * heightRatio));
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
