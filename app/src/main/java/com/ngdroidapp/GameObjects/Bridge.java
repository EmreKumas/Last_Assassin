package com.ngdroidapp.GameObjects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;

public class Bridge implements Collideables{

    private double widthRatio;
    private double heightRatio;

    private Background background;

    private Path ground;
    private Region region;
    private RectF collisionDetector;

    private Paint paint;

    public Bridge(Background background, int screenWidth, int screenHeight){

        this.background = background;

        widthRatio = (double) screenWidth / 1280;
        heightRatio = (double) screenHeight / 720;

        ground = new Path();
        region = new Region();
        collisionDetector = new RectF();

        paint = new Paint();
    }

    public void draw(Canvas canvas){

        ground.reset();

        ground.moveTo((int) (1850  * widthRatio), (int) (480 * heightRatio));
        ground.lineTo((int) (2350 * widthRatio), (int) (210 * heightRatio));
        ground.lineTo((int) (2550 * widthRatio), (int) (210 * heightRatio));
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

    public boolean isColliding(int x, int y){

        return region.contains(x, y);
    }

    public void setPaint(int color){
        paint.setColor(color);
    }
}
