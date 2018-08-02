package com.ngdroidapp.GameObjects;

import android.graphics.Canvas;
import android.graphics.Region;

import com.ngdroidapp.GameObjects.Abstracts_Interfaces.GroundForEverything;

public class Ground extends GroundForEverything{

    public Ground(Background background, int screenWidth, int screenHeight){

        super(background, screenWidth, screenHeight);
    }

    public void draw(Canvas canvas){

        ground.reset();

        ground.moveTo(0, (int) (690 * heightRatio));
        ground.lineTo((int) (410 * widthRatio), (int) (650 * heightRatio));
        ground.lineTo((int) (1050 * widthRatio), (int) (560 * heightRatio));
        ground.lineTo((int) (1390 * widthRatio), (int) (510 * heightRatio));
        ground.lineTo((int) (1670 * widthRatio), (int) (420 * heightRatio));
        ground.lineTo((int) (1840 * widthRatio), (int) (420 * heightRatio));
        ground.lineTo((int) (2290 * widthRatio), (int) (510 * heightRatio));
        ground.lineTo((int) (2500 * widthRatio), (int) (700 * heightRatio));
        ground.lineTo((int) (2700 * widthRatio), (int) (700 * heightRatio));
        ground.lineTo((int) (2900 * widthRatio), (int) (680 * heightRatio));
        ground.lineTo((int) (3070 * widthRatio), (int) (650 * heightRatio));
        ground.lineTo((int) (3240 * widthRatio), (int) (610 * heightRatio));
        //Below
        ground.lineTo((int) (3240 * widthRatio), screenHeight);
        ground.lineTo(0, screenHeight);
        ground.lineTo(0, (int) (660 * heightRatio));
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
