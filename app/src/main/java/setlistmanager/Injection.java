package setlistmanager;

import android.app.Activity;
import android.content.Context;

import setlistmanager.data.source.local.LocalDataSource;
import setlistmanager.data.source.local.SetlistManagerDatabase;
import setlistmanager.util.BaseNavigator;
import setlistmanager.util.Navigator;

/**
 * Created by User on 15.12.2017.
 */

public class Injection {

    public static LocalDataSource provideLocalDataSource(Context context ) {
        SetlistManagerDatabase setlistManagerDatabase = SetlistManagerDatabase.getInstance(context);
        return LocalDataSource.getInstance(setlistManagerDatabase.setlistDao());
    }

    public static BaseNavigator provideNavigator(Activity activity) {
        BaseNavigator baseNavigator = new Navigator(activity);
        return baseNavigator;
    }

    public static ViewModelFactory provideViewModelFactory( Context context, Activity activity ) {
        LocalDataSource localDataSource = provideLocalDataSource(context);
        BaseNavigator baseNavigator = provideNavigator(activity);
        return new ViewModelFactory(localDataSource, baseNavigator);
    }

}
