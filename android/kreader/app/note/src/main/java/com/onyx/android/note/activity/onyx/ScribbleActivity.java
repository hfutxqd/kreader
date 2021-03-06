package com.onyx.android.note.activity.onyx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.note.NoteApplication;
import com.onyx.android.note.R;
import com.onyx.android.note.actions.common.CheckNoteNameLegalityAction;
import com.onyx.android.note.actions.scribble.ClearPageAction;
import com.onyx.android.note.actions.scribble.DocumentDiscardAction;
import com.onyx.android.note.actions.scribble.DocumentSaveAction;
import com.onyx.android.note.actions.scribble.GotoTargetPageAction;
import com.onyx.android.note.actions.scribble.NoteBackgroundChangeAction;
import com.onyx.android.note.actions.scribble.RedoAction;
import com.onyx.android.note.actions.scribble.UndoAction;
import com.onyx.android.note.activity.BaseScribbleActivity;
import com.onyx.android.note.data.PenType;
import com.onyx.android.note.data.ScribbleMenuCategory;
import com.onyx.android.note.data.ScribbleSubMenuID;
import com.onyx.android.note.dialog.DialogNoteNameInput;
import com.onyx.android.note.utils.NoteAppConfig;
import com.onyx.android.note.utils.Utils;
import com.onyx.android.note.view.ScribbleSubMenu;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GAdapterUtil;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.scribble.data.NoteBackgroundType;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.ui.dialog.DialogSetValue;
import com.onyx.android.sdk.ui.view.ContentItemView;
import com.onyx.android.sdk.ui.view.ContentView;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.HashMap;


/**
 * when any button clicked, flush at first and render page, after that always switch to drawing state.
 */
