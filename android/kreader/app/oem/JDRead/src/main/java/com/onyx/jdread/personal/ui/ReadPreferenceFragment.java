package com.onyx.jdread.personal.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ReadPreferenceBinding;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.personal.action.SetReadPreferenceAction;
import com.onyx.jdread.personal.adapter.ReadPreferenceAdapter;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;
import com.onyx.jdread.shop.action.BookCategoryAction;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.view.DividerItemDecoration;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by li on 2018/1/3.
 */

public class ReadPreferenceFragment extends BaseFragment {
    private ReadPreferenceBinding binding;
    private ReadPreferenceAdapter readPreferenceAdapter;
    private final static int SUB_CATGORY = 1;
    private Map<Integer, List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo>> datas = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = (ReadPreferenceBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_read_preference, container, false);
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

    private void initView() {
        binding.readPreferenceRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        DividerItemDecoration decoration = new DividerItemDecoration(JDReadApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        binding.readPreferenceRecycler.addItemDecoration(decoration);
        readPreferenceAdapter = new ReadPreferenceAdapter();
        binding.readPreferenceRecycler.setAdapter(readPreferenceAdapter);
    }

    private void initData() {
        TitleBarModel titleModel = PersonalDataBundle.getInstance().getTitleModel();
        titleModel.title.set(JDReadApplication.getInstance().getResources().getString(R.string.read_preference));
        titleModel.backEvent.set(new BackToSettingFragmentEvent());
        binding.readPreferenceTitle.setTitleModel(titleModel);

        if (datas != null && datas.size() > 0) {
            datas.clear();
        }
        List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> categoryBean = ShopDataBundle.getInstance().getCategoryBean();
        if (categoryBean != null && categoryBean.size() > 0) {
            datas.put(categoryBean.get(0).cateLevel, categoryBean);
            if (readPreferenceAdapter != null) {
                readPreferenceAdapter.setData(categoryBean);
            }
        } else {
            final BookCategoryAction action = new BookCategoryAction(false);
            action.execute(ShopDataBundle.getInstance(), new RxCallback() {
                @Override
                public void onNext(Object o) {
                    List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> cateTwo = ShopDataBundle.getInstance().getCategoryBean();
                    datas.put(cateTwo.get(0).cateLevel, cateTwo);
                    if (readPreferenceAdapter != null) {
                        readPreferenceAdapter.setData(cateTwo);
                    }
                }
            });
        }
    }

    private void initListener() {
        if (readPreferenceAdapter != null) {
            readPreferenceAdapter.setOnItemClickListener(new PageRecyclerView.PageAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> data = readPreferenceAdapter.getData();
                    CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo catListBean = data.get(position);
                    List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> sub_category = catListBean.sub_category;
                    if (sub_category != null && sub_category.size() > 0) {
                        readPreferenceAdapter.setData(sub_category);
                    }
                }
            });
        }

        binding.readPreferenceSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (datas.size() == SUB_CATGORY) {
                    List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> selectedBean = readPreferenceAdapter.getSelectedBean();
                    if (selectedBean!= null && selectedBean.size() > 0) {
                        SetReadPreferenceAction action = new SetReadPreferenceAction(selectedBean);
                        action.execute(PersonalDataBundle.getInstance(), null);
                    }
                    readPreferenceAdapter.setData(datas.get(SUB_CATGORY));
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToSettingFragmentEvent(BackToSettingFragmentEvent event) {
        viewEventCallBack.viewBack();
    }
}