package swift.com.camera.Album;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;

import swift.com.camera.R;

/**
 * Created by bool on 17-4-18.
 */

public class SelectImageView extends FrameLayout {
    ImageView mIvPicture;
    //private CheckBox mCkSelectPicture; 多选按钮，方便将来扩展
    public SelectImageView(@NonNull Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.image_select_item, this, true);
        mIvPicture = (ImageView)findViewById(R.id.ivPicture);
        //mCkSelectPicture = (CheckBox)findViewById(R.id.cbSelect);
    }

//    public void setImage(@NonNull Bitmap bitmap){
//        //mBitmap = bitmap;
//        mIvPicture.setImageBitmap(bitmap);
//    }

    public ImageView getImageView(){
        return mIvPicture;
    }
}
