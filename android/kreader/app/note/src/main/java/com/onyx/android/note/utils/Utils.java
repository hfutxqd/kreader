package com.onyx.android.note.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GAdapterUtil;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.scribble.data.NoteModel;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by solskjaer49 on 16/6/24 11:54.
 */

public class Utils {
    public static int screenWidth, screenHeight;
    static final String TAG = Utils.class.getSimpleName();

    public static final String DOCUMENT_ID = "document_id";
    public static final String ITEM_TYPE_TAG = "item_type";
    public static final int TYPE_CREATE = 0;
    public static final int TYPE_GOTO_UP = 1;
    public static final int TYPE_LIBRARY = 2;
    public static final int TYPE_DOCUMENT = 3;

    public static SimpleDateFormat getDateFormat(Locale locale) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale);
    }

    public static void updateVisualInfo(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) {
            Log.w(TAG, "WINDOW_SERVICE is not ready");
            return;
        }
        wm.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
    }

    public static GObject createNewItem(String title, int imageRes) {
        GObject object = GAdapterUtil.createTableItem(title, null, imageRes, 0, null);
        object.putString(GAdapterUtil.TAG_UNIQUE_ID, null);
        object.putInt(ITEM_TYPE_TAG, TYPE_CREATE);
        return object;
    }

    public static GObject createGotoUpItem(String title, int imageRes) {
        GObject object = GAdapterUtil.createTableItem(title, null, imageRes, 0, null);
        object.putString(GAdapterUtil.TAG_UNIQUE_ID, null);
        object.putInt(ITEM_TYPE_TAG, TYPE_GOTO_UP);
        return object;
    }

    public static GObject createLibraryItem(final NoteModel noteModel, final int folderRes) {
        GObject object = GAdapterUtil.createTableItem(noteModel.getTitle(), null, folderRes, 0, null);
        object.putString(GAdapterUtil.TAG_UNIQUE_ID, noteModel.getUniqueId());
        object.putInt(ITEM_TYPE_TAG, TYPE_LIBRARY);
        return object;
    }

    public static GObject createDocumentItem(final NoteModel noteModel, final int docRes) {
        GObject object = GAdapterUtil.createTableItem(noteModel.getTitle(), null, docRes, 0, null);
        object.putString(GAdapterUtil.TAG_UNIQUE_ID, noteModel.getUniqueId());
        object.putInt(ITEM_TYPE_TAG, TYPE_DOCUMENT);
        return object;
    }

    public static GObject createNoteItem(final NoteModel noteModel, final int folderRes, final int docRes) {
        if (noteModel.isLibrary()) {
            return createLibraryItem(noteModel, folderRes);
        }
        return createDocumentItem(noteModel, docRes);
    }

    public static GAdapter adapterFromNoteModelList(final List<NoteModel> noteModelList, final int folderRes, final int docRes) {
        GAdapter adapter = new GAdapter();
        for(NoteModel model : noteModelList) {
            adapter.addObject(createNoteItem(model, folderRes, docRes));
        }
        return adapter;
    }

    public static boolean isNew(final GObject object) {
        return isSameType(object, TYPE_CREATE);
    }

    public static boolean isGotoUp(final GObject object) {
        return isSameType(object, TYPE_GOTO_UP);
    }

    public static boolean isLibrary(final GObject object) {
        return isSameType(object, TYPE_LIBRARY);
    }

    public static boolean isDocument(final GObject object) {
        return isSameType(object, TYPE_DOCUMENT);
    }

    private static boolean isSameType(final GObject object, int value) {
        if (!object.hasKey(ITEM_TYPE_TAG)) {
            return false;
        }
        int type = object.getInt(ITEM_TYPE_TAG);
        return type == value;
    }

}
