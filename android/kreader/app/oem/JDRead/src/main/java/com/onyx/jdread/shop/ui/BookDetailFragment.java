package com.onyx.jdread.shop.ui;

import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jingdong.app.reader.data.DrmTools;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.DialogBookInfoBinding;
import com.onyx.jdread.databinding.FragmentBookDetailBinding;
import com.onyx.jdread.databinding.LayoutBookBatchDownloadBinding;
import com.onyx.jdread.databinding.LayoutBookCopyrightBinding;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.CommonUtils;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.personal.common.LoginHelper;
import com.onyx.jdread.personal.dialog.TopUpDialog;
import com.onyx.jdread.personal.event.UserLoginResultEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.model.PersonalViewModel;
import com.onyx.jdread.personal.model.UserLoginViewModel;
import com.onyx.jdread.reader.common.DocumentInfo;
import com.onyx.jdread.reader.common.OpenBookHelper;
import com.onyx.jdread.reader.ui.view.PageTextView;
import com.onyx.jdread.setting.ui.WifiFragment;
import com.onyx.jdread.shop.action.AddOrDeleteCartAction;
import com.onyx.jdread.shop.action.BookDetailAction;
import com.onyx.jdread.shop.action.BookRecommendListAction;
import com.onyx.jdread.shop.action.BookshelfInsertAction;
import com.onyx.jdread.shop.action.DownloadAction;
import com.onyx.jdread.shop.action.GetChapterGroupInfoAction;
import com.onyx.jdread.shop.action.GetOrderInfoAction;
import com.onyx.jdread.shop.action.MetadataQueryAction;
import com.onyx.jdread.shop.action.SearchBookListAction;
import com.onyx.jdread.shop.adapter.BatchDownloadChaptersAdapter;
import com.onyx.jdread.shop.adapter.RecommendAdapter;
import com.onyx.jdread.shop.cloud.entity.jdbean.BatchDownloadResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookExtraInfoBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelBooksResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.GetOrderInfoResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.ResultBookBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.UpdateBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.PageTagConstants;
import com.onyx.jdread.shop.event.BookDetailReadNowEvent;
import com.onyx.jdread.shop.event.BookDetailViewInfoEvent;
import com.onyx.jdread.shop.event.BookSearchKeyWordEvent;
import com.onyx.jdread.shop.event.BookSearchPathEvent;
import com.onyx.jdread.shop.event.BuyBookSuccessEvent;
import com.onyx.jdread.shop.event.CopyrightCancelEvent;
import com.onyx.jdread.shop.event.CopyrightEvent;
import com.onyx.jdread.shop.event.DownloadFinishEvent;
import com.onyx.jdread.shop.event.DownloadStartEvent;
import com.onyx.jdread.shop.event.DownloadWholeBookEvent;
import com.onyx.jdread.shop.event.DownloadingEvent;
import com.onyx.jdread.shop.event.GoShopingCartEvent;
import com.onyx.jdread.shop.event.HideAllDialogEvent;
import com.onyx.jdread.shop.event.LoadingDialogEvent;
import com.onyx.jdread.shop.event.MenuWifiSettingEvent;
import com.onyx.jdread.shop.event.RecommendItemClickEvent;
import com.onyx.jdread.shop.event.RecommendNextPageEvent;
import com.onyx.jdread.shop.event.TopBackEvent;
import com.onyx.jdread.shop.event.ViewCommentEvent;
import com.onyx.jdread.shop.model.BookBatchDownloadViewModel;
import com.onyx.jdread.shop.model.BookDetailViewModel;
import com.onyx.jdread.shop.model.DialogBookInfoViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.utils.BookDownloadUtils;
import com.onyx.jdread.shop.utils.DownLoadHelper;
import com.onyx.jdread.shop.utils.ViewHelper;
import com.onyx.jdread.shop.view.BookInfoDialog;
import com.onyx.jdread.shop.view.DividerItemDecoration;
import com.onyx.jdread.shop.view.SubjectBookItemSpaceItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

/**
 * Created by jackdeng on 2017/12/16.
 */

public class BookDetailFragment extends BaseFragment {

