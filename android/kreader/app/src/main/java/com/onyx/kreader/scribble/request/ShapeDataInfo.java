package com.onyx.kreader.scribble.request;

import android.content.Context;
import com.onyx.kreader.host.math.PageInfo;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.scribble.data.ShapeDataProvider;
import com.onyx.kreader.scribble.data.ShapeModel;
import com.onyx.kreader.scribble.data.ShapePage;
import com.onyx.kreader.scribble.shape.ShapeFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuzeng on 6/7/16.
 */
public class ShapeDataInfo {

    private Map<String, ShapePage> shapePageMap = new HashMap<String, ShapePage>();
    public boolean canUndoShape;
    public boolean canRedoShape;

    public final ShapePage getShapePage(final String pageName) {
        return shapePageMap.get(pageName);
    }

    public boolean loadUserShape(final Context context, final String docUniqueId, final List<PageInfo> visiblePages) {
        for(PageInfo pageInfo: visiblePages) {
            final ShapePage shapePage = ShapePage.createPage(context, docUniqueId, pageInfo.getName(), null);
            final List<ShapeModel> modelList = ShapeDataProvider.loadShapeList(context, docUniqueId, pageInfo.getName(), null);
            for(ShapeModel model : modelList) {
                shapePage.addShapeFromModel(ShapeFactory.shapeFromModel(model));
            }
            shapePageMap.put(pageInfo.getName(), shapePage);
        }
        return true;
    }

}
