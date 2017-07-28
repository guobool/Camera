package com.swift.camera.data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by bool on 17-4-11.
 */

public final class PictureInfo implements Serializable {
    private String mPath;
    private String mName;
    private Date mCreateDate;
    public boolean isSelected;

    public PictureInfo(String path){
        mPath = path;
        isSelected = false;
    }
    public PictureInfo(String path, String name, int date) {
        mPath = path;
        mName = name;
        mCreateDate = new Date(date);
        isSelected = false;
    }
    public String getImagePath(){
        return mPath;
    }
}
