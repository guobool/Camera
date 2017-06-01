package swift.com.camera.Album;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import swift.com.camera.R;
import swift.com.camera.TheApplication;
import swift.com.camera.beautify.BeautifyActivity;
import swift.com.camera.data.PictureBean;
import swift.com.camera.processing.ProcessingActivity;
import swift.com.camera.utils.ImageLoad.ImageLoader;

import static dagger.internal.Preconditions.checkNotNull;


/**
 * Created by bool on 17-4-10.
 */

public class AlbumActivity extends AppCompatActivity implements AlbumContract.View {
    @Inject AlbumPresenter mPresenter;
    private List<PictureBean> mImageList;
    private RecyclerView mRvPhotoList;
    private AlbumRecycleViewAdapter mRecycleAdaptere;

    @Override
    protected void onCreate(final Bundle savedInstancesStace){
        super.onCreate(savedInstancesStace);
        setContentView(R.layout.activity_album);
        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("相册"); // 必须在setSupportActionBar(toolbar)之前调用
        toolbar.setTitleTextColor(Color.BLACK);
        setSupportActionBar(toolbar);  //用toolbar替换actionbar

        // 添加返回按钮图标
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.vector_drawable_back);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //添加菜单点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.toProcessingActivity();
            }
        });

        // 注入AlbumPresenter对象
        DaggerAlbumComponent.builder()
                .pictureRepositoryComponent(((TheApplication)getApplication()).getTasksRepositoryComponent())
                .albumPresenterModule(new AlbumPresenterModule(this))
                .build()
                .inject(this);


        //----------------------------RecycleView------------------------
        mRvPhotoList = (RecyclerView)findViewById(R.id.rvPhotoList);
        mRvPhotoList.setLayoutManager(new GridLayoutManager(this,3)); //每行3列
        mRecycleAdaptere = new AlbumRecycleViewAdapter(new ArrayList<PictureBean>(0),
                AlbumActivity.this);
        mRvPhotoList.setAdapter(mRecycleAdaptere);
        mRvPhotoList.addItemDecoration(new DivideItemDecoration(this));
        mPresenter.getImagesList();
    }

    @Override
    public void setPresenter(@NonNull AlbumContract.Presenter presenter) {
        mPresenter = (AlbumPresenter)checkNotNull(presenter);
    }

    @Override
    public void toProcessingActivity() {
        Intent intent = new Intent(this, ProcessingActivity.class);
        startActivity(intent);
    }


    @Override
    public void toBeautifyActivity(PictureBean pictureBean) {
        Intent intent = new Intent(this, BeautifyActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("PictureBean",pictureBean);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    @Override
    public void pictureBeanLoaded(List<PictureBean> pictureBeanList) {
        mImageList = pictureBeanList;
        mRecycleAdaptere.onDataChaged(mImageList);
    }

    public void onItemSelected(int index) {
        mPresenter.toBeaytifyActivity(mImageList.get(index));
    }
}
