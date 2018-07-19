package istanbul.gamelab.ngdroid.base;

import android.graphics.Canvas;

import com.ngdroidapp.NgApp;


/**
 * Created by noyan on 24.06.2016.
 * Nitra Games Ltd.
 */


public abstract class BaseCanvas {
    protected NgApp root;

    protected final String TAG = this.getClass().getSimpleName();

    public BaseCanvas(NgApp ngApp) {
        root = ngApp;
    }

    public abstract void setup();
    public abstract void update();
    public abstract void draw(Canvas canvas);
    public void onGuiClick(int objectId) {}
    public void onDialogueHide(int objectId) {}
    public void onNotificationHide(int objectId) {}
    public void onNgResult(int request, int response, String data) {}
    public void onConnected() {}
    public void keyPressed(int key){}
    public void keyReleased(int key){}
    public abstract boolean backPressed();
    public abstract void touchDown(int x, int y, int id);
    public abstract void touchMove(int x, int y, int id);
    public abstract void touchUp(int x, int y, int id);
    public void surfaceChanged(int width, int height){}
    public void surfaceCreated(){}
    public void surfaceDestroyed(){}
    public void pause(){}
    public void resume(){}
    public void reloadTextures(){}
    public void showNotify(){}
    public void hideNotify(){}

    public int getWidth() {
        return root.appManager.getScreenWidth();
    }
    public int getHeight() {
        return root.appManager.getScreenHeight();
    }
    public int getWidthHalf() {
        return root.appManager.getScreenWidthHalf();
    }
    public int getHeightHalf() {
        return root.appManager.getScreenHeightHalf();
    }
}
