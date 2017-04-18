package swift.com.camera.data;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * Created by bool on 17-4-18.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface Local {
}
