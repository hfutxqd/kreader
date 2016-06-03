package com.onyx.kreader.scribble.data;

import android.graphics.Color;
import android.graphics.RectF;
import com.onyx.kreader.dataprovider.ReaderDatabase;
import com.raizlabs.android.dbflow.annotation.*;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by zhuzeng on 6/3/16.
 * Persistence for shape.
 */
@Table(database = ReaderDatabase.class)
public class Scribble extends BaseModel {

    private static final String TAG = Scribble.class.getSimpleName();
    public static final int INVALID_ID = -1;

    @Column
    @PrimaryKey(autoincrement = true)
    @Index
    public long id = INVALID_ID;

    @Column
    @Unique
    public String md5 = null;

    @Column
    public Date createdAt = null;

    @Column
    public Date updatedAt = null;

    @Column
    public String pageName;

    @Column
    public String subPageName;

    @Column
    public int color = Color.BLACK;

    @Column
    public float thickness = 3.0f;

    //@Column
    public List<TouchPoint> points;

    @Column
    public String position;

    @Column
    public String uniqueId  = null;

    //@Column
    public RectF boundingRect = null;

    @Column
    public int scribbleType;

    @Column
    public String extraAttributes;

    public Scribble() {
    }

    public long getId() {
        return id;
    }

    public void setId(long value) {
        id = value;
    }

    public final String getMd5() {
        return md5;
    }

    public void setMd5(final String value) {
        md5 = value;
    }

    public void setCreatedAt(final Date d) {
        createdAt = d;
    }

    public final Date getCreatedAt() {
        return createdAt;
    }

    public final Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final Date d) {
        updatedAt = d;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(final String name) {
        pageName = name;
    }

    public String getSubPageName() {
        return subPageName;
    }

    public void setSubPageName(final String spn) {
        subPageName = spn;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int c) {
        color = c;
    }

    public float getThickness() {
        return thickness;
    }

    public void setThickness(float t) {
        thickness = t;
    }

    public List<TouchPoint> getPoints() {
        return points;
    }

    public List<TouchPoint> allocatePoints(int size) {
        points = new ArrayList<TouchPoint>(size);
        return points;
    }

    public void setPoints(final List<TouchPoint> pts) {
        points = pts;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(final String pos) {
        position = pos;
    }

    public final String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(final String id) {
        uniqueId = id;
    }

    public String generateUniqueId() {
        return uniqueId = UUID.randomUUID().toString();
    }

    public void updateBoundingRect(final float x, final float y) {
        if (boundingRect == null) {
            boundingRect = new RectF(x, y, x, y);
        } else {
            boundingRect.union(x, y);
        }
    }

    public void setBoundingRect(final RectF rect) {
        boundingRect = rect;
    }

    public final RectF getBoundingRect() {
        return boundingRect;
    }

    public int getScribbleType() {
        return scribbleType;
    }

    public void setScribbleType(int t) {
        scribbleType = t;
    }

    public String getExtraAttributes() {
        return extraAttributes;
    }

    public void setExtraAttributes(final String attributes) {
        extraAttributes = attributes;
    }
}