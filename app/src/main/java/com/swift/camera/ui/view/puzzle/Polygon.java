package com.swift.camera.ui.view.puzzle;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

/**
 * Created by bool on 17-6-21.
 * 用于显示一张图片的多边形
 */

public class Polygon {
    protected PointF[] mPointFArray;         // 多边形顶点
    protected Drawable mDrawable;
    protected Bitmap mBitmap;
    protected int mFillet  = 0;         // 默认圆角为０
    protected int mMaxFillet = 100;     // 设置贝塞尔曲线控制点到数据点的长度的阀值
    protected PointF[] mBezierDataPoint;// 绘制圆角的贝塞尔曲线数据点，原多边形定点作为控制点。每个控制点的两边
                                        // 添加两个数据点
    protected Path mPath;               // 边界路径
    protected int mBorderWidth;

    /**
     * 用于绘制图片的多边形
     * @param pointFs　多边形的顶点，数量只少为三个
     */
    public Polygon(PointF... pointFs) {
        this(new BitmapDrawable(), pointFs);
    }

    /**
     * 用于绘制图片的多边形,带有绘制的图片
     * @param drawable　用于绘制的drawable对象
     * @param pointFs　多边形的顶点，数量大于等于３
     */
    public Polygon(@NonNull BitmapDrawable drawable, PointF... pointFs) {
        mDrawable = drawable;
        assert pointFs != null: "参数为空";
        assert pointFs.length >= 3: "点个数不能少于三个";

        // TODO: 17-6-21 添加相邻两直线不重合，任意两直线不相交的检测。
        this.mPointFArray = pointFs;
        mBezierDataPoint = new PointF[pointFs.length * 2];
        for(int i = 0; i < mBezierDataPoint.length; i++) {
            mBezierDataPoint[i] = new PointF();
        }
        calculateBezierDataPoint();
        mPath = new Path();
    }
    protected void calculateBezierDataPoint(){
        if ( mPointFArray == null) return ;
        double lambda = 0; // A(x1,y1),B(x2,y2),点P(x,y)分线段AB所成的比是λ
        //x=[x1＋λx2]/(1＋λ)
        //y=[y1＋λy2]/(1＋λ)
        PointF upperPoint, nextPoint;
        for(int i = 0; i < mPointFArray.length; i++) {
            // 当前点之前插入的贝塞尔数据点的计算
            // 计算lambda
            upperPoint = mPointFArray[(i - 1 + mPointFArray.length) % mPointFArray.length];
            lambda = mFillet / (Math.sqrt(Math.pow(mPointFArray[i].x - upperPoint.x, 2) +
                                            Math.pow(mPointFArray[i].y - upperPoint.y, 2)));
            // 计算当前点之前插入的点
            //mBezierDataPoint[i * 2] = new PointF();
            mBezierDataPoint[i * 2].x = (float) ((mPointFArray[i].x + lambda * upperPoint.x) / (1 + lambda));
            mBezierDataPoint[i * 2].y = (float) ((mPointFArray[i].y + lambda * upperPoint.y) / (1 + lambda));

            // 计算当前点之后插入的贝塞尔数据点
            // 计算lambda
            nextPoint = mPointFArray[(i + 1) % mPointFArray.length];
            lambda = mFillet / (Math.sqrt(Math.pow(mPointFArray[i].x - nextPoint.x, 2) +
                    Math.pow(mPointFArray[i].y - nextPoint.y, 2)));
            // 点位置
            mBezierDataPoint[i * 2 + 1].x = (float) ((mPointFArray[i].x + lambda * nextPoint.x) / (1 + lambda));
            mBezierDataPoint[i * 2 + 1].y = (float) ((mPointFArray[i].y + lambda * nextPoint.y) / (1 + lambda));
        }
    }

    /**
     * 获得多边形定点数
     * @return 定点数
     */
    public int getVertexNum() {
        return mPointFArray.length;
    }

    /**
     * 获得多边形的顶点
     * @return 顶点
     */
    public PointF[] getVertex() {
        return mPointFArray;
    }


    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    /**
     * 绘制多边形的图片
     * @param canvas 画布
     * @param paint 画笔
     */
    public void draw(Canvas canvas, Paint paint) {
        canvas.save();
        calculateBezierDataPoint();
        paint.setStyle(Paint.Style.STROKE);
        mPath.reset();
        float pointX = 0, pointY = 0;
        if (mPointFArray != null & mPointFArray.length != 0) {
            mPath.moveTo(mBezierDataPoint[0].x, mBezierDataPoint[0].y);
            mPath.quadTo(mPointFArray[0].x, mPointFArray[0].y, mBezierDataPoint[1].x,
                    mBezierDataPoint[1].y);
            pointX = mPointFArray[0].x;
            pointY = mPointFArray[0].y;
        }
        int i = 1;
        for (i = 1; i < mPointFArray.length; i++) {
            mPath.lineTo(mBezierDataPoint[i * 2].x, mBezierDataPoint[i * 2].y);
            mPath.quadTo(mPointFArray[i].x, mPointFArray[i].y, mBezierDataPoint[i * 2 + 1].x,
                    mBezierDataPoint[i * 2 + 1].y);
            pointX += mPointFArray[i].x;
            pointY += mPointFArray[i].y;
        }
        pointX /= i;
        pointY /= i;
        mPath.close();
        // 先向重心收缩，收缩比例为：(width/2 - borderWidth) / (width / 2)
        canvas.scale((1080f-mBorderWidth*2)/1080, (1080f-mBorderWidth*2)/1080, pointX, pointY);
        canvas.scale(1080f /(1080f + mBorderWidth/2), 1080f/(1080+mBorderWidth/2), 1080f/2, (1206+126)/2);
        canvas.concat(new Matrix());
        canvas.clipPath(mPath); // 切割丢弃边缘外的图像
        mDrawable.setBounds(0, 126, 1080, 1206);
        mDrawable.draw(canvas);
        canvas.restore();
    }

    public PointF[] getPointFArray() {
        return mPointFArray;
    }

    public void setBorderWidth(int borderWidth) {
        mBorderWidth = borderWidth;
    }

    public void setAngleRoundness(int angleRoundness) {
        mFillet = angleRoundness;
    }
}
