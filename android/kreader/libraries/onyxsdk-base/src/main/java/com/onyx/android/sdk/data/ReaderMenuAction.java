package com.onyx.android.sdk.data;

/**
 * Created by joy on 8/25/16.
 */
public enum ReaderMenuAction {
    //main menu
    FONT, FONT_STYLE, FONT_SET_FONT_SIZE, FONT_DECREASE_FONT_SIE, FONT_INCREASE_FONT_SIZE,
    FONT_SET_FONT_FACE, FONT_SET_MORE_FONT_FACE, FONT_SET_INTENT, FONT_SET_NO_INTENT,
    FONT_SET_LINE_SPACING,FONT_SET_PAGE_MARGINS,
    FONT_SET_SMALL_LINE_SPACING, FONT_SET_MIDDLE_LINE_SPACING, FONT_SET_LARGE_LINE_SPACING,
    FONT_DECREASE_LINE_SPACING, FONT_INCREASE_LINE_SPACING,
    FONT_SET_SMALL_PAGE_MARGINS, FONT_SET_MIDDLE_PAGE_MARGINS, FONT_SET_LARGE_PAGE_MARGINS,
    FONT_DECREASE_PAGE_MARGINS, FONT_INCREASE_PAGE_MARGINS,
    IMAGE_REFLOW, GAMMA_CORRECTION, GLYPH_EMBOLDEN,
    ZOOM, ZOOM_IN, ZOOM_OUT, ZOOM_TO_PAGE, ZOOM_TO_WIDTH, ZOOM_BY_CROP_PAGE, ZOOM_BY_CROP_WIDTH, ZOOM_BY_RECT,
    NAVIGATION, NAVIGATION_COMIC_MODE, NAVIGATION_ARTICLE_MODE, NAVIGATION_RESET, NAVIGATION_MORE_SETTINGS,
    NOTE, DIRECTORY_TOC, DIRECTORY_BOOKMARK, DIRECTORY_NOTE, DIRECTORY_SCRIBBLE, NOTE_EXPORT, NOTE_WRITING, NOTE_IMPORT,
    SHOW_ANNOTATION, SHOW_NOTE, ROTATION, ROTATION_ROTATE_0, ROTATION_ROTATE_90, ROTATION_ROTATE_180, ROTATION_ROTATE_270,
    MORE, TTS, FRONT_LIGHT, REFRESH, SLIDESHOW, SETTINGS, GOTO_PAGE, NAVIGATE_BACKWARD, NAVIGATE_FORWARD, DICT, SEARCH, EXIT,JUMP_PAGE,
    NEXT_CHAPTER, PREV_CHAPTER,NATURAL_LIGHT, MANUAL_CROP, NEXT_PAGE, PREV_PAGE, SUBMIT,

    //scribble menu
    SCRIBBLE_WIDTH, SCRIBBLE_WIDTH1, SCRIBBLE_WIDTH2, SCRIBBLE_WIDTH3, SCRIBBLE_WIDTH4, SCRIBBLE_WIDTH5,SCRIBBLE_CUSTOM_WIDTH,
    SCRIBBLE_SHAPE, SCRIBBLE_PENCIL, SCRIBBLE_BRUSH, SCRIBBLE_LINE, SCRIBBLE_CIRCLE, SCRIBBLE_SQUARE,
    SCRIBBLE_TRIANGLE, SCRIBBLE_TRIANGLE_45, SCRIBBLE_TRIANGLE_60, SCRIBBLE_TRIANGLE_90,
    SCRIBBLE_TEXT,
    SCRIBBLE_COLOR, SCRIBBLE_BLACK, SCRIBBLE_BLUE, SCRIBBLE_GREEN, SCRIBBLE_MAGENTA, SCRIBBLE_RED, SCRIBBLE_YELLOW,
    SCRIBBLE_ERASER, SCRIBBLE_ERASER_PART, SCRIBBLE_ERASER_ALL,
    SCRIBBLE_DRAG,
    SCRIBBLE_MINIMIZE, SCRIBBLE_MAXIMIZE,
    SCRIBBLE_PREV_PAGE,
    SCRIBBLE_NEXT_PAGE,
    SCRIBBLE_PAGE_POSITION,
    SCRIBBLE_UNDO, SCRIBBLE_SAVE, SCRIBBLE_REDO, SCRIBBLE_CLOSE
}
