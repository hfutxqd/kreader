package com.onyx.kreader.scribble.shape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.kreader.scribble.EPDRenderer;
import com.onyx.kreader.scribble.data.TouchPoint;
import com.onyx.kreader.scribble.data.TouchPointList;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 4/26/16.
 */
public class EPDShape implements Shape {

    static public final UpdateMode updateMode = UpdateMode.DU;
    private RectF boundingRect = new RectF();
    private TouchPointList normalizedPoints = new TouchPointList();
    private String uniqueId;

    /**
     * rectangle, circle, etc.
     * @return
     */
    public int getType() {
        return ShapeFactory.SHAPE_NORMAL_SCRIBBLE;
    }

    public void setUniqueId(final String id) {
        uniqueId = id;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public int getZOrder() {
        return 0;
    }

    public void setZOrder(int order) {

    }

    public int getColor() {
        return 0;
    }

    public void setColor(int color) {

    }

    public float getStrokeWidth() {
        return 0.0f;
    }

    public void setStrokeWidth(final float width) {}

    public boolean supportDFB() {
        return true;
    }


    public RectF getBoundingRect() {
        return null;
    }

    public void moveTo(final float x, final float y) {

    }

    public void resize(final float width, final float height) {}
    public int getOrientation() {
        return 0;
    }

    public void setOrientation(int orientation) {}


    public void onDown(final TouchPoint normalizedPoint, final TouchPoint screenPoint) {
        EPDRenderer.moveTo(screenPoint.x, screenPoint.y, getStrokeWidth());
        boundingRect.union(normalizedPoint.x, normalizedPoint.y);
        normalizedPoints.add(normalizedPoint);
    }

    public void onMove(final TouchPoint normalizedPoint, final TouchPoint screenPoint) {
        EPDRenderer.quadTo(screenPoint.x, screenPoint.y, updateMode);
        boundingRect.union(normalizedPoint.x, normalizedPoint.y);
        normalizedPoints.add(normalizedPoint);
    }

    public void onUp(final TouchPoint normalizedPoint, final TouchPoint screenPoint) {
        EPDRenderer.quadTo(screenPoint.x, screenPoint.y, updateMode);
        boundingRect.union(normalizedPoint.x, normalizedPoint.y);
        normalizedPoints.add(normalizedPoint);
    }

    public void addPoints(final TouchPointList points) {
        normalizedPoints.addAll(points);
    }

    public void render(final Matrix matrix, final Canvas canvas, final Paint paint) {
    }

    /**
     * check with normalized point.
     * @param x
     * @param y
     * @return
     */
    public boolean hitTest(final float x, final float y) {
        if (!boundingRect.contains(x, y)) {
            return false;
        }
        return true;
    }

    public final TouchPointList getNormalizedPoints() {
        return normalizedPoints;
    }
}
