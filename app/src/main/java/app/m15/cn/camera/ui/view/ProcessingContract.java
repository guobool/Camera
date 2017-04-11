/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.m15.cn.camera.ui.view;

import android.support.annotation.NonNull;
import java.util.List;
import app.m15.cn.camera.BasePresenter;
import app.m15.cn.camera.BaseView;
import app.m15.cn.camera.data.PictureData;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface ProcessingContract {

    interface View extends BaseView<Presenter> {

    }

    interface Presenter extends BasePresenter {

    }
}
