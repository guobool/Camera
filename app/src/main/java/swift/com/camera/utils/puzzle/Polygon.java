package swift.com.camera.utils.puzzle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
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
    public Polygon(PointF... pointFs) {
        assert pointFs != null: "参数为空";
        assert pointFs.length >= 3: "点个数不能少于三个";

        // TODO: 17-6-21 添加相邻两直线不重合，任意两直线不相交的检测。
        this.pointFs = pointFs;
    }

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
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(20);
        Path path = new Path();
        if (pointFs != null & pointFs.length != 0) {
            path.moveTo(pointFs[0].x, pointFs[0].y);
        }
        for (int i = 1; i < pointFs.length; i++) {
            path.lineTo(pointFs[i].x, pointFs[i].y);
            //canvas.drawLine(pointFs[i], pointFs[i%pointFs.length]);
        }
        path.close();
        //paint.setStrokeWidth(20);
        //canvas.drawPath(path, paint);

        canvas.concat(new Matrix());
        canvas.clipPath(path); // 切割丢弃边缘外的图像
        mDrawable.setBounds(0, 126, 1080, 1206);
        //canvas.drawBitmap(((BitmapDrawable)mDrawable).getBitmap(), new Matrix(), paint);
        mDrawable.draw(canvas);
        canvas.restore();
    }

}
