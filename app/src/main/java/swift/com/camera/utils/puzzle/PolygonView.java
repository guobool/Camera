package swift.com.camera.utils.puzzle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by bool on 17-6-21.
 * 多边形视图，用于显示多个多边形的内容。
 */

public class PolygonView extends View {
    public PolygonView(Context context) {
        super(context);
    }

    /**
     * 视图中添加Bitmap类型图片
     * @param bitmap 要显示的图片
     */
    public void addBitmap(Bitmap bitmap) {
        addDrawable(new BitmapDrawable(getResources(), bitmap));
    }

    /**
     * 视图中添加Drawable类型的图片
     * @param drawable 要显示的图片
     */
    public void addDrawable(Drawable drawable) {
        // if ()
    }

}
