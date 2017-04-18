package swift.com.camera.Album;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by bool on 17-4-17.
 */
// Adapter必须实现三个方法,分别用于创建组件容器，绑定组件容器和获取组件。
public class AlbumRecycleViewAdapter extends RecyclerView.Adapter<
        AlbumRecycleViewAdapter.ImageViewHolder>{
    private AlbumActivity mContext;

    public AlbumRecycleViewAdapter(Context context){
        super();
        mContext = (AlbumActivity)context;
    }
    // 创建时用于创建组件,每次创建一个组件都会调用一次该方法。封装是内部实现了组件的重复利用，进行了优化
    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageViewHolder holder = new ImageViewHolder(new SelectImageView(mContext));
        Log.e("onCreateViewHolder", "onCreateViewHolder");
        return holder;
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        // 根据显示的宽高获取相应尺寸的图片，
        Bitmap bitmap = mContext.getImage(position,
                holder.mSivItem.getWidth(), holder.mSivItem.getHeight());
        holder.mSivItem.setImage(bitmap);

    }

    @Override
    public int getItemCount() {
        int i = mContext.getImageNum();
        Log.e("xcvc","getItemCount"+ i);
        return mContext.getImageNum();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        SelectImageView mSivItem;
        public ImageViewHolder(View itemView) {
            super(itemView);
            mSivItem = (SelectImageView) itemView;
        }
    }
}

