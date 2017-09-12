package com.onyx.kreader.note.request;

import com.onyx.kreader.note.NoteManager;

/**
 * Created by zhuzeng on 9/28/16.
 */

public class AddNoteSubPageRequest extends ReaderBaseNoteRequest {

    private String pageName;
    private int subPageIndex;

    public AddNoteSubPageRequest(final String pageName, final int subPageIndex) {
        this.pageName = pageName;
        this.subPageIndex = subPageIndex;
    }

    public void execute(final NoteManager noteManager) throws Exception {
        noteManager.getNoteDocument().addPage(pageName, subPageIndex);
    }
}
