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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import java.util.List;

import javax.inject.Inject;
import swift.com.camera.R;
import swift.com.camera.TheApplication;
import swift.com.camera.beautify.BeautifyActivity;
import swift.com.camera.data.PictureBean;
import swift.com.camera.processing.ProcessingActivity;

import static dagger.internal.Preconditions.checkNotNull;


/**
 * Created by bool on 17-4-10.
 */

public class AlbumActivity extends AppCompatActivity implements AlbumContract.View {
    @Inject AlbumPresenter mPresenter;
    List<PictureBean> mImageList;
    RecyclerView mRvPhotoList;
    FloatingActionButton mFabComplateSelect;
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
        mImageList = mPresenter.getImagesList();


        //----------------------------RecycleView------------------------
        mRvPhotoList = (RecyclerView)findViewById(R.id.rvPhotoList);
        mRvPhotoList.setLayoutManager(new GridLayoutManager(this,3)); //每行3列
        mRvPhotoList.setAdapter(new AlbumRecycleViewAdapter(AlbumActivity.this));
        mRvPhotoList.addItemDecoration(new DivideItemDecoration(this));
    }


    public Bitmap getImage(int position, int width, int height){
        PictureBean picture = (PictureBean)mImageList.get(position);
        return mPresenter.getBitMap(picture.getmImagePath(), width, height);
    }

    public int getImageNum(){
        return mImageList.size();
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
    public void toBeautifyActivity() {
        Intent intent = new Intent(this, BeautifyActivity.class);
        startActivity(intent);
    }

    public void onItemSelected(int index) {
        mPresenter.toBeaytifyActivity(mImageList.get(index));
    }
}
