package com.swift.camera;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by skye on 4/11/17.
 * 该接口作为不同层级间通信的标准，外层接口名只是作为一个定义空间而已。
 * 内层借口才是定义不同层间通信的标准。
 */

public interface MainContract {

    // UI展示
    // 提供了供Presenter层用来更新界面的接口，View层必须实现。
    interface View {

        android.view.View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstacesState);
    }

    // 逻辑处理
    interface Presenter {

    }

    // 数据存储
    // 提供了Presenter层对数据进行更新或获取的方法，Model层必须实现。
    interface Support {

    }
}
