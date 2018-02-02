package com.onyx.jdread.setting.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.jdread.R;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.databinding.FragmentManualBinding;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.setting.event.BackToHelpFragmentEvent;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by hehai on 17-12-28.
 */

public class ManualFragment extends BaseFragment {

    private FragmentManualBinding manualBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        manualBinding = FragmentManualBinding.inflate(inflater, container, false);
        loadData();
        return manualBinding.getRoot();
    }

    private void loadData() {
        TitleBarModel titleBarModel = new TitleBarModel(SettingBundle.getInstance().getEventBus());
        titleBarModel.title.set(getString(R.string.manual));
        titleBarModel.backEvent.set(new BackToHelpFragmentEvent());
        manualBinding.manualTitle.setTitleModel(titleBarModel);
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.ensureRegister(SettingBundle.getInstance().getEventBus(), this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.ensureUnregister(SettingBundle.getInstance().getEventBus(), this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToHelpFragmentEvent(BackToHelpFragmentEvent event) {
        viewEventCallBack.viewBack();
    }
}