package com.example.bushu.mineoppskrifter;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


public abstract class SwipeListener implements View.OnTouchListener {
    private int min_distance = 100;
    private float downX, downY, upX, upY;
    View v;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        this.v = v;
        switch(event.getAction()) { // Check vertical and horizontal touches
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                downY = event.getY();
                return true;
            }
            case MotionEvent.ACTION_UP: {
                upX = event.getX();
                upY = event.getY();

                float deltaX = downX - upX;
                float deltaY = downY - upY;

                //HORIZONTAL SCROLL
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    if (Math.abs(deltaX) > min_distance) {
                        // left or right
                        if (deltaX < 0) {
                            this.onLeftToRightSwipe(v);
                            return true;
                        }
                        if (deltaX > 0) {
                            this.onRightToLeftSwipe(v);
                            return true;
                        }
                    } else {
                        //not long enough swipe...
                        return false;
                    }
                }

                return false;
            }
        }
        return false;
    }


    public abstract void onRightToLeftSwipe(View v);

    public abstract void onLeftToRightSwipe(View v);
}
