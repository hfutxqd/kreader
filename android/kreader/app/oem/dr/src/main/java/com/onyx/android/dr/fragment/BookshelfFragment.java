package com.onyx.android.dr.fragment;

import android.graphics.Bitmap;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.BookListAdapter;
import com.onyx.android.dr.adapter.BookshelfGroupAdapter;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.event.BackToMainViewEvent;
import com.onyx.android.dr.event.EBookListEvent;
import com.onyx.android.dr.holder.LibraryDataHolder;
import com.onyx.android.dr.interfaces.BookshelfView;
import com.onyx.android.dr.presenter.BookshelfPresenter;
import com.onyx.android.dr.reader.view.DisableScrollGridManager;
import com.onyx.android.dr.util.DRPreferenceManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.LibraryDataModel;
import com.onyx.android.sdk.data.LibraryViewInfo;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.common.FetchPolicy;
import com.onyx.android.sdk.data.model.v2.CloudMetadata_Table;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by hehai on 17-7-11.
 */

public class BookshelfFragment extends BaseFragment implements BookshelfView {
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.menu_back)
    LinearLayout menuBack;
    @Bind(R.id.title_bar_title)
    TextView titleBarTitle;
    @Bind(R.id.title_bar_right_menu)
    TextView titleBarRightMenu;
    @Bind(R.id.bookshelf_book_search)
    ImageView bookshelfBookSearch;
    @Bind(R.id.bookshelf_type_toggle)
    TextView bookshelfTypeToggle;
    @Bind(R.id.bookshelf_groups_recycler)
    PageRecyclerView bookshelfGroupsRecycler;
    @Bind(R.id.bookshelf_tab)
    TabLayout bookshelfTab;
    @Bind(R.id.bookshelf_tab_title)
    LinearLayout bookshelfTabTitle;
    private BookshelfGroupAdapter adapter;
    private String mode;
    private BookshelfPresenter bookshelfPresenter;
    private LibraryDataHolder dataHolder;
    private BookListAdapter listAdapter;
    private List<Library> libraryList;
    private List<String> languageList;
    private View titleBar;

    @Override
    protected void initListener() {
        bookshelfTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadDataWithMode(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void initView(View rootView) {
        adapter = new BookshelfGroupAdapter(getActivity());
        bookshelfGroupsRecycler.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        bookshelfGroupsRecycler.addItemDecoration(dividerItemDecoration);
        bookshelfGroupsRecycler.setAdapter(adapter);
        menuBack = (LinearLayout) rootView.findViewById(R.id.menu_back);

        listAdapter = new BookListAdapter(getActivity(), getDataHolder());
        listAdapter.setShowName(true);
        titleBar = rootView.findViewById(R.id.bookshelf_title_bar);
    }

    @Override
    protected void loadData() {
        if (bookshelfPresenter == null) {
            bookshelfPresenter = new BookshelfPresenter(this);
        }
        mode = DRPreferenceManager.getBookshelfType(getActivity(), Constants.LANGUAGE_BOOKSHELF);
        loadTabWithMode(mode);
    }

    private void loadTabWithMode(String mode) {
        switch (mode) {
            case Constants.LANGUAGE_BOOKSHELF:
                bookshelfPresenter.getLanguageList();
                break;
            case Constants.GRADED_BOOKSHELF:
                bookshelfPresenter.getLibraryList();
                break;
        }
    }

    private void loadDataWithMode(int position) {
        switch (mode) {
            case Constants.LANGUAGE_BOOKSHELF:
                loadBookshelf(languageList.get(position));
                break;
            case Constants.GRADED_BOOKSHELF:
                loadLibrary(libraryList.get(position));
                break;
        }
    }

    private void loadBookshelf(String language) {
        if (titleBarTitle != null) {
            titleBarTitle.setText(String.format(getString(R.string.bookshelf), language));
            bookshelfPresenter.getBookshelf(language, getDataHolder());
        }
    }

    private void loadLibrary(Library library) {
        if (library != null) {
            if (titleBarTitle != null) {
                titleBarTitle.setText(library.getName());
            }
            QueryArgs queryArgs = getDataHolder().getCloudViewInfo().buildLibraryQuery(library.getIdString());
            queryArgs.fetchPolicy = FetchPolicy.DB_ONLY;
            queryArgs.conditionGroup.and(CloudMetadata_Table.nativeAbsolutePath.isNotNull());
            bookshelfPresenter.getLibrary(queryArgs);
        }
    }

    @Override
    protected int getRootView() {
        return R.layout.fragment_bookshelf;
    }

    @Override
    public boolean onKeyBack() {
        back();
        return true;
    }

    private void back() {
        if (bookshelfGroupsRecycler.getAdapter() instanceof BookListAdapter) {
            bookshelfGroupsRecycler.setAdapter(adapter);
            titleBar.setVisibility(View.GONE);
            bookshelfTabTitle.setVisibility(View.VISIBLE);
        } else {
            EventBus.getDefault().post(new BackToMainViewEvent());
        }
    }

    @OnClick({R.id.menu_back, R.id.bookshelf_book_search, R.id.bookshelf_type_toggle})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_back:
                back();
                break;
            case R.id.bookshelf_book_search:
                search(Constants.NAME_SEARCH);
                break;
            case R.id.bookshelf_type_toggle:
                toggleBookshelfMode();
                break;
        }
    }

    private void toggleBookshelfMode() {
        if (mode.equals(Constants.LANGUAGE_BOOKSHELF)) {
            mode = Constants.GRADED_BOOKSHELF;
            bookshelfTypeToggle.setText(getString(R.string.grade_bookshelf));
        } else {
            mode = Constants.LANGUAGE_BOOKSHELF;
            bookshelfTypeToggle.setText(getString(R.string.language_bookshelf));
        }
        loadTabWithMode(mode);
        DRPreferenceManager.saveBookshelfType(DRApplication.getInstance(), mode);
    }

    private void search(String type) {
        ActivityManager.startSearchBookActivity(getActivity(), type);
    }

    @Override
    public void setBooks(List<Metadata> result) {
        bookshelfGroupsRecycler.setAdapter(listAdapter);
        QueryResult<Metadata> queryResult = new QueryResult<>();
        queryResult.list = result;
        queryResult.count = result.size();
        Map<String, CloseableReference<Bitmap>> bitmaps = DataManagerHelper.loadCloudThumbnailBitmapsWithCache(DRApplication.getInstance(), DRApplication.getCloudStore().getCloudManager(), queryResult.list);
        listAdapter.updateContentView(getLibraryDataModel(queryResult, bitmaps));
    }

    @Override
    public void setLanguageCategory(Map<String, List<Metadata>> map) {
        adapter.setMap(map);
    }

    @Override
    public void setLibraryList(List<Library> list) {
        this.libraryList = list;
        bookshelfTab.removeAllTabs();
        for (Library lib : libraryList) {
            bookshelfTab.addTab(bookshelfTab.newTab().setText(lib.getName()));
        }
        int selectedTabPosition = bookshelfTab.getSelectedTabPosition();
        loadLibrary(libraryList.get(selectedTabPosition));
    }

    @Override
    public void setLanguageList(List<String> languageList) {
        this.languageList = languageList;
        bookshelfTab.removeAllTabs();
        for (String language : languageList) {
            bookshelfTab.addTab(bookshelfTab.newTab().setText(language));
        }
        int selectedTabPosition = bookshelfTab.getSelectedTabPosition();
        loadBookshelf(languageList.get(selectedTabPosition));
    }

    private LibraryDataHolder getDataHolder() {
        if (dataHolder == null) {
            dataHolder = new LibraryDataHolder(getActivity());
            dataHolder.setCloudManager(DRApplication.getCloudStore().getCloudManager());
        }
        return dataHolder;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEBookListEvent(EBookListEvent event) {
        titleBar.setVisibility(View.VISIBLE);
        bookshelfTabTitle.setVisibility(View.GONE);
        if (mode.equals(Constants.LANGUAGE_BOOKSHELF)) {
            titleBarTitle.setText(languageList.get(bookshelfTab.getSelectedTabPosition()) + "/" + event.getLanguage());
        } else {
            titleBarTitle.setText(libraryList.get(bookshelfTab.getSelectedTabPosition()).getName() + "/" + event.getLanguage());
        }
        bookshelfPresenter.getBooks(event.getLanguage());
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private LibraryDataModel getLibraryDataModel(QueryResult<Metadata> result, Map<String, CloseableReference<Bitmap>> map) {
        return LibraryViewInfo.buildLibraryDataModel(result, map);
    }
}