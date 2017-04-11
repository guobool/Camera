package swift.com.camera.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import app.m15.cn.camera.R;

/**
 * Created by bool on 17-4-10.
 */

public class AlbumActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstancesStace){
        super.onCreate(savedInstancesStace);
        setContentView(R.layout.activity_album);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);  // 兼容早起版本的ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_previous_page);
        actionBar.setDisplayHomeAsUpEnabled(true);

    }
}
