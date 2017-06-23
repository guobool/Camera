package swift.com.camera.utils.puzzle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by bool on 17-6-21.
 * 用于显示一张图片的多边形
 */

public class Polygon {
    protected PointF[] pointFs;
    protected Drawable mDrawable;
    protected Bitmap mBitmap;
    protected Context mContext;
    public Polygon(@NonNull BitmapDrawable drawable, PointF... pointFs) {
        mDrawable = drawable;

        assert pointFs != null: "参数为空";
        assert pointFs.length >= 3: "点个数不能少于三个";

        // TODO: 17-6-21 添加相邻两直线不重合，任意两直线不相交的检测。
        this.pointFs = pointFs;
    }

    public int getVertexNum() {
        return pointFs.length;
    }
    public PointF[] getVertex() {
        return pointFs;
    }


    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.save();
        Path path = new Path();
        //.getClipBounds().clear
        for (int i = 0; i < pointFs.length; i++) {
            path.lineTo(pointFs[i].x, pointFs[i].y);
        }
        path.close();
        canvas.concat(new Matrix());
        //paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        //mDrawable.setAlpha(alpha);
        canvas.clipPath(path); // 切割丢弃边缘外的图像
        mDrawable.setBounds(0, 126, 1080, 1206);
        //canvas.drawBitmap(mBitmap, pointFs[0].x, pointFs[0].y, paint);
        mDrawable.draw(canvas);

        //canvas.restore();
        //paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        //path.reset();
        canvas.restore();
    }

}
