package com.swift.camera.beautify;

import android.graphics.Bitmap;

import com.swift.camera.BaseView;
import com.swift.camera.processing.ProcessingContract;
import com.swift.camera.ui.BasePresenter;

/**
 * Created by bool on 17-4-19.
 */

public interface BeautifyContract {
    interface View extends BaseView<Presenter>{

    }

    interface Presenter extends BasePresenter{

    }
}
