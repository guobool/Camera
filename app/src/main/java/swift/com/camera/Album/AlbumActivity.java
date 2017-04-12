package swift.com.camera.Album;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import swift.com.camera.MainContract;
import swift.com.camera.R;


/**
 * Created by bool on 17-4-10.
 */

public class AlbumActivity extends AppCompatActivity implements MainContract.View {
    @Override
    protected void onCreate(Bundle savedInstancesStace){
        super.onCreate(savedInstancesStace);
        setContentView(R.layout.activity_album);
        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("相册"); // 必须在setSupportActionBar(toolbar)之前调用
        toolbar.setTitleTextColor(Color.BLACK);
        setSupportActionBar(toolbar);  //用toolbar替换actionbar

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.vector_drawable_back);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //添加菜单点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(toolbar, "Click setNavigationIcon", Snackbar.LENGTH_SHORT).show();
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstacesState) {
        return null;
    }

}
