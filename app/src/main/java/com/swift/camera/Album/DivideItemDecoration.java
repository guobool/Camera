package com.swift.camera.Album;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;

/**
 * Created by bool on 17-4-14.
 */

public class DivideItemDecoration extends RecyclerView.ItemDecoration {


    public DivideItemDecoration(Context context){
    }
    private int getSpanCount(RecyclerView parent)
    {
        // 列数
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager)
        {

            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager)
        {
            spanCount = ((StaggeredGridLayoutManager) layoutManager)
                    .getSpanCount();
        }
        return spanCount;
    }
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int spanCount = getSpanCount(parent);//列数
        int itemCount = parent.getAdapter().getItemCount(); // 组件数
        int itemPosition = parent.getChildAdapterPosition(view);
        if (itemPosition > (itemCount - (itemCount % spanCount))) {
            // 如果是最后一行，则不需要需要绘制底部
            outRect.set(0, 0, 20, 0);
        } else if ((itemPosition + 1) % spanCount == 0) {
            // 如果是最后一列，则不需要绘制右边
            outRect.set(0, 0, 0, 20);
        } else {
            outRect.set(0, 0, 20,
                    20);
        }
    }

}
