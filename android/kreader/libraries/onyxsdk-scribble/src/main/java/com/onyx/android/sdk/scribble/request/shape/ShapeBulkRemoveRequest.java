package com.onyx.android.sdk.scribble.request.shape;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.ShapeModel;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 12/25/15.
 */
public class ShapeBulkRemoveRequest extends BaseNoteRequest {

    private String documentIndex;
    private List<ShapeModel> list = new ArrayList<ShapeModel>();


    public ShapeBulkRemoveRequest(final String doc, final List<ShapeModel> l) {
        documentIndex = doc;
        list.addAll(l);
    }

    @Override
    public void execute(final NoteViewHelper parent) throws Exception {
        // ask scribble provider to removeShape them.
    }
}
