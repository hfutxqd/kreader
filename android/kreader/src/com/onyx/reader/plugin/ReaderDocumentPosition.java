package com.onyx.reader.plugin;

/**
 * Created by zhuzeng on 10/2/15.
 */
public interface ReaderDocumentPosition {

    /**
     * Get page number.
     * @return Return 0 based page number.
     */
    public int getPageNumber();

    /**
     * Get the page name.
     * @return get page name string.
     */
    public String getPageName();

    /**
     * Get persistent string representation of object.
     * @return position persistent representation.
     */
    public String getPersistentString();



}
