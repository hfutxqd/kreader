package com.onyx.jdread.reader.catalog.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.BookmarkListItemViewBinding;
import com.onyx.jdread.databinding.NotesListItemViewBinding;
import com.onyx.jdread.main.common.PageAdapter;
import com.onyx.jdread.reader.catalog.model.NoteModel;

import java.util.List;

/**
 * Created by huxiaomao on 2018/1/10.
 */

public class NoteAdapter extends PageAdapter<PageRecyclerView.ViewHolder, NoteModel, NoteModel> {
    private static final int row = JDReadApplication.getInstance().getApplicationContext().getResources().getInteger(R.integer.book_info_dialog_note_row);
    private View.OnClickListener onClickListener;

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public int getRowCount() {
        return row;
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public int getDataCount() {
        return getItemVMList().size();
    }

    @Override
    public void setRawData(List<NoteModel> rawData, Context context) {
        super.setRawData(rawData, context);
        setItemVMList(rawData);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        return new BookmarkModelHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_list_item_view, null));
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final NoteModel dataModel = getItemVMList().get(position);
        BookmarkModelHolder viewHolder = (BookmarkModelHolder) holder;
        viewHolder.bindTo(dataModel);
        viewHolder.rootView.setTag(dataModel.getPosition());
        viewHolder.rootView.setOnClickListener(onClickListener);
    }

    static class BookmarkModelHolder extends PageRecyclerView.ViewHolder {
        private final NotesListItemViewBinding binding;
        private View rootView;

        public BookmarkModelHolder(View view) {
            super(view);
            rootView = view;
            binding = DataBindingUtil.bind(view);
        }

        public NotesListItemViewBinding getBind() {
            return binding;
        }

        public void bindTo(NoteModel model) {
            binding.setNoteModel(model);
            binding.executePendingBindings();
        }
    }
}