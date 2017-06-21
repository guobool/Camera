package swift.com.camera.utils.puzzle;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by bool on 17-6-21.
 */

public class Polygon {
    protected PointF[] pointFs;
    protected Line[] lines;
    public Polygon(PointF... pointFs) {
        assert pointFs != null: "参数为空";
        assert pointFs.length >= 3: "点个数不能少于三个";

        lines = new Line[pointFs.length];
        for ( int i = 0; i < pointFs.length ; i++) {
            lines[i] = new Line(pointFs[i], pointFs[i % pointFs.length]);
        }
        // TODO: 17-6-21 添加相邻两直线不重合，任意两直线不相交的检测。
        this.pointFs = pointFs;
    }

    public int getVertexNum() {
        return pointFs.length;
    }
    public PointF[] getVertex() {
        return pointFs;
    }
    public Line[] getLines() {
        return lines;
    }

}
