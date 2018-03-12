package com.onyx.android.note;

import com.onyx.android.note.handler.HandlerManager;
import com.onyx.android.note.note.DrawDataHolder;
import com.onyx.android.note.note.PenEventHandler;
import com.onyx.android.sdk.note.NoteManager;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lxm on 2018/1/31.
 */

public class NoteDataBundle {

    private static final NoteDataBundle ourInstance = new NoteDataBundle();

    public static NoteDataBundle getInstance() {
        return ourInstance;
    }

    private NoteDataBundle() {
    }

    private EventBus eventBus;
    private HandlerManager handlerManager;
    private NoteManager noteManager;
    private PenEventHandler penEventHandler;
    private DrawDataHolder drawDataHolder;

    public EventBus getEventBus() {
        if (eventBus == null) {
            eventBus = new EventBus();
        }
        return eventBus;
    }

    public void post(Object event) {
        getEventBus().post(event);
    }

    public HandlerManager getHandlerManager() {
        if (handlerManager == null) {
            handlerManager = new HandlerManager(getEventBus(), getNoteManager());
        }
        return handlerManager;
    }

    public NoteManager getNoteManager() {
        if (noteManager == null) {
            noteManager = new NoteManager(NoteApp.instance, getEventBus());
        }
        return noteManager;
    }

    public PenEventHandler getPenEventHandler() {
        if (penEventHandler == null) {
            penEventHandler = new PenEventHandler(getEventBus());
        }
        return penEventHandler;
    }

    public DrawDataHolder getDrawDataHolder() {
        if (drawDataHolder == null) {
            drawDataHolder = new DrawDataHolder(getEventBus());
        }
        return drawDataHolder;
    }
}
