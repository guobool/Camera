package swift.com.camera.Album;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import swift.com.camera.R;
import swift.com.camera.data.PictureInfo;
import swift.com.camera.utils.ImageLoad.GlideImageLoader;
import swift.com.camera.utils.ImageLoad.ImageLoader;

import static dagger.internal.Preconditions.checkNotNull;

/**
 * Created by bool on 17-6-19.
 */

public class SelectedPictureGridAdapter extends RecyclerView.Adapter {
    private ArrayMap<Integer, PictureInfo> mSelectedPictureMap;
    private AlbumActivity mAlbumActivity;
    private ImageLoader mImageLoader;
    public SelectedPictureGridAdapter(ArrayMap<Integer, PictureInfo> map, Context context) {
        super();
        mSelectedPictureMap = map;
        mAlbumActivity = (AlbumActivity)context;
        mImageLoader = GlideImageLoader.getInstance(context);
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SelectImageItem imageItem = new SelectImageItem(mAlbumActivity);
        imageItem.mCkSelect.setVisibility(View.GONE);
        return new ImageViewHolder(imageItem);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mImageLoader.getAdapteImage(mSelectedPictureMap.valueAt(position).getImagePath(),
                ((ImageViewHolder)holder).mSivItem.getImageView());
    }

    @Override
    public int getItemCount() {
        return mSelectedPictureMap.size();
    }

    public void onDataChaged(ArrayMap<Integer, PictureInfo> pictureInfoMap) {
        mSelectedPictureMap = checkNotNull(pictureInfoMap);
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
            mAlbumActivity.onItemSelected(getAdapterPosition());
        }

        public SelectImageItem getView() {
            return mSivItem;
        }
    }
}
