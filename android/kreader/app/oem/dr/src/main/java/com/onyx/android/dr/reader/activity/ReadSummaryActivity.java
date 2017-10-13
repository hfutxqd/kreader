package com.onyx.android.dr.reader.activity;

import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.activity.BaseActivity;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.reader.adapter.GoodSentenceReviewListAdapter;
import com.onyx.android.dr.reader.adapter.NewWordsReviewListAdapter;
import com.onyx.android.dr.reader.base.ReadSummaryView;
import com.onyx.android.dr.reader.data.ReadSummaryGoodSentenceReviewBean;
import com.onyx.android.dr.reader.data.ReadSummaryNewWordReviewBean;
import com.onyx.android.dr.reader.event.RedrawPageEvent;
import com.onyx.android.dr.reader.presenter.ReadSummaryPresenter;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

import static com.onyx.android.dr.R.id.new_words_review_recycler;

/**
 * Created by hehai on 17-8-18.
 */

public class ReadSummaryActivity extends BaseActivity implements ReadSummaryView {

    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.title_bar_title)
    TextView titleBarTitle;
    @Bind(R.id.menu_back)
    LinearLayout menuBack;
    @Bind(R.id.title_bar_right_menu)
    TextView save;
    @Bind(R.id.read_summary_title)
    TextView readSummaryTitle;
    @Bind(R.id.edit_read_summary)
    EditText editReadSummary;
    @Bind(R.id.new_words_review_title)
    TextView newWordsReviewTitle;
    @Bind(new_words_review_recycler)
    PageRecyclerView newWordsReviewRecycler;
    @Bind(R.id.good_sentence_title)
    TextView goodSentenceTitle;
    @Bind(R.id.new_words_review_title_hint)
    TextView newWordsReviewTitleHint;
    @Bind(R.id.good_sentence_title_hint)
    TextView goodSentenceTitleHint;
    @Bind(R.id.good_sentence_recycler)
    PageRecyclerView goodSentenceRecycler;
    private NewWordsReviewListAdapter newWordsReviewListAdapter;
    private GoodSentenceReviewListAdapter goodSentenceReviewListAdapter;
    private ReadSummaryPresenter readSummaryPresenter;
    private String bookName;
    private String pageNumber;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_read_summary;
    }

    @Override
    protected void initConfig() {
        Intent intent = getIntent();
        String[] metadataArray = intent.getStringArrayExtra(Constants.METADATA_ARRAY);
        bookName = metadataArray[0];
        if (StringUtils.isNotBlank(bookName) && bookName.contains(".")) {
            bookName = bookName.substring(0, bookName.indexOf("."));
        }
        pageNumber = metadataArray[1];
        newWordsReviewListAdapter = new NewWordsReviewListAdapter();
        newWordsReviewRecycler.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        newWordsReviewRecycler.addItemDecoration(dividerItemDecoration);
        newWordsReviewRecycler.setAdapter(newWordsReviewListAdapter);
        goodSentenceReviewListAdapter = new GoodSentenceReviewListAdapter();
        goodSentenceRecycler.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        goodSentenceRecycler.addItemDecoration(dividerItemDecoration);
        goodSentenceRecycler.setAdapter(goodSentenceReviewListAdapter);

        save.setVisibility(View.VISIBLE);
        save.setText(getString(R.string.save));
    }

    @Override
    protected void initView() {
        titleBarTitle.setText(getString(R.string.read_summary));
        image.setImageResource(R.drawable.ic_reader_top_main);
    }

    @Override
    protected void initData() {
        readSummaryPresenter = new ReadSummaryPresenter(this);
        readSummaryPresenter.getReadSummary(bookName, pageNumber);
    }

    @OnClick({R.id.menu_back, R.id.title_bar_right_menu})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_back:
                finish();
                break;
            case R.id.title_bar_right_menu:
                saveReadSummary();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new RedrawPageEvent());
    }

    @Override
    public void setNewWordList(List<ReadSummaryNewWordReviewBean> newWordList) {
        if (newWordList == null || newWordList.size() <= 0) {
            newWordsReviewTitleHint.setVisibility(View.VISIBLE);
            newWordsReviewRecycler.setVisibility(View.GONE);
            newWordsReviewTitleHint.setText(getString(R.string.new_words_review_title_hint));
        } else {
            newWordsReviewTitleHint.setVisibility(View.GONE);
            newWordsReviewRecycler.setVisibility(View.VISIBLE);
            newWordsReviewListAdapter.setList(newWordList);
        }
    }

    @Override
    public void setGoodSentenceList(List<ReadSummaryGoodSentenceReviewBean> goodSentenceList) {
        if (goodSentenceList == null || goodSentenceList.size() <= 0) {
            goodSentenceTitleHint.setVisibility(View.VISIBLE);
            goodSentenceRecycler.setVisibility(View.GONE);
            goodSentenceTitleHint.setText(getString(R.string.good_sentence_title_hint));
        } else {
            goodSentenceTitleHint.setVisibility(View.GONE);
            goodSentenceRecycler.setVisibility(View.VISIBLE);
            goodSentenceReviewListAdapter.setList(goodSentenceList);
        }
        goodSentenceReviewListAdapter.setList(goodSentenceList);
    }

    @Override
    public void setSummary(String summary) {
        editReadSummary.setText(summary);
    }

    public void saveReadSummary() {
        String summary = editReadSummary.getText().toString();
        String newWordListJson = newWordsReviewListAdapter.getNewWordListJson();
        String goodSentenceJson = goodSentenceReviewListAdapter.getGoodSentenceJson();
        readSummaryPresenter.saveReadSummary(bookName, pageNumber, summary, newWordListJson, goodSentenceJson);
    }
}
