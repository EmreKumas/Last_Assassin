package com.ngdroidapp.GameObjects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;

import com.ngdroidapp.TouchControl;

public class Ground{

    private int screenWidth;
    private int screenHeight;
    private double widthRatio;
    private double heightRatio;

    private Background background;
    private HUD hud;
    private TouchControl touchControl;

    private Path ground;
    private Region region;
    private RectF collisionDetector;

    private Paint paint;

    public Ground(Background background, HUD hud, TouchControl touchControl, int screenWidth, int screenHeight){

        this.background = background;
        this.hud = hud;
        this.touchControl = touchControl;

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        widthRatio = (double) screenWidth / 1280;
        heightRatio = (double) screenHeight / 720;

        ground = new Path();
        region = new Region();
        collisionDetector = new RectF();

        paint = new Paint();

        paint.setColor(Color.TRANSPARENT);
    }

    public void draw(Canvas canvas){

        ground.reset();

        ground.moveTo(0, (int) (670 * heightRatio));
        ground.lineTo((int) (410 * widthRatio), (int) (640 * heightRatio));
        ground.lineTo((int) (1050 * widthRatio), (int) (550 * heightRatio));
        ground.lineTo((int) (1390 * widthRatio), (int) (490 * heightRatio));
        ground.lineTo((int) (1670 * widthRatio), (int) (410 * heightRatio));
        ground.lineTo((int) (1840 * widthRatio), (int) (410 * heightRatio));
        ground.lineTo((int) (2290 * widthRatio), (int) (540 * heightRatio));
        ground.lineTo((int) (2500 * widthRatio), (int) (710 * heightRatio));
        ground.lineTo((int) (2730 * widthRatio), (int) (712 * heightRatio));
        ground.lineTo((int) (2900 * widthRatio), (int) (660 * heightRatio));
        ground.lineTo((int) (3070 * widthRatio), (int) (630 * heightRatio));
        ground.lineTo((int) (3240 * widthRatio), (int) (615 * heightRatio));
        //Below
        ground.lineTo((int) (3240 * widthRatio), screenHeight);
        ground.lineTo(0, screenHeight);
        ground.lineTo(0, (int) (670 * heightRatio));
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
}
