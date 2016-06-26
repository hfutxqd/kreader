package com.onyx.android.note.activity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.onyx.android.note.R;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;

import java.util.Collection;

public class ScribbleActivity extends OnyxAppCompatActivity {

    private SurfaceView surfaceView;
    private NoteViewHelper noteViewHelper = new NoteViewHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scribble);
        initSupportActionBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        surfaceView = (SurfaceView)findViewById(R.id.surface_view);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                cleanup(surfaceView);
                noteViewHelper.setView(surfaceView);
                noteViewHelper.stop();
                noteViewHelper.startDrawing();
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        noteViewHelper.stop();
    }

    private void cleanup(final SurfaceView surfaceView) {
        Rect rect = new Rect(0, 0, surfaceView.getWidth(), surfaceView.getHeight());
        Canvas canvas = surfaceView.getHolder().lockCanvas(rect);
        if (canvas == null) {
            return;
        }

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawRect(rect, paint);
        surfaceView.getHolder().unlockCanvasAndPost(canvas);

    }
}
