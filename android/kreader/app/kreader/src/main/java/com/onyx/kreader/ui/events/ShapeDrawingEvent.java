package com.onyx.kreader.ui.events;

import com.onyx.android.sdk.scribble.shape.Shape;

/**
 * Created by zhuzeng on 9/23/16.
 */
public class ShapeDrawingEvent {

    public Shape shape;
    public ShapeDrawingEvent(final Shape s) {
        shape = s;
    }
}
