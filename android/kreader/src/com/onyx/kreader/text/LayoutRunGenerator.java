package com.onyx.kreader.text;

import android.graphics.Rect;
import android.util.Log;
import com.onyx.kreader.formats.model.Paragraph;
import com.onyx.kreader.formats.model.TextModelPosition;
import com.onyx.kreader.formats.model.entry.ParagraphEntry;
import com.onyx.kreader.formats.model.entry.TextParagraphEntry;
import com.onyx.kreader.utils.UnicodeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zengzhu on 3/12/16.
 * Read data from model and generate LayoutRun list. After that the layout engine could use the LayoutRun list easily.
 */
public class LayoutRunGenerator {

    private static boolean debugString = true;
    private int currentRunIndex = 0;
    private List<LayoutRun> runList = new ArrayList<LayoutRun>();
    private final TextModelPosition textModelPosition;

    public LayoutRunGenerator(final TextModelPosition position) {
        textModelPosition = position;
    }

    public int leftRuns() {
        return runList.size() - currentRunIndex;
    }

    public boolean hasNext() {
        if (leftRuns() > 0) {
            return true;
        }
        if (!textModelPosition.isLoadFinished()) {
            return true;
        }
        if (textModelPosition.hasNextParagraph() || textModelPosition.hasNextEntry()) {
            return true;
        }
        return false;
    }

    public final LayoutRun getRun(final Style textStyle) {
        if (leftRuns() <= 0) {
            generate(runList, textStyle, 2000);
        }
        return runList.get(currentRunIndex);
    }

    public void moveToNext() {
        currentRunIndex++;
    }

    public void breakRun(final LayoutRun layoutRun) {
        runList.add(currentRunIndex + 1, layoutRun);
    }

    /**
     * fetch data from model and generate LayoutRun list.
     * @param list
     * @param textStyle
     * @param limit
     */
    public void generate(final List<LayoutRun> list, final Style textStyle, final int limit) {
        while (leftRuns() < limit && textModelPosition.fetchMore()) {
            split(list, textModelPosition, textStyle);
        }
    }

    /**
     * paragraph based splitter.
     * @param list
     * @param textModelPosition
     * @param style
     */
    public static void split(final List<LayoutRun> list, final TextModelPosition textModelPosition, final Style style) {
        while (textModelPosition.hasNextParagraph()) {
            final Paragraph paragraph = textModelPosition.nextParagraph();
            if (paragraph == null) {
                break;
            }
            addParagraphBeginRun(list);
            for (ParagraphEntry paragraphEntry : paragraph.getParagraphEntryList()) {
                if (paragraphEntry instanceof TextParagraphEntry) {
                    final TextParagraphEntry textParagraphEntry = (TextParagraphEntry) paragraphEntry;
                    split(list, textParagraphEntry.getText(), style);
                }
            }
            addParagraphEndRun(list);
        }
    }

    public static void split(final List<LayoutRun> list, final String text, final Style style) {
        int last = 0;
        for(int i = 0; i < text.length(); ++i) {
            Character character = text.charAt(i);
            if (UnicodeUtils.isWhitespace(character)) {
                addLayoutTextRun(list, text, style, last, i, LayoutRun.Type.TYPE_WORD);
                addLayoutTextRun(list, text, style, i, i + 1, LayoutRun.Type.TYPE_SPACING);
                last = i + 1;
            } else if (UnicodeUtils.isCJKCharacter(character)) {
                addLayoutTextRun(list, text, style, last, i, LayoutRun.Type.TYPE_WORD);
                addLayoutTextRun(list, text, style, i, i + 1, LayoutRun.Type.TYPE_WORD);
                last = i + 1;
            } else if (UnicodeUtils.isPunctuation(character)) {
                addLayoutTextRun(list, text, style, last, i, LayoutRun.Type.TYPE_WORD);
                addLayoutTextRun(list, text, style, i, i + 1, LayoutRun.Type.TYPE_PUNCTUATION);
                last = i + 1;
            }
        }
        addLayoutTextRun(list, text, style, last, text.length(), LayoutRun.Type.TYPE_WORD);
    }

    public static boolean addParagraphBeginRun(final List<LayoutRun> list) {
        LayoutRun run = LayoutRun.createParagraphBegin();
        list.add(run);
        return true;
    }

    public static boolean addParagraphEndRun(final List<LayoutRun> list) {
        LayoutRun run = LayoutRun.createParagraphEnd();
        list.add(run);
        return true;
    }

    public static boolean addLayoutTextRun(final List<LayoutRun> list, final String text, final Style style, final int start, final int end, final LayoutRun.Type type) {
        if (end <= start) {
            return false;
        }
        Rect rect = new Rect();
        style.getPaint().getTextBounds(text, start, end, rect);
        float width = style.getPaint().measureText(text, start, end);
        LayoutRun run = LayoutRun.create(text, start, end, width, rect.height(), type);
        list.add(run);
        if (debugString) {
            String subString = text.substring(start, end);
            Log.d("Test", subString);
        }
        return true;
    }

}
