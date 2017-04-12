package swift.com.camera.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import swift.com.camera.R;

/**
 * Created by bool on 17-4-10.
 */

public class FunctionLayout extends RelativeLayout {
    private TextView mTvDesction;
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
        mTvDesction = ((TextView)findViewById(R.id.tvDescription));
    }

    public void setDesption(String desc){
        mTvDesction.setText(desc);
    }
}
