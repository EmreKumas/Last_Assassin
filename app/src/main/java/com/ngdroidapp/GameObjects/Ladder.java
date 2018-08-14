package com.ngdroidapp.GameObjects;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Ladder{

    @SuppressWarnings({"WeakerAccess", "FieldCanBeLocal"})
    private int screenWidth, screenHeight;
    private double widthRatio, heightRatio;

    private Background background;

    private RectF ladder;

    protected Paint paint;

    public Ladder(Background background, int screenWidth, int screenHeight){

        this.background = background;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        widthRatio = (double) screenWidth / 1280;
        heightRatio = (double) screenHeight / 720;

        ladder = new RectF();

        paint = new Paint();
    }

    public void draw(Canvas canvas){

        ladder.setEmpty();

        ladder.set((float)(2418 * widthRatio), (float)(-491 * heightRatio), (float)(2421 * widthRatio), (float)(-70 * heightRatio));

        //Offsetting the path.
        ladder.offset(-1 * background.getSourceX() / (1.2f / (float) widthRatio),
                -1 * (background.getSourceY() - (2160 - background.getSourceHeight())) / (1.2f / (float) heightRatio));

        canvas.drawRect(ladder, paint);
    }

    public boolean isColliding(int x, int y){

        return ladder.intersect(new RectF(x - Player.destinationWidth, y - Player.destinationHeight, x, y));
    }

    public void setPaint(int color){
        paint.setColor(color);
    }
}
