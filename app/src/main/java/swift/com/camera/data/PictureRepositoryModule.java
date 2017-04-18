package swift.com.camera.data;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import swift.com.camera.data.local.LocalPictureSource;

/**
 * Created by bool on 17-4-13.
 */
@Module
abstract class PictureRepositoryModule {
    @Singleton
    @Binds
    @Local
    abstract PictureDataSource provideLocalPictureSpurce(LocalPictureSource dateSource);
//   如果需要在远程获取数据，可以在此用
//    @Singleton
//    @Binds
//    @Remote
}
