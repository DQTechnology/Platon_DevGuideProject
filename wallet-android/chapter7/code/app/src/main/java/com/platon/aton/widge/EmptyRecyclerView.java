package com.platon.aton.widge;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class EmptyRecyclerView extends RecyclerView {

    private View mEmptyView;



    private final AdapterDataObserver mObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            Adapter adapter = getAdapter();
            assert adapter != null;
            if (adapter.getItemCount() == 0) {
                if (mEmptyView != null) {
                    mEmptyView.setVisibility(VISIBLE);
                }
                EmptyRecyclerView.this.setVisibility(GONE);
            } else {
                if (mEmptyView != null) {
                    mEmptyView.setVisibility(GONE);
                }
                EmptyRecyclerView.this.setVisibility(VISIBLE);
            }
        }


        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            onChanged();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            onChanged();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            onChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            onChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            onChanged();
        }

    };

    public EmptyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    public void setEmptyView(View view) {
        this.mEmptyView = view;
    }


    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        super.setAdapter(adapter);
        adapter.registerAdapterDataObserver(mObserver);
    }
}
