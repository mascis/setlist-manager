package setlistmanager.util;

import java.util.Map;

/**
 * Created by User on 15.12.2017.
 */

public interface BaseNavigator {

    void finishActivity();

    void finishActivityWithResult( int resultcode );

    void startActivityForResult(Class clazz, int requestcode);

    void startActivityForResultWithExtra(Class clazz, int requestcode, String extraKey, String extraValue);

    void startActivityForResultWithExtrasBundle(Class clazz, int requestcode, Map<String, String> extras);

}
