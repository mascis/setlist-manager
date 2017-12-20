package setlistmanager.setlist;

import android.app.Activity;

import setlistmanager.song.SongsActivity;
import setlistmanager.util.BaseNavigator;

/**
 * Created by User on 15.12.2017.
 */

public class SetlistSongsNavigator {

    private final BaseNavigator navigationProvider;

    public SetlistSongsNavigator(BaseNavigator navigationProvider ) {
        this.navigationProvider = navigationProvider;
    }

    void editSong(String id) {}

    void toSetlists() {}

    void toSongs() {}

    void addSongsToSetlist(String id) {
        navigationProvider.startActivityForResultWithExtra(AddSongsToSetlistActivity.class, Activity.RESULT_OK, AddSongsToSetlistActivity.SETLIST_ID, id);
    }
}