public class ScribbleActivity extends BaseScribbleActivity {
    static final String TAG = ScribbleActivity.class.getCanonicalName();
    private TextView titleTextView;
    private GAdapter adapter;
    private ScribbleSubMenu scribbleSubMenu = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NoteApplication.initWithAppConfig(this);
        setContentView(R.layout.onyx_activity_scribble);
        initSupportActionBarWithCustomBackFunction();
        initToolbarButtons();
    }

    private void initToolbarButtons() {
        titleTextView = (TextView) findViewById(R.id.textView_main_title);
        ImageView addPageBtn = (ImageView) findViewById(R.id.button_add_page);
        ImageView deletePageBtn = (ImageView) findViewById(R.id.button_delete_page);
        ImageView prevPageBtn = (ImageView) findViewById(R.id.button_previous_page);
        ImageView nextPageBtn = (ImageView) findViewById(R.id.button_next_page);
        ImageView undoBtn = (ImageView) findViewById(R.id.button_undo);
        ImageView redoBtn = (ImageView) findViewById(R.id.button_redo);
        ImageView saveBtn = (ImageView) findViewById(R.id.button_save);
        ImageView exportBtn = (ImageView) findViewById(R.id.button_export);
        exportBtn.setVisibility(NoteAppConfig.sharedInstance(this).isEnableExport() ? View.VISIBLE : View.GONE);
        pageIndicator = (Button) findViewById(R.id.button_page_progress);
        ContentView functionContentView = (ContentView) findViewById(R.id.function_content_view);
        functionContentView.setShowPageInfoArea(false);
        functionContentView.setSubLayoutParameter(R.layout.onyx_main_function_item, getItemViewDataMap());
        functionContentView.setCallback(new ContentView.ContentViewCallback() {
            @Override
            public void onItemClick(ContentItemView view) {
                final GObject temp = view.getData();
                syncWithCallback(true, false, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        getScribbleSubMenu().show(ScribbleMenuCategory.translate(GAdapterUtil.getUniqueIdAsIntegerType(temp)));
                    }
                });
            }
        });
        functionContentView.setupContent(1,
                getResources().getInteger(R.integer.onyx_scribble_main_function_cols), getFunctionAdapter(), 0);
        addPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddNewPage();
            }
        });
        deletePageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeletePage();
            }
        });
        prevPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPrevPage();
            }
        });
        nextPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextPage();
            }
        });
        undoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUndo();
            }
        });
        redoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRedo();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSave(false);
            }
        });
        exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onExport();
            }
        });
        pageIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncWithCallback(true, false, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        if (e == null) {
                            showGotoPageDialog();
                        }
                    }
                });
            }
        });
    }

    private void showGotoPageDialog() {
        final int originalVisualPageIndex = currentVisualPageIndex;
        final DialogSetValue dlg = new DialogSetValue();
        Bundle args = new Bundle();
        args.putString(DialogSetValue.ARGS_DIALOG_TITLE, getString(R.string.go_to_page));
        args.putString(DialogSetValue.ARGS_VALUE_TITLE, getString(R.string.current_page));
        args.putInt(DialogSetValue.ARGS_CURRENT_VALUE, originalVisualPageIndex);
        args.putInt(DialogSetValue.ARGS_MAX_VALUE, totalPageCount);
        args.putInt(DialogSetValue.ARGS_MIN_VALUE, 1);
        dlg.setArguments(args);
        dlg.setCallback(new DialogSetValue.DialogCallback() {
            @Override
            public void valueChange(int newValue) {
                int logicalIndex = newValue - 1;
                GotoTargetPageAction<ScribbleActivity> action = new GotoTargetPageAction<>(logicalIndex);
                action.execute(ScribbleActivity.this);
            }

            @Override
            public void done(boolean isValueChange, int newValue) {
                GotoTargetPageAction<ScribbleActivity> action =
                        new GotoTargetPageAction<>(isValueChange ? newValue : originalVisualPageIndex -1);
                action.execute(ScribbleActivity.this);
                syncWithCallback(true, true, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        if (e == null) {
                            dlg.dismiss();
                        }
                    }
                });
            }
        });
        dlg.show(getFragmentManager());
    }

    private void onExport() {
    }

    private void onSave(final boolean finishAfterSave) {
        syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                saveDocument(finishAfterSave);
            }
        });
    }

    private void onRedo() {
        syncWithCallback(false, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                final RedoAction<ScribbleActivity> action = new RedoAction<>();
                action.execute(ScribbleActivity.this);
            }
        });
    }

    private void onUndo() {
        syncWithCallback(false, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                final UndoAction<ScribbleActivity> action = new UndoAction<>();
                action.execute(ScribbleActivity.this);
            }
        });
    }

    private ScribbleSubMenu getScribbleSubMenu() {
        if (scribbleSubMenu == null) {
            scribbleSubMenu = new ScribbleSubMenu(this, shapeDataInfo, (RelativeLayout) this.findViewById(R.id.onyx_activity_scribble),
                    new ScribbleSubMenu.MenuCallback() {
                        @Override
                        public void onItemSelect(@ScribbleSubMenuID.ScribbleSubMenuIDDef int item) {
                            invokeSubMenuItem(item);
                        }

                        @Override
                        public void onCancel() {
                            syncWithCallback(true, true, null);
                        }

                        @Override
                        public void onLayoutStateChanged() {

                        }
                    }, R.id.divider, true
            );
        }
        return scribbleSubMenu;
    }

    private void invokeSubMenuItem(@ScribbleSubMenuID.ScribbleSubMenuIDDef int item) {
        switch (item) {
            //TODO:stroke width need confirm.
            case ScribbleSubMenuID.THICKNESS_ULTRA_LIGHT:
                onStrokeWidthChanged(3.0f, null);
                break;
            case ScribbleSubMenuID.THICKNESS_LIGHT:
                onStrokeWidthChanged(5.0f, null);
                break;
            case ScribbleSubMenuID.THICKNESS_NORMAL:
                onStrokeWidthChanged(7.0f, null);
                break;
            case ScribbleSubMenuID.THICKNESS_BOLD:
                onStrokeWidthChanged(9.0f, null);
                break;
            case ScribbleSubMenuID.THICKNESS_ULTRA_BOLD:
                onStrokeWidthChanged(11.0f, null);
                break;
            case ScribbleSubMenuID.ERASE_PARTIALLY:
                onEraseClicked(true);
                break;
            case ScribbleSubMenuID.ERASE_TOTALLY:
                onEraseClicked(false);
                break;
            case ScribbleSubMenuID.NORMAL_PEN_STYLE:
                onNoteShapeChanged(true, true, ShapeFactory.SHAPE_PENCIL_SCRIBBLE, null);
                break;
            case ScribbleSubMenuID.BRUSH_PEN_STYLE:
                onNoteShapeChanged(true, true, ShapeFactory.SHAPE_BRUSH_SCRIBBLE, null);
                break;
            case ScribbleSubMenuID.LINE_STYLE:
                onNoteShapeChanged(true, false, ShapeFactory.SHAPE_LINE, null);
                break;
            case ScribbleSubMenuID.CIRCLE_STYLE:
                onNoteShapeChanged(true, false, ShapeFactory.SHAPE_CIRCLE, null);
                break;
            case ScribbleSubMenuID.RECT_STYLE:
                onNoteShapeChanged(true, false, ShapeFactory.SHAPE_RECTANGLE, null);
                break;
            case ScribbleSubMenuID.TRIANGLE_STYLE:
                onNoteShapeChanged(true, false, ShapeFactory.SHAPE_TRIANGLE, null);
                break;
            case ScribbleSubMenuID.BG_EMPTY:
                setBackgroundType(NoteBackgroundType.EMPTY);
                onBackgroundChanged();
                break;
            case ScribbleSubMenuID.BG_LINE:
                setBackgroundType(NoteBackgroundType.LINE);
                onBackgroundChanged();
                break;
            case ScribbleSubMenuID.BG_GRID:
                setBackgroundType(NoteBackgroundType.GRID);
                onBackgroundChanged();
                break;
            case ScribbleSubMenuID.BG_MUSIC:
                setBackgroundType(NoteBackgroundType.MUSIC);
                onBackgroundChanged();
                break;
            case ScribbleSubMenuID.BG_MATS:
                setBackgroundType(NoteBackgroundType.MATS);
                onBackgroundChanged();
                break;
            case ScribbleSubMenuID.BG_ENGLISH:
                setBackgroundType(NoteBackgroundType.ENGLISH);
                onBackgroundChanged();
                break;
        }
    }

    private void onBackgroundChanged() {
        final NoteBackgroundChangeAction<ScribbleActivity> changeBGAction =
                new NoteBackgroundChangeAction<>(getBackgroundType(), !getNoteViewHelper().inUserErasing());
        changeBGAction.execute(ScribbleActivity.this, null);
    }

    private HashMap<String, Integer> getItemViewDataMap() {
        HashMap<String, Integer> mapping = new HashMap<>();
        mapping.put(GAdapterUtil.TAG_IMAGE_RESOURCE, R.id.item_img);
        return mapping;
    }

    private GAdapter getFunctionAdapter() {
        if (adapter == null) {
            adapter = new GAdapter();
            adapter.addObject(createFunctionItem(R.drawable.ic_shape, ScribbleMenuCategory.PEN_STYLE));
            adapter.addObject(createFunctionItem(R.drawable.ic_eraser, ScribbleMenuCategory.ERASER));
            adapter.addObject(createFunctionItem(R.drawable.ic_width, ScribbleMenuCategory.PEN_WIDTH));
            adapter.addObject(createFunctionItem(R.drawable.ic_template, ScribbleMenuCategory.BG));
        }
        return adapter;
    }

    @Override
    protected void handleActivityIntent(final Intent intent) {
        super.handleActivityIntent(intent);
        if (StringUtils.isNotBlank(noteTitle)) {
            titleTextView.setText(noteTitle);
        }
    }

    @Override
    protected void cleanUpAllPopMenu() {
        if (getScribbleSubMenu().isShow()) {
            getScribbleSubMenu().dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        getNoteViewHelper().pauseDrawing();
        onSave(true);
    }

    private void saveDocument(boolean finishAfterSave) {
        if (isNewDocument()) {
            saveNewNoteDocument(finishAfterSave);
        } else {
            saveExistingNoteDocument(finishAfterSave);
        }
    }

    private boolean isNewDocument() {
        return (Utils.ACTION_CREATE.equals(activityAction) || StringUtils.isNullOrEmpty(activityAction)) &&
                StringUtils.isNullOrEmpty(noteTitle);
    }

    private void saveNewNoteDocument(final boolean finishAfterSave) {
        final DialogNoteNameInput dialogNoteNameInput = new DialogNoteNameInput();
        Bundle bundle = new Bundle();
        bundle.putString(DialogNoteNameInput.ARGS_TITTLE, getString(R.string.save_note));
        bundle.putString(DialogNoteNameInput.ARGS_HINT, noteTitle);
        bundle.putBoolean(DialogNoteNameInput.ARGS_ENABLE_NEUTRAL_OPTION, true);
        dialogNoteNameInput.setArguments(bundle);
        dialogNoteNameInput.setCallBack(new DialogNoteNameInput.ActionCallBack() {
            @Override
            public boolean onConfirmAction(final String input) {
                final CheckNoteNameLegalityAction<ScribbleActivity> action =
                        new CheckNoteNameLegalityAction<>(input, parentID, NoteModel.TYPE_DOCUMENT, false, false);
                action.execute(ScribbleActivity.this, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        if (action.isLegal()) {
                            saveDocumentWithTitle(input, finishAfterSave);
                        } else {
                            showNoteNameIllegal();
                        }
                    }
                });
                return false;
            }

            @Override
            public void onCancelAction() {
                dialogNoteNameInput.dismiss();
                syncWithCallback(true, true, null);
            }

            @Override
            public void onDiscardAction() {
                dialogNoteNameInput.dismiss();
                final DocumentDiscardAction<ScribbleActivity> discardAction = new DocumentDiscardAction<>(null);
                discardAction.execute(ScribbleActivity.this);
            }
        });
        syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                dialogNoteNameInput.show(getFragmentManager());
            }
        });
    }

    private void saveDocumentWithTitle(final String title, final boolean finishAfterSave) {
        noteTitle = title;
        final DocumentSaveAction<ScribbleActivity> saveAction = new
                DocumentSaveAction<>(shapeDataInfo.getDocumentUniqueId(), noteTitle, finishAfterSave);
        saveAction.execute(ScribbleActivity.this, null);
    }

    private void saveExistingNoteDocument(final boolean finishAfterSave) {
        final DocumentSaveAction<ScribbleActivity> saveAction = new
                DocumentSaveAction<>(shapeDataInfo.getDocumentUniqueId(), noteTitle, finishAfterSave);
        saveAction.execute(ScribbleActivity.this, null);
    }

    private void onNoteShapeChanged(boolean render, boolean resume, int type, BaseCallback callback) {
        setCurrentShapeType(type);
        syncWithCallback(render, resume, callback);
    }

    private void onStrokeWidthChanged(float width, BaseCallback callback) {
        if (shapeDataInfo.isInUserErasing()) {
            setCurrentShapeType(PenType.PENCIL);
        }
        setStrokeWidth(width);
        syncWithCallback(true, true, callback);
    }

    private void onEraseClicked(boolean isPartialErase) {
        if (isPartialErase) {
            setCurrentShapeType(ShapeFactory.SHAPE_ERASER);
            syncWithCallback(true, false, null);
        } else {
            ClearPageAction<ScribbleActivity> action = new ClearPageAction<>();
            action.execute(this, null);
        }
    }

    @Override
    protected void updateDataInfo(final BaseNoteRequest request) {
        super.updateDataInfo(request);
        getScribbleSubMenu().setCurShapeDataInfo(shapeDataInfo);
    }

    private GObject createFunctionItem(final int functionIconRes,
                                       @ScribbleMenuCategory.ScribbleMenuCategoryDef int menuCategory) {
        GObject object = GAdapterUtil.createTableItem(0, 0, functionIconRes, 0, null);
        object.putInt(GAdapterUtil.TAG_UNIQUE_ID, menuCategory);
        return object;
    }

}
