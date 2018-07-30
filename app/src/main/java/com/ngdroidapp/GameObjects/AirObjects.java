package com.ngdroidapp.GameObjects;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;

public class AirObjects implements Collideables{

    private double widthRatio;
    private double heightRatio;

    private Background background;

    private Path ground;
    private Region region;
    private RectF collisionDetector;

    private Paint paint;

    public AirObjects(Background background, int screenWidth, int screenHeight){

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

        ground.moveTo((int) (2375  * widthRatio), (int) (130 * heightRatio));
        ground.lineTo((int) (2375 * widthRatio), (int) (70 * heightRatio));
        ground.lineTo((int) (3100 * widthRatio), (int) (70 * heightRatio));
        ground.lineTo((int) (3100 * widthRatio), (int) (210 * heightRatio));
        ground.lineTo((int) (2500 * widthRatio), (int) (210 * heightRatio));
        ground.lineTo((int) (2500 * widthRatio), (int) (130 * heightRatio));
        ground.lineTo((int) (2375  * widthRatio), (int) (130 * heightRatio));
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

    @Override
    public boolean isColliding(int x, int y){

        return region.contains(x, y);
    }

    public void setPaint(int color){
        paint.setColor(color);
    }
}
