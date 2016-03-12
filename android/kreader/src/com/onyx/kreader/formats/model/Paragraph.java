package com.onyx.kreader.formats.model;

import com.onyx.kreader.formats.model.entry.ParagraphEntry;
import com.onyx.kreader.formats.model.entry.TextParagraphEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zengzhu on 2/28/16.
 */
public class Paragraph {

    static public enum ParagraphKind {
        TEXT_PARAGRAPH,
        TREE_PARAGRAPH,
        EMPTY_LINE_PARAGRAPH,
        BEFORE_SKIP_PARAGRAPH,
        AFTER_SKIP_PARAGRAPH,
        END_OF_SECTION_PARAGRAPH,
        PSEUDO_END_OF_SECTION_PARAGRAPH,
        END_OF_TEXT_PARAGRAPH,
        ENCRYPTED_SECTION_PARAGRAPH,
    }

    static public Paragraph create(final ParagraphKind kind) {
        Paragraph paragraph = new Paragraph(kind);
        return paragraph;
    }

    private long physicalPosition;
    private long streamLength;
    private ParagraphKind paragraphKind;
    private List<ParagraphEntry> paragraphEntryList = new ArrayList<ParagraphEntry>();

    public Paragraph(final ParagraphKind kind) {
        paragraphKind = kind;
    }

    public void setPhysicalPosition(final long position) {
        physicalPosition = position;
    }

    public void addEntry(final ParagraphEntry paragraphEntry) {
        paragraphEntryList.add(paragraphEntry);
    }

    public final List<ParagraphEntry> getParagraphEntryList() {
        return paragraphEntryList;
    }

    public final ParagraphEntry getLastEntry() {
        if (paragraphEntryList.isEmpty()) {
            return null;
        }
        return paragraphEntryList.get(paragraphEntryList.size() - 1);
    }

    public final ParagraphEntry getEntry(final int index) {
        if (index >= 0 && index < paragraphEntryList.size()) {
            return paragraphEntryList.get(index);
        }
        return null;
    }

    public final List<ParagraphEntry> getEntryList() {
        return paragraphEntryList;
    }

    public final int getEntryCount() {
        return paragraphEntryList.size();
    }

    public boolean isBeginOfParagraph(final ParagraphEntry paragraphEntry) {
        if (paragraphEntryList.isEmpty()) {
            return false;
        }
        return paragraphEntryList.get(0).equals(paragraphEntry);
    }

    public boolean isEndOfParagraph(final ParagraphEntry paragraphEntry) {
        if (paragraphEntryList.isEmpty()) {
            return false;
        }
        return getLastEntry().equals(paragraphEntry);
    }

    public int available(int sinceEntry) {
        int available = 0;
        final List<ParagraphEntry> entryList = getEntryList();
        if (entryList.size() <= 0) {
            return available;
        }
        if (sinceEntry < 0) {
            sinceEntry = 0;
        }
        for(int i = sinceEntry; i < entryList.size(); ++i) {
            final ParagraphEntry entry = entryList.get(i);
            if (entry instanceof TextParagraphEntry) {
                TextParagraphEntry textParagraphEntry = (TextParagraphEntry) entry;
                available += textParagraphEntry.getText().length();
            }
        }
        return available;
    }

}
