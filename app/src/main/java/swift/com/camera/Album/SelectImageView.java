package swift.com.camera.Album;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import swift.com.camera.R;

/**
 * Created by bool on 17-4-18.
 */

public class SelectImageView extends FrameLayout {
    ImageView mIvPicture;
    private CheckBox mCkSelectPicture; // 多选按钮，方便将来扩展
    private Boolean isSelected;
    public SelectImageView(@NonNull Context context) {
        super(context);
        isSelected = false;
        LayoutInflater.from(context).inflate(R.layout.image_select_item, this, true);
        mIvPicture = (ImageView)findViewById(R.id.ivPicture);
        mCkSelectPicture = (CheckBox)findViewById(R.id.cb_image_select);
        mCkSelectPicture.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.cb_image_select) {
                    if ( isSelected == false) {
                        isSelected = true;
                    } else {
                        isSelected = false;
                    }
                }
            }
        });
    }

    public ImageView getImageView(){
        return mIvPicture;
    }
}
