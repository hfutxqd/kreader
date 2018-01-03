package com.onyx.jdread.personal.ui;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.OnyxPageDividerItemDecoration;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.PointsForBinding;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.personal.adapter.PointsForAdapter;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.model.PointsForData;
import com.onyx.jdread.personal.model.PointsForModel;
import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by li on 2018/1/2.
 */

public class PointsForFragment extends BaseFragment {
    private PointsForBinding binding;
    private PointsForAdapter pointsForAdapter;
    private PointsForModel pointsForModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = (PointsForBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_points_for, container, false);
        initView();
        initData();
        initListener();
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
        TitleBarModel titleModel = PersonalDataBundle.getInstance().getTitleModel();
        titleModel.title.set(JDReadApplication.getInstance().getResources().getString(R.string.points_for));
        titleModel.backEvent.set(new BackToSettingFragmentEvent());
        binding.pointsForTitleBar.setTitleModel(titleModel);

        pointsForModel = PersonalDataBundle.getInstance().getPointsForModel();
        if (pointsForAdapter != null) {
            pointsForAdapter.setData(pointsForModel.getList());
        }
    }

    private void initView() {
        binding.pointsForRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        OnyxPageDividerItemDecoration decoration = new OnyxPageDividerItemDecoration(JDReadApplication.getInstance(), OnyxPageDividerItemDecoration.VERTICAL);
        binding.pointsForRecycler.addItemDecoration(decoration);
        pointsForAdapter = new PointsForAdapter();
        binding.pointsForRecycler.setAdapter(pointsForAdapter);
    }

    private void initListener() {
        if (pointsForAdapter != null) {
            pointsForAdapter.setOnItemClickListener(new PageRecyclerView.PageAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    PointsForData pointsForData = pointsForModel.getList().get(position);
                    // TODO: 2018/1/2
                    Log.d("+++++", "onItemClick: "+pointsForData.getDays());
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToSettingFragmentEvent(BackToSettingFragmentEvent event) {
        viewEventCallBack.viewBack();
    }
}
