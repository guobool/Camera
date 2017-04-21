package swift.com.camera;

/**
 * Created by bool on 17-4-12.
 */

public interface BaseView<T> {
    String TAG = null;
    void setPresenter(T presenter);

}
