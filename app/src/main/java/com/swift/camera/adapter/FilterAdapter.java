package com.swift.camera.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.swift.camera.R;
import com.swift.camera.utils.PluginFilterHelper;
import com.swift.camera.utils.PluginFilterPackage;

/**
 * Created by why8222 on 2016/3/17.
 */
public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterHolder>{
    
    private List<String> mFilters;
    private Context mContext;
    private int mSelected = 0;

    public FilterAdapter(Context context) {
        this.mContext = context;
        this.mFilters = PluginFilterHelper.getInstance(mContext).getFilters();
    }

    @Override
    public FilterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.filter_item_layout, parent, false);
        FilterHolder viewHolder = new FilterHolder(view);
        viewHolder.thumbImage = (ImageView) view.findViewById(R.id.filter_thumb_image);
        viewHolder.filterName = (TextView) view.findViewById(R.id.filter_thumb_name);
        viewHolder.filterRoot = (FrameLayout)view.findViewById(R.id.filter_root);
        viewHolder.thumbSelected = (FrameLayout) view.findViewById(R.id.filter_thumb_selected);
        viewHolder.thumbSelected_bg = view.findViewById(R.id.filter_thumb_selected_bg);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FilterHolder holder, final int position) {
        if (position == 0) {
            holder.thumbImage.setImageResource(R.mipmap.filter_thumb_original);
            holder.filterName.setText(mContext.getString(R.string.no_filter));
            holder.filterName.setBackgroundColor(Color.GRAY);
            if (position == mSelected) {
                holder.thumbSelected.setVisibility(View.VISIBLE);
                holder.thumbSelected_bg.setBackgroundColor(Color.GRAY);
                holder.thumbSelected_bg.setAlpha(0.7f);
            } else {
                holder.thumbSelected.setVisibility(View.GONE);
            }
        } else {
            PluginFilterPackage p = PluginFilterHelper.getInstance(mContext).getPackage(mFilters.get(position));
            if (p != null) {
                holder.thumbImage.setImageDrawable(p.getFilterThumb());
                holder.filterName.setText(p.getFilterName());
                holder.filterName.setBackgroundColor(p.getFilterColor());
                if (position == mSelected) {
                    holder.thumbSelected.setVisibility(View.VISIBLE);
                    holder.thumbSelected_bg.setBackgroundColor(p.getFilterColor());
                    holder.thumbSelected_bg.setAlpha(0.7f);
                } else {
                    holder.thumbSelected.setVisibility(View.GONE);
                }
            }
        }

        holder.filterRoot.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(mSelected == position)
                    return;
                int lastSelected = mSelected;
                mSelected = position;
                notifyItemChanged(lastSelected);
                notifyItemChanged(position);
                onFilterChangeListener.onFilterChanged(mFilters.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFilters == null ? 0 : mFilters.size();
    }

    class FilterHolder extends RecyclerView.ViewHolder {
        ImageView thumbImage;
        TextView filterName;
        FrameLayout thumbSelected;
        FrameLayout filterRoot;
        View thumbSelected_bg;

        public FilterHolder(View itemView) {
            super(itemView);
        }
    }

    public interface onFilterChangeListener{
        void onFilterChanged(String filterId);
    }

    private onFilterChangeListener onFilterChangeListener;

    public void setOnFilterChangeListener(onFilterChangeListener onFilterChangeListener){
        this.onFilterChangeListener = onFilterChangeListener;
    }
}
