package com.onyx.android.sdk.scribble.shape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * Created by zhuzeng on 4/19/16.
 */
public class TexShape extends BaseShape  {


    /**
     * rectangle, circle, etc.
     * @return
     */
    public int getType() {
        return ShapeFactory.SHAPE_TEXT;
    }

    public void render(final Canvas canvas, final Paint paint, final Matrix matrix) {
        float left = Math.min(getDownPoint().x, getCurrentPoint().x);
        float height = Math.abs(getDownPoint().y - getCurrentPoint().y);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(height / 2);
        canvas.drawText("Sample text", left, getCurrentPoint().y, paint);
    }

}
