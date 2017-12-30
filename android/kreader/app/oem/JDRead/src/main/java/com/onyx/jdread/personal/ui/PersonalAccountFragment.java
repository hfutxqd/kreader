package com.onyx.jdread.personal.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.OnyxPageDividerItemDecoration;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.common.BaseFragment;
import com.onyx.jdread.databinding.PersonalAccountBinding;
import com.onyx.jdread.personal.adapter.PersonalAccountAdapter;
import com.onyx.jdread.personal.dialog.TopUpDialog;
import com.onyx.jdread.personal.event.ConsumptionRecordEvent;
import com.onyx.jdread.personal.event.FiftyYuanEvent;
import com.onyx.jdread.personal.event.FiveHundredYuanEvent;
import com.onyx.jdread.personal.event.HundredYuanEvent;
import com.onyx.jdread.personal.event.OneYuanEvent;
import com.onyx.jdread.personal.event.PaidRecordEvent;
import com.onyx.jdread.personal.event.PointsForEvent;
import com.onyx.jdread.personal.event.ReadVipEvent;
import com.onyx.jdread.personal.event.TenYuanEvent;
import com.onyx.jdread.personal.event.TopUpEvent;
import com.onyx.jdread.personal.event.TwoHundredYuanEvent;
import com.onyx.jdread.personal.model.PersonalAccountModel;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.setting.model.SettingTitleModel;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by li on 2017/12/29.
 */

public class PersonalAccountFragment extends BaseFragment {
    private PersonalAccountBinding binding;
    private PersonalAccountAdapter personalAccountAdapter;
    private OnyxPageDividerItemDecoration decoration;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = (PersonalAccountBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_personal_account, container, false);
        initView();
        initData();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        PersonalDataBundle.getInstance().getEventBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        PersonalDataBundle.getInstance().getEventBus().unregister(this);
    }

    private void initData() {
        SettingTitleModel titleModel = PersonalDataBundle.getInstance().getTitleModel();
        titleModel.setToggle(false);
        titleModel.setViewHistory(false);
        titleModel.setTitle(JDReadApplication.getInstance().getResources().getString(R.string.personal_account));
        binding.accountTitleBar.setTitleModel(titleModel);

        PersonalAccountModel personalAccountModel = PersonalDataBundle.getInstance().getPersonalAccountModel();
        if (personalAccountAdapter != null) {
            decoration.setBlankCount(personalAccountModel.getAccountTitles().length);
            personalAccountAdapter.setData(personalAccountModel.getAccountTitles(), personalAccountModel.getAccountEvents());
        }
    }

    private void initView() {
        binding.accountRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        decoration = new OnyxPageDividerItemDecoration(JDReadApplication.getInstance(), OnyxPageDividerItemDecoration.VERTICAL);
        binding.accountRecycler.addItemDecoration(decoration);
        personalAccountAdapter = new PersonalAccountAdapter(PersonalDataBundle.getInstance().getEventBus());
        binding.accountRecycler.setAdapter(personalAccountAdapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTopUpEvent(TopUpEvent event) {
        TopUpDialog dialog = new TopUpDialog();
        dialog.show(getActivity().getFragmentManager(), "");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConsumptionRecordEvent(ConsumptionRecordEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPaidRecordEvent(PaidRecordEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReadVipEvent(ReadVipEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPointsForEvent(PointsForEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOneYuanEvent(OneYuanEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTenYuanEvent(TenYuanEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFiftyYuanEvent(FiftyYuanEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHundredYuanEvent(HundredYuanEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTwoHundredYuanEvent(TwoHundredYuanEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFiveHundredYuanEvent(FiveHundredYuanEvent event) {

    }
}
