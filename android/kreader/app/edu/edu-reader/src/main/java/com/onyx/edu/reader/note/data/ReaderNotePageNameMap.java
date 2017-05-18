package com.onyx.edu.reader.note.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by zhuzeng on 9/16/16.
 * pageName A --- ["uuidA", "uuidB", "uuidC"],
 * pageName B --- ["uuidX"]
 * for pageName it's specified by the caller, usually it's document page number or file name (for image file)
 * for sub page unique id, it's always generated by note document.
 */

public class ReaderNotePageNameMap {

    private LinkedHashMap<String, List<String>> data = new LinkedHashMap<>();

    public ReaderNotePageNameMap() {
    }

    public void add(final String pageName, final String subPageUniqueId) {
        getPageList(pageName, true).add(subPageUniqueId);
    }

    public void add(final String pageName, int index, final String subPageUniqueId) {
        getPageList(pageName, true).add(index, subPageUniqueId);
    }

    public List<String> getPageList(final String pageName, boolean create) {
        if (data.containsKey(pageName)) {
            return data.get(pageName);
        }
        if (!create) {
            return null;
        }
        List<String> list = new ArrayList<>();
        data.put(pageName, list);
        return list;
    }

    public void remove(final String pageName) {
        data.remove(pageName);
    }

    public boolean remove(final String pageName, final String subPageUniqueId) {
        final List<String> list = getPageList(pageName, false);
        if (list == null) {
            return false;
        }
        return list.remove(subPageUniqueId);
    }

    public int size() {
        return data.size();
    }

    public int size(final String pageName) {
        final List<String> list = getPageList(pageName, false);
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public void clear() {
        data.clear();
    }

    public LinkedHashMap<String, List<String>> getData() {
        return data;
    }

    public void setData(LinkedHashMap<String, List<String>> data) {
        this.data = data;
    }

    public void copyFrom(final ReaderNotePageNameMap map) {
        data.clear();
        data.putAll(map.getData());
    }

    public List<String> nameList() {
        return new ArrayList<>(data.keySet());
    }
}
