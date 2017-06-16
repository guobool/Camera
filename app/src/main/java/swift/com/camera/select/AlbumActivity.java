package swift.com.camera.select;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import swift.com.camera.R;
import swift.com.camera.TheApplication;
import swift.com.camera.data.PicturesFolder;

import static dagger.internal.Preconditions.checkNotNull;

/**
 * Created by bool on 17-6-12.
 */

public class AlbumActivity extends AppCompatActivity implements AlbumContract.View{
    /**
     * Android M 的Runtime Permission特性申请权限用的
     */
    private final static int REQUEST_READ_EXTERNAL_STORAGE_CODE = 1;
    @Inject
    protected AlbumPresenter mAlbumPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photo);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, R.string.grant_advice_read_album, Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                    .READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_CODE);
        } else {
            mAlbumPresenter.scanImage();
        }
        DaggerAlbumComponent
                .builder()
                .albumPresenterModule(new AlbumPresenterModule(this))
                .pictureRepositoryComponent(((TheApplication)getApplication()).getTasksRepositoryComponent())
                .build()
                .inject(this);

    }

    @Override
    public void setPresenter(@NonNull AlbumContract.Presenter presenter) {
        mAlbumPresenter = (AlbumPresenter) checkNotNull(presenter);
    }

    public void refreshAlbumList(PicturesFolder picturesFolder){
        if (picturesFolder != null){


        }
    }
}
