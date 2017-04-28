package com.onyx.android.eschool.activity;

import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.onyx.android.eschool.SchoolApp;
import com.onyx.android.eschool.action.LibraryDeleteAction;
import com.onyx.android.eschool.action.LibraryGotoPageAction;
import com.onyx.android.eschool.action.LibraryRemoveFromAction;
import com.onyx.android.eschool.adapter.LibraryAdapter;
import com.onyx.android.eschool.custom.PageIndicator;
import com.onyx.android.eschool.holder.LibraryDataHolder;
import com.onyx.android.eschool.R;
import com.onyx.android.eschool.action.LibraryBuildAction;
import com.onyx.android.eschool.action.ConfigFilterAction;
import com.onyx.android.eschool.action.MetadataLoadAction;
import com.onyx.android.eschool.action.LibraryMoveToAction;
import com.onyx.android.eschool.action.ConfigSortAction;
import com.onyx.android.eschool.events.LoadFinishEvent;
import com.onyx.android.eschool.glide.ThumbnailLoader;
import com.onyx.android.eschool.model.LibraryDataModel;
import com.onyx.android.eschool.utils.StudentPreferenceManager;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.BookFilter;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryPagination;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.data.db.LibraryDataCacheClearRequest;
import com.onyx.android.sdk.ui.utils.SelectionMode;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.SinglePageRecyclerView;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.ViewDocumentUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by suicheng on 2017/4/10.
 */

public class LibraryActivity extends BaseActivity {

    @Bind(R.id.content_pageView)
    SinglePageRecyclerView contentPageView;
    PageIndicator pageIndicator;

    @Bind(R.id.parent_library_ref)
    LinearLayout parentLibraryRefLayout;

    private LibraryDataHolder dataHolder;
    private LibraryAdapter libraryAdapter;

    private boolean longClickMode = false;
    private int currentChosenItemIndex = 0;

    private ThumbnailLoader thumbnailLoader;
    private QueryPagination pagination;