    private FragmentBookDetailBinding bookDetailBinding;
    private int bookRecommendSpace = ResManager.getDimens(R.dimen.book_detail_recommend_recycle_view_space);
    private SubjectBookItemSpaceItemDecoration itemDecoration;
    private long ebookId;
    private PageRecyclerView recyclerViewRecommend;
    private BookInfoDialog copyRightDialog;
    private String localPath;
    private BookDetailResultBean.DetailBean bookDetailBean;
    private int downloadTaskState;
    private TextView buyBookButton;
    private TextView nowReadButton;
    private int percentage;
    private boolean isWholeBookDownLoad;
    private BookInfoDialog infoDialog;
    private boolean hasAddToCart = false;
    private BookInfoDialog batchDownloadDialog;
    private BookBatchDownloadViewModel batchDownloadViewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bookDetailBinding = FragmentBookDetailBinding.inflate(inflater, container, false);
        initView();
        initLibrary();
        initData();
        return bookDetailBinding.getRoot();
    }

    private void initLibrary() {
        if (!getEventBus().isRegistered(this)) {
            getEventBus().register(this);
        }
    }

    private void initData() {
        cleanData();
        ebookId = JDPreferenceManager.getLongValue(Constants.SP_KEY_BOOK_ID, 0);
        getBookDetail();
    }

    private void getBookDetail() {
        queryMetadata();
        getBookDetailData(false);
        getRecommendData();
    }

    private void queryMetadata() {
        MetadataQueryAction metadataQueryAction = new MetadataQueryAction(String.valueOf(ebookId));
        metadataQueryAction.execute(getShopDataBundle(), new RxCallback<MetadataQueryAction>() {
            @Override
            public void onNext(MetadataQueryAction queryAction) {
                Metadata metadata = queryAction.getMetadataResult();
                if (metadata != null) {
                    String extraInfoStr = metadata.getDownloadInfo();
                    BookExtraInfoBean extraInfoBean = JSONObjectParseUtils.toBean(extraInfoStr, BookExtraInfoBean.class);
                    localPath = metadata.getNativeAbsolutePath();
                    if (extraInfoBean.localPath != null) {
                        setQueryResult(extraInfoBean);
                    }
                }
            }
        });
    }

    private void cleanData() {
        isWholeBookDownLoad = false;
        hasAddToCart = false;
        ebookId = 0;
        downloadTaskState = 0;
        localPath = "";
    }

    private void initView() {
        bookDetailBinding.setBookDetailViewModel(getBookDetailViewModel());
        //TODO  Do not do temporarily:  bookDetailBinding.bookDetailInfo.bookDetailYuedouPriceOld.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        bookDetailBinding.bookDetailInfo.bookDetailCategorySecondPath.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        bookDetailBinding.bookDetailInfo.bookDetailCategoryThirdPath.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        nowReadButton = bookDetailBinding.bookDetailInfo.bookDetailNowRead;
        buyBookButton = bookDetailBinding.bookDetailInfo.bookDetailBuyBook;
        initDividerItemDecoration();
        setRecommendRecycleView();
        getBookDetailViewModel().getTitleBarViewModel().leftText = ResManager.getString(R.string.title_bar_title_book_detail);
        getBookDetailViewModel().getTitleBarViewModel().pageTag = PageTagConstants.BOOK_DETAIL;
        getBookDetailViewModel().getTitleBarViewModel().showRightText = false;
    }

    private void setRecommendRecycleView() {
        RecommendAdapter adapter = new RecommendAdapter(getEventBus());
        recyclerViewRecommend = bookDetailBinding.bookDetailInfo.recyclerViewRecommend;
        recyclerViewRecommend.setPageTurningCycled(true);
        recyclerViewRecommend.setCanTouchPageTurning(false);
        recyclerViewRecommend.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerViewRecommend.addItemDecoration(itemDecoration);
        recyclerViewRecommend.setAdapter(adapter);
    }

    private void initDividerItemDecoration() {
        itemDecoration = new SubjectBookItemSpaceItemDecoration(false, bookRecommendSpace);
    }

    @Override
    public void onResume() {
        super.onResume();
        initLibrary();
    }

    @Override
    public void onStop() {
        super.onStop();
        getEventBus().unregister(this);
    }

    private BookDetailViewModel getBookDetailViewModel() {
        return getShopDataBundle().getBookDetailViewModel();
    }

    private EventBus getEventBus() {
        return getShopDataBundle().getEventBus();
    }

    private void getBookDetailData(final boolean shouldDownloadWholeBook) {
        BookDetailAction bookDetailAction = new BookDetailAction(ebookId, JDReadApplication.getInstance().getLogin());
        bookDetailAction.execute(getShopDataBundle(), new RxCallback<BookDetailAction>() {
            @Override
            public void onNext(BookDetailAction bookDetailAction) {
                BookDetailResultBean bookDetailResultBean = bookDetailAction.getBookDetailResultBean();
                if (bookDetailResultBean != null) {
                    if (bookDetailResultBean.result_code != Integer.valueOf(Constants.RESULT_CODE_SUCCESS)) {
                        return;
                    }

                    if (bookDetailBean != null && (bookDetailBean.ebook_id == bookDetailResultBean.data.ebook_id)) {
                        BookDetailResultBean.DetailBean newData = bookDetailResultBean.data;
                        newData.bookExtraInfoBean = bookDetailBean.bookExtraInfoBean;
                        newData.key = bookDetailBean.key;
                        newData.random = bookDetailBean.random;
                        bookDetailBean = newData;
                    } else {
                        bookDetailBean = bookDetailResultBean.data;
                    }

                    if (!ViewHelper.isCanNowRead(bookDetailBean)) {
                        hideNowReadButton();
                    }
                    if (!bookDetailBean.can_buy) {
                        if (!isWholeBookDownLoad && !fileIsExists(localPath) && !ViewHelper.isCanNowRead(bookDetailBean)) {
                            hideNowReadButton();
                        }
                        showShopCartView(false);
                    }
                    if (!StringUtils.isNullOrEmpty(bookDetailBean.author) && !ResManager.getString(R.string.content_empty).equals(bookDetailBean.author)) {
                        getAuthorBooksData(bookDetailBean.author);
                    } else {
                        bookDetailBean.setAuthor(ResManager.getString(R.string.error_content_author_unknown));
                    }

                    if (isaNetBook()) {
                        buyBookButton.setText(ResManager.getString(R.string.batch_download));
                        showShopCartView(false);
                    }

                    if (shouldDownloadWholeBook) {
                        smoothDownload();
                    }
                }
            }
        });
    }

    private boolean isaNetBook() {
        return bookDetailBean.book_type == Constants.BOOK_DETAIL_TYPE_NET;
    }

    private void showShopCartView(boolean show) {
        bookDetailBinding.bookDetailInfo.shopCartContainer.setVisibility(show ? View.VISIBLE : View.GONE);
        bookDetailBinding.bookDetailInfo.spaceTwo.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void hideNowReadButton() {
        nowReadButton.setVisibility(View.GONE);
        bookDetailBinding.bookDetailInfo.spaceOne.setVisibility(View.GONE);
    }

    private void getAuthorBooksData(String keyWord) {
        SearchBookListAction booksAction = new SearchBookListAction("", 1, CloudApiContext.CategoryLevel2BookList.SORT_KEY_DEFAULT_VALUES,
                CloudApiContext.CategoryLevel2BookList.SORT_TYPE_DEFAULT_VALUES, keyWord, CloudApiContext.SearchBook.FILTER_DEFAULT);
        booksAction.execute(getShopDataBundle(), new RxCallback<SearchBookListAction>() {
            @Override
            public void onNext(SearchBookListAction action) {
                BookModelBooksResultBean booksResultBean = action.getBooksResultBean();
                if (booksResultBean != null && booksResultBean.data != null) {
                    if (booksResultBean.data.items != null && booksResultBean.data.items.size() > 0) {
                        bookDetailBinding.bookDetailInfo.bookDetailAuthor.setEnabled(true);
                        bookDetailBinding.bookDetailInfo.bookDetailAuthor.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                    }
                }
            }
        });
    }

    private void getRecommendData() {
        BookRecommendListAction recommendListAction = new BookRecommendListAction(ebookId);
        recommendListAction.execute(getShopDataBundle(), new RxCallback() {
            @Override
            public void onNext(Object o) {

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = Integer.MAX_VALUE)
    public void onRecommendItemClickEvent(RecommendItemClickEvent event) {
        ResultBookBean bookBean = event.getBookBean();
        if (bookBean != null) {
            cleanData();
            initButton();
            setBookId(bookBean.ebook_id);
        }
    }

    private void initButton() {
        resetNowReadButton();
        resetBuyBookButton();
        showShopCartView(true);
    }

    private void resetBuyBookButton() {
        buyBookButton.setVisibility(View.VISIBLE);
        buyBookButton.setEnabled(true);
        buyBookButton.setText(ResManager.getString(R.string.book_detail_button_buy_whole_book));
        bookDetailBinding.bookDetailInfo.spaceOne.setVisibility(View.VISIBLE);
    }

    private void resetNowReadButton() {
        nowReadButton.setVisibility(View.VISIBLE);
        nowReadButton.setEnabled(true);
        nowReadButton.setText(ResManager.getString(R.string.book_detail_button_now_read));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecommendNextPageEvent(RecommendNextPageEvent event) {
        if (recyclerViewRecommend != null) {
            recyclerViewRecommend.nextPage();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookDetailTopBackEvent(TopBackEvent event) {
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().viewBack();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onViewCommentEvent(ViewCommentEvent event) {
        if (checkWfiDisConnected()) {
            return;
        }
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().gotoView(CommentFragment.class.getName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadWholeBookEvent(DownloadWholeBookEvent event) {
        if (checkWfiDisConnected()) {
            return;
        }
        BookDetailResultBean bookDetailResultBean = event.getBookDetailResultBean();
        if (bookDetailResultBean != null) {
            bookDetailBean = bookDetailResultBean.data;
            BookExtraInfoBean extraInfoBean = new BookExtraInfoBean();
            bookDetailBean.bookExtraInfoBean = extraInfoBean;
            bookDetailBean.bookExtraInfoBean.isWholeBookDownLoad = isWholeBookDownLoad;
            if (!JDReadApplication.getInstance().getLogin() && !LoginHelper.loginDialogIsShowing()) {
                LoginHelper.showUserLoginDialog(getUserLoginViewModel(), getActivity());
            } else {
                smoothDownload();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCopyrightEvent(CopyrightEvent event) {
        if (checkWfiDisConnected()) {
            return;
        }
        showCopyRightDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCopyrightCancelEvent(CopyrightCancelEvent event) {
        dismissCopyRightDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGoShopingCartEvent(GoShopingCartEvent event) {
        if (checkWfiDisConnected()) {
            return;
        }
        if (!JDReadApplication.getInstance().getLogin()) {
            LoginHelper.showUserLoginDialog(getUserLoginViewModel(), getActivity());
        } else {
            if (!hasAddToCart) {
                if (bookDetailBean != null) {
                    if (bookDetailBean.add_cart) {
                        hasAddToCart = true;
                        ToastUtil.showToast(ResManager.getString(R.string.book_detail_add_cart_tip_the_book_already_add_cart));
                        return;
                    }
                    if (!bookDetailBean.can_buy) {
                        ToastUtil.showToast(ResManager.getString(R.string.book_detail_add_cart_tip_the_book_not_can_buy));
                        return;
                    }
                    addToCart(bookDetailBean.ebook_id);
                }
            } else {
                gotoShopCartFragment();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookDetailReadNowEvent(BookDetailReadNowEvent event) {
        if (checkWfiDisConnected()) {
            return;
        }
        BookDetailResultBean bookDetailResultBean = event.getBookDetailResultBean();
        if (bookDetailResultBean != null) {
            bookDetailBean = bookDetailResultBean.data;
            BookExtraInfoBean extraInfoBean = new BookExtraInfoBean();
            bookDetailBean.bookExtraInfoBean = extraInfoBean;
            tryDownload(bookDetailBean);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadStartEvent(DownloadStartEvent event) {
        String tag = (String) event.tag;
        isWholeBookDownLoad = isCurrentDownWholeBook(tag);
        if (isWholeBookDownLoad) {
            changeBuyBookButtonState();
        }
    }

    private boolean isCurrentDownWholeBook(String tag) {
        return tag != null && tag.endsWith(Constants.WHOLE_BOOK_DOWNLOAD_TAG);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadFinishEvent(DownloadFinishEvent event) {
        BaseDownloadTask task = OnyxDownloadManager.getInstance().getTask(event.tag);
        if (task != null) {
            handlerDownloadResult(task);
            isWholeBookDownLoad = bookDetailBean.bookExtraInfoBean.isWholeBookDownLoad;
            if (isWholeBookDownLoad) {
                nowReadButton.setEnabled(true);
                hideNowReadButton();
                showShopCartView(false);
                upDataButtonDown(buyBookButton, true, bookDetailBean.bookExtraInfoBean);
            } else {
                buyBookButton.setEnabled(true);
                upDataButtonDown(nowReadButton, true, bookDetailBean.bookExtraInfoBean);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadingEvent(DownloadingEvent event) {
        BaseDownloadTask task = OnyxDownloadManager.getInstance().getTask(event.tag);
        if (task != null && event.progressInfoModel != null) {
            percentage = (int) (event.progressInfoModel.progress * 100);
            handlerDownloadResult(task);
            isWholeBookDownLoad = bookDetailBean.bookExtraInfoBean.isWholeBookDownLoad;
            if (isWholeBookDownLoad) {
                hideNowReadButton();
                showShopCartView(false);
                upDataButtonDown(buyBookButton, false, bookDetailBean.bookExtraInfoBean);
            } else {
                upDataButtonDown(nowReadButton, false, bookDetailBean.bookExtraInfoBean);
            }
        }
    }

    private void handlerDownloadResult(BaseDownloadTask task) {
        downloadTaskState = task.getStatus();
        localPath = task.getPath();
        if (bookDetailBean.bookExtraInfoBean == null) {
            bookDetailBean.bookExtraInfoBean = new BookExtraInfoBean();
        }
        bookDetailBean.bookExtraInfoBean.downLoadState = downloadTaskState;
        bookDetailBean.bookExtraInfoBean.downLoadTaskTag = task.getTag();
        bookDetailBean.bookExtraInfoBean.isWholeBookDownLoad = isCurrentDownWholeBook((String) task.getTag());
        if (DownLoadHelper.isDownloaded(downloadTaskState)) {
            percentage = DownLoadHelper.DOWNLOAD_PERCENT_FINISH;
        }
        bookDetailBean.bookExtraInfoBean.percentage = percentage;
        bookDetailBean.bookExtraInfoBean.localPath = localPath;
        bookDetailBean.bookExtraInfoBean.downloadUrl = task.getUrl();
        bookDetailBean.bookExtraInfoBean.progress = task.getSmallFileSoFarBytes();
        bookDetailBean.bookExtraInfoBean.totalSize = task.getSmallFileTotalBytes();
        if (DownLoadHelper.canInsertBookDetail(downloadTaskState)) {
            insertBookDetail(bookDetailBean, localPath);
        }
    }

    private void insertBookDetail(BookDetailResultBean.DetailBean bookDetailBean, String localPath) {
        BookshelfInsertAction insertAction = new BookshelfInsertAction(bookDetailBean, localPath);
        insertAction.execute(getShopDataBundle(), new RxCallback() {
            @Override
            public void onNext(Object o) {

            }
        });
    }

    private void upDataButtonDown(TextView button, boolean enabled, BookExtraInfoBean infoBean) {
        button.setEnabled(enabled);
        if (DownLoadHelper.isDownloading(infoBean.downLoadState)) {
            button.setText(percentage + "%" + ResManager.getString(R.string.book_detail_downloading));
        } else if (DownLoadHelper.isDownloaded(infoBean.downLoadState)) {
            button.setText(ResManager.getString(R.string.book_detail_button_now_read));
        } else if (DownLoadHelper.isError(infoBean.downLoadState)) {
            button.setText(ResManager.getString(R.string.book_detail_tip_try_again));
        }
    }

    private void smoothDownload() {

        if (isaNetBook()) {
            getChapterGroupInfo();
            return;
        }

        if (isWholeBookDownLoad &&  fileIsExists(localPath)) {
            openBook(localPath, bookDetailBean);
            return;
        }
        if (PersonalDataBundle.getInstance().isUserVip()) {
            if (bookDetailBean.can_read) {
                bookDetailBean.downLoadType = CloudApiContext.BookDownLoad.TYPE_SMOOTH_READ;
                downLoadWholeBook();
                return;
            }
        }

        if (!bookDetailBean.can_buy || bookDetailBean.isAlreadyBuy) {
            downLoadWholeBook();
            return;
        }

        if (bookDetailBean.can_buy) {
            showPayDialog(bookDetailBean.ebook_id);
            return;
        }
    }

    private void getChapterGroupInfo() {
        GetChapterGroupInfoAction action = new GetChapterGroupInfoAction(ebookId, "");
        action.setViewModel(getBookBatchDownloadViewModel());
        action.execute(getShopDataBundle(), new RxCallback<GetChapterGroupInfoAction>() {
            @Override
            public void onNext(GetChapterGroupInfoAction action) {
                BatchDownloadResultBean.DataBean data= getBookBatchDownloadViewModel().getDataBean();
                if (data != null && data.list != null) {
                    showBatchDownload();
                }
            }
        });
    }

    private BookBatchDownloadViewModel getBookBatchDownloadViewModel() {
        if (batchDownloadViewModel == null) {
            batchDownloadViewModel = new BookBatchDownloadViewModel(getEventBus());
        }
        return batchDownloadViewModel;
    }

    private void downLoadWholeBook() {
        nowReadButton.setEnabled(false);
        showShopCartView(false);
        BookDownloadUtils.download(bookDetailBean, getShopDataBundle());
    }

    private void openBook(String localPath, BookDetailResultBean.DetailBean detailBean) {
        DocumentInfo documentInfo = new DocumentInfo();
        DocumentInfo.SecurityInfo securityInfo = new DocumentInfo.SecurityInfo();
        securityInfo.setKey(detailBean.key);
        securityInfo.setRandom(detailBean.random);
        securityInfo.setUuId(DrmTools.getHardwareId(Build.SERIAL));
        documentInfo.setSecurityInfo(securityInfo);
        documentInfo.setBookPath(localPath);
        documentInfo.setBookName(detailBean.name);
        OpenBookHelper.openBook(super.getContext(), documentInfo);
    }

    private void showPayDialog(long ebookId) {
        getOrderInfo(new String[]{String.valueOf(ebookId)});
    }

    private void getOrderInfo(String[] bookIds) {
        if (bookIds != null) {
            GetOrderInfoAction action = new GetOrderInfoAction(bookIds);
            action.execute(getShopDataBundle(), new RxCallback<GetOrderInfoAction>() {
                @Override
                public void onNext(GetOrderInfoAction getOrderInfoAction) {
                    GetOrderInfoResultBean.DataBean dataBean = getOrderInfoAction.getDataBean();
                    if (dataBean != null) {
                        TopUpDialog dialog = new TopUpDialog();
                        Bundle bundle = new Bundle();
                        bundle.putInt(Constants.PAY_DIALOG_TYPE, Constants.PAY_DIALOG_TYPE_PAY_ORDER);
                        bundle.putSerializable(Constants.ORDER_INFO, dataBean);
                        dialog.setArguments(bundle);
                        dialog.show(getActivity().getFragmentManager(), "");
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    super.onError(throwable);
                }
            });
        }
    }

    private void addToCart(long ebookId) {
        final AddOrDeleteCartAction addOrDeleteCartAction = new AddOrDeleteCartAction(new String[]{String.valueOf(ebookId)}, Constants.CART_TYPE_ADD);
        addOrDeleteCartAction.execute(getShopDataBundle(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                UpdateBean data = addOrDeleteCartAction.getData();
                if (data != null) {
                    setAddOrDelFromCart(data);
                }
            }
        });
    }

    public void setAddOrDelFromCart(UpdateBean result) {
        ToastUtil.showToast(ResManager.getString(R.string.book_detail_success_add_cart));
        hasAddToCart = true;
    }

    private void gotoShopCartFragment() {
        getViewEventCallBack().gotoView(ShopCartFragment.class.getName());
    }

    private void tryDownload(BookDetailResultBean.DetailBean bookDetailBean) {
        if (bookDetailBean == null) {
            return;
        }
        if (fileIsExists(localPath)) {
            openBook(localPath, bookDetailBean);
            return;
        }

        if (StringUtils.isNullOrEmpty(bookDetailBean.try_url)) {
            ToastUtil.showToast(getContext(), ResManager.getString(R.string.empty_url));
            return;
        }
        if (DownLoadHelper.isDownloading(downloadTaskState)) {
            ToastUtil.showToast(JDReadApplication.getInstance(), ResManager.getString(R.string.book_detail_downloading));
            return;
        }
        nowReadButton.setEnabled(false);
        buyBookButton.setEnabled(false);
        nowReadButton.setText(ResManager.getString(R.string.book_detail_downloading));
        download(bookDetailBean);
        ToastUtil.showToast(JDReadApplication.getInstance(), bookDetailBean.name + ResManager.getString(R.string.book_detail_tip_book_add_to_bookself));
    }

    private void download(BookDetailResultBean.DetailBean bookDetailBean) {
        String tryDownLoadUrl = bookDetailBean.try_url;
        if (StringUtils.isNullOrEmpty(tryDownLoadUrl)) {
            ToastUtil.showToast(getContext(), ResManager.getString(R.string.empty_url));
            return;
        }
        String localPath = CommonUtils.getJDBooksPath() + File.separator + bookDetailBean.name + Constants.BOOK_FORMAT;
        DownloadAction downloadAction = new DownloadAction(getContext(), tryDownLoadUrl, localPath, bookDetailBean.ebook_id + "");
        downloadAction.execute(getShopDataBundle(), new RxCallback() {
            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
            }
        });
    }

    public void setQueryResult(BookExtraInfoBean extraInfoBean) {
        if (extraInfoBean != null) {
            localPath = extraInfoBean.localPath;
            downloadTaskState = extraInfoBean.downLoadState;
            isWholeBookDownLoad = extraInfoBean.isWholeBookDownLoad;
            if (bookDetailBean != null) {
                bookDetailBean.bookExtraInfoBean = extraInfoBean;
                bookDetailBean.bookExtraInfoBean.isWholeBookDownLoad = isWholeBookDownLoad;
            }
            if (isWholeBookDownLoad && fileIsExists(localPath)) {
                hideNowReadButton();
                buyBookButton.setText(ResManager.getString(R.string.book_detail_button_now_read));
            }
        }
    }

    private boolean fileIsExists(String localPath) {
        if (localPath != null) {
            return FileUtils.fileExist(localPath);
        } else {
            return false;
        }
    }

    public Context getContext() {
        return JDReadApplication.getInstance().getApplicationContext();
    }

    private void showBatchDownload() {
        if (ViewHelper.dialogIsShowing(batchDownloadDialog)) {
            return;
        }
        LayoutBookBatchDownloadBinding batchDownloadBinding = LayoutBookBatchDownloadBinding.inflate(LayoutInflater.from(getActivity()), null, false);
        batchDownloadBinding.setViewModel(getBookBatchDownloadViewModel());
        if (batchDownloadDialog == null) {
            batchDownloadDialog = new BookInfoDialog(JDReadApplication.getInstance());
            batchDownloadDialog.setView(batchDownloadBinding.getRoot());
            batchDownloadBinding.closeDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissBatchDownloadDialog();
                }
            });
            PageRecyclerView batchDownloadRecyclerView = batchDownloadBinding.batchDownloadRecyclerView;
            batchDownloadRecyclerView.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
            DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
            decoration.setSpace(ResManager.getInteger(R.integer.book_batch_download_recycle_view_item_space));
            batchDownloadRecyclerView.addItemDecoration(decoration);
            RecyclerView.Adapter adapter = new BatchDownloadChaptersAdapter(getEventBus());
            batchDownloadRecyclerView.setAdapter(adapter);
        }
        if (!ViewHelper.dialogIsShowing(batchDownloadDialog)) {
            batchDownloadDialog.show();
        }
    }

    private void showCopyRightDialog() {
        if (ViewHelper.dialogIsShowing(copyRightDialog)) {
            return;
        }
        LayoutBookCopyrightBinding copyrightBinding = LayoutBookCopyrightBinding.inflate(LayoutInflater.from(getActivity()), null, false);
        copyrightBinding.setBookDetailViewModel(getBookDetailViewModel());
        if (copyRightDialog == null) {
            copyRightDialog = new BookInfoDialog(JDReadApplication.getInstance());
            copyRightDialog.setView(copyrightBinding.getRoot());
        }
        if (!ViewHelper.dialogIsShowing(copyRightDialog)) {
            copyRightDialog.show();
        }
    }

    private void dismissCopyRightDialog() {
        if (copyRightDialog != null && copyRightDialog.isShowing()) {
            copyRightDialog.dismiss();
        }
    }

    private void dismissBatchDownloadDialog() {
        if (batchDownloadDialog != null) {
            batchDownloadDialog.dismiss();
        }
    }

    public void setBookId(long ebookId) {
        this.ebookId = ebookId;
        JDPreferenceManager.setLongValue(Constants.SP_KEY_BOOK_ID, ebookId);
        queryMetadata();
        getBookDetail();
    }

    public ShopDataBundle getShopDataBundle() {
        return ShopDataBundle.getInstance();
    }

    public PersonalDataBundle getPersonalDataBundle() {
        return PersonalDataBundle.getInstance();
    }

    public PersonalViewModel getPersonalViewModel() {
        return getPersonalDataBundle().getPersonalViewModel();
    }

    public UserLoginViewModel getUserLoginViewModel() {
        return getPersonalViewModel().getUserLoginViewModel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dismissCopyRightDialog();
        LoginHelper.dismissUserLoginDialog();
        copyRightDialog = null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadingDialogEvent(LoadingDialogEvent event) {
        if (isAdded()) {
            showLoadingDialog(ResManager.getString(event.getResId()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHideAllDialogEvent(HideAllDialogEvent event) {
        hideLoadingDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBuyBookSuccessEvent(BuyBookSuccessEvent event) {
        String msg = ResManager.getString(R.string.buy_book_success) + bookDetailBean.name + ResManager.getString(R.string.book_detail_tip_book_add_to_bookself);
        bookDetailBean.isAlreadyBuy = true;
        ToastUtil.showToast(JDReadApplication.getInstance(), msg);
        downLoadWholeBook();
    }

    private void changeBuyBookButtonState() {
        hideNowReadButton();
        buyBookButton.setEnabled(false);
        buyBookButton.setText(ResManager.getString(R.string.book_detail_downloading));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserLoginResultEvent(UserLoginResultEvent event) {
        if (ResManager.getString(R.string.login_success).equals(event.getMessage())) {
            getBookDetailData(true);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMenuWifiSettingEvent(MenuWifiSettingEvent event) {
        if (checkWfiDisConnected()) {
            return;
        }
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().gotoView(WifiFragment.class.getName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookSearchKeyWordEvent(BookSearchKeyWordEvent event) {
        if (checkWfiDisConnected()) {
            return;
        }
        if (getViewEventCallBack() != null) {
            JDPreferenceManager.setStringValue(Constants.SP_KEY_SEARCH_BOOK_CAT_ID, "");
            JDPreferenceManager.setStringValue(Constants.SP_KEY_KEYWORD, event.keyWord);
            getViewEventCallBack().gotoView(SearchBookListFragment.class.getName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookSearchPathEvent(BookSearchPathEvent event) {
        if (checkWfiDisConnected()) {
            return;
        }
        if (getViewEventCallBack() != null) {
            JDPreferenceManager.setStringValue(Constants.SP_KEY_KEYWORD, "");
            JDPreferenceManager.setStringValue(Constants.SP_KEY_SEARCH_BOOK_CAT_ID, event.catId);
            getViewEventCallBack().gotoView(SearchBookListFragment.class.getName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookDetailViewInfoEvent(BookDetailViewInfoEvent event) {
        showInfoDialog(event.info);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        hideLoadingDialog();
        dismissCopyRightDialog();
        dismissInfoDialog();
    }

    private void showInfoDialog(String content) {
        if (ViewHelper.dialogIsShowing(infoDialog)) {
            return;
        }
        if (StringUtils.isNullOrEmpty(content)) {
            return;
        }
        DialogBookInfoBinding infoBinding = DialogBookInfoBinding.inflate(LayoutInflater.from(getActivity()), null, false);
        final DialogBookInfoViewModel dialogBookInfoViewModel = getBookDetailViewModel().getDialogBookInfoViewModel();
        dialogBookInfoViewModel.content.set(content);
        dialogBookInfoViewModel.title.set(ResManager.getString(R.string.book_detail_text_view_content_introduce));
        infoBinding.setViewModel(dialogBookInfoViewModel);
        infoDialog = new BookInfoDialog(JDReadApplication.getInstance());
        infoDialog.setView(infoBinding.getRoot());
        PageTextView pagedWebView = infoBinding.bookInfoWebView;
        pagedWebView.setOnPagingListener(new PageTextView.OnPagingListener() {
            @Override
            public void onPageChange(int currentPage, int totalPage) {
                dialogBookInfoViewModel.currentPage.set(currentPage);
                dialogBookInfoViewModel.totalPage.set(totalPage);
            }
        });
        infoBinding.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissInfoDialog();
            }
        });
        if (!ViewHelper.dialogIsShowing(infoDialog)) {
            infoDialog.show();
        }
    }

    private void dismissInfoDialog() {
        if (infoDialog != null) {
            infoDialog.dismiss();
        }
    }
}
