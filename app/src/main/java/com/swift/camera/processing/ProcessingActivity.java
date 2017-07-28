package com.swift.camera.processing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import javax.inject.Inject;

import com.swift.camera.R;
import com.swift.camera.Album.AlbumActivity;
import com.swift.camera.TheApplication;
import com.swift.camera.beautify.BeautifyActivity;
import com.swift.camera.puzzle.PuzzleActivity;
import com.swift.camera.ui.view.FunctionLayout;

import static com.squareup.haha.guava.base.Joiner.checkNotNull;

/**
 * Created by bool on 17-4-11.
 */

public class ProcessingActivity extends AppCompatActivity implements ProcessingContract.View{
    @Inject  ProcessingPresenter mPresenter;
    private FunctionLayout mRlOneLeyBeautify; // 一键美图
    private FunctionLayout mRlBeautifyPictures;  // 美化图片
    private FunctionLayout mRlPuzzle; // 拼图
    private FunctionLayout mRlPictureInPicture; // 画中画
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imgprocess);
        mRlOneLeyBeautify = (FunctionLayout)findViewById(R.id.rlOneLeyBeautify);
        mRlBeautifyPictures = (FunctionLayout)findViewById(R.id.rlBeautifyPictures);
        mRlPuzzle = (FunctionLayout)findViewById(R.id.rlPuzzle);
        mRlPictureInPicture = (FunctionLayout)findViewById(R.id.rlPictureInPicture);

        // 子类依赖对象 ，并注入
        DaggerProcessingComponent.builder()
                .pictureRepositoryComponent(((TheApplication)getApplication()).getTasksRepositoryComponent())
                .processingPresenterModule(new ProcessingPresenterModule(this))
                .build()
                .inject(this);

        mRlOneLeyBeautify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.jumpAlbumPage();
            }
        });

        mRlBeautifyPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.jumpBeautifyPage();
            }
        });
        mRlPuzzle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProcessingActivity.this, PuzzleActivity.class));
            }
        });

    }


    public void jumpAlbumPage(){
        Intent intent = new Intent(this, AlbumActivity.class);
        startActivity(intent);
    }

    @Override
    public void jumpBeautifyPage() {
        Intent intent = new Intent(this, BeautifyActivity.class);
        startActivity(intent);
    }


    @Override
    public void setPresenter(ProcessingContract.Presenter presenter) {
        mPresenter = (ProcessingPresenter)checkNotNull(presenter);
    }
}
