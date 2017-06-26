package com.swift.camera.beautify;

import android.graphics.Bitmap;

import java.util.List;

import javax.inject.Inject;

import com.swift.camera.Album.AlbumContract;
import com.swift.camera.data.PictureBean;
import com.swift.camera.data.PictureDataSource;
import com.swift.camera.data.PictureRepository;

/**
 * Created by bool on 17-4-19.
 */

public class BeautifyPresenter implements BeautifyContract.Presenter{
    private PictureRepository mPictureReposotory;
    private BeautifyContract.View mBeautifyView;

    @Inject
    public BeautifyPresenter(PictureRepository repository, BeautifyContract.View beautifyView){
        mPictureReposotory = repository;
        mBeautifyView = beautifyView;
    }
}
