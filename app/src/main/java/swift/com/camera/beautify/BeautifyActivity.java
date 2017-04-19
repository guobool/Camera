package swift.com.camera.beautify;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import javax.inject.Inject;

import swift.com.camera.Album.AlbumPresenterModule;
import swift.com.camera.Album.DaggerAlbumComponent;
import swift.com.camera.R;
import swift.com.camera.TheApplication;
import swift.com.camera.data.PictureBean;

import static dagger.internal.Preconditions.checkNotNull;

/**
 * Created by bool on 17-4-12.
 */

public class BeautifyActivity extends AppCompatActivity implements BeautifyContract.View{
    @Inject BeautifyPresenter mPresenter;
    private ImageView mIvBeautifyImage;
    @Override
    protected void onCreate(Bundle savedInstancesState){
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_beautify);
        mIvBeautifyImage = (ImageView)findViewById(R.id.ivBeautifyImage);
        PictureBean mPictureBean = (PictureBean) getIntent().getSerializableExtra("PictureBean");

        // 注入
        DaggerBeautifyComponent.builder()
                .pictureRepositoryComponent(((TheApplication)getApplication()).getTasksRepositoryComponent())
                .beautifyPresenterModule(new BeautifyPresenterModule(this))
                .build()
                .inject(this);
        if(mPictureBean != null){
            //获取原图
            Bitmap bitmap = mPresenter.getImage(mPictureBean.getmImagePath(), 0, 0);
            if(bitmap != null) {
                mIvBeautifyImage.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    public void setPresenter(@NonNull BeautifyContract.Presenter presenter) {
        mPresenter = (BeautifyPresenter)checkNotNull(presenter);
    }
}
