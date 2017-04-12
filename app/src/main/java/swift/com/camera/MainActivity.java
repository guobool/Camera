package swift.com.camera;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import swift.com.camera.ui.activity.CameraActivity;
import swift.com.camera.processing.ProcessingActivity;

public class MainActivity extends AppCompatActivity implements MainContract.View {
    private Button mBtnCamera; //相机页面跳转按钮
    private Button mBtnProcessuing; //处理页面跳转按钮
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnCamera = (Button)findViewById(R.id.btnCamera);
        mBtnProcessuing = (Button)findViewById(R.id.btnProcessing);
        mBtnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itnGoCameraPage = new Intent();
                itnGoCameraPage.setClass(MainActivity.this, CameraActivity.class);
                MainActivity.this.startActivity(itnGoCameraPage);
            }
        });
        mBtnProcessuing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itnGoCameraPage = new Intent();
                itnGoCameraPage.setClass(MainActivity.this, ProcessingActivity.class);
                MainActivity.this.startActivity(itnGoCameraPage);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstacesState) {
        return null;
    }
}
