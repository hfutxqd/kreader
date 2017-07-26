package com.onyx.android.dr.activity;

import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.NewWordAdapter;
import com.onyx.android.dr.data.database.NewWordNoteBookEntity;
import com.onyx.android.dr.interfaces.NewWordView;
import com.onyx.android.dr.presenter.NewWordPresenter;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by zhouzhiming on 17-7-11.
 */
public class NewWordNotebookActivity extends BaseActivity implements NewWordView {
    @Bind(R.id.new_word_activity_recyclerview)
    PageRecyclerView goodSentenceRecyclerView;
    @Bind(R.id.new_word_activity_delete)
    TextView delete;
    @Bind(R.id.new_word_activity_export)
    TextView export;
    private DividerItemDecoration dividerItemDecoration;
    private NewWordAdapter newWordAdapter;
    private NewWordPresenter newWordPresenter;
    private List<NewWordNoteBookEntity> newWordList;
    private ArrayList<Boolean> listCheck;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_new_word_notebook;
    }

    @Override
    protected void initConfig() {
    }

    @Override
    protected void initView() {
        initRecylcerView();
    }

    private void initRecylcerView() {
        dividerItemDecoration =
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        newWordAdapter = new NewWordAdapter();
        goodSentenceRecyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        goodSentenceRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected void initData() {
        newWordPresenter = new NewWordPresenter(getApplicationContext(), this);
        newWordPresenter.getAllNewWordData();
        newWordList = new ArrayList<NewWordNoteBookEntity>();
        listCheck = new ArrayList<>();
        initEvent();
    }

    @Override
    public void setNewWordData(List<NewWordNoteBookEntity> dataList, ArrayList<Boolean> checkList) {
        if (dataList == null || dataList.size() <= 0) {
            return;
        }
        newWordList = dataList;
        listCheck = checkList;
        newWordAdapter.setDataList(newWordList, listCheck);
        goodSentenceRecyclerView.setAdapter(newWordAdapter);
    }

    public void initEvent() {
        newWordAdapter.setOnItemListener(new NewWordAdapter.OnItemClickListener() {
            @Override
            public void setOnItemClick(int position, boolean isCheck) {
                listCheck.set(position, isCheck);
            }

            @Override
            public void setOnItemCheckedChanged(int position, boolean isCheck) {
                listCheck.set(position, isCheck);
            }
        });
    }

    @OnClick({R.id.new_word_activity_delete,
            R.id.image_view_back,
            R.id.new_word_activity_export})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_view_back:
                finish();
                break;
            case R.id.new_word_activity_delete:
                newWordPresenter.remoteAdapterDatas(listCheck, newWordAdapter);
                break;
            case R.id.new_word_activity_export:
                newWordPresenter.getHtmlTitle();
                break;
        }
    }

    @Override
    public void setHtmlTitleData(ArrayList<String> dataList) {
        newWordPresenter.exportDataToHtml(this, dataList, newWordList);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
