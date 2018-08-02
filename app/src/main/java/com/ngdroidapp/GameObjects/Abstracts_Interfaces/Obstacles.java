package com.ngdroidapp.GameObjects.Abstracts_Interfaces;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;

import com.ngdroidapp.GameObjects.Background;

abstract public class Obstacles implements nonCollideables{

    protected double widthRatio, heightRatio;

    protected Background background;

    protected Path ground;
    protected Region region;
    protected RectF collisionDetector;

    protected Paint paint;

    public Obstacles(Background background, int screenWidth, int screenHeight){

        this.background = background;

        widthRatio = (double) screenWidth / 1280;
        heightRatio = (double) screenHeight / 720;

        ground = new Path();
        region = new Region();
        collisionDetector = new RectF();

        paint = new Paint();
    }

    abstract public void draw(Canvas canvas);

    @Override
    public boolean isColliding(int x, int y){

        return region.contains(x, y);
    }

    public void setPaint(int color){
        paint.setColor(color);
    }
}
