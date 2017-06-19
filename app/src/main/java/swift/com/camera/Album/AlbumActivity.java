package swift.com.camera.Album;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import swift.com.camera.R;
import swift.com.camera.TheApplication;
import swift.com.camera.beautify.BeautifyActivity;
import swift.com.camera.data.PictureInfo;
import swift.com.camera.data.PicturesFolder;
import swift.com.camera.processing.ProcessingActivity;
import static dagger.internal.Preconditions.checkNotNull;


/**
 * Created by bool on 17-4-10.
 */

public class AlbumActivity extends AppCompatActivity implements AlbumContract.View {
    /**
     * Android M 的Runtime Permission特性申请权限用的
     */
    private final static int REQUEST_READ_EXTERNAL_STORAGE_CODE = 1;
    @Inject AlbumPresenter mPresenter;
    private PicturesFolder mPictureFolder;
    private RecyclerView mRvPhotoList;
    private RecyclerView mRvSelectedPhotosView;
    private List mOnShowPhotosList;
    private SelectedPictureGridAdapter mSelectedPictureGridAdapter;
    private ViewPager mVpPageContainer;
    private FrameLayout mFlViewContainer;
    private TextView mTvNoImages;
    private TextView mTvSelectedCount;
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
        mFlViewContainer = (FrameLayout)findViewById(R.id.fl_view_container);
        mVpPageContainer = (ViewPager)findViewById(R.id.vp_pager_container);
        mTvNoImages = (TextView)findViewById(R.id.tv_no_image);
        // 注入AlbumPresenter对象
        DaggerAlbumComponent.builder()
                .pictureRepositoryComponent(((TheApplication)getApplication()).getTasksRepositoryComponent())
                .albumPresenterModule(new AlbumPresenterModule(this))
                .build()
                .inject(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, R.string.grant_advice_read_album, Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                    .READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_CODE);
        } else {
            mPresenter.loadImages();
        }
        mRvSelectedPhotosView = (RecyclerView)findViewById(R.id.rv_selected_photos);
        mRvSelectedPhotosView.setLayoutManager(new GridLayoutManager(this, 4)); //每行4列
        mSelectedPictureGridAdapter = new SelectedPictureGridAdapter(new ArrayMap<Integer, PictureInfo>(5),
                AlbumActivity.this);
        mRvSelectedPhotosView.setAdapter(mSelectedPictureGridAdapter);
        mTvSelectedCount = (TextView)findViewById(R.id.tv_selected_count);
        String selectedStringFormat = getString(R.string.select_count);
        String selectedString = String.format(selectedStringFormat, Integer.toString(0));
        mTvSelectedCount.setText(selectedString);

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.album_select_view, null);
        mRvPhotoList = (RecyclerView)view.findViewById(R.id.rv_photos_list);
        final List<View> viewList = new ArrayList<>();
        viewList.add(view);

        view = inflater.inflate(R.layout.album_folder_list, null);
        viewList.add(view);
        PagerAdapter pagerAdapter = new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return viewList.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                // TODO Auto-generated method stub
                container.removeView(viewList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                // TODO Auto-generated method stub
                container.addView(viewList.get(position));


                return viewList.get(position);
            }
        };
        mVpPageContainer.setAdapter(pagerAdapter);
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
    public void toBeautifyActivity(PictureInfo PictureInfo) {
        Intent intent = new Intent(this, BeautifyActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("PictureInfo",PictureInfo);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    @Override
    public void showFolderList(PicturesFolder picturesFolder) {
        mPictureFolder = picturesFolder;
        mFlViewContainer.setVisibility(View.VISIBLE);
        mTvNoImages.setVisibility(View.GONE);

        //----------------------------RecycleView------------------------
        mRvPhotoList.setLayoutManager(new GridLayoutManager(this, 4)); //每行4列
        AlbumRecycleViewAdapter gridAdapter = new AlbumRecycleViewAdapter(new ArrayList<PictureInfo>(0),
                AlbumActivity.this);
        mRvPhotoList.setAdapter(gridAdapter);
        mRvPhotoList.addItemDecoration(new DivideItemDecoration(this));
        //mPresenter.getImagesList();
        mOnShowPhotosList = picturesFolder.get("/storage/emulated/0/DCIM/Camera");
        gridAdapter.onDataChaged(mOnShowPhotosList);
    }

    @Override
    public void showNoPictures() {
        mFlViewContainer.setVisibility(View.GONE);
        mTvNoImages.setVisibility(View.VISIBLE);
    }

    public void onItemSelected(int index) {
        mPresenter.toBeaytifyActivity((PictureInfo)mOnShowPhotosList.get(index));
    }

    public void onSelectChanged(ArrayMap<Integer, PictureInfo> pictureMap) {
        int selectedNum = pictureMap.size();
        String selectedStringFormat = getString(R.string.select_count);
        String selectedString = String.format(selectedStringFormat, Integer.toString(selectedNum));
        mTvSelectedCount.setText(selectedString);
        mSelectedPictureGridAdapter.onDataChaged(pictureMap);
    }
}
