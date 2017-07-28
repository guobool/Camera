package com.swift.camera.Album;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;


import com.swift.camera.data.PictureInfo;
import com.swift.camera.utils.ImageLoad.GlideImageLoader;
import com.swift.camera.utils.ImageLoad.ImageLoader;

import java.util.List;
import java.util.Map;

import static dagger.internal.Preconditions.checkNotNull;

/**
 * Created by bool on 17-4-17.
 */
// Adapter必须实现三个方法,分别用于创建组件容器，绑定组件容器和获取组件。
public class AlbumRecycleViewAdapter extends RecyclerView.Adapter<
        AlbumRecycleViewAdapter.ImageViewHolder>{
    private AlbumActivity mContext;
    private List<PictureInfo> mPictureInfoList;
    private Map<Integer, PictureInfo> mSelectedPictureMap;
    private ImageLoader mImageLoader;
    public AlbumRecycleViewAdapter(List<PictureInfo> list, Context context){
        super();
        mPictureInfoList = list;
        mContext = (AlbumActivity)context;
        mImageLoader = GlideImageLoader.getInstance(context);
        mSelectedPictureMap = new ArrayMap<>();
    }
    // 创建时用于创建组件,每次创建一个组件都会调用一次该方法。封装是内部实现了组件的重复利用，进行了优化
    @Override
    public ImageViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        ImageViewHolder holder = new ImageViewHolder(new SelectImageItem(mContext));
        return holder;
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, final int position) {
        // 根据显示的宽高获取相应尺寸的图片
        final PictureInfo pictureInfo =  mPictureInfoList.get(position);
        final SelectImageItem selectItem = holder.getView();
        final CheckBox checkBox = selectItem.mCkSelect;
        checkBox.setTag(position);
        checkBox.setOnCheckedChangeListener(null);//先设置一次CheckBox的选中监听器，传入参数null
        checkBox.setChecked(pictureInfo.isSelected);//用数组中的值设置CheckBox的选中状态

        //再设置一次CheckBox的选中监听器，当CheckBox的选中状态发生改变时，把改变后的状态储存在数组中
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean selectFlag) {
                pictureInfo.isSelected = selectFlag;
                if (selectFlag) {
                    mSelectedPictureMap.put(position, pictureInfo);
                } else if (mSelectedPictureMap.containsKey(position)) {
                    mSelectedPictureMap.remove(position);
                }
                mContext.onSelectChanged((ArrayMap)mSelectedPictureMap);
            }
        });

        mImageLoader.getAdapteImage(mPictureInfoList.get(position).getImagePath(),
                holder.mSivItem.getImageView());
    }

    @Override
    public int getItemCount() {
        return mPictureInfoList.size();
    }

    public void onDataChaged(List<PictureInfo> mImageList) {
        mPictureInfoList = checkNotNull(mImageList);
        notifyDataSetChanged();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        SelectImageItem mSivItem;
        public ImageViewHolder(View itemView) {
            super(itemView);
            mSivItem = (SelectImageItem) itemView;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mContext.onItemSelected(getAdapterPosition());
        }

        public SelectImageItem getView() {
            return mSivItem;
        }
    }
}