    private int row = 3;
    private int col = 3;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_library;
    }

    @Override
    protected void initConfig() {
        initDataHolder();
        initThumbnailLoader();
        loadQueryArgsConf();
    }

    private void initDataHolder() {
        dataHolder = SchoolApp.getLibraryDataHolder();
        dataHolder.setContext(this);
    }

    private void initThumbnailLoader() {
        thumbnailLoader = new ThumbnailLoader(LibraryActivity.this, SchoolApp.getDataManager()
                .getRemoteContentProvider());
    }

    @Override
    protected void initView() {
        initSupportActionBarWithCustomBackFunction();
        getSupportActionBar().addOnMenuVisibilityListener(new ActionBar.OnMenuVisibilityListener() {
            @Override
            public void onMenuVisibilityChanged(boolean isVisible) {
                if (!isVisible) {
                    quitLongClickMode();
                }
            }
        });
        initContentPageView();
        initPageIndicator();
    }

    private TextView getLibraryTextView(Library library) {
        TextView tv = (TextView) LayoutInflater.from(this).inflate(R.layout.parent_library_ref_item, null);
        tv.setText(library.getName());
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processLibraryRefViewClick(v);
            }
        });
        return tv;
    }

    private void processLibraryRefViewClick(View v) {
        int index = parentLibraryRefLayout.indexOfChild(v);
        if (index == parentLibraryRefLayout.getChildCount() - 1) {
            return;
        }
        int removeCount = parentLibraryRefLayout.getChildCount() - 1 - index;
        for (int i = 0; i < removeCount; i++) {
            removeLastParentLibrary();
        }
        loadData();
    }

    private void initPageIndicator() {
        pagination = dataHolder.getLibraryViewInfo().getQueryPagination();
        pagination.setCurrentPage(0);
        pageIndicator = new PageIndicator(findViewById(R.id.page_indicator_layout), pagination);
        pageIndicator.setTotalFormat(getString(R.string.total_format));
        pageIndicator.setPageChangedListener(new PageIndicator.PageChangedListener() {
            @Override
            public void prev() {
                prevPage();
            }

            @Override
            public void next() {
                nextPage();
            }

            @Override
            public void gotoPage(int page) {
                showGotoPageAction(page);
            }
        });
    }

    private void initContentPageView() {
        contentPageView.setLayoutManager(new DisableScrollGridManager(this));
        contentPageView.setOnChangePageListener(new SinglePageRecyclerView.OnChangePageListener() {
            @Override
            public void prev() {
                prevPage();
            }

            @Override
            public void next() {
                nextPage();
            }
        });
        libraryAdapter = new LibraryAdapter(this, thumbnailLoader);
        libraryAdapter.setRowCol(row, col);
        libraryAdapter.setItemClickListener(new LibraryAdapter.ItemClickListener() {
            @Override
            public void onClick(int position, View view) {
                processItemClick(position);
            }

            @Override
            public void onLongClick(int position, View view) {
                processItemLongClick(position);
            }
        });
        contentPageView.setAdapter(libraryAdapter);
    }

    private void updateContentView(LibraryDataModel libraryDataModel) {
        dataHolder.getLibraryViewInfo().setLibraryDataModel(libraryDataModel);
        LibraryDataModel newDataModel = new LibraryDataModel();
        int currentPage = pagination.getCurrentPage();
        int itemsPerPage = pagination.itemsPerPage();
        if (currentPage > libraryDataModel.libraryCount / itemsPerPage) {
            newDataModel.visibleLibraryList = new ArrayList<>();
            newDataModel.visibleBookList = libraryDataModel.visibleBookList;
        } else {
            int position = currentPage * itemsPerPage;
            for (int i = position;
                 (i < libraryDataModel.libraryCount && i < (currentPage + 1) * itemsPerPage);
                 i++) {
                newDataModel.visibleLibraryList.add(libraryDataModel.visibleLibraryList.get(i));
            }
            int size = itemsPerPage - CollectionUtils.getSize(newDataModel.visibleLibraryList);
            for (int i = 0; (i < size && i < CollectionUtils.getSize(libraryDataModel.visibleBookList)); i++) {
                newDataModel.visibleBookList.add(libraryDataModel.visibleBookList.get(i));
            }
        }
        newDataModel.bookCount = libraryDataModel.bookCount;
        newDataModel.libraryCount = libraryDataModel.libraryCount;
        libraryAdapter.updateLibraryDataModel(newDataModel);
        updateContentView();
    }

    private void updateContentView() {
        contentPageView.getAdapter().notifyDataSetChanged();
        updatePageIndicator();
    }

    private void updatePageIndicator() {
        int totalCount = getTotalCount();
        pagination.resize(row, col, totalCount);
        pageIndicator.updateTotal(totalCount);
        pageIndicator.updateCurrentPage(totalCount);
    }

    @Override
    protected void initData() {
        loadData(dataHolder.getLibraryViewInfo().libraryQuery());
    }

    @Override
    protected void onStart() {
        super.onStart();
        dataHolder.getEventBus().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        dataHolder.getEventBus().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataHolder.setContext(SchoolApp.singleton().getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dataHolder.getLibraryViewInfo().getLibraryDataModel() != null) {
            loadData(dataHolder.getLibraryViewInfo().getCurrentQueryArgs());
        }
    }

    private void prevPage() {
        if (!pagination.prevPage()) {
            return;
        }
        final MetadataLoadAction loadAction = new MetadataLoadAction(dataHolder.getLibraryViewInfo().prevPage());
        loadAction.execute(dataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null) {
                    updateContentView(loadAction.getLibraryDataModel());
                }
            }
        });
        prevLoad();
    }

    private void prevLoad() {
        int preLoadPage = pagination.getCurrentPage() - 1;
        if (preLoadPage < 0) {
            return;
        }
        final MetadataLoadAction loadAction = new MetadataLoadAction(
                dataHolder.getLibraryViewInfo().preLoadingPage(preLoadPage), false);
        loadAction.execute(dataHolder, null);
    }

    private void nextPage() {
        if (!pagination.nextPage()) {
            return;
        }
        final MetadataLoadAction loadAction = new MetadataLoadAction(dataHolder.getLibraryViewInfo().nextPage());
        loadAction.execute(dataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null) {
                    updateContentView(loadAction.getLibraryDataModel());
                }
            }
        });
        nextLoad();
    }

    private void nextLoad() {
        int preLoadPage = pagination.getCurrentPage() + 1;
        if (preLoadPage >= pagination.pages()) {
            return;
        }
        final MetadataLoadAction loadAction = new MetadataLoadAction(
                dataHolder.getLibraryViewInfo().preLoadingPage(preLoadPage), false);
        loadAction.execute(dataHolder, null);
    }

    private void showGotoPageAction(int currentPage) {
        final LibraryGotoPageAction gotoPageAction = new LibraryGotoPageAction(this, currentPage, pagination.pages());
        gotoPageAction.execute(dataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                int newPage = gotoPageAction.getSelectPage();
                gotoPageImpl(newPage);
            }
        });
    }

    private void gotoPageImpl(int page) {
        final int originPage = pagination.getCurrentPage();
        final MetadataLoadAction loadAction = new MetadataLoadAction(dataHolder.getLibraryViewInfo().gotoPage(page));
        loadAction.execute(dataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    pagination.setCurrentPage(originPage);
                    return;
                }
                prevLoad();
                nextLoad();
                updateContentView(loadAction.getLibraryDataModel());
            }
        });
    }

    private void loadData() {
        pagination.setCurrentPage(0);
        loadData(dataHolder.getLibraryViewInfo().libraryQuery());
    }

    private void loadData(QueryArgs queryArgs) {
        final MetadataLoadAction loadAction = new MetadataLoadAction(queryArgs);
        loadAction.execute(dataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                LibraryDataModel dataModel = loadAction.getLibraryDataModel();
                pagination.resize(row, col, dataModel.bookCount + dataModel.libraryCount);
                updateContentView(dataModel);
            }
        });
        nextLoad();
    }

    private void loadQueryArgsConf() {
        String sortBy = StudentPreferenceManager.getStringValue(LibraryActivity.this,
                R.string.library_activity_sort_by_key, SortBy.Name.toString());
        String filterBy = StudentPreferenceManager.getStringValue(LibraryActivity.this,
                R.string.library_activity_book_filter_key, BookFilter.ALL.toString());
        SortOrder sortOrder = SortOrder.values()[StudentPreferenceManager.getIntValue(LibraryActivity.this,
                R.string.library_activity_asc_order_key, 0)];
        dataHolder.getLibraryViewInfo().updateSortBy(SortBy.valueOf(sortBy), sortOrder);
        dataHolder.getLibraryViewInfo().updateFilterBy(BookFilter.valueOf(filterBy), sortOrder);
    }

    private void processSortBy() {
        final ConfigSortAction sortByAction = new ConfigSortAction(this);
        sortByAction.execute(dataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                loadData();
            }
        });
    }

    private void processFilterByBy() {
        final ConfigFilterAction filterByAction = new ConfigFilterAction(this);
        filterByAction.execute(dataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                loadData();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadFinishEvent(LoadFinishEvent event) {
        updateContentView(dataHolder.getLibraryViewInfo().getLibraryDataModel());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.library_option_menu, menu);
        return true;
    }

    private void prepareNormalOptionsMenu(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            switch (menu.getItem(i).getItemId()) {
                case R.id.menu_remove_from_library:
                    if (CollectionUtils.isNullOrEmpty(dataHolder.getLibraryViewInfo().getLibraryPathList())) {
                        menu.getItem(i).setVisible(false);
                    } else {
                        menu.getItem(i).setVisible(isMultiSelectionMode());
                    }
                    break;
                case R.id.menu_add_to_library:
                    menu.getItem(i).setVisible(isMultiSelectionMode());
                    break;
                case R.id.menu_delete_library:
                case R.id.menu_properties:
                    menu.getItem(i).setVisible(false);
                    break;
                default:
                    menu.getItem(i).setVisible(true);
                    break;
            }
        }
    }

    private void prepareLongClickOptionsMenu(Menu menu) {
        boolean isLibraryItem = isLibraryItem(currentChosenItemIndex);
        for (int i = 0; i < menu.size(); i++) {
            switch (menu.getItem(i).getItemId()) {
                case R.id.menu_properties:
                    menu.getItem(i).setVisible(true);
                    break;
                case R.id.menu_remove_from_library:
                    if (isLibraryItem) {
                        menu.getItem(i).setVisible(false);
                    } else {
                        menu.getItem(i).setVisible(!CollectionUtils.isNullOrEmpty(
                                dataHolder.getLibraryViewInfo().getLibraryPathList()));
                    }
                    break;
                case R.id.menu_delete_library:
                    menu.getItem(i).setVisible(isLibraryItem);
                    break;
                default:
                    menu.getItem(i).setVisible(!isLibraryItem);
                    break;
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isLongClickMode()) {
            prepareLongClickOptionsMenu(menu);
        } else {
            prepareNormalOptionsMenu(menu);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_by:
                processSortBy();
                return true;
            case R.id.menu_filter_by:
                processFilterByBy();
                return true;
            case R.id.menu_multi_select:
                getIntoMultiSelectMode();
                return true;
            case R.id.menu_build_library:
                processBuildLibrary();
                return true;
            case R.id.menu_add_to_library:
                if (isLongClickMode() && !isMultiSelectionMode()) {
                    libraryAdapter.clearChosenItemsList();
                    libraryAdapter.getChosenItemsList().add(getBookList().get(getBookItemPosition(currentChosenItemIndex)));
                }
                processAddToLibrary();
                return true;
            case R.id.menu_remove_from_library:
                if (isLongClickMode() && !isMultiSelectionMode()) {
                    libraryAdapter.clearChosenItemsList();
                    libraryAdapter.getChosenItemsList().add(getBookList().get(getBookItemPosition(currentChosenItemIndex)));
                }
                processRemoveFromLibrary();
                break;
            case R.id.menu_delete_library:
                processDeleteLibrary();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void cleanAllCache() {
        clearDataCache(true, true);
    }

    private void cleanMetadataCache() {
        clearDataCache(false, true);
    }

    private void clearDataCache(boolean clearLibrary, boolean clearMetadata) {
        LibraryDataCacheClearRequest clearRequest = new LibraryDataCacheClearRequest(clearLibrary, clearMetadata);
        dataHolder.getDataManager().submit(this, clearRequest, null);
    }

    private void processBuildLibrary() {
        new LibraryBuildAction(this, dataHolder.getLibraryViewInfo().getLibraryIdString())
                .execute(dataHolder, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        if (e != null) {
                            return;
                        }
                        cleanAllCache();
                        loadData();
                    }
                });
    }

    private void processAddToLibrary() {
        Library currentLibrary = new Library();
        currentLibrary.setIdString(dataHolder.getLibraryViewInfo().getLibraryIdString());
        LibraryMoveToAction moveToLibraryAction = new LibraryMoveToAction(this,
                dataHolder.getLibraryViewInfo().getLibraryDataModel().visibleLibraryList,
                libraryAdapter.getChosenItemsList());
        moveToLibraryAction.execute(dataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                quitMultiSelectionMode();
                cleanMetadataCache();
                loadData();
            }
        });
    }

    private void processRemoveFromLibrary() {
        Library fromLibrary = dataHolder.getLibraryViewInfo().getLibraryPathList().get(CollectionUtils.getSize(
                dataHolder.getLibraryViewInfo().getLibraryPathList()) - 1);
        LibraryRemoveFromAction removeFromAction = new LibraryRemoveFromAction(fromLibrary, libraryAdapter.getChosenItemsList());
        removeFromAction.execute(dataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                cleanMetadataCache();
                loadData();
            }
        });
    }

    private void processDeleteLibrary() {
        new LibraryDeleteAction(this, libraryAdapter.getLibraryList().get(currentChosenItemIndex))
                .execute(dataHolder, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        if (e != null) {
                            return;
                        }
                        cleanAllCache();
                        loadData();
                    }
                });
    }

    private void getIntoMultiSelectMode() {
        libraryAdapter.setMultiSelectionMode(SelectionMode.MULTISELECT_MODE);
        libraryAdapter.clearChosenItemsList();
        updateContentView();
    }

    @Override
    public void onBackPressed() {
        processBackRequest();
    }

    private void processBackRequest() {
        if (isMultiSelectionMode()) {
            quitMultiSelectionMode();
            updateContentView();
            return;
        }
        if (CollectionUtils.isNullOrEmpty(dataHolder.getLibraryViewInfo().getLibraryPathList())) {
            super.onBackPressed();
            return;
        }
        removeLastParentLibrary();
        loadData();
    }

    private void removeLastParentLibrary() {
        parentLibraryRefLayout.removeViewAt(parentLibraryRefLayout.getChildCount() - 1);
        dataHolder.getLibraryViewInfo().getLibraryPathList().remove(
                dataHolder.getLibraryViewInfo().getLibraryPathList().size() - 1);
    }

    private int getBookItemPosition(int originPosition) {
        return originPosition - getLibraryListSize();
    }

    private int getTotalCount() {
        LibraryDataModel dataModel = dataHolder.getLibraryViewInfo().getLibraryDataModel();
        return dataModel.bookCount + dataModel.libraryCount;
    }

    private int getBookListSize() {
        return libraryAdapter.getBookListSize();
    }

    private int getLibraryListSize() {
        return libraryAdapter.getLibraryListSize();
    }

    private List<Metadata> getBookList() {
        return libraryAdapter.getMetadataList();
    }

    private List<Library> getLibraryList() {
        return libraryAdapter.getLibraryList();
    }

    private void processBookItemOpen(int position) {
        if (getBookListSize() == 0) {
            return;
        }
        String filePath = getBookList().get(position).getNativeAbsolutePath();
        if (StringUtils.isNullOrEmpty(filePath)) {
            showToast(R.string.file_path_null, Toast.LENGTH_SHORT);
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            showToast(R.string.file_not_exist, Toast.LENGTH_SHORT);
            return;
        }
        ActivityUtil.startActivitySafely(this,
                ViewDocumentUtils.viewActionIntentWithMimeType(file),
                ViewDocumentUtils.getReaderComponentName(this));
    }

    private void addLibraryToParentRefList(Library library) {
        dataHolder.getLibraryViewInfo().getLibraryPathList().add(library);
        parentLibraryRefLayout.addView(getLibraryTextView(library));
    }

    private void processLibraryItemClick(int position) {
        Library library = getLibraryList().get(position);
        addLibraryToParentRefList(library);
        loadData();
    }

    private void processNormalModeItemClick(int position) {
        if (position < getLibraryListSize()) {
            processLibraryItemClick(position);
            return;
        }
        processBookItemOpen(getBookItemPosition(position));
    }

    private void processMultiModeItemClick(int position) {
        if (position < getLibraryListSize()) {
            return;
        }
        Metadata metadata = getBookList().get(getBookItemPosition(position));
        if (libraryAdapter.getChosenItemsList().contains(metadata)) {
            libraryAdapter.getChosenItemsList().remove(metadata);
        } else {
            libraryAdapter.getChosenItemsList().add(metadata);
        }
        updateContentView();
    }

    private void processItemClick(int position) {
        if (isMultiSelectionMode()) {
            processMultiModeItemClick(position);
            return;
        }
        processNormalModeItemClick(position);
    }

    private void processItemLongClick(int position) {
        if (isMultiSelectionMode()) {
            return;
        }
        getIntoLongClickMode(position);
        getSupportActionBar().openOptionsMenu();
    }

    private boolean isLibraryItem(int originPosition) {
        return originPosition < getLibraryListSize();
    }

    private void getIntoLongClickMode(int index) {
        longClickMode = true;
        currentChosenItemIndex = index;
    }

    private boolean isLongClickMode() {
        return longClickMode;
    }

    private void quitLongClickMode() {
        longClickMode = false;
    }

    public boolean isMultiSelectionMode() {
        return libraryAdapter.isMultiSelectionMode();
    }

    private void quitMultiSelectionMode() {
        libraryAdapter.setMultiSelectionMode(SelectionMode.NORMAL_MODE);
        libraryAdapter.clearChosenItemsList();
    }
}