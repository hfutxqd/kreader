package com.onyx.kreader.note.bridge;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.kreader.note.NoteManager;
import com.onyx.kreader.utils.DeviceUtils;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by zhuzeng on 9/19/16.
 */
public class RawEventProcessor extends NoteEventProcessorBase {


    private static final int EV_SYN = 0x00;
    private static final int EV_KEY = 0x01;
    private static final int EV_ABS = 0x03;

    private static final int ABS_X = 0x00;
    private static final int ABS_Y = 0x01;
    private static final int ABS_PRESSURE = 0x18;

    private static final int BTN_TOUCH = 0x14a;
    private static final int BTN_TOOL_PEN = 0x140;
    private static final int BTN_TOOL_RUBBER = 0x141;
    private static final int BTN_TOOL_PENCIL = 0x143;

    private static final int PEN_SIZE = 0;


    private volatile int px, py, pressure;
    private volatile boolean erasing = false;
    private volatile boolean lastErasing = false;
    private volatile boolean pressed = false;
    private volatile boolean lastPressed = false;
    private volatile boolean stop = false;
    private volatile boolean reportData = false;
    private String inputDevice = "/dev/input/event1";
    private volatile Matrix inputToScreenMatrix;
    private volatile Matrix screenToViewMatrix;
    private volatile float[] srcPoint = new float[2];
    private volatile float[] dstPoint = new float[2];
    private volatile TouchPointList touchPointList;
    private Handler handler = new Handler(Looper.getMainLooper());
    private ExecutorService singleThreadPool = null;

    public RawEventProcessor(final NoteManager p) {
        super(p);
    }

    public void update(final Matrix screenMatrix, final Matrix viewMatrix, final Rect rect) {
        this.inputToScreenMatrix = screenMatrix;
        this.screenToViewMatrix = viewMatrix;
        setLimitRect(rect);
    }

    public void start() {
        stop = false;
        reportData = true;
        clearInternalState();
        submitJob();
    }

    public void resume() {
        reportData = true;
    }

    public void pause() {
        reportData = false;
    }

    public void quit() {
        reportData = false;
        stop = true;
        clearInternalState();
        shutdown();
    }

    private void clearInternalState() {
        pressed = false;
        lastErasing = false;
        lastPressed = false;
    }

    private void shutdown() {
        getSingleThreadPool().shutdown();
        singleThreadPool = null;
    }

