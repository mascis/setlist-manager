package setlistmanager.song;

import setlistmanager.main.MainActivity;
import setlistmanager.setlist.SetlistsActivity;
import setlistmanager.util.BaseNavigator;

/**
 * Created by User on 15.12.2017.
 */

public class AddEditSongNavigator {

    private final BaseNavigator navigationProvider;

    public AddEditSongNavigator(BaseNavigator navigationProvider ) {
        this.navigationProvider = navigationProvider;
    }

    void onSongSaved() {

        navigationProvider.finishActivity();

    }

    void onCancel() {

        navigationProvider.finishActivity();

    }

}
