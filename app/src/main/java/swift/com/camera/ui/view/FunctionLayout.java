package swift.com.camera.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import swift.com.camera.R;

/**
 * Created by bool on 17-4-10.
 */

public class FunctionLayout extends RelativeLayout {

    public FunctionLayout(Context context) {
        super(context);
        initLayout(context);
    }
    public FunctionLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public FunctionLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    private void initLayout(Context context){
        LayoutInflater.from(context).inflate(R.layout.function_layout, this, true);
    }

}
