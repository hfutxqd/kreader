package com.onyx.edu.student.action;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.ui.dialog.DialogSetValue;
import com.onyx.edu.student.R;
import com.onyx.edu.student.holder.LibraryDataHolder;

/**
 * Created by suicheng on 2017/4/25.
 */
public class PageGotoAction extends BaseAction<LibraryDataHolder> {

    private FragmentManager fragmentManager;
    private int currentPage = 0;
    private int pages;

    private int selectPage;

    public PageGotoAction(Activity activity, int currentPage, int pages) {
        this.fragmentManager = activity.getFragmentManager();
        this.currentPage = currentPage;
        this.pages = pages;
    }

    @Override
    public void execute(Context context, LibraryDataHolder dataHolder, final BaseCallback baseCallback) {
        final DialogSetValue dlg = new DialogSetValue();
        Bundle args = new Bundle();
        args.putString(DialogSetValue.ARGS_DIALOG_TITLE, context.getString(R.string.go_to_page));
        args.putString(DialogSetValue.ARGS_VALUE_TITLE, context.getString(R.string.current_page));
        args.putInt(DialogSetValue.ARGS_CURRENT_VALUE, currentPage + 1);
        args.putInt(DialogSetValue.ARGS_MAX_VALUE, pages);
        args.putInt(DialogSetValue.ARGS_MIN_VALUE, 1);
        dlg.setArguments(args);
        dlg.setCallback(new DialogSetValue.DialogCallback() {
            @Override
            public void valueChange(int newValue) {
            }

            @Override
            public void done(boolean isValueChange, int newValue) {
                if (!isValueChange) {
                    return;
                }
                selectPage = newValue;
                BaseCallback.invoke(baseCallback, null, null);
            }

            @Override
            public void dismiss() {
            }
        });
        dlg.show(fragmentManager);
    }

    public int getSelectPage() {
        return selectPage;
    }
}
