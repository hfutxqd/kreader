package com.onyx.jdread.personal.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ConsumptionRecordBinding;
import com.onyx.jdread.library.view.DashLineItemDivider;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.personal.action.ReadBeanRecordAction;
import com.onyx.jdread.personal.adapter.ConsumptionRecordAdapter;
import com.onyx.jdread.personal.cloud.entity.jdbean.ConsumeRecordBean;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by li on 2018/1/2.
 */

public class TopUpRecordFragment extends BaseFragment {
    private ConsumptionRecordBinding binding;
    private ConsumptionRecordAdapter adapter;
    private GPaginator paginator;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = (ConsumptionRecordBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_consumption_record, container, false);
        initView();
        initData();
        initListener();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.ensureRegister(PersonalDataBundle.getInstance().getEventBus(), this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.ensureUnregister(PersonalDataBundle.getInstance().getEventBus(), this);
    }

    private void initData() {
        TitleBarModel titleModel = PersonalDataBundle.getInstance().getTitleModel();
        titleModel.title.set(JDReadApplication.getInstance().getResources().getString(R.string.paid_record));
        titleModel.backEvent.set(new BackToSettingFragmentEvent());
        binding.consumptionRecordTitle.setTitleModel(titleModel);

        final ReadBeanRecordAction action = new ReadBeanRecordAction();
        action.execute(PersonalDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                List<ConsumeRecordBean.DataBean> data = action.getData();
                if (data != null) {
                    adapter.setData(data);
                    paginator.resize(adapter.getRowCount(), adapter.getColumnCount(), data.size());
                    setPageSize();
                }
            }
        });
    }

    private void initView() {
        binding.consumptionRecordRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        DashLineItemDivider decoration = new DashLineItemDivider();
        binding.consumptionRecordRecycler.addItemDecoration(decoration);
        adapter = new ConsumptionRecordAdapter();
        binding.consumptionRecordRecycler.setAdapter(adapter);
        paginator = binding.consumptionRecordRecycler.getPaginator();
    }

    private void initListener() {
        binding.consumptionRecordRecycler.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                setPageSize();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToSettingFragmentEvent(BackToSettingFragmentEvent event) {
        viewEventCallBack.viewBack();
    }

    private void setPageSize() {
        String pageText = paginator.getProgressText();
        binding.setPageText(pageText);
    }
}