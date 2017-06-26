package com.swift.camera.utils.ImageLoad;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.concurrent.ExecutionException;


/**
 * Created by bool on 17-5-31.
 */
public class GlideImageLoader implements ImageLoader {
    private static Context mContext;
    private static GlideImageLoader singleInstance = null;
    private GlideImageLoader(Context context){
        mContext = context;
    }
    public static ImageLoader getInstance(Context context){
        if(singleInstance == null){
            synchronized (GlideImageLoader.class){
                if(singleInstance == null){
                    singleInstance = new GlideImageLoader(context);
                }
            }
        }
        return singleInstance;
    }
    @Override
    public  void getAdapteImage(String fullName, ImageView imageView){
        Glide.with(mContext)
                .load(fullName)
                .centerCrop()
                .into(imageView);
    }

    @Override
    public  void getOriginalImage(String fullName, ImageView imageView) {
        Glide.with(mContext)
                .load(fullName)
                .into(imageView);
    }

    @Override
    public Bitmap getSpecifiedSizeImage(String fullPath, int width, int height){
        Bitmap bitmap = null;
        try {
            bitmap = Glide.with(mContext)
                    .load(fullPath)
                    .asBitmap() //必须
                    .centerCrop()
                    .into(width, height)
                    .get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    public void getSpecifiedSizeImage(String fullName, int width, int height,
                                        ImageView imageView) {
        Glide.with(mContext)
                .load(fullName)
                .override(width, height)
                .into(imageView);
    }
}
