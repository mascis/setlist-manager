package setlistmanager.util;

/**
 * Created by User on 15.12.2017.
 */

public interface BaseNavigator {

    void finishActivity();

    void finishActivityWithResult( int resultcode );

    void startActivityForResult(Class clazz, int requestcode);

    void startActivityForResultWithExtra(Class clazz, int requestcode, String extraKey, String extraValue);

}
