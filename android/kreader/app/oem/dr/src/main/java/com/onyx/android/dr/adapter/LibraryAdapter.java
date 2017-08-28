package com.onyx.android.dr.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.sdk.data.LibraryDataModel;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.ui.utils.SelectionMode;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by suicheng on 2017/4/22.
 */

public class LibraryAdapter extends PageRecyclerView.PageAdapter<LibraryAdapter.LibraryItemViewHolder> {
    public interface ItemClickListener {
        void onClick(int position, View view);

        void onLongClick(int position, View view);
    }

    private Context context;

    private LibraryDataModel libraryDataModel = new LibraryDataModel();

    private List<Metadata> chosenItemsList = new ArrayList<>();
    private int selectionMode = SelectionMode.NORMAL_MODE;

    private ItemClickListener itemClickListener;
    private int rowCount = DRApplication.getInstance().getResources().getInteger(R.integer.common_books_fragment_row);
    private int colCount = DRApplication.getInstance().getResources().getInteger(R.integer.common_books_fragment_col);

    public LibraryAdapter(Context context) {
        this.context = context;
    }

    public void setRowCol(int row, int col) {
        this.rowCount = row;
        this.colCount = col;
    }

    public void updateLibraryDataModel(LibraryDataModel dataModel) {
        this.libraryDataModel = dataModel;
    }

    public LibraryDataModel getLibraryDataModel() {
        return libraryDataModel;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public int getLibraryListSize() {
        return CollectionUtils.getSize(libraryDataModel.visibleLibraryList);
    }

    public int getBookListSize() {
        return CollectionUtils.getSize(libraryDataModel.visibleBookList);
    }

    public int getBookItemPosition(int originPosition) {
        return originPosition - getLibraryListSize();
    }

    public List<Metadata> getChosenItemsList() {
        return chosenItemsList;
    }

    public List<Metadata> getMetadataList() {
        return libraryDataModel.visibleBookList;
    }

    public List<Library> getLibraryList() {
        return libraryDataModel.visibleLibraryList;
    }

    public void clearChosenItemsList() {
        chosenItemsList.clear();
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public int getColumnCount() {
        return colCount;
    }

    @Override
    public int getDataCount() {
        return getLibraryListSize() + getBookListSize();
    }

    @Override
    public LibraryItemViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.library_item, parent, false);
        inflate.setPadding(context.getResources().getInteger(R.integer.library_item_padding_left),
                context.getResources().getInteger(R.integer.library_item_padding_top),
                context.getResources().getInteger(R.integer.library_item_padding_right),
                context.getResources().getInteger(R.integer.library_item_padding_bottom));
        return new LibraryItemViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(LibraryItemViewHolder holder, int position) {
        holder.itemView.setTag(position);
        holder.checkBox.setVisibility(View.GONE);
        holder.titleView.setVisibility(View.VISIBLE);
        String title;
        if (position < getLibraryListSize()) {
            Library library = getLibraryList().get(position);
            title = library.getName();
            holder.imageCover.setImageResource(R.drawable.book_cover);
            holder.progressBar.setVisibility(View.INVISIBLE);
        } else {
            Metadata metadata = getMetadataList().get(getBookItemPosition(position));
            title = metadata.getTitle();
            if (StringUtils.isNullOrEmpty(title)) {
                title = metadata.getName();
            }
            holder.progressBar.setVisibility(View.VISIBLE);
            renderBookCoverImage(holder, metadata);
            renderBookProgress(holder, metadata);
            renderMultiCheckBox(holder, metadata);
        }
        holder.titleView.setText(title);
    }

    private void renderBookCoverImage(final LibraryItemViewHolder holder, final Metadata metadata) {
        String associationId = metadata.getAssociationId();
        if (StringUtils.isNullOrEmpty(associationId) || !getLibraryDataModel().thumbnailMap.containsKey(associationId)) {
            holder.imageCover.setImageResource(R.drawable.book_cover);
            return;
        }
        CloseableReference<Bitmap> refBitmap = getLibraryDataModel().thumbnailMap.get(associationId);
        if (refBitmap.isValid()) {
            holder.titleView.setVisibility(View.GONE);
            holder.imageCover.setImageBitmap(refBitmap.get());
        }
    }

    private void renderBookProgress(LibraryItemViewHolder holder, Metadata metadata) {
        holder.progressBar.setVisibility(metadata == null ? View.INVISIBLE : View.VISIBLE);
        if (metadata != null) {
            holder.progressBar.setProgress(metadata.getProgressPercent());
        }
    }

    private void renderMultiCheckBox(LibraryItemViewHolder holder, Metadata metadata) {
        if (isMultiSelectionMode()) {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(chosenItemsList.contains(metadata));
        }
    }

    public boolean isMultiSelectionMode() {
        return selectionMode == SelectionMode.MULTISELECT_MODE;
    }

    public void setMultiSelectionMode(int selectionMode) {
        this.selectionMode = selectionMode;
        if (selectionMode == SelectionMode.NORMAL_MODE) {
            chosenItemsList.clear();
        }
    }

    private void processItemClick(View view) {
        if (itemClickListener != null) {
            int position = (Integer) view.getTag();
            itemClickListener.onClick(position, view);
        }
    }

    private void processItemLongClick(View view) {
        if (itemClickListener != null) {
            int position = (Integer) view.getTag();
            itemClickListener.onLongClick(position, view);
        }
    }

    class LibraryItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.library_item_image_cover)
        ImageView imageCover;
        @Bind(R.id.library_item_textView_title)
        TextView titleView;
        @Bind(R.id.library_item_progress_line)
        ProgressBar progressBar;
        @Bind(R.id.library_item_multi_select_check_box)
        CheckBox checkBox;

        public LibraryItemViewHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processItemClick(v);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    processItemLongClick(v);
                    return true;
                }
            });
            ButterKnife.bind(this, itemView);
        }
    }
}