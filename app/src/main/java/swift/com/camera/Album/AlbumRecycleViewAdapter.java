package swift.com.camera.Album;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;
import java.util.Map;

import swift.com.camera.data.PictureBean;

import static android.support.v7.recyclerview.R.attr.layoutManager;
import static dagger.internal.Preconditions.checkNotNull;
import static java.lang.System.in;

/**
 * Created by bool on 17-4-17.
 */
// Adapter必须实现三个方法,分别用于创建组件容器，绑定组件容器和获取组件。
public class AlbumRecycleViewAdapter extends RecyclerView.Adapter<
        AlbumRecycleViewAdapter.ImageViewHolder>{
    private AlbumActivity mContext;
    private List<PictureBean> mPictureBeanList;
    public AlbumRecycleViewAdapter(List<PictureBean> list, Context context){
        super();
        mPictureBeanList = list;
        mContext = (AlbumActivity)context;
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
        mContext.setImage(mPictureBeanList.get(position).getmImagePath(),
                holder.mSivItem.getWidth(), holder.mSivItem.getHeight(),
                holder.mSivItem.getImageView());

        //holder.mSivItem.setImage(bitmap);
    }

    @Override
    public int getItemCount() {
        return mPictureBeanList.size();
    }

    public void onDataChaged(List<PictureBean> mImageList) {
        mPictureBeanList = checkNotNull(mImageList);
        notifyDataSetChanged();
    }

    public void onPictureLoaded(Bitmap picture) {
        checkNotNull(picture);
        //createViewHolder().setIsRecyclable();
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

