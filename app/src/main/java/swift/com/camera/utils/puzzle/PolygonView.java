package swift.com.camera.utils.puzzle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by bool on 17-6-21.
 * 多边形视图，用于显示多个多边形的内容。
 */

public class PolygonView extends View {
    protected static final String TAG = "PolygonView";
    private int mLineWidth = 4;
    private Paint mPaint;
    private Polygon[] mPolygons;
    //private List<BitmapDrawable> mBitmapDrawableList;

    /**
     * 一般在直接New一个View的时候调用。
     * @param context
     */
    public PolygonView(Context context) {
        this(context, null, 0);
    }

    /**
     * 一般在layout文件中使用的时候会调用，关于它的所有属性(包括自定义属性)都会包含在attrs中传递进来。
     * @param context
     * @param attrs
     */
    public PolygonView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PolygonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mLineWidth);
        //mBitmapDrawableList = new ArrayList<>();
    }

    /**
     * APT21以上才用到
     * @param context
     * @param attrs
     * @param defStyleAttr　默认的Style，这里的默认的Style是指它在当前Application或Activity所用的Theme中的
     *                    默认Style，且只有在明确调用的时候才会生效，默认调用的依旧是两个参数的构造函数。
     * @param defStyleRes
     */

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PolygonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(mLineWidth);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthsize = MeasureSpec.getSize(widthMeasureSpec);      //取出宽度的确切数值
        int widthmode = MeasureSpec.getMode(widthMeasureSpec);      //取出宽度的测量模式

        int heightsize = MeasureSpec.getSize(heightMeasureSpec);    //取出高度的确切数值
        int heightmode = MeasureSpec.getMode(heightMeasureSpec);    //取出高度的测量模式

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        // 绘制圆弧
//        RectF rectF = new RectF(100,100,800,400);
//        mPaint.setColor(Color.BLUE);
//        canvas.drawArc(rectF,0,90,false,mPaint);
        for (int i = 0; i < mPolygons.length; i++) {
           // mPolygons[i].setBitmap(mBitmapDrawableList.get(i).getBitmap());
            mPolygons[i].draw(canvas, mPaint);
        }

    }

    /**
     * 添加用于显示图片的多边形区域
     * @param polygons　可变长度的多边形参数, 也可以传入数组
     */
    public void setPolygons(Polygon... polygons) {
        mPolygons = polygons;
    }


    public void setPolygons(List<Polygon> polygonList) {
        mPolygons = polygonList.toArray(new Polygon[polygonList.size()]);
    }

    /**
     * 视图中添加Bitmap类型图片
     * @param bitmap 要显示的图片
     */
    public void addBitmap(@NonNull Bitmap bitmap) {
        addDrawable(new BitmapDrawable(getResources(), bitmap));
    }

    /**
     * 视图中添加Drawable类型的图片
     * @param drawable 要显示的图片
     */
    public void addDrawable(@NonNull Drawable drawable) {
//         if (mBitmapDrawableList.size() >= mPolygons.length) {
//             Log.e(TAG, "addPiece: can not add more. the current puzzle layout can contains "
//                     + mPolygons.length
//                     + " puzzle piece.");
//         }

    }

    public void setBitmaps(@NonNull Bitmap... bitmaps) {
        for (Bitmap bitmap : bitmaps) {
            addBitmap(bitmap);
        }
    }

    public void setBitmapDrawables(@NonNull BitmapDrawable... drawables) {
        for (BitmapDrawable drawable: drawables) {
            addDrawable(drawable);
        }
    }



}