    private ExecutorService getSingleThreadPool()   {
        if (singleThreadPool == null) {
            singleThreadPool = Executors.newSingleThreadExecutor(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable runnable) {
                    Thread thread = new Thread(runnable);
                    thread.setPriority(Thread.MAX_PRIORITY);
                    return thread;
                }
            });
        }
        return singleThreadPool;
    }

    private void submitJob() {
        getSingleThreadPool().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    detectInputDevicePath();
                    readLoop();
                } catch (Exception e) {
                }
            }
        });
    }

    private void readLoop() throws Exception {
        DataInputStream in = new DataInputStream(new FileInputStream(inputDevice));
        byte[] data = new byte[16];
        while (!stop) {
            in.readFully(data);
            ByteBuffer wrapped = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
            processInputEvent(wrapped.getLong(), wrapped.getShort(), wrapped.getShort(), wrapped.getInt());
        }
    }

    private void detectInputDevicePath() {
        inputDevice = DeviceUtils.detectInputDevicePath();
    }

    private void processInputEvent(long ts, int type, int code, int value) {
        if (type == EV_ABS) {
            if (code == ABS_X) {
                px = value;
            } else if (code == ABS_Y) {
                py = value;
            } else if (code == ABS_PRESSURE) {
                pressure = value;
            }
        } else if (type == EV_SYN) {
            if (pressed) {
                if (!lastPressed) {
                    lastPressed = pressed;
                    pressReceived(px, py, pressure, PEN_SIZE, ts, erasing);
                } else {
                    moveReceived(px, py, pressure, PEN_SIZE, ts, erasing);
                }
            } else {
                releaseReceived(px, py, pressure, PEN_SIZE, ts, erasing);
            }
        } else if (type == EV_KEY) {
            if (code ==  BTN_TOUCH)  {
                erasing = false;
                lastErasing = false;
                pressed = value > 0;
                lastPressed = false;
            } else if (code == BTN_TOOL_PENCIL || code == BTN_TOOL_PEN) {
                erasing = false;
            } else if (code == BTN_TOOL_RUBBER) {
                pressed = value > 0;
                erasing = true;
                lastErasing = true;
            }
        }
    }

    /**
     * Use screen matrix to map from touch device to screen with correct orientation.
     * Use view matrix to map from screen to view.
     * finally we getById points inside view. we may need the page matrix
     * to map points from view to page.
     * @param touchPoint
     * @return
     */
    private TouchPoint mapInputToScreenPoint(final TouchPoint touchPoint) {
        dstPoint[0] = touchPoint.x;
        dstPoint[1] = touchPoint.y;
        if (inputToScreenMatrix != null) {
            srcPoint[0] = touchPoint.x;
            srcPoint[1] = touchPoint.y;
            inputToScreenMatrix.mapPoints(dstPoint, srcPoint);
        }
        touchPoint.x = dstPoint[0];
        touchPoint.y = dstPoint[1];
        return touchPoint;
    }

    /**
     * map points from screen to view.
     * @param touchPoint
     * @return
     */
    private TouchPoint mapScreenPointToView(final TouchPoint touchPoint) {
        dstPoint[0] = touchPoint.x;
        dstPoint[1] = touchPoint.y;
        if (screenToViewMatrix != null) {
            srcPoint[0] = touchPoint.x;
            srcPoint[1] = touchPoint.y;
            screenToViewMatrix.mapPoints(dstPoint, srcPoint);
        }
        touchPoint.x = dstPoint[0];
        touchPoint.y = dstPoint[1];
        return touchPoint;
    }

    private boolean addToList(final TouchPoint touchPoint, boolean create) {
        if (touchPointList == null) {
            if (!create) {
                return false;
            }
            touchPointList = new TouchPointList(600);
        }

        if (!inLimitRect(touchPoint.x, touchPoint.y)) {
            return false;
        }

        if (touchPoint != null && touchPointList != null) {
            touchPointList.add(touchPoint);
        }
        return true;
    }

    private boolean isReportData() {
        return reportData;
    }

    private void pressReceived(int x, int y, int pressure, int size, long ts, boolean erasing) {
        if (!isReportData()) {
            return;
        }
        if (erasing) {
            erasingPressReceived(x, y, pressure, size, ts);
        } else {
            drawingPressReceived(x, y, pressure, size, ts);
        }
    }

    private void moveReceived(int x, int y, int pressure, int size, long ts, boolean erasing) {
        if (!isReportData()) {
            return;
        }
        if (erasing) {
            erasingMoveReceived(x, y, pressure, size, ts);
        } else {
            drawingMoveReceived(x, y, pressure, size, ts);
        }
    }

    private void releaseReceived(int x, int y, int pressure, int size, long ts, boolean erasing) {
        if (!isReportData()) {
            return;
        }
        if (erasing) {
            erasingReleaseReceived(x, y, pressure, size, ts);
        } else {
            drawingReleaseReceived(x, y, pressure, size, ts);
        }
    }

    private void erasingPressReceived(int x, int y, int pressure, int size, long ts) {
    }

    private void erasingMoveReceived(int x, int y, int pressure, int size, long ts) {
    }

    private void erasingReleaseReceived(int x, int y, int pressure, int size, long ts) {
    }

    private void drawingPressReceived(int x, int y, int pressure, int size, long ts) {
        final TouchPoint touchPoint = new TouchPoint(x, y, pressure, size, ts);
        final TouchPoint screen = new TouchPoint(mapInputToScreenPoint(touchPoint));
        mapScreenPointToView(touchPoint);
        if (hitTest(touchPoint.x, touchPoint.y) == null || !inLimitRect(touchPoint.x, touchPoint.y)) {
            return;
        }
        touchPoint.normalize(getLastPageInfo());
        final Shape shape = getNoteManager().createNewShape(getLastPageInfo());
        getNoteManager().onDownMessage(shape);
        shape.onDown(touchPoint, screen);
    }

    private void drawingMoveReceived(int x, int y, int pressure, int size, long ts) {
        final TouchPoint touchPoint = new TouchPoint(x, y, pressure, size, ts);
        final TouchPoint screen = new TouchPoint(mapInputToScreenPoint(touchPoint));
        mapScreenPointToView(touchPoint);
        if (!isInValidRegion(touchPoint.x, touchPoint.y)) {
            return;
        }
        final Shape shape = getNoteManager().getCurrentShape();
        if (shape == null) {
            return;
        }
        touchPoint.normalize(getLastPageInfo());
        shape.onMove(touchPoint, screen);
    }

    private void drawingReleaseReceived(int x, int y, int pressure, int size, long ts) {
        final TouchPoint touchPoint = new TouchPoint(x, y, pressure, size, ts);
        final TouchPoint screen = new TouchPoint(mapInputToScreenPoint(touchPoint));
        mapScreenPointToView(touchPoint);
        if (!isInValidRegion(touchPoint.x, touchPoint.y)) {
            return;
        }
        final Shape shape = getNoteManager().getCurrentShape();
        if (shape == null) {
            return;
        }
        touchPoint.normalize(getLastPageInfo());
        shape.onUp(touchPoint, screen);
        getNoteManager().resetCurrentShape();
        resetLastPageInfo();
        invokeDFBShapeFinished(shape);
    }

    private void invokeDFBShapeFinished(final Shape shape) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                getCallback().onDFBShapeFinished(shape);
            }
        });
    }
}
