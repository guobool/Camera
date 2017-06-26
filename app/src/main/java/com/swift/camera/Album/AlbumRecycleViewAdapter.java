package com.swift.camera.Album;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import javax.inject.Inject;
import com.swift.camera.data.PictureBean;
import com.swift.camera.utils.ImageLoad.GlideImageLoader;
import com.swift.camera.utils.ImageLoad.ImageLoader;
import static dagger.internal.Preconditions.checkNotNull;

/**
 * Created by bool on 17-4-17.
 */
// Adapter必须实现三个方法,分别用于创建组件容器，绑定组件容器和获取组件。
public class AlbumRecycleViewAdapter extends RecyclerView.Adapter<
        AlbumRecycleViewAdapter.ImageViewHolder>{
    private AlbumActivity mContext;
    private List<PictureBean> mPictureBeanList;
    private ImageLoader mImageLoader;
    public AlbumRecycleViewAdapter(List<PictureBean> list, Context context){
        super();
        mPictureBeanList = list;
        mContext = (AlbumActivity)context;
        mImageLoader = GlideImageLoader.getInstance(context);

    }
    // 创建时用于创建组件,每次创建一个组件都会调用一次该方法。封装是内部实现了组件的重复利用，进行了优化
    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageViewHolder holder = new ImageViewHolder(new SelectImageView(mContext));
        return holder;
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        // 根据显示的宽高获取相应尺寸的图片
        mImageLoader.getAdapteImage(mPictureBeanList.get(position).getImagePath(),
                holder.mSivItem.getImageView());
//        mContext.setImage(mPictureBeanList.get(position).getImagePath(),
//                holder.mSivItem.getImageView());
    }

    @Override
    public int getItemCount() {
        return mPictureBeanList.size();
    }

    public void onDataChaged(List<PictureBean> mImageList) {
        mPictureBeanList = checkNotNull(mImageList);
        notifyDataSetChanged();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        SelectImageView mSivItem;
        public ImageViewHolder(View itemView) {
            super(itemView);
            mSivItem = (SelectImageView) itemView;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mContext.onItemSelected(getAdapterPosition());
        }

        public View getView() {
            return mSivItem;
        }
    }
}

