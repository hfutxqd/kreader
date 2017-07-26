package com.onyx.android.dr.activity;

import android.support.v7.widget.DividerItemDecoration;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.DictSpinnerAdapter;
import com.onyx.android.dr.adapter.QueryRecordAdapter;
import com.onyx.android.dr.bean.NewWordBean;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.data.database.QueryRecordEntity;
import com.onyx.android.dr.dialog.SelectAlertDialog;
import com.onyx.android.dr.event.RefreshWebviewEvent;
import com.onyx.android.dr.interfaces.QueryRecordView;
import com.onyx.android.dr.presenter.QueryRecordPresenter;
import com.onyx.android.dr.util.Utils;
import com.onyx.android.dr.view.AutoPagedWebView;
import com.onyx.android.sdk.dict.data.DictionaryManager;
import com.onyx.android.sdk.dict.data.DictionaryQueryResult;
import com.onyx.android.sdk.dict.request.QueryWordRequest;
import com.onyx.android.sdk.dict.request.common.DictBaseCallback;
import com.onyx.android.sdk.dict.request.common.DictBaseRequest;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.StringUtils;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.Bind;

/**
 * Created by zhouzhiming on 17-7-11.
 */
public class QueryRecordActivity extends BaseActivity implements QueryRecordView {
    @Bind(R.id.query_record_activity_recyclerview)
    PageRecyclerView queryRecordRecyclerView;
    private DividerItemDecoration dividerItemDecoration;
    private QueryRecordPresenter queryRecordPresenter;
    private QueryRecordAdapter queryRecordAdapter;
    private AutoPagedWebView resultView;
    private TextView pageIndicator;
    private ImageView prevPageButton;
    private ImageView nextPageButton;
    private List<String> searchResultList = new ArrayList<String>();
    private DictionaryManager dictionaryManager;
    private QueryWordRequest queryWordRequest;
    public volatile Map<String, DictionaryQueryResult> queryResult;
    private int customFontSize = 10;
    private SelectAlertDialog selectTimeDialog;
    private List<QueryRecordEntity> queryRecordList;
    private static final String TAG = QueryRecordActivity.class.getSimpleName();
    private Spinner resultSpinner;
    private TextView addNewWordNote;
    private TextView exit;
    private String newWord = "";
    private String dictionaryLookup = "";
    private String readingMatter = "";
    private long millisecond = 2000;
    private DictSpinnerAdapter dictSpinnerAdapter;
    private TextView baiduBaike;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_query_record;
    }

    @Override
    protected void initConfig() {
    }

    @Override
    protected void initView() {
        initRecylcerView();
        loadDialog();
    }

    private void initRecylcerView() {
        dividerItemDecoration =
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        queryRecordAdapter = new QueryRecordAdapter();
        queryRecordRecyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        queryRecordRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected void initData() {
        queryResult = new ConcurrentHashMap<String, DictionaryQueryResult>();
        queryRecordList = new ArrayList<QueryRecordEntity>();
        customFontSize = DRApplication.getInstance().getCustomFontSize();
        queryRecordPresenter = new QueryRecordPresenter(getApplicationContext(), this);
        queryRecordPresenter.getAllQueryRecordData();
        dictSpinnerAdapter = new DictSpinnerAdapter(this);
        initEvent();
    }

    @Override
    public void setQueryRecordData(List<QueryRecordEntity> list) {
        queryRecordAdapter.setDatas(list);
        queryRecordRecyclerView.setAdapter(queryRecordAdapter);
        queryRecordList = list;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int fontSize = DRApplication.getInstance().getCustomFontSize();
        if (fontSize != customFontSize) {
            if (resultView != null) {
                customFontSize = fontSize;
                resultView.setTextZoom(fontSize);
            }
        }
    }

    private void loadDialog() {
        RelativeLayout view = (RelativeLayout) LayoutInflater.from(this).inflate(
                R.layout.dialog_word_query_result, null);
        selectTimeDialog = new SelectAlertDialog(this);
        // find id
        prevPageButton = (ImageView) view.findViewById(R.id.dialog_result_button_previous);
        pageIndicator = (TextView) view.findViewById(R.id.dialog_result_page_size_indicator);
        nextPageButton = (ImageView) view.findViewById(R.id.dialog_result_button_next);
        resultSpinner = (Spinner) view.findViewById(R.id.dialog_result_spinner);
        resultView = (AutoPagedWebView) view.findViewById(R.id.dialog_result_view);
        exit = (TextView) view.findViewById(R.id.select_dialog_close_cancel);
        addNewWordNote = (TextView) view.findViewById(R.id.select_dialog_close_confirm);
        baiduBaike = (TextView) view.findViewById(R.id.select_dialog_baidubaike);
        WindowManager.LayoutParams attributes = selectTimeDialog.getWindow().getAttributes();
        Float heightProportion = Float.valueOf(getString(R.string.query_record_activity_dialog_height));
        Float widthProportion = Float.valueOf(getString(R.string.query_record_activity_dialog_width));
        attributes.height = (int) (Utils.getScreenHeight(DRApplication.getInstance()) * heightProportion);
        attributes.width = (int) (Utils.getScreenWidth(DRApplication.getInstance()) * widthProportion);
        selectTimeDialog.getWindow().setAttributes(attributes);
        selectTimeDialog.setView(view);
    }

    private void initEvent() {
        queryRecordAdapter.setOnItemClick(new QueryRecordAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                loadDialogData(position);
            }
        });

        resultSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (searchResultList.size() > 0) {
                    String dictName = (String) resultSpinner.getItemAtPosition(position);
                    dictionaryLookup = dictName;
                    if (!StringUtils.isNullOrEmpty(dictName)) {
                        showResultToWebview(dictName);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadDialogData(int position) {
        QueryRecordEntity queryRecordEntity = queryRecordList.get(position);
        newWord = queryRecordEntity.word;
        testWordDictQuery(newWord);
        resultView.setPageChangedListener(new AutoPagedWebView.PageChangedListener() {
            @Override
            public void onPageChanged(int totalPage, int curPage) {
                QueryRecordActivity.this.onPageChanged(totalPage, curPage);
            }
        });
        resultView.setUpdateDictionaryListCallback(new AutoPagedWebView.UpdateDictionaryListCallback() {
            @Override
            public void update(String dictName) {
                saveDictionary(dictName);
            }
        });
        resultView.setTextZoom(customFontSize);
        setWebviewDefaultFontSize();
        prevPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultView.prevPage();
            }
        });
        nextPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultView.nextPage();
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectTimeDialog.isShowing()) {
                    selectTimeDialog.cancel();
                }
            }
        });
        addNewWordNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewWordBean bean = new NewWordBean();
                bean.setNewWord(newWord);
                bean.setDictionaryLookup(dictionaryLookup);
                bean.setReadingMatter(readingMatter);
                queryRecordPresenter.insertNewWord(bean);
            }
        });
        baiduBaike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.openBaiduBaiKe(QueryRecordActivity.this, newWord);
            }
        });
        selectTimeDialog.show();
    }

    private void saveDictionary(String dictName) {
        if (!searchResultList.contains(dictName)) {
            searchResultList.add(dictName);
            dictSpinnerAdapter.notifyDataSetChanged();
        }
    }

    public void setWebviewDefaultFontSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int density = metrics.densityDpi;
        resultView.setWebviewDefaultFontSize(density);
    }

    public void testWordDictQuery(String editQuery) {
        if (!StringUtils.isNullOrEmpty(editQuery)) {
            dictionaryManager = DRApplication.getDictionaryManager();
            queryWordRequest = new QueryWordRequest(editQuery);
            reset();
            boolean bRet = dictionaryManager.sendRequest(DRApplication.getInstance(), queryWordRequest, new DictBaseCallback() {
                @Override
                public void done(DictBaseRequest request, Exception e) {
                    resultView.loadResultAsHtml(queryWordRequest.queryResult);
                    addSearchResult(queryWordRequest.queryResult);

                    dictSpinnerAdapter.setDatas(searchResultList);
                    resultSpinner.setAdapter(dictSpinnerAdapter);
                }
            });
            if (!bRet) {
                CommonNotices.showMessage(this, getString(R.string.headword_search_empty));
            }
        }
    }

    private void reset() {
        resultView.setScroll(0, 0);
        resultView.stopPlayer(0);
        resultView.clearPreviousResult();
        searchResultList.clear();
        queryResult.clear();
    }

    private void addSearchResult(Map<String, DictionaryQueryResult> result) {
        if (result.size() > 0) {
            for (Map.Entry<String, DictionaryQueryResult> entry : result.entrySet()) {
                if (entry.getValue().dictionary.dictVoiceInfo != null) {
                    continue;
                }
                if (queryResult.get(entry.getValue().dictionary.name) != null) {
                    continue;
                }
                queryResult.put(entry.getValue().dictionary.name, entry.getValue());
            }
            Log.i("###result.size()", result.size() + "");
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_PAGE_DOWN:
                resultView.nextPage();
                break;
            case KeyEvent.KEYCODE_PAGE_UP:
                resultView.prevPage();
                break;
            case KeyEvent.KEYCODE_BACK:
                resultView.stopPlayer(0);
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void onPageChanged(int totalPage, int curPage) {
        if (totalPage > 1) {
            if (curPage > 1) {
                prevPageButton.setVisibility(View.VISIBLE);
            } else {
                prevPageButton.setVisibility(View.INVISIBLE);
            }

            if (curPage < totalPage) {
                nextPageButton.setVisibility(View.VISIBLE);
            } else {
                nextPageButton.setVisibility(View.INVISIBLE);
            }
        } else {
            prevPageButton.setVisibility(View.INVISIBLE);
            nextPageButton.setVisibility(View.INVISIBLE);
        }
        if (totalPage <= 0) {
            pageIndicator.setVisibility(View.GONE);
        } else {
            pageIndicator.setVisibility(View.VISIBLE);
        }
        pageIndicator.setText(curPage + "/" + totalPage);
    }

    private void showResultToWebview(String dictName) {
        resultView.setScroll(0, 0);
        resultView.stopPlayer(0);
        //get dictionary result
        DictionaryQueryResult dictionaryQueryResult = queryResult.get(dictName);
        if (dictionaryQueryResult == null) {
            return;
        }
        String dictPath = dictionaryQueryResult.dictionary.dictPath;
        resultView.loadUrl("javascript:" + "showDict(\"" + dictName + "\",\"" + dictPath + "\")");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(RefreshWebviewEvent event) {
        resultView.refreshWebview(event);
    }

    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
