package setlistmanager.util;

import android.app.Activity;
import android.content.Intent;

import java.lang.ref.WeakReference;

/**
 * Created by User on 15.12.2017.
 */

public class Navigator implements BaseNavigator {

    private final WeakReference<Activity> activity;

    public Navigator( Activity activity ) {

        this.activity = new WeakReference<Activity>(activity);
    }

    @Override
    public void finishActivity() {

        if ( activity.get() != null ) {

            activity.get().finish();

        }

    }

    @Override
    public void finishActivityWithResult(int resultcode) {

        if ( activity.get() != null ) {

            activity.get().setResult(resultcode);
            activity.get().finish();

        }

    }

    @Override
    public void startActivityForResult(Class clazz, int requestcode) {

        if ( activity.get() != null ) {

            Intent intent = new Intent(activity.get(), clazz);
            activity.get().startActivityForResult(intent, requestcode);

        }

    }

    @Override
    public void startActivityForResultWithExtra(Class clazz, int requestcode, String extraKey, String extraValue) {

        if ( activity.get() != null ) {

            Intent intent = new Intent(activity.get(), clazz);
            intent.putExtra(extraKey, extraValue);
            activity.get().startActivityForResult(intent, requestcode);

        }

    }
}
