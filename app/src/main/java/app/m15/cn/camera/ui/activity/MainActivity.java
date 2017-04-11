package app.m15.cn.camera.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import app.m15.cn.camera.R;

public class MainActivity extends AppCompatActivity {
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
}
