package swift.com.camera.Album;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import swift.com.camera.R;

/**
 * Created by bool on 17-4-18.
 */

public class SelectImageItem extends FrameLayout {
    ImageView mIvPicture;
    CheckBox mCkSelect; // 多选按钮，方便将来扩展
    public SelectImageItem(@NonNull Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.image_select_item, this, true);
        mIvPicture = (ImageView)findViewById(R.id.ivPicture);
        mCkSelect = (CheckBox)findViewById(R.id.cb_image_select);
    }

    public ImageView getImageView(){
        return mIvPicture;
    }

    public void listernSelectChange(CompoundButton.OnCheckedChangeListener listener) {
        mCkSelect.setOnCheckedChangeListener(listener);
    }
}
