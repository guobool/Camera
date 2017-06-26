package com.swift.camera.beautify;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import javax.inject.Inject;

import com.swift.camera.Album.AlbumPresenterModule;
import com.swift.camera.Album.DaggerAlbumComponent;
import com.swift.camera.R;
import com.swift.camera.TheApplication;
import com.swift.camera.data.PictureBean;
import com.swift.camera.utils.ImageLoad.GlideImageLoader;
import com.swift.camera.utils.ImageLoad.ImageLoader;

import static dagger.internal.Preconditions.checkNotNull;

/**
 * Created by bool on 17-4-12.
 */

public class BeautifyActivity extends AppCompatActivity implements BeautifyContract.View{
    @Inject BeautifyPresenter mPresenter;
    private ImageView mIvBeautifyImage;
    private ImageLoader mImageLoader;
    @Override
    protected void onCreate(Bundle savedInstancesState){
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_beautify);
        mIvBeautifyImage = (ImageView)findViewById(R.id.ivBeautifyImage);
        PictureBean mPictureBean = (PictureBean) getIntent().getSerializableExtra("PictureBean");
        mImageLoader = GlideImageLoader.getInstance(this);
        // 注入
        DaggerBeautifyComponent.builder()
                .pictureRepositoryComponent(((TheApplication)getApplication()).getTasksRepositoryComponent())
                .beautifyPresenterModule(new BeautifyPresenterModule(this))
                .build()
                .inject(this);

        if(mPictureBean != null){
            //获取原图
            mImageLoader.getOriginalImage(mPictureBean.getImagePath(), mIvBeautifyImage);
        }
    }

    @Override
    public void setPresenter(@NonNull BeautifyContract.Presenter presenter) {
        mPresenter = (BeautifyPresenter)checkNotNull(presenter);
    }

}
