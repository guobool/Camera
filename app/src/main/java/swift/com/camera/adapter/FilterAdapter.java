package swift.com.camera.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import swift.com.camera.R;
import swift.com.camera.utils.FilterHelper;

/**
 * Created by why8222 on 2016/3/17.
 */
public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterHolder>{
    
    private List<String> filters;
    private Context context;
    private int selected = 0;

    public FilterAdapter(Context context) {
        this.filters = new ArrayList<>();
        this.context = context;
    }

    @Override
    public FilterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.filter_item_layout,
                parent, false);
        FilterHolder viewHolder = new FilterHolder(view);
        viewHolder.thumbImage = (ImageView) view
                .findViewById(R.id.filter_thumb_image);
        viewHolder.filterName = (TextView) view
                .findViewById(R.id.filter_thumb_name);
        viewHolder.filterRoot = (FrameLayout)view
                .findViewById(R.id.filter_root);
        viewHolder.thumbSelected = (FrameLayout) view
                .findViewById(R.id.filter_thumb_selected);
        viewHolder.thumbSelected_bg = view.
                findViewById(R.id.filter_thumb_selected_bg);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FilterHolder holder, final int position) {
        holder.thumbImage.setImageBitmap(FilterHelper.filterThumb(filters.get(position)));
        holder.filterName.setText(FilterHelper.filterName(filters.get(position)));
        holder.filterName.setBackgroundColor(context.getResources().getColor(
                FilterHelper.filterColor(filters.get(position))));
        if(position == selected){
            holder.thumbSelected.setVisibility(View.VISIBLE);
            holder.thumbSelected_bg.setBackgroundColor(context.getResources().getColor(
                    FilterHelper.filterColor(filters.get(position))));
            holder.thumbSelected_bg.setAlpha(0.7f);
        }else {
            holder.thumbSelected.setVisibility(View.GONE);
        }

        holder.filterRoot.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(selected == position)
                    return;
                int lastSelected = selected;
                selected = position;
                notifyItemChanged(lastSelected);
                notifyItemChanged(position);
                onFilterChangeListener.onFilterChanged(filters.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return filters == null ? 0 : filters.size();
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
