package com.onyx.android.dr.activity;

import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.DictSpinnerAdapter;
import com.onyx.android.dr.bean.NewWordBean;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.database.QueryRecordEntity;
import com.onyx.android.dr.event.RefreshWebviewEvent;
import com.onyx.android.dr.event.WebViewLoadOverEvent;
import com.onyx.android.dr.interfaces.QueryRecordView;
import com.onyx.android.dr.presenter.QueryRecordPresenter;
import com.onyx.android.dr.util.DictPreference;
import com.onyx.android.dr.manager.OperatingDataManager;
import com.onyx.android.dr.util.Utils;
import com.onyx.android.dr.view.AutoPagedWebView;
import com.onyx.android.sdk.dict.data.DictionaryManager;
import com.onyx.android.sdk.dict.data.DictionaryQueryResult;
import com.onyx.android.sdk.dict.request.QueryWordRequest;
import com.onyx.android.sdk.dict.request.common.DictBaseCallback;
import com.onyx.android.sdk.dict.request.common.DictBaseRequest;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.Bind;
import butterknife.OnClick;


/**
 * Created by zhouzhiming on 17-7-11.
 */
public class NewWordQueryDialogActivity extends BaseActivity implements QueryRecordView {
    @Bind(R.id.new_word_query_activity_view)
    AutoPagedWebView resultView;
    @Bind(R.id.new_word_query_activity_button_previous)
    ImageView prevPageButton;
    @Bind(R.id.new_word_query_activity_page_size_indicator)
    TextView pageIndicator;
    @Bind(R.id.new_word_query_activity_button_next)
    ImageView nextPageButton;
    @Bind(R.id.new_word_query_activity_spinner)
    Spinner resultSpinner;
    @Bind(R.id.new_word_query_activity_confirm)
    TextView incomeNewWordNote;
    @Bind(R.id.new_word_query_activity_baidubaike)
    TextView baiduBaike;
    @Bind(R.id.title_bar_container)
    LinearLayout titleBarContainer;
    private List<String> searchResultList = new ArrayList<String>();
    private DictionaryManager dictionaryManager;
    private QueryWordRequest queryWordRequest;
    public volatile Map<String, DictionaryQueryResult> queryResult;
    private int customFontSize = 10;
    private String dictionaryLookup = "";
    private String readingMatter = "";
    private long millisecond = 2000;
    private DictSpinnerAdapter dictSpinnerAdapter;
    private QueryRecordPresenter queryRecordPresenter;
    private String editQuery = "";
    private List<String> pathList;
    private boolean tag;
    private NewWordBean intentBean;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_new_word_query;
    }

    @Override
    protected void initConfig() {
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        queryResult = new ConcurrentHashMap<String, DictionaryQueryResult>();
        customFontSize = DRApplication.getInstance().getCustomFontSize();
        queryRecordPresenter = new QueryRecordPresenter(this);
        dictSpinnerAdapter = new DictSpinnerAdapter(this);
        titleBarContainer.setVisibility(View.GONE);
        getIntentData();
        setHeightAndWidth();
        initEvent();
    }

    @Override
    public void onAttachedToWindow() {
        tag = intentBean.isTag();
        View view = getWindow().getDecorView();
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) view
                .getLayoutParams();
        if (tag) {
            layoutParams.gravity = Gravity.BOTTOM;
            layoutParams.y = getResources().getDimensionPixelSize(
                    R.dimen.playqueue_dialog_marginbottom);
        } else {
            layoutParams.gravity = Gravity.TOP;
        }
        getWindowManager().updateViewLayout(view, layoutParams);
    }

    private void setHeightAndWidth() {
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        Float heightProportion = Float.valueOf(getString(R.string.new_word_query_activity_dialog_height));
        Float widthProportion = Float.valueOf(getString(R.string.new_word_query_activity_dialog_width));
        attributes.height = (int) (Utils.getScreenHeight(DRApplication.getInstance()) * heightProportion);
        attributes.width = (int) (Utils.getScreenWidth(DRApplication.getInstance()) * widthProportion);
        getWindow().setAttributes(attributes);
    }

    private void getIntentData() {
        intentBean = (NewWordBean) getIntent().getSerializableExtra(Constants.NEW_WORD_BEAN);
        editQuery = intentBean.getNewWord();
        loadDictionary();
    }

    private void loadDictionary() {
        DictPreference.init(this);
        pathList = new ArrayList<>();
        int dictType = DictPreference.getIntValue(this, Constants.DICTTYPE, Constants.ENGLISH_TYPE);
        dictionaryManager = DRApplication.getInstance().getDictionaryManager();
        dictionaryManager.newProviderMap.clear();
        pathList = Utils.getPathList(dictType);
    }

    @Override
    public void setQueryRecordData(List<QueryRecordEntity> list) {
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

    private void initEvent() {
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
        resultView.setPageChangedListener(new AutoPagedWebView.PageChangedListener() {
            @Override
            public void onPageChanged(int totalPage, int curPage) {
                NewWordQueryDialogActivity.this.onPageChanged(totalPage, curPage);
            }
        });
        resultView.setUpdateDictionaryListCallback(new AutoPagedWebView.UpdateDictionaryListCallback() {
            @Override
            public void update(String dictName) {
                saveDictionary(dictName);
            }
        });
        resultView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        resultView.setLongClickable(false);
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
        incomeNewWordNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewWordBean bean = new NewWordBean();
                bean.setNewWord(editQuery);
                bean.setDictionaryLookup(intentBean.getDictionaryLookup());
                bean.setReadingMatter(intentBean.getReadingMatter());
                bean.setPageNumber(intentBean.getPageNumber());
                bean.setNewWordType(intentBean.getNewWordType());
                OperatingDataManager.getInstance().insertNewWord(bean);
            }
        });
    }

    private void saveDictionary(String dictName) {
        if (!searchResultList.contains(dictName)) {
            searchResultList.add(dictName);
        }
    }

    public void setWebviewDefaultFontSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int density = metrics.densityDpi;
        resultView.setWebviewDefaultFontSize(density);
    }

    public void testWordDictQuery() {
        if (!StringUtils.isNullOrEmpty(editQuery)) {
            dictionaryManager = DRApplication.getInstance().getDictionaryManager();
            queryWordRequest = new QueryWordRequest(editQuery);
            boolean bRet = dictionaryManager.sendRequest(DRApplication.getInstance(), queryWordRequest, pathList, new DictBaseCallback() {
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

    @Subscribe
    public void onWebViewLoadOverEvent(WebViewLoadOverEvent event) {
        testWordDictQuery();
    }

    @OnClick({R.id.new_word_query_activity_baidubaike})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.new_word_query_activity_baidubaike:
                Utils.openBaiduBaiKe(this, editQuery);
                break;
        }
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