package com.onyx.android.sdk.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.onyx.android.sdk.data.OnyxDictionaryInfo;
import com.onyx.android.sdk.data.ReaderMenu;
import com.onyx.android.sdk.data.ReaderMenuItem;
import com.onyx.android.sdk.data.ReaderMenuState;
import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.ui.data.ReaderLayerMenuItem;
import com.onyx.android.sdk.ui.data.ReaderLayerMenuState;
import com.onyx.android.sdk.ui.view.ReaderLayerMenuLayout;

import java.net.URI;

/**
 * Created by joy on 6/28/16.
 */
public class DialogReaderMenu extends Dialog {

    private Activity activity;
    private ReaderMenu.ReaderMenuCallback readerMenuCallback;
    private ReaderLayerMenuLayout menuLayout;

    public DialogReaderMenu(Activity activity, ReaderMenu.ReaderMenuCallback menuCallback) {
        super(activity, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        this.activity = activity;
        readerMenuCallback = menuCallback;

        setContentView(R.layout.dialog_reader_menu);
        fitDialogToWindow();
        this.setCanceledOnTouchOutside(true);

        initDialogContent();
    }

    public ReaderLayerMenuLayout getReaderMenuLayout() {
        return menuLayout;
    }

    private void fitDialogToWindow() {
        Window mWindow = getWindow();
        WindowManager.LayoutParams mParams = mWindow.getAttributes();
        mParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mParams.gravity = Gravity.BOTTOM;
        mWindow.setAttributes(mParams);
        //force use all space in the screen.
        mWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private void initDialogContent() {
        findViewById(R.id.dismiss_zone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readerMenuCallback.onHideMenu();
            }
        });

        menuLayout = (ReaderLayerMenuLayout)findViewById(R.id.layout_reader_menu);

        findViewById(R.id.layout_back_area).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readerMenuCallback.onMenuItemClicked(createVirtualMenuItem("/Exit"));
            }
        });

        findViewById(R.id.button_front_light).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogBrightness(getContext()).show();
            }
        });

        findViewById(R.id.button_screen_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogScreenRefresh dlg = new DialogScreenRefresh();
                dlg.setListener(new DialogScreenRefresh.onScreenRefreshChangedListener() {
                    @Override
                    public void onRefreshIntervalChanged(int oldValue, int newValue) {
                        readerMenuCallback.onMenuItemValueChanged(createVirtualMenuItem("/SetRefreshInterval"), oldValue, newValue);
                    }
                });
                dlg.show(activity.getFragmentManager());
            }
        });

        findViewById(R.id.button_toc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readerMenuCallback.onHideMenu();
                readerMenuCallback.onMenuItemClicked(createVirtualMenuItem("/Directory/TOC"));
            }
        });

        findViewById(R.id.button_dict).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readerMenuCallback.onHideMenu();
                readerMenuCallback.onMenuItemClicked(createVirtualMenuItem("/StartDictApp"));
            }
        });

        findViewById(R.id.button_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readerMenuCallback.onHideMenu();
                readerMenuCallback.onMenuItemClicked(createVirtualMenuItem("/Search"));
//                activity.onSearchRequested();
            }
        });

        findViewById(R.id.button_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readerMenuCallback.onHideMenu();
            }
        });

        findViewById(R.id.text_view_progress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readerMenuCallback.onMenuItemClicked(createVirtualMenuItem("/GotoPage"));
            }
        });

    }

    private ReaderMenuItem createVirtualMenuItem(String uri) {
        return new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, URI.create(uri), null, null, -1);
    }

    public void show(ReaderLayerMenuState state) {
        updateReaderState(state);
        show();
    }

    private void updateReaderState(ReaderLayerMenuState state) {
        ((TextView)findViewById(R.id.text_view_title)).setText(state.getTitle());
        ((TextView)findViewById(R.id.text_view_progress)).setText(formatPageProgress(state));
    }

    private String formatPageProgress(ReaderMenuState state) {
        return String.valueOf(state.getPageIndex() + 1) + "/" + String.valueOf(state.getPageCount());
    }
}
