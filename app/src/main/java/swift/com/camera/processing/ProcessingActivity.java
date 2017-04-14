package swift.com.camera.processing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import javax.inject.Inject;

import swift.com.camera.R;
import swift.com.camera.Album.AlbumActivity;
import swift.com.camera.beautify.BeautifyActivity;
import swift.com.camera.data.DaggerPictureRepositoryComponent;
import swift.com.camera.data.PictureRepository;
import swift.com.camera.data.PictureRepositoryComponent;
import swift.com.camera.data.PictureRepositoryModule;
import swift.com.camera.ui.view.FunctionLayout;

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
        // 添加Presenter
        // 依赖对象　Component
        PictureRepositoryComponent appCom = DaggerPictureRepositoryComponent.builder()
                .pictureRepositoryModule(new PictureRepositoryModule()).build();

        // 子类依赖对象 ，并注入
        DaggerProcessingComponent.builder()
                .pictureRepositoryComponent(appCom)
                .processingPresenterModule(new ProcessingPresenterModule(this))
                .build()
                .inject(this);

        // Create the presenter
//        DaggerTasksComponent.builder()
//                .tasksRepositoryComponent(((ToDoApplication) getApplication()).getTasksRepositoryComponent())
//                .tasksPresenterModule(new TasksPresenterModule(tasksFragment)).build()
//                .inject(this);


        mRlOneLeyBeautify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.jumpAlbumPage();
                Log.w(this.getClass().getName(), "绑定监听器");
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
                mPresenter.jumpAlbumPage();
                Log.w(this.getClass().getName(), "绑定监听器");
            }
        });
        mRlPictureInPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.jumpAlbumPage();
                Log.w(this.getClass().getName(), "绑定监听器");
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
