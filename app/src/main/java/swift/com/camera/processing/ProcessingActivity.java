package swift.com.camera.processing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import swift.com.camera.Injection;
import swift.com.camera.R;
import swift.com.camera.Album.AlbumActivity;
import swift.com.camera.beautify.BeautifyActivity;
import swift.com.camera.ui.view.FunctionLayout;

import static com.squareup.haha.guava.base.Joiner.checkNotNull;

/**
 * Created by bool on 17-4-11.
 */

public class ProcessingActivity extends AppCompatActivity implements ProcessingContract.View{
    private ProcessingContract.Presenter mPresenter;
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
        mPresenter = new ProcessingPresenter(Injection.providePictureRepository(this), this);

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
        mPresenter = checkNotNull(presenter);
    }
}
