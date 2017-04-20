package swift.com.camera.Album;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import swift.com.camera.R;

/**
 * Created by bool on 17-4-20.
 */

public class MyRecyclerAdapter extends RecyclerView.Adapter {
    private static final String TAG = "MyRecyclerAdapter";

    private List mData;
    private Context mContext;
    private LayoutInflater inflater;

    public MyRecyclerAdapter(Context context, List data) {
        this.mContext = context;
        this.mData = data;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onViewRecycled(MyViewHolder holder) {
        super.onViewRecycled(holder);
        AsyncTask asyncTask = (AsyncTask) holder.imageView.getTag(1);
        asyncTask.cancel(true);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        //先设置图片占位符
        holder.imageView.setImageDrawable(mContext.getDrawable(R.mipmap.ic_launcher));

        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                try {
                    URL url = new URL((String) mData.get(position));
                    Bitmap bitmap = BitmapFactory.decodeStream(url.openStream());
                    return bitmap;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                holder.imageView.setImageBitmap(bitmap);
            }
        };
        holder.imageView.setTag(1,asyncTask);
        asyncTask.execute();
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        //先设置图片占位符
        holder.imageView.setImageDrawable(mContext.getDrawable(R.mipmap.ic_launcher));
        final String url = mData.get(position);
        //为imageView设置Tag,内容是该imageView等待加载的图片url
        holder.imageView.setTag(url);
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    URL url = new URL(mData.get(position));
                    Bitmap bitmap = BitmapFactory.decodeStream(url.openStream());
                    return bitmap;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
            @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_layout, parent, false);
        return new MyViewHolder(view);
    }



    static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.id_img);
        }

    }
}